/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.americanwell.sdk.entity.visit.VisitReport;
import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.ths.utility.THSManager;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uid.view.widget.Label;

import java.util.List;

public class THSVisitHistoryFragment extends THSBaseFragment{

    public static final String TAG = THSVisitHistoryFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private THSVisitHistoryPresenter mThsVisitHistoryPresenter;
    private THSVisitHistoryAdapter mThsVisitHistoryAdapter;
    private Label mNumberOfAppointmentsLabel;
    RelativeLayout mRelativeLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.ths_scheduled_visits_list, container, false);
        mThsVisitHistoryPresenter = new THSVisitHistoryPresenter(this);
        mNumberOfAppointmentsLabel = (Label) view.findViewById(R.id.ths_number_of_visits);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.ths_visit_dates_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.providerListItemLayout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBarListener actionBarListener = getActionBarListener();
        if(null != actionBarListener){
            actionBarListener.updateActionBar(getString(R.string.ths_visit_history),true);
        }
        mThsVisitHistoryPresenter.getVisitHistory();
    }

    public void updateVisitHistoryView(List<VisitReport> visitReports) {
        if(getContext()!=null) {
            String text = getString(R.string.ths_number_of_visits_scheduled, visitReports.size());
            mNumberOfAppointmentsLabel.setText(text);
            mThsVisitHistoryAdapter = new THSVisitHistoryAdapter(visitReports, this);
            mRecyclerView.setAdapter(mThsVisitHistoryAdapter);
        }
    }
}
