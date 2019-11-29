/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.screens;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.adapters.CheckOutHistoryAdapter;
import com.philips.cdp.di.iap.address.AddressFields;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.cart.ShoppingCartAPI;
import com.philips.cdp.di.iap.cart.ShoppingCartData;
import com.philips.cdp.di.iap.cart.ShoppingCartPresenter;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.controller.AddressController;
import com.philips.cdp.di.iap.controller.ControllerFactory;
import com.philips.cdp.di.iap.controller.PaymentController;
import com.philips.cdp.di.iap.eventhelper.EventHelper;
import com.philips.cdp.di.iap.eventhelper.EventListener;
import com.philips.cdp.di.iap.response.State.RegionsList;
import com.philips.cdp.di.iap.response.addresses.Addresses;
import com.philips.cdp.di.iap.response.addresses.DeliveryModes;
import com.philips.cdp.di.iap.response.addresses.GetDeliveryModes;
import com.philips.cdp.di.iap.response.addresses.GetShippingAddressData;
import com.philips.cdp.di.iap.response.payment.MakePaymentData;
import com.philips.cdp.di.iap.response.payment.PaymentMethod;
import com.philips.cdp.di.iap.response.placeorder.PlaceOrder;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.IAPUtility;
import com.philips.cdp.di.iap.utils.ModelConstants;
import com.philips.cdp.di.iap.utils.NetworkUtility;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;
import com.philips.platform.uid.view.widget.Label;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderSummaryFragment extends InAppBaseFragment
        implements View.OnClickListener, PaymentController.MakePaymentListener, EventListener, AddressController.AddressListener,
        CheckOutHistoryAdapter.OutOfStockListener, ShoppingCartPresenter.ShoppingCartListener<ShoppingCartData>,
        OnSetDeliveryModeListener, com.philips.cdp.di.iap.utils.AlertListener, CheckOutHistoryAdapter.OrderSummaryUpdateListner {

    public static final String TAG = OrderSummaryFragment.class.getName();
    Context mContext;
    private RelativeLayout mParentLayout;
    private Button mPayNowBtn;
    private Button mCancelBtn;
    private Label mPrivacy, mFaq, mTerms;

    public CheckOutHistoryAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private AddressController mAddressController;
    private ShoppingCartAPI mShoppingCartAPI;
    private ArrayList<ShoppingCartData> mData = new ArrayList<>();
    private List<Addresses> mAddresses = new ArrayList<>();
    private DeliveryModes mSelectedDeliveryMode;
    private TextView mNumberOfProducts;
    private PaymentMethod mPaymentMethod;
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
        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.BUTTON_STATE_CHANGED), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.PRODUCT_DETAIL_FRAGMENT), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.IAP_LAUNCH_PRODUCT_CATALOG), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.IAP_EDIT_DELIVERY_MODE), this);
        EventHelper.getInstance().registerEventNotification(String.valueOf(IAPConstant.PRODUCT_DETAIL_FRAGMENT_FROM_ORDER), this);

        View rootView = inflater.inflate(R.layout.iap_order_summary_fragment, container, false);
        mParentLayout = rootView.findViewById(R.id.parent_layout);
        initializeViews(rootView);

        return rootView;
    }


    void initializeViews(View rootView) {
        TextView tv_checkOutSteps = rootView.findViewById(R.id.tv_checkOutSteps);
        tv_checkOutSteps.setText(String.format(mContext.getString(R.string.iap_checkout_steps_overview), "3"));

        mPaymentController = new PaymentController(mContext, this);
        bundle = getArguments();
        if (bundle.containsKey(IAPConstant.SELECTED_PAYMENT)) {
            mPaymentMethod = (PaymentMethod) bundle.getSerializable(IAPConstant.SELECTED_PAYMENT);
            if (mPaymentMethod != null && mPaymentMethod.getBillingAddress() != null) {
                final Addresses billingAddress = mPaymentMethod.getBillingAddress();
                final AddressFields billingAddressFields = new AddressFields();
                billingAddressFields.setFirstName(billingAddress.getFirstName());
                billingAddressFields.setLastName(billingAddress.getLastName());
                billingAddressFields.setTitleCode(billingAddress.getTitleCode());
                billingAddressFields.setCountryIsocode(billingAddress.getCountry().getIsocode());
                billingAddressFields.setLine1(billingAddress.getLine1());
                billingAddressFields.setPostalCode(billingAddress.getPostalCode());
                billingAddressFields.setTown(billingAddress.getTown());
                if(billingAddress.getRegion()!=null) {
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
        mPrivacy = rootView.findViewById(R.id.iap_privacy);
        mFaq = rootView.findViewById(R.id.iap_faq);
        mTerms = rootView.findViewById(R.id.iap_terms);
        mCancelBtn.setOnClickListener(this);
        privacyTextView(mPrivacy);
        faqTextView(mFaq);
        termsTextView(mTerms);
        mShoppingCartAPI = ControllerFactory.getInstance()
                .getShoppingCartPresenter(mContext, this);
        mAddressController = new AddressController(mContext, this);
        //    mAddressController.setDeliveryMode(CartModelContainer.getInstance().getDeliveryModes().get(0).getCode());
       // mAddressController.getDeliveryModes();
        mNumberOfProducts = rootView.findViewById(R.id.number_of_products);
    }

    private void privacyTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                mContext.getString(R.string.iap_read_privacy));
        spanTxt.append(" ");
        spanTxt.append(mContext.getString(R.string.iap_privacy));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showPrivacyFragment();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(R.attr.uidHyperlinkDefaultPressedTextColor);
            }
        }, spanTxt.length() - mContext.getString(R.string.iap_privacy).length(), spanTxt.length(), 0);
        spanTxt.append(" ");
        spanTxt.append(mContext.getString(R.string.iap_just_read));
        spanTxt.append(".");
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    private void faqTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                mContext.getString(R.string.iap_questions));
        spanTxt.append(" ");
        spanTxt.append(mContext.getString(R.string.iap_faq));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showFaqFragment();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(R.attr.uidHyperlinkDefaultPressedTextColor);
            }
        }, spanTxt.length() - mContext.getString(R.string.iap_faq).length(), spanTxt.length(), 0);
        spanTxt.append(" ");
        spanTxt.append(mContext.getString(R.string.iap_page));
        spanTxt.append(".");
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    private void termsTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                mContext.getString(R.string.iap_accept_terms));
        spanTxt.append(" ");
        spanTxt.append(mContext.getString(R.string.iap_terms_conditions));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showTermsFragment();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(R.attr.uidHyperlinkDefaultPressedTextColor);
            }
        }, spanTxt.length() - mContext.getString(R.string.iap_terms_conditions).length(), spanTxt.length(), 0);
        spanTxt.append(".");
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        IAPAnalytics.trackPage(IAPAnalyticsConstant.SHOPPING_CART_PAGE_NAME);
        IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                IAPAnalyticsConstant.SPECIAL_EVENTS, IAPAnalyticsConstant.SHOPPING_CART_VIEW);
        setTitleAndBackButtonVisibility(R.string.iap_checkout, true);
        setCartIconVisibility(false);
        if (isNetworkConnected()) {
            updateCartOnResume();
        }
        if(setLinkVisibility()){
                mPrivacy.setVisibility(View.VISIBLE);
            mTerms.setVisibility(View.VISIBLE);
            mFaq.setVisibility(View.VISIBLE);
        } else {
            mPrivacy.setVisibility(View.GONE);
            mTerms.setVisibility(View.GONE);
            mFaq.setVisibility(View.GONE);
        }
    }

    private void updateCartOnResume() {
        createCustomProgressBar(mParentLayout,BIG);
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

    private void unregisterEventNotification(){
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(IAPConstant.BUTTON_STATE_CHANGED), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(IAPConstant.PRODUCT_DETAIL_FRAGMENT), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(IAPConstant.IAP_LAUNCH_PRODUCT_CATALOG), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(IAPConstant.IAP_EDIT_DELIVERY_MODE), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(IAPConstant.IAP_UPDATE_PRODUCT_COUNT), this);
        EventHelper.getInstance().unregisterEventNotification(String.valueOf(IAPConstant.PRODUCT_DETAIL_FRAGMENT_FROM_ORDER), this);
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

    private void showPrivacyFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(IAPConstant.IAP_PRIVACY_URL, IAPUtility.getInstance().getPrivacyUrl());
        addFragment(WebPrivacy.createInstance(bundle, AnimationType.NONE), null, true);
    }

    private void showTermsFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(IAPConstant.IAP_TERMS_URL, IAPUtility.getInstance().getTermsUrl());
        bundle.putString(IAPConstant.IAP_TERMS,IAPConstant.IAP_TERMS);
        addFragment(WebPrivacy.createInstance(bundle, AnimationType.NONE), null, true);
    }

    private void showFaqFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(IAPConstant.IAP_FAQ_URL, IAPUtility.getInstance().getFaqUrl());
        bundle.putString(IAPConstant.IAP_FAQ,IAPConstant.IAP_FAQ);
        addFragment(WebPrivacy.createInstance(bundle, AnimationType.NONE), null, true);
    }

    private boolean setLinkVisibility(){
        return (!(ControllerFactory.getInstance().isPlanB()) && ((IAPUtility.getInstance().getPrivacyUrl() != null) && (IAPUtility.getInstance().getTermsUrl() != null) && (IAPUtility.getInstance().getFaqUrl() != null))) &&
                ((CartModelContainer.getInstance().getCountry().equals("DE")) || (CartModelContainer.getInstance().getCountry().equals("CH")) || (CartModelContainer.getInstance().getCountry().equals("AT")) || (CartModelContainer.getInstance().getCountry().equals("SE")));
    }


    private void placeOrder(String pSecurityCode) {
        IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA, IAPAnalyticsConstant.DELIVERY_METHOD,
                IAPAnalyticsConstant.DELIVERY_UPS_PARCEL);
        createCustomProgressBar(mParentLayout, BIG);
        mPaymentController.placeOrder(pSecurityCode);

    }

    private void showCvvDialog(FragmentManager pFragmentManager) {
        if (mPaymentMethod != null)
            bundle.putSerializable(IAPConstant.SELECTED_PAYMENT, mPaymentMethod);
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
        if (event.equalsIgnoreCase(String.valueOf(IAPConstant.BUTTON_STATE_CHANGED))) {
            mPayNowBtn.setEnabled(!Boolean.valueOf(event));
        } else if (event.equalsIgnoreCase(String.valueOf(IAPConstant.PRODUCT_DETAIL_FRAGMENT_FROM_ORDER))) {
            startProductDetailFragment();
        } else if (event.equalsIgnoreCase(String.valueOf(IAPConstant.IAP_LAUNCH_PRODUCT_CATALOG))) {
            showProductCatalogFragment(ShoppingCartFragment.TAG);
        } else if (event.equalsIgnoreCase(IAPConstant.IAP_EDIT_DELIVERY_MODE)) {
            addFragment(DeliveryMethodFragment.createInstance(new Bundle(), AnimationType.NONE),
                    DeliveryMethodFragment.TAG,true);
        }
    }

    private void startProductDetailFragment() {
        ShoppingCartData shoppingCartData = mAdapter.getTheProductDataForDisplayingInProductDetailPage();
        Bundle bundle = new Bundle();
        bundle.putString(IAPConstant.PRODUCT_TITLE, shoppingCartData.getProductTitle());
        bundle.putString(IAPConstant.PRODUCT_CTN, shoppingCartData.getCtnNumber());
        bundle.putString(IAPConstant.PRODUCT_PRICE, shoppingCartData.getFormattedPrice());
        bundle.putString(IAPConstant.PRODUCT_OVERVIEW, shoppingCartData.getMarketingTextHeader());
        addFragment(ProductDetailFragment.createInstance(bundle, AnimationType.NONE), ProductDetailFragment.TAG,true);
    }

    @Override
    public void onGetAddress(Message msg) {
        hideProgressBar();
        if (msg.obj instanceof IAPNetworkError) {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
        } else {
            Bundle bundle = new Bundle();
            if (mSelectedDeliveryMode != null)
                bundle.putParcelable(IAPConstant.SET_DELIVERY_MODE, mSelectedDeliveryMode);

            if ((msg.obj).equals(NetworkConstants.EMPTY_RESPONSE)) {
                addFragment(AddressFragment.createInstance(bundle, AnimationType.NONE),
                        AddressFragment.TAG,true);
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
        if (data != null && data instanceof ArrayList)
            hideProgressBar();
        if (getActivity() == null) return;
        mData = (ArrayList<ShoppingCartData>) data;
        onOutOfStock(false);
        mAdapter = new CheckOutHistoryAdapter(mContext, mData, this);
        mAdapter.setOrderSummaryUpdateListner(this);
        if (mData.get(0) != null && mData.get(0).getDeliveryItemsQuantity() > 0) {
            updateCount(mData.get(0).getDeliveryItemsQuantity());
        }
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.tagProducts();
        String numberOfProducts = mContext.getResources().getString(R.string.iap_number_of_products);
        if (mData.size() == 1) {
            numberOfProducts = mContext.getResources().getString(R.string.iap_number_of_product);
        }
        numberOfProducts = String.format(numberOfProducts, mData.size());
        mNumberOfProducts.setText(numberOfProducts);
        mNumberOfProducts.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadError(Message msg) {
        hideProgressBar();
        if (!isNetworkConnected()) return;

        if (msg.obj instanceof IAPNetworkError) {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
        } else {
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
        } else if ((msg.obj instanceof GetDeliveryModes)) {
            GetDeliveryModes deliveryModes = (GetDeliveryModes) msg.obj;
            List<DeliveryModes> deliveryModeList = deliveryModes.getDeliveryModes();
            CartModelContainer.getInstance().setDeliveryModes(deliveryModeList);
            mAddressController.setDeliveryMode(deliveryModeList.get(0).getCode());
            updateCartDetails(mShoppingCartAPI);
        }
    }

    @Override
    public void onSetDeliveryMode(Message msg) {
        if (msg.obj.equals(IAPConstant.IAP_SUCCESS)) {
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
    public void onItemClick(int position) {
        final List<DeliveryModes> deliveryModes = CartModelContainer.getInstance().getDeliveryModes();
        createCustomProgressBar(mParentLayout,BIG);
        updateCartDetails(mShoppingCartAPI);
    }

    @Override
    public void onPositiveBtnClick() {
        EventHelper.getInstance().notifyEventOccurred(IAPConstant.IAP_DELETE_PRODUCT);
    }

    @Override
    public void onNegativeBtnClick() {

    }

    @Override
    public void onMakePayment(Message msg) {
        hideProgressBar();
        if (msg.obj instanceof MakePaymentData) {

            //Track new billing address added action
            IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA, IAPAnalyticsConstant.SPECIAL_EVENTS,
                    IAPAnalyticsConstant.NEW_BILLING_ADDRESS_ADDED);

            MakePaymentData mMakePaymentData = (MakePaymentData) msg.obj;
            Bundle bundle = new Bundle();
            bundle.putString(ModelConstants.WEB_PAY_URL, mMakePaymentData.getWorldpayUrl());
            addFragment(WebPaymentFragment.createInstance(bundle, AnimationType.NONE), null,true);
        } else if (msg.obj instanceof IAPNetworkError) {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
        } else {
            NetworkUtility.getInstance().showErrorDialog(mContext, getFragmentManager(), mContext.getString(R.string.iap_ok),
                    mContext.getString(R.string.iap_server_error), mContext.getString(R.string.iap_something_went_wrong));
        }

    }

    @Override
    public void onPlaceOrder(final Message msg) {
        // launchConfirmationScreen(new PlaceOrder());//need to remove
        if (msg.obj instanceof PlaceOrder) {
            PlaceOrder order = (PlaceOrder) msg.obj;
            String orderID = order.getCode();
            updateCount(0);
            CartModelContainer.getInstance().setOrderNumber(orderID);
            if (paymentMethodAvailable()) {
                hideProgressBar();
                launchConfirmationScreen((PlaceOrder) msg.obj);
            } else {
                mPaymentController.makPayment(orderID);
            }
        } else if (msg.obj instanceof IAPNetworkError) {
            hideProgressBar();
            IAPNetworkError iapNetworkError = (IAPNetworkError) msg.obj;
            if (null != iapNetworkError.getServerError()) {
                checkForOutOfStock(iapNetworkError, msg);
            } else {
                NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CvvCvcDialogFragment.REQUEST_CODE) {
            String securityCode = data.getStringExtra(
                    IAPConstant.CVV_KEY_BUNDLE);
            IAPLog.d(IAPLog.LOG, "CVV =" + securityCode);
            placeOrder(securityCode);
            mAddressController.getDeliveryModes();

        }
    }


    private void launchConfirmationScreen(PlaceOrder details) {
        Bundle bundle = new Bundle();
        bundle.putString(ModelConstants.ORDER_NUMBER, details.getCode());
        bundle.putBoolean(ModelConstants.PAYMENT_SUCCESS_STATUS, Boolean.TRUE);
        addFragment(PaymentConfirmationFragment.createInstance(bundle, AnimationType.NONE), null,true);
    }

    private void checkForOutOfStock(final IAPNetworkError iapNetworkError, Message msg) {
        com.philips.cdp.di.iap.response.error.Error error = iapNetworkError.getServerError().getErrors().get(0);
        String type = error.getType();
        if (type.equalsIgnoreCase(IAPConstant.INSUFFICIENT_STOCK_LEVEL_ERROR)) {
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
