/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.settings;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKLocalDate;
import com.americanwell.sdk.entity.visit.Appointment;
import com.americanwell.sdk.exception.AWSDKInstantiationException;
import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBasePresenter;
import com.philips.platform.ths.sdkerrors.THSSDKError;
import com.philips.platform.ths.sdkerrors.THSSDKErrorFactory;
import com.philips.platform.ths.utility.AmwellLog;
import com.philips.platform.ths.utility.THSConstants;
import com.philips.platform.ths.utility.THSManager;
import com.philips.platform.ths.welcome.THSInitializeCallBack;

import java.util.List;

import static com.philips.platform.ths.utility.THSConstants.THS_SEND_DATA;

public class THSScheduledVisitsPresenter implements THSBasePresenter, THSGetAppointmentsCallback<List<Appointment>, THSSDKError>, THSInitializeCallBack<Void, THSSDKError> {

    THSScheduledVisitsFragment mThsScheduledVisitsFragment;

    public THSScheduledVisitsPresenter(THSScheduledVisitsFragment thsScheduledVisitsFragment) {
        mThsScheduledVisitsFragment = thsScheduledVisitsFragment;
    }

    @Override
    public void onEvent(int componentID) {

    }

    void cancelAppointment(Appointment appointment) {
        try {
            THSManager.getInstance().cancelAppointment(mThsScheduledVisitsFragment.getContext(), appointment, this);
        } catch (AWSDKInstantiationException e) {
            e.printStackTrace();
        }
    }

    public void getAppointmentsSince(SDKLocalDate dateSince) throws AWSDKInstantiationException {
        THSManager.getInstance().getAppointments(mThsScheduledVisitsFragment.getContext(), dateSince, this);
    }

    @Override
    public void onResponse(List<Appointment> appointments, SDKError sdkError) {
        if(null!= mThsScheduledVisitsFragment && mThsScheduledVisitsFragment.isFragmentAttached()) {
            AmwellLog.i(AmwellLog.LOG, "appoint response");
            mThsScheduledVisitsFragment.updateList(appointments);
            setProgressBarVisibility(false);
        }
    }

    @Override
    public void onFailure(Throwable throwable) {
        if(null!= mThsScheduledVisitsFragment && mThsScheduledVisitsFragment.isFragmentAttached()) {
            mThsScheduledVisitsFragment.showError(mThsScheduledVisitsFragment.getString(R.string.ths_se_server_error_toast_message),true);
            setProgressBarVisibility(false);
        }
    }

    @Override
    public void onInitializationResponse(Void var1, THSSDKError var2) {
        if(null!= mThsScheduledVisitsFragment && mThsScheduledVisitsFragment.isFragmentAttached()) {
            if(var2.getSdkError() != null){
                if(var2.getSDKErrorReason() != null){
                    mThsScheduledVisitsFragment.showError(THSSDKErrorFactory.getErrorType(var2.getSDKErrorReason()));
                    return;
                }else {
                    mThsScheduledVisitsFragment.showError(THSConstants.THS_GENERIC_SERVER_ERROR);
                }
            }else {
                THSManager.getInstance().getThsTagging().trackActionWithInfo(THS_SEND_DATA, "specialEvents", "appointmentsCancelled");
                mThsScheduledVisitsFragment.refreshList();
            }
        }
    }

    @Override
    public void onInitializationFailure(Throwable var1) {
        setProgressBarVisibility(false);
    }

    public void setProgressBarVisibility(boolean isVisible) {
        if(null!= mThsScheduledVisitsFragment && mThsScheduledVisitsFragment.isFragmentAttached()) {
            if (!isVisible) {
                mThsScheduledVisitsFragment.hideProgressBar();
            }
        }
    }
}
