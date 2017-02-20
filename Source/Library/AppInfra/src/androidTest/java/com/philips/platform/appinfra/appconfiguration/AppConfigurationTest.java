package com.philips.platform.appinfra.appconfiguration;

import android.content.Context;
import android.util.Log;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.ConfigValues;
import com.philips.platform.appinfra.MockitoTestCase;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 310238114 on 8/2/2016.
 */
public class AppConfigurationTest extends MockitoTestCase {

    AppConfigurationInterface mConfigInterface = null;

    private Context context;
    private AppInfra mAppInfra;
    private Method method;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = getInstrumentation().getContext();
        assertNotNull(context);
        mAppInfra = new AppInfra.Builder().build(context);
        //mConfigInterface = mAppInfra.getConfigInterface();

        // overriding ConfigManager to get Test JSON data, as AppInfra library does not have uApp configuration file
        mConfigInterface = new AppConfigurationManager(mAppInfra) {
            @Override
            protected JSONObject getMasterConfigFromApp() {
                JSONObject result = null;
                try {
                  /*  InputStream mInputStream = mContext.getAssets().open("configuration.json");
                    BufferedReader r = new BufferedReader(new InputStreamReader(mInputStream));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line).append('\n');
                    }
                    result = new JSONObject(total.toString());*/
                    String testJson = ConfigValues.testJson();
                    result = new JSONObject(testJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            }

        };
        assertNotNull(mConfigInterface);

    }


    public void testGetPropertyForKey() throws IllegalArgumentException {
        try {
            AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface
                    .AppConfigurationError();


            configError.setErrorCode(null);// reset error code to null
            assertNull(mConfigInterface.getPropertyForKey("", "", configError));//  invalid key and invalid Group
            assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.InvalidKey,
                    configError.getErrorCode());

            configError.setErrorCode(null);// reset error code to null
            assertNull(mConfigInterface.getPropertyForKey(null, null, configError));// invalid key and invalid Group
            assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.InvalidKey, configError.getErrorCode());

            configError.setErrorCode(null);// reset error code to null
            assertNull(mConfigInterface.getPropertyForKey(null, "AI", configError)); //  Existing Group but invalid key
            assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.InvalidKey, configError.getErrorCode());

            configError.setErrorCode(null);// reset error code to null
            assertNull(mConfigInterface.getPropertyForKey("incorrectKey", "incorrectGroupKey", configError)); // Non Existing Group  and Non Existing key
            assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.GroupNotExists, configError.getErrorCode());

            configError.setErrorCode(null);// reset error code to null
            assertNull(mConfigInterface.getPropertyForKey("incorrectKey", "AI", configError)); //  Existing Group  but Non Existing key
            assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.KeyNotExists, configError.getErrorCode());

            assertNull(mConfigInterface.getPropertyForKey("NewKey", "NonExistingGroupKey", configError));// Non Existing Group  and  Existing key


            //String fetch
            configError.setErrorCode(null);// reset error code to null
            assertNotNull(mConfigInterface.getPropertyForKey("RegistrationEnvironment", "AI", configError));//  Existing Group and  Existing key
            // make sure AI and MicrositeID exist in configuration file else this test case will fail
            assertEquals(null, configError.getErrorCode()); // success

            //Integer fetch
            configError.setErrorCode(null);// reset error code to null
            assertNotNull(mConfigInterface.getPropertyForKey("MicrositeID", "AI", configError));//  Existing Group and  Existing key
            // make sure AI and MicrositeID exist in configuration file else this test case will fail
            assertEquals(null, configError.getErrorCode()); // success

            //String array fetch
            configError.setErrorCode(null);// reset error code to null
            assertNotNull(mConfigInterface.getPropertyForKey("US", "AI", configError));//  Existing Group and  Existing key
            // make sure AI and MicrositeID exist in configuration file else this test case will fail
            assertEquals(null, configError.getErrorCode()); // success

            //Integer array fetch
            configError.setErrorCode(null);// reset error code to null
            assertNotNull(mConfigInterface.getPropertyForKey("EE", "AI", configError));//  Existing Group and  Existing key
            // make sure AI and MicrositeID exist in configuration file else this test case will fail
            assertEquals(null, configError.getErrorCode()); // success

            // fetch map
            configError.setErrorCode(null);// reset error code to null
            assertNotNull(mConfigInterface.getPropertyForKey("Map", "AI", configError));//  Existing Group and  Existing key
            Map<String, String> hmS = new HashMap<>();
            hmS.put("one", "123");
            hmS.put("two", "123.45");
            Object val = hmS;

            Object obj = mConfigInterface.getPropertyForKey("MAP", "AI", configError);
            if (obj instanceof Map) {
                Map<String, String> newMap = (Map<String, String>) obj;
                for (Map.Entry<String, String> entry : newMap.entrySet()) {
                    String key = entry.getKey();
                    assertTrue(entry.getValue().equals(hmS.get(key)));
                }
            }
            assertEquals(null, configError.getErrorCode()); // success
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }

    }


    public void testSetPropertyForKey() throws IllegalArgumentException {
        try {

            AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface.AppConfigurationError();

//        configError.setErrorCode(null);// reset error code to null
//        assertFalse(mConfigInterface.setPropertyForKey("", "", "", configError));//invalid Group and  invalid key
//        assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.InvalidKey, configError.getErrorCode());

//        configError.setErrorCode(null);// reset error code to null
//        assertFalse(mConfigInterface.setPropertyForKey(null, null, "null", configError));////invalid Group and  invalid key
//        assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.InvalidKey, configError.getErrorCode());

//        configError.setErrorCode(null);// reset error code to null
//        assertFalse(mConfigInterface.setPropertyForKey("AI", null, "null", configError)); //  Existing Group and  invalid key
//        assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.InvalidKey, configError.getErrorCode());

            configError.setErrorCode(null);// reset error code to null
            assertTrue(mConfigInterface.setPropertyForKey("NewKey", "NonExistingGroupKey", "val", configError));// Non Existing Group  and  Existing key
            assertEquals(null, configError.getErrorCode());

            //String set
            configError.setErrorCode(null);// reset error code to null
            assertTrue(mConfigInterface.setPropertyForKey("NewKey", "AI", "test", configError));//  Existing Group  and Existing key
            assertEquals(null, configError.getErrorCode());

            //Integer set
            configError.setErrorCode(null);// reset error code to null
            assertTrue(mConfigInterface.setPropertyForKey("MicrositeID", "AI", new Integer(77000), configError));//  Existing Group  and Existing key
            assertEquals(null, configError.getErrorCode());

            //String array set
            List<String> stringArray = new ArrayList<String>();
            stringArray.add("twitter");
            stringArray.add("rss");
            configError.setErrorCode(null);// reset error code to null
            assertTrue(mConfigInterface.setPropertyForKey("US", "AI", stringArray, configError));//  Existing Group  and Existing key
            assertEquals(null, configError.getErrorCode());

            //Integer array set
            List<Integer> integerArray = new ArrayList<Integer>();
            integerArray.add(new Integer(111));
            integerArray.add(new Integer(222));
            configError.setErrorCode(null);// reset error code to null
            assertTrue(mConfigInterface.setPropertyForKey("EE", "AI", integerArray, configError));//  Existing Group  and Existing key
            assertEquals(null, configError.getErrorCode());

        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }
    }

    public void testSetAndGetKey() throws IllegalArgumentException {
        AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface.AppConfigurationError();
        String existingGroup = "APPINFRA";
        String existingKey = "appidentity.micrositeId";
        mConfigInterface.setPropertyForKey(existingKey, existingGroup, "OldValue", configError);

        // Modify a existing Key
        configError.setErrorCode(null);// reset error code to null


        assertNotNull(mConfigInterface.getPropertyForKey(existingKey, existingGroup, configError));//  Existing Group and  Existing key
        // make sure AI and MicrositeID exist in configuration file else this test case will fail
        assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.NoError, configError.getErrorCode()); // success
        configError.setErrorCode(null);// reset error code to null
        assertTrue(mConfigInterface.setPropertyForKey(existingKey, existingGroup, "NewValue", configError));//  Existing Group  and  Existing key with new value

        assertNotNull(mConfigInterface.getDefaultPropertyForKey(existingKey, existingGroup, configError));//  Existing Group and  Existing key
        // make sure AI and MicrositeID exist in configuration file else this test case will fail
        assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.NoError, configError.getErrorCode()); // success
        configError.setErrorCode(null);// reset error code to null
        assertTrue(mConfigInterface.setPropertyForKey(existingKey, existingGroup, "NewValue", configError));//  Existing Group  and Non Existing key

        configError.setErrorCode(null);// reset error code to null
        assertTrue(mConfigInterface.setPropertyForKey("NEWKEY", "AI", "test", configError));//  Existing Group  and Non Existing key
        assertEquals(null, configError.getErrorCode());

        // Add a new String value
        configError.setErrorCode(null);// reset error code to null
        String newlyAddedKey1 = "NEWKEYADDED1";
        String newlyAddedValue1 = "New Value";
        assertTrue(mConfigInterface.setPropertyForKey(newlyAddedKey1, existingGroup, newlyAddedValue1, configError));//  Existing Group  and Non Existing key
        assertEquals(null, configError.getErrorCode());
        configError.setErrorCode(null);// reset error code to null
        assertEquals(newlyAddedValue1, mConfigInterface.getPropertyForKey(newlyAddedKey1, existingGroup, configError));//  Existing Group and  Existing key
        //assertEquals(newlyAddedValue1, mConfigInterface.getDefaultPropertyForKey(newlyAddedKey1, existingGroup, configError));//  Existing Group and  Existing key

        assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.NoError, configError.getErrorCode()); // success

        // Add a new Integer value
        configError.setErrorCode(null);// reset error code to null
        String newlyAddedKey2 = "NEWKEYADDED2";
        Integer integer = new Integer(23);
        assertTrue(mConfigInterface.setPropertyForKey(newlyAddedKey2, existingGroup, integer, configError));//  Existing Group  and Non Existing key
        assertEquals(null, configError.getErrorCode());
        configError.setErrorCode(null);// reset error code to null
        assertEquals(integer, mConfigInterface.getPropertyForKey(newlyAddedKey2, existingGroup, configError));//  Existing Group and  Existing key
        //   assertEquals(integer, mConfigInterface.getDefaultPropertyForKey(newlyAddedKey2, existingGroup, configError));//  Existing Group and  Existing key

        //  assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.NoError, configError.getErrorCode()); // success

        // Add a new String Arraylist value
        configError.setErrorCode(null);// reset error code to null
        String newlyAddedKey3 = "NEWKEYADDED3";
        ArrayList<String> stringArrayList = new ArrayList<String>();
        stringArrayList.add("item1");
        stringArrayList.add("item2");
        assertTrue(mConfigInterface.setPropertyForKey(newlyAddedKey3, existingGroup, stringArrayList, configError));//  Existing Group  and Non Existing key
        assertEquals(null, configError.getErrorCode());
        configError.setErrorCode(null);// reset error code to null
        assertEquals(stringArrayList, mConfigInterface.getPropertyForKey(newlyAddedKey3, existingGroup, configError));//  Existing Group and  Existing key
        // assertEquals(stringArrayList, mConfigInterface.getDefaultPropertyForKey(newlyAddedKey3, existingGroup, configError));//  Existing Group and  Existing key
        assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.NoError, configError.getErrorCode()); // success


        // Add a new Integer ArrayList value
        ArrayList<Integer> integerArrayList = new ArrayList<Integer>();
        integerArrayList.add(new Integer(23));
        integerArrayList.add(new Integer(34));
        integerArrayList.add(new Integer(84));
        configError.setErrorCode(null);// reset error code to null
        String newlyAddedKey4 = "NEWKEYADDED4";
        assertTrue(mConfigInterface.setPropertyForKey(newlyAddedKey4, existingGroup, integerArrayList, configError));//  Existing Group  and Non Existing key
        assertEquals(null, configError.getErrorCode());
        configError.setErrorCode(null);// reset error code to null
        assertEquals(integerArrayList, mConfigInterface.getPropertyForKey(newlyAddedKey4, existingGroup, configError));//  Existing Group and  Existing key
        // assertEquals(integerArrayList, mConfigInterface.getDefaultPropertyForKey(newlyAddedKey4, existingGroup, configError));//  Existing Group and  Existing key
        assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.NoError, configError.getErrorCode()); // success

        // Add a new null value
        configError.setErrorCode(null);// reset error code to null
        assertTrue(mConfigInterface.setPropertyForKey("NewKeyAdded5", existingGroup, null, configError));//  Existing Group  and Non Existing key
        assertEquals(null, configError.getErrorCode());
        configError.setErrorCode(null);// reset error code to null
        assertEquals(null, mConfigInterface.getPropertyForKey("NEWKEYADDED5", existingGroup, configError));//  Existing Group and  Existing key
        // assertEquals(null, mConfigInterface.getDefaultPropertyForKey("NewKeyAdded5", existingGroup, configError));//  Existing Group and  Existing key

        assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.KeyNotExists, configError.getErrorCode()); // success

        // Add a hashmap
        configError.setErrorCode(null);
        Map<String, String> hmS = new HashMap<>();
        hmS.put("Key1", "value1");
        hmS.put("Key2", "value2");
        Object val = hmS;
        assertTrue(mConfigInterface.setPropertyForKey("NewKeyHashMap", existingGroup, val, configError));//  Existing Group  and Non Existing key
        assertEquals(null, configError.getErrorCode());
        configError.setErrorCode(null);// reset error code to null

        Object obj = mConfigInterface.getPropertyForKey("NewKeyHashMap", existingGroup, configError);
        if (obj instanceof Map) {
            Map<String, String> newMap = (Map<String, String>) obj;
            for (Map.Entry<String, String> entry : newMap.entrySet()) {
                String key = entry.getKey();
                assertTrue(entry.getValue().equals(hmS.get(key)));
            }
        }

//        // Add a hashmap
        configError.setErrorCode(null);
        Map<String, Integer> newhmS = new HashMap<>();
        newhmS.put("Key1", 2);
        newhmS.put("Key2", 1);
        Object newval = newhmS;
        assertTrue(mConfigInterface.setPropertyForKey("IntHashMap", existingGroup, newval, configError));//  Existing Group  and Non Existing key
        assertEquals(null, configError.getErrorCode());
        configError.setErrorCode(null);// reset error code to null

        Object newobj = mConfigInterface.getPropertyForKey("NewKeyHashMap", existingGroup, configError);
        if (newobj instanceof Map) {
            Map<String, String> newMap = (Map<String, String>) newobj;
            for (Map.Entry<String, String> entry : newMap.entrySet()) {
                String key = entry.getKey();
                assertTrue(entry.getValue().equals(hmS.get(key)));
            }
        }
    }


    public void testDefaultPropertyForKey() throws IllegalArgumentException {
        try {
            AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface
                    .AppConfigurationError();


            configError.setErrorCode(null);// reset error code to null
            assertNull(mConfigInterface.getDefaultPropertyForKey("", "", configError));//  invalid key and invalid Group
            assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.InvalidKey,
                    configError.getErrorCode());

            configError.setErrorCode(null);// reset error code to null
            assertNull(mConfigInterface.getDefaultPropertyForKey(null, null, configError));// invalid key and invalid Group
            assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.InvalidKey, configError.getErrorCode());

            configError.setErrorCode(null);// reset error code to null
            assertNull(mConfigInterface.getDefaultPropertyForKey(null, "AI", configError)); //  Existing Group but invalid key
            assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.InvalidKey, configError.getErrorCode());

            configError.setErrorCode(null);// reset error code to null
            assertNull(mConfigInterface.getDefaultPropertyForKey("incorrectKey", "incorrectGroupKey", configError)); // Non Existing Group  and Non Existing key
            assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.GroupNotExists, configError.getErrorCode());

            configError.setErrorCode(null);// reset error code to null
            assertNull(mConfigInterface.getDefaultPropertyForKey("incorrectKey", "AI", configError)); //  Existing Group  but Non Existing key
            assertEquals(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum.KeyNotExists, configError.getErrorCode());

            assertNull(mConfigInterface.getDefaultPropertyForKey("NewKey", "NonExistingGroupKey", configError));// Non Existing Group  and  Existing key


            //String fetch
            configError.setErrorCode(null);// reset error code to null
            assertNotNull(mConfigInterface.getDefaultPropertyForKey("RegistrationEnvironment", "AI", configError));//  Existing Group and  Existing key
            // make sure AI and MicrositeID exist in configuration file else this test case will fail
            assertEquals(null, configError.getErrorCode()); // success

            //Integer fetch
            configError.setErrorCode(null);// reset error code to null
            assertNotNull(mConfigInterface.getDefaultPropertyForKey("MicrositeID", "AI", configError));//  Existing Group and  Existing key
            // make sure AI and MicrositeID exist in configuration file else this test case will fail
            assertEquals(null, configError.getErrorCode()); // success

            //String array fetch
            configError.setErrorCode(null);// reset error code to null
            assertNotNull(mConfigInterface.getDefaultPropertyForKey("US", "AI", configError));//  Existing Group and  Existing key
            // make sure AI and MicrositeID exist in configuration file else this test case will fail
            assertEquals(null, configError.getErrorCode()); // success

            //Integer array fetch
            configError.setErrorCode(null);// reset error code to null
            assertNotNull(mConfigInterface.getDefaultPropertyForKey("EE", "AI", configError));//  Existing Group and  Existing key
            // make sure AI and MicrositeID exist in configuration file else this test case will fail
            assertEquals(null, configError.getErrorCode()); // success

        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }

    }

    /*public void testMakeKeyUppercase() {
        try {
            AppConfigurationManager  appConfigurationManager =(AppConfigurationManager) mConfigInterface;

            method = appConfigurationManager.getClass().getDeclaredMethod("makeKeyUppercase", new Class[]{JSONObject.class});
            method.setAccessible(true);
            JSONObject jsonObj = new JSONObject(getJSONString());
            method.invoke(mConfigInterface, jsonObj);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppConfig",
                    e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }*/

    public void testgetMasterConfigFromApp() {
        mConfigInterface = mAppInfra.getConfigInterface();
        try {
            method = mConfigInterface.getClass().getDeclaredMethod("getMasterConfigFromApp");
            method.setAccessible(true);
            method.invoke(mConfigInterface);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "AppConfig",
                    e.getMessage());
        }

    }


    // If someone modifies ConfigValues then this method needs to be updated else test case will fail
    public void testMigration() {
        SecureStorageInterface.SecureStorageError sse;
        SecureStorageInterface.SecureStorageError sse2;
        AppConfigurationManager appConfigurationManager = (AppConfigurationManager) mConfigInterface;
        String mAppConfig_SecureStoreKeyOLD = "ail.app_config";
        String mAppConfig_SecureStoreKey_NEW = "ailNew.app_config";
        SecureStorageInterface ssi = mAppInfra.getSecureStorage();
        JSONObject oldData = null;
        try {
            oldData = new JSONObject(testJsonOld());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // set the previos file to old data with duplicate values
        sse = new SecureStorageInterface.SecureStorageError();
        ssi.storeValueForKey(mAppConfig_SecureStoreKeyOLD, oldData.toString(), sse);
        ssi.removeValueForKey(mAppConfig_SecureStoreKey_NEW);
        // set the new file empty
        sse2 = new SecureStorageInterface.SecureStorageError();
        appConfigurationManager.migrateDynamicData();
        sse = new SecureStorageInterface.SecureStorageError();
        ssi.fetchValueForKey(mAppConfig_SecureStoreKeyOLD, sse);
        assertEquals(sse.getErrorCode(), SecureStorageInterface.SecureStorageError.secureStorageError.UnknownKey);
        sse2 = new SecureStorageInterface.SecureStorageError();
        String newDynamicValue = ssi.fetchValueForKey(mAppConfig_SecureStoreKey_NEW, sse2);
        JSONObject newJSON = null;
        try {
            newJSON = new JSONObject(newDynamicValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if ((testJsonNew().toString()).equalsIgnoreCase(newJSON.toString())) {
            Log.v("MIGRATION", "SUCCESS");
        } else {
            Log.v("MIGRATION", "FAILURE");
        }
        assertEquals(testJsonNew().toString(), newJSON.toString());
    }


    private String testJsonOld() {
        String testJsonString = "{\n" +
                "  \"UR\": {\n" +
                "\n" +
                "    \"DEVELOPMENT\": \"moodifiedData1\",\n" +
                "    \"TESTING\": \"moodifiedData2\",\n" +
                "    \"EVALUATION\": \"4r36zdbeycca933nufcknn2hnpsz6gxu\",\n" +
                "    \"STAGING\": \"f2stykcygm7enbwfw2u9fbg6h6syb8yd\",\n" +
                "    \"PRODUCTION\": \"mz6tg5rqrg4hjj3wfxfd92kjapsrdhy3\"\n" +
                "\n" +
                "  },\n" +
                "  \"AI\": {\n" +
                "    \"MICROSITEID\": 2222,\n" +
                "    \"REGISTRATIONENVIRONMENT\": \"Staging\",\n" +
                "    \"NL\": [\"googleplus\", \"facebook\"  ],\n" +
                "    \"US\": [\"facebook\",\"googleplus\" ],\n" +
                "    \"MAP\": {\"one\": \"123\", \"two\": \"123.45\"},\n" +
                "    \"EE\": [123,234 ]\n" +
                "  }, \n" +
                " \"APPINFRA\": { \n" +
                "   \"APPIDENTITY.NEWKEY1\" : \"101010\",\n" +
                "   \"APPIDENTITY.MICROSITEID\" : \"77000\",\n" +
                "  \"APPIDENTITY.SECTOR\"  : \"B2C\",\n" +
                " \"APPIDENTITY.APPSTATE\"  : \"Staging\",\n" +
                "\"APPIDENTITY.SERVICEDISCOVERYENVIRONMENT\"  : \"Staging\",\n" +
                "\"RESTCLIENT.CACHESIZEINKB\"  : 1024, \n" +
                " \"TAGGING.SENSITIVEDATA\": [\"bundleId, language\"] ,\n" +
                "  \"ABTEST.PRECACHE\":[\"philipsmobileappabtest1content\",\"philipsmobileappabtest1success\"],\n" +
                "    \"CONTENTLOADER.LIMITSIZE\":555,\n" +
                "    \"SERVICEDISCOVERY.PLATFORMMICROSITEID\":\"77000\",\n" +
                "    \"SERVICEDISCOVERY.PLATFORMENVIRONMENT\":\"production\",\n" +
                "    \"APPCONFIG.CLOUDSERVICEID\":\" appinfra.appconfigdownload\"\n" +
                "  }\n" +
                "}\n";

        return testJsonString;
    }


    // expected modified value
    private JSONObject testJsonNew() {
        String testJsonString = "{\n" +
                "  \"UR\": {\n" +
                "\n" +
                "    \"DEVELOPMENT\": \"moodifiedData1\",\n" +
                "    \"TESTING\": \"moodifiedData2\"\n" +
                "\n" +
                "  },\n" +
                "  \"AI\": {\n" +
                "    \"MICROSITEID\": 2222\n" +

                "  }, \n" +
                " \"APPINFRA\": { \n" +

                "    \"CONTENTLOADER.LIMITSIZE\":555\n" +

                "  }\n" +
                "}\n";

        JSONObject obj = null;
        try {
            obj = new JSONObject(testJsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public void testRefreshCloudConfig() {
        AppConfigurationManager appConfigurationManager = (AppConfigurationManager) mAppInfra.getConfigInterface();
        Method method;
        try {
            method = AppConfigurationManager.class.getDeclaredMethod("refreshCloudConfig", AppConfigurationInterface.OnRefreshListener.class);

            method.setAccessible(true);
            AppConfigurationInterface.OnRefreshListener listener = new AppConfigurationInterface.OnRefreshListener() {
                @Override
                public void onError(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum error, String message) {
                    Log.v("refreshCloudConfig", message);
                }

                @Override
                public void onSuccess(REFRESH_RESULT result) {
                    Log.v("refreshCloudConfig", result.toString());

                }
            };
            method.invoke(appConfigurationManager, listener);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void testFetchCloudConfig() {
        AppConfigurationManager appConfigurationManager = (AppConfigurationManager) mAppInfra.getConfigInterface();
        Method method;
        try {
            method = AppConfigurationManager.class.getDeclaredMethod("fetchCloudConfig",String.class, AppConfigurationInterface.OnRefreshListener.class);
            method.setAccessible(true);
            AppConfigurationInterface.OnRefreshListener listener = new AppConfigurationInterface.OnRefreshListener() {
                @Override
                public void onError(AppConfigurationInterface.AppConfigurationError.AppConfigErrorEnum error, String message) {
                    Log.v("refreshCloudConfig", message);
                }

                @Override
                public void onSuccess(REFRESH_RESULT result) {
                    Log.v("refreshCloudConfig", result.toString());

                }
            };
            method.invoke(appConfigurationManager,"url", listener);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void testSaveCloudConfig(){
        AppConfigurationManager appConfigurationManager = (AppConfigurationManager) mAppInfra.getConfigInterface();
        Method method;
        try {
            method = AppConfigurationManager.class.getDeclaredMethod("saveCloudConfig",JSONObject.class, String.class);
            method.setAccessible(true);
           JSONObject jObject = new JSONObject();
            method.invoke(appConfigurationManager,jObject, "url");
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void testClearCloudConfigFile(){
        AppConfigurationManager appConfigurationManager = (AppConfigurationManager) mAppInfra.getConfigInterface();
        Method method;
        try {
            method = AppConfigurationManager.class.getDeclaredMethod("clearCloudConfigFile");
            method.setAccessible(true);
            JSONObject jObject = new JSONObject();
            method.invoke(appConfigurationManager);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
