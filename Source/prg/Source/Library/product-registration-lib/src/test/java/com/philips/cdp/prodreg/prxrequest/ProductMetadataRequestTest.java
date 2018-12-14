package com.philips.cdp.prodreg.prxrequest;


import com.philips.cdp.prxclient.PrxConstants;
import com.philips.cdp.prxclient.request.RequestType;

import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.Mock;

/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
public class ProductMetadataRequestTest extends TestCase {

    ProductMetadataRequest productMetadataRequest;
    @Mock
    String mCtn = "HD8967/01";
    String mSerialNumber = "1344";
    PrxConstants.Sector sector;
    PrxConstants.Catalog catalog;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sector = PrxConstants.Sector.B2C;
        catalog = PrxConstants.Catalog.CONSUMER;
        productMetadataRequest = new ProductMetadataRequest(mCtn, mSerialNumber,sector,catalog);
    }

    public void testGetRequestType() throws Exception {
        int mIntType = productMetadataRequest.getRequestType();
        assertEquals(RequestType.GET.getValue(), mIntType);
    }

//    public void testGetServerInfo() {
//        productMetadataRequest = new ProductMetadataRequest(mCtn) {
//            @Override
//            protected String getRegistrationEnvironment() {
//                return Configuration.PRODUCTION.name();
//            }
//        };
//        final String serverInfo = productMetadataRequest.getServerInfo();
//        assertEquals(serverInfo, "https://www.philips.com/prx/registration/");
//    }

    @Test
    public void testGetRequestTimeOut() throws Exception {
        assertEquals(productMetadataRequest.getRequestTimeOut(), 30000);
    }
}