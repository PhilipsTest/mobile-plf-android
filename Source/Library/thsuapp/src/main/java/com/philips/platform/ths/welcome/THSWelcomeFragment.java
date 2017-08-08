/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.welcome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.ths.base.THSBasePresenter;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.BackEventListener;
import com.philips.platform.uid.view.widget.Button;

public class THSWelcomeFragment extends THSBaseFragment implements BackEventListener, View.OnClickListener {
    public static final String TAG = THSWelcomeFragment.class.getSimpleName();
    THSBasePresenter presenter;
    Button mInitButton;
    private RelativeLayout mRelativeLayoutInitContainer;

    public FragmentLauncher getFragmentLauncher() {
        return mFragmentLauncher;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new THSWelcomePresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.ths_welcome_fragment, container, false);
        createCustomProgressBar(view, BIG);
        ((THSWelcomePresenter) presenter).initializeAwsdk();
        if (getActionBarListener() != null)
            getActionBarListener().updateActionBar("", false);
        mInitButton = (Button) view.findViewById(R.id.init_amwell);
        mRelativeLayoutInitContainer = (RelativeLayout) view.findViewById(R.id.init_container);
        mInitButton.setOnClickListener(this);
        return view;
    }


    @Override
    public void finishActivityAffinity() {
        getActivity().finishAffinity();
    }

    @Override
    public FragmentActivity getFragmentActivity() {
        return getActivity();
    }


    @Override
    public boolean handleBackEvent() {
        return true;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.init_amwell) {
            createCustomProgressBar(mRelativeLayoutInitContainer,BIG);
            presenter.onEvent(R.id.init_amwell);
        }
    }

    void enableInitButton(boolean isEnabled) {
        if (mInitButton != null) {
            mInitButton.setEnabled(isEnabled);
        }
    }
}
