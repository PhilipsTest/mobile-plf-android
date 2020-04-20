package com.philips.cdp.ecs.Oath;

import com.android.volley.VolleyError;
import com.philips.cdp.ecs.TestUtil;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.integration.ECSOAuthProvider;
import com.philips.platform.ecs.integration.GrantType;
import com.philips.platform.ecs.model.oauth.ECSOAuthData;
import com.philips.platform.ecs.request.OAuthRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class MockOAuthRequest extends OAuthRequest {
    String jsonFile;

    public MockOAuthRequest(String jsonFile, GrantType grantType, ECSOAuthProvider oAuthInput, ECSCallback<ECSOAuthData, Exception> ecsListener) {
        super(grantType,oAuthInput, ecsListener);
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
