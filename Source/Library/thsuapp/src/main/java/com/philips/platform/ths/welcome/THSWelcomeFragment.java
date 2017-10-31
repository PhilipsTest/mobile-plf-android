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
import com.philips.platform.ths.utility.THSManager;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uid.view.widget.Button;


import static com.philips.platform.ths.utility.THSConstants.THS_HOW_IT_WORKS;
import static com.philips.platform.ths.utility.THSConstants.THS_PRACTICE_PAGE;
import static com.philips.platform.ths.utility.THSConstants.THS_SCHEDULE_APPOINTMENT_PICK_PROVIDER;
import static com.philips.platform.ths.utility.THSConstants.THS_SEND_DATA;
import static com.philips.platform.ths.utility.THSConstants.THS_SPECIAL_EVENT;
import static com.philips.platform.ths.utility.THSConstants.THS_START;
import static com.philips.platform.ths.utility.THSConstants.THS_VISIT_HISTORY_LIST;
import static com.philips.platform.ths.utility.THSConstants.THS_WELCOME;

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
        
        presenter.getStarted();

        ActionBarListener actionBarListener = getActionBarListener();
        if(null != actionBarListener){
            actionBarListener.updateActionBar(getString(R.string.ths_welcome),true);
        }
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
           // THSManager.getInstance().getThsTagging().trackActionWithInfo(THS_SEND_DATA,THS_SPECIAL_EVENT, THS_START+ THS_SCHEDULE_APPOINTMENT_PICK_PROVIDER);
            presenter.onEvent(R.id.appointments);
        }else if(i == R.id.visit_history){
            //THSManager.getInstance().getThsTagging().trackActionWithInfo(THS_SEND_DATA,THS_SPECIAL_EVENT, THS_START+THS_VISIT_HISTORY_LIST);
            presenter.onEvent(R.id.visit_history);
        }else if(i == R.id.how_it_works){
            //THSManager.getInstance().getThsTagging().trackActionWithInfo(THS_SEND_DATA,THS_SPECIAL_EVENT, THS_START+THS_HOW_IT_WORKS);
            presenter.onEvent(R.id.how_it_works);
        }else if(i == R.id.ths_start){
           // THSManager.getInstance().getThsTagging().trackActionWithInfo(THS_SEND_DATA,THS_SPECIAL_EVENT, THS_START+THS_PRACTICE_PAGE);
            presenter.onEvent(R.id.ths_start);
        }
    }

    void updateView(){
        mRelativeLayoutHowItWorks.setEnabled(true);
        mRelativeLayoutVisitHostory.setEnabled(true);
        mRelativeLayoutAppointments.setEnabled(true);
        mButton.setEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        // entry to THS, start tagging
        THSManager.getInstance().getThsTagging().collectLifecycleInfo(this.getActivity());
        THSManager.getInstance().getThsTagging().trackPageWithInfo(THS_WELCOME,null,null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // exit from THS, collect tagging data
        THSManager.getInstance().getThsTagging().pauseLifecycleInfo();
    }
}
