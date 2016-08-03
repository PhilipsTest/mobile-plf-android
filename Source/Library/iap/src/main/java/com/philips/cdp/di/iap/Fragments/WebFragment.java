/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.Fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.uikit.customviews.CircularLineProgressBar;

public class WebFragment extends BaseAnimationSupportFragment {

    public static final String TAG = WebPaymentFragment.class.getName();
    protected WebView mWebView;
    private String mUrl;
    private CircularLineProgressBar mProgress;
    private boolean mShowProgressBar = true;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        ViewGroup group = (ViewGroup) inflater.inflate(R.layout.iap_web_payment, container, false);

        mWebView = (WebView) group.findViewById(R.id.wv_payment);
        mProgress = (CircularLineProgressBar) group.findViewById(R.id.cl_progress);
        mProgress.startAnimation(70);
        mWebView.setWebViewClient(new IAPWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mUrl = getWebUrl();
        return group;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView.loadUrl(mUrl);
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    protected String getWebUrl() {
        if (getArguments() == null) {
            throw new RuntimeException("URL must be provided");
        }
        String string = getArguments().getString(IAPConstant.ORDER_TRACK_URL);
        if (string == null) {
            throw new RuntimeException("URL must be provided");
        }
        return string;
    }

    protected boolean shouldOverrideUrlLoading(final String url) {
        return false;
    }

    private boolean shouldHandleError(final int errorCode) {
        return (errorCode == WebViewClient.ERROR_CONNECT
                || errorCode == WebViewClient.ERROR_BAD_URL
                || errorCode == WebViewClient.ERROR_TIMEOUT
                || errorCode == WebViewClient.ERROR_HOST_LOOKUP);
    }

    private class IAPWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {

            return WebFragment.this.shouldOverrideUrlLoading(url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // Handle the error
            if (isVisible()) {
                if (isNetworkNotConnected()) return;
            }
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
            // Redirect to deprecated method, so you can use it in all SDK versions
            if (rerr != null && shouldHandleError(rerr.getErrorCode())) {
                if (isVisible()) {
                    onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                }
            }
        }

        @Override
        public void onPageFinished(final WebView view, final String url) {
            super.onPageFinished(view, url);
            if (mProgress != null && mShowProgressBar) {
                mShowProgressBar = false;
                mProgress.setVisibility(View.GONE);
            }
        }
    }
}
