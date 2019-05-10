/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.platform.appinfra.tagging;

import com.philips.platform.appinfra.AppInfraLogEventID;
import com.philips.platform.appinfra.logging.LoggingInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static com.philips.platform.appinfra.tagging.AppInfraTaggingUtil.SEND_DATA;
import static com.philips.platform.appinfra.tagging.AppInfraTaggingUtil.SUCCESS_MESSAGE;
import static com.philips.platform.appinfra.tagging.AppInfraTaggingUtil.TECHNICAL_ERROR;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class AppInfraTaggingUtilTest {

    private AppInfraTaggingUtil appInfraTaggingUtil;
    private AppTaggingInterface appTaggingInterface;
    private LoggingInterface loggingInterface;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        appTaggingInterface = mock(AppTaggingInterface.class);
        loggingInterface = mock(LoggingInterface.class);
        appInfraTaggingUtil = new AppInfraTaggingUtil(appTaggingInterface, loggingInterface);
    }

    @Test
    public void testErrorAction() throws Exception {
        String message = " some message";
        appInfraTaggingUtil.trackErrorAction(AppInfraTaggingUtil.SERVICE_DISCOVERY, message);
        verify(appTaggingInterface).trackActionWithInfo(SEND_DATA, TECHNICAL_ERROR,"AIL:".concat(AppInfraTaggingUtil.SERVICE_DISCOVERY).concat(":").concat(message));
        verify(loggingInterface).log(LoggingInterface.LogLevel.DEBUG, AppInfraLogEventID.AI_SERVICE_DISCOVERY, "AIL:".concat(AppInfraTaggingUtil.SERVICE_DISCOVERY).concat(":").concat(message));
    }

}