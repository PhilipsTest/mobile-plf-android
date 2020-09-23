/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package cdp.cdp.philips.com.prxclientlib;

import android.util.Log;

import com.philips.cdp.prxclient.request.ProductAssetRequest;
import com.philips.cdp.prxclient.request.ProductSummaryRequest;
import com.philips.cdp.prxclient.request.PrxRequest;
import com.philips.cdp.prxclient.response.ResponseData;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;

/**
 * Description :
 * Project : PRX Common Component.
 */
public class ProductSummaryRequestTest {

    private static final String TAG = ProductAssetRequestTest.class.getSimpleName();
    private PrxRequest mProductAssetBuilder = null;

    @Before
    public void setUp() throws Exception {
        mProductAssetBuilder = new ProductSummaryRequest("125", null, null, null);
    }

    @Test
    public void testPrxBuilderObjectWithQueueParameter() {
        mProductAssetBuilder = new ProductAssetRequest("125", null, null, "TAGINFO");
        assertNotNull(mProductAssetBuilder);
    }

    @Test
    public void testSummaryResponseSuccess() throws Exception {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getInstrumentation().getContext().getResources().getAssets().open("summary_template_one.txt")));

            // do reading, usually loop until end of file reading
            String mLine = reader.readLine();
            while (mLine != null) {
                // process line
                sb.append(mLine);
                mLine = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            // log the exception
            Log.d(TAG, "Error in Input file ");
        }
        JSONObject mJsonObject = new JSONObject(sb.toString());
        //ResponseData mResponseData = mProductAssetBuilder.getResponseData(mJsonObject);
        assertNotNull(mJsonObject);
    }

    @Test
    public void testSummaryResponseObject() throws Exception {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getInstrumentation().getContext().getResources().getAssets().open("summary_template_one.txt")));

            // do reading, usually loop until end of file reading
            String mLine = reader.readLine();
            while (mLine != null) {
                // process line
                sb.append(mLine);
                mLine = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            // log the exception
            Log.d(TAG, "Error in Input file ");
        }
        JSONObject mJsonObject = new JSONObject(sb.toString());
        ResponseData mResponseData = mProductAssetBuilder.getResponseData(mJsonObject);
        assertNotNull(mResponseData);
    }

}
