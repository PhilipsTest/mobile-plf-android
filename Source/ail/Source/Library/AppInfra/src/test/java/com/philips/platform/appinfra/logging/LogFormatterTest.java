/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.platform.appinfra.logging;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.timesync.TimeInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class LogFormatterTest {

    private LogFormatter logFormatter;

    @Mock
    private AppInfraInterface appInfra;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        logFormatter = new LogFormatter(appInfra);
    }

    @Test
    public void testLogFormat() {
        TimeInterface timeInterface = mock(TimeInterface.class);
        when(appInfra.getTime()).thenReturn(timeInterface);
        Date value = new Date();
        when(timeInterface.getUTCTime()).thenReturn(value);
        LogRecord logRecord = mock(LogRecord.class);
        when(logRecord.getLevel()).thenReturn(Level.FINE);

        Object[] params = new Object[5];
        params[0] = "eventId";
        params[1] = "eventName";
        params[2] = "eventVersion";
        when(logRecord.getParameters()).thenReturn(params);
        String format = logFormatter.format(logRecord);
        assertNotNull(format);
        assertTrue(format.contains("eventId"));
        assertTrue(format.contains("eventName"));
        assertTrue(format.contains("eventVersion"));
        assertNotNull(logFormatter.getHead(new ConsoleHandler()));
        assertNotNull(logFormatter.getTail(new CloudLogHandler(appInfra)));
    }
}