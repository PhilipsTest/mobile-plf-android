package com.philips.pins.shinelib.wrappers;

import android.os.Handler;

import com.philips.pins.shinelib.SHNCapability;
import com.philips.pins.shinelib.SHNCapabilityType;
import com.philips.pins.shinelib.capabilities.SHNCapabilityBattery;
import com.philips.pins.shinelib.capabilities.SHNCapabilityConfigWearingPosition;
import com.philips.pins.shinelib.capabilities.SHNCapabilityDataStreaming;
import com.philips.pins.shinelib.capabilities.SHNCapabilityDeviceInformation;
import com.philips.pins.shinelib.capabilities.SHNCapabilityFirmwareUpdate;
import com.philips.pins.shinelib.capabilities.SHNCapabilityLogSynchronization;
import com.philips.pins.shinelib.capabilities.SHNCapabilityNotifications;
import com.philips.pins.shinelib.capabilities.SHNCapabilityUserInformationLifeSense;

/**
 * Created by 310188215 on 29/04/15.
 */
public class SHNCapabilityWrapperFactory {
    public static SHNCapability createCapabilityWrapper(SHNCapability shnCapability, SHNCapabilityType shnCapabilityType, Handler internalHandler, Handler userHandler) {
        SHNCapability shnCapabilityWrapper;
        switch (shnCapabilityType) {
            case Notifications:
                shnCapabilityWrapper = new SHNCapabilityNotificationsWrapper(
                        (SHNCapabilityNotifications) shnCapability,
                        internalHandler,
                        userHandler);
                break;
            case DeviceInformation:
                shnCapabilityWrapper = new SHNCapabilityDeviceInformationWrapper(
                        (SHNCapabilityDeviceInformation) shnCapability,
                        internalHandler,
                        userHandler);
                break;
            case DataStreaming:
                shnCapabilityWrapper = new SHNCapabilityDataStreamingWrapper(
                        (SHNCapabilityDataStreaming) shnCapability,
                        internalHandler,
                        userHandler);
                break;
            case LogSynchronization:
                shnCapabilityWrapper = new SHNCapabilityLogSynchronizationWrapper(
                        (SHNCapabilityLogSynchronization) shnCapability,
                        internalHandler,
                        userHandler);
                break;
            case Battery:
                shnCapabilityWrapper = new SHNCapabilityBatteryWrapper(
                        (SHNCapabilityBattery) shnCapability,
                        internalHandler,
                        userHandler);
                break;
            case UserInformationLifeSense:
                shnCapabilityWrapper = new SHNCapabilityUserInformationLifeSenseWrapper(
                        (SHNCapabilityUserInformationLifeSense) shnCapability,
                        internalHandler,
                        userHandler);
                break;
            case WearingPosition:
                shnCapabilityWrapper = new SHNCapabilityConfigWearingPositionWrapper(
                        (SHNCapabilityConfigWearingPosition) shnCapability,
                        internalHandler,
                        userHandler);
                break;
            case FirmwareUpdate:
                shnCapabilityWrapper = new SHNCapabilityFirmwareUpdateWrapper(
                        (SHNCapabilityFirmwareUpdate) shnCapability,
                        internalHandler,
                        userHandler);
                break;
            default:
                throw new IllegalStateException("No wrapper for capability: " + shnCapabilityType);
        }
        return shnCapabilityWrapper;
    }
}
