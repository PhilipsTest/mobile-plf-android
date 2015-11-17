package com.philips.pins.shinelib.capabilities;

import com.philips.pins.shinelib.SHNCapability;
import com.philips.pins.shinelib.SHNFirmwareInfoResultListener;
import com.philips.pins.shinelib.SHNResult;

/**
 * Created by 310188215 on 16/06/15.
 */
public interface SHNCapabilityFirmwareUpdate extends SHNCapability {

    enum SHNFirmwareUpdateState {
        SHNFirmwareUpdateStateIdle,
        SHNFirmwareUpdateStatePreparing,
        SHNFirmwareUpdateStateUploading,
        SHNFirmwareUpdateStateVerifying,
        SHNFirmwareUpdateStateDeploying;
    }

    boolean supportsUploadWithoutDeploy();

    void uploadFirmware(byte[] firmwareData);

    void abortFirmwareUpload();

    void deployFirmware();

    void getUploadedFirmwareInfo(SHNFirmwareInfoResultListener shnFirmwareInfoResultListener);

    void setSHNCapabilityFirmwareUpdateListener(SHNCapabilityFirmwareUpdateListener shnCapabilityFirmwareUpdateListener);

    SHNFirmwareUpdateState getState();

    interface SHNCapabilityFirmwareUpdateListener {
        void onStateChanged(SHNCapabilityFirmwareUpdate shnCapabilityFirmwareUpdate);
        void onProgressUpdate(SHNCapabilityFirmwareUpdate shnCapabilityFirmwareUpdate, float progress);
        void onUploadFailed(SHNCapabilityFirmwareUpdate shnCapabilityFirmwareUpdate, SHNResult shnResult);
        void onUploadFinished(SHNCapabilityFirmwareUpdate shnCapabilityFirmwareUpdate);
        void onDeployFailed(SHNCapabilityFirmwareUpdate shnCapabilityFirmwareUpdate, SHNResult shnResult);
        void onDeployFinished(SHNCapabilityFirmwareUpdate shnCapabilityFirmwareUpdate);
    }
}
