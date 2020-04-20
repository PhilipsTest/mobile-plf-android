package com.philips.cdp.ecs.Payment;

import com.philips.cdp.ecs.TestUtil;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.request.SetPaymentMethodRequest;

import org.json.JSONObject;

import java.io.InputStream;

public class MockSetPaymentMethodRequest extends SetPaymentMethodRequest {

    String jsonFile;
    public MockSetPaymentMethodRequest(String paymentDetailsId, ECSCallback<Boolean, Exception> ecsCallback, String jsonFile) {
        super(paymentDetailsId, ecsCallback);
        this.jsonFile=jsonFile;
    }

    @Override
    public void executeRequest() {

        JSONObject result = null;
        InputStream in = getClass().getClassLoader().getResourceAsStream(jsonFile);
        String jsonString = TestUtil.loadJSONFromFile(in);
        onResponse(jsonString);


    }
}
