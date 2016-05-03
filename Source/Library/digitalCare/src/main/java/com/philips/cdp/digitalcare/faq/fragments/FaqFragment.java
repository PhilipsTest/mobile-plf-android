/**
 * FaqFragment Questions  Listing Screen (First Screen) as per the US/UI requirement.
 *
 * @author : naveen@philips.com
 * @since : 25 june 2015
 *
 * Copyright (c) 2016 Philips. All rights reserved.
 */
package com.philips.cdp.digitalcare.faq.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.analytics.AnalyticsConstants;
import com.philips.cdp.digitalcare.analytics.AnalyticsTracker;
import com.philips.cdp.digitalcare.faq.listeners.FaqCallback;
import com.philips.cdp.digitalcare.faq.view.FAQCustomView;
import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;
import com.philips.cdp.prxclient.datamodels.support.SupportModel;

import java.util.HashMap;
import java.util.Map;


public class FaqFragment extends DigitalCareBaseFragment implements FaqCallback {

    private final int EXPAND_FIRST = 2;
    private final int COLLAPSE_ALL = 0;
    private View mView = null;
    private WebView mWebView = null;
    private ProgressBar mProgressBar = null;
    private ImageView mActionBarMenuIcon = null;
    private ImageView mActionBarArrow = null;
    private SupportModel mSupportModel = null;
    private String TAG = FaqFragment.class.getSimpleName();
    private FAQCustomView faqCustomView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {
        if (faqCustomView == null) {
            faqCustomView = new FAQCustomView(getActivity(), mSupportModel, this);
            faqCustomView.setDeviceType(isTablet());
            mView = faqCustomView.init();
            faqCustomView.updateView(null, COLLAPSE_ALL);
        }
        return mView;
    }

    public void setSupportModel(SupportModel supportModel) {
        this.mSupportModel = supportModel;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActionBarMenuIcon = (ImageView) getActivity().findViewById(R.id.home_icon);
        mActionBarArrow = (ImageView) getActivity().findViewById(R.id.back_to_home_img);
        hideActionBarIcons(mActionBarMenuIcon, mActionBarArrow);
        AnalyticsTracker.trackPage(AnalyticsConstants.PAGE_FAQ, getPreviousName());
    }


    @Override
    public void onResume() {
        super.onResume();
        enableActionBarLeftArrow(mActionBarMenuIcon, mActionBarArrow);
    }

  /*  private String getFaqUrl() {
        if (DigitalCareConfigManager.getInstance().getLocaleMatchResponseWithCountryFallBack() == null)
            return null;
        String language = DigitalCareConfigManager.getInstance().getLocaleMatchResponseWithCountryFallBack()
                .getLanguage().toLowerCase();

        String country = DigitalCareConfigManager.getInstance().getLocaleMatchResponseWithCountryFallBack()
                .getCountry().toUpperCase();

        ConsumerProductInfo consumerProductInfo = DigitalCareConfigManager
                .getInstance().getConsumerProductInfo();

        return String.format(FAQ_URL, LocaleMatchHandler.getPRXUrl(language + "_" + country), consumerProductInfo.getSector(),
                language, country, consumerProductInfo.getCtn());
    }*/

    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.FAQ_KEY);
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
    }

    @Override
    public void onFaqQuestionClicked(String webUrl) {
        FaqDetailedScreen faqDetailedScreen = new FaqDetailedScreen();
        faqDetailedScreen.setFaqWebUrl(webUrl);
        showFragment(faqDetailedScreen);
    }

    private boolean isTablet(){
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float yInches= metrics.heightPixels/metrics.ydpi;
        float xInches= metrics.widthPixels/metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches);

        if (diagonalInches>=6.5){
            // 6.5inch device or bigger
            return true;
        }
        return false;
    }
}
