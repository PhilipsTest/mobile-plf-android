/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp.prodreg.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class ProdRegUtilTest {

    private ProdRegUtil prodRegUtil;

    @Before
    public void setUp() throws Exception {
        prodRegUtil = new ProdRegUtil();
    }

    @Test
    public void testReturnTrueForValidDate() throws Exception {
        assertTrue(prodRegUtil.isValidDate("2016-03-22"));
        assertFalse(prodRegUtil.isValidDate(""));
        assertFalse(prodRegUtil.isValidDate(null));
    }

    @Test
    public void testIsFutureDate() {
        String date = "2098-05-22";
        assertTrue(prodRegUtil.isFutureDate(date));
        String date1 = "2016-05-16";
        assertFalse(prodRegUtil.isFutureDate(date1));
    }

    @Test
    public void testReturnFalseForInValidDate() throws Exception {
        assertFalse(prodRegUtil.isValidDate("1998-03-22"));
        assertTrue(prodRegUtil.isValidDate("2098-05-22"));
    }

    @Test
    public void testGettingMinDate() {
        assertTrue(prodRegUtil.getMinDate() != 0);
    }

    @Test
    public void testGettingValidatedString() {
        int value = 5;
        String data = prodRegUtil.getValidatedString(value);
        assertEquals("05", data);

        int value2 = 15;
        String data2 = prodRegUtil.getValidatedString(value2);
        assertEquals("15", data2);
    }

    @Test
    public void testIsValidSerialNumber() {
        String serialNumber = "124";
        assertTrue(prodRegUtil.isValidSerialNumber(true, "[0-9][0-9][0-9]", serialNumber));
        assertFalse(prodRegUtil.isValidSerialNumber(true, "[0-9][0-9][0-9]", ""));
        assertTrue(prodRegUtil.isValidSerialNumber(false, "[0-9][0-9][0-9]", ""));
    }
}