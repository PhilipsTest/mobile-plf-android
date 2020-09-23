/**
 * FaceBook Feature in the INAPP Webpage as a configurable data.
 *
 * <p/>
 * Copyright (c) 2016 Philips. All rights reserved.
 */

package com.philips.cdp.digitalcare.social.facebook;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.analytics.AnalyticsConstants;
import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;

@SuppressWarnings("serial")
public class FacebookWebFragment extends DigitalCareBaseFragment {

    private WebView mFacebookWebView = null;
    private ProgressBar mProgressBar = null;

    private String mFacebookUrl = null;

    public FacebookWebFragment() {
    }

    public FacebookWebFragment(String facebookUrlIndex) {
        this.mFacebookUrl = facebookUrlIndex;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.consumercare_common_webview, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadInAppFacebook();
        DigitalCareConfigManager.getInstance().getTaggingInterface().
                trackPageWithInfo(AnalyticsConstants.PAGE_CONTACTUS_FACEBOOK,
                        getPreviousName(), getPreviousName());
    }

    private void loadInAppFacebook() {
        setWebSettingForWebview(mFacebookUrl, mFacebookWebView, mProgressBar);

    }

    private void initView(View view) {
        mFacebookWebView = (WebView) view.findViewById(R.id.webView);
        mProgressBar = (ProgressBar) view
                .findViewById(R.id.common_webview_progress);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.contact_us);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void setViewParams(Configuration config) {
    }

    @Override
    public String setPreviousPageName() {
        return AnalyticsConstants.PAGE_CONTACTUS_FACEBOOK;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFacebookWebView != null) {
            mFacebookWebView = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initView(getView());
        loadInAppFacebook();
    }


    @Override
    public void onPause() {
        mFacebookWebView.loadUrl("about:blank");
        clearWebViewData(mFacebookWebView);
        super.onPause();
    }

    protected void clearWebViewData(WebView webView) {
        webView.stopLoading();
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearFormData();
    }
}