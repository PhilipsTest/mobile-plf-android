/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.platform.referenceapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.referenceapp.interfaces.HandleNotificationPayloadInterface;
import com.philips.platform.referenceapp.interfaces.PushNotificationTokenRegistrationInterface;
import com.philips.platform.referenceapp.interfaces.RegistrationCallbacks;
import com.philips.platform.referenceapp.services.PlatformFirebaseMessagingService;
import com.philips.platform.referenceapp.services.RegistrationIntentService;
import com.philips.platform.referenceapp.utils.PNLog;
import com.philips.platform.referenceapp.utils.PushNotificationConstants;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ServiceController;

import java.util.HashMap;
import java.util.Map;

import static com.philips.platform.referenceapp.utils.PushNotificationConstants.GCM_TOKEN;
import static com.philips.platform.referenceapp.utils.PushNotificationConstants.IS_TOKEN_REGISTERED;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * @author Ritesh.jha@philips.com
 * <p>
 * Test cases for PushNotificationManager.java
 */

@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({PreferenceManager.class, TextUtils.class})
public class PushNotificationManagerTest {
    private static final String TAG = "PushNotificationTest";
    private static final String PUSH_NOTIFICATION_TOKEN = "Push_Notification_Token";
    private PushNotificationManager pushNotificationManager;
    private PushNotificationUserRegistationWrapperInterface pnUserRegistrationInterface = null;
    private TextUtils textUtils;

    private Context context;
    private AppInfraInterface appInfraInterface = null;
    private AppInfra appInfra = null;
    private PushNotificationTokenRegistrationInterface pushNotificationTokenRegistrationInterface;
    private SecureStorageInterface secureStorageInterfaceMock;
    private SecureStorageInterface.SecureStorageError secureStorageErrorMock;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setUp() throws Exception {
        mockStatic(PreferenceManager.class);
        mockStatic(TextUtils.class);

        initMocks(this);
        initialMocking();
        PowerMockito.when(appInfra.getSecureStorage()).thenReturn(secureStorageInterfaceMock);
        /*  Whitebox -> Various utilities for accessing internals of a class.
         *  invokeConstructor -> Invoke a constructor. Useful for testing classes with a private constructor.
         */
        try {
            pushNotificationManager = Whitebox.invokeConstructor(PushNotificationManager.class);
        } catch (Exception e) {
            PNLog.d(TAG, "Registering component for handling payload");
        }
        pushNotificationManager.init(appInfra, pnUserRegistrationInterface);
        PNLog.disablePNLogging();
    }

    private void initialMocking() {
        context = PowerMockito.mock(Context.class);
        appInfraInterface = PowerMockito.mock(AppInfraInterface.class);
        appInfra = PowerMockito.mock(AppInfra.class);
        secureStorageInterfaceMock = PowerMockito.mock(SecureStorageInterface.class);
        secureStorageErrorMock = PowerMockito.mock(SecureStorageInterface.SecureStorageError.class);
        textUtils = PowerMockito.mock(TextUtils.class);
        pnUserRegistrationInterface = PowerMockito.mock(PushNotificationUserRegistationWrapperInterface.class);
        pushNotificationTokenRegistrationInterface = PowerMockito.mock(PushNotificationTokenRegistrationInterface.class);
    }

    @Test
    public void testGetTokenNotEmpty() {
        SecureStorageInterface.SecureStorageError secureStorageErrorMock = PowerMockito.mock(SecureStorageInterface.SecureStorageError.class);
        PowerMockito.when(secureStorageInterfaceMock.fetchValueForKey(GCM_TOKEN, secureStorageErrorMock)).thenReturn(PUSH_NOTIFICATION_TOKEN);
        assertEquals(PUSH_NOTIFICATION_TOKEN, pushNotificationManager.getToken(secureStorageErrorMock));
    }

    @Test
    public void testGetPushNotificationUserRegistationWrapperInterface() {
        pushNotificationManager.init(appInfraInterface, pnUserRegistrationInterface);
        assertEquals(pnUserRegistrationInterface, pushNotificationManager.getPushNotificationUserRegistationWrapperInterface());
    }

    @Test
    public void testGetTokenWhenEmpty() {
        SecureStorageInterface.SecureStorageError secureStorageErrorMock = PowerMockito.mock(SecureStorageInterface.SecureStorageError.class);
        PowerMockito.when(secureStorageInterfaceMock.fetchValueForKey(GCM_TOKEN, secureStorageErrorMock)).thenReturn("");
        assertEquals("", pushNotificationManager.getToken(secureStorageErrorMock));
    }

    @Test
    public void testStartPushNotificationRegistrationWhenTokenEmpty() {
        setExpectationTrueWhenPreferenceIsEmpty(GCM_TOKEN);
        setExpectationTrueWhenPreferenceIsEmpty(IS_TOKEN_REGISTERED);

        pushNotificationManager.startPushNotificationRegistration(context, secureStorageErrorMock);

        ServiceController<TestService> controller;
        controller = Robolectric.buildService(TestService.class);
        RegistrationIntentService service = controller.create().get();
        Intent intent = new Intent(RuntimeEnvironment.application, TestService.class);
        service.onStart(intent, 0);
        assertEquals(TestService.class.getName(), intent.getComponent().getClassName());
    }

    private void setExpectationTrueWhenPreferenceIsEmpty(String key) {
        PowerMockito.when(secureStorageInterfaceMock.fetchValueForKey(key, pushNotificationManager.getSecureStorageError())).thenReturn("");
        PowerMockito.when(textUtils.isEmpty("")).thenReturn(true);
    }

    @Test
    public void testStartPushNotificationRegistrationRegisterToken() {
        setExpectationFalseWhenPreferenceIsEmpty(GCM_TOKEN);
        MockInternetReacheablity();

        PushNotificationUserRegistationWrapperInterface pushNotificationUserRegistationWrapperInterface =
                getPushNotificationUserRegistationWrapperInterface();

        pushNotificationManager.init(appInfra, pushNotificationUserRegistationWrapperInterface);
        PNLog.disablePNLogging();

        final Boolean[] isRegisterTokenApiInvoked = {false};

        PushNotificationTokenRegistrationInterface pushNotificationTokenRegistrationInterface =
                getRegistrationInterfaceRegisterToken(isRegisterTokenApiInvoked);

        final Boolean[] isResponseSuccess = {false};

        RegistrationCallbacks.RegisterCallbackListener registerCallbackListener = getRegisterCallbackListener(isResponseSuccess);

        pushNotificationManager.registerForTokenRegistration(pushNotificationTokenRegistrationInterface, registerCallbackListener);

        pushNotificationManager.startPushNotificationRegistration(context, secureStorageErrorMock);

        assertTrue(isRegisterTokenApiInvoked[0]);
        assertTrue(isResponseSuccess[0]);
    }

    @NonNull
    private PushNotificationUserRegistationWrapperInterface getPushNotificationUserRegistationWrapperInterface() {
        return appContext -> true;
    }

    @NonNull
    private RegistrationCallbacks.RegisterCallbackListener getRegisterCallbackListener(final Boolean[] isResponseSuccess) {
        return new RegistrationCallbacks.RegisterCallbackListener() {
            @Override
            public void onResponse(boolean isRegistered) {
                isResponseSuccess[0] = true;
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                isResponseSuccess[0] = false;
            }
        };
    }

    @Test
    public void testStartPushNotificationRegistrationPlatformInstanceIDListenerService() throws Exception {
        ServiceController<TestPlatformFirebaseMessagingService> controller;
        controller = Robolectric.buildService(TestPlatformFirebaseMessagingService.class);
        TestPlatformFirebaseMessagingService service = controller.create().get();
        Intent intent = new Intent(RuntimeEnvironment.application, TestPlatformFirebaseMessagingService.class);
        service.onStartCommand(intent, 0, 0);
        assertEquals(TestPlatformFirebaseMessagingService.class.getName(), intent.getComponent().getClassName());
    }

    @Test
    public void testSendPayloadToCoCo() {
        final Boolean[] isResponseSuccess = {false};

        HandleNotificationPayloadInterface handleNotificationPayloadInterface =
                handleNotificationPayloadInterfaceForSuccess(isResponseSuccess);

        pushNotificationManager.registerForTokenRegistration(pushNotificationTokenRegistrationInterface);
        pushNotificationManager.registerForPayload(handleNotificationPayloadInterface);

        String jsonData = getJsonString();
        Map<String,String> bundle = getBundle(jsonData, PushNotificationConstants.PLATFORM_KEY);
        PNLog.disablePNLogging();
        pushNotificationManager.sendPayloadToCoCo(bundle);
        assertTrue(isResponseSuccess[0]);

        bundle = getBundle(jsonData, "non_platform");
        pushNotificationManager.sendPayloadToCoCo(bundle);
        assertFalse(isResponseSuccess[0]);
    }

    @NonNull
    private HandleNotificationPayloadInterface handleNotificationPayloadInterfaceForSuccess(final Boolean[] isResponseSuccess) {
        return new HandleNotificationPayloadInterface() {
            @Override
            public void handlePayload(JSONObject payloadObject) {
                isResponseSuccess[0] = true;
            }

            @Override
            public void handlePushNotification(String message) {
                isResponseSuccess[0] = false;
            }
        };
    }

    @NonNull
    private String getJsonString() {
        return "{ \"dsc\": { \"dataSync\": \"moment\" } }";
    }

    @NonNull
    protected Map<String,String> getBundle(String dataWithCorrectJson, String key) {
        Map<String,String> bundle = new HashMap<>();
        bundle.put(key, dataWithCorrectJson);
        return bundle;
    }

    @Test
    public void testRegisterTokenWithBackendWhenTokenRegistrationIsTrue() {
        setExpectationFalseWhenPreferenceIsEmpty(GCM_TOKEN);
        setExpectationFalseWhenPreferenceIsEmpty(IS_TOKEN_REGISTERED);

        final Boolean[] isRegisterTokenApiInvoked = {false};

        PushNotificationTokenRegistrationInterface pushNotificationTokenRegistrationInterface =
                getRegistrationInterfaceRegisterToken(isRegisterTokenApiInvoked);

        final Boolean[] isResponseSuccess = {false};

        RegistrationCallbacks.RegisterCallbackListener registerCallbackListener =
                getRegisterCallbackListener(isResponseSuccess);

        pushNotificationManager.registerForTokenRegistration(pushNotificationTokenRegistrationInterface, registerCallbackListener);
        pushNotificationManager.registerTokenWithBackend(context, secureStorageErrorMock);

        assertTrue(isRegisterTokenApiInvoked[0]);
        assertTrue(isResponseSuccess[0]);
    }

    @NonNull
    private PushNotificationTokenRegistrationInterface getRegistrationInterfaceRegisterToken(final Boolean[] isRegisterTokenApiInvoked) {
        return new PushNotificationTokenRegistrationInterface() {
            @Override
            public void registerToken(String deviceToken, String appVariant, String protocolProvider, RegistrationCallbacks.RegisterCallbackListener registerCallbackListener) {
                isRegisterTokenApiInvoked[0] = true;
                registerCallbackListener.onResponse(true);
            }

            @Override
            public void deregisterToken(String appToken, String appVariant, RegistrationCallbacks.DergisterCallbackListener dergisterCallbackListener) {
                isRegisterTokenApiInvoked[0] = false;
                dergisterCallbackListener.onResponse(false);
            }
        };
    }

    private void setExpectationFalseWhenPreferenceIsEmpty(String key) {
        PowerMockito.when(secureStorageInterfaceMock.fetchValueForKey(key, pushNotificationManager.getSecureStorageError())).thenReturn("");
        PowerMockito.when(textUtils.isEmpty("")).thenReturn(false);
    }

    @Test
    public void testRegisterTokenWithBackendWhenTokenRegistrationErrorCondition() {
        setExpectationFalseWhenPreferenceIsEmpty(GCM_TOKEN);

        final Boolean[] isRegisterTokenApiInvoked = {false};

        PushNotificationTokenRegistrationInterface pushNotificationTokenRegistrationInterface =
                getRegistrationInterfaceRegisterTokenErrorCondition(isRegisterTokenApiInvoked);

        final Boolean[] isResponseSuccess = {false};

        RegistrationCallbacks.RegisterCallbackListener registerCallbackListener =
                getRegisterCallbackListenerErrorCondition(isResponseSuccess);

        pushNotificationManager.registerForTokenRegistration(pushNotificationTokenRegistrationInterface, registerCallbackListener);
        pushNotificationManager.registerTokenWithBackend(context, secureStorageErrorMock);

        assertTrue(isRegisterTokenApiInvoked[0]);
        assertTrue(isResponseSuccess[0]);
    }

    @NonNull
    private RegistrationCallbacks.RegisterCallbackListener getRegisterCallbackListenerErrorCondition(final Boolean[] isResponseSuccess) {
        return new RegistrationCallbacks.RegisterCallbackListener() {
            @Override
            public void onResponse(boolean isRegistered) {
                isResponseSuccess[0] = false;
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                isResponseSuccess[0] = true;
            }
        };
    }

    @NonNull
    private PushNotificationTokenRegistrationInterface getRegistrationInterfaceRegisterTokenErrorCondition(final Boolean[] isRegisterTokenApiInvoked) {
        return new PushNotificationTokenRegistrationInterface() {
            @Override
            public void registerToken(String deviceToken, String appVariant, String protocolProvider, RegistrationCallbacks.RegisterCallbackListener registerCallbackListener) {
                isRegisterTokenApiInvoked[0] = true;
                registerCallbackListener.onError(1, "dummy");
            }

            @Override
            public void deregisterToken(String appToken, String appVariant, RegistrationCallbacks.DergisterCallbackListener dergisterCallbackListener) {
                isRegisterTokenApiInvoked[0] = false;
                dergisterCallbackListener.onResponse(false);
            }
        };
    }

    @Test
    public void testDegisterTokenWithBackend() {
        setExpectationFalseWhenPreferenceIsEmpty(GCM_TOKEN);
        MockInternetReacheablity();

        pushNotificationManager.init(appInfra, pnUserRegistrationInterface);
        PNLog.disablePNLogging();

        final Boolean[] isDeregisterTokenApiInvoked = {false};

        PushNotificationTokenRegistrationInterface pushNotificationTokenRegistrationInterface =
                getDeregistrationInterfaceRegisterToken(isDeregisterTokenApiInvoked);

        final Boolean[] isResponseSuccess = {false};

        PushNotificationManager.DeregisterTokenListener deregisterTokenListener =
                getDeregisterTokenListener(isResponseSuccess);

        pushNotificationManager.registerForTokenRegistration(pushNotificationTokenRegistrationInterface);
        pushNotificationManager.deregisterTokenWithBackend(deregisterTokenListener, secureStorageErrorMock);

        assertTrue(isDeregisterTokenApiInvoked[0]);
        assertTrue(isResponseSuccess[0]);
    }

    @NonNull
    private PushNotificationManager.DeregisterTokenListener getDeregisterTokenListener(final Boolean[] isResponseSuccess) {
        return new
                PushNotificationManager.DeregisterTokenListener() {
                    @Override
                    public void onSuccess() {
                        isResponseSuccess[0] = true;
                    }

                    @Override
                    public void onError() {
                        isResponseSuccess[0] = false;
                    }
                };
    }

    @NonNull
    private PushNotificationTokenRegistrationInterface getDeregistrationInterfaceRegisterToken(final Boolean[] isDeregisterTokenApiInvoked) {
        return new PushNotificationTokenRegistrationInterface() {
            @Override
            public void registerToken(String deviceToken, String appVariant, String protocolProvider, RegistrationCallbacks.RegisterCallbackListener registerCallbackListener) {
                isDeregisterTokenApiInvoked[0] = false;
                registerCallbackListener.onResponse(false);
            }

            @Override
            public void deregisterToken(String appToken, String appVariant, RegistrationCallbacks.DergisterCallbackListener dergisterCallbackListener) {
                isDeregisterTokenApiInvoked[0] = true;
                dergisterCallbackListener.onResponse(true);
            }
        };
    }

    @Test
    public void testDegisterTokenWithBackendForErrorCondition() {
        setExpectationFalseWhenPreferenceIsEmpty(GCM_TOKEN);
        MockInternetReacheablity();
        pushNotificationManager.init(appInfra, pnUserRegistrationInterface);
        PNLog.disablePNLogging();

        final Boolean[] isDeregisterTokenApiInvoked = {false};

        PushNotificationTokenRegistrationInterface pushNotificationTokenRegistrationInterface =
                new PushNotificationTokenRegistrationInterface() {
                    @Override
                    public void registerToken(String deviceToken, String appVariant, String protocolProvider, RegistrationCallbacks.RegisterCallbackListener registerCallbackListener) {
                        isDeregisterTokenApiInvoked[0] = false;
                        registerCallbackListener.onResponse(false);
                    }

                    @Override
                    public void deregisterToken(String appToken, String appVariant, RegistrationCallbacks.DergisterCallbackListener dergisterCallbackListener) {
                        isDeregisterTokenApiInvoked[0] = true;
                        dergisterCallbackListener.onResponse(false);
                    }
                };

        final Boolean[] isResponseSuccess = {false};

        PushNotificationManager.DeregisterTokenListener deregisterTokenListener = new
                PushNotificationManager.DeregisterTokenListener() {
                    @Override
                    public void onSuccess() {
                        isResponseSuccess[0] = false;
                    }

                    @Override
                    public void onError() {
                        isResponseSuccess[0] = true;
                    }
                };


        pushNotificationManager.registerForTokenRegistration(pushNotificationTokenRegistrationInterface);
        pushNotificationManager.deregisterTokenWithBackend(deregisterTokenListener, secureStorageErrorMock);

        assertTrue(isDeregisterTokenApiInvoked[0]);
        assertTrue(isResponseSuccess[0]);
    }

    private void MockInternetReacheablity() {
        RestInterface restInterface = PowerMockito.mock(RestInterface.class);
        PowerMockito.when(appInfra.getRestClient()).thenReturn(restInterface);
        PowerMockito.when(restInterface.isInternetReachable()).thenReturn(true);
    }

    @After
    public void tearDown() {
        PNLog.disablePNLogging();
        pushNotificationManager.deregisterForTokenRegistration();
        pushNotificationManager.deRegisterForPayload();
        pushNotificationManager = null;
    }

    private static class TestService extends RegistrationIntentService {
        @Override
        public void onStart(Intent intent, int startId) {
            // same logic as in internal ServiceHandler.handleMessage()
            // but runs on same thread as Service
            onHandleIntent(intent);
            stopSelf(startId);
        }
    }

    private static class TestPlatformFirebaseMessagingService extends PlatformFirebaseMessagingService {

        @Override
        public void onNewToken(String token) {
            super.onNewToken(token);
            PNLog.d(TAG, token);
        }

        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);
        }


    }
}