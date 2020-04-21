package com.philips.platform.ecs.Config;

import com.android.volley.VolleyError;
import com.philips.platform.ecs.TestUtil;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.config.ECSConfig;
import com.philips.platform.ecs.request.GetConfigurationRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class MockGetConfigurationRequest extends GetConfigurationRequest {

    private   String jsonFile;

    public MockGetConfigurationRequest(String jsonFile, ECSCallback<ECSConfig, Exception> eCSCallback) {
        super(eCSCallback);
        this.jsonFile = jsonFile;
    }

    @Override
    public void executeRequest() {

        JSONObject result = null;
        InputStream in = getClass().getClassLoader().getResourceAsStream(jsonFile);
        String jsonString = TestUtil.loadJSONFromFile(in);
        try {
            result = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            VolleyError volleyError = new VolleyError(e.getMessage());
            onErrorResponse(volleyError);
        }
        onResponse(result);

    }

}
