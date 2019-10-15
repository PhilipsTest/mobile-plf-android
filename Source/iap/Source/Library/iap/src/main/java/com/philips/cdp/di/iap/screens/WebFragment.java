/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.screens;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.philips.cdp.di.iap.R;

public abstract class WebFragment extends InAppBaseFragment {

    public static final String TAG = WebFragment.class.getName();
    protected WebView mWebView;
    private String mUrl;
    private RelativeLayout mParentContainer;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.iap_web_payment, container, false);
        mParentContainer = (RelativeLayout) viewGroup.findViewById(R.id.iap_web_container);
        mWebView = (WebView) viewGroup.findViewById(R.id.wv_payment);
        mWebView.setWebViewClient(new IAPWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSaveFormData(false);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 9_3 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13E233 Safari/601.1");
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setUseWideViewPort(true);
        createCustomProgressBar(mParentContainer,BIG);

        mUrl = getWebUrl();
        return viewGroup;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView.loadUrl(mUrl);
    }

    @Override
    public void onResume() {
        super.onResume();
        setCartIconVisibility(false);
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    protected abstract boolean isJavaScriptEnable();

    protected abstract String getWebUrl();

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

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            return WebFragment.this.shouldOverrideUrlLoading(url);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return WebFragment.this.shouldOverrideUrlLoading(request.getUrl().toString());
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view,handler,error);
            handler.proceed(); // Ignore SSL certificate errors
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            hideProgressBar();
        }

        @TargetApi(Build.VERSION_CODES.M)
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
            hideProgressBar();
            super.onPageFinished(view, url);
        }
    }
}
