package com.philips.cdp.di.ecs.Cart;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.philips.cdp.di.ecs.ECSServices;
import com.philips.cdp.di.ecs.MockECSServices;
import com.philips.cdp.di.ecs.StaticBlock;
import com.philips.cdp.di.ecs.constants.ModelConstants;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart;
import com.philips.cdp.di.ecs.model.cart.EntriesEntity;
import com.philips.cdp.di.ecs.model.products.Product;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.rest.RestInterface;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@RunWith(RobolectricTestRunner.class)
public class UpdateShoppingCartTest {
    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockUpdateECSShoppingCartQuantityRequest mockUpdateECSShoppingCartQuantityRequest;

    ECSCallback<Boolean, Exception> ecsCallback;

    EntriesEntity entriesEntity;

    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        appInfra.getServiceDiscovery().setHomeCountry("DE");


        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);

        StaticBlock.initialize();
        ecsCallback = new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };

         entriesEntity = new EntriesEntity();
        entriesEntity.setEntryNumber(123456);
         Product product = new Product();
         product.setCode("1234");
        entriesEntity.setProduct(product);

        mockUpdateECSShoppingCartQuantityRequest = new MockUpdateECSShoppingCartQuantityRequest("UpdateShoppingCartSuccess.json",ecsCallback,entriesEntity,2);

    }

    @Test
    public void updateShoppingCartSuccess() {
        final int quantity= 2;
        mockECSServices.setJsonFileName("UpdateShoppingCartSuccess.json");

        EntriesEntity entriesEntity = new EntriesEntity();
        mockECSServices.updateQuantity(quantity, entriesEntity, new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {
                assertNotNull(result);
                assertNotNull(result.getGuid());
                assertNotNull(result.getEntries().get(0).getQuantity());
                assertEquals(quantity,result.getEntries().get(0).getQuantity());
                assertEquals(true,result.getEntries().get(0).isUpdateable());
                assertNotNull(result.getEntries().get(0).getBasePrice());
                assertNotNull(result.getEntries().get(0).getBasePrice().getCurrencyIso());
                assertNotNull(result.getEntries().get(0).getBasePrice().getFormattedValue());
                assertNotNull(result.getEntries().get(0).getBasePrice().getPriceType());
                assertNotNull(result.getEntries().get(0).getBasePrice().getValue());
                assertNotNull(result.getEntries().get(0).getTotalPrice());
                // test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);
                // test case failed
            }
        });

    }


    //TODO
 /*   @Test
    public void updateShoppingCartFailure() {
        final int quantity= 2;
        mockECSServices.setJsonFileName("EmptyString.json");

        EntriesEntity entriesEntity = new EntriesEntity();
        mockECSServices.updateQuantity(quantity, entriesEntity, new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart result) {
               // assertTrue(false);
                // test case failed
            }

            @Override
            public void onFailure(Exception error, int errorCode) {
                //assertEquals(8999,errorCode);
                // test case passed

            }
        });

    }*/

    @Test
    public void isValidURL() {
        //acc.us.pil.shop.philips.com/pilcommercewebservices/v2/US_Tuscany/users/current/carts/current/entries/0?fields=FULL&lang=en_US
        System.out.println("print the URL"+mockUpdateECSShoppingCartQuantityRequest.getURL());
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/users/current/carts/current/entries/123456?fields=FULL&lang="+StaticBlock.getLocale();
        Assert.assertEquals(excepted,mockUpdateECSShoppingCartQuantityRequest.getURL());
    }

    @Test
    public void isValidPutRequest() {
        Assert.assertEquals(2,mockUpdateECSShoppingCartQuantityRequest.getMethod());
    }

    @Test
    public void isValidHeader() {

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("Content-Type", "application/x-www-form-urlencoded");
        expectedMap.put("Authorization", "Bearer " + "acceesstoken");

        Map<String, String> actual = mockUpdateECSShoppingCartQuantityRequest.getHeader();

        assertTrue(expectedMap.equals(actual));
    }

    @Test
    public void isValidParam() {

        Map<String, String> expected = new HashMap<>();
        expected.put(ModelConstants.PRODUCT_CODE, "1234");
        expected.put(ModelConstants.ENTRY_CODE, 123456+"");
        expected.put(ModelConstants.PRODUCT_QUANTITY, String.valueOf(2));

        assertTrue(expected.equals(mockUpdateECSShoppingCartQuantityRequest.getParams()));
    }

    @Test
    public void verifyOnResponseError() {
        ECSCallback<Boolean, Exception> spy1 = Mockito.spy(ecsCallback);
        mockUpdateECSShoppingCartQuantityRequest = new MockUpdateECSShoppingCartQuantityRequest("UpdateShoppingCartSuccess.json",spy1,entriesEntity,2);
        VolleyError volleyError = new NoConnectionError();
        mockUpdateECSShoppingCartQuantityRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }


    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockUpdateECSShoppingCartQuantityRequest.getStringSuccessResponseListener());
    }

}
