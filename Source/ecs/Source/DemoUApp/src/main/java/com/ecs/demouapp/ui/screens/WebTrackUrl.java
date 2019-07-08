/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.screens;

import android.os.Bundle;

import com.ecs.demouapp.ui.session.NetworkConstants;
import com.ecs.demouapp.ui.utils.IAPConstant;


public class WebTrackUrl extends WebFragment {
    public static final String TAG = WebTrackUrl.class.getName();

    @Override
    protected boolean isJavaScriptEnable() {
        return true;
    }

    @Override
    protected String getWebUrl() {
        Bundle bundle = getArguments();
        return bundle.getString(IAPConstant.ORDER_TRACK_URL);
    }

    public static WebTrackUrl createInstance(Bundle args, AnimationType animType) {
        WebTrackUrl fragment = new WebTrackUrl();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

}
