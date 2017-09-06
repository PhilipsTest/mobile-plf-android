/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/

package com.philips.platform.baseapp.screens.introscreen.pager;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.philips.platform.appframework.R;
import com.philips.platform.baseapp.base.AbstractAppFrameworkBaseFragment;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.uid.view.widget.Label;

/**
 * Welcome fragment contains the screens for onboarding , as of now it supports 3 screens
 * The default content can be resplaced by verticals by changing the xml file 'parent_introduction_fragment_layout'
 */
public class WelcomePagerFragment extends AbstractAppFrameworkBaseFragment {
    public static final String TAG =  WelcomePagerFragment.class.getSimpleName();

    private static final String ARG_PAGE_TITLE = "pageTitle";
    private static final String ARG_PAGE_SUBTITLE = "pageSubtitle";
    private static final String ARG_PAGE_BG_ID = "pageBgId";

    // Store instance variables
    @StringRes private int titleId;
    @StringRes private int subtitleId;
    @DrawableRes private int backgroundId;

    public static WelcomePagerFragment newInstance(@StringRes int title, @StringRes int subtitle,
                                                   @DrawableRes int background) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_TITLE, title);
        args.putInt(ARG_PAGE_SUBTITLE, subtitle);
        args.putInt(ARG_PAGE_BG_ID, background);

        WelcomePagerFragment fragmentFirst = new WelcomePagerFragment();
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public String getActionbarTitle() {
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        RALog.d(TAG," onCreate");
        super.onCreate(savedInstanceState);
        titleId = getArguments().getInt(ARG_PAGE_TITLE, 0);
        subtitleId = getArguments().getInt(ARG_PAGE_SUBTITLE, 0);
        backgroundId = getArguments().getInt(ARG_PAGE_BG_ID, R.drawable.onboarding_screen_2);
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.af_welcome_slide_fragment, null);

        Label largeText = (Label) view.findViewById(R.id.welcome_slide_large_text);
        Label smallText = (Label) view.findViewById(R.id.welcome_slide_small_text);
        View background = view.findViewById(R.id.welcome_slide_fragment_layout);

        largeText.setText(titleId);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            smallText.setText(Html.fromHtml(getString(subtitleId), Html.FROM_HTML_MODE_LEGACY));
        } else {
            smallText.setText(Html.fromHtml(getString(subtitleId)));
        }
        background.setBackground(ContextCompat.getDrawable(getActivity(), backgroundId));

        return view;
    }
}