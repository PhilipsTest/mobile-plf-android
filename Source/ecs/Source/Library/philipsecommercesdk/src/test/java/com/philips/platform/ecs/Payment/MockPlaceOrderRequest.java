package com.philips.platform.ecs.Payment;

import com.philips.platform.ecs.TestUtil;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.orders.ECSOrderDetail;
import com.philips.platform.ecs.request.SubmitOrderRequest;

import org.json.JSONObject;

import java.io.InputStream;

public class MockPlaceOrderRequest extends SubmitOrderRequest {

    String jsonfileName;

    public MockPlaceOrderRequest(String jsonFileName, String cvv, ECSCallback<ECSOrderDetail, Exception> exceptionECSCallback) {
        super(cvv, exceptionECSCallback);
        this.jsonfileName=jsonFileName;
    }

    @Override
    public void executeRequest() {

        JSONObject result = null;
        InputStream in = getClass().getClassLoader().getResourceAsStream(jsonfileName);
        String jsonString = TestUtil.loadJSONFromFile(in);
        onResponse(jsonString);

    }
}
