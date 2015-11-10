package prxclient.cdp.philips.com.prxclientlib;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.philips.cdp.prxclient.prxdatabuilder.ProductAssetBuilder;
import com.philips.cdp.prxclient.prxdatabuilder.PrxDataBuilder;
import com.philips.cdp.prxclient.prxdatamodels.assets.Asset;
import com.philips.cdp.prxclient.prxdatamodels.assets.AssetModel;
import com.philips.cdp.prxclient.prxdatamodels.assets.Assets;
import com.philips.cdp.prxclient.response.ResponseData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 09-Nov-15.
 */
public class AssetModelTest extends InstrumentationTestCase {

    private static final String TAG = AssetModelTest.class.getSimpleName();
    PrxDataBuilder mProductAssetBuilder = null;
    Asset mAssetObject = null;
    Assets mAssetsObject = null;

    String mCode = "RQ1250_17";
    String mDescription = "User manual";
    String mExtension = "pdf";
    String mExtent = "36822233";
    String mLastModified = "2015-11-09";
    String mLocale = "en_GB";
    String mNumber = "001";
    String mType = "DFU";
    String mAssetResource = "http://download.p4c.philips.com/files/r/rq1250_17/rq1250_17_dfu_eng.pdf";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mProductAssetBuilder = new ProductAssetBuilder("125", null);
        mProductAssetBuilder.setmCatalogCode("COnsumer");
        mProductAssetBuilder.setmLocale("nl_NL");
        mProductAssetBuilder.setmSectorCode("HAIR");

        mAssetObject = new Asset();
        loadResources();

    }


    public void loadResources() {

        mAssetObject.setCode(mCode);
        mAssetObject.setDescription(mDescription);
        mAssetObject.setExtension(mExtension);
        mAssetObject.setExtent(mExtent);
        mAssetObject.setLastModified(mLastModified);
        mAssetObject.setLocale(mLocale);
        mAssetObject.setNumber(mNumber);
        mAssetObject.setType(mType);
        mAssetObject.setAsset(mAssetResource);
    }

    public void testAssetDataLoad() {
        JSONObject mJsonObject = null;
        try {
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
                e.printStackTrace();
            }
            Log.d(TAG, "Parsed Data : " + sb.toString());
            mJsonObject = new JSONObject(sb.toString());
            ResponseData mResponseData = mProductAssetBuilder.getResponseData(mJsonObject);
            AssetModel mAssetModel = new AssetModel();
            ResponseData responseData = mAssetModel.parseJsonResponseData(mJsonObject);
            assertNotNull(responseData);
        } catch (JSONException e) {
            Log.d(TAG, "JSON : " + e);

        } catch (Exception e) {
            Log.d(TAG, "IO " + e);
        }
    }

    public void testAssetDataSuccess() {
        JSONObject mJsonObject = null;
        try {
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
                e.printStackTrace();
            }
            Log.d(TAG, "Parsed Data : " + sb.toString());
            mJsonObject = new JSONObject(sb.toString());
            ResponseData mResponseData = mProductAssetBuilder.getResponseData(mJsonObject);
            AssetModel assetModel = new AssetModel();
            ResponseData responseData = assetModel.parseJsonResponseData(mJsonObject);
            AssetModel assetModelObject = (AssetModel) responseData;
            assertEquals(assetModelObject.isSuccess(), true);
        } catch (JSONException e) {
            Log.d(TAG, "JSON : " + e);

        } catch (Exception e) {
            Log.d(TAG, "IO " + e);
        }
    }


    public void testAssetResourceData() {
        assertEquals(mAssetObject.getAsset(), "http://download.p4c.philips.com/files/r/rq1250_17/rq1250_17_dfu_eng.pdf");
    }

    public void testAssetCodeData() {
        assertEquals(mAssetObject.getCode(), "RQ1250_17");

    }

    public void testAssetDescription() {
        assertEquals(mAssetObject.getDescription(), "User manual");

    }


    public void testAssetExtensionData() {
        assertEquals(mAssetObject.getExtension(), "pdf");
    }

    public void testAssetExtentData() {

        assertEquals(mAssetObject.getExtent(), "36822233");

    }

    public void testAssetLastModifiedData() {
        assertEquals(mAssetObject.getLastModified(), "2015-11-09");
    }

    public void testAssetLocaleData() {
        assertEquals(mAssetObject.getLocale(), "en_GB");
    }

    public void testAssetNumberData() {
        assertEquals(mAssetObject.getNumber(), "001");
    }

    public void testAssetTypeData() {
        assertEquals(mAssetObject.getType(), "DFU");
    }

    public void testAssetDataModelWithMultiContructor() {
        Asset asset = new Asset(mAssetResource, mCode, mDescription, mExtension, mExtent, mLocale, mNumber, mType, mAssetResource);

        assertNotNull(asset.getLastModified());
    }


    public void testAssetsDataModelTest() {

        List<Asset> list = new ArrayList<Asset>();
        list.add(mAssetObject);
        list.add(mAssetObject);
        mAssetsObject = new Assets(list);
        assertEquals(mAssetsObject.getAsset().size(), 2);
    }



    public void testAssetsDataModelTest2() {

        List<Asset> list = new ArrayList<Asset>();
        list.add(mAssetObject);
        list.add(mAssetObject);
        mAssetsObject = new Assets();
        mAssetsObject.setAsset(list);
        assertEquals(mAssetsObject.getAsset().size(), 2);
    }


}
