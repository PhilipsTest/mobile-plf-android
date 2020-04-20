package com.philips.cdp.ecs.Payment;

import com.android.volley.VolleyError;
import com.philips.cdp.ecs.TestUtil;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.payment.ECSPayment;
import com.philips.platform.ecs.request.GetPaymentsRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

public class MockGetPaymentsRequest extends GetPaymentsRequest {

    String jsonFile;

    public MockGetPaymentsRequest(String jsonFile, ECSCallback<List<ECSPayment>, Exception> ecsCallback) {
        super(ecsCallback);
        this.jsonFile=jsonFile;
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
