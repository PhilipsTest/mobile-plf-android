package com.philips.cdp.di.ecs.Address;

import com.philips.cdp.di.ecs.TestUtil;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.request.SetDeliveryAddressRequest;

import org.json.JSONObject;

import java.io.InputStream;

public class MockSetDeliveryAddressRequest extends SetDeliveryAddressRequest{

     String jsonfileName;

    public MockSetDeliveryAddressRequest(String jsonFileName,String addressID, ECSCallback<Boolean, Exception> ecsCallback) {
        super(addressID, ecsCallback);
        this.jsonfileName=jsonFileName;
    }

    @Override
    public void executeRequest() {

        JSONObject result = null;
        InputStream in = getClass().getClassLoader().getResourceAsStream(jsonfileName);//"CreateAddressSuccess
        String jsonString = TestUtil.loadJSONFromFile(in);
        onResponse(jsonString);

    }
}
