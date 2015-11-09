package com.philips.pins.shinelib.datatypes;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class SHNLogTest {

    @Test
    public void whenStartAndEndDateAreNullThenGetDurationReturns0() {
        SHNLog shnLog = new SHNLog(null, null, "", new ArrayList<SHNLogItem>(), new HashSet<SHNDataType>());

        assertEquals(0, shnLog.getDurationMS());
    }

    @Test
    public void whenStartDateAreNullThenGetDurationReturns0() {
        SHNLog shnLog = new SHNLog(new Date(), null, "", new ArrayList<SHNLogItem>(), new HashSet<SHNDataType>());

        assertEquals(0, shnLog.getDurationMS());
    }

    @Test
    public void whenEndDateAreNullThenGetDurationReturns0() {
        SHNLog shnLog = new SHNLog(null, new Date(), "", new ArrayList<SHNLogItem>(), new HashSet<SHNDataType>());

        assertEquals(0, shnLog.getDurationMS());
    }
}