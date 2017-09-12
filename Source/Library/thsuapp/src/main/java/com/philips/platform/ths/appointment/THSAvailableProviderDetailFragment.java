/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.appointment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.americanwell.sdk.entity.consumer.RemindOptions;
import com.americanwell.sdk.entity.practice.Practice;
import com.americanwell.sdk.entity.provider.EstimatedVisitCost;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.philips.platform.ths.R;
import com.philips.platform.ths.providerdetails.THSProviderDetailsDisplayHelper;
import com.philips.platform.ths.providerdetails.THSProviderDetailsFragment;
import com.philips.platform.ths.providerdetails.THSProviderEntity;
import com.philips.platform.ths.providerslist.THSProviderInfo;
import com.philips.platform.ths.utility.THSConstants;

import java.util.Date;

public class THSAvailableProviderDetailFragment extends THSProviderDetailsFragment implements View.OnClickListener, OnDateSetChangedInterface, THSDialogFragmentCallback<String> {
    public static final String TAG = THSAvailableProviderDetailFragment.class.getSimpleName();

    private Date mDate;
    private THSProviderEntity thsProviderEntity;
    private THSAvailableProviderDetailPresenter thsAvailableDetailProviderPresenter;
    private Practice mPractice;
    private THSProviderDetailsDisplayHelper thsProviderDetailsDisplayHelper;
    private int position;
    private RemindOptions remindOptions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ths_provider_details_fragment, container, false);
        SwipeRefreshLayout swipeProviderLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeProviderLayout);
        swipeProviderLayout.setEnabled(false);
        swipeProviderLayout.setRefreshing(false);

        if (null != getActionBarListener()) {
            getActionBarListener().updateActionBar(getString(R.string.ths_pick_time), true);
        }
        Bundle arguments = getArguments();
        thsProviderEntity = arguments.getParcelable(THSConstants.THS_PROVIDER_ENTITY);
        mDate = (Date) arguments.getSerializable(THSConstants.THS_DATE);
        mPractice = arguments.getParcelable(THSConstants.THS_PRACTICE_INFO);

        thsProviderDetailsDisplayHelper = new THSProviderDetailsDisplayHelper(getContext(),this,this,this,this,view);
        thsAvailableDetailProviderPresenter = new THSAvailableProviderDetailPresenter(this,thsProviderDetailsDisplayHelper);
        thsAvailableDetailProviderPresenter.updateContinueButtonState(false);
        refreshView();
        return view;
    }

    @Override
    public String getFragmentTag() {
        return THSAvailableProviderDetailFragment.TAG;
    }

    @Override
    public Practice getPractice(){
        return mPractice;
    }

    public void onCalenderItemClick(int position) {
        this.position = position;
        thsAvailableDetailProviderPresenter.updateContinueButtonState(true);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if(viewId == R.id.calendar_container) {
            thsAvailableDetailProviderPresenter.onEvent(viewId);
        }
        if(viewId == R.id.detailsButtonContinue){
            thsAvailableDetailProviderPresenter.scheduleAppointment(position);
        }
        if(viewId == R.id.set_reminder_layout){
            thsAvailableDetailProviderPresenter.onEvent(viewId);
        }
    }

    public THSProviderInfo getProviderEntity() {
        ProviderInfo providerInfo;
        if(thsProviderEntity instanceof THSAvailableProvider) {
            providerInfo = ((THSAvailableProvider) thsProviderEntity).getProviderInfo();
        }else {
            providerInfo = ((THSProviderInfo)thsProviderEntity).getProviderInfo();
        }

        THSProviderInfo thsProviderInfo = new THSProviderInfo();
        thsProviderInfo.setTHSProviderInfo(providerInfo);
        return thsProviderInfo;
    }

    @Override
    public void refreshView() {
        thsAvailableDetailProviderPresenter.fetchProviderDetails(getContext(),getProviderEntity());
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public Date getDate() {
        return mDate;
    }


    @Override
    public void onPostData(String o) {
        if(null != o){
            thsProviderDetailsDisplayHelper.setReminderValue(o);
            if(o.equalsIgnoreCase(THSConstants.THS_NO_REMINDER_STRING)){
                remindOptions = RemindOptions.NO_REMINDER;
            }
            if(o.equalsIgnoreCase(THSConstants.THS_15_MINS_REMINDER)){
                remindOptions = RemindOptions.FIFTEEN_MIN;
            }
            if(o.equalsIgnoreCase(THSConstants.THS_ONE_HOUR_REMINDER)){
                remindOptions = RemindOptions.ONE_HOUR;
            }
            if(o.equalsIgnoreCase(THSConstants.THS_FOUR_HOURS_REMINDER)){
                remindOptions = RemindOptions.FOUR_HOURS;
            }
            if(o.equalsIgnoreCase(THSConstants.THS_EIGHT_HOURS_REMINDER)){
                remindOptions = RemindOptions.EIGHT_HOURS;
            }
            if(o.equalsIgnoreCase(THSConstants.THS_ONE_DAY_REMINDER)){
                remindOptions = RemindOptions.ONE_DAY;
            }
            if(o.equalsIgnoreCase(THSConstants.THS_ONE_WEEK_REMINDER)){
                remindOptions = RemindOptions.ONE_WEEK;
            }

        }
    }

    @Override
    public void updateEstimatedCost(EstimatedVisitCost estimatedVisitCost) {
        thsProviderDetailsDisplayHelper.updateEstimateCost(estimatedVisitCost);
    }


    public RemindOptions getReminderOptions(){
        return remindOptions;
    }

    @Override
    public String getReminderTime(){
        return thsProviderDetailsDisplayHelper.getReminderValue();
    }

}
