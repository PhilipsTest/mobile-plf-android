package com.philips.platform.appinfra.tagging;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.utility.AIUtility;
import com.philips.platform.pif.chi.ConsentHandlerInterface;
import com.philips.platform.pif.chi.FetchConsentTypeStateCallback;
import com.philips.platform.pif.chi.PostConsentTypeCallback;
import com.philips.platform.pif.chi.datamodel.ConsentStates;
import com.philips.platform.pif.chi.datamodel.ConsentStatus;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Date;

import static com.philips.platform.appinfra.tagging.AppTagging.CLICKSTREAM_CONSENT_TYPE;
import static junit.framework.TestCase.assertEquals;

class ClickStreamConsentHandler implements ConsentHandlerInterface {

    @VisibleForTesting
    static final String CLICKSTREAM_CONSENT_VERSION = CLICKSTREAM_CONSENT_TYPE + "_Version";
    @VisibleForTesting
    static final String CLICKSTREAM_CONSENT_TIMESTAMP = CLICKSTREAM_CONSENT_TYPE + "_Timestamp";
    private AppInfraInterface appInfraInterface;

    ClickStreamConsentHandler(AppInfraInterface appInfraInterface) {
        this.appInfraInterface = appInfraInterface;
    }

    private AppTaggingInterface.PrivacyStatus getPrivacyStatus(boolean status) {
        return status ? AppTaggingInterface.PrivacyStatus.OPTIN : AppTaggingInterface.PrivacyStatus.OPTOUT;
    }

    private ConsentStatus getConsentStatus(AppTaggingInterface.PrivacyStatus privacyStatus, int version, Date lastModifiedTimeStamp) {
        ConsentStates status = ConsentStates.inactive;
        if (privacyStatus.equals(AppTaggingInterface.PrivacyStatus.OPTIN))
            status = ConsentStates.active;
        else if (privacyStatus.equals(AppTaggingInterface.PrivacyStatus.OPTOUT))
            status = ConsentStates.rejected;
        return new ConsentStatus(status, version, lastModifiedTimeStamp);
    }

    @VisibleForTesting
    @NonNull
    SecureStorageInterface.SecureStorageError getSecureStorageError() {
        return new SecureStorageInterface.SecureStorageError();
    }

    @Override
    public void fetchConsentTypeState(String consentType, FetchConsentTypeStateCallback callback) {
        assertEquals(consentType, CLICKSTREAM_CONSENT_TYPE);

        String valueForKey = getValueForKey(CLICKSTREAM_CONSENT_VERSION);
        int version = valueForKey == null ? 0 : Integer.valueOf(valueForKey);

        AppTaggingInterface.PrivacyStatus privacyStatus = appInfraInterface.getTagging().getPrivacyConsent();

        Date timestamp;
        String storedTimestamp = getValueForKey(CLICKSTREAM_CONSENT_TIMESTAMP);
        if (storedTimestamp == null) {
            timestamp = new Date(0);
        } else {
            if (isTimestampInCurrentFormattedDateTimeString(storedTimestamp)) {
                timestamp = AIUtility.convertStringToDate(storedTimestamp, "yyyy-MM-dd HH:mm:ss.SSS Z");
            } else {
                timestamp = parseDateTimeFromLegacyFormat(storedTimestamp);
            }
        }
        callback.onGetConsentsSuccess(getConsentStatus(privacyStatus, version, timestamp));
    }

    private Date parseDateTimeFromLegacyFormat(String storedTimestamp) {
        return new DateTime(Long.parseLong(storedTimestamp), DateTimeZone.UTC).toDate();
    }

    private boolean isTimestampInCurrentFormattedDateTimeString(String storedTimestamp) {
        return storedTimestamp.contains("-");
    }

    @Override
    public void storeConsentTypeState(String consentType, boolean status, int version, PostConsentTypeCallback callback) {
        assertEquals(consentType, CLICKSTREAM_CONSENT_TYPE);

        appInfraInterface.getTagging().setPrivacyConsent(getPrivacyStatus(status));
        storeValueForKey(CLICKSTREAM_CONSENT_VERSION, String.valueOf(version));
        storeValueForKey(CLICKSTREAM_CONSENT_TIMESTAMP, AIUtility.convertDateToString(getUTCTime()));
        callback.onPostConsentSuccess();
    }

    @VisibleForTesting
    Date getUTCTime() {
        return appInfraInterface.getTime().getUTCTime();
    }

    private String getValueForKey(String key) {
        return appInfraInterface.getSecureStorage().fetchValueForKey(key, getSecureStorageError());
    }

    private void storeValueForKey(String key, String value) {
        appInfraInterface.getSecureStorage().storeValueForKey(key, value, getSecureStorageError());
    }
}