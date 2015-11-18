/*
 * Copyright (c) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.pins.shinelib.capabilities;

import com.philips.pins.shinelib.SHNCapability;
import com.philips.pins.shinelib.SHNResultListener;

/**
 * Created by 310188215 on 03/03/15.
 */
public interface SHNCapabilityNotifications extends SHNCapability {
    public  enum SHNNotificationType {
        SHNNotificationTypeEmail
    }

    void showNotificationForType(SHNNotificationType shnNotificationType, SHNResultListener shnResultListener);
    void hideNotificationForType(SHNNotificationType shnNotificationType, SHNResultListener shnResultListener);
}
