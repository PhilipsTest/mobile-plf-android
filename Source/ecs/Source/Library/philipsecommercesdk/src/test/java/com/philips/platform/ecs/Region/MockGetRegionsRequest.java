package com.philips.platform.ecs.Region;

import com.android.volley.VolleyError;
import com.philips.platform.ecs.TestUtil;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.region.ECSRegion;
import com.philips.platform.ecs.request.GetRegionsRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

public class MockGetRegionsRequest extends GetRegionsRequest {

    private   String jsonFile;
    private final String countryISO;
    public MockGetRegionsRequest(String jsonFile, ECSCallback<List<ECSRegion>, Exception> ecsCallback, String countryISO) {
        super(countryISO, ecsCallback);
        this.jsonFile = jsonFile;
        this.countryISO = countryISO;
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
