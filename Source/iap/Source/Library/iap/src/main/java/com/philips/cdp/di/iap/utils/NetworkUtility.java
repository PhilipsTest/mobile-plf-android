/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.screens.ErrorDialogFragment;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.view.widget.AlertDialogFragment;

import java.util.List;

public class NetworkUtility {
    private static NetworkUtility mNetworkUtility;
    private ErrorDialogFragment mModalAlertDemoFragment;
    private AlertDialogFragment alertDialogFragment;
    public static final String ALERT_DIALOG_TAG = "ALERT_DIALOG_TAG";

    public static NetworkUtility getInstance() {
        synchronized (NetworkUtility.class) {
            if (mNetworkUtility == null) {
                mNetworkUtility = new NetworkUtility();
            }
        }
        return mNetworkUtility;
    }

    public void dismissErrorDialog() {
        if (null != mModalAlertDemoFragment && mModalAlertDemoFragment.isAdded()) {
            mModalAlertDemoFragment.dismissAllowingStateLoss();
            mModalAlertDemoFragment = null;
        }
    }

    public void showErrorDialog(Context context, FragmentManager pFragmentManager,
                                String pButtonText, String pErrorString, String pErrorDescription) {

        //Track pop up
//        IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
//                IAPAnalyticsConstant.IN_APP_NOTIFICATION_POP_UP, pErrorDescription);
        IAPAnalytics.trackInformationalErrorWithPrefix(pErrorDescription);
        if (!((Activity) context).isFinishing()) {
            showDLSDialog(UIDHelper.getPopupThemedContext(context), pButtonText, pErrorString, pErrorDescription, pFragmentManager);
        }
    }

    private void showErrorDialogPayment(Context context, FragmentManager pFragmentManager,
                                        String pButtonText, String pErrorString, String pErrorDescription, final AlertListener alertListener) {

        //Track pop up
//        IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
//                IAPAnalyticsConstant.IN_APP_NOTIFICATION_POP_UP, pErrorDescription);
        IAPAnalytics.trackInformationalErrorWithPrefix(pErrorDescription);
        if (!((Activity) context).isFinishing()) {
            showDLSDialogForPayment(UIDHelper.getPopupThemedContext(context), pButtonText, pErrorString, pErrorDescription, pFragmentManager, alertListener);
        }
    }

    public void showErrorMessage(final Message msg, FragmentManager pFragmentManager, Context context) {
        if (context == null) return;
        if (msg.obj instanceof IAPNetworkError) {
            IAPNetworkError error = (IAPNetworkError) msg.obj;
            showErrorDialog(context, pFragmentManager, context.getString(R.string.iap_ok),
                    getErrorTitleMessageFromErrorCode(context, error.getIAPErrorCode()),
                    getErrorDescriptionMessageFromErrorCode(context, error));
        }
    }

    public void showDialogMessage(String title, String description, FragmentManager pFragmentManager, Context context, final AlertListener alertListener) {
        if (context == null) return;
        showErrorDialogPayment(context, pFragmentManager, context.getString(R.string.iap_ok),
                title,
                description, alertListener);
    }

    public void showVoucherSuccessMessage(final Message msg, FragmentManager pFragmentManager, Context context) {
        if (context == null) return;

        showErrorDialog(context, pFragmentManager, context.getString(R.string.iap_ok),
                "Successful",
                "Vouchers Applied Successfully");
    }

    public void showVoucherErrorMessage(final Message msg, FragmentManager pFragmentManager, Context context) {
        if (context == null) return;
        if (msg.obj instanceof IAPNetworkError) {
            IAPNetworkError error = (IAPNetworkError) msg.obj;
            showErrorDialog(context, pFragmentManager, context.getString(R.string.iap_ok),
                    "Unsuccessful",
                    getErrorDescriptionMessageFromErrorCode(context, error));
        }
    }

    public String getErrorTitleMessageFromErrorCode(final Context context, int errorCode) {
        String errorMessage;
        if (errorCode == IAPConstant.IAP_ERROR_NO_CONNECTION) {
            errorMessage = context.getString(R.string.iap_you_are_offline);
        } else {
            errorMessage = context.getString(R.string.iap_server_error);
        }
        return errorMessage;
    }

    public String getErrorDescriptionMessageFromErrorCode(final Context context,
                                                          IAPNetworkError error) {
        if (error.getIAPErrorCode() != IAPConstant.IAP_ERROR_NO_CONNECTION
                && !TextUtils.isEmpty(error.getMessage())) {
            IAPAnalytics.trackInformationalErrorWithPrefix(IAPAnalyticsConstant.SERVER + error.getIAPErrorCode() + "_" + error.getMessage());
            return error.getMessage();
        }

        String errorMessage;
        int errorCode = error.getIAPErrorCode();
        if (errorCode == IAPConstant.IAP_ERROR_NO_CONNECTION) {
            errorMessage = context.getString(R.string.iap_no_internet);
            IAPAnalytics.trackInformationalErrorWithPrefix(IAPAnalyticsConstant.SERVER + errorCode + "_" + errorMessage);
        } else if (errorCode == IAPConstant.IAP_ERROR_CONNECTION_TIME_OUT) {
            errorMessage = context.getString(R.string.iap_time_out_error);
            IAPAnalytics.trackTechnicalErrorWithPrefix(IAPAnalyticsConstant.SERVER + errorCode + "_" + errorMessage);
        } else if (errorCode == IAPConstant.IAP_ERROR_AUTHENTICATION_FAILURE) {
            errorMessage = context.getString(R.string.iap_authentication_failure);
            IAPAnalytics.trackTechnicalErrorWithPrefix(IAPAnalyticsConstant.SERVER + errorCode + "_" + errorMessage);
        } else {
            errorMessage = context.getString(R.string.iap_something_went_wrong);
            IAPAnalytics.trackTechnicalErrorWithPrefix(IAPAnalyticsConstant.SERVER + errorCode + "_" + errorMessage);
        }

        return errorMessage;
    }

    public boolean isNetworkAvailable(ConnectivityManager connectivityManager) {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public IAPNetworkError createIAPErrorMessage(String pServer, String errorMessage) {
        VolleyError volleyError = new ServerError();
        IAPNetworkError error = new IAPNetworkError(volleyError, -1, null);
        error.setCustomErrorMessage(pServer, errorMessage);
        return error;
    }

    void showDLSDialog(final Context context, String pButtonText, String pErrorString, String pErrorDescription, final FragmentManager pFragmentManager) {
        final AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(context)
                .setMessage(pErrorDescription).
                        setPositiveButton(pButtonText, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dismissAlertFragmentDialog(alertDialogFragment, pFragmentManager);
                            }
                        });

        builder.setTitle(pErrorString);
        if (alertDialogFragment != null) {
            dismissAlertFragmentDialog(alertDialogFragment, pFragmentManager);
        }
        alertDialogFragment = builder.create();
        if (alertDialogFragment == null) {
            alertDialogFragment = builder.setCancelable(false).create();
        }

        if (!alertDialogFragment.isVisible() && isCallingFragmentVisible(pFragmentManager)) {
            alertDialogFragment.show(pFragmentManager, ALERT_DIALOG_TAG);
        }

    }

    void showDLSDialogForPayment(final Context context, String pButtonText, String pErrorString, String pErrorDescription, final FragmentManager pFragmentManager, final AlertListener alertListener) {
        final AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(context)
                .setMessage(pErrorDescription).
                        setPositiveButton(pButtonText, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertListener.onPositiveBtnClick();
                                dismissAlertFragmentDialog(alertDialogFragment, pFragmentManager);
                            }
                        });

        builder.setTitle(pErrorString);
        if (alertDialogFragment != null) {
            dismissAlertFragmentDialog(alertDialogFragment, pFragmentManager);
        }
        alertDialogFragment = builder.create();
        if (alertDialogFragment == null) {
            alertDialogFragment = builder.setCancelable(false).create();
        }

        if (!alertDialogFragment.isVisible() && isCallingFragmentVisible(pFragmentManager)) {
            alertDialogFragment.show(pFragmentManager, ALERT_DIALOG_TAG);
        }

    }

    void dismissAlertFragmentDialog(AlertDialogFragment alertDialogFragment, FragmentManager fragmentManager) {

        if (alertDialogFragment == null) {
            alertDialogFragment = (AlertDialogFragment) fragmentManager.findFragmentByTag(ALERT_DIALOG_TAG);
        }
        if (alertDialogFragment != null && alertDialogFragment.isVisible() && isCallingFragmentVisible(fragmentManager))
            alertDialogFragment.dismiss();
    }

    boolean isCallingFragmentVisible(FragmentManager fragmentManager) {

        if (fragmentManager != null) {
            List<Fragment> fragments = fragmentManager.getFragments();
            if (fragments != null && fragments.size() > 0) {
                Fragment fragment = fragments.get((fragments.size()) - 1);
                return fragment.getActivity() != null && fragment.isAdded() && fragment.isVisible() && fragment.isResumed();
            }
        }
        return false;
    }
}