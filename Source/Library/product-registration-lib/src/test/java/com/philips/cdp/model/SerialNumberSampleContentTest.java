package com.philips.cdp.model;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class SerialNumberSampleContentTest extends TestCase {

    ProdRegSerNumbSampleContent serialNumberSampleContentTest;

    @Override
    public void setUp() throws Exception {
        serialNumberSampleContentTest = new ProdRegSerNumbSampleContent();
    }
    @Test
    public void ShouldGetTitle() throws Exception {
        serialNumberSampleContentTest.setTitle("Find the serial number");
        assertEquals("Find the serial number", serialNumberSampleContentTest.getTitle());
    }
    @Test
    public void ShouldGetAsset() throws Exception {

        serialNumberSampleContentTest.setAsset("/consumerfiles/assets/img/registerproducts/HC.jpg");
        assertEquals("/consumerfiles/assets/img/registerproducts/HC.jpg", serialNumberSampleContentTest.getAsset());
    }
    @Test
    public void testGetSnExample() throws Exception {
        serialNumberSampleContentTest.setSnExample("Example: 1344");
        assertEquals("Example: 1344", serialNumberSampleContentTest.getSnExample());
    }
    @Test
    public void ShouldGetSnFormat() throws Exception {
        serialNumberSampleContentTest.setSnFormat("cc");
        assertEquals("cc", serialNumberSampleContentTest.getSnFormat());
    }
}