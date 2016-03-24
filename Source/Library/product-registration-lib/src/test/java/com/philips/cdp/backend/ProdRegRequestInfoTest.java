package com.philips.cdp.backend;

import com.philips.cdp.MockitoTestCase;

import org.junit.Test;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class ProdRegRequestInfoTest extends MockitoTestCase {

    ProdRegRequestInfo prodRegRequestInfo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        prodRegRequestInfo = new ProdRegRequestInfo(null, null, null, null);
    }
    @Test
    public void testGetSerialNumber() throws Exception {
        prodRegRequestInfo.setSerialNumber("1344");
        assertEquals("1344", prodRegRequestInfo.getSerialNumber());
    }
    @Test
    public void testGetPurchaseDate() throws Exception {
        prodRegRequestInfo.setPurchaseDate("28-02-2016");
        assertEquals("28-02-2016", prodRegRequestInfo.getPurchaseDate());
    }
    @Test
    public void testGetLocale() throws Exception {
        prodRegRequestInfo.setLocale("en_GB");
        assertEquals("en_GB", prodRegRequestInfo.getLocale());
    }
}