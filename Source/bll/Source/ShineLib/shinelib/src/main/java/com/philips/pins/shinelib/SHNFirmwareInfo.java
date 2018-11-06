/*
 * Copyright (c) Koninklijke Philips N.V., 2015, 2016, 2017.
 * All rights reserved.
 */

package com.philips.pins.shinelib;

/**
 * Interface that provides information about a firmware image during firmware upload.
 *
 * @publicApi
 */
public class SHNFirmwareInfo {

    /**
     * Possible state of firmware image on a peripheral.
     */
    public enum SHNFirmwareState {
        /**
         * No image is available or being uploaded.
         */
        Idle,
        /**
         * An image is uploading.
         */
        Uploading,
        /**
         * Image is successfully uploaded and ready to be deployed.
         */
        ReadyToDeploy,
        /**
         * Uploaded image is invalid.
         */
        InvalidImage
    }

    private String version;
    private SHNFirmwareState state;

    public SHNFirmwareInfo(String version, SHNFirmwareState state) {
        this.version = version;
        this.state = state;
    }

    /**
     * @return a string representation of the firmware version for the being uploaded or already uploaded firmware image.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the current state of the firmware image.
     *
     * @return current state of the firmware image.
     */
    public SHNFirmwareState getState() {
        return state;
    }
}
