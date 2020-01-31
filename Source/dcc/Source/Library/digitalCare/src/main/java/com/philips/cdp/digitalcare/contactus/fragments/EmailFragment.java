/**
 * EMail WebView Fragment
 *
 * @author : pawan.kumar.deshpande@philips.com
 * Since  17 july 2015.
 * Copyright (c) 2016 Philips. All rights reserved.
 */
package com.philips.cdp.digitalcare.contactus.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.analytics.AnalyticsConstants;
import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;
import com.philips.cdp.digitalcare.util.DigiCareLogger;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class EmailFragment extends DigitalCareBaseFragment {

    private View mView = null;
    private WebView mWebView = null;
    private ProgressBar mProgressBar = null;
    private String TAG = EmailFragment.class.getSimpleName();
    private String mEmailUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.consumercare_common_webview, container, false);
        }
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Map<String, String> contextData = new HashMap<String, String>();
        contextData.put(AnalyticsConstants.ACTION_KEY_SERVICE_CHANNEL,
                AnalyticsConstants.ACTION_VALUE_SERVICE_CHANNEL_EMAIL);
        contextData.put(AnalyticsConstants.PAGE_CONTACTUS_EMAIL,
                getPreviousName());
        DigitalCareConfigManager.getInstance().getTaggingInterface().trackPageWithInfo
                (AnalyticsConstants.PAGE_CONTACTUS_EMAIL,
                        contextData);
        initView();
        mEmailUrl = getEmailUrl();
        loadEmail(mEmailUrl);
    }

    private void initView() {
        mWebView = (WebView) mView.findViewById(R.id.webView);
        mWebView.getSettings().setSaveFormData(false);
        mProgressBar = (ProgressBar) mView
                .findViewById(R.id.common_webview_progress);
        mProgressBar.setVisibility(View.GONE);
    }



    public void loadEmail(String emailUrl) {
        if (emailUrl == null) {
            mProgressBar.setVisibility(View.GONE);
        } else {
            String url = emailUrl + "&origin=15_global_en_" + getAppName() + "-app_" + getAppName() + "-app";
            setWebSettingForWebview(url, mWebView, mProgressBar);
        }
    }

    private String getEmailUrl() {
        return DigitalCareConfigManager.getInstance().getEmailUrl();
    }

    @Override
    public void setViewParams(Configuration config) {

    }

    @Override
    public String getActionbarTitle() {
        String title = getResources().getString(R.string.send_us_email);
        DigiCareLogger.i(TAG, "Title : " + title);
        return title;
    }

    @Override
    public String setPreviousPageName() {
        return AnalyticsConstants.PAGE_CONTACTUS_EMAIL;
    }

    @Override
    public void onClick(View view) {

    }
    @Override
    public void onStop() {
        super.onStop();
        CookieManager.getInstance().removeSessionCookies(null);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mWebView != null) {
            mWebView = null;
        }
    }
}
