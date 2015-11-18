/*
 * Copyright (c) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.pins.shinelib.capabilities;

import com.philips.pins.shinelib.SHNCapability;
import com.philips.pins.shinelib.SHNStringResultListener;

/**
 * Created by 310188215 on 03/03/15.
 */
public interface SHNCapabilityDeviceInformation extends SHNCapability {
    enum SHNDeviceInformationType {
        ManufacturerName,
        ModelNumber,
        SerialNumber,
        HardwareRevision,
        FirmwareRevision,
        SoftwareRevision,
        SystemID,
        DeviceCloudComponentVersion,
        DeviceCloudComponentName,
        CTN,
        Unknown
    }

    void readDeviceInformation(SHNDeviceInformationType shnDeviceInformationType, SHNStringResultListener shnStringResultListener);
}
