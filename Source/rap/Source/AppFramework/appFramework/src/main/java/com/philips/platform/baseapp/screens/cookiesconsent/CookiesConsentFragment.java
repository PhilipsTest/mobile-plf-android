/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/

package com.philips.platform.baseapp.screens.cookiesconsent;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.platform.appframework.R;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.baseapp.base.AbstractOnboardingBaseFragment;
import com.philips.platform.baseapp.base.AbstractUIBasePresenter;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.base.AppFrameworkTagging;
import com.philips.platform.baseapp.screens.introscreen.pager.WelcomePagerAdapter;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.uappframework.listener.BackEventListener;
import com.philips.platform.uid.view.widget.Button;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.ProgressBarButton;

/**
 * <b></b>Introduction screen are the screen that acts as the Welcome screens. It may be used to make the user learn about the functionality of the app</b>
 * <br>
 * <p/>
 * <b>To use the Introduction screen flow, start the mActivity with IntroudctionScreenActivity as the Intent</b><br>
 * <pre>&lt;To make the start , skip ,left and right button visibility in each screen, please use the onPageSelected
 */
public class CookiesConsentFragment extends AbstractOnboardingBaseFragment implements View.OnClickListener,
        CookiesConsentFragmentView, BackEventListener, View.OnLongClickListener {

    public static String TAG = CookiesConsentFragment.class.getSimpleName();

    private AbstractUIBasePresenter presenter;
    private WelcomePagerAdapter welcomePagerAdapter ;
    public void onBackPressed() {
        RALog.d(TAG, " On Back Pressed");

    }

    protected AbstractUIBasePresenter getWelcomePresenter() {
        return new CookiesConsentPresenter(this);
    }

   Label usr_cookiesConsentScreen_info_weblink_label;
   ProgressBarButton usr_cookiesConsentScreen_accept_button;
      Button      usr_cookiesConsentScreen_Reject_button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        presenter = getWelcomePresenter();
        startLogging();
        View view = inflater.inflate(R.layout.rap_fragment_cookies_consent, container, false);
        welcomePagerAdapter = new WelcomePagerAdapter(getActivity().getSupportFragmentManager());
        usr_cookiesConsentScreen_info_weblink_label = view.findViewById(R.id.usr_cookiesConsentScreen_info_weblink_label);
        usr_cookiesConsentScreen_info_weblink_label.setMovementMethod(LinkMovementMethod.getInstance());


        usr_cookiesConsentScreen_accept_button = view.findViewById(R.id.usr_cookiesConsentScreen_accept_button);

         usr_cookiesConsentScreen_Reject_button = view.findViewById(R.id.usr_cookiesConsentScreen_Reject_button);
        usr_cookiesConsentScreen_Reject_button.setOnClickListener(this);
        usr_cookiesConsentScreen_accept_button.setOnClickListener(this);
        usr_cookiesConsentScreen_info_weblink_label.setOnClickListener(this);

        startAppTagging();
        return view;
    }
    private ClickableSpan mPhilipsNewsClick = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            RALog.d(TAG, " mPhilipsNewsClick ");

        }
    };
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected void startAppTagging() {
        AppFrameworkTagging.getInstance().trackPage(TAG);
    }

    protected void startLogging() {
        RALog.d(TAG, " start Logging ");
        ((AppFrameworkApplication) getFragmentActivity().getApplicationContext()).getLoggingInterface().log(LoggingInterface.LogLevel.INFO, TAG,
                " IntroductionScreen Activity Created ");
    }

    @Override
    public void onClick(View v) {
        if (presenter != null) {
            presenter.onEvent(v.getId());
        }
    }

    @Override
    public boolean handleBackEvent() {
        onBackPressed();
        return true;
    }

    @Override
    public void clearAdapter() {

    }

    @Override
    public boolean onLongClick(View view) {return false;}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}