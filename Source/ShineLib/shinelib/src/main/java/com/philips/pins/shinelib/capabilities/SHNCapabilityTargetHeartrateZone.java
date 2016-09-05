/*
 * Copyright (c) Koninklijke Philips N.V., 2015, 2016.
 * All rights reserved.
 */

package com.philips.pins.shinelib.capabilities;

import com.philips.pins.shinelib.SHNCapability;
import com.philips.pins.shinelib.SHNResultListener;

public interface SHNCapabilityTargetHeartrateZone extends SHNCapability {
    void setTargetZone(short minHeartRate, short maxHeartRate, SHNResultListener shnResultListener);
}
