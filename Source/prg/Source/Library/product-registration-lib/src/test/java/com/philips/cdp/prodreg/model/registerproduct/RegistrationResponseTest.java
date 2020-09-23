package com.philips.cdp.prodreg.model.registerproduct;


import com.philips.cdp.prodreg.prxrequest.RegistrationRequest;
import com.philips.cdp.prxclient.PrxConstants;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;

/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
public class RegistrationResponseTest extends TestCase {

    RegistrationResponse registrationResponse;
    @Mock
    RegistrationResponseData data;
    String mCTN = "HC5450/83", mSerialNumber = "123456", mAccessToken;
    RegistrationRequest mRegistrationRequest;
    private String TAG = getClass() + "";
    private String jsonData = "{\"success\": true,\"data\":{\"locale\": \"en_GB\",\"modelNumber\": \"HC5450/83\",\"registrationDate\": \"2014-11-26\",\"dateOfPurchase\": \"2014-06-12\",\"warrantyEndDate\": \"2019-06-12\",\"contractNumber\": \"CQ5A000ef\",\"productRegistrationUuid\": \"eb26c6d8-693f-4ec0-be60-2c603eaad8a3\",\"emailStatus\": \"success\"}}";
    PrxConstants.Sector sector;
    PrxConstants.Catalog catalog;

    @Override
    public void setUp() throws Exception {
        registrationResponse = new RegistrationResponse();

        sector = PrxConstants.Sector.B2C;
        catalog = PrxConstants.Catalog.CONSUMER;

        mRegistrationRequest = new RegistrationRequest(mCTN, mSerialNumber, sector,catalog, true);
    }

    public void testSetData() throws Exception {
        registrationResponse.setData(data);
    }

    public void testGetData() throws Exception {
        registrationResponse.setData(data);
        assertEquals(data, registrationResponse.getData());
    }

    @Test
    public void testSummaryResponseObject() {
        try {
            RegistrationResponse registrationResponse = (RegistrationResponse) mRegistrationRequest.getResponseData(new JSONObject(jsonData));
            RegistrationResponseData mResponseData = registrationResponse.getData();
            assertNotNull(mResponseData);

            RegistrationResponseData data = setDataObject(mResponseData);

            TestAssertionOnResponse(mResponseData, data);
        } catch (Exception e) {
        }
    }

    @Test
    private void TestAssertionOnResponse(final RegistrationResponseData mResponseData, final RegistrationResponseData data) {
        assertEquals(mResponseData.getLocale(), data.getLocale());
        assertEquals(mResponseData.getModelNumber(), data.getModelNumber());
        assertEquals(mResponseData.getRegistrationDate(), data.getRegistrationDate());
        assertEquals(mResponseData.getDateOfPurchase(), data.getDateOfPurchase());
        assertEquals(mResponseData.getWarrantyEndDate(), data.getWarrantyEndDate());
        assertEquals(mResponseData.getContractNumber(), data.getContractNumber());
        assertEquals(mResponseData.getProductRegistrationUuid(), data.getProductRegistrationUuid());
        assertEquals(mResponseData.getEmailStatus(), data.getEmailStatus());
    }

    @Test
    private RegistrationResponseData setDataObject(final RegistrationResponseData mResponseData) {
        RegistrationResponseData data = new RegistrationResponseData();
        data.setLocale(mResponseData.getLocale());
        data.setModelNumber(mResponseData.getModelNumber());
        data.setRegistrationDate(mResponseData.getRegistrationDate());
        data.setDateOfPurchase(mResponseData.getDateOfPurchase());
        data.setWarrantyEndDate(mResponseData.getWarrantyEndDate());
        data.setContractNumber(mResponseData.getContractNumber());
        data.setProductRegistrationUuid(mResponseData.getProductRegistrationUuid());
        data.setEmailStatus(mResponseData.getEmailStatus());
        return data;
    }
}