package com.philips.cdp.registration.coppa.ui.controllers;

import android.view.View;
import android.widget.Toast;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.apptagging.AppTagging;
import com.philips.cdp.registration.apptagging.AppTagingConstants;
import com.philips.cdp.registration.coppa.R;
import com.philips.cdp.registration.coppa.base.CoppaExtension;
import com.philips.cdp.registration.coppa.base.CoppaStatus;
import com.philips.cdp.registration.coppa.interfaces.CoppaConsentUpdateCallback;
import com.philips.cdp.registration.coppa.ui.customviews.RegCoppaAlertDialog;
import com.philips.cdp.registration.coppa.ui.fragment.ParentalApprovalFragment;
import com.philips.cdp.registration.coppa.utils.AppCoppaTaggingConstants;
import com.philips.cdp.registration.coppa.utils.RegistrationCoppaHelper;
import com.philips.cdp.registration.coppa.utils.RegistrationCoppaLaunchHelper;
import com.philips.cdp.registration.handlers.RefreshUserHandler;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.servertime.ServerTime;
import com.philips.cdp.servertime.constants.ServerTimeConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class ParentalApprovalFragmentController implements RefreshUserHandler, View.OnClickListener {
    private ParentalApprovalFragment mParentalApprovalFragment;
    private CoppaExtension mCoppaExtension;
    private boolean isCoppaConsent;

    public ParentalApprovalFragmentController(ParentalApprovalFragment fragment) {
        mParentalApprovalFragment = fragment;
        mCoppaExtension = new CoppaExtension(mParentalApprovalFragment.getRegistrationFragment().getParentActivity().getApplicationContext());
    }


    public void refreshUser() {
        mParentalApprovalFragment.showRefreshProgress();
        User user = new User(mParentalApprovalFragment.getContext());
        user.refreshUser(this);
    }

    public boolean isCountryUS() {
        boolean isCountryUS;
        if (getCoppaExtension().getConsent().getLocale() != null) {
            isCountryUS = getCoppaExtension().getConsent().getLocale().equalsIgnoreCase("en_US");
        } else {
            isCountryUS = RegistrationHelper.getInstance().getCountryCode().equalsIgnoreCase("US");
        }
        return isCountryUS;
    }

    @Override
    public void onRefreshUserSuccess() {
        mCoppaExtension.buildConfiguration();
        mParentalApprovalFragment.showContent();
        mParentalApprovalFragment.hideRefreshProgress();
        updateUIBasedOnConsentStatus(mCoppaExtension.getCoppaEmailConsentStatus());
    }

    private void navigateToWelcome() {
        RegistrationCoppaLaunchHelper.isBackEventConsumedByRegistration(mParentalApprovalFragment.getActivity());
    }

    private View.OnClickListener mOkBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RegCoppaAlertDialog.dismissDialog();
            navigateToWelcome();
        }
    };

    @Override
    public void onRefreshUserFailed(int error) {
        mParentalApprovalFragment.hideRefreshProgress();
        showServerErrorALert();

    }

    public void showServerErrorALert() {
        RegCoppaAlertDialog.showCoppaDialogMsg("", mParentalApprovalFragment.getContext().getResources().getString(R.string.JanRain_Server_Connection_Failed), mParentalApprovalFragment.getActivity(), mOkBtnClick);
    }


    public CoppaExtension getCoppaExtension() {
        return mCoppaExtension;
    }

    private void updateUIBasedOnConsentStatus(final CoppaStatus coppaStatus){
        RLog.d("Consent status :", "" + coppaStatus);
        if(coppaStatus == CoppaStatus.kDICOPPAConsentPending){
            mParentalApprovalFragment.setConfirmApproval();
            isCoppaConsent = true;
            RLog.d("ParentalApprovalFragmentController Consent Pending :", "");
        } else{
            //first consent success
            if(mCoppaExtension.getConsent().getLocale().equalsIgnoreCase("en_US")) {
                if ( (hoursSinceLastConsent() >= 24L)) {
                    new User(mParentalApprovalFragment.getContext()).refreshUser(new RefreshUserHandler() {
                        @Override
                        public void onRefreshUserSuccess() {
                            mCoppaExtension.buildConfiguration();
                            if (coppaStatus == CoppaStatus.kDICOPPAConfirmationPending) {
                                RLog.d("ParentalApprovalFragmentController  :","Consent status"+hoursSinceLastConsent());
                                isCoppaConsent = false;
                                AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS, AppCoppaTaggingConstants.SECOND_CONSENT_VIEW);
                                mParentalApprovalFragment.setIsUSRegionCode();
                            }else{
                                if (RegistrationCoppaHelper.getInstance().getUserRegistrationListener() != null) {
                                    RegistrationCoppaHelper.getInstance().getUserRegistrationListener().notifyonUserRegistrationCompleteEventOccurred(mParentalApprovalFragment.getActivity());
                                }
                            }
                        }

                        @Override
                        public void onRefreshUserFailed(int error) {

                        }
                    });


                } else {
                    if (RegistrationCoppaHelper.getInstance().getUserRegistrationListener() != null) {
                        RegistrationCoppaHelper.getInstance().getUserRegistrationListener().notifyonUserRegistrationCompleteEventOccurred(mParentalApprovalFragment.getActivity());
                    }
                }
            } else {
                if (RegistrationCoppaHelper.getInstance().getUserRegistrationListener() != null) {
                    RegistrationCoppaHelper.getInstance().getUserRegistrationListener().notifyonUserRegistrationCompleteEventOccurred(mParentalApprovalFragment.getActivity());
                }
            }
        }


    }

    private long hoursSinceLastConsent() {

        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat(ServerTimeConstants.DATE_FORMAT_COPPA);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        long diff = 0;
        try {
            date = format.parse(mCoppaExtension.getConsent().getStoredAt());
            long millisecondsatConsentGiven = date.getTime();

            String timeNow = ServerTime.getInstance().getCurrentUTCTimeWithFormat(ServerTimeConstants.DATE_FORMAT_FOR_JUMP);
            format = new SimpleDateFormat(ServerTimeConstants.DATE_FORMAT_FOR_JUMP);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = format.parse(timeNow);
            long timeinMillisecondsNow = date.getTime();
            diff = timeinMillisecondsNow - millisecondsatConsentGiven;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return diff / 3600000;

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.reg_btn_agree) {
            ConsentHandler consentHandler = new ConsentHandler(mCoppaExtension, mParentalApprovalFragment.getContext());
            if(isCoppaConsent) {
                consentHandler.agreeConsent(AppTagingConstants.SEND_DATA, AppCoppaTaggingConstants.FIRST_LEVEL_CONSENT, mParentalApprovalFragment);

            }else{
                consentHandler.agreeConfirmation(AppTagingConstants.SEND_DATA, AppCoppaTaggingConstants.SECOND_LEVEL_CONSENT, mParentalApprovalFragment );

            }
        } else if (id == R.id.reg_btn_dis_agree) {
            ConsentHandler consentHandler = new ConsentHandler(mCoppaExtension, mParentalApprovalFragment.getContext());
            if (isCoppaConsent) {
                consentHandler.disAgreeConsent(mParentalApprovalFragment);

            } else {
                consentHandler.disAgreeConfirmation(mParentalApprovalFragment);
            }

            if (mCoppaExtension.getCoppaEmailConsentStatus() == CoppaStatus.kDICOPPAConsentNotGiven || mCoppaExtension.getCoppaEmailConsentStatus() == CoppaStatus.kDICOPPAConfirmationNotGiven) {
                if (RegistrationCoppaHelper.getInstance().getUserRegistrationListener() != null) {
                    RegistrationCoppaHelper.getInstance().getUserRegistrationListener().notifyonUserRegistrationCompleteEventOccurred(mParentalApprovalFragment.getActivity());
                }
            }

        }


    }

}
