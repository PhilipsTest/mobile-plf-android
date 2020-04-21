package com.philips.platform.ecs.Voucher;

import com.philips.platform.ecs.TestUtil;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.request.RemoveVoucherRequest;

import org.json.JSONObject;

import java.io.InputStream;

public class MockRemoveVoucherRequest extends RemoveVoucherRequest {
    String jsonFile;
    public MockRemoveVoucherRequest(String jsonFile,String mVoucherCode, ECSCallback<Boolean, Exception> ecsCallback) {
        super(mVoucherCode, ecsCallback);
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
