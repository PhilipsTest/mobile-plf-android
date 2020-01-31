/**
 * Product Specific Philips Page.
 *
 * @author : naveen@philips.com
 * @since : 9 Nov 2015
 *
 * Copyright (c) 2016 Philips. All rights reserved.
 */

package com.philips.cdp.digitalcare.productdetails;

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


@SuppressWarnings("serial")
public class ProductInformationFragment extends DigitalCareBaseFragment {

    private WebView mWebView = null;
    private ProgressBar mProgressBar = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.consumercare_common_webview, container, false);
        DigitalCareConfigManager.getInstance().getTaggingInterface().trackPageWithInfo
                (AnalyticsConstants.PAGE_VIEW_PRODUCT_WEBSITE,
                        getPreviousName(), getPreviousName());
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadFaq();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void loadFaq() {
        if (getPhilipsProductPageUrl() == null) {
            mProgressBar.setVisibility(View.GONE);
        } else {
            String url = getPhilipsProductPageUrl();
            setWebSettingForWebview(url, mWebView, mProgressBar);
        }
    }

    private void initView(View view) {
        mWebView = (WebView) view.findViewById(R.id.webView);
        mWebView.getSettings().setSaveFormData(false);
        mProgressBar = (ProgressBar) view
                .findViewById(R.id.common_webview_progress);
        mProgressBar.setVisibility(View.GONE);
    }

    private String getPhilipsProductPageUrl() {
        String domainUrl = null;
        if(DigitalCareConfigManager.getInstance().getViewProductDetailsData().getDomain() == null)
            domainUrl = "www.philips.com";
        else
            domainUrl = DigitalCareConfigManager.getInstance().getViewProductDetailsData().getDomain();

        return domainUrl + DigitalCareConfigManager.getInstance().getViewProductDetailsData().getProductInfoLink();
    }

    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.product_info);
    }

    @Override
    public void onClick(View v) {
    }
    @Override
    public void onStop() {
        super.onStop();
        CookieManager.getInstance().removeSessionCookies(null);
    }

    @Override
    public void setViewParams(Configuration config) {
    }

    @Override
    public String setPreviousPageName() {
        return AnalyticsConstants.PAGE_VIEW_PRODUCT_WEBSITE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mWebView != null) {
            mWebView = null;
        }
    }
}
