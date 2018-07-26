package com.philips.platform.appinfra.consentmanager.consenthandler;

import android.support.annotation.NonNull;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.internationalization.InternationalizationInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.pif.chi.ConsentError;
import com.philips.platform.pif.chi.FetchConsentTypeStateCallback;
import com.philips.platform.pif.chi.PostConsentTypeCallback;
import com.philips.platform.pif.chi.datamodel.ConsentStates;
import com.philips.platform.pif.chi.datamodel.ConsentStatus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by abhishek on 5/22/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceStoredConsentHandlerTest {

    @Mock
    AppInfraInterface appInfra;
    private DeviceStoredConsentHandler handler;
    private String storedValueHighVersion = "true" + DeviceStoredConsentHandler.DEVICESTORE_VALUE_DELIMITER + "2" + DeviceStoredConsentHandler.DEVICESTORE_VALUE_DELIMITER + "en_US" + DeviceStoredConsentHandler.DEVICESTORE_VALUE_DELIMITER +"2018-05-18 07:30:27.119 +0000";
    private String storedValueFalseVersion = "false" + DeviceStoredConsentHandler.DEVICESTORE_VALUE_DELIMITER + "2" + DeviceStoredConsentHandler.DEVICESTORE_VALUE_DELIMITER + "en_US" + DeviceStoredConsentHandler.DEVICESTORE_VALUE_DELIMITER + "2018-07-08 10:19:27.156 +0000";
    @Mock
    private SecureStorageInterface storageInterface;

    @Mock
    private SecureStorageInterface.SecureStorageError secureStorageError;

    @Mock
    private InternationalizationInterface internationalizationInterface;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(appInfra.getSecureStorage()).thenReturn(storageInterface);
        when(appInfra.getInternationalization()).thenReturn(internationalizationInterface);
        handler = new TestDeviceStoredConsentHandler(appInfra);
    }

    @Test
    public void verifyInactiveStatusForFirstFetch() {
        when(secureStorageError.getErrorCode()).thenReturn(SecureStorageInterface.SecureStorageError.secureStorageError.NoDataFoundForKey);
        handler.fetchConsentTypeState("type1", new TestCheckConsentCallback() {
            @Override
            public void onGetConsentsSuccess(ConsentStatus consent) {
                assertTrue(consent.getConsentState() == ConsentStates.inactive);
            }
        });
    }

    @Test
    public void verifyErrorForKeyNotFound() {
        handler.fetchConsentTypeState("type1", new TestCheckConsentCallback() {
            @Override
            public void onGetConsentsSuccess(ConsentStatus consent) {
                assertTrue(consent.getConsentState() == ConsentStates.inactive);
            }
        });
    }

    @Test
    public void verifyErrorForMissingType() {
        when(storageInterface.fetchValueForKey(anyString(), eq(secureStorageError))).thenReturn(storedValueFalseVersion).thenReturn(null);
        handler.fetchConsentTypeState("type1", new TestCheckConsentCallback() {
            @Override
            public void onGetConsentsSuccess(ConsentStatus consent) {
                assertTrue(consent.getConsentState() == ConsentStates.inactive);
            }
        });
    }

    @Test
    public void verifyActiveStatus() {
        when(storageInterface.fetchValueForKey(anyString(), eq(secureStorageError))).thenReturn(storedValueHighVersion);
        handler.fetchConsentTypeState("type1", new TestCheckConsentCallback() {
            @Override
            public void onGetConsentsSuccess(ConsentStatus consent) {
                assertTrue(consent.getConsentState() == ConsentStates.active);
            }
        });
    }

    @Test
    public void verifyPostSuccess() {
        PostConsentTypeCallback mockCallback = mock(PostConsentTypeCallback.class);
        when(storageInterface.storeValueForKey(anyString(), anyString(), eq(secureStorageError))).thenReturn(true).thenReturn(true);
        handler.storeConsentTypeState("type1", true, 1, mockCallback);
        verify(mockCallback).onPostConsentSuccess();
    }

    @Test
    public void verifyPostFailure() {
        when(storageInterface.storeValueForKey(anyString(), anyString(), eq(secureStorageError))).thenReturn(false);
        when(secureStorageError.getErrorCode()).thenReturn(SecureStorageInterface.SecureStorageError.secureStorageError.AccessKeyFailure);
        handler.storeConsentTypeState("type1", true, 1, new TestPostConsentCallback() {
            @Override
            public void onPostConsentFailed(ConsentError error) {
                assertTrue(error.getError().startsWith("Error updating device stored consent"));
            }
        });
    }

    private class TestDeviceStoredConsentHandler extends DeviceStoredConsentHandler {

        TestDeviceStoredConsentHandler(AppInfraInterface appInfra) {
            super(appInfra);
        }

        @NonNull
        @Override
        SecureStorageInterface.SecureStorageError getSecureStorageError() {
            return secureStorageError;
        }

        @Override
        Date getUTCTime() {
            return new Date();
        }
    }

    private class TestPostConsentCallback implements PostConsentTypeCallback {
        @Override
        public void onPostConsentFailed(ConsentError error) {
            throw new RuntimeException("onPostConsentFailed Failed");
        }

        @Override
        public void onPostConsentSuccess() {
            throw new RuntimeException("onPostConsentSuccess Failed");
        }
    }

    private class TestCheckConsentCallback implements FetchConsentTypeStateCallback {
        @Override
        public void onGetConsentsSuccess(ConsentStatus consentStatus) {
            throw new RuntimeException("onGetConsentsSuccess Failed");
        }

        @Override
        public void onGetConsentsFailed(ConsentError error) {
            throw new RuntimeException("onGetConsentsFailed Failed");
        }
    }
}