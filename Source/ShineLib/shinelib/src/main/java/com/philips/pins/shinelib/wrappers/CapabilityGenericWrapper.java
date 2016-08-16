/*
 * Copyright (c) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.pins.shinelib.wrappers;

import android.os.Handler;
import android.support.annotation.NonNull;
import com.philips.pins.shinelib.SHNDataRawResultListener;
import com.philips.pins.shinelib.SHNResult;
import com.philips.pins.shinelib.capabilities.CapabilityGeneric;
import com.philips.pins.shinelib.datatypes.SHNDataRaw;
import java.util.UUID;

public class CapabilityGenericWrapper implements CapabilityGeneric, CapabilityGeneric.CapabilityGenericListener {
    private final CapabilityGeneric wrappedShnCapability;
    private final Handler userHandler;
    private final Handler internalHandler;
    private CapabilityGeneric.CapabilityGenericListener capabilityGenericListener;

    public CapabilityGenericWrapper(CapabilityGeneric shnCapability, Handler internalHandler, Handler userHandler) {
        wrappedShnCapability = shnCapability;
        this.userHandler = userHandler;
        this.internalHandler = internalHandler;
        wrappedShnCapability.setCapabilityGenericListener(this);
    }



    @Override
    public void readCharacteristic(final SHNDataRawResultListener listener, final UUID uuid) {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                wrappedShnCapability.readCharacteristic(new SHNDataRawResultListener() {

                    @Override
                    public void onActionCompleted(final SHNDataRaw value, @NonNull final SHNResult result) {
                        Runnable resultRunnable = new Runnable() {
                            @Override
                            public void run() {
                                listener.onActionCompleted(value, result);
                            }
                        };
                        userHandler.post(resultRunnable);
                    }
                }, uuid);
            }
        };
        internalHandler.post(command);
    }

    @Override
    public void onReadCompleted(final UUID aChar, final byte[] data) {
        Runnable callback = new Runnable() {
            @Override
            public void run() {
                if (capabilityGenericListener != null) {
                    capabilityGenericListener.onReadCompleted(aChar, data);
                }
            }
        };
        userHandler.post(callback);
    }

    @Override
    public void writeCharacteristic(final SHNDataRawResultListener listener, final UUID uuid, final byte[] data) {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                wrappedShnCapability.writeCharacteristic(new SHNDataRawResultListener() {

                    @Override
                    public void onActionCompleted(final SHNDataRaw value, @NonNull final SHNResult result) {
                        Runnable resultRunnable = new Runnable() {
                            @Override
                            public void run() {
                                listener.onActionCompleted(value, result);
                            }
                        };
                        userHandler.post(resultRunnable);
                    }
                }, uuid, data);
            }
        };
        internalHandler.post(command);
    }

    @Override
    public void onWriteCompleted(final UUID aChar, final byte[] data) {
        Runnable callback = new Runnable() {
            @Override
            public void run() {
                if (capabilityGenericListener != null) {
                    capabilityGenericListener.onWriteCompleted(aChar, data);
                }
            }
        };
        userHandler.post(callback);
    }

    @Override
    public void setCapabilityGenericListener(CapabilityGenericListener genericCapabilityListener) {
        this.capabilityGenericListener = genericCapabilityListener;
    }
}
