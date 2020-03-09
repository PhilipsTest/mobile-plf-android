/*
 * DigitalCareBaseFragment is <b>Base class</b> for all fragments.
 *
 * @author: ritesh.jha@philips.com
 * @since: Dec 5, 2014
 * <p/>
 * Copyright (c) 2016 Philips. All rights reserved.
 */

package com.philips.cdp.digitalcare.homefragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
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
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uappframework.listener.BackEventListener;

@SuppressWarnings("serial")
public abstract class DigitalCareBaseFragment extends Fragment implements
        OnClickListener, NetworkStateListener, BackEventListener {

    protected static SummaryModel mViewProductSummaryModel = null;
    protected static FragmentLauncher mFragmentLauncher = null;
    public static boolean isInternetAvailable;
    private static String TAG = DigitalCareBaseFragment.class.getSimpleName();
    protected static int mContainerId = 0;
    protected static ActionBarListener mActionbarUpdateListener = null;
    private static String mPreviousPageName = null;
    private static int mEnterAnimation = 0;
    private static int mExitAnimation = 0;
    private FragmentActivity mFragmentActivityContext = null;
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
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);


        webView.setWebViewClient(new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request.getUrl().toString().startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(request.getUrl().toString()));
                    startActivity(intent);
                    return true;
                }
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
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
            Bitmap videoPoster = BitmapFactory.decodeResource(getResources(), R.drawable.ic_media_video_poster);

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress > 80) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Nullable
            @Override
            public Bitmap getDefaultVideoPoster() {
                if (super.getDefaultVideoPoster() == null) {
                    try {
                        return BitmapFactory.decodeResource(getCustomResources(), R.drawable.ic_media_video_poster);
                    } catch (Exception e) {
                        return super.getDefaultVideoPoster();
                    }
                } else {
                    return super.getDefaultVideoPoster();
                }
            }

        });
        webView.loadUrl(url);
    }

    private Resources getCustomResources() {
        return getActivity().getResources();
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
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        mFragmentActivityContext = getActivity();
        registerNetWorkReceiver();
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
        return isInternetAvailable || isConnectionAlertDisplayed();
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
        FragmentActivity mActivityContext = fragmentLauncher.getFragmentActivity();
        mActionbarUpdateListener = fragmentLauncher.getActionbarListener();

        String startAnim;
        String endAnim;

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

                    (getActivity().findViewById(
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