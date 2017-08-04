/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.visit;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.visit.ChatReport;
import com.americanwell.sdk.entity.visit.VisitEndReason;
import com.americanwell.sdk.exception.AWSDKInstantiationException;
import com.americanwell.sdk.manager.ValidationReason;
import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBasePresenter;
import com.philips.platform.ths.utility.THSManager;

import java.util.Map;

import static com.philips.platform.ths.utility.THSConstants.REQUEST_VIDEO_VISIT;

/**
 * Created by philips on 7/26/17.
 */

public class THSWaitingRoomPresenter implements THSBasePresenter, THSStartVisitCallback, THSCancelVisitCallBack.SDKCallback<Void, SDKError> {

    THSWaitingRoomFragment mTHSWaitingRoomFragment;

    public THSWaitingRoomPresenter(THSWaitingRoomFragment mTHSWaitingRoomFragment) {
        this.mTHSWaitingRoomFragment = mTHSWaitingRoomFragment;
    }

    @Override
    public void onEvent(int componentID) {
        if (componentID == R.id.uid_alert_positive_button) {
            mTHSWaitingRoomFragment.mProgressBarWithLabel.setText("Cancelling Visit");
            cancelVisit();
            mTHSWaitingRoomFragment.alertDialogFragment.dismiss();
        }
    }

    void startVisit() {
        try {
            if (null != THSManager.getInstance().getTHSVisit().getVisit() && null != THSManager.getInstance().getTHSVisit().getVisit().getAssignedProvider()) {
                mTHSWaitingRoomFragment.mProviderNameLabel.setText(THSManager.getInstance().getTHSVisit().getVisit().getAssignedProvider().getFullName());
                mTHSWaitingRoomFragment.mProviderPracticeLabel.setText(THSManager.getInstance().getTHSVisit().getVisit().getAssignedProvider().getPracticeInfo().getName());

                ///////////
                ProviderInfo providerInfo = THSManager.getInstance().getTHSVisit().getVisit().getAssignedProvider();
                if (providerInfo.hasImage()) {
                    try {
                        THSManager.getInstance().getAwsdk(mTHSWaitingRoomFragment.getFragmentActivity()).
                                getPracticeProvidersManager().
                                newImageLoader(providerInfo,
                                        mTHSWaitingRoomFragment.mProviderImageView, ProviderImageSize.SMALL).placeholder
                                (mTHSWaitingRoomFragment.mProviderImageView.getResources().getDrawable(R.drawable.doctor_placeholder)).
                                build().load();
                    } catch (AWSDKInstantiationException e) {
                        e.printStackTrace();
                    }
                }
                ////////////
            }
            Integer patientWaitingCount = THSManager.getInstance().getTHSVisit().getVisit().getPatientsAheadOfYou();
            if (null != patientWaitingCount && patientWaitingCount > 0) {

                mTHSWaitingRoomFragment.mProgressBarWithLabel.setText(patientWaitingCount + " patients waiting");
            }
            THSManager.getInstance().startVisit(mTHSWaitingRoomFragment.getFragmentActivity(), null,this);
        } catch (AWSDKInstantiationException e) {
            e.printStackTrace();
        }

    }

    void cancelVisit() {
        try {
            THSManager.getInstance().cancelVisit(mTHSWaitingRoomFragment.getFragmentActivity(), this);
        } catch (AWSDKInstantiationException e) {
            e.printStackTrace();
        }
    }

    void abondonCurrentVisit() {
        try {
            THSManager.getInstance().abondonCurrentVisit(mTHSWaitingRoomFragment.getFragmentActivity());
            mTHSWaitingRoomFragment.getFragmentActivity().getSupportFragmentManager().popBackStack();
        } catch (AWSDKInstantiationException e) {
            e.printStackTrace();
        }
    }


    void updatePatientAheadCount(int count) {
        if (count > 0) {
            mTHSWaitingRoomFragment.mProgressBarWithLabel.setText(count + " patients waiting");
        }

    }

    @Override
    public void onProviderEntered(@NonNull Intent intent) {
        // set up ongoing notification
  /*      PendingIntent pendingIntent = PendingIntent.getActivity(mTHSWaitingRoomFragment.getFragmentActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mTHSWaitingRoomFragment.getFragmentActivity());
        builder.setSmallIcon(R.drawable.awsdk_ic_visit_camera_default)
                .setContentTitle("THS")
                .setContentText("provider name")
                .setAutoCancel(false)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);
        builder.build();*/
        // notificationManager.notify(ONGOING_NOTIFICATION_ID, builder.build());
        // start activity
        mTHSWaitingRoomFragment.startActivityForResult(intent, REQUEST_VIDEO_VISIT);
    }

    @Override
    public void onStartVisitEnded(@NonNull VisitEndReason visitEndReason) {

    }

    @Override
    public void onPatientsAheadOfYouCountChanged(int i) {
        updatePatientAheadCount(i);
    }

    @Override
    public void onSuggestedTransfer() {

    }

    @Override
    public void onChat(@NonNull ChatReport chatReport) {

    }

    @Override
    public void onPollFailure(@NonNull Throwable throwable) {

    }

    @Override
    public void onValidationFailure(Map<String, ValidationReason> map) {

    }

    @Override
    public void onResponse(Void aVoid, SDKError sdkError) {
        // must  be cancel visit call back
        abondonCurrentVisit();
    }

    @Override
    public void onFailure(Throwable throwable) {
        abondonCurrentVisit();
    }


}
