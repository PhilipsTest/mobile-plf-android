/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.platform.mya;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.philips.platform.mya.account.AccountView;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uappframework.listener.BackEventListener;


public class MyaFragment extends Fragment implements
        OnClickListener, BackEventListener {
    private FragmentManager mFragmentManager;
    private ActionBarListener mActionBarListener;

    static String BACK_STACK_ID = MyaFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mya_fragment_my_account_root, container, false);
        mFragmentManager = getChildFragmentManager();
        if (mFragmentManager.getBackStackEntryCount() < 1) {
            inflateAccountView();
        }
        return view;
    }


    public boolean onBackPressed() {
        return handleBackStack();
    }

    private boolean handleBackStack() {
        if (mFragmentManager != null) {
            int count = mFragmentManager.getBackStackEntryCount();
            if (count == 0) {
                return true;
            }
            mFragmentManager.popBackStack();
        } else {
            getActivity().finish();
        }
        return false;
    }

/*    public void inflateIntroView() {
        try {
            if (null != mFragmentManager) {
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.csw_frame_layout_view_container, new IntroMenuView());
                fragmentTransaction.commitAllowingStateLoss();
            }
        } catch (IllegalStateException ignore) {
        }
    }*/

    public void inflateAccountView() {
        try {
            if (null != mFragmentManager) {
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.csw_frame_layout_view_container, new AccountView());
                fragmentTransaction.commitAllowingStateLoss();
            }
        } catch (IllegalStateException ignore) {
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == com.philips.cdp.registration.R.id.iv_reg_back) {
            onBackPressed();
        }
    }

    public int getFragmentCount() {
        FragmentManager fragmentManager = getChildFragmentManager();
        int fragmentCount = fragmentManager.getFragments().size();
        return fragmentCount;
    }

    public ActionBarListener getUpdateTitleListener() {
        return mActionBarListener;
    }

    public void setOnUpdateTitleListener(ActionBarListener listener) {
        this.mActionBarListener = listener;
    }

    private int titleResourceID = -99;

    public void setResourceID(int titleResourceId) {
        titleResourceID = titleResourceId;
    }

    public int getResourceID() {
        return titleResourceID;
    }


    @Override
    public boolean handleBackEvent() {
        return !(onBackPressed());
    }

    protected void setChildFragmentManager(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

}
