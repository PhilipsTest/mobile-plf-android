/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.screens;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.activity.ECSActivity;
import com.ecs.demouapp.ui.adapters.ShoppingCartAdapter;
import com.ecs.demouapp.ui.analytics.ECSAnalytics;
import com.ecs.demouapp.ui.analytics.ECSAnalyticsConstant;
import com.ecs.demouapp.ui.cart.ECSCartListener;
import com.ecs.demouapp.ui.cart.ShoppingCartAPI;
import com.ecs.demouapp.ui.cart.ShoppingCartData;
import com.ecs.demouapp.ui.cart.ShoppingCartPresenter;
import com.ecs.demouapp.ui.container.CartModelContainer;
import com.ecs.demouapp.ui.controller.AddressController;
import com.ecs.demouapp.ui.controller.ControllerFactory;
import com.ecs.demouapp.ui.controller.VoucherController;
import com.ecs.demouapp.ui.eventhelper.EventHelper;
import com.ecs.demouapp.ui.eventhelper.EventListener;
import com.ecs.demouapp.ui.session.IAPNetworkError;
import com.ecs.demouapp.ui.session.NetworkConstants;
import com.ecs.demouapp.ui.utils.AlertListener;
import com.ecs.demouapp.ui.utils.ECSConstant;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.ecs.demouapp.ui.utils.NetworkUtility;
import com.ecs.demouapp.ui.utils.Utility;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.model.address.ECSDeliveryMode;
import com.philips.platform.ecs.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.model.cart.ECSEntries;
import com.philips.platform.ecs.model.region.ECSRegion;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ShoppingCartFragment extends InAppBaseFragment
        implements View.OnClickListener, EventListener, AddressController.AddressListener,
        ShoppingCartAdapter.OutOfStockListener, ShoppingCartPresenter.ShoppingCartListener<ShoppingCartData>,
        OnSetDeliveryModeListener, AlertListener, VoucherController.VoucherListener, ECSCartListener {

    public static final String TAG = ShoppingCartFragment.class.getName();
    private Button mCheckoutBtn;
    private Button mContinuesBtn;
    private RelativeLayout mParentLayout;
    public ShoppingCartAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private AddressController mAddressController;
    private ShoppingCartAPI mShoppingCartAPI;
    private ECSShoppingCart ecsShoppingCart ;
    private List<ECSAddress> mAddresses = new ArrayList<>();
    private ECSDeliveryMode mSelectedDeliveryMode;
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
        EventHelper.getInstance().registerEventNotification(String.valueOf(ECSConstant.BUTTON_STATE_CHANGED), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(ECSConstant.EMPTY_CART_FRAGMENT_REPLACED), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(ECSConstant.PRODUCT_DETAIL_FRAGMENT), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(ECSConstant.IAP_LAUNCH_PRODUCT_CATALOG), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(ECSConstant.IAP_DELETE_PRODUCT), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(ECSConstant.IAP_UPDATE_PRODUCT_COUNT), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(ECSConstant.IAP_EDIT_DELIVERY_MODE), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(ECSConstant.IAP_APPLY_VOUCHER), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(ECSConstant.IAP_DELETE_VOUCHER), this);

        EventHelper.getInstance().registerEventNotification(String.valueOf(ECSConstant.IAP_DELETE_PRODUCT_CONFIRM), this);

        mVoucherController = new VoucherController(getActivity(), this);

        if(Utility.getVoucherCode()!=null){
           voucherCode=Utility.getVoucherCode();
        }


        View rootView = inflater.inflate(R.layout.ecs_shopping_cart_view, container, false);

        mRecyclerView = rootView.findViewById(R.id.shopping_cart_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mCheckoutBtn = rootView.findViewById(R.id.checkout_btn);
        mCheckoutBtn.setOnClickListener(this);
        mContinuesBtn = rootView.findViewById(R.id.continues_btn);
        mContinuesBtn.setOnClickListener(this);
        mParentLayout = rootView.findViewById(R.id.parent_layout);
        mAddressController = new AddressController(getActivity(), this);
        mNumberOfProducts = rootView.findViewById(R.id.number_of_products);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mShoppingCartAPI = ControllerFactory.
                getInstance().getShoppingCartPresenter(mContext, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ECSAnalytics.trackPage(ECSAnalyticsConstant.SHOPPING_CART_PAGE_NAME);
        ECSAnalytics.trackAction(ECSAnalyticsConstant.SEND_DATA,
                ECSAnalyticsConstant.SPECIAL_EVENTS, ECSAnalyticsConstant.SHOPPING_CART_VIEW);
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
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(ECSConstant.BUTTON_STATE_CHANGED), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(ECSConstant.EMPTY_CART_FRAGMENT_REPLACED), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(ECSConstant.PRODUCT_DETAIL_FRAGMENT), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(ECSConstant.IAP_LAUNCH_PRODUCT_CATALOG), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(ECSConstant.IAP_EDIT_DELIVERY_MODE), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(ECSConstant.IAP_DELETE_PRODUCT), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(ECSConstant.IAP_UPDATE_PRODUCT_COUNT), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(ECSConstant.IAP_DELETE_PRODUCT_CONFIRM), this);
    }

    @Override
    public void onClick(final View v) {
        if (v == mCheckoutBtn) {

            if (ECSUtility.getInstance().getMaxCartCount() == ECSConstant.UN_LIMIT_CART_COUNT) {
                getRegionAndTag();
            } else {
                createCustomProgressBar(mParentLayout, BIG);
                mShoppingCartAPI.getProductCartCount(getActivity(), this);
            }

        }
        if (v == mContinuesBtn) {
            if (!isNetworkConnected()) return;

            //Track continue shopping action
            ECSAnalytics.trackAction(ECSAnalyticsConstant.SEND_DATA, ECSAnalyticsConstant.SPECIAL_EVENTS,
                    ECSAnalyticsConstant.CONTINUE_SHOPPING_SELECTED);
            EventHelper.getInstance().notifyEventOccurred(ECSConstant.IAP_LAUNCH_PRODUCT_CATALOG);
        }
    }

    private void getRegionAndTag() {
        createCustomProgressBar(mParentLayout, BIG);
        mAddressController.getRegions();

        ECSAnalytics.trackAction(ECSAnalyticsConstant.SEND_DATA,
                ECSAnalyticsConstant.SPECIAL_EVENTS, ECSAnalyticsConstant.CHECKOUT_BUTTON_SELECTED);

        if (mAdapter != null && mAdapter.isFreeDelivery()) {
            //Action to track free delivery
            ECSAnalytics.trackAction(ECSAnalyticsConstant.SEND_DATA,
                    ECSAnalyticsConstant.SPECIAL_EVENTS, ECSAnalyticsConstant.FREE_DELIVERY);
        }
    }

    @Override
    public boolean handleBackEvent() {
        Fragment fragment = getFragmentManager().findFragmentByTag(ProductCatalogFragment.TAG);
        if (fragment == null && getActivity() != null && getActivity() instanceof ECSActivity) {
            finishActivity();
        }
        return false;
    }

    @Override
    public void onEventReceived(final String event) {
        hideProgressBar();
        if (event.equalsIgnoreCase(ECSConstant.EMPTY_CART_FRAGMENT_REPLACED)) {
            getFragmentManager().popBackStack();
            addFragment(EmptyCartFragment.createInstance(new Bundle(), AnimationType.NONE), EmptyCartFragment.TAG, true);
        } else if (event.equalsIgnoreCase(String.valueOf(ECSConstant.BUTTON_STATE_CHANGED))) {
            mCheckoutBtn.setEnabled(!Boolean.valueOf(event));
        } else if (event.equalsIgnoreCase(String.valueOf(ECSConstant.PRODUCT_DETAIL_FRAGMENT))) {
            startProductDetailFragment(mAdapter);
        } else if (event.equalsIgnoreCase(String.valueOf(ECSConstant.IAP_LAUNCH_PRODUCT_CATALOG))) {
            showProductCatalogFragment(ShoppingCartFragment.TAG);
        } else if (event.equalsIgnoreCase(ECSConstant.IAP_DELETE_PRODUCT)) {
            createCustomProgressBar(mParentLayout, BIG);
            mShoppingCartAPI.deleteProduct(ecsShoppingCart.getEntries().get(mAdapter.getSelectedItemPosition()));

        } else if (event.equalsIgnoreCase(ECSConstant.IAP_UPDATE_PRODUCT_COUNT)) {
            createCustomProgressBar(mParentLayout, BIG);

            mShoppingCartAPI.updateProductQuantity(ecsShoppingCart.getEntries().get(mAdapter.getSelectedItemPosition()),
                    mAdapter.getNewCount());

        } else if (event.equalsIgnoreCase(ECSConstant.IAP_EDIT_DELIVERY_MODE)) {

            addFragment(DeliveryMethodFragment.createInstance(new Bundle(), AnimationType.NONE),
                    AddressSelectionFragment.TAG, true);
        } else if (event.equalsIgnoreCase(ECSConstant.IAP_DELETE_PRODUCT_CONFIRM)) {
            Utility.showActionDialog(getActivity(), getString(R.string.iap_remove_product), getString(R.string.iap_cancel)
                    , getString(R.string.iap_delete_item_alert_title), getString(R.string.iap_product_remove_description), getFragmentManager(), this);
        } else if (event.equalsIgnoreCase(ECSConstant.IAP_APPLY_VOUCHER)) {
            VoucherFragment voucherFragment = new VoucherFragment();
            Bundle bundle = new Bundle();
            voucherFragment.setArguments(bundle);
            addFragment(voucherFragment, VoucherFragment.TAG, true);
        }
    }

    void startProductDetailFragment(ShoppingCartAdapter mAdapter) {
        ECSEntries theProductDataForDisplayingInProductDetailPage = mAdapter.getTheProductDataForDisplayingInProductDetailPage();
        Bundle bundle = new Bundle();
        bundle.putString(ECSConstant.PRODUCT_TITLE, theProductDataForDisplayingInProductDetailPage.getProduct().getName());
        bundle.putString(ECSConstant.PRODUCT_CTN, theProductDataForDisplayingInProductDetailPage.getProduct().getCode());
        bundle.putString(ECSConstant.PRODUCT_PRICE, theProductDataForDisplayingInProductDetailPage.getBasePrice().getFormattedValue());
        bundle.putString(ECSConstant.PRODUCT_VALUE_PRICE, String.valueOf(theProductDataForDisplayingInProductDetailPage.getBasePrice().getValue()));
        bundle.putString(ECSConstant.PRODUCT_OVERVIEW, theProductDataForDisplayingInProductDetailPage.getProduct().getSummary().getMarketingTextHeader());
        if (bundle.getStringArrayList(ECSConstant.IAP_IGNORE_RETAILER_LIST) != null) {
            final ArrayList<String> list = bundle.getStringArrayList(ECSConstant.IAP_IGNORE_RETAILER_LIST);
            bundle.putStringArrayList(ECSConstant.IAP_IGNORE_RETAILER_LIST, list);
        }
        bundle.putInt(ECSConstant.PRODUCT_QUANTITY, theProductDataForDisplayingInProductDetailPage.getQuantity());
        bundle.putInt(ECSConstant.PRODUCT_STOCK, theProductDataForDisplayingInProductDetailPage.getProduct().getStock().getStockLevel());
        bundle.putSerializable(ECSConstant.SHOPPING_CART_CODE, theProductDataForDisplayingInProductDetailPage);
        addFragment(ProductDetailFragment.createInstance(bundle, AnimationType.NONE), ProductDetailFragment.TAG,true);
    }

    @Override
    public void onGetAddress(Message msg) {
        hideProgressBar();
        if (msg.obj instanceof IAPNetworkError) {
            ECSUtility.showECSAlertDialog(mContext,"Error",((IAPNetworkError) msg.obj).getMessage());
        } else  if (msg.obj instanceof Exception) {
            ECSUtility.showECSAlertDialog(mContext,"Error",((Exception) msg.obj).getMessage());
        } else{
            Bundle bundle = new Bundle();
            if (mSelectedDeliveryMode != null)
                bundle.putParcelable(ECSConstant.SET_DELIVERY_MODE, mSelectedDeliveryMode);

            if ((msg.obj).equals(NetworkConstants.EMPTY_RESPONSE)) {
                addFragment(AddressFragment.createInstance(bundle, AnimationType.NONE),
                        AddressSelectionFragment.TAG,true);
            } else if (msg.obj instanceof List) {
                List list = (List) msg.obj;
                if(list.get(0) instanceof ECSAddress){
                    mAddresses = (List<ECSAddress>)  list;
                }
                bundle.putSerializable(ECSConstant.ADDRESS_LIST, (Serializable) mAddresses);
                addFragment(AddressSelectionFragment.createInstance(bundle, AnimationType.NONE),
                        AddressSelectionFragment.TAG,true);
            }
        }
    }

    @Override
    public void onGetRegions(Message msg) {
        hideProgressBar();
        if (msg.obj instanceof IAPNetworkError) {
            CartModelContainer.getInstance().setRegionList(null);
        }else if (msg.obj instanceof Exception) {
            CartModelContainer.getInstance().setRegionList(null);
        } else if (msg.obj instanceof List) {
            List list = (List)msg.obj;
            if(list != null && !list.isEmpty() && list.get(0) instanceof ECSRegion){
                CartModelContainer.getInstance().setRegionList(list);
            }
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
    public void onLoadFinished(ECSShoppingCart data) {

        ecsShoppingCart = data;
        if(voucherCode!=null) {
            mVoucherController.applyCoupon(voucherCode);
        }

        if( data!=null && data.getEntries()!=null && data.getEntries().size()!=0 ) {

            onOutOfStock(false);
            mAdapter = new ShoppingCartAdapter(getActivity(), data, this);
            mAdapter.setCountArrow(getActivity(), true);

            if (data.getDeliveryItemsQuantity() > 0) {
                updateCount(data.getDeliveryItemsQuantity());
            }
            mRecyclerView.setAdapter(mAdapter);

            String numberOfProducts = getActivity().getResources().getString(R.string.iap_number_of_products);
            if (data.getEntries().size() == 1) {
                numberOfProducts = getActivity().getResources().getString(R.string.iap_number_of_product);
            }
            numberOfProducts = String.format(numberOfProducts, data.getEntries().size());
            mNumberOfProducts.setText(numberOfProducts);
            mNumberOfProducts.setVisibility(View.VISIBLE);
        }else{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onEventReceived(ECSConstant.EMPTY_CART_FRAGMENT_REPLACED);
                }
            });

        }


        hideProgressBar();
    }


    @Override
    public void onLoadFinished(ArrayList<?> data) {

        mVoucherController.applyCoupon(voucherCode);

       /* if (data!=null && data instanceof ArrayList) {
            hideProgressBar();
            mData = (ArrayList<ShoppingCartData>) data;
        }
        if (getActivity() == null) return;
        
        if(mData!=null && mData.get(0)!=null && mData.get(0).getDeliveryMode()!=null) {

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
            mAddressController.fetchDeliveryModes();
        }
        if(voucherCode!=null) {

            Utility.setVoucherCode(null);
            voucherCode=null;
        }
        hideProgressBar();*/
    }

    @Override
    public void onLoadError(Message msg) {
        hideProgressBar();
        if (!isNetworkConnected()) return;

        if (msg.obj instanceof IAPNetworkError) {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), getActivity());
        }
        else if(msg.obj instanceof String){
            NetworkUtility.getInstance().showErrorDialog(getActivity(), getFragmentManager(), getActivity().getString(R.string.iap_ok),
                    getActivity().getString(R.string.iap_server_error), (String) msg.obj);
        }else {
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
        }
        if ((msg.obj instanceof Exception)) {
            updateCartDetails(mShoppingCartAPI);
        }else if((msg.obj instanceof List )) {
            List<ECSDeliveryMode> deliveryModeList =( List<ECSDeliveryMode>) msg.obj;
            mAddressController.setDeliveryMode(deliveryModeList.get(0));
            //TODO
            //CartModelContainer.getInstance().setDeliveryModes(deliveryModeList);
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
        //TODO
        final List<ECSDeliveryMode> deliveryModes = CartModelContainer.getInstance().getDeliveryModes();
        mSelectedDeliveryMode = deliveryModes.get(position);

        createCustomProgressBar(mParentLayout,BIG);
        mAddressController.setDeliveryMode(deliveryModes.get(position));
    }

    @Override
    public void onPositiveBtnClick() {
        EventHelper.getInstance().notifyEventOccurred(ECSConstant.IAP_DELETE_PRODUCT);
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
    public void onSuccess(ECSShoppingCart ecsShoppingCart) {
        hideProgressBar();
        if (ECSUtility.getInstance().getQuantity(ecsShoppingCart) > ECSUtility.getInstance().getMaxCartCount()) {
            NetworkUtility.getInstance().showErrorDialog(getActivity(),getFragmentManager(),
                    getString(R.string.iap_ok),"Exceed Cart limit","You can not add more than "+ ECSUtility.getInstance().getMaxCartCount()+ " product in your cart");
        } else {
            getRegionAndTag();
        }

    }

    @Override
    public void onFailure(Message msg) {
        hideProgressBar();
        NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
    }

}
