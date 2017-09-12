/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.providerslist;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.practice.Practice;
import com.americanwell.sdk.entity.provider.ProviderVisibility;
import com.americanwell.sdk.exception.AWSDKInstantiationException;
import com.philips.platform.ths.R;
import com.philips.platform.ths.appointment.THSAvailableProviderListBasedOnDateFragment;
import com.philips.platform.ths.appointment.THSDatePickerFragmentUtility;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.ths.base.THSBasePresenter;
import com.philips.platform.ths.intake.THSSymptomsFragment;
import com.philips.platform.ths.providerdetails.THSProviderDetailsFragment;
import com.philips.platform.ths.sdkerrors.THSSDKError;
import com.philips.platform.ths.utility.AmwellLog;
import com.philips.platform.ths.utility.THSConstants;
import com.philips.platform.ths.utility.THSManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class THSProviderListPresenter implements THSProvidersListCallback, THSBasePresenter,THSOnDemandSpecialtyCallback<List<THSOnDemandSpeciality>,THSSDKError> {

    private THSBaseFragment mThsBaseFragment;
    private THSProviderListViewInterface thsProviderListViewInterface;
    private List<THSOnDemandSpeciality> mThsOnDemandSpeciality;


    public THSProviderListPresenter(THSBaseFragment uiBaseView, THSProviderListViewInterface thsProviderListViewInterface){
        this.mThsBaseFragment = uiBaseView;
        this.thsProviderListViewInterface = thsProviderListViewInterface;
    }

    public void fetchProviderList(Consumer consumer, Practice practice){
        try {
            getPthManager().getProviderList(mThsBaseFragment.getFragmentActivity(), practice,this);
        } catch (AWSDKInstantiationException e) {
            e.printStackTrace();
        }

    }

    THSManager getPthManager() {
        return THSManager.getInstance();
    }


    @Override
    public void onProvidersListReceived(List<THSProviderInfo> providerInfoList, SDKError sdkError) {
        if(null != providerInfoList && providerInfoList.size() > 0 && null == sdkError) {
            boolean providerAvailable = isProviderAvailable(providerInfoList);
            thsProviderListViewInterface.updateMainView(providerAvailable);
            thsProviderListViewInterface.updateProviderAdapterList(providerInfoList);
        }
        else if(null != sdkError){
            AmwellLog.e(THSProviderDetailsFragment.TAG,sdkError.getMessage());
        }
        else {
            thsProviderListViewInterface.showNoProviderErrorDialog();
        }
    }

    boolean isProviderAvailable(List<THSProviderInfo> providerInfoList){
        for (THSProviderInfo thsProviderInfo: providerInfoList) {
            if(!ProviderVisibility.isOffline(thsProviderInfo.getVisibility())){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onProvidersListFetchError(Throwable throwable) {

    }

    @Override
    public void onEvent(int componentID) {
        final Practice practice = ((THSProvidersListFragment) mThsBaseFragment).getPractice();
        if (componentID == R.id.getStartedButton) {
            try {
                THSManager.getInstance().getOnDemandSpecialities(mThsBaseFragment.getFragmentActivity(),
                        practice, null, this);
            } catch (AWSDKInstantiationException e) {


            }
        } else if (componentID == R.id.getScheduleAppointmentButton) {
            final THSDatePickerFragmentUtility thsDatePickerFragmentUtility = new THSDatePickerFragmentUtility(mThsBaseFragment,true);


            final DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    thsDatePickerFragmentUtility.setCalendar(year, month, day);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);
                    Date date = new Date();
                    date.setTime(calendar.getTimeInMillis());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(THSConstants.THS_DATE, date);
                    bundle.putParcelable(THSConstants.THS_PRACTICE_INFO, practice);
                    final THSAvailableProviderListBasedOnDateFragment fragment = new THSAvailableProviderListBasedOnDateFragment();
                    fragment.setFragmentLauncher(mThsBaseFragment.getFragmentLauncher());
                    mThsBaseFragment.addFragment(fragment, THSAvailableProviderListBasedOnDateFragment.TAG, bundle);
                    mThsBaseFragment.hideProgressBar();
                }
            };
            thsDatePickerFragmentUtility.showDatePicker(onDateSetListener);
        }
    }

    @Override
    public void onResponse(List<THSOnDemandSpeciality> onDemandSpecialties, THSSDKError sdkError) {
        if(onDemandSpecialties == null || onDemandSpecialties.size()==0){
            mThsBaseFragment.showToast("No OnDemandSpecialities available at present, please try after some time");
            mThsBaseFragment.hideProgressBar();
            return;
        }
        mThsOnDemandSpeciality = onDemandSpecialties;
        Bundle bundle = new Bundle();
        bundle.putParcelable(THSConstants.THS_ON_DEMAND,onDemandSpecialties.get(0));
        final THSSymptomsFragment fragment = new THSSymptomsFragment();
        fragment.setFragmentLauncher(mThsBaseFragment.getFragmentLauncher());
        mThsBaseFragment.addFragment(fragment,THSSymptomsFragment.TAG,bundle);
        mThsBaseFragment.hideProgressBar();
    }

    @Override
    public void onFailure(Throwable throwable) {

    }
}
