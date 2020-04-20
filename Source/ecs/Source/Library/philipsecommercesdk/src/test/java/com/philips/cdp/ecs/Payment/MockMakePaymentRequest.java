package com.philips.cdp.ecs.Payment;

import com.philips.cdp.ecs.TestUtil;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.model.orders.ECSOrderDetail;
import com.philips.platform.ecs.model.payment.ECSPaymentProvider;
import com.philips.platform.ecs.request.MakePaymentRequest;

import org.json.JSONObject;

import java.io.InputStream;

public class MockMakePaymentRequest extends MakePaymentRequest {

    String jsonfileName;

    public MockMakePaymentRequest(String jsonFileName, ECSOrderDetail orderDetail, ECSAddress ecsBillingAddressRequest, ECSCallback<ECSPaymentProvider, Exception> ecsCallback) {
        super(orderDetail, ecsBillingAddressRequest, ecsCallback);
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
