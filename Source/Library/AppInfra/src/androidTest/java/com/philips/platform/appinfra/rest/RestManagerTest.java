package com.philips.platform.appinfra.rest;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.MockitoTestCase;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationManager;
import com.philips.platform.appinfra.rest.request.GsonCustomRequest;
import com.philips.platform.appinfra.rest.request.ImageRequest;
import com.philips.platform.appinfra.rest.request.JsonObjectRequest;
import com.philips.platform.appinfra.rest.request.RequestQueue;
import com.philips.platform.appinfra.rest.request.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by 310243577 on 11/4/2016.
 */

public class RestManagerTest extends MockitoTestCase {

    private RestInterface mRestInterface = null;
    private Context context;
    private AppInfra mAppInfra;
    private RequestQueue queue;
    private String baseURL = "https://hashim.herokuapp.com";
    private String serviceIdString = "userreg.janrain.api";
    private String accessToken;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = getInstrumentation().getContext();
        assertNotNull(context);
        mAppInfra = new AppInfra.Builder().build(context);
        assertNotNull(mAppInfra);
        mRestInterface = mAppInfra.getRestClient();
        assertNotNull(mRestInterface);
    }

    public void testgetRequestQueue() {
        queue = mRestInterface.getRequestQueue();
        assertNotNull(queue);
    }

    public void testIsInternetReachable()
    {
        if(mRestInterface.isInternetReachable()) {
            assertTrue(mRestInterface.isInternetReachable());
        }else
        {
            assertFalse(mRestInterface.isInternetReachable());
        }
    }

    public void testGetNetworkInfo()
    {
        assertNotNull(mRestInterface.getNetworkReachabilityStatus());
        String netWorkInfo=mRestInterface.getNetworkReachabilityStatus();
        if(null!=netWorkInfo)
        {
            Log.v("NetworkInfo","device connected to "+netWorkInfo);
        }


    }


    public void testStringRequestwithUrl() {

        StringRequest mStringRequest = null;
        try {
            mStringRequest = new StringRequest(Request.Method.POST, baseURL + "/RCT/test.php?action=data&id=" + "az",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("LOG", "" + response);
                            assertSame("{\"id\":\"az\"}", "{\"id\":\"az\"}");
                            assertNotNull(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("LOG", "" + error);
                    String errorcode = null != error.networkResponse ? error.networkResponse.statusCode + "" : "";
                    assertNotNull(errorcode);
                }
            }, null, null,null);
        } catch (Exception e) {
            Log.i("LOG", "" + e.toString());
        }
        if (null != mStringRequest) {
            mRestInterface.getRequestQueue().add(mStringRequest);
        }
    }

    public void testJsonRequestWithUrl() {
        JsonObjectRequest jsonRequest = null;
        try {
            jsonRequest = new JsonObjectRequest(Request.Method.GET,
                    baseURL + "/RCT/test.php?action=data&id=" + "az", null
                    , new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("LOG", "" + response);
                    assertSame("{\"id\":\"az\"}", "{\"id\":\"az\"}");
                    assertNotNull(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("LOG", "" + error);
                    String errorcode = null != error.networkResponse ? error.networkResponse.statusCode + "" : "";
                    assertNotNull(errorcode);
                }
            }, null, null,null);
        } catch (Exception e) {
            Log.e("LOG REST SD", e.toString());
            e.printStackTrace();
        }
        if (null != jsonRequest) {
            mRestInterface.getRequestQueue().add(jsonRequest);
        }
    }

    public void testImageRequestWithUrl() {
        ImageRequest imageRequest = null;
        try {
            imageRequest = new ImageRequest("http://i.imgur.com/7spzG.png", new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    assertNotNull(response);
                }
            }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    assertNotNull(error);
                }
            }, null, null,null);

        } catch (Exception e) {
            Log.e("LOG REST SD", e.toString());
            e.printStackTrace();
        }
        if (null != imageRequest) {
            mRestInterface.getRequestQueue().add(imageRequest);
        }
    }

    public void testStringRequestWithServiceId() {
        StringRequest stringRequest = null;
        try {
            stringRequest = new StringRequest(Request.Method.GET,
                    serviceIdString, ServiceIDUrlFormatting.SERVICEPREFERENCE.BYLANGUAGE,
                    ""
                    , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG", "" + response);
                    assertNotNull(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("LOG", "" + error);
                    String errorcode = null != error.networkResponse ? error.networkResponse.statusCode + "" : "";
                    assertNotNull(errorcode);
                }
            });

        } catch (Exception e) {
            Log.e("LOG REST SD", e.toString());
            e.printStackTrace();
        }

        // mStringRequest.setShouldCache(false); // set false to disable cache

        if (null != stringRequest) {
            //  urlFired.setText(mStringRequest.getUrl());
            mRestInterface.getRequestQueue().add(stringRequest);
        }
        if (stringRequest.getCacheEntry() != null) {
            String cachedResponse = new String(stringRequest.getCacheEntry().data);
            Log.i("CACHED DATA: ", "" + cachedResponse);
        }
    }

    public void testJsonRequestwithServiceId() {
        JsonObjectRequest jsonRequest = null;
        try {
            jsonRequest = new JsonObjectRequest(Request.Method.GET,
                    serviceIdString, ServiceIDUrlFormatting.SERVICEPREFERENCE.BYLANGUAGE,
                    "", null
                    , new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("LOG", "" + response);
                    assertNotNull(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("LOG", "" + error);
                    String errorcode = null != error.networkResponse ? error.networkResponse.statusCode + "" : "";
                    assertNotNull(errorcode);
                }
            });
        } catch (Exception e) {
            Log.e("LOG REST SD", e.toString());
            e.printStackTrace();
        }
        if (null != jsonRequest) {
            mRestInterface.getRequestQueue().add(jsonRequest);
        }
    }

    public void testImageRequestwithServiceId() {
        ImageRequest imageRequest = null;
        try {
            imageRequest = new ImageRequest(serviceIdString, ServiceIDUrlFormatting.SERVICEPREFERENCE.BYLANGUAGE,
                    "", new Response.Listener<Bitmap>() {

                @Override
                public void onResponse(Bitmap response) {
                    Log.i("LOG", "" + response);
                    assertNotNull(response);
                }
            }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("LOG", "" + error);
                    String errorcode = null != error.networkResponse ? error.networkResponse.statusCode + "" : "";
                    assertNotNull(errorcode);
                }
            });
        } catch (Exception e) {
            Log.e("LOG REST SD", e.toString());
            e.printStackTrace();
        }
        if (null != imageRequest) {
            mRestInterface.getRequestQueue().add(imageRequest);
        }
    }

    public void testAppInfraRequestWithUrl() {
        GsonCustomRequest request = null;
        try {
            request = new GsonCustomRequest(Request.Method.GET, baseURL + "/RCT/test.php?action=data&id=" + "az",
                    null,  new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    assertSame("{\"id\":\"az\"}", "{\"id\":\"az\"}");
                    assertNotNull(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle error
                }
            }, null, null,null);
        } catch (Exception e) {
            Log.e("LOG REST SD", e.toString());
        }

        if (null != request) {
            mRestInterface.getRequestQueue().add(request);
        }
    }

    public void testAppInfraRequestWithServiceId() {
        Map<String , String> header = new HashMap<>();
        header.put("test","pwd");
        GsonCustomRequest request = null;
        try {
            request = new GsonCustomRequest(Request.Method.GET, serviceIdString, ServiceIDUrlFormatting.SERVICEPREFERENCE.BYLANGUAGE
                    , "",
                    null, header, new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    assertNotNull(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle error
                }
            });
        } catch (Exception e) {
            Log.e("LOG REST SD", e.toString());
        }

        if (null != request) {
            mRestInterface.getRequestQueue().add(request);
        }
    }

    public void testlogin() {
        StringRequest mStringRequest = null;
        try {
            mStringRequest = new StringRequest(Request.Method.GET, baseURL + "/RCT/test.php?action=authtoken", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG", "" + response);
                    //Toast.makeText(RestClientActivity.this, response, Toast.LENGTH_SHORT).show();
                    JSONObject jobj = null;
                    try {
                        jobj = new JSONObject(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    accessToken = jobj.optString("access_token");
                    if (null != accessToken) {
                        assertNotNull(accessToken);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("LOG", "" + error);
                    //Toast.makeText(RestClientActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    String errorcode = null != error.networkResponse ? error.networkResponse.statusCode + "" : "";
                    assertNotNull(errorcode);
                }

            }, null, null,null);
        } catch (Exception e) {
            Log.i("LOG", "" + e.toString());
        }
        mStringRequest.setShouldCache(false); // set false to disable cache , by default its true
        if (null != mStringRequest) {
            mRestInterface.getRequestQueue().add(mStringRequest);
        }
    }

    public void testAuthentication() {
        TokenProviderInterface provider = new TokenProviderInterface() {
            @Override
            public Token getToken() {
                return new Token() {
                    @Override
                    public TokenType getTokenType() {
                        return TokenType.OAUTH2;
                    }

                    @Override
                    public String getTokenValue() {
                        return accessToken;
                    }
                };
            }
        };
        Map<String, String> header = new HashMap<>();
        header.put("userName", "test");

        StringRequest mStringRequest = null;
        try {
            mStringRequest = new StringRequest(Request.Method.GET, baseURL + "/RCT/test.php?action=authcheck",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("LOG", "" + response);
                            assertNotNull(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("LOG", "" + error);
                            String errorcode = null != error.networkResponse ? error.networkResponse.statusCode + "" : "";
                            assertNotNull(errorcode);
                        }
                    }, header ,null , provider
            ) {

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    int mStatusCode = response.statusCode;
                    return super.parseNetworkResponse(response);
                }

            };
        } catch (Exception e) {
            Log.i("LOG", "" + e.toString());
        }
        if (null != mStringRequest) {
            mRestInterface.getRequestQueue().add(mStringRequest);
        }
    }
}







