package com.philips.cdp.ecs.Cart;

import com.android.volley.VolleyError;
import com.philips.cdp.ecs.TestUtil;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.request.GetECSShoppingCartsRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class MockGetECSShoppingCartsRequest extends GetECSShoppingCartsRequest
{

    String jsonfileName;
    public MockGetECSShoppingCartsRequest(String jsonFileName,ECSCallback<ECSShoppingCart, Exception> ecsCallback) {
        super(ecsCallback);
        this.jsonfileName=jsonFileName;
    }

    @Override
    public void executeRequest() {

        JSONObject result = null;
        InputStream in = getClass().getClassLoader().getResourceAsStream(jsonfileName);//"PRXProductAssets.json"
        String jsonString = TestUtil.loadJSONFromFile(in);
        try {
            result = new JSONObject(jsonString);
            new  JSONObject();
        } catch (JSONException e) {
            e.printStackTrace();
            VolleyError volleyError = new VolleyError(ECSErrorEnum.ECSsomethingWentWrong.toString());
            onErrorResponse(volleyError);
        }
        onResponse(result);

    }

}
