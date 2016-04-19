/*----------------------------------------------------------------------------
Copyright(c) Philips Electronics India Ltd
All rights reserved. Reproduction in whole or in part is prohibited without
the written consent of the copyright holder.

Project           : InAppPurchase
----------------------------------------------------------------------------*/

package com.philips.cdp.di.iap.Fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.activity.IAPBackButtonListener;
import com.philips.cdp.di.iap.activity.IAPFragmentListener;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.cdp.tagging.Tagging;
import com.philips.cdp.uikit.drawable.VectorDrawable;

public abstract class BaseAnimationSupportFragment extends Fragment implements IAPBackButtonListener {
    private IAPFragmentListener mActivityListener;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mActivityListener = (IAPFragmentListener) getActivity();
    }

    public enum AnimationType {
        /**
         * No animation for Fragment
         */
        NONE,
    }

    @Override
    public void onResume() {
        super.onResume();
        setBackButtonVisibility(View.VISIBLE);
        setCartIconVisibility(View.GONE);
    }

    public void addFragment(BaseAnimationSupportFragment newFragment,
                            String newFragmentTag) {

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_mainFragmentContainer, newFragment, newFragmentTag);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();

        IAPLog.d(IAPLog.LOG, "Add fragment " + newFragment.getClass().getSimpleName() + "   ("
                + newFragmentTag + ")");
    }

    public void replaceFragment(Fragment newFragment, String newFragmentTag) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.remove(this);
        transaction.replace(R.id.fl_mainFragmentContainer, newFragment, newFragmentTag);
        transaction.commitAllowingStateLoss();
    }

    protected void setTitle(int resourceId) {
        mActivityListener.setHeaderTitle(resourceId);
    }

    protected void setTitle(String title) {
        mActivityListener.setHeaderTitle(title);
    }

    protected void setBackButtonVisibility(final int isVisible) {
        mActivityListener.setBackButtonVisibility(isVisible);
    }

    protected void finishActivity() {
        getActivity().finish();
    }

    @Override
    public void onBackPressed() {
        //NOP
    }


    public void updateCount(final int count) {
        mActivityListener.updateCount(count);
    }


    public void setCartIconVisibility(final int visibility) {
        mActivityListener.setCartIconVisibility(visibility);
    }

}
