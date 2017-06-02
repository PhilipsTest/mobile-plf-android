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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.philips.platform.appframework.R;
import com.philips.platform.baseapp.screens.utility.RALog;

/**
 * Welcome fragment contains the screens for onboarding , as of now it supports 3 screens
 * The default content can be resplaced by verticals by changing the xml file 'parent_introduction_fragment_layout'
 */
public class WelcomePagerFragment extends Fragment {
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        RALog.d(TAG," onCreate");
        super.onCreate(savedInstanceState);
        titleId = getArguments().getInt(ARG_PAGE_TITLE, 0);
        subtitleId = getArguments().getInt(ARG_PAGE_SUBTITLE, 0);
        backgroundId = getArguments().getInt(ARG_PAGE_BG_ID, R.drawable.af_welcome_start_page_bg);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.af_welcome_slide_fragment, null);

        TextView largeText = (TextView) view.findViewById(R.id.welcome_slide_large_text);
        TextView smallText = (TextView) view.findViewById(R.id.welcome_slide_small_text);
        View background = view.findViewById(R.id.welcome_slide_fragment_layout);

        largeText.setText(titleId);
        smallText.setText(subtitleId);
        background.setBackground(ContextCompat.getDrawable(getActivity(), backgroundId));

        return view;
    }
}