/**
 * DigitalCareBaseFragment is <b>Base class</b> for all fragments.
 *
 * @author: ritesh.jha@philips.com
 * @since: Dec 5, 2014
 * <p/>
 * Copyright (c) 2016 Philips. All rights reserved.
 */

package com.philips.cdp.digitalcare.homefragment;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.analytics.AnalyticsConstants;
import com.philips.cdp.digitalcare.customview.NetworkAlertView;
import com.philips.cdp.digitalcare.listeners.ActivityTitleListener;
import com.philips.cdp.digitalcare.listeners.NetworkStateListener;
import com.philips.cdp.digitalcare.util.DigiCareLogger;
import com.philips.cdp.digitalcare.util.DigitalCareConstants;
import com.philips.cdp.digitalcare.util.NetworkReceiver;
//import com.philips.cdp.productselection.launchertype.FragmentLauncher;
//import com.philips.cdp.productselection.listeners.ActionbarUpdateListener;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;
import com.philips.platform.uappframework.listener.BackEventListener;

import java.util.Locale;


public abstract class DigitalCareBaseFragment extends Fragment implements
        OnClickListener, NetworkStateListener, BackEventListener {

    protected static SummaryModel mViewProductSummaryModel = null;
    protected static FragmentLauncher mFragmentLauncher = null;
    public static boolean isInternetAvailable;
    private static String TAG = DigitalCareBaseFragment.class.getSimpleName();
    private static int mContainerId = 0;
    private static ActionBarListener mActionbarUpdateListener = null;
    private static String mPreviousPageName = null;
    private static int mEnterAnimation = 0;
    private static int mExitAnimation = 0;
    private FragmentActivity mFragmentActivityContext = null;
    private FragmentActivity mActivityContext = null;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    protected int mLeftRightMarginPort = 0;
    protected int mLeftRightMarginLand = 0;
    private NetworkReceiver mNetworkutility = null;
    private Thread mUiThread = Looper.getMainLooper().getThread();
    private ImageView mBackToHome = null;
    private ImageView mHomeIcon = null;
    private ActivityTitleListener activityTitleListener;

    public synchronized static void setStatus(boolean connection) {
        isInternetAvailable = connection;
    }

    protected void setWebSettingForWebview(String url, WebView webView, final ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
            webView.getSettings().setDomStorageEnabled(true);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress > 80) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public Bitmap getDefaultVideoPoster() {
                if (Build.VERSION.SDK_INT >= 26) {
                    return BitmapFactory.decodeResource(getResources(), R.drawable.ic_media_video_poster);
                }
                else{
                    return super.getDefaultVideoPoster();
                }
            }

        });
        webView.loadUrl(url);
    }

    public abstract void setViewParams(Configuration config);

    public abstract String getActionbarTitle();

    public abstract String setPreviousPageName();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(activityTitleListener instanceof ActivityTitleListener){
            activityTitleListener=(ActivityTitleListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
       /* DigiCareLogger.i(DigiCareLogger.FRAGMENT, "OnCreate on "
                + this.getClass().getSimpleName());*/
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        mFragmentActivityContext = getActivity();
        registerNetWorkReceiver();
        //setLocaleLanguage();
    }

    private void registerNetWorkReceiver() {
        IntentFilter mfilter = new IntentFilter(
                "android.net.conn.CONNECTIVITY_CHANGE");
        mNetworkutility = new NetworkReceiver(this);
        getActivity().registerReceiver(mNetworkutility, mfilter);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLeftRightMarginPort = (int) getActivity().getResources()
                .getDimension(R.dimen.activity_margin_port);
        mLeftRightMarginLand = (int) getActivity().getResources()
                .getDimension(R.dimen.activity_margin_land);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (getActivity().isInMultiWindowMode()){
                mLeftRightMarginLand = (int) getActivity().getResources()
                        .getDimension(R.dimen.activity_margin_port);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionbarTitle();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mNetworkutility);
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPreviousPageName = setPreviousPageName();
        hideKeyboard();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected boolean isConnectionAvailable() {
        if (isInternetAvailable)
            return true;
        else {
            return isConnectionAlertDisplayed();
        }
    }

    protected boolean isConnectionAlertDisplayed() {
        mHandler.postAtFrontOfQueue(new Runnable() {

            @Override
            public void run() {
                new NetworkAlertView().showAlertBox(
                        DigitalCareBaseFragment.this,
                        null,
                        getActivity().getResources().getString(
                                R.string.no_internet),
                        getActivity().getResources().getString(
                                android.R.string.yes));
                DigitalCareConfigManager.getInstance().getTaggingInterface().trackActionWithInfo
                        (AnalyticsConstants.ACTION_SET_ERROR,
                                AnalyticsConstants.ACTION_KEY_TECHNICAL_ERROR,
                                AnalyticsConstants.ACTION_VALUE_TECHNICAL_ERROR_NETWORK_CONNECITON);

            }
        });
        return false;
    }

    protected void enableActionBarLeftArrow(ImageView hambergermenu, ImageView backarrow) {
        DigiCareLogger.d(TAG, "BackArrow icon Enabled");
        if (hambergermenu != null && backarrow != null) {
            backarrow.setVisibility(View.VISIBLE);
            backarrow.bringToFront();
        }
    }

    protected void enableActionBarHamburgerIcon(ImageView hambergermenu, ImageView backarrow) {
        DigiCareLogger.d(TAG, "Hamburger icon Enabled");
        if (hambergermenu != null && backarrow != null) {
            hambergermenu.setVisibility(View.VISIBLE);
            hambergermenu.bringToFront();
        }
    }

    protected void hideActionBarIcons(ImageView hambergermenu, ImageView backarrow) {
        DigiCareLogger.d(TAG, "Hide menu & arrow icons");
        if (hambergermenu != null && backarrow != null) {
            hambergermenu.setVisibility(View.GONE);
            backarrow.setVisibility(View.GONE);
        }
    }

    protected void showAlert(final String message) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                new NetworkAlertView().showAlertBox(
                        DigitalCareBaseFragment.this,
                        null,
                        message,
                        getActivity().getResources().getString(
                                android.R.string.ok));
                DigitalCareConfigManager.getInstance().getTaggingInterface().trackActionWithInfo
                        (AnalyticsConstants.ACTION_SET_ERROR,
                                AnalyticsConstants.ACTION_KEY_TECHNICAL_ERROR,
                                AnalyticsConstants.ACTION_VALUE_TECHNICAL_ERROR_NETWORK_CONNECITON);

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getAppName();
    }

	/*
     * This method can be called directly from outside and helps to invoke the
	 * fragments, instead of full screen activity. DigitalCare fragments will be
	 * added in the root container of hosting app. Integrating app has to pass
	 * some parameters in order to do smooth operations.
	 */

    private void enableActionBarLeftArrow() {
        mBackToHome = (ImageView) mFragmentActivityContext
                .findViewById(R.id.back_to_home_img);
        mHomeIcon = (ImageView) mFragmentActivityContext
                .findViewById(R.id.home_icon);
        if (mHomeIcon != null) mHomeIcon.setVisibility(View.GONE);
        if (mBackToHome != null) {
            mBackToHome.setVisibility(View.VISIBLE);
            mBackToHome.bringToFront();
        }
    }

    /*
    This method will provide vertical APP NAME which is required for TAGGING (Analytics).
     */
    @SuppressWarnings("static-access")
    protected String getAppName() {
        String appName = "";

        if(isAdded()) {
            appName = getActivity().getResources().getString(R.string.app_name);

            try {
                int metaData = PackageManager.GET_META_DATA;
                ApplicationInfo appInfo = getActivity().getPackageManager().getApplicationInfo
                        (getActivity().getPackageName(),
                                metaData);
                appName = appInfo.loadLabel(getActivity().getPackageManager()).toString();
            } catch (PackageManager.NameNotFoundException e) {
                DigiCareLogger.e(TAG, "NameNotFoundException" + e.getMessage());
            }
        }
        return appName;
    }

    protected void showFragment(Fragment fragment) {
        int containerId = R.id.mainContainer;

        if (mContainerId != 0) {
            containerId = mContainerId;
            mFragmentActivityContext = mFragmentLauncher.getFragmentActivity();
        } else {
            enableActionBarLeftArrow();
            InputMethodManager imm = (InputMethodManager) mFragmentActivityContext
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (mFragmentActivityContext.getWindow() != null
                    && mFragmentActivityContext.getWindow().getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(mFragmentActivityContext
                        .getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        try {
            FragmentTransaction fragmentTransaction = mFragmentActivityContext
                    .getSupportFragmentManager().beginTransaction();
            if (mEnterAnimation != 0 && mExitAnimation != 0) {
                fragmentTransaction.setCustomAnimations(mEnterAnimation,
                        mExitAnimation, mEnterAnimation, mExitAnimation);
            }
            fragmentTransaction.replace(containerId, fragment, DigitalCareConstants.DIGITALCARE_FRAGMENT_TAG);
            fragmentTransaction.hide(this);
            fragmentTransaction.addToBackStack(fragment.getTag());
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {
            DigiCareLogger.e(TAG, "IllegalStateException" + e.getMessage());
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        DigiCareLogger.d(DigiCareLogger.FRAGMENT, "onHiddenChanged : " + hidden
                + " ---class " + this.getClass().getSimpleName());
        if (mContainerId == 0) {
            if (this.getClass().getSimpleName()
                    .equalsIgnoreCase(SupportHomeFragment.class.getSimpleName())) {
                enableActionBarHome();
            }
        }
    }

    private void enableActionBarHome() {

        if (mBackToHome != null) mBackToHome.setVisibility(View.GONE);
        if (mHomeIcon != null) {
            mHomeIcon.setVisibility(View.VISIBLE);
            mHomeIcon.bringToFront();
        }
    }

    public void showFragment(/*FragmentActivity context, int parentContainer,*/
                             Fragment fragment, FragmentLauncher fragmentLauncher,/*ActionbarUpdateListener actionbarUpdateListener,*/
                             int startAnimation, int endAnimation) {
        mFragmentLauncher = fragmentLauncher;
        mContainerId = fragmentLauncher.getParentContainerResourceID();
        mActivityContext = fragmentLauncher.getFragmentActivity();
        mActionbarUpdateListener = fragmentLauncher.getActionbarListener();

        String startAnim = null;
        String endAnim = null;

        if ((startAnimation != 0) && (endAnimation != 0)) {
            startAnim = mActivityContext.getResources().getResourceName(startAnimation);
            endAnim = mActivityContext.getResources().getResourceName(endAnimation);

            String packageName = mActivityContext.getPackageName();
            mEnterAnimation = mActivityContext.getResources().getIdentifier(startAnim,
                    "anim", packageName);
            mExitAnimation = mActivityContext.getResources().getIdentifier(endAnim, "anim",
                    packageName);
        }

        try {
            FragmentTransaction fragmentTransaction = mActivityContext
                    .getSupportFragmentManager().beginTransaction();
            if (mEnterAnimation != 0 && mExitAnimation != 0) {
                fragmentTransaction.setCustomAnimations(mEnterAnimation,
                        mExitAnimation, mEnterAnimation, mExitAnimation);
            }
            fragmentTransaction.replace(mContainerId, fragment, DigitalCareConstants.DIGITALCARE_FRAGMENT_TAG);
            fragmentTransaction.addToBackStack(fragment.getTag());
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {
            DigiCareLogger.e(TAG, e.getMessage());
        }
    }

    protected void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onNetworkStateChanged(boolean connection) {
        setStatus(connection);
    }

    protected final void updateUI(Runnable runnable) {
        if (Thread.currentThread() != mUiThread) {
            mHandler.post(runnable);
        } else {
            runnable.run();
        }
    }

    /**
     * Updating action bar title. The text has to be updated at each fragment
     * seletion/creation.
     */
    private void setActionbarTitle() {

        if(getActionbarTitle() == null || !isAdded()) {
            return;
        }

        if (mContainerId == 0) {
            TextView actionBarTitle =

                    ((TextView) getActivity().findViewById(
                            R.id.uid_toolbar_title));
            actionBarTitle.setText(getActionbarTitle());
        } else {
            updateActionbar();
        }
    }

    private void updateActionbar() {
        if (this.getClass().getSimpleName()
                .equalsIgnoreCase(SupportHomeFragment.class.getSimpleName())) {
            mActionbarUpdateListener.updateActionBar(getActionbarTitle(), false);
        } else {
            mActionbarUpdateListener.updateActionBar(getActionbarTitle(), true);
        }
    }

    protected String getPreviousName() {
        return mPreviousPageName;
    }

    @Override
    public boolean handleBackEvent() {
        return false;
    }
}
