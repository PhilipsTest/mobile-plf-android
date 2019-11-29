/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.screens;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.activity.IAPActivity;
import com.philips.cdp.di.iap.adapters.ShoppingCartAdapter;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.cart.IAPCartListener;
import com.philips.cdp.di.iap.cart.ShoppingCartAPI;
import com.philips.cdp.di.iap.cart.ShoppingCartData;
import com.philips.cdp.di.iap.cart.ShoppingCartPresenter;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.controller.AddressController;
import com.philips.cdp.di.iap.controller.ControllerFactory;
import com.philips.cdp.di.iap.controller.VoucherController;
import com.philips.cdp.di.iap.eventhelper.EventHelper;
import com.philips.cdp.di.iap.eventhelper.EventListener;
import com.philips.cdp.di.iap.response.State.RegionsList;
import com.philips.cdp.di.iap.response.addresses.Addresses;
import com.philips.cdp.di.iap.response.addresses.DeliveryCost;
import com.philips.cdp.di.iap.response.addresses.DeliveryModes;
import com.philips.cdp.di.iap.response.addresses.GetDeliveryModes;
import com.philips.cdp.di.iap.response.addresses.GetShippingAddressData;
import com.philips.cdp.di.iap.response.carts.DeliveryCostEntity;
import com.philips.cdp.di.iap.response.carts.DeliveryModeEntity;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.AlertListener;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPUtility;
import com.philips.cdp.di.iap.utils.NetworkUtility;
import com.philips.cdp.di.iap.utils.Utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.philips.cdp.di.iap.utils.IAPConstant.IAP_VOUCHER_CODE;

public class ShoppingCartFragment extends InAppBaseFragment
        implements View.OnClickListener, EventListener, AddressController.AddressListener,
        ShoppingCartAdapter.OutOfStockListener, ShoppingCartPresenter.ShoppingCartListener<ShoppingCartData>,
        OnSetDeliveryModeListener, AlertListener, VoucherController.VoucherListener, IAPCartListener {

    public static final String TAG = ShoppingCartFragment.class.getName();
    private Button mCheckoutBtn;
    private Button mContinuesBtn;
    private RelativeLayout mParentLayout;
    public ShoppingCartAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private AddressController mAddressController;
    private ShoppingCartAPI mShoppingCartAPI;
    private ArrayList<ShoppingCartData> mData = new ArrayList<>();
    private List<Addresses> mAddresses = new ArrayList<>();
    private DeliveryModes mSelectedDeliveryMode;
    private TextView mNumberOfProducts;
    private String voucherCode;
    VoucherController mVoucherController;

    public static ShoppingCartFragment createInstance(Bundle args, AnimationType animType) {
        ShoppingCartFragment fragment = new ShoppingCartFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.BUTTON_STATE_CHANGED), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.EMPTY_CART_FRAGMENT_REPLACED), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.PRODUCT_DETAIL_FRAGMENT), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.IAP_LAUNCH_PRODUCT_CATALOG), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.IAP_DELETE_PRODUCT), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.IAP_UPDATE_PRODUCT_COUNT), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.IAP_EDIT_DELIVERY_MODE), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.IAP_APPLY_VOUCHER), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.IAP_DELETE_VOUCHER), this);

        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.IAP_DELETE_PRODUCT_CONFIRM), this);

        mVoucherController = new VoucherController(getActivity(), this);

        if(Utility.getVoucherCode()!=null){
           voucherCode=Utility.getVoucherCode();
        }


        View rootView = inflater.inflate(R.layout.iap_shopping_cart_view, container, false);

        mRecyclerView = rootView.findViewById(R.id.shopping_cart_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mCheckoutBtn = rootView.findViewById(R.id.checkout_btn);
        mCheckoutBtn.setOnClickListener(this);
        mContinuesBtn = rootView.findViewById(R.id.continues_btn);
        mContinuesBtn.setOnClickListener(this);
        mParentLayout = rootView.findViewById(R.id.parent_layout);
        mShoppingCartAPI = ControllerFactory.getInstance()
                .getShoppingCartPresenter(getActivity(), this);
        mAddressController = new AddressController(getActivity(), this);
        mNumberOfProducts = rootView.findViewById(R.id.number_of_products);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        IAPAnalytics.trackPage(IAPAnalyticsConstant.SHOPPING_CART_PAGE_NAME);
        IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                IAPAnalyticsConstant.SPECIAL_EVENTS, IAPAnalyticsConstant.SHOPPING_CART_VIEW);
        setTitleAndBackButtonVisibility(R.string.iap_shopping_cart_dls, true);
        setCartIconVisibility(false);
        if (isNetworkConnected()) {
            updateCartDetails(mShoppingCartAPI);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null)
            mAdapter.onStop();
        hideProgressBar();
        NetworkUtility.getInstance().dismissErrorDialog();
    }

    private void updateCartDetails(ShoppingCartAPI presenter) {
        createCustomProgressBar(mParentLayout,BIG);
        presenter.getCurrentCartDetails();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(IAPConstant.BUTTON_STATE_CHANGED), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(IAPConstant.EMPTY_CART_FRAGMENT_REPLACED), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(IAPConstant.PRODUCT_DETAIL_FRAGMENT), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(IAPConstant.IAP_LAUNCH_PRODUCT_CATALOG), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(IAPConstant.IAP_EDIT_DELIVERY_MODE), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(IAPConstant.IAP_DELETE_PRODUCT), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(IAPConstant.IAP_UPDATE_PRODUCT_COUNT), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(IAPConstant.IAP_DELETE_PRODUCT_CONFIRM), this);
    }

    @Override
    public void onClick(final View v) {
        if (v == mCheckoutBtn) {

            if (IAPUtility.getInstance().getMaxCartCount() == IAPConstant.UN_LIMIT_CART_COUNT) {
                getRegionAndTag();
            } else {
                createCustomProgressBar(mParentLayout, BIG);
                mShoppingCartAPI.getProductCartCount(getActivity(), this);
            }

        }
        if (v == mContinuesBtn) {
            if (!isNetworkConnected()) return;

            //Track continue shopping action
            IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA, IAPAnalyticsConstant.SPECIAL_EVENTS,
                    IAPAnalyticsConstant.CONTINUE_SHOPPING_SELECTED);
            EventHelper.getInstance().notifyEventOccurred(IAPConstant.IAP_LAUNCH_PRODUCT_CATALOG);
        }
    }

    private void getRegionAndTag() {
        createCustomProgressBar(mParentLayout, BIG);
        mAddressController.getRegions();

        IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                IAPAnalyticsConstant.SPECIAL_EVENTS, IAPAnalyticsConstant.CHECKOUT_BUTTON_SELECTED);

        if (mAdapter != null && mAdapter.isFreeDelivery()) {
            //Action to track free delivery
            IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                    IAPAnalyticsConstant.SPECIAL_EVENTS, IAPAnalyticsConstant.FREE_DELIVERY);
        }
    }

    @Override
    public boolean handleBackEvent() {
        Fragment fragment = getFragmentManager().findFragmentByTag(ProductCatalogFragment.TAG);
        if (fragment == null && getActivity() != null && getActivity() instanceof IAPActivity) {
            finishActivity();
        }
        return false;
    }

    @Override
    public void onEventReceived(final String event) {
        hideProgressBar();
        if (event.equalsIgnoreCase(IAPConstant.EMPTY_CART_FRAGMENT_REPLACED)) {
            getFragmentManager().popBackStack();
            addFragment(EmptyCartFragment.createInstance(new Bundle(), AnimationType.NONE), EmptyCartFragment.TAG, true);
        } else if (event.equalsIgnoreCase(String.valueOf(IAPConstant.BUTTON_STATE_CHANGED))) {
            mCheckoutBtn.setEnabled(!Boolean.valueOf(event));
        } else if (event.equalsIgnoreCase(String.valueOf(IAPConstant.PRODUCT_DETAIL_FRAGMENT))) {
            startProductDetailFragment(mAdapter);
        } else if (event.equalsIgnoreCase(String.valueOf(IAPConstant.IAP_LAUNCH_PRODUCT_CATALOG))) {
            showProductCatalogFragment(ShoppingCartFragment.TAG);
        } else if (event.equalsIgnoreCase(IAPConstant.IAP_DELETE_PRODUCT)) {
            createCustomProgressBar(mParentLayout, BIG);
            if(mAdapter.getSelectedItemPosition()>0) {
                mShoppingCartAPI.deleteProduct(mData.get(mAdapter.getSelectedItemPosition()));
            }

        } else if (event.equalsIgnoreCase(IAPConstant.IAP_UPDATE_PRODUCT_COUNT)) {
            createCustomProgressBar(mParentLayout, BIG);
            mShoppingCartAPI.updateProductQuantity(mData.get(mAdapter.getSelectedItemPosition()),
                    mAdapter.getNewCount(), mAdapter.getQuantityStatusInfo());

        } else if (event.equalsIgnoreCase(IAPConstant.IAP_EDIT_DELIVERY_MODE)) {

            addFragment(DeliveryMethodFragment.createInstance(new Bundle(), AnimationType.NONE),
                    AddressSelectionFragment.TAG, true);
        } else if (event.equalsIgnoreCase(IAPConstant.IAP_DELETE_PRODUCT_CONFIRM)) {
            Utility.showActionDialog(getActivity(), getString(R.string.iap_remove_product), getString(R.string.iap_cancel)
                    , getString(R.string.iap_delete_item_alert_title), getString(R.string.iap_product_remove_description), getFragmentManager(), this);
        } else if (event.equalsIgnoreCase(IAPConstant.IAP_APPLY_VOUCHER)) {
            VoucherFragment voucherFragment = new VoucherFragment();
            Bundle bundle = new Bundle();
            bundle.putString(IAP_VOUCHER_CODE, mData.get(0).getAppliedVoucherCode());
            voucherFragment.setArguments(bundle);
            addFragment(voucherFragment, VoucherFragment.TAG, true);
        }
    }

    void startProductDetailFragment(ShoppingCartAdapter mAdapter) {
        ShoppingCartData shoppingCartData = mAdapter.getTheProductDataForDisplayingInProductDetailPage();
        Bundle bundle = new Bundle();
        bundle.putString(IAPConstant.PRODUCT_TITLE, shoppingCartData.getProductTitle());
        bundle.putString(IAPConstant.PRODUCT_CTN, shoppingCartData.getCtnNumber());
        bundle.putString(IAPConstant.PRODUCT_PRICE, shoppingCartData.getFormattedPrice());
        bundle.putString(IAPConstant.PRODUCT_VALUE_PRICE, shoppingCartData.getValuePrice());
        bundle.putString(IAPConstant.PRODUCT_OVERVIEW, shoppingCartData.getMarketingTextHeader());
        if (bundle.getStringArrayList(IAPConstant.IAP_IGNORE_RETAILER_LIST) != null) {
            final ArrayList<String> list = bundle.getStringArrayList(IAPConstant.IAP_IGNORE_RETAILER_LIST);
            bundle.putStringArrayList(IAPConstant.IAP_IGNORE_RETAILER_LIST, list);
        }
        bundle.putInt(IAPConstant.PRODUCT_QUANTITY, shoppingCartData.getQuantity());
        bundle.putInt(IAPConstant.PRODUCT_STOCK, shoppingCartData.getStockLevel());
        bundle.putSerializable(IAPConstant.SHOPPING_CART_CODE, shoppingCartData);
        addFragment(ProductDetailFragment.createInstance(bundle, AnimationType.NONE), ProductDetailFragment.TAG,true);
    }

    @Override
    public void onGetAddress(Message msg) {
        hideProgressBar();
        if (msg.obj instanceof IAPNetworkError) {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), getActivity());
        } else {
            Bundle bundle = new Bundle();
            if (mSelectedDeliveryMode != null)
                bundle.putParcelable(IAPConstant.SET_DELIVERY_MODE, mSelectedDeliveryMode);

            if ((msg.obj).equals(NetworkConstants.EMPTY_RESPONSE)) {
                addFragment(AddressFragment.createInstance(bundle, AnimationType.NONE),
                        AddressSelectionFragment.TAG,true);
            } else if (msg.obj instanceof GetShippingAddressData) {
                GetShippingAddressData shippingAddresses = (GetShippingAddressData) msg.obj;
                mAddresses = shippingAddresses.getAddresses();
                bundle.putSerializable(IAPConstant.ADDRESS_LIST, (Serializable) mAddresses);
                addFragment(AddressSelectionFragment.createInstance(bundle, AnimationType.NONE),
                        AddressSelectionFragment.TAG,true);
            }
        }
    }

    @Override
    public void onGetRegions(Message msg) {
        if (msg.obj instanceof IAPNetworkError) {
            CartModelContainer.getInstance().setRegionList(null);
        } else if (msg.obj instanceof RegionsList) {
            CartModelContainer.getInstance().setRegionList((RegionsList) msg.obj);
        } else {
            CartModelContainer.getInstance().setRegionList(null);
        }
        mAddressController.getAddresses();
    }

    @Override
    public void onOutOfStock(boolean isOutOfStockReached) {
        if (mCheckoutBtn == null) return;
        mCheckoutBtn.setEnabled(isOutOfStockReached);
    }

    @Override
    public void onLoadFinished(ArrayList<?> data) {

        if (data!=null && data instanceof ArrayList) {
            hideProgressBar();
            mData = (ArrayList<ShoppingCartData>) data;
        }
        if (getActivity() == null) return;
        
        if(mData!=null && mData.get(0)!=null) {

            if(mData.get(0).getDeliveryMode()!=null){
                mSelectedDeliveryMode =  getDeliveryMethodFromDeliveryModeEntity(mData.get(0).getDeliveryMode());
            }

            onOutOfStock(false);
            mAdapter = new ShoppingCartAdapter(getActivity(), mData, this);
            mAdapter.setCountArrow(getActivity(), true);
            if (mData.get(0) != null && mData.get(0).getDeliveryItemsQuantity() > 0) {
                updateCount(mData.get(0).getDeliveryItemsQuantity());
            }
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.tagProducts();

            String numberOfProducts = getActivity().getResources().getString(R.string.iap_number_of_products);
            if (mData.size() == 1) {
                numberOfProducts = getActivity().getResources().getString(R.string.iap_number_of_product);
            }
            numberOfProducts = String.format(numberOfProducts, mData.size());
            mNumberOfProducts.setText(numberOfProducts);
            mNumberOfProducts.setVisibility(View.VISIBLE);
        }else {
            mAddressController.getDeliveryModes();
        }
        if(voucherCode!=null) {
            mVoucherController.applyCoupon(voucherCode);
            Utility.setVoucherCode(null);
            voucherCode=null;
        }
        hideProgressBar();
    }

    @Override
    public void onLoadError(Message msg) {
        hideProgressBar();
        if (!isNetworkConnected()) return;

        if (msg.obj instanceof IAPNetworkError) {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), getActivity());
        } else {
            NetworkUtility.getInstance().showErrorDialog(getActivity(), getFragmentManager(), getActivity().getString(R.string.iap_ok),
                    getActivity().getString(R.string.iap_server_error), getActivity().getString(R.string.iap_something_went_wrong));
        }
    }

    @Override
    public void onGetUser(Message msg) {
        //NOP
    }

    @Override
    public void onSetDeliveryAddress(Message msg) {
        //NOP
    }

    @Override
    public void onGetDeliveryModes(Message msg) {
        hideProgressBar();
        if ((msg.obj instanceof IAPNetworkError)) {
            updateCartDetails(mShoppingCartAPI);
        } else if ((msg.obj instanceof GetDeliveryModes)) {
            GetDeliveryModes deliveryModes = (GetDeliveryModes) msg.obj;
            List<DeliveryModes> deliveryModeList = deliveryModes.getDeliveryModes();
            mAddressController.setDeliveryMode(deliveryModeList.get(0).getCode());
            CartModelContainer.getInstance().setDeliveryModes(deliveryModeList);
            handleDeliveryMode(msg,mAddressController);
        }
    }

    @Override
    public void onSetDeliveryMode(Message msg) {
        hideProgressBar();
        updateCartDetails(mShoppingCartAPI);
    }

    @Override
    public void onCreateAddress(Message msg) {
        //NOP
    }

    @Override
    public void onRetailerError(IAPNetworkError errorMsg) {
        hideProgressBar();
    }

    @Override
    public void onItemClick(int position) {
        final List<DeliveryModes> deliveryModes = CartModelContainer.getInstance().getDeliveryModes();
        mSelectedDeliveryMode = deliveryModes.get(position);

        createCustomProgressBar(mParentLayout,BIG);
        mAddressController.setDeliveryMode(deliveryModes.get(position).getCode());
    }

    @Override
    public void onPositiveBtnClick() {
        EventHelper.getInstance().notifyEventOccurred(IAPConstant.IAP_DELETE_PRODUCT);
    }

    @Override
    public void onNegativeBtnClick() {

    }

    @Override
    public void onApplyVoucherResponse(Message msg) {
        if (isNetworkConnected()) {
            updateCartDetails(mShoppingCartAPI);
        }
        hideProgressBar();
    }

    @Override
    public void onGetAppliedVoucherResponse(Message msg) {

    }

    @Override
    public void onDeleteAppliedVoucherResponse(Message msg) {

    }

    @Override
    public void onSuccess(int count) {
        hideProgressBar();
        if (count > IAPUtility.getInstance().getMaxCartCount()) {
            NetworkUtility.getInstance().showErrorDialog(getActivity(),getFragmentManager(),
                    getString(R.string.iap_ok),"Exceed Cart limit","You can not add more than "+IAPUtility.getInstance().getMaxCartCount()+ " product in your cart");
        } else {
            getRegionAndTag();
        }

    }

    @Override
    public void onFailure(Message msg) {
        hideProgressBar();
    }

    private DeliveryModes getDeliveryMethodFromDeliveryModeEntity(DeliveryModeEntity deliveryModeEntity){

        DeliveryModes deliveryMode = new DeliveryModes();
        DeliveryCost deliveryCost = new DeliveryCost();

        deliveryMode.setCode(deliveryModeEntity.getCode());
        deliveryMode.setDescription(deliveryModeEntity.getDescription());
        deliveryMode.setName(deliveryModeEntity.getName());

        DeliveryCostEntity deliveryCostEntity = deliveryModeEntity.getDeliveryCost();

        deliveryCost.setCurrencyIso(deliveryCostEntity.getCurrencyIso());
        deliveryCost.setFormattedValue(deliveryCostEntity.getFormattedValue());
        deliveryCost.setPriceType(deliveryCostEntity.getPriceType());
        deliveryCost.setValue(deliveryCostEntity.getValue());

        deliveryMode.setDeliveryCost(deliveryCost);

        return deliveryMode;
    }

}
