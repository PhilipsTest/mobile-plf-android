package com.philips.cdp.ecs.Cart;

import com.android.volley.VolleyError;
import com.philips.cdp.ecs.TestUtil;
import com.philips.platform.ecs.error.ECSErrorEnum;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.request.AddProductToECSShoppingCartRequest;

import java.io.InputStream;

public class MockAddProductToECSShoppingCartRequest extends AddProductToECSShoppingCartRequest {
    String jsonfileName;

    public MockAddProductToECSShoppingCartRequest(String jsonFileName, String ctn, ECSCallback<Boolean, Exception> ecsCallback) {
        super(ctn, ecsCallback);
        this.jsonfileName=jsonFileName;
    }

    @Override
    public void executeRequest() {

        InputStream in = getClass().getClassLoader().getResourceAsStream(jsonfileName);
        String jsonString = TestUtil.loadJSONFromFile(in);
        if(null!=jsonString && !jsonString.isEmpty()){
            onResponse(jsonString);
        }else{
            VolleyError volleyError = new VolleyError(ECSErrorEnum.ECSsomethingWentWrong.toString());
            onErrorResponse(volleyError);
        }

    }
}
