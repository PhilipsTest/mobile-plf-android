/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.screens;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPUtility;

import java.net.MalformedURLException;
import java.net.URL;


public class WebPrivacy extends WebFragment {
    public static final String TAG = WebPrivacy.class.getName();
    private static final long serialVersionUID = -3779144707231623502L;
    private String mUrl, termsUrl, faqUrl;

    @Override
    protected boolean isJavaScriptEnable() {
        return true;
    }

    @Override
    protected String getWebUrl() {
        termsUrl = getArguments().getString(IAPConstant.IAP_TERMS);
        faqUrl = getArguments().getString(IAPConstant.IAP_FAQ);
        if(!TextUtils.isEmpty(termsUrl)){
            mUrl = getArguments().getString(IAPConstant.IAP_TERMS_URL);
        } else if(!TextUtils.isEmpty(faqUrl)){
            mUrl = getArguments().getString(IAPConstant.IAP_FAQ_URL);
        } else {
            mUrl = getArguments().getString(IAPConstant.IAP_PRIVACY_URL);
        }
        return mUrl;
    }

    public static WebPrivacy createInstance(Bundle args, AnimationType animType) {
        WebPrivacy fragment = new WebPrivacy();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(termsUrl)){
            setTitleAndBackButtonVisibility(R.string.iap_terms_conditions,true);
        } else if(!TextUtils.isEmpty(faqUrl)){
            setTitleAndBackButtonVisibility(R.string.iap_faq,true);
        } else {
            setTitleAndBackButtonVisibility(R.string.iap_privacy,true);
        }
    }

    private void tagExitLink(String urlClicked){

          String UrlWithTrackingCode =  getPhilipsFormattedUrl(urlClicked);

        IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,IAPAnalyticsConstant.KEY_EXIT_LINK_RETAILER,UrlWithTrackingCode);

    }

    public String getPhilipsFormattedUrl(String url) {

        String appName = IAPUtility.getInstance().getAppName();
        String localeTag = IAPUtility.getInstance().getLocaleTag();
        Uri.Builder builder = new Uri.Builder().appendQueryParameter("origin", String.format(IAPAnalyticsConstant.PHILIPS_EXIT_LINK_PARAMETER,localeTag,appName,appName));

        if(isParameterizedURL(url)){
            return  url + "&" + builder.toString().replace("?","");
        }else{
            return url + builder.toString();
        }
    }

    private boolean isParameterizedURL(String url) {

        try {
            URL urlString  = new URL(url);
            return urlString.getQuery() != null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
