package com.philips.platform.ecs.ProductDetail;

import com.android.volley.VolleyError;
import com.philips.platform.ecs.TestUtil;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.asset.Assets;
import com.philips.platform.ecs.request.GetProductAssetRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class MockGetProductAssetRequest extends GetProductAssetRequest {
    String jsonfileName;
    public MockGetProductAssetRequest(String jsonFileName, String assetUrl, ECSCallback<Assets, Exception> ecsCallback) {
        super(assetUrl, ecsCallback);
        this.jsonfileName=jsonFileName;
    }


    @Override
    public void executeRequest() {

        JSONObject result = null;
        InputStream in = getClass().getClassLoader().getResourceAsStream(jsonfileName);//"PRXProductAssets.json"
        String jsonString = TestUtil.loadJSONFromFile(in);
        try {
            result = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            VolleyError volleyError = new VolleyError(ECSErrorEnum.ECSsomethingWentWrong.toString());
            onErrorResponse(volleyError);
        }
        onResponse(result);

    }
}
