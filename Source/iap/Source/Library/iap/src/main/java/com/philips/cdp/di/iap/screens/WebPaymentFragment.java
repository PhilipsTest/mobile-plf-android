/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.screens;

import android.content.Context;
import android.os.Bundle;
import android.webkit.CookieManager;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.AlertListener;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.ModelConstants;
import com.philips.cdp.di.iap.utils.NetworkUtility;
import com.philips.cdp.di.iap.utils.Utility;

public class WebPaymentFragment extends WebFragment implements AlertListener {

    public static final String TAG = WebPaymentFragment.class.getName();
    private Context mContext;
    private boolean mIsPaymentFailed;

    private static final String SUCCESS_KEY = "successURL";
    private static final String PENDING_KEY = "pendingURL";
    private static final String FAILURE_KEY = "failureURL";
    private static final String CANCEL_KEY = "cancelURL";

    private static final String PAYMENT_SUCCESS_CALLBACK_URL = "http://www.philips.com/paymentSuccess";
    private static final String PAYMENT_PENDING_CALLBACK_URL = "http://www.philips.com/paymentPending";
    private static final String PAYMENT_FAILURE_CALLBACK_URL = "http://www.philips.com/paymentFailure";
    private static final String PAYMENT_CANCEL_CALLBACK_URL = "http://www.philips.com/paymentCancel";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        IAPAnalytics.trackPage(IAPAnalyticsConstant.WORLD_PAY_PAGE_NAME);
        setTitleAndBackButtonVisibility(R.string.iap_payment, true);
        Utility.isDelvieryFirstTimeUser=false;
        setCartIconVisibility(false);
    }

    @Override
    protected boolean isJavaScriptEnable() {
        return false;
    }

    public static WebPaymentFragment createInstance(Bundle args, AnimationType animType) {
        WebPaymentFragment fragment = new WebPaymentFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getWebUrl() {
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey(ModelConstants.WEB_PAY_URL)) {
            IAPLog.d(TAG,"URL must be provided");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(arguments.getString(ModelConstants.WEB_PAY_URL));
        builder.append("&" + SUCCESS_KEY + "=" + PAYMENT_SUCCESS_CALLBACK_URL);
        builder.append("&" + PENDING_KEY + "=" + PAYMENT_PENDING_CALLBACK_URL);
        builder.append("&" + FAILURE_KEY + "=" + PAYMENT_FAILURE_CALLBACK_URL);
        builder.append("&" + CANCEL_KEY + "=" + PAYMENT_CANCEL_CALLBACK_URL);
        return builder.toString();
    }

    private Bundle createSuccessBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(ModelConstants.ORDER_NUMBER, CartModelContainer.getInstance().getOrderNumber());
        bundle.putBoolean(ModelConstants.PAYMENT_SUCCESS_STATUS, true);
        return bundle;
    }

    private Bundle createErrorBundle(Bundle bundle) {
        bundle.putBoolean(ModelConstants.PAYMENT_SUCCESS_STATUS, false);
        return bundle;
    }

    private void launchConfirmationScreen(Bundle bundle) {
        addFragment(PaymentConfirmationFragment.createInstance(bundle, AnimationType.NONE), null,true);
    }

    @Override
    protected boolean shouldOverrideUrlLoading(final String url) {
        return verifyResultCallBacks(url);
    }

    private boolean verifyResultCallBacks(String url) {

        boolean match = true;
        if (url.startsWith(PAYMENT_SUCCESS_CALLBACK_URL)) {
            launchConfirmationScreen(createSuccessBundle());
        } else if (url.startsWith(PAYMENT_PENDING_CALLBACK_URL)) {
            Bundle bundle = new Bundle();
            launchConfirmationScreen(createErrorBundle(bundle));
        } else if (url.startsWith(PAYMENT_FAILURE_CALLBACK_URL)) {
            mIsPaymentFailed = true;
            IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                    IAPAnalyticsConstant.SPECIAL_EVENTS, IAPAnalyticsConstant.PAYMENT_FAILURE);
            NetworkUtility.getInstance().showDialogMessage(mContext.getString(R.string.iap_payment_failed_title),getString(R.string.iap_payment_failed_message),getFragmentManager(), mContext,this);
        } else if (url.startsWith(PAYMENT_CANCEL_CALLBACK_URL)) {
            IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                    IAPAnalyticsConstant.PAYMENT_STATUS, IAPAnalyticsConstant.CANCELLED);
            Bundle bundle = new Bundle();
            bundle.putBoolean(ModelConstants.PAYMENT_CANCELLED,true);
            launchConfirmationScreen(createErrorBundle(bundle));
        } else {
            match = false;
        }
        if(match){
            clearCookies();;
        }
        return match;
    }

    @Override
    public boolean handleBackEvent() {
        mIsPaymentFailed = false;
        Utility.showActionDialog(mContext, mContext.getString(R.string.iap_ok), mContext.getString(R.string.iap_cancel), mContext.getString(R.string.iap_cancel_order_title), mContext.getString(R.string.iap_cancel_payment), getFragmentManager(), this);
        return true;
    }

    private void handleNavigation() {
        if(shouldGiveCallBack()){
            sendCallback(false);
            return;
        }
        showProductCatalogFragment(WebPaymentFragment.TAG);
    }

    @Override
    public void onPositiveBtnClick() {
        IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                IAPAnalyticsConstant.SPECIAL_EVENTS, IAPAnalyticsConstant.CANCEL_PAYMENT);
        clearCookies();
        handleNavigation();
    }

    @Override
    public void onNegativeBtnClick() {
        if (mIsPaymentFailed) {
            handleNavigation();
        }
    }

    private void clearCookies(){
        CookieManager.getInstance().removeSessionCookies(null);
    }
}