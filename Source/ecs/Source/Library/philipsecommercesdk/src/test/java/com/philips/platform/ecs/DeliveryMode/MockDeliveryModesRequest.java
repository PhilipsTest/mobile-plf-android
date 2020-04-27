package com.philips.platform.ecs.DeliveryMode;

import com.android.volley.VolleyError;
import com.philips.platform.ecs.TestUtil;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSDeliveryMode;
import com.philips.platform.ecs.request.GetDeliveryModesRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

public class MockDeliveryModesRequest extends GetDeliveryModesRequest {

    private final  String jsonFile;

    public MockDeliveryModesRequest(ECSCallback<List<ECSDeliveryMode>, Exception> ecsCallback, String jsonFile) {
        super(ecsCallback);
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
