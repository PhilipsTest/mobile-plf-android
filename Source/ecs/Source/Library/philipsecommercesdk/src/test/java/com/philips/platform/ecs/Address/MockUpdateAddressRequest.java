package com.philips.platform.ecs.Address;

import com.philips.platform.ecs.TestUtil;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.request.UpdateAddressRequest;

import org.json.JSONObject;

import java.io.InputStream;

public class MockUpdateAddressRequest extends UpdateAddressRequest
{
    String jsonfileName;

    public MockUpdateAddressRequest(String jsonFileName, ECSAddress addresses, ECSCallback<Boolean, Exception> ecsCallback) {
        super(addresses, ecsCallback);
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
