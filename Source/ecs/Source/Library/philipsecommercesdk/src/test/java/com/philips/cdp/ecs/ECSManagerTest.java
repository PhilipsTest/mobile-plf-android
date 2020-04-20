package com.philips.cdp.ecs;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.philips.cdp.ecs.Cart.MockGetECSShoppingCartsRequest;
import com.philips.platform.ecs.ECSManager;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.model.products.ECSProduct;
import com.philips.platform.ecs.network.NetworkController;
import com.philips.platform.ecs.prx.serviceDiscovery.ServiceDiscoveryRequest;
import com.philips.platform.ecs.request.APPInfraRequest;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.rest.TokenProviderInterface;
import com.philips.platform.appinfra.rest.request.JsonObjectRequest;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.when;

public class ECSManagerTest {

    @Mock
    ECSManager ecsManager;

    @Mock
    MockGetECSShoppingCartsRequest mockGetECSShoppingCartsRequest;


    ECSProduct product;
    ECSCallback<ECSProduct, Exception> ecsCallback;
    @Mock
    private AppInfraInterface appInfra;

    @Mock
    private ServiceDiscoveryInterface serviceDiscoveryMock;

    @Mock
    private ServiceDiscoveryRequest serviceDiscoveryRequest;


    private NetworkController networkControllerMock;

    @Mock
    private JsonObjectRequest jsonObjectRequest;


    APPInfraRequest appInfraRequest = new APPInfraRequest() {
        @Override
        public int getMethod() {
            return 0;
        }

        @Override
        public String getURL() {
            return null;
        }

        @Override
        public JSONObject getJSONRequest() {
            return null;
        }

        @Override
        public Response.Listener<JSONObject> getJSONSuccessResponseListener() {
            return null;
        }

        @Override
        public Response.Listener<String> getStringSuccessResponseListener() {
            return null;
        }

        @Override
        public Response.ErrorListener getJSONFailureResponseListener() {
            return null;
        }

        @Override
        public Map<String, String> getHeader() {
            return null;
        }

        @Override
        public Map<String, String> getParams() {
            return null;
        }

        @Override
        public TokenProviderInterface getTokenProviderInterface() {
            return null;
        }

        @Override
        public void onErrorResponse(VolleyError error) {

        }

        @Override
        public Token getToken() {
            return null;
        }
    };

    @Mock
    Log logMock;






    @Before
    public void setUp() throws Exception {
       networkControllerMock= new NetworkController(appInfraRequest);
        ecsManager = new ECSManager();
        product = new ECSProduct();
        product.setCode("abcd");
        //when(networkControllerMock.getAppInfraJSONObject(any(APPInfraRequest.class))).thenReturn(jsonObjectRequest);

      // when(getECSShoppingCartsRequest.executeRequest()).then(mockGetECSShoppingCartsRequest.executeRequest());



   // when(appInfra.getServiceDiscovery()).thenReturn(serviceDiscoveryMock);
        //.when(serviceDiscoveryRequest.OnUrlReceived()).thenReturn();




    }




    @Test
    public void getECSShoppingCartTest() {
        ECSCallback ecsCallback =  new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {
                Log.v("tag", "");
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                Log.v("tag", "");
            }
        };

        mockGetECSShoppingCartsRequest = new MockGetECSShoppingCartsRequest("ShoppingCartSuccess.json",(ecsCallback));
        ECSManager mocECSManager = org.mockito.Mockito.mock(ECSManager.class, CALLS_REAL_METHODS);
        when(mocECSManager.getShoppingCartsRequestObject(any( ECSCallback.class))).thenReturn(mockGetECSShoppingCartsRequest);

        try {
            mocECSManager.getECSShoppingCart(ecsCallback);
        } catch (Exception e) {
            //Log.v("Err", e.getMessage());
        }

    }
}
