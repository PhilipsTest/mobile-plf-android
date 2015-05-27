package com.pins.philips.shinelib;

/**
 * Created by 310188215 on 02/03/15.
 */
public interface SHNAssociationProcedure {
    public interface SHNAssociationProcedureListener {
        void onStopScanRequest();
        void onAssociationSuccess(SHNDevice shnDevice);
        void onAssociationFailed(SHNDevice shnDevice);
    }

    boolean getShouldScan();
    void deviceDiscovered(SHNDevice shnDevice, SHNBLEAdvertisementData shnbleAdvertisementData, int rssi);
}
