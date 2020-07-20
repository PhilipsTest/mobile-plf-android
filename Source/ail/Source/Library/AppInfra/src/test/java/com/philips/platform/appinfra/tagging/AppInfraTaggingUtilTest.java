/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.platform.appinfra.tagging;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.AppInfraLogEventID;
import com.philips.platform.appinfra.logging.LoggingInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

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
        appInfraTaggingUtil = new AppInfraTaggingUtil((AppInfraInterface) appTaggingInterface, loggingInterface);
    }

    @Test
    public void testErrorAction() throws Exception {
        String message = " some message";
        appInfraTaggingUtil.trackErrorAction(AppInfraTaggingUtil.SERVICE_DISCOVERY, message);
        verify(appTaggingInterface).trackErrorAction(ErrorCategory.TECHNICAL_ERROR, new TaggingError(AppInfraTaggingUtil.SERVICE_DISCOVERY + ":" + message));
//        verify(appTaggingInterface).trackActionWithInfo(SEND_DATA, ErrorCategory.TECHNICAL_ERROR,"ail:".concat(AppInfraTaggingUtil.SERVICE_DISCOVERY).concat(":").concat(message));
        verify(loggingInterface).log(LoggingInterface.LogLevel.DEBUG, AppInfraLogEventID.AI_SERVICE_DISCOVERY, "ail:".concat(AppInfraTaggingUtil.SERVICE_DISCOVERY).concat(":").concat(message));
    }

}