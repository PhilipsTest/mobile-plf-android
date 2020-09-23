/**
 * FAQ Detailed Screen renders the FAQ QUESTION & Answer Webpage with the required data.
 * Some of the response content will be hided dynamically after the response data renders completly
 *
 * @Created 13-Apr-16.
 * <p/>
 * Copyright (c) 2016 Philips. All rights reserved.
 */
package com.philips.cdp.digitalcare.faq.fragments;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.analytics.AnalyticsConstants;
import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;
import com.philips.cdp.digitalcare.util.DigiCareLogger;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class FaqDetailedFragment extends DigitalCareBaseFragment {

    private View mView = null;
    private WebView mWebView = null;
    private ProgressBar mProgressBar = null;

    private String FAQ_PAGE_URL = null;
    private String TAG = FaqDetailedFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity() != null) getActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        if (mView == null) {
            mView = inflater.inflate(R.layout.consumercare_common_webview, container, false);
        }

        Map<String, String> contextData = new HashMap<String, String>();
        contextData.put(AnalyticsConstants.ACTION_KEY_SERVICE_CHANNEL,
                AnalyticsConstants.ACTION_VALUE_SERVICE_CHANNEL_FAQ);
        contextData.put(AnalyticsConstants.PAGE_FAQ_QUESTION_ANSWER,
                getPreviousName());
        DigitalCareConfigManager.getInstance().getTaggingInterface().trackPageWithInfo
                (AnalyticsConstants.PAGE_FAQ_QUESTION_ANSWER,
                        contextData);

        return mView;
    }

    public void setFaqWebUrl(String url) {
        FAQ_PAGE_URL = url;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("url", FAQ_PAGE_URL);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        initView();
        if (savedInstanceState != null) {
            FAQ_PAGE_URL = savedInstanceState.getString("url");
        }
        loadFaq(FAQ_PAGE_URL);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        loadFaq(FAQ_PAGE_URL);
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        loadFaq(FAQ_PAGE_URL);
    }


    @Override
    public void onPause() {
        mWebView.loadUrl("about:blank");
        clearWebViewData();
        super.onPause();
    }

    protected void loadFaq(String pageUrl) {
        if (pageUrl == null) {
            mProgressBar.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
        } else {
            mWebView.getSettings().setJavaScriptEnabled(true);
            mProgressBar.setVisibility(View.VISIBLE);

            mWebView.setVisibility(View.INVISIBLE);

            mWebView.getSettings().setStandardFontFamily("file:///android_asset/fonts/centralesansbook.ttf");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
                mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
                mWebView.getSettings().setDomStorageEnabled(true);
                mWebView.getSettings().setSaveFormData(false);
                mWebView.getSettings().setBuiltInZoomControls(true);
            }
            mWebView.setWebChromeClient(new WebChromeClient() {

                Bitmap videoPoster = BitmapFactory.decodeResource(getResources(), R.drawable.ic_media_video_poster);

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress > 95) {
                        mProgressBar.setVisibility(View.GONE);
                        enableWebView();
                    }
                }

               @Override
               public Bitmap getDefaultVideoPoster() {
                   if (Build.VERSION.SDK_INT >= 26) {
                       return videoPoster;
                   }

                   return super.getDefaultVideoPoster();
               }
           });
            mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    DigiCareLogger.e(TAG, "WebClient Response error : " + error);
                    mProgressBar.setVisibility(View.GONE);
                    enableWebView();

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    DigiCareLogger.d(TAG, "OnPage Finished invoked with URL " + url);
                    mProgressBar.setVisibility(View.GONE);
                    enableWebView();
                    setPaddingForWebdata();
                    mWebView.loadUrl("javascript:(function(){" + "document.getElementsByClassName('group faqfeedback_group')[0].remove();})()");
                    mWebView.loadUrl("javascript:window.CallToAnAndroidFunction.setVisible()");
                }

            });
            mWebView.addJavascriptInterface(new myJavaScriptInterface(), "CallToAnAndroidFunction");
            mWebView.loadUrl(FAQ_PAGE_URL);
        }
    }

    private void enableWebView() {
        if (mWebView != null)
            if (mWebView.getVisibility() == View.INVISIBLE) mWebView.setVisibility(View.VISIBLE);
    }

    protected void setPaddingForWebdata() {
        if (mWebView == null) initView();
        mWebView.loadUrl("javascript:document.body.style.setProperty(\"font-size\", \"100%\");");
        mWebView.loadUrl("javascript:document.body.style.setProperty(\"margin-top\", \"2%\");");
        mWebView.loadUrl("javascript:document.body.style.setProperty(\"margin-bottom\", \"2%\");");
        mWebView.loadUrl("javascript:document.body.style.setProperty(\"margin-left\", \"10%\");");
        mWebView.loadUrl("javascript:document.body.style.setProperty(\"margin-right\", \"10%\");");
    }

    private void initView() {
        mWebView = (WebView) mView.findViewById(R.id.webView);
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mProgressBar = (ProgressBar) mView
                .findViewById(R.id.common_webview_progress);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.QUESTION_KEY);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void setViewParams(Configuration config) {
    }

    @Override
    public String setPreviousPageName() {
        return AnalyticsConstants.PAGE_FAQ_QUESTION_ANSWER;
    }

    protected void clearWebViewData() {
        mWebView.stopLoading();
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.clearFormData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mWebView != null) {
            mWebView = null;
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        CookieManager.getInstance().removeSessionCookies(null);

    }

    private class myJavaScriptInterface {
        @JavascriptInterface
        public void setVisible() {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mWebView != null) mWebView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }
}
