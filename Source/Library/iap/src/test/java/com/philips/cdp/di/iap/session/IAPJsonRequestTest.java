/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.session;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.philips.cdp.di.iap.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static junit.framework.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class IAPJsonRequestTest {
    private IAPJsonRequest mIAIapJsonRequest;

    @Before
    public void setUp() {
        mIAIapJsonRequest = new IAPJsonRequest(1,null,null,null,null);
    }

    @Test
    public void testParseNetworkResponse(){
        NetworkResponse networkResponse = new NetworkResponse(fileToByteArray("C:\\Users\\310228564\\InAppGit\\Source\\Library\\iap\\src\\test\\java\\com\\philips\\cdp\\di\\iap\\session\\server_error.txt"));
        assertNotNull(mIAIapJsonRequest.parseNetworkResponse(networkResponse));
    }

    @Test
    public void testParseNetworkResponseWithException(){
        NetworkResponse networkResponse = new NetworkResponse(fileToByteArray("C:\\Users\\310228564\\InAppGit\\Source\\Library\\iap\\src\\test\\java\\com\\philips\\cdp\\di\\iap\\session\\wrong_json.txt"));
        assertNotNull(mIAIapJsonRequest.parseNetworkResponse(networkResponse));
    }

    @Test(expected = NullPointerException.class)
    public void testHandleMiscErrorWithAuthFailure(){
        TestUtils.getStubbedHybrisDelegate();
        mIAIapJsonRequest.handleMiscErrors(new AuthFailureError());
    }

    private byte[] fileToByteArray(String path) {
        File file = new File(path);
        byte[] byteArray = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 8];
            int bytesRead;
            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }
            byteArray = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }
}