package com.philips.platform.ecs.Address;


import com.philips.platform.ecs.TestUtil;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.request.CreateAddressRequest;

import org.json.JSONObject;

import java.io.InputStream;

public class MockCreateAddressRequest extends CreateAddressRequest {

    String jsonfileName;


    public MockCreateAddressRequest(String jsonFileName, ECSAddress ecsAddressRequest, ECSCallback<ECSAddress, Exception> ecsCallback) {
        super(ecsAddressRequest, ecsCallback);
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
