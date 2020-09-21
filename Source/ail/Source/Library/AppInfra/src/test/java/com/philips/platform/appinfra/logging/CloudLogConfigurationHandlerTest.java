/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.platform.appinfra.logging;

import com.philips.platform.appinfra.AppInfraInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class CloudLogConfigurationHandlerTest {


    private CloudLogConfigHandler cloudLogConfigHandler;
    @Mock
    private AppInfraInterface appInfra;
    @Mock
    private LoggingConfiguration loggingConfiguration;
    @Mock
    private LogFormatter logFormatter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        cloudLogConfigHandler = new CloudLogConfigHandler(appInfra);
    }

    @Test
    public void testHandleConsoleLogConfig() {
        Logger logger = Logger.getLogger("component");
        logger.setLevel(Level.ALL);
        when(loggingConfiguration.isCloudLogEnabled()).thenReturn(true);
        when(loggingConfiguration.getLogLevel()).thenReturn("All");
        cloudLogConfigHandler.handleCloudLogConfig(loggingConfiguration, logger);
        assertTrue(logger.getHandlers().length == 1);
        Handler handler = logger.getHandlers()[0];
        assertTrue(handler instanceof CloudLogHandler);
        assertTrue(handler.getLevel() == Level.ALL);
    }

    @Test
    public void testNotToAddHandlerWhenDisabled() {
        Logger logger = Logger.getLogger("component");
        logger.setLevel(Level.OFF);
        when(loggingConfiguration.isCloudLogEnabled()).thenReturn(false);
        when(loggingConfiguration.getLogLevel()).thenReturn("All");
        cloudLogConfigHandler.handleCloudLogConfig(loggingConfiguration, logger);
        assertTrue(logger.getHandlers().length == 0);
    }
}