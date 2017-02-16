/*
 * (C) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.commlib.core.port.firmware.state;

import com.philips.commlib.core.port.firmware.operation.FirmwareUpdatePushLocal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.philips.cdp.dicommclient.util.DICommLog.disableLogging;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FirmwareUpdateStateIdleTest {

    @Mock
    private FirmwareUpdatePushLocal mockOperation;

    private FirmwareUpdateStateIdle stateUnderTest;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        disableLogging();

        stateUnderTest = new FirmwareUpdateStateIdle(mockOperation);
    }

    @Test
    public void onStartFromChecking_DeployFinished() {

        stateUnderTest.start(new FirmwareUpdateStateChecking(mockOperation));

        verify(mockOperation).onDeployFinished();
    }
}