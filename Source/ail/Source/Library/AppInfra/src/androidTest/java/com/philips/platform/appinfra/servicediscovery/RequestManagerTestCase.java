/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.platform.appinfra.servicediscovery;

import android.content.Context;
import android.content.SharedPreferences;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.tagging.AppInfraTaggingUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.philips.platform.appinfra.tagging.AppInfraTaggingUtil.SD_STORE_FAILED;
import static com.philips.platform.appinfra.tagging.AppInfraTaggingUtil.SERVICE_DISCOVERY;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RequestManager Test class.
 */
public class RequestManagerTestCase {

    private Context context;
    private ServiceDiscoveryInterface mServiceDiscoveryInterface = null;
    private ServiceDiscoveryManager mServiceDiscoveryManager = null;
    private RequestManager mRequestItemManager = null;
    private AppInfra mAppInfra;

    @Before
    public void setUp() throws Exception {
        context = getInstrumentation().getContext();
        assertNotNull(context);
        mAppInfra = new AppInfra.Builder().build(context);
        assertNotNull(mAppInfra);
        mServiceDiscoveryInterface = mAppInfra.getServiceDiscovery();
        mServiceDiscoveryManager = new ServiceDiscoveryManager(mAppInfra);
        mRequestItemManager = new RequestManager(context, mAppInfra);
        assertNotNull(mServiceDiscoveryInterface);
        assertNotNull(mServiceDiscoveryManager);
        assertNotNull(mRequestItemManager);
    }

    @Test
    public void testRequestManager() {
        RequestManager mRequestManagerTest = new RequestManager(context, mAppInfra);
        assertNotSame(mRequestItemManager, mRequestManagerTest);
    }

    @Test
    public void testTaggingCacheData() {
        AppInfraTaggingUtil appInfraTaggingUtil = mock(AppInfraTaggingUtil.class);
        final SharedPreferences sharedPreferencesMock = mock(SharedPreferences.class);
        when(sharedPreferencesMock.edit()).thenThrow(new NullPointerException());
        mRequestItemManager = new RequestManager(context, mAppInfra, appInfraTaggingUtil) {
            @Override
            SharedPreferences getServiceDiscoverySharedPreferences() {
                return sharedPreferencesMock;
            }

        };
        context = getInstrumentation().getContext();
        mAppInfra = new AppInfra.Builder().build(context);
        mRequestItemManager.cacheServiceDiscovery(null, "https://www.philips.com/api/v1/discovery/B2C/70000?locale=en_US", ServiceDiscoveryManager.AISDURLType.AISDURLTypeProposition);
        verify(appInfraTaggingUtil).trackErrorAction(SERVICE_DISCOVERY, SD_STORE_FAILED);

    }

    @Test
    public void testexecuteNegetivePath() {
        mRequestItemManager.execute("https://acc", ServiceDiscoveryManager.AISDURLType.AISDURLTypeProposition);
    }

    @Test
    public void testParseResponse() {
        Method method;
        try {

            method = RequestManager.class.getDeclaredMethod("parseResponse", JSONObject.class);
            method.setAccessible(true);
            method.invoke(mRequestItemManager, makJsonObject(true, 1));
            method.invoke(mRequestItemManager, makJsonObject(false, 2));
            method.invoke(mRequestItemManager, makJsonObject(true, 0));

            method = RequestManager.class.getDeclaredMethod("execute", String.class, ServiceDiscoveryManager.AISDURLType.class);
            method.setAccessible(true);
            method.invoke(mRequestItemManager, "https://www.philips.com/api/v1/discovery/b2c/77000?locale=nl_NL&tags=apps%2b%2benv%2bprod&country=CN", ServiceDiscoveryManager.AISDURLType.AISDURLTypeProposition);

            method.invoke(mRequestItemManager, "http://www.philips.com/api/v1/discovery/b2c/77000?locale=nl_NL&tags=apps%2b%2benv%2bprod&country=CN", ServiceDiscoveryManager.AISDURLType.AISDURLTypePlatform);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
        }
    }

    @Test
    public void testGetLocaleList() {
        String list = mRequestItemManager.getLocaleList();
        assertNotNull(list);
    }

    @Test
    public void testGetCachedData() {
        final SharedPreferences sharedPreferencesMock = mock(SharedPreferences.class);
        mRequestItemManager = new RequestManager(context, mAppInfra) {
            @Override
            SharedPreferences getServiceDiscoverySharedPreferences() {
                return sharedPreferencesMock;
            }

            @Override
            boolean getPropositionEnabled(AppInfraInterface appInfra) {
                return true;
            }
        };
        when(sharedPreferencesMock.getString("SDPLATFORM", null)).thenThrow(new NullPointerException());
        AppInfraTaggingUtil appInfraTaggingAction = mock(AppInfraTaggingUtil.class);
        assertNull(mRequestItemManager.getCachedData());
    }

    @Test
    public void testgetUrlProposition() {
        String urlProposition = mRequestItemManager.getUrlProposition();
        //        assertNotNull(urlProposition);
    }

    @Test
    public void testgetUrlPlatform() {
        String urlPlatform = mRequestItemManager.getUrlPlatform();
        //       assertNotNull(urlPlatform);
    }

    @Test
    public void testisServiceDiscoveryDataExpired() {
        boolean dateexpired = mRequestItemManager.isServiceDiscoveryDataExpired();
    }

    @Test
    public void testclearCacheServiceDiscovery() {
        mRequestItemManager.clearCacheServiceDiscovery();
    }

    private JSONObject makJsonObject(boolean isSuccess, int resultJsonArraySize) {
        JSONObject urls = new JSONObject();
        try {
            urls.put("userreg.janrain.cdn.v2", "https://d1lqe9temigv1p.cloudfront.net");
            urls.put("userreg.janrain.api.v2", "https://philips.janraincapture.com");

        } catch (JSONException ignored) {
        }

        JSONObject tagsObject = new JSONObject();

        try {
            tagsObject.put("id", "apps:env/prod");
            tagsObject.put("name", "prod");
            tagsObject.put("key", "apps++env+prod");
        } catch (JSONException ignored) {
        }
        JSONArray tags = new JSONArray();

        tags.put(tagsObject);

        JSONObject Config = new JSONObject();
        try {
            Config.put("micrositeId", "77000");
            Config.put("urls", urls);
            Config.put("tags", tags);

        } catch (JSONException ignored) {
        }

        JSONArray ConfigArray = new JSONArray();
        ConfigArray.put(Config);

        JSONObject resultObject = new JSONObject();
        try {
            resultObject.put("locale", "nl_NL");
            resultObject.put("configs", ConfigArray);

        } catch (JSONException ignored) {
        }

        JSONArray results = new JSONArray();
        if (resultJsonArraySize == 0) {
            results.put(null);
        } else if (resultJsonArraySize == 1) {
            results.put(resultObject);
        } else if (resultJsonArraySize == 2) {
            results.put(resultObject);
            results.put(resultObject);
        }

        JSONObject MatchbyLangauge = new JSONObject();
        try {
            MatchbyLangauge.put("available", true);
            MatchbyLangauge.put("results", results);

        } catch (JSONException ignored) {
        }

        JSONObject playLoad = new JSONObject();
        try {
            playLoad.put("country", "IN");
            playLoad.put("matchByLanguage", MatchbyLangauge);
            playLoad.put("matchByCountry", MatchbyLangauge);

        } catch (JSONException ignored) {
        }

        JSONObject MainObj = new JSONObject();
        try {
            if (isSuccess) {
                MainObj.put("success", true);
            } else {
                MainObj.put("success", false);
            }

            MainObj.put("payload", playLoad);
            MainObj.put("httpStatus", "OK");

        } catch (JSONException ignored) {
        }

        String jsonStr = MainObj.toString();

        return MainObj;
    }
}
