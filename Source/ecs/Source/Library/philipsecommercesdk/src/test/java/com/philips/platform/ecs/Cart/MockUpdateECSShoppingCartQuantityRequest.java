package com.philips.platform.ecs.Cart;

import com.android.volley.VolleyError;
import com.philips.platform.ecs.TestUtil;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.cart.ECSEntries;
import com.philips.platform.ecs.request.UpdateECSShoppingCartQuantityRequest;

import java.io.InputStream;

public class MockUpdateECSShoppingCartQuantityRequest extends UpdateECSShoppingCartQuantityRequest
{  String jsonfileName;
    public MockUpdateECSShoppingCartQuantityRequest(String jsonFileName, ECSCallback<Boolean, Exception> ecsCallback, ECSEntries entriesEntity, int quantity) {
        super(ecsCallback, entriesEntity, quantity);
        this.jsonfileName=jsonFileName;
    }

    @Override
    public void executeRequest() {

        InputStream in = getClass().getClassLoader().getResourceAsStream(jsonfileName);
        String jsonString = TestUtil.loadJSONFromFile(in);
        if(null!=jsonString && !jsonString.isEmpty()){
            onResponse(jsonString);
        }else{
            VolleyError volleyError = new VolleyError("Update Quantity failed");
            onErrorResponse(volleyError);
        }

    }
}
