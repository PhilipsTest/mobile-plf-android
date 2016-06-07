/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.coppa.ui.controllers;

import android.content.Context;
import android.widget.Toast;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.apptagging.AppTagging;
import com.philips.cdp.registration.coppa.R;
import com.philips.cdp.registration.coppa.base.CoppaExtension;
import com.philips.cdp.registration.coppa.base.CoppaStatus;
import com.philips.cdp.registration.coppa.interfaces.CoppaConsentUpdateCallback;
import com.philips.cdp.registration.coppa.ui.fragment.ParentalApprovalFragment;
import com.philips.cdp.registration.coppa.utils.RegistrationCoppaHelper;
import com.philips.cdp.registration.handlers.RefreshLoginSessionHandler;
import com.philips.cdp.registration.handlers.RefreshUserHandler;

/**
 * Created by 310202337 on 5/3/2016.
 */
public class ConsentHandler implements RefreshUserHandler {

    private CoppaExtension mCoppaExtension;
    private String mTaggingState;
    private String mTaggingKey;
    private String mTaggignValue;
    private Context mContext;
    private User mUser;
    private ParentalApprovalFragment mParentalApprovalFragment;
    public ConsentHandler(CoppaExtension coppaExtension,Context context ){
        mContext = context;
        mCoppaExtension = coppaExtension;
        mUser = new User(mContext);
    }
    public void agreeConsent(final String taggingState, final String taggingKey, ParentalApprovalFragment parentalApprovalFragment){
        mParentalApprovalFragment = parentalApprovalFragment;
        mTaggingState = taggingState;
        mTaggingKey = taggingKey;
        mParentalApprovalFragment.showRefreshProgress();
        mUser.refreshLoginSession(new RefreshLoginSessionHandler() {
            @Override
            public void onRefreshLoginSessionSuccess() {
                mCoppaExtension.updateCoppaConsentStatus(true, new CoppaConsentUpdateCallback() {
                    @Override
                    public void onSuccess() {
                        //First consent

                        mUser.refreshUser(ConsentHandler.this);
                        AppTagging.trackAction(mTaggingState, mTaggingKey, "Yes");
                        mParentalApprovalFragment.hideRefreshProgress();
                        if (RegistrationCoppaHelper.getInstance().getUserRegistrationListener() != null) {
                            RegistrationCoppaHelper.getInstance().getUserRegistrationListener().notifyonUserRegistrationCompleteEventOccurred(mParentalApprovalFragment.getActivity());
                        }
                    }

                    @Override
                    public void onFailure(int errorCode) {
                        AppTagging.trackAction(mTaggingState, mTaggingKey, "No");
                        mParentalApprovalFragment.hideRefreshProgress();
                        if (errorCode == -1) {
                            Toast.makeText(mParentalApprovalFragment.getContext(), mParentalApprovalFragment.getContext().getResources().getString(R.string.JanRain_Server_Connection_Failed)
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onRefreshLoginSessionFailedWithError(int error) {
                handleFailure();
            }

            @Override
            public void onRefreshLoginSessionInProgress(String message) {

            }
        });


    }

    public void agreeConfirmation(final String taggingState, final String taggingKey, ParentalApprovalFragment parentalApprovalFragment){
        mParentalApprovalFragment = parentalApprovalFragment;
        mTaggingState = taggingState;
        mTaggingKey = taggingKey;
        mParentalApprovalFragment.showRefreshProgress();
        mUser.refreshLoginSession(new RefreshLoginSessionHandler() {
            @Override
            public void onRefreshLoginSessionSuccess() {
                mCoppaExtension.updateCoppaConsentConfirmationStatus(true, new CoppaConsentUpdateCallback() {
                    @Override
                    public void onSuccess() {
                        //2nd Consent
                        mUser.refreshUser(ConsentHandler.this);
                        AppTagging.trackAction(mTaggingState, mTaggingKey, "Yes");
                        mParentalApprovalFragment.hideRefreshProgress();
                        /*if (RegistrationCoppaHelper.getInstance().getUserRegistrationListener() != null) {
                            RegistrationCoppaHelper.getInstance().getUserRegistrationListener().notifyonUserRegistrationCompleteEventOccurred(mParentalApprovalFragment.getActivity());
                        }*/
                    }

                    @Override
                    public void onFailure(int errorCode) {
                        AppTagging.trackAction(mTaggingState, mTaggingKey, "No");
                        mParentalApprovalFragment.hideRefreshProgress();
                        if (errorCode == -1) {
                            Toast.makeText(mParentalApprovalFragment.getContext(), mParentalApprovalFragment.getContext().getResources().getString(R.string.JanRain_Server_Connection_Failed)
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onRefreshLoginSessionFailedWithError(int error) {
                handleFailure();
            }

            @Override
            public void onRefreshLoginSessionInProgress(String message) {

            }
        });


    }

    public void disAgreeConsent(ParentalApprovalFragment parentalApprovalFragment){
        mParentalApprovalFragment = parentalApprovalFragment;
        mParentalApprovalFragment.showRefreshProgress();
        mUser.refreshLoginSession(new RefreshLoginSessionHandler() {
            @Override
            public void onRefreshLoginSessionSuccess() {
                mCoppaExtension.updateCoppaConsentStatus(false, new CoppaConsentUpdateCallback() {
                    @Override
                    public void onSuccess() {
                        mParentalApprovalFragment.hideRefreshProgress();
                        mCoppaExtension.buildConfiguration();
                        if (RegistrationCoppaHelper.getInstance().getUserRegistrationListener() != null) {
                            RegistrationCoppaHelper.getInstance().getUserRegistrationListener().notifyonUserRegistrationCompleteEventOccurred(mParentalApprovalFragment.getActivity());
                        }
                    }

                    @Override
                    public void onFailure(int errorCode) {
                        mParentalApprovalFragment.hideRefreshProgress();
                        if (errorCode == -1) {
                            Toast.makeText(mParentalApprovalFragment.getContext(), mParentalApprovalFragment.getContext().getResources().getString(R.string.JanRain_Server_Connection_Failed)
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onRefreshLoginSessionFailedWithError(int error) {
                handleFailure();
            }

            @Override
            public void onRefreshLoginSessionInProgress(String message) {

            }
        });


    }

    public void disAgreeConfirmation(ParentalApprovalFragment parentalApprovalFragment){
        mParentalApprovalFragment = parentalApprovalFragment;
        mParentalApprovalFragment.showRefreshProgress();
        mUser.refreshLoginSession(new RefreshLoginSessionHandler() {
            @Override
            public void onRefreshLoginSessionSuccess() {
                mCoppaExtension.updateCoppaConsentConfirmationStatus(false, new CoppaConsentUpdateCallback() {
                    @Override
                    public void onSuccess() {
                        mParentalApprovalFragment.hideRefreshProgress();
                        mCoppaExtension.buildConfiguration();
                        if (RegistrationCoppaHelper.getInstance().getUserRegistrationListener() != null) {
                            RegistrationCoppaHelper.getInstance().getUserRegistrationListener().notifyonUserRegistrationCompleteEventOccurred(mParentalApprovalFragment.getActivity());
                        }
                    }

                    @Override
                    public void onFailure(int errorCode) {
                        mParentalApprovalFragment.hideRefreshProgress();
                        if (errorCode == -1) {
                            Toast.makeText(mParentalApprovalFragment.getContext(), mParentalApprovalFragment.getContext().getResources().getString(R.string.JanRain_Server_Connection_Failed)
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onRefreshLoginSessionFailedWithError(int error) {
                handleFailure();
            }

            @Override
            public void onRefreshLoginSessionInProgress(String message) {

            }
        });


    }
    @Override
    public void onRefreshUserSuccess() {
        mCoppaExtension.buildConfiguration();
        updateUIBasedOnConsentStatus(mCoppaExtension.getCoppaEmailConsentStatus());

    }

    private void updateUIBasedOnConsentStatus(final CoppaStatus coppaStatus){
        if(coppaStatus == CoppaStatus.kDICOPPAConfirmationPending){
            //show thank you and 24 hour screen
        }else{
            if (RegistrationCoppaHelper.getInstance().getUserRegistrationListener() != null) {
                RegistrationCoppaHelper.getInstance().getUserRegistrationListener().notifyonUserRegistrationCompleteEventOccurred(mParentalApprovalFragment.getActivity());
            }
        }

    }





    @Override
    public void onRefreshUserFailed(int error) {
        AppTagging.trackAction(mTaggingState,mTaggingKey,"No");
        handleFailure();
    }

    private void handleFailure() {
        mParentalApprovalFragment.hideRefreshProgress();

        Toast.makeText(mParentalApprovalFragment.getContext(), mParentalApprovalFragment.getContext().getResources().getString(R.string.JanRain_Server_Connection_Failed)
                , Toast.LENGTH_SHORT).show();
    }
}
