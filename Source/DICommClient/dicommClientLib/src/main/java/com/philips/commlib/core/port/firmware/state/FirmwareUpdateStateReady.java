/*
 * Copyright (c) Koninklijke Philips N.V. 2017
 * All rights reserved.
 */
package com.philips.commlib.core.port.firmware.state;

import android.support.annotation.NonNull;

import com.philips.commlib.core.port.firmware.operation.FirmwareUpdatePushLocal;
import com.philips.commlib.core.port.firmware.util.StateWaitException;

import static com.philips.commlib.core.port.firmware.FirmwarePortProperties.FirmwarePortState.PROGRAMMING;

public class FirmwareUpdateStateReady extends CancelableFirmwareUpdateState {

    public FirmwareUpdateStateReady(@NonNull FirmwareUpdatePushLocal operation) {
        super(operation);
    }

    @Override
    public void onStart(FirmwareUpdateState previousState) {
        operation.onDownloadFinished();
    }

    @Override
    public void deploy() {
        operation.requestState(PROGRAMMING);
        try {
            operation.waitForNextState();
        } catch (StateWaitException e) {
            operation.onDeployFailed("Deployment failed.");
            operation.finish();
        }
    }
}
