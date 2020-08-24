/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package cdp.cdp.philips.com.prxclientlib;

import android.util.Log;

import com.philips.cdp.prxclient.datamodels.assets.Asset;
import com.philips.cdp.prxclient.datamodels.assets.AssetModel;
import com.philips.cdp.prxclient.datamodels.assets.Assets;
import com.philips.cdp.prxclient.datamodels.assets.Data;
import com.philips.cdp.prxclient.response.ResponseData;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 09-Nov-15.
 */
public class AssetModelTest {

    private static final String TAG = AssetModelTest.class.getSimpleName();
    private final String mCode = "RQ1250_17";
    private final String mDescription = "User manual";
    private final String mExtension = "pdf";
    private final String mExtent = "36822233";
    private final String mLocale = "en_GB";
    private final String mNumber = "001";
    private final String mType = "DFU";
    private final String mAssetResource = "http://download.p4c.philips.com/files/r/rq1250_17/rq1250_17_dfu_eng.pdf";
    private Asset mAssetObject = null;
    private Assets mAssetsObject = null;
    private Data mData = null;
    private AssetModel mAssetModel = null;

    @Before
    public void setUp() throws Exception {
        mAssetObject = new Asset("code", "description","extension","extent",
                 "lastModified","locale", "number", "type","asset");

        loadResources();
    }

    @Test
    public void testAssetDataLoad() throws Exception {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getInstrumentation().getContext().getResources().getAssets().open("asset_template_one.txt")));

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
        AssetModel mAssetModel = new AssetModel();
        ResponseData responseData = mAssetModel.parseJsonResponseData(mJsonObject);
        assertNotNull(responseData);
    }

    @Test
    public void testAssetDataSuccess() throws Exception {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getInstrumentation().getContext().getResources().getAssets().open("asset_template_one.txt")));

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
        AssetModel assetModel = new AssetModel();
        ResponseData responseData = assetModel.parseJsonResponseData(mJsonObject);
        AssetModel assetModelObject = (AssetModel) responseData;
        assertTrue(assetModelObject.isSuccess());
    }

    @Test
    public void testAssetResourceData() {
        assertEquals(mAssetObject.getAsset(), "http://download.p4c.philips.com/files/r/rq1250_17/rq1250_17_dfu_eng.pdf");
    }

    @Test
    public void testAssetCodeData() {
        assertEquals(mAssetObject.getCode(), "RQ1250_17");
    }

    @Test
    public void testAssetDescription() {
        assertEquals(mAssetObject.getDescription(), "User manual");
    }

    @Test
    public void testAssetExtensionData() {
        assertEquals(mAssetObject.getExtension(), "pdf");
    }

    @Test
    public void testAssetExtentData() {
        assertEquals(mAssetObject.getExtent(), "36822233");
    }

    @Test
    public void testAssetLastModifiedData() {
        assertEquals(mAssetObject.getLastModified(), "2015-11-09");
    }

    @Test
    public void testAssetLocaleData() {
        assertEquals(mAssetObject.getLocale(), "en_GB");
    }

    @Test
    public void testAssetNumberData() {
        assertEquals(mAssetObject.getNumber(), "001");
    }

    @Test
    public void testAssetTypeData() {
        assertEquals(mAssetObject.getType(), "DFU");
    }

    @Test
    public void testAssetDataModelWithMultiContructor() {
        Asset asset = new Asset(mAssetResource, mCode, mDescription, mExtension, mExtent, mLocale, mNumber, mType, mAssetResource);

        assertNotNull(asset.getLastModified());
    }

    @Test
    public void testAssetsDataModelTest() {
        List<Asset> list = new ArrayList<Asset>();
        list.add(mAssetObject);
        list.add(mAssetObject);
        mAssetsObject = new Assets(list);
        assertEquals(mAssetsObject.getAsset().size(), 2);
    }

    @Test
    public void testAssetsDataModelTest2() {
        List<Asset> list = new ArrayList<Asset>();
        list.add(mAssetObject);
        list.add(mAssetObject);
        mAssetsObject = new Assets();
        mAssetsObject.setAsset(list);
        assertEquals(mAssetsObject.getAsset().size(), 2);
    }

    @Test
    public void testDataModelObject() {
        testAssetsDataModelTest();
        mData = new Data(mAssetsObject);
        assertNotNull(mData.getAssets());
    }

    @Test
    public void testDataModelwithAssets() {
        testAssetsDataModelTest();
        mData = new Data();
        mData.setAssets(mAssetsObject);
        assertNotNull(mData.getAssets());
    }

    @Test
    public void testAssetModelExistanceTest() {
        testDataModelObject();
        mAssetModel = new AssetModel();
        mAssetModel.setData(mData);
        mAssetModel.setSuccess(false);
        assertFalse(mAssetModel.isSuccess());
    }

    @Test
    public void testAssetModelDataLoaderTest() {
        testDataModelObject();
        mAssetModel = new AssetModel(true, mData);
        mAssetModel.setData(mData);
        mAssetModel.setSuccess(true);
        assertEquals(mData, mAssetModel.getData());
    }

    private void loadResources() {
        mAssetObject.setCode(mCode);
        mAssetObject.setDescription(mDescription);
        mAssetObject.setExtension(mExtension);
        mAssetObject.setExtent(mExtent);
        String mLastModified = "2015-11-09";
        mAssetObject.setLastModified(mLastModified);
        mAssetObject.setLocale(mLocale);
        mAssetObject.setNumber(mNumber);
        mAssetObject.setType(mType);
        mAssetObject.setAsset(mAssetResource);
    }
}
