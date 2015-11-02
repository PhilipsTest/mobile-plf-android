
package com.philips.cdp.registration.ui.traditional;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;

import com.philips.cdp.registration.apptagging.AppTagging;
import com.philips.cdp.registration.apptagging.AppTaggingErrors;
import com.philips.cdp.registration.apptagging.AppTagingConstants;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.ui.utils.RLog;

import java.util.HashMap;
import java.util.Map;

public abstract class RegistrationBaseFragment extends Fragment {

    protected int mLeftRightMarginPort;

    protected int mLeftRightMarginLand;

    protected abstract void setViewParams(Configuration config, int width);

    protected abstract void handleOrientation(final View view);

    public abstract int getTitleResourceId();

    private int mPrevTitleResourceId = -99;

    protected int mWidth = 0;

    private final int JELLY_BEAN = 16;

    @Override
    public void onAttach(Activity activity) {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "RegistrationBaseFragment : onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "RegistrationBaseFragment : onCreate");
        mLeftRightMarginPort = (int) getResources().getDimension(R.dimen.reg_layout_margin_port);
        mLeftRightMarginLand = (int) getResources().getDimension(R.dimen.reg_layout_margin_land);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "RegistrationBaseFragment : onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "RegistrationBaseFragment : onStart");
    }

    @Override
    public void onResume() {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "RegistrationBaseFragment : onResume");
        super.onResume();
        setCurrentTitle();
    }

    @Override
    public void onPause() {
        super.onPause();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "RegistrationBaseFragment : onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "RegistrationBaseFragment : onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "RegistrationBaseFragment : onDestroyView");
    }

    @Override
    public void onDestroy() {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "RegistrationBaseFragment : onDestroy");
        setPrevTiltle();
        super.onDestroy();
    }

    private void setPrevTiltle() {
        RegistrationFragment fragment = (RegistrationFragment) getParentFragment();

        if (null != fragment && null != fragment.getUpdateTitleListener()
                && mPrevTitleResourceId != -99) {
            if (fragment.getFragmentCount() > 2) {
                fragment.getUpdateTitleListener().updateRegistrationTitleWithBack(
                        mPrevTitleResourceId);
            } else {
                fragment.getUpdateTitleListener().updateRegistrationTitle(mPrevTitleResourceId);
            }

            trackBackActionPage();
            fragment.setResourceID(mPrevTitleResourceId);
        }
    }

    private void trackBackActionPage() {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "RegistrationBaseFragment : onDetach");
    }

    private void setCurrentTitle() {

        RegistrationFragment fragment = (RegistrationFragment) getParentFragment();
        if (null != fragment && null != fragment.getUpdateTitleListener()
                && -99 != fragment.getResourceID()) {
            mPrevTitleResourceId = (Integer) fragment.getResourceID();
        }
        if (fragment.getFragmentCount() > 1) {
            if (this instanceof WelcomeFragment) {
                fragment.getUpdateTitleListener().updateRegistrationTitle(getTitleResourceId());
            } else {
                fragment.getUpdateTitleListener().updateRegistrationTitleWithBack(getTitleResourceId());
            }
        } else {
            fragment.getUpdateTitleListener().updateRegistrationTitle(getTitleResourceId());
        }
        fragment.setResourceID(getTitleResourceId());
    }

    public RegistrationFragment getRegistrationFragment() {
        Fragment fragment = getParentFragment();
        if (fragment != null && (fragment instanceof RegistrationFragment)) {
            return (RegistrationFragment) fragment;
        }
        return null;
    }

    protected void consumeTouch(View view) {
        if (view == null)
            return;
        view.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }


    protected void applyParams(Configuration config, View view, int width) {

        LinearLayout.LayoutParams mParams = (LayoutParams) view.getLayoutParams();
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (getResources().getBoolean(R.bool.isTablet)) {
                mParams.leftMargin = mParams.rightMargin = width / 5;
            } else {
                mParams.leftMargin = mParams.rightMargin = 0;
            }
        } else {
            if (getResources().getBoolean(R.bool.isTablet)) {
                mParams.leftMargin = mParams.rightMargin = (int) (((width / 6) * (1.75)));
            } else {
                mParams.leftMargin = mParams.rightMargin = (int) ((width) / 6);
            }
        }
        view.setLayoutParams(mParams);
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

    protected void trackActionRegisterError(int errorCode) {
        AppTaggingErrors.trackActionRegisterError(errorCode);
    }

    protected void trackActionLoginError(int errorCode) {
        AppTaggingErrors.trackActionLoginError(errorCode);
    }

    protected void trackActionForgotPasswordFailure(int errorCode) {
        AppTaggingErrors.trackActionForgotPasswordFailure(errorCode);
    }

    protected void trackActionResendVerificationFailure(int errorCode) {
        AppTaggingErrors.trackActionResendNetworkFailure(errorCode);
    }

    protected void trackMultipleActionsRegistration() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(AppTagingConstants.REGISTRATION_CHANNEL, AppTagingConstants.MY_PHILIPS);
        map.put(AppTagingConstants.SPECIAL_EVENTS, AppTagingConstants.START_USER_REGISTRATION);
        AppTagging.trackMultipleActions(AppTagingConstants.SEND_DATA, map);
    }

    protected void trackMultipleActionsLogin(String providerName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(AppTagingConstants.LOGIN_CHANNEL, providerName);
        map.put(AppTagingConstants.SPECIAL_EVENTS, AppTagingConstants.LOGIN_START);
        AppTagging.trackMultipleActions(AppTagingConstants.SEND_DATA, map);
    }

    protected void handleOrientationOnView(final View view) {
        if (null == view) {
            return;
        }
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int currentWidth = view.getWidth();
                if (mWidth != currentWidth) {
                    mWidth = currentWidth;
                    if (Build.VERSION.SDK_INT < JELLY_BEAN) {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    setViewParams(getResources().getConfiguration(), mWidth);
                    view.getViewTreeObserver().addOnGlobalLayoutListener(this);
                }
            }
        });
    }

    protected void scrollViewAutomatically(final View view, final ScrollView scrollView) {
        view.requestFocus();
      /* scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, view.getTop());
            }
        });*/

       scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

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
