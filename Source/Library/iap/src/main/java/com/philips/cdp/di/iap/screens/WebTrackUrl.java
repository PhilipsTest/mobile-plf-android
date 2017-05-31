/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.screens;

import android.os.Bundle;

import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.IAPConstant;

public class WebTrackUrl extends WebFragment {
    public static final String TAG = WebTrackUrl.class.getName();

    @Override
    protected String getWebUrl() {
        Bundle bundle = getArguments();
        if(getArguments() == null){
            throw new RuntimeException("URL must be provided");
        }
        return bundle.getString(IAPConstant.ORDER_TRACK_URL);
    }

    @Override
    public void onResume() {
        super.onResume();
        IAPAnalytics.trackPage(IAPAnalyticsConstant.ORDER_DETAIL_PAGE_NAME);
    }

    public static WebTrackUrl createInstance(Bundle args, AnimationType animType) {
        WebTrackUrl fragment = new WebTrackUrl();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }
}
