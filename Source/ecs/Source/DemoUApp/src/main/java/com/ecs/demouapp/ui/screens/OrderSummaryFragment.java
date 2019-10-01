/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.adapters.CheckOutHistoryAdapter;
import com.ecs.demouapp.ui.address.AddressFields;
import com.ecs.demouapp.ui.analytics.ECSAnalytics;
import com.ecs.demouapp.ui.analytics.ECSAnalyticsConstant;
import com.ecs.demouapp.ui.cart.ShoppingCartAPI;
import com.ecs.demouapp.ui.cart.ShoppingCartData;
import com.ecs.demouapp.ui.cart.ShoppingCartPresenter;
import com.ecs.demouapp.ui.container.CartModelContainer;
import com.ecs.demouapp.ui.controller.AddressController;
import com.ecs.demouapp.ui.controller.ControllerFactory;
import com.ecs.demouapp.ui.controller.PaymentController;
import com.ecs.demouapp.ui.eventhelper.EventHelper;
import com.ecs.demouapp.ui.eventhelper.EventListener;
import com.ecs.demouapp.ui.response.placeorder.PlaceOrder;
import com.ecs.demouapp.ui.session.IAPNetworkError;
import com.ecs.demouapp.ui.session.NetworkConstants;
import com.ecs.demouapp.ui.utils.AlertListener;
import com.ecs.demouapp.ui.utils.ECSConstant;
import com.ecs.demouapp.ui.utils.ECSLog;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.ecs.demouapp.ui.utils.ModelConstants;
import com.ecs.demouapp.ui.utils.NetworkUtility;
import com.ecs.demouapp.ui.utils.Utility;
import com.philips.cdp.di.ecs.model.address.ECSAddress;
import com.philips.cdp.di.ecs.model.address.ECSDeliveryMode;
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart;
import com.philips.cdp.di.ecs.model.cart.ECSEntries;
import com.philips.cdp.di.ecs.model.orders.ECSOrderDetail;
import com.philips.cdp.di.ecs.model.payment.ECSPaymentProvider;
import com.philips.cdp.di.ecs.model.payment.ECSPayment;
import com.philips.cdp.di.ecs.model.region.ECSRegion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderSummaryFragment extends InAppBaseFragment
        implements View.OnClickListener, PaymentController.MakePaymentListener, EventListener, AddressController.AddressListener,
        CheckOutHistoryAdapter.OutOfStockListener, ShoppingCartPresenter.ShoppingCartListener<ShoppingCartData>,
        OnSetDeliveryModeListener, AlertListener, CheckOutHistoryAdapter.OrderSummaryUpdateListner {

    public static final String TAG = OrderSummaryFragment.class.getName();
    Context mContext;
    private RelativeLayout mParentLayout;
    private Button mPayNowBtn;
    private Button mCancelBtn;

    public CheckOutHistoryAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private AddressController mAddressController;
    private ShoppingCartAPI mShoppingCartAPI;
    private List<ECSAddress> mAddresses = new ArrayList<>();
    private ECSDeliveryMode mSelectedDeliveryMode;
    private TextView mNumberOfProducts;
    private ECSPayment mPaymentMethod;
    Bundle bundle;
    protected PaymentController mPaymentController;

    public static OrderSummaryFragment createInstance(Bundle args, AnimationType animType) {
        OrderSummaryFragment fragment = new OrderSummaryFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        EventHelper.getInstance().registerEventNotification(String.valueOf(ECSConstant.BUTTON_STATE_CHANGED), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(ECSConstant.PRODUCT_DETAIL_FRAGMENT), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(ECSConstant.IAP_LAUNCH_PRODUCT_CATALOG), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(ECSConstant.IAP_EDIT_DELIVERY_MODE), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(ECSConstant.PRODUCT_DETAIL_FRAGMENT_FROM_ORDER), this);

        View rootView = inflater.inflate(R.layout.ecs_order_summary_fragment, container, false);
        mParentLayout = rootView.findViewById(R.id.parent_layout);
        initializeViews(rootView);
        Utility.isDelvieryFirstTimeUser = true;
        return rootView;
    }

    void initializeViews(View rootView) {
        TextView tv_checkOutSteps = rootView.findViewById(R.id.tv_checkOutSteps);
        tv_checkOutSteps.setText(String.format(mContext.getString(R.string.iap_checkout_steps_overview), "3"));

        mPaymentController = new PaymentController(mContext, this);
        bundle = getArguments();
        if (bundle.containsKey(ECSConstant.SELECTED_PAYMENT)) {
            mPaymentMethod = (ECSPayment) bundle.getSerializable(ECSConstant.SELECTED_PAYMENT);
            if (mPaymentMethod != null && mPaymentMethod.getBillingAddress() != null) {
                final ECSAddress billingAddress = mPaymentMethod.getBillingAddress();
                final AddressFields billingAddressFields = new AddressFields();
                billingAddressFields.setFirstName(billingAddress.getFirstName());
                billingAddressFields.setLastName(billingAddress.getLastName());
                billingAddressFields.setTitleCode(billingAddress.getTitleCode());
                billingAddressFields.setCountryIsocode(billingAddress.getCountry().getIsocode());
                billingAddressFields.setLine1(billingAddress.getLine1());
                billingAddressFields.setPostalCode(billingAddress.getPostalCode());
                billingAddressFields.setTown(billingAddress.getTown());
                if (billingAddress.getRegion() != null) {
                    billingAddressFields.setRegionName(billingAddress.getRegion().getIsocodeShort());
                }
                CartModelContainer.getInstance().setBillingAddress(billingAddressFields);
            }
        }

        mRecyclerView = rootView.findViewById(R.id.order_summary_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mPayNowBtn = rootView.findViewById(R.id.pay_now_btn);
        mPayNowBtn.setOnClickListener(this);
        mCancelBtn = rootView.findViewById(R.id.cancel_btn);
        mCancelBtn.setOnClickListener(this);
        mShoppingCartAPI = ControllerFactory.getInstance()
                .getShoppingCartPresenter(mContext, this);
        mAddressController = new AddressController(mContext, this);
        //    mAddressController.setDeliveryMode(CartModelContainer.getInstance().fetchDeliveryModes().get(0).getCode());
        // mAddressController.fetchDeliveryModes();
        mNumberOfProducts = rootView.findViewById(R.id.number_of_products);
    }

    @Override
    public void onResume() {
        super.onResume();
        ECSAnalytics.trackPage(ECSAnalyticsConstant.SHOPPING_CART_PAGE_NAME);
        ECSAnalytics.trackAction(ECSAnalyticsConstant.SEND_DATA,
                ECSAnalyticsConstant.SPECIAL_EVENTS, ECSAnalyticsConstant.SHOPPING_CART_VIEW);
        setTitleAndBackButtonVisibility(R.string.iap_checkout, true);
        setCartIconVisibility(false);
        if (isNetworkConnected()) {
            updateCartOnResume();
        }
    }

    private void updateCartOnResume() {
        createCustomProgressBar(mParentLayout, BIG);
        updateCartDetails(mShoppingCartAPI);
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
        presenter.getCurrentCartDetails();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterEventNotification();
    }

    private void unregisterEventNotification() {
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(ECSConstant.BUTTON_STATE_CHANGED), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(ECSConstant.PRODUCT_DETAIL_FRAGMENT), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(ECSConstant.IAP_LAUNCH_PRODUCT_CATALOG), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(ECSConstant.IAP_EDIT_DELIVERY_MODE), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(ECSConstant.IAP_UPDATE_PRODUCT_COUNT), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(ECSConstant.PRODUCT_DETAIL_FRAGMENT_FROM_ORDER), this);
    }

    @Override
    public void onClick(final View v) {

        if (!isNetworkConnected()) return;
        if (v.getId() == R.id.pay_now_btn) {
            if (mPaymentMethod != null)
                showCvvDialog(getFragmentManager());
            else {
                placeOrder(null);
            }
        } else if (v.getId() == R.id.cancel_btn) {
            unregisterEventNotification();
            doOnCancelOrder();
        }
    }

    private void placeOrder(String pSecurityCode) {

        createCustomProgressBar(mParentLayout, BIG);
        mPaymentController.placeOrder(pSecurityCode);

    }

    private void showCvvDialog(FragmentManager pFragmentManager) {
        if (mPaymentMethod != null)
            bundle.putSerializable(ECSConstant.SELECTED_PAYMENT, mPaymentMethod);
        CvvCvcDialogFragment cvvCvcDialogFragment = new CvvCvcDialogFragment();
        cvvCvcDialogFragment.setTargetFragment(this, CvvCvcDialogFragment.REQUEST_CODE);
        cvvCvcDialogFragment.setArguments(bundle);
        cvvCvcDialogFragment.show(pFragmentManager, "EditErrorDialog");
        cvvCvcDialogFragment.setShowsDialog(true);
    }


    private void doOnCancelOrder() {
        Fragment fragment = getFragmentManager().findFragmentByTag(BuyDirectFragment.TAG);
        if (fragment != null) {
            moveToVerticalAppByClearingStack();
        } else {
            showFragment(ShoppingCartFragment.TAG);
        }
    }

    private boolean paymentMethodAvailable() {
        return mPaymentMethod != null;
    }

    @Override
    public boolean handleBackEvent() {
        unregisterEventNotification();
        showAddressFragment(AddressSelectionFragment.TAG);
        return true;
    }

    @Override
    public void onEventReceived(final String event) {
        hideProgressBar();
        if (event.equalsIgnoreCase(String.valueOf(ECSConstant.BUTTON_STATE_CHANGED))) {
            mPayNowBtn.setEnabled(!Boolean.valueOf(event));
        } else if (event.equalsIgnoreCase(String.valueOf(ECSConstant.PRODUCT_DETAIL_FRAGMENT_FROM_ORDER))) {
            startProductDetailFragment();
        } else if (event.equalsIgnoreCase(String.valueOf(ECSConstant.IAP_LAUNCH_PRODUCT_CATALOG))) {
            showProductCatalogFragment(ShoppingCartFragment.TAG);
        } else if (event.equalsIgnoreCase(ECSConstant.IAP_EDIT_DELIVERY_MODE)) {
            addFragment(DeliveryMethodFragment.createInstance(new Bundle(), AnimationType.NONE),
                    DeliveryMethodFragment.TAG, true);
        }
    }

    private void startProductDetailFragment() {
        ECSEntries shoppingCartData = mAdapter.getTheProductDataForDisplayingInProductDetailPage();
        Bundle bundle = new Bundle();
        bundle.putString(ECSConstant.PRODUCT_TITLE, shoppingCartData.getProduct().getSummary().getProductTitle());
        bundle.putString(ECSConstant.PRODUCT_CTN, shoppingCartData.getProduct().getCode());
        bundle.putString(ECSConstant.PRODUCT_PRICE, shoppingCartData.getBasePrice().getFormattedValue());
        bundle.putString(ECSConstant.PRODUCT_OVERVIEW, shoppingCartData.getProduct().getSummary().getMarketingTextHeader());
        addFragment(ProductDetailFragment.createInstance(bundle, AnimationType.NONE), ProductDetailFragment.TAG, true);
    }

    @Override
    public void onGetAddress(Message msg) {
        hideProgressBar();
        if (msg.obj instanceof IAPNetworkError) {
            ECSUtility.showECSAlertDialog(mContext,"Error",((IAPNetworkError) msg.obj).getMessage());
        } else if (msg.obj instanceof Exception) {

            Exception exception = (Exception) msg.obj;
            ECSUtility.showECSAlertDialog(mContext,"Error",((Exception) msg.obj).getMessage());
        } else {
            Bundle bundle = new Bundle();
            if (mSelectedDeliveryMode != null)
                bundle.putParcelable(ECSConstant.SET_DELIVERY_MODE, mSelectedDeliveryMode);

            if ((msg.obj).equals(NetworkConstants.EMPTY_RESPONSE)) {
                addFragment(AddressFragment.createInstance(bundle, AnimationType.NONE),
                        AddressFragment.TAG, true);
            } else if (msg.obj instanceof List) {
                List list = (List) msg.obj;
                if(list.get(0) instanceof ECSAddress){
                    mAddresses = (List<ECSAddress>)  list;
                }
                bundle.putSerializable(ECSConstant.ADDRESS_LIST, (Serializable) mAddresses);
                addFragment(AddressSelectionFragment.createInstance(bundle, AnimationType.NONE),
                        AddressSelectionFragment.TAG, true);
            }
        }
    }

    @Override
    public void onGetRegions(Message msg) {
        if (msg.obj instanceof IAPNetworkError) {
            CartModelContainer.getInstance().setRegionList(null);
        } else if (msg.obj instanceof Exception) {
            CartModelContainer.getInstance().setRegionList(null);
        } else if (msg.obj instanceof List) {
            List list = (List)msg.obj;
            if(list != null && !list.isEmpty() && list.get(0) instanceof ECSRegion){
                CartModelContainer.getInstance().setRegionList(list);
            }else{
                CartModelContainer.getInstance().setRegionList(null);
            }
        } else {
            CartModelContainer.getInstance().setRegionList(null);
        }

        mAddressController.getAddresses();
    }

    @Override
    public void onOutOfStock(boolean isOutOfStockReached) {
        if (mPayNowBtn == null) return;
        mPayNowBtn.setEnabled(isOutOfStockReached);
    }

//    @Override
//    public void onLoadFinished(final ArrayList<ShoppingCartData> data) {
//        dismissProgressDialog();
//        if (getActivity() == null) return;
//        mData = data;
//        onOutOfStock(false);
//        mAdapter = new ShoppingCartAdapter(mContext, mData, this);
//        if (data.get(0) != null && data.get(0).getDeliveryItemsQuantity() > 0) {
//            updateCount(data.get(0).getDeliveryItemsQuantity());
//        }
//        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.tagProducts();
//
//        String numberOfProducts = mContext.getResources().getString(R.string.iap_number_of_products);
//        numberOfProducts = String.format(numberOfProducts, mData.size());
//        mNumberOfProducts.setText(numberOfProducts);
//        mNumberOfProducts.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void onLoadFinished(ShoppingCartData data) {
//        dismissProgressDialog();
//        IAPLog.d(IAPLog.LOG, data.getCtnNumber());
//
//    }

    @Override
    public void onLoadFinished(ArrayList<?> data) {

    }

    @Override
    public void onLoadError(Message msg) {
        hideProgressBar();
        if (!isNetworkConnected()) return;

        if (msg.obj instanceof IAPNetworkError) {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
        }
        else if(msg.obj instanceof String){
            NetworkUtility.getInstance().showErrorDialog(getActivity(), getFragmentManager(), getActivity().getString(R.string.iap_ok),
                    getActivity().getString(R.string.iap_server_error), (String) msg.obj);
        }else {
            NetworkUtility.getInstance().showErrorDialog(mContext, getFragmentManager(), mContext.getString(R.string.iap_ok),
                    mContext.getString(R.string.iap_server_error), mContext.getString(R.string.iap_something_went_wrong));
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
        } else if ((msg.obj instanceof Exception)) {
            Exception exception = (Exception) msg.obj;
            ECSUtility.showECSAlertDialog(mContext,"Error",exception.getMessage());
        } else if((msg.obj instanceof List )) {
            List<ECSDeliveryMode> deliveryModeList =( List<ECSDeliveryMode>) msg.obj;
            CartModelContainer.getInstance().setDeliveryModes(deliveryModeList);
            mAddressController.setDeliveryMode(deliveryModeList.get(0));
            updateCartDetails(mShoppingCartAPI);
        }
    }

    @Override
    public void onSetDeliveryMode(Message msg) {
        Utility.isDelvieryFirstTimeUser = false;
        if (msg.obj.equals(ECSConstant.IAP_SUCCESS)) {
            updateCartOnResume();
        } else {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
            hideProgressBar();
        }
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
    public void onLoadFinished(ECSShoppingCart ecsShoppingCart) {
        hideProgressBar();

        if (getActivity() == null) return;
        onOutOfStock(false);
        mAdapter = new CheckOutHistoryAdapter(mContext, ecsShoppingCart, this);
        mAdapter.setOrderSummaryUpdateListner(this);
        if (ecsShoppingCart.getDeliveryItemsQuantity() > 0) {
            updateCount(ecsShoppingCart.getDeliveryItemsQuantity());
        }
        mRecyclerView.setAdapter(mAdapter);
        String numberOfProducts = mContext.getResources().getString(R.string.iap_number_of_products);
        if (ecsShoppingCart.getEntries().size() == 1) {
            numberOfProducts = mContext.getResources().getString(R.string.iap_number_of_product);
        }
        numberOfProducts = String.format(numberOfProducts, ecsShoppingCart.getEntries().size());
        mNumberOfProducts.setText(numberOfProducts);
        mNumberOfProducts.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(int position) {
        final List<ECSDeliveryMode> deliveryModes = CartModelContainer.getInstance().getDeliveryModes();
        createCustomProgressBar(mParentLayout, BIG);
        updateCartDetails(mShoppingCartAPI);
    }

    @Override
    public void onPositiveBtnClick() {
        EventHelper.getInstance().notifyEventOccurred(ECSConstant.IAP_DELETE_PRODUCT);
    }

    @Override
    public void onNegativeBtnClick() {

    }

    @Override
    public void onMakePayment(Message msg) {
        hideProgressBar();
        if (msg.obj instanceof ECSPaymentProvider) {

            ECSPaymentProvider mMakePaymentData = (ECSPaymentProvider) msg.obj;
            Bundle bundle = new Bundle();
            bundle.putString(ModelConstants.WEB_PAY_URL, mMakePaymentData.getWorldpayUrl());
            addFragment(WebPaymentFragment.createInstance(bundle, AnimationType.NONE), null, true);
            //todo Anurag: after worlpay payment is done order confirmation screen has to be shown
        } else if (msg.obj instanceof Exception) {
            hideProgressBar();
            Exception exception = (Exception) msg.obj;
            ECSUtility.showECSAlertDialog(mContext,"Error",exception);
        } else {
            NetworkUtility.getInstance().showErrorDialog(mContext, getFragmentManager(), mContext.getString(R.string.iap_ok),
                    mContext.getString(R.string.iap_server_error), mContext.getString(R.string.iap_something_went_wrong));
        }

    }

    @Override
    public void onPlaceOrder(final Message msg) {
        // launchConfirmationScreen(new PlaceOrder());//need to remove
        if (msg.obj instanceof ECSOrderDetail) {
            ECSOrderDetail order = (ECSOrderDetail) msg.obj;

            String orderID = order.getCode();
            updateCount(0);
            CartModelContainer.getInstance().setOrderNumber(orderID);
            if (paymentMethodAvailable()) {
                hideProgressBar();
                launchConfirmationScreen((ECSOrderDetail) msg.obj);
            } else {

                mPaymentController.makPayment(order);
            }
        } else if (msg.obj instanceof Exception) {
            hideProgressBar();
            Exception exception = (Exception) msg.obj;
            ECSUtility.showECSAlertDialog(mContext,"Error",exception.getMessage());
           /* IAPNetworkError iapNetworkError = (IAPNetworkError) msg.obj;
            if (null != iapNetworkError.getServerError()) {
                checkForOutOfStock(iapNetworkError, msg);
            } else {
                NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
            }*/
        }else if(msg.obj instanceof String){
            hideProgressBar();
            ECSUtility.showECSAlertDialog(mContext,"Error",(String)msg.obj);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CvvCvcDialogFragment.REQUEST_CODE) {
            String securityCode = data.getStringExtra(
                    ECSConstant.CVV_KEY_BUNDLE);
            ECSLog.d(ECSLog.LOG, "CVV =" + securityCode);
            placeOrder(securityCode);
           // mAddressController.getDeliveryModes();

        }
    }


    private void launchConfirmationScreen(ECSOrderDetail details) {
        Bundle bundle = new Bundle();
        bundle.putString(ModelConstants.ORDER_NUMBER, details.getCode());
        bundle.putBoolean(ModelConstants.PAYMENT_SUCCESS_STATUS, Boolean.TRUE);
        addFragment(PaymentConfirmationFragment.createInstance(bundle, AnimationType.NONE), null, true);
    }

    private void checkForOutOfStock(final IAPNetworkError iapNetworkError, Message msg) {
        com.ecs.demouapp.ui.response.error.Error error = iapNetworkError.getServerError().getErrors().get(0);
        String type = error.getType();
        if (type.equalsIgnoreCase(ECSConstant.INSUFFICIENT_STOCK_LEVEL_ERROR)) {
            String subject = error.getMessage();
            NetworkUtility.getInstance().showErrorDialog(mContext, getFragmentManager(), getString(R.string.iap_ok),
                    getString(R.string.iap_out_of_stock), subject);
        } else {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
        }
    }

    @Override
    public void onGetCartUpdate() {
        hideProgressBar();
    }


}
