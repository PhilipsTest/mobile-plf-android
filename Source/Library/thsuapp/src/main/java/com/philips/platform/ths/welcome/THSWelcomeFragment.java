/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.welcome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.ths.base.THSBasePresenter;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uid.view.widget.Button;

public class THSWelcomeFragment extends THSBaseFragment implements View.OnClickListener {
    public static final String TAG = THSWelcomeFragment.class.getSimpleName();
    protected THSWelcomePresenter presenter;
    private RelativeLayout mRelativeLayoutAppointments;
    private RelativeLayout mRelativeLayoutVisitHostory;
    private RelativeLayout mRelativeLayoutHowItWorks;
    private Button mButton;
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

        mRelativeLayoutInitContainer = (RelativeLayout) view.findViewById(R.id.init_container);
        mRelativeLayoutAppointments = (RelativeLayout)view.findViewById(R.id.appointments);
        mRelativeLayoutVisitHostory  = (RelativeLayout) view.findViewById(R.id.visit_history);
        mRelativeLayoutHowItWorks = (RelativeLayout) view.findViewById(R.id.how_it_works);
        mButton = (Button) view.findViewById(R.id.ths_start);

        mRelativeLayoutAppointments.setEnabled(false);
        mRelativeLayoutAppointments.setOnClickListener(this);

        mRelativeLayoutVisitHostory.setEnabled(false);
        mRelativeLayoutVisitHostory.setOnClickListener(this);

        mRelativeLayoutHowItWorks.setEnabled(false);
        mRelativeLayoutHowItWorks.setOnClickListener(this);

        mButton.setEnabled(false);
        mButton.setOnClickListener(this);

        createCustomProgressBar(view, BIG);
        presenter.initializeAwsdk();

        ActionBarListener actionBarListener = getActionBarListener();
        if(null != actionBarListener){
            actionBarListener.updateActionBar(getString(R.string.ths_welcome),true);
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

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
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.appointments) {
            createCustomProgressBar(mRelativeLayoutInitContainer,BIG);
            presenter.onEvent(R.id.appointments);
        }else if(i == R.id.visit_history){
            createCustomProgressBar(mRelativeLayoutInitContainer,BIG);
            presenter.onEvent(R.id.visit_history);
        }else if(i == R.id.how_it_works){
            presenter.onEvent(R.id.how_it_works);
        }else if(i == R.id.ths_start){
            createCustomProgressBar(mRelativeLayoutInitContainer,BIG);
            presenter.onEvent(R.id.ths_start);
        }
    }

    void updateView(){
        mRelativeLayoutHowItWorks.setEnabled(true);
        mRelativeLayoutVisitHostory.setEnabled(true);
        mRelativeLayoutAppointments.setEnabled(true);
        mButton.setEnabled(true);
    }
}
