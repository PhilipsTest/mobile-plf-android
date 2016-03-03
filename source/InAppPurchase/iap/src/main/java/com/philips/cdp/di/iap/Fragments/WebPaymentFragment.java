/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.model.ModelConstants;
import com.philips.cdp.di.iap.session.NetworkConstants;

public class WebPaymentFragment extends BaseAnimationSupportFragment {
    public final static String FRAGMENT_TAG = "WebPaymentFragment";

    private static final String SUCCESS_KEY = "successURL";
    private static final String PENDING_KEY = "pendingURL";
    private static final String FAILURE_KEY = "failureURL";
    private static final String CANCEL_KEY = "cancelURL";

    private static final String PAYMENT_SUCCESS_CALLBACK_URL = "http://www.philips.com/paymentSuccess";
    private static final String PAYMENT_PENDING_CALLBACK_URL = "http://www.philips.com/paymentPending";
    private static final String PAYMENT_FAILURE_CALLBACK_URL = "http://www.philips.com/paymentFailure";
    private static final String PAYMENT_CANCEL_CALLBACK_URL = "http://www.philips.com/paymentCancel";

    private WebView mPaymentWebView;
    private String mUrl;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        mPaymentWebView = new WebView(getContext());
        mPaymentWebView.setWebViewClient(new PaymentWebViewClient());
        mPaymentWebView.getSettings().setJavaScriptEnabled(true);
        mUrl = getPaymentURL();
        return mPaymentWebView;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPaymentWebView.loadUrl(mUrl);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPaymentWebView.onResume();
        setTitle(R.string.iap_payment);
        setBackButtonVisibility(View.INVISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaymentWebView.onPause();
    }

    public static WebPaymentFragment createInstance(Bundle args, AnimationType animType) {
        WebPaymentFragment fragment = new WebPaymentFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    private String getPaymentURL() {
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey(ModelConstants.WEBPAY_URL)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(arguments.getString(ModelConstants.WEBPAY_URL));
        builder.append("&" + SUCCESS_KEY + "=" + PAYMENT_SUCCESS_CALLBACK_URL);
        builder.append("&" + PENDING_KEY + "=" + PAYMENT_PENDING_CALLBACK_URL);
        builder.append("&" + FAILURE_KEY + "=" + PAYMENT_FAILURE_CALLBACK_URL);
        builder.append("&" + CANCEL_KEY + "=" + PAYMENT_CANCEL_CALLBACK_URL);
        return builder.toString();
    }

    private Bundle createSuccessBundle() {
        Bundle bundle = new Bundle();
        return bundle;
    }

    private Bundle createErrorBundle() {
        Bundle bundle = new Bundle();
        return bundle;
    }

    private void launchConfirmationScreen(Bundle bundle) {
        addFragment(PaymentConfirmationFragment.createInstance(bundle, AnimationType.NONE), null);
    }

    private class PaymentWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {

            return verifyResultCallBacks(url);
        }

        private boolean verifyResultCallBacks(String url) {
            boolean match = true;
            if (url.contains(PAYMENT_SUCCESS_CALLBACK_URL)) {
                launchConfirmationScreen(createSuccessBundle());
            } else if (url.contains(PAYMENT_PENDING_CALLBACK_URL)) {
                launchConfirmationScreen(createSuccessBundle());
            } else if (url.contains(PAYMENT_FAILURE_CALLBACK_URL)) {
                launchConfirmationScreen(createSuccessBundle());
            } else if (url.contains(PAYMENT_CANCEL_CALLBACK_URL)) {
                launchConfirmationScreen(createSuccessBundle());
            } else {
                match = false;
            }
            return match;
        }
    }
}