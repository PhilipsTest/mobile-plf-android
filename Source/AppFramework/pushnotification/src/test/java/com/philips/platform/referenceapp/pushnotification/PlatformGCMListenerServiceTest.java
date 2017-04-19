package com.philips.platform.referenceapp.pushnotification;

import android.content.Intent;
import android.os.Bundle;

import com.philips.platform.referenceapp.BuildConfig;
import com.philips.platform.referenceapp.PlatformGCMListenerService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ServiceController;
import org.robolectric.annotation.Config;

/**
 * Created by philips on 14/04/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 24)
public class PlatformGCMListenerServiceTest {
    private PlatformGCMListenerService service;
    private ServiceController<PlatformGCMListenerService> controller;
    @Before
    public void setUp() {
        controller = Robolectric.buildService(PlatformGCMListenerService.class);
        service = controller.attach().create().get();
    }

    @Test
    public void testWithIntent() {
        Intent intent = new Intent(RuntimeEnvironment.application, PlatformGCMListenerServiceOveriden.class);
        // add extras to intent
        controller.withIntent(intent).startCommand(0, 0);
        // assert here
    }

    @After
    public void tearDown() {
        controller.destroy();
    }

    public static class PlatformGCMListenerServiceOveriden extends PlatformGCMListenerService {

        @Override
        public void onStart(Intent intent, int startId) {
            // same logic as in internal ServiceHandler.handleMessage()
            // but runs on same thread as Service
            onMessageReceived("",new Bundle());
        }
    }

}