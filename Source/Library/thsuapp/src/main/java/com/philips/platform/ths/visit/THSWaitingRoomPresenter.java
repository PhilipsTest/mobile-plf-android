package com.philips.platform.ths.visit;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.visit.ChatReport;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitEndReason;
import com.americanwell.sdk.exception.AWSDKInstantiationException;
import com.americanwell.sdk.manager.ValidationReason;
import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.ths.base.THSBasePresenter;
import com.philips.platform.ths.intake.THSSDKCallback;
import com.philips.platform.ths.utility.THSManager;


import java.util.Map;

import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_FINISHED_EXTRAS;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_RESULT_CODE;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_STATUS_APP_SERVER_DISCONNECTED;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_STATUS_PROVIDER_CONNECTED;
import static com.americanwell.sdk.activity.VideoVisitConstants.VISIT_STATUS_VIDEO_DISCONNECTED;
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


            final Bundle bundle = new Bundle();
           // awsdk.saveInstanceState(bundle);

            Intent intent = new Intent(mTHSWaitingRoomFragment.getFragmentActivity(),THSVisitFinishedActivity.class);
            intent.putExtra("awsdkState", bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            THSManager.getInstance().startVisit(mTHSWaitingRoomFragment.getFragmentActivity(), intent,this);
        } catch (AWSDKInstantiationException e) {
            e.printStackTrace();
        }

    }

    void handleVisitFinish(Intent intent){
        final Bundle visitExtras = intent.getBundleExtra(VISIT_FINISHED_EXTRAS);
        if (visitExtras != null) {

            int mResultcode = visitExtras.getInt(VISIT_RESULT_CODE);
            Visit visit = (Visit) visitExtras.getParcelable(VISIT);
            boolean isServerDisconnected = visitExtras.getBoolean(VISIT_STATUS_APP_SERVER_DISCONNECTED);
            boolean isVideoDisconnected = visitExtras.getBoolean(VISIT_STATUS_VIDEO_DISCONNECTED);
            boolean isProviderConnected = visitExtras.getBoolean(VISIT_STATUS_PROVIDER_CONNECTED);

            mTHSWaitingRoomFragment.mProgressBarWithLabel.setText(  " Please wait, your visit is  wrapping up");
            mTHSWaitingRoomFragment.addFragment(new THSVisitSummaryFragment(), THSVisitSummaryFragment.TAG,null);

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


       // mTHSWaitingRoomFragment.startActivityForResult(intent, REQUEST_VIDEO_VISIT);
        mTHSWaitingRoomFragment.getActivity().startActivity(intent);
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
