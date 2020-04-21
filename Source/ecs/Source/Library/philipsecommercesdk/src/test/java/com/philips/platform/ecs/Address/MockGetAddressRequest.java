package com.philips.platform.ecs.Address;

import com.android.volley.VolleyError;
import com.philips.platform.ecs.TestUtil;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.request.GetAddressRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

public class MockGetAddressRequest extends GetAddressRequest {

    String jsonfileName;

    public MockGetAddressRequest(String jsonFileName,ECSCallback<List<ECSAddress>, Exception> ecsCallback) {
        super(ecsCallback);
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
