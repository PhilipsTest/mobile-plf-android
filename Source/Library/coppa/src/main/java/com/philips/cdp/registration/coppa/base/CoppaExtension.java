
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.coppa.base;

import android.content.Context;

import com.janrain.android.Jump;
import com.janrain.android.capture.CaptureRecord;
import com.philips.cdp.registration.coppa.interfaces.CoppaConsentUpdateCallback;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.ntputils.ServerTime;
import com.philips.ntputils.constants.ServerTimeConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CoppaExtension {

    private final String NULL = "null";

    private Context mContext;

    /**
     * {@code CoppaExtension } get context of Fragment
     *
     * @param context context
     */
    public CoppaExtension(Context context) {
        mContext = context;
    }



    /**
     * {@code coppaStatus} get coppa email consent status
     *
     * @return get coppa status will return enums
     */
    public CoppaStatus getCoppaEmailConsentStatus() {
        return getCoppaStatusForConsent(CoppaConfiguration.getConsent());
    }

    private CoppaStatus getCoppaStatusForConsent(com.philips.cdp.registration.coppa.base.Consent consent) {
        System.out.print("###### New changes getCoppaStatusForConsent Called  ");
        if (consent == null) {
            return null;
        }
        CoppaStatus coppaStatus = null;
        if (null != consent.getGiven()) {
            if (!consent.getGiven().equalsIgnoreCase(null)
                    && Boolean.parseBoolean(consent.getGiven())) {
                coppaStatus = CoppaStatus.kDICOPPAConsentGiven;
                if (null != consent.getConfirmationGiven() && !consent.getConfirmationGiven().equalsIgnoreCase(NULL)) {
                    if ((Boolean.parseBoolean(consent.getConfirmationGiven()))) {
                        coppaStatus = CoppaStatus.kDICOPPAConfirmationGiven;
                    } else {
                        coppaStatus = CoppaStatus.kDICOPPAConfirmationNotGiven;
                    }
                } else if (consent.getGiven() != null && (hoursSinceLastConsent() >= 24L) && consent.getConfirmationGiven() == null) {
                    System.out.print("$$$$$$$$$$$$$$$  > 24 Called ");
                    RLog.d("Consent", "Consent ***" + consent.getConfirmationCommunicationSentAt() + " " + consent.getConfirmationCommunicationSentAt());
                    coppaStatus = CoppaStatus.kDICOPPAConfirmationPending;
                    if (!consent.getLocale().equals("en_US")) {
                        coppaStatus = CoppaStatus.kDICOPPAConfirmationGiven;
                    }
                    RLog.d("Consent", "Consent coppaconfirmationPending");
                }else{
                    System.out.print("$$$$$$$$$$$$$$$  else 24 Called ");
                    coppaStatus = CoppaStatus.kDICOPPAConsentGiven;
                    if (!consent.getLocale().equals("en_US")) {
                        coppaStatus = CoppaStatus.kDICOPPAConfirmationGiven;
                    }
                }
            } else {
                coppaStatus = CoppaStatus.kDICOPPAConsentNotGiven;
            }
        } else {
            coppaStatus = CoppaStatus.kDICOPPAConsentNotGiven;
        }
        System.out.print("$$$$$$$$$$$$$$$  coppaStatus : "+coppaStatus);
        return coppaStatus;
    }

    private long hoursSinceLastConsent() {

        Date date;
        SimpleDateFormat format = new SimpleDateFormat(ServerTimeConstants.DATE_FORMAT_COPPA,
                Locale.ROOT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        long diff = 0;
        try {
            date = format.parse(getConsent().getStoredAt());
            long millisecondsatConsentGiven = date.getTime();

            final String timeNow = ServerTime.getCurrentUTCTimeWithFormat(
                    ServerTimeConstants.DATE_FORMAT_FOR_JUMP);
            format = new SimpleDateFormat(ServerTimeConstants.DATE_FORMAT_FOR_JUMP, Locale.ROOT);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = format.parse(timeNow);
            long timeinMillisecondsNow = date.getTime();
            diff = timeinMillisecondsNow - millisecondsatConsentGiven;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return diff / 3600000;
    }

    /**
     * {@code updateCoppaConsentConsentStatus}to update coppa consent Consent status
     *
     * @param coppaConsentStatus         this will give the coppa consent status as true or false
     * @param coppaConsentUpdateCallback call back  to get onSuccess or onFailure
     */
    public void updateCoppaConsentStatus(final boolean coppaConsentStatus, final CoppaConsentUpdateCallback coppaConsentUpdateCallback) {
        new CoppaConsentUpdater(mContext).updateCoppaConsentStatus(coppaConsentStatus, coppaConsentUpdateCallback);
    }

    /**
     * {@code updateCoppaConsentConfirmationStatus}to update coppa consent confirmation status
     *
     * @param coppaConsentStatus         this will give the coppa consent status as true or false
     * @param coppaConsentUpdateCallback call back  to get onSuccess or onFailure
     */
    public void updateCoppaConsentConfirmationStatus(final boolean coppaConsentStatus, final CoppaConsentUpdateCallback coppaConsentUpdateCallback) {
        new CoppaConsentUpdater(mContext).updateCoppaConsentConfirmationStatus(coppaConsentStatus, coppaConsentUpdateCallback);
    }

    /**
     * {@code getConsent} get consent
     *
     * @return coppa configuration
     */
    public Consent getConsent() {
        rebuildUserData();
        CoppaConfiguration.getCoopaConfigurationFlields(Jump.getSignedInUser());
        return CoppaConfiguration.getConsent();
    }

    /**
     * {@code buildConfiguration} build the configuration
     */
    public void buildConfiguration() {
        rebuildUserData();
        CoppaConfiguration.getCoopaConfigurationFlields(Jump.getSignedInUser());
    }

    /**
     * {@code resetConfiguration} reset configuration
     */
    public void resetConfiguration() {
        CoppaConfiguration.clearConfiguration();
    }

    private void rebuildUserData() {
        CaptureRecord signInUser = Jump.getSignedInUser();
        if (signInUser == null)
            Jump.loadUserFromDiskInternal(mContext);
    }
}
