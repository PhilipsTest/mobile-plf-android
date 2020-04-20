package com.philips.platform.ecs.ProductDetail;

import com.android.volley.VolleyError;
import com.philips.platform.ecs.TestUtil;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.disclaimer.Disclaimers;
import com.philips.platform.ecs.request.GetProductDisclaimerRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class MockGetProductDisclaimerRequest extends GetProductDisclaimerRequest {
    String jsonFileName;
    public MockGetProductDisclaimerRequest(String jsonFileName, String assetUrl, ECSCallback<Disclaimers, Exception> ecsCallback) {
        super(assetUrl, ecsCallback);
        this.jsonFileName=jsonFileName;
    }

    @Override
    public void executeRequest() {

        JSONObject result = null;
        InputStream in = getClass().getClassLoader().getResourceAsStream(jsonFileName);//"PRXDisclaimers.json"
        String jsonString = TestUtil.loadJSONFromFile(in);
        try {
            result = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            VolleyError volleyError = new VolleyError(ECSErrorEnum.ECSsomethingWentWrong.toString());
            onErrorResponse(volleyError);
        }
        onResponse(result);

    }
}
