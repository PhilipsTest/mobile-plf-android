package com.philips.cdp.ecs.Voucher;

import com.android.volley.VolleyError;
import com.philips.cdp.ecs.TestUtil;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.voucher.ECSVoucher;
import com.philips.platform.ecs.request.GetVouchersRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

public class MockGetVouchersRequest extends GetVouchersRequest {

    String jsonFile;
    public MockGetVouchersRequest(String jsonFile, ECSCallback<List<ECSVoucher>, Exception> ecsCallback) {
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
