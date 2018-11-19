/*
 * Copyright (c) Koninklijke Philips N.V., 2016.
 * All rights reserved.
 */

package com.philips.cdp2.bluelib.demouapp.devicedefs;

import android.os.Handler;

import com.philips.pins.shinelib.SHNCapabilityType;
import com.philips.pins.shinelib.SHNCentral;
import com.philips.pins.shinelib.SHNDevice;
import com.philips.pins.shinelib.SHNDeviceDefinitionInfo;
import com.philips.pins.shinelib.SHNDeviceImpl;
import com.philips.pins.shinelib.capabilities.CapabilityFirmwareUpdateDiComm;
import com.philips.pins.shinelib.capabilities.SHNCapabilityDeviceInformation;
import com.philips.pins.shinelib.capabilities.SHNCapabilityDeviceInformationCached;
import com.philips.pins.shinelib.capabilities.SHNCapabilityDeviceInformationImpl;
import com.philips.pins.shinelib.capabilities.SHNCapabilityFirmwareUpdate;
import com.philips.pins.shinelib.dicommsupport.DiCommChannel;
import com.philips.pins.shinelib.dicommsupport.ports.DiCommFirmwarePort;
import com.philips.pins.shinelib.protocols.moonshinestreaming.SHNProtocolByteStreamingVersionSwitcher;
import com.philips.pins.shinelib.protocols.moonshinestreaming.SHNProtocolMoonshineStreaming;
import com.philips.pins.shinelib.services.SHNServiceDeviceInformation;
import com.philips.pins.shinelib.services.SHNServiceMoonshineStreaming;
import com.philips.pins.shinelib.services.SHNServiceMoonshineUnencryptedStreaming;
import com.philips.pins.shinelib.utility.DeviceInformationCache;
import com.philips.pins.shinelib.utility.PersistentStorageFactory;
import com.philips.pins.shinelib.wrappers.SHNDeviceWrapper;

class DeviceDefinitionAurora implements SHNDeviceDefinitionInfo.SHNDeviceDefinition {

    public static final int RESPONSE_TIME_OUT = 4000;

    @Override
    public SHNDevice createDeviceFromDeviceAddress(String deviceAddress, SHNDeviceDefinitionInfo shnDeviceDefinitionInfo, SHNCentral shnCentral) {
        // using existing building block from BlueLib. It is also possible to create custom implementation inside the plugin
        SHNDeviceImpl device = new SHNDeviceImpl(shnCentral.getBTDevice(deviceAddress), shnCentral, shnDeviceDefinitionInfo.getDeviceTypeName(), SHNDeviceImpl.SHNBondInitiator.NONE);

        // only registered capabilities are exposed to the user of the plugin
        registerDeviceInformationCapability(shnCentral, device);
        registerFirmwareUpdateCapability(shnCentral.getInternalHandler(), device);

        // the SHNDevice implementation needs to be wrapped for thread safety
        return new SHNDeviceWrapper(device);
    }

    private void registerDeviceInformationCapability(SHNCentral shnCentral, SHNDeviceImpl device) {
        SHNServiceDeviceInformation shnServiceDeviceInformation = new SHNServiceDeviceInformation();
        // it is important to register the service for SHNDeviceImpl. SHNDeviceImpl is waiting for all registered services to indicate 'ready' before changing state to 'Connected'
        device.registerService(shnServiceDeviceInformation);

        SHNCapabilityDeviceInformation capabilityDeviceInformation = new SHNCapabilityDeviceInformationImpl(shnServiceDeviceInformation);
        // register the capability directly
        // device.registerCapability(capabilityDeviceInformation, SHNCapabilityType.DEVICE_INFORMATION);
        // or use SHNCapabilityDeviceInformationCached. SHNCapabilityDeviceInformationCached is a wrapper around SHNCapabilityDeviceInformation that provides access to device information then device is not connected.
        // SHNCapabilityDeviceInformationCached stores  device information in SharedPreferences.
        PersistentStorageFactory persistentStorageFactory = shnCentral.getPersistentStorageFactory();
        DeviceInformationCache deviceInformationCache = new DeviceInformationCache(persistentStorageFactory.getPersistentStorageForDevice(device));
        SHNCapabilityDeviceInformationCached deviceInformationCached = new SHNCapabilityDeviceInformationCached(capabilityDeviceInformation, shnServiceDeviceInformation, deviceInformationCache);
        device.registerCapability(deviceInformationCached, SHNCapabilityType.DEVICE_INFORMATION);
    }

    private void registerFirmwareUpdateCapability(Handler internalHandler, SHNDeviceImpl device) {
        SHNServiceMoonshineStreaming moonshineStreaming = new SHNServiceMoonshineUnencryptedStreaming();
        // it is important to register the service for SHNDeviceImpl. SHNDeviceImpl is waiting for all registered services to indicate 'ready' before changing state to 'Connected'
        device.registerService(moonshineStreaming);

        SHNProtocolMoonshineStreaming shnProtocolMoonshineStreaming = new SHNProtocolByteStreamingVersionSwitcher(moonshineStreaming, internalHandler);
        // Let the protocol know about service state changes
        moonshineStreaming.setShnServiceMoonshineStreamingListener(shnProtocolMoonshineStreaming);

        // create DiComm port and channel
        DiCommChannel commChannel = new DiCommChannel(shnProtocolMoonshineStreaming, RESPONSE_TIME_OUT);
        DiCommFirmwarePort firmwarePort = new DiCommFirmwarePort(internalHandler);
        // add supported ports to the channel
        commChannel.addPort(firmwarePort);

        // register the capability
        SHNCapabilityFirmwareUpdate capabilityFirmwareUpdate = new CapabilityFirmwareUpdateDiComm(firmwarePort, internalHandler);
        device.registerCapability(capabilityFirmwareUpdate, SHNCapabilityType.FIRMWARE_UPDATE);
    }
}
