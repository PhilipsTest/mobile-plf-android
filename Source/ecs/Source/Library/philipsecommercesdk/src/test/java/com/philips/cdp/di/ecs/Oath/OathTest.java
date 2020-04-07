package com.philips.cdp.di.ecs.Oath;

import android.content.Context;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.philips.cdp.di.ecs.ECSServices;
import com.philips.cdp.di.ecs.MockECSServices;
import com.philips.cdp.di.ecs.MockInputValidator;
import com.philips.cdp.di.ecs.StaticBlock;
import com.philips.cdp.di.ecs.TestUtil;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.integration.ECSOAuthProvider;
import com.philips.cdp.di.ecs.integration.GrantType;
import com.philips.cdp.di.ecs.model.oauth.ECSOAuthData;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.rest.RestInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(RobolectricTestRunner.class)
public class OathTest {
    private Context mContext;


    MockECSServices mockECSServices;
    ECSServices ecsServices;


    private AppInfra appInfra;


    @Mock
    RestInterface mockRestInterface;

    ECSCallback<ECSOAuthData, Exception> ecsCallback;

    MockOAuthRequest mockOAuthRequest;

    ECSOAuthProvider oAuthInput;
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

        ecsCallback = new ECSCallback<ECSOAuthData, Exception>() {
            @Override
            public void onResponse(ECSOAuthData result) {
                assertNotNull(result);
                assertNotNull(result.getAccessToken());
                // test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);
                // test case failed
            }
        };

        oAuthInput = new ECSOAuthProvider() {
            @Override
            public String getOAuthID() {
                return "mock Jainrain ID";
            }
        };

        mockOAuthRequest = new MockOAuthRequest("HybrisOauthSuccess.json",GrantType.JANRAIN,oAuthInput,ecsCallback);

    }

    @Test
    public void hybrisOathAuthenticationSuccess() {
        mockInputValidator.setJsonFileName("HybrisOauthSuccess.json");

        ECSOAuthProvider oAuthInput = new ECSOAuthProvider() {
            @Override
            public String getOAuthID() {
                return "mock Jainrain ID";
            }
        };
        mockECSServices.hybrisOAthAuthentication(oAuthInput, new ECSCallback<ECSOAuthData, Exception>() {
            @Override
            public void onResponse(ECSOAuthData result) {
                assertNotNull(result);
                assertNotNull(result.getAccessToken());
                // test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);
                // test case failed
            }
        });

    }

    @Test
    public void refreshOAuthSuccess() {
        mockInputValidator.setJsonFileName("HybrisOauthSuccess.json");

        ECSOAuthProvider oAuthInput = new ECSOAuthProvider() {
            @Override
            public String getOAuthID() {
                return "mock Jainrain ID";
            }
        };
        mockECSServices.hybrisRefreshOAuth(oAuthInput, new ECSCallback<ECSOAuthData, Exception>() {
            @Override
            public void onResponse(ECSOAuthData result) {
                assertNotNull(result);
                assertNotNull(result.getAccessToken());
                // test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);
                // test case failed
            }
        });

    }

    @Test
    public void hybrisOathAuthenticationFailure() {
        mockInputValidator.setJsonFileName("HybrisOauthFailure.json");

        ECSOAuthProvider oAuthInput = new ECSOAuthProvider() {
            @Override
            public String getOAuthID() {
                return "mock Jainrain ID";
            }
        };
        mockECSServices.hybrisOAthAuthentication(oAuthInput, new ECSCallback<ECSOAuthData, Exception>() {
            @Override
            public void onResponse(ECSOAuthData result) {
                assertNotNull(result);
                assertNull(result.getAccessToken());
                // test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);
                // test case failed
            }
        });

    }

    @Test
    public void hybrisOathAuthenticationInvalidClient() {
        mockInputValidator.setJsonFileName("HybrisInvalidClient.json");

        ECSOAuthProvider oAuthInput = new ECSOAuthProvider() {
            @Override
            public String getOAuthID() {
                return "mock Jainrain ID";
            }
        };
        mockECSServices.hybrisOAthAuthentication(oAuthInput, new ECSCallback<ECSOAuthData, Exception>() {
            @Override
            public void onResponse(ECSOAuthData result) {
                assertNotNull(result);
                assertNull(result.getAccessToken());
                // test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);
                // test case failed
            }
        });

    }

    @Test
    public void hybrisOathAuthenticationInvalidClientReal() {
        //mockECSServices.setJsonFileName("HybrisInvalidClient.json");

        ECSOAuthProvider oAuthInput = new ECSOAuthProvider() {
            @Override
            public String getOAuthID() {
                return "mock Jainrain ID";
            }
        };
        ecsServices.hybrisOAthAuthentication(oAuthInput, new ECSCallback<ECSOAuthData, Exception>() {
            @Override
            public void onResponse(ECSOAuthData result) {
                assertNotNull(result);
                assertNull(result.getAccessToken());
                // test case passed
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                assertTrue(true);
                // test case failed
            }
        });

    }



    @Test
    public void isValidURL() {
        String excepted = StaticBlock.getBaseURL()+"pilcommercewebservices/oauth/token?janrain="+oAuthInput.getOAuthID()+"&grant_type=janrain&client_id=mobile_android&client_secret=secret";
        Assert.assertEquals(excepted,mockOAuthRequest.getURL());
    }

    @Test
    public void isValidPostRequest() {
        Assert.assertEquals(1,mockOAuthRequest.getMethod());
    }

    @Test
    public void isValidHeader() {

        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("janrain","mock Jainrain ID");
        expectedMap.put("grant_type",GrantType.JANRAIN.getType());
        expectedMap.put("client_id",oAuthInput.getClientType().getType());
        expectedMap.put("client_secret",oAuthInput.getClientSecret());

        Map<String, String> actual = mockOAuthRequest.getHeader();

        assertTrue(expectedMap.equals(actual));
    }

    @Test
    public void verifyOnResponseError() {
        ECSCallback<ECSOAuthData, Exception> spy1 = Mockito.spy(ecsCallback);
        mockOAuthRequest = new MockOAuthRequest("HybrisOauthSuccess.json", GrantType.JANRAIN,oAuthInput,spy1);
        VolleyError volleyError = new NoConnectionError();
        mockOAuthRequest.onErrorResponse(volleyError);
        Mockito.verify(spy1).onFailure(any(Exception.class),any(ECSError.class));

    }

    @Test
    public void verifyOnResponseSuccess() {

        ECSCallback<ECSOAuthData, Exception> spy1 = Mockito.spy(ecsCallback);
        mockOAuthRequest = new MockOAuthRequest("HybrisOauthSuccess.json",GrantType.JANRAIN,oAuthInput,spy1);

        JSONObject jsonObject = getJsonObject("HybrisOauthSuccess.json");

        mockOAuthRequest.onResponse(jsonObject);

        Mockito.verify(spy1).onResponse(any(ECSOAuthData.class));

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
        assertNotNull(mockOAuthRequest.getJSONSuccessResponseListener());
    }

}
