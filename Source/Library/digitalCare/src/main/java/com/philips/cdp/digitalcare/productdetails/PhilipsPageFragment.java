package com.philips.cdp.digitalcare.productdetails;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.philips.cdp.digitalcare.ConsumerProductInfo;
import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.analytics.AnalyticsConstants;
import com.philips.cdp.digitalcare.analytics.AnalyticsTracker;
import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;
import com.philips.cdp.digitalcare.localematch.LocaleMatchHandler;
import com.philips.cdp.digitalcare.util.DigiCareLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * Product Specific Philips Page.
 *
 * @author : naveen@philips.com
 * @since : 9 Nov 2015
 */
public class PhilipsPageFragment extends DigitalCareBaseFragment {

    private View mView = null;
    private WebView mWebView = null;
    private ProgressBar mProgressBar = null;
    private ImageView mActionBarMenuIcon = null;
    private ImageView mActionBarArrow = null;

    private String PRODUCT_PAGE_URL = "https://%s%s";
    private String TAG = PhilipsPageFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.common_webview, container, false);
        }

        AnalyticsTracker.trackPage(AnalyticsConstants.PAGE_VIEW_PRODUCT_WEBSITE,
                getPreviousName());

        return mView;
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
        if (getPhilipsProductPageUrl() == null) {
            mProgressBar.setVisibility(View.GONE);
        } else {
            //DigiCareLogger.d("URLTest", getPhilipsProductPageUrl());
            DigiCareLogger.d(TAG, getPhilipsProductPageUrl());
            String url = getPhilipsProductPageUrl();
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

    private String getPhilipsProductPageUrl() {
        if (DigitalCareConfigManager.getInstance().getLocaleMatchResponseWithCountryFallBack() == null)
            return null;
        String language = DigitalCareConfigManager.getInstance().getLocaleMatchResponseWithCountryFallBack()
                .getLanguage().toLowerCase();

        String country = DigitalCareConfigManager.getInstance().getLocaleMatchResponseWithCountryFallBack()
                .getCountry().toUpperCase();

        return String.format(PRODUCT_PAGE_URL, LocaleMatchHandler.getPRXUrl(language + "_" + country), DigitalCareConfigManager.getInstance().getViewProductDetailsData().getProductInfoLink());
    }

    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.product_info);
    }

    @Override
    public void onClick(View v) {
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
