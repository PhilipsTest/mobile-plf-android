package com.philips.cdp.prodreg.register;

import com.philips.cdp.localematch.enums.Catalog;
import com.philips.cdp.localematch.enums.Sector;
import com.philips.cdp.prodreg.MockitoTestCase;
import com.philips.cdp.prodreg.RegistrationState;

import org.mockito.Mock;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class RegisteredProductTest extends MockitoTestCase {

    RegisteredProduct registeredProduct;
    @Mock
    String mCTN, mSerialNumber, mPurchdate;
    @Mock
    Sector mSector;
    @Mock
    Catalog mCatalog;

    @Override
    protected void setUp() throws Exception {
        registeredProduct = new RegisteredProduct(mCTN, mSector, mCatalog);
    }

    public void testSetRegistrationState() throws Exception {
        registeredProduct.setRegistrationState(RegistrationState.FAILED);
    }

    public void testGetEndWarrantyDate() throws Exception {
        registeredProduct.setEndWarrantyDate("29/4/2016");
        assertEquals("29/4/2016", registeredProduct.getEndWarrantyDate());
    }

    public void testSetEndWarrantyDate() throws Exception {
        registeredProduct.setEndWarrantyDate("29/4/2016");
    }

    public void testGetUserUUid() throws Exception {
        registeredProduct.setUserUUid("ABCD");
        assertEquals("ABCD", registeredProduct.getUserUUid());
    }

    public void testGetContractNumber() throws Exception {
        registeredProduct.setContractNumber("900000");
        assertEquals("900000", registeredProduct.getContractNumber());
    }

    public void testSetContractNumber() throws Exception {
        registeredProduct.setContractNumber("900000");
    }

    public void testIsShouldConsiderUUID() {
        assertFalse(registeredProduct.isShouldConsiderUUID("abc", "abc", true, true));
        assertFalse(registeredProduct.isShouldConsiderUUID("abc", "abc", true, false));
        assertTrue(registeredProduct.isShouldConsiderUUID("abcd", "abc", true, true));
    }

    public void testEqual() {
        assertFalse(registeredProduct.equals(registeredProduct.isShouldConsiderUUID("abc", "abc", true, true)));
    }
}