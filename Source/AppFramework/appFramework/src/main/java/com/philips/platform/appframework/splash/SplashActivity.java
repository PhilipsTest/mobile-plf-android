/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/


package com.philips.platform.appframework.splash;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.philips.platform.appframework.AppFrameworkApplication;
import com.philips.platform.appframework.AppFrameworkBaseActivity;
import com.philips.platform.appframework.R;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.modularui.statecontroller.UIView;

/**
 * <H1>Dev Guide</H1>
 * <p>
 * UIKit provides 3 basic templates that can be filled via target activity.<br>
 * By default themed gradient background is applied.<br>
 * SplashActivity is class which will appear at the very start when user opens the app.
 */
public class SplashActivity extends AppFrameworkBaseActivity implements UIView {
    private static int SPLASH_TIME_OUT = 3000;
    private static String TAG = SplashActivity.class.getSimpleName();
    private boolean isVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        presenter = new SplashPresenter(this);
        initView();
        AppFrameworkApplication.loggingInterface.log(LoggingInterface.LogLevel.INFO, TAG, " Splash Activity Created ");

    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    /*
    * Initialize the views.
    */
    private void initView() {
        //Hide the Action bar
        getSupportActionBar().hide();
        setContentView(R.layout.uikit_splash_screen_logo_center_tb);

        ViewGroup group = (ViewGroup) findViewById(R.id.splash_layout);
        ImageView logo = (ImageView) findViewById(R.id.splash_logo);
        logo.setImageDrawable(VectorDrawableCompat.create(getResources(),R.drawable.uikit_philips_logo, getTheme()) );


        String splashScreenTitle = getResources().getString(R.string.splash_screen_title);
        CharSequence titleText = Html.fromHtml(splashScreenTitle);

        TextView title = (TextView) findViewById(R.id.splash_title);
        title.setText(titleText);
    }

    /*
     * Splash screen has to go off after specified timeframe.
     */
    private void startTimer() {
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                if(isVisible) {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    presenter.onLoad(SplashActivity.this);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void updateActionBarIcon(boolean b) {

    }

    @Override
    public void updateActionBar(@StringRes int i, boolean b) {

    }

    @Override
    public void updateActionBar(String s, boolean b) {

    }

    @Override
    public FragmentActivity getFragmentActivity() {
        return this;
    }
}
