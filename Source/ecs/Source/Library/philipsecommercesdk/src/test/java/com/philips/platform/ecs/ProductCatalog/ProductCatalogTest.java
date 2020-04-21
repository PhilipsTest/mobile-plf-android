package com.philips.platform.ecs.ProductCatalog;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.philips.platform.ecs.ECSServices;
import com.philips.platform.ecs.MockECSServices;
import com.philips.platform.ecs.MockInputValidator;
import com.philips.platform.ecs.StaticBlock;
import com.philips.platform.ecs.TestUtil;
import com.philips.platform.ecs.constants.ModelConstants;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.products.ECSProducts;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.rest.RestInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)
public class ProductCatalogTest {
    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    MockGetProductListRequest  mockGetProductListRequest;

    ECSCallback<ECSProducts, Exception> ecsCallback;

    int currentPage = 0;
    int pageSize = 20;
    private MockInputValidator mockInputValidator;

    @Before
    public void setUp() throws Exception {


        mContext = getInstrumentation().getContext();
        appInfra = new AppInfra.Builder().setRestInterface(mockRestInterface).build(mContext);
        appInfra.getServiceDiscovery().setHomeCountry("DE");


        mockECSServices = new MockECSServices("", appInfra);
        ecsServices = new ECSServices("",appInfra);

        StaticBlock.initialize();

        mockInputValidator = new MockInputValidator();

        ecsCallback = new ECSCallback<ECSProducts, Exception>() {
            @Override
            public void onResponse(ECSProducts result) {

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

            }
        };

        mockGetProductListRequest = new MockGetProductListRequest("GetProductList.json",0,20,ecsCallback);
    }

    @Test
    public void getProductListSuccess() {
        mockInputValidator.setJsonFileName("GetProductList.json");
        mockECSServices.fetchProducts(0, 1, new ECSCallback<ECSProducts, Exception>() {
            @Override
            public void onResponse(ECSProducts result) {
                assertNotNull(result);
                assert (result.getProducts().size() > 0); // at least 1 product should come
                assertNotNull(result.getProducts().get(0).getSummary());
                assertNotNull(result.getProducts().get(1).getSummary());
                assertEquals("USD",result.getProducts().get(0).getPrice().getCurrencyIso());
                assertEquals("$ 7.99",result.getProducts().get(0).getPrice().getFormattedValue());
                assertEquals("BUY",result.getProducts().get(0).getPrice().getPriceType());
                assertEquals(7.99,result.getProducts().get(0).getPrice().getValue());
                assertEquals(false,result.getProducts().get(0).isAvailableForPickup());
                assertEquals("productCategorySearchPageWsDTO",result.getType());
                // test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertFalse(true);
                // test case failed

            }
        });

    }


    /*
     * In this test case Get Product list API (Hybris) is failed
     * */
    @Test
    public void getProductListHybrisFailure() {
        mockInputValidator.setJsonFileName("GetProductListHybrisFailure.json");
        mockECSServices.fetchProducts(0, 1, new ECSCallback<ECSProducts, Exception>() {
            @Override
            public void onResponse(ECSProducts result) {
                assertTrue(true);
                // test case failed

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertEquals(5999, ecsError); // error code for Product List
                // test case passed
            }
        });
    }

    /*
     * In this test case Get Product list API (Hybris) is failed
     * */
    @Test
    public void getProductListSummaryFailure() {
        mockInputValidator.setJsonFileName("GetProductList.json");
        mockECSServices.fetchProducts(0, 1, new ECSCallback<ECSProducts, Exception>() {
            @Override
            public void onResponse(ECSProducts result) {
                assertTrue(true);
                // test case failed

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertEquals(4999, ecsError); // error code for Product List
                // test case passed
            }
        });
    }


    @Test
    public void isValidURL() {
        System.out.println("print the URL"+mockGetProductListRequest.getURL());
        //acc.us.pil.shop.philips.com/pilcommercewebservices/v2/US_Tuscany/products/search?query=::category:null&lang=en_US&currentPage=0&pageSize=20
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices"+"/v2/"+StaticBlock.getSiteID()+"/products/search?query=::category:"+StaticBlock.getRootCategory()+"&lang="+StaticBlock.getLocale()+"&currentPage="+currentPage+"&pageSize="+pageSize;
        assertEquals(excepted,mockGetProductListRequest.getURL());
    }

    @Test
    public void isValidGetRequest() {
        assertEquals(0,mockGetProductListRequest.getMethod());
    }

    @Test
    public void isValidParam() {

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put(ModelConstants.CURRENT_PAGE, String.valueOf(currentPage));
        expectedMap.put(ModelConstants.PAGE_SIZE, String.valueOf(pageSize));


        assertNotNull(mockGetProductListRequest.getParams());
        assertTrue(expectedMap.equals(mockGetProductListRequest.getParams()));
    }




    @Test
    public void verifyOnResponseError() {
        ECSCallback<ECSProducts, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetProductListRequest = new MockGetProductListRequest("GetProductList.json",0,20,spy1);
        VolleyError volleyError = new NoConnectionError();
        mockGetProductListRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void verifyOnResponseSuccess() {

        ECSCallback<ECSProducts, Exception> spy1 = Mockito.spy(ecsCallback);
        mockGetProductListRequest = new MockGetProductListRequest("GetProductList.json",0,20,spy1);

        JSONObject jsonObject = getJsonObject("GetProductList.json");

        mockGetProductListRequest.onResponse(jsonObject);

        Mockito.verify(spy1).onResponse(any(ECSProducts.class));

    }


    JSONObject getJsonObject(String jsonfileName){

        JSONObject result = null;
        InputStream in = getClass().getClassLoader().getResourceAsStream(jsonfileName);
        String jsonString = TestUtil.loadJSONFromFile(in);
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            return null;
        }
    }


    @Test
    public void assertResponseSuccessListenerNotNull() {
        assertNotNull(mockGetProductListRequest.getJSONSuccessResponseListener());
    }

    @Test
    public void headerShouldNull() {
        assertNull(mockGetProductListRequest.getHeader());
    }

    @Test
    public void isValidToken() {
        assertNull(mockGetProductListRequest.getToken());
    }

}
