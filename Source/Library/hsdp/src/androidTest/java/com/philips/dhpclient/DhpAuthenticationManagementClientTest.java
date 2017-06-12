package com.philips.dhpclient;

import android.support.multidex.MultiDex;
import android.test.InstrumentationTestCase;

import com.philips.dhpclient.response.DhpResponse;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 310243576 on 8/21/2016.
 */
public class DhpAuthenticationManagementClientTest extends InstrumentationTestCase {

    DhpAuthenticationManagementClient mDhpAuthenticationManagementClient;
    @Before
    public void setUp() throws Exception {
        MultiDex.install(getInstrumentation().getTargetContext());
//        MockitoAnnotations.initMocks(this);
        super.setUp();
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());

        DhpApiClientConfiguration dhpApiClientConfiguration = new DhpApiClientConfiguration("apiBaseUrl","dhpApplicationName","signingKey","signingSecret");
        mDhpAuthenticationManagementClient = new DhpAuthenticationManagementClient(dhpApiClientConfiguration);

    }

    @Test
    public void testDhpAuthenticateManagementClient(){
        try{
            mDhpAuthenticationManagementClient.authenticate("username","password","secret");
        }catch(Exception e){}
//
        mDhpAuthenticationManagementClient.createRefreshSignature("refresh_Secret","date","accessToken");
        mDhpAuthenticationManagementClient.createRefreshSignature("refresh_Secret","","");
        mDhpAuthenticationManagementClient.validateToken("userId","accessToken");
        mDhpAuthenticationManagementClient.validateToken(null,null);
        mDhpAuthenticationManagementClient.validateToken("","");
try  {mDhpAuthenticationManagementClient.loginSocialProviders("email","socialaccesstoken","asjdbwdbwdbejkwfbjkewbwejkdw");}
catch(Exception e){}
        mDhpAuthenticationManagementClient.logout("sample","sample");
        mDhpAuthenticationManagementClient.logout(null,null);
        mDhpAuthenticationManagementClient.logout("","");
        DhpAuthenticationManagementClient.AuthenticationRequestJson mAuthenticationRequestJson=
                new DhpAuthenticationManagementClient.AuthenticationRequestJson("loginId","password");

        DhpAuthenticationManagementClient.RefreshTokenRequest mRefreshTokenRequest=
                new DhpAuthenticationManagementClient.RefreshTokenRequest("refreshToken");

        DhpAuthenticationManagementClient.ResetPasswordRequest mResetPasswordRequest=
                new DhpAuthenticationManagementClient.ResetPasswordRequest("resetPasswordRequest");

     assertNotNull(mDhpAuthenticationManagementClient);

    }
    public void testSign() throws URISyntaxException {
        Method method = null;
        String s= "sample";
        Map<String, String> headers = new HashMap<>();
        Map<String, Object> rawResponse = new HashMap<>();
        DhpResponse dhpResponse = new DhpResponse(rawResponse);

        try {
            method = DhpAuthenticationManagementClient.class.getDeclaredMethod("getDhpAuthenticationResponse", new Class[]{DhpResponse.class});
            method.setAccessible(true);
            method.invoke(mDhpAuthenticationManagementClient, new  Object[]{dhpResponse});
            dhpResponse = null;
            method.invoke(mDhpAuthenticationManagementClient, new  Object[]{dhpResponse});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void testGetUTCdatetimeAsString()  {
        Method method = null;
        try {
            method = DhpAuthenticationManagementClient.class.getDeclaredMethod("getUTCdatetimeAsString");
            method.setAccessible(true);
            method.invoke(mDhpAuthenticationManagementClient);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}