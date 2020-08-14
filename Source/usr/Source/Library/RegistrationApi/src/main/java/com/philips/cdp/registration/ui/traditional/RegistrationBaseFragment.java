/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.traditional;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import com.philips.cdp.registration.ProgressAlertDialog;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.errors.NotificationMessage;
import com.philips.cdp.registration.events.EventHelper;
import com.philips.cdp.registration.events.EventListener;
import com.philips.cdp.registration.myaccount.UserDetailsFragment;
import com.philips.cdp.registration.ui.customviews.URNotification;
import com.philips.cdp.registration.ui.utils.RLog;

import java.util.HashMap;
import java.util.Map;

import static com.philips.cdp.registration.ui.utils.RegConstants.NOTIFICATION;

public abstract class RegistrationBaseFragment extends Fragment implements URNotification.URNotificationInterface, EventListener {

    private Context mContext;
    private URNotification.URNotificationInterface notificationInterface;

    protected abstract void setViewParams(Configuration config, int width);

    protected abstract void handleOrientation(final View view);

    public abstract int getTitleResourceId();

    public String getTitleResourceText() {
        return null;
    }

    private int mPrevTitleResourceId = -99;

    protected static int mWidth = 0;
    protected static int mHeight = 0;

    private final int JELLY_BEAN = 16;
    private final static String TAG = "RegistrationBaseFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getNotification();
        EventHelper.getInstance().registerEventNotification(NOTIFICATION, this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public void onStop() {
        super.onStop();
        setPrevTiltle();
        EventHelper.getInstance().unregisterEventNotification(NOTIFICATION, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setCurrentTitle();
    }

    private void setPrevTiltle() {
        RegistrationFragment fragment = (RegistrationFragment) getParentFragment();

        if (null != fragment && null != fragment.getUpdateTitleListener()
                && mPrevTitleResourceId != -99) {
            if (fragment.getFragmentCount() > 1) {
                fragment.getUpdateTitleListener().updateActionBar(
                        mPrevTitleResourceId, true);
                fragment.setCurrentTitleResource(mPrevTitleResourceId);
            } else {
                fragment.getUpdateTitleListener().updateActionBar(
                        mPrevTitleResourceId, false);
                fragment.setCurrentTitleResource(mPrevTitleResourceId);
            }

            fragment.setResourceID(mPrevTitleResourceId);
        }
    }

    private void setCurrentTitle() {

        RegistrationFragment fragment = (RegistrationFragment) getParentFragment();
        if (null != fragment && null != fragment.getUpdateTitleListener()
                && -99 != fragment.getResourceID()) {
            mPrevTitleResourceId = fragment.getResourceID();
        }
        if (null != fragment) {
            if (fragment.getFragmentCount() > 1) {
                if (this instanceof HomeFragment && null != fragment.
                        getUpdateTitleListener()) {
                    fragment.getUpdateTitleListener().updateActionBar(
                            getTitleResourceId(), false);
                } else {
                    if (null != fragment.getUpdateTitleListener()) {
                        fragment.getUpdateTitleListener().updateActionBar(
                                getTitleResourceId(), true);
                        String titleText = getTitleResourceText();
                        if (titleText != null && titleText.length() > 0) {
                            fragment.getUpdateTitleListener().updateActionBar(titleText, false);
                        }
                    }
                }
            } else {
                if (null != fragment.getUpdateTitleListener()) {


                    if (this instanceof UserDetailsFragment || this instanceof MarketingAccountFragment) {
                        fragment.getUpdateTitleListener().updateActionBar(
                                getTitleResourceId(), true);
                    } else {

                        fragment.getUpdateTitleListener().updateActionBar(
                                getTitleResourceId(), false);
                    }
                    String titleText = getTitleResourceText();
                    if (titleText != null && titleText.length() > 0) {
                        fragment.getUpdateTitleListener().updateActionBar(titleText, false);
                    }
                }
            }
            fragment.setResourceID(getTitleResourceId());
            fragment.setCurrentTitleResource(getTitleResourceId());
        }
    }

    public RegistrationFragment getRegistrationFragment() {
        Fragment fragment = getParentFragment();
        if (fragment != null && (fragment instanceof RegistrationFragment)) {
            return (RegistrationFragment) fragment;
        }
        return null;
    }

    protected void consumeTouch(View view) {

        RLog.d(TAG, "consumeTouch is called");
        if (view == null)
            return;
        view.setOnTouchListener((v, event) -> true);
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    protected void trackPage(String currPage) {
        AppTagging.trackPage(currPage);
    }

    protected void trackActionStatus(String state, String key, String value) {
        AppTagging.trackAction(state, key, value);
    }

    protected void trackActionForRemarkettingOption(String state) {
        AppTagging.trackAction(state, null, null);
    }

    protected void trackActionForAcceptTermsOption(String state) {
        AppTagging.trackAction(state, null, null);
    }

    protected void trackActionForPersonalConsentOption(String state) {
        AppTagging.trackAction(state, null, null);
    }

    protected static void trackCreateAccount() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(AppTagingConstants.SPECIAL_EVENTS, AppTagingConstants.START_USER_REGISTRATION);
        AppTagging.trackMultipleActions(AppTagingConstants.SEND_DATA, map);
    }

    protected void trackMultipleActionsMap(String state, HashMap<String, String> map) {
        AppTagging.trackMultipleActions(state, map);
    }

    protected void trackMultipleActionsLogin(String providerName) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(AppTagingConstants.LOGIN_CHANNEL, providerName);
        AppTagging.trackMultipleActions(AppTagingConstants.SEND_DATA, map);
    }

    protected void handleOrientationOnView(final View view) {
        if (null == view) {
            return;
        }
        if (mWidth == 0 && mHeight == 0) {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    if (isAdded()) {
                        Configuration config = getResources().getConfiguration();
                        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
                            mWidth = view.getWidth();
                            mHeight = view.getHeight();
                        } else {
                            mWidth = view.getHeight();
                            mHeight = view.getWidth();
                        }

                        if (Build.VERSION.SDK_INT < JELLY_BEAN) {
                            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        setViewParams(getResources().getConfiguration(), view.getWidth());
                    }
                }
            });
        } else {
            if (isAdded()) {
                Configuration config = getResources().getConfiguration();
                if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setViewParams(getResources().getConfiguration(), mWidth);
                } else {
                    setViewParams(getResources().getConfiguration(), mHeight);
                }
            }

        }
    }


    public void setCustomParams(Configuration config) {
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setViewParams(config, mWidth);
        } else {
            setViewParams(config, mHeight);
        }

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    protected void scrollViewAutomatically(final View view, final ScrollView scrollView) {
        RLog.d(TAG, "scrollViewAutomatically is called");
        view.requestFocus();
        if (scrollView != null) {
            scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, view.getTop());
                        }
                    });
                    if (Build.VERSION.SDK_INT < JELLY_BEAN) {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }


    public ProgressAlertDialog mProgressDialog;

    public void showProgressDialog() {
        if (this.isVisible()) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressAlertDialog(getActivity(), R.style.reg_Custom_loaderTheme);
                mProgressDialog.setCancelable(false);
            }
            mProgressDialog.show();
        }
    }

    public void hideProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.cancel();
            }
        } catch (IllegalArgumentException e) {
            RLog.e(TAG, "hideProgressDialog: view not attached " + e.getMessage());
        }
    }


    public static void setHeightWidthToZero() {
        mWidth = 0;
        mHeight = 0;
    }

    public void updateErrorNotification(String errorMessage, int errorCode) {
        RLog.d(TAG, "errorMessage = " + errorMessage + "errorCode" + errorCode);
        RLog.i(TAG, "errorMessage = " + errorMessage + "errorCode" + errorCode);
        System.out.println("errorMessageSeen"+ errorMessage+ "errorCode" + errorCode);
        getNotification().showNotification(new NotificationMessage(errorMessage, errorCode));
    }

    public void updateErrorNotification(String errorMessage) {
        RLog.d(TAG, "errorMessage = " + errorMessage);
        getNotification().showNotification(new NotificationMessage(errorMessage));


    }


    public void showNotificationBarOnNetworkNotAvailable() {

        new Handler().postDelayed(() -> {
            getNotification().showNotification(
                    new NotificationMessage(mContext.getResources().getString(R.string.USR_Title_NoInternetConnection_Txt), mContext.getResources().getString(R.string.USR_Network_ErrorMsg)))
            ;
        }, 100);
    }

    public void hideNotificationBarView() {
            getNotification().hideNotification();
    }

    public void registerInlineNotificationListener(RegistrationBaseFragment baseFragment) {
        RLog.d(TAG, "registerInlineNotificationListener :" + baseFragment);
        notificationInterface = baseFragment;
    }

    public URNotification getNotification() {
        RLog.d(TAG, "getNotification ");
        return new URNotification(getRegistrationFragment().getParentActivity(), this);
    }

    @Override
    public void onEventReceived(String event) {
        hideNotificationBarView();
    }

}
