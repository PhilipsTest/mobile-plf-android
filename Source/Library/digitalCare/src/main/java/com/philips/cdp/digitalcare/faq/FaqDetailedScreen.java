package com.philips.cdp.digitalcare.faq;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.analytics.AnalyticsConstants;
import com.philips.cdp.digitalcare.analytics.AnalyticsTracker;
import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;
import com.philips.cdp.digitalcare.util.DigiCareLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 310190678 on 13-Apr-16.
 */
public class FaqDetailedScreen extends DigitalCareBaseFragment {

    private View mView = null;
    private WebView mWebView = null;
    private ProgressBar mProgressBar = null;
    private ImageView mActionBarMenuIcon = null;
    private ImageView mActionBarArrow = null;

    private String FAQ_PAGE_URL = null;
    private String TAG = FaqDetailedScreen.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.common_webview, container, false);
        }

        Map<String, Object> contextData = new HashMap<String, Object>();
        contextData.put(AnalyticsConstants.ACTION_KEY_SERVICE_CHANNEL, AnalyticsConstants.ACTION_VALUE_SERVICE_CHANNEL_FAQ);
        AnalyticsTracker.trackPage(AnalyticsConstants.PAGE_FAQ,
                getPreviousName(), contextData);

        return mView;
    }

    public void setFaqWebUrl(String url) {
        FAQ_PAGE_URL = url;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActionBarMenuIcon = (ImageView) getActivity().findViewById(R.id.home_icon);
        mActionBarArrow = (ImageView) getActivity().findViewById(R.id.back_to_home_img);
        hideActionBarIcons(mActionBarMenuIcon, mActionBarArrow);
        initView();
        loadFaq();
    }

    @Override
    public void onResume() {
        super.onResume();
        enableActionBarLeftArrow(mActionBarMenuIcon, mActionBarArrow);
    }

    private void loadFaq() {
        if (FAQ_PAGE_URL == null) {
            mProgressBar.setVisibility(View.GONE);
        } else {
            //DigiCareLogger.d("URLTest", getPhilipsProductPageUrl());
            DigiCareLogger.d(TAG, FAQ_PAGE_URL);
            String url = FAQ_PAGE_URL;
            mWebView.getSettings().setJavaScriptEnabled(true);
            mProgressBar.setVisibility(View.VISIBLE);
            mWebView.getSettings().setJavaScriptEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
                mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
                mWebView.getSettings().setDomStorageEnabled(true);
            }
            mWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress > 80) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            });
            mWebView.loadUrl(url);
        }
    }

    private void initView() {
        mWebView = (WebView) mView.findViewById(R.id.webView);
        mProgressBar = (ProgressBar) mView
                .findViewById(R.id.common_webview_progress);
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.view_faq);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void setViewParams(Configuration config) {
    }

    @Override
    public String setPreviousPageName() {
        return AnalyticsConstants.PAGE_FAQ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mWebView != null) {
            mWebView = null;
        }
    }
}
