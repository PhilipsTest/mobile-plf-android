/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.platform.appinfra.logging;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.timesync.TimeInterface;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.logging.Level;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class LoggingUtilsTest {

    @Test
    public void testLogLevel() {
        assertEquals(Level.SEVERE, LoggingUtils.getJavaLoggerLogLevel("error"));
        assertEquals(Level.WARNING, LoggingUtils.getJavaLoggerLogLevel("Warning"));
        assertEquals(Level.INFO, LoggingUtils.getJavaLoggerLogLevel("info"));
        assertEquals(Level.CONFIG, LoggingUtils.getJavaLoggerLogLevel("debug"));
        assertEquals(Level.FINE, LoggingUtils.getJavaLoggerLogLevel("verbose"));
        assertEquals(Level.FINE, LoggingUtils.getJavaLoggerLogLevel("all"));
        assertEquals(Level.FINE, LoggingUtils.getJavaLoggerLogLevel("fine"));
        assertEquals(Level.OFF, LoggingUtils.getJavaLoggerLogLevel("off"));
    }

    @Test
    public void testAILogLevel() {
        assertEquals(LoggingUtils.getAILLogLevel("SEVERE"), "ERROR");
        assertEquals(LoggingUtils.getAILLogLevel("WARNING"), "WARNING");
        assertEquals(LoggingUtils.getAILLogLevel("INFO"), "INFO");
        assertEquals(LoggingUtils.getAILLogLevel("CONFIG"), "DEBUG");
        assertEquals(LoggingUtils.getAILLogLevel("FINE"), "VERBOSE");
        assertEquals(LoggingUtils.getAILLogLevel("OFF"), "OFF");
        assertEquals(LoggingUtils.getAILLogLevel(""), "VERBOSE");
    }

    @Test
    public void testUUID() {
        assertNotNull(LoggingUtils.getUUID());
    }

    @Test
    public void testGetStringLengthInBytes() {
        assertEquals(LoggingUtils.getStringLengthInBytes(""), 0);
        assertEquals(LoggingUtils.getStringLengthInBytes("hi"), 4);
    }

    @Test
    public void testConvertObjectToJsonString() {
        assertNotNull(LoggingUtils.convertObjectToJsonString("data"));
        assertNull(LoggingUtils.convertObjectToJsonString(null));
    }

    @Test
    public void testGetCurrentDateAndTime() {
        AppInfraInterface appInfraInterfaceMock = mock(AppInfraInterface.class);
        TimeInterface timeInterfaceMock = mock(TimeInterface.class);
        Date dateMock = mock(Date.class);
        when(timeInterfaceMock.getUTCTime()).thenReturn(dateMock);
        when(appInfraInterfaceMock.getTime()).thenReturn(timeInterfaceMock);
        assertNotNull(LoggingUtils.getCurrentDateAndTime("yyyy:dd:mm", appInfraInterfaceMock));
    }

    @Test
    public void testgetFormattedDateAndTime() {
        assertNotNull(LoggingUtils.getFormattedDateAndTime(123456789,"yyyy:dd:mm"));
    }
}