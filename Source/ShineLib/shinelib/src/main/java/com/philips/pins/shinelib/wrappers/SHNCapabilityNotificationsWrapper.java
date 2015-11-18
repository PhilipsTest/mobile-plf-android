/*
 * Copyright (c) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.pins.shinelib.wrappers;

import android.os.Handler;

import com.philips.pins.shinelib.SHNResult;
import com.philips.pins.shinelib.SHNResultListener;
import com.philips.pins.shinelib.capabilities.SHNCapabilityNotifications;

/**
 * Created by 310188215 on 29/04/15.
 */
public class SHNCapabilityNotificationsWrapper implements SHNCapabilityNotifications {

    private final Handler internalHandler;
    private final Handler userHandler;
    private final SHNCapabilityNotifications wrappedShnCapabilityNotifications;

    public SHNCapabilityNotificationsWrapper(SHNCapabilityNotifications shnCapabilityNotifications, Handler internalHandler, Handler userHandler) {
        this.internalHandler = internalHandler;
        this.userHandler = userHandler;
        wrappedShnCapabilityNotifications = shnCapabilityNotifications;
    }

    // implements SHNCapabilityNotifications
    @Override
    public void showNotificationForType(final SHNNotificationType shnNotificationType, final SHNResultListener shnResultListener) {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                wrappedShnCapabilityNotifications.showNotificationForType(shnNotificationType, new SHNResultListener() {
                    @Override
                    public void onActionCompleted(final SHNResult result) {
                        Runnable resultRunnable = new Runnable() {
                            @Override
                            public void run() {
                                shnResultListener.onActionCompleted(result);
                            }
                        };
                        userHandler.post(resultRunnable);
                    }
                });
            }
        };
        internalHandler.post(command);
    }

    @Override
    public void hideNotificationForType(final SHNNotificationType shnNotificationType, final SHNResultListener shnResultListener) {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                wrappedShnCapabilityNotifications.hideNotificationForType(shnNotificationType, new SHNResultListener() {
                    @Override
                    public void onActionCompleted(final SHNResult result) {
                        Runnable resultRunnable = new Runnable() {
                            @Override
                            public void run() {
                                shnResultListener.onActionCompleted(result);
                            }
                        };
                        userHandler.post(resultRunnable);
                    }
                });
            }
        };
        internalHandler.post(command);
    }
}
