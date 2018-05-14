package com.philips.platform.mya.catk;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.mya.catk.datamodel.CachedConsentStatus;
import com.philips.platform.pif.chi.PostConsentTypeCallback;
import com.philips.platform.pif.chi.datamodel.ConsentStates;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Consent cache implementation.
 *
 * @since 18.2.0s
 */

public class ConsentCache implements ConsentCacheInterface {

    private String CONSENT_CACHE_KEY = "CONSENT_CACHE";
    private String CONSENT_EXPIRY_KEY = "ConsentCacheTTLInMinutes";
    private AppInfraInterface appInfra;
    private Gson objGson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeSerializer())
            .registerTypeAdapter(DateTime.class, new DateTimeDeSerializer()).create();
    private Map<String, CachedConsentStatus> inMemoryCache;

    public ConsentCache(AppInfraInterface appInfra) {
        this.appInfra = appInfra;
    }

    @Override
    public void fetchConsentTypeState(String consentType, FetchConsentCacheCallback callback) {
        if (inMemoryCache != null) {
            callback.onGetConsentsSuccess(inMemoryCache.get(consentType));
            return;
        }
        inMemoryCache = getMapFromSecureStorage();
        if (inMemoryCache == null) {
            callback.onGetConsentsSuccess(null);
        } else {
            callback.onGetConsentsSuccess(inMemoryCache.get(consentType));
        }

    }


    @Override
    public void storeConsentTypeState(String consentType, ConsentStates status, int version, PostConsentTypeCallback callback) {
        if (inMemoryCache == null) {
            inMemoryCache = new HashMap<>();
        }
        inMemoryCache.put(consentType, new CachedConsentStatus(status, version, (new DateTime(DateTimeZone.UTC)).plusMinutes(getConfiguredExpiryTime())));
        writeMapToSecureStorage(inMemoryCache);
    }

    private int getConfiguredExpiryTime() {
        AppConfigurationInterface appConfigInterface = appInfra.getConfigInterface();
        AppConfigurationInterface.AppConfigurationError error = new AppConfigurationInterface.AppConfigurationError();
        return (int) appConfigInterface.getPropertyForKey(CONSENT_EXPIRY_KEY, "css", error);
    }

    private Map<String, CachedConsentStatus> getMapFromSecureStorage() {
        String serializedCache = appInfra.getSecureStorage().fetchValueForKey(CONSENT_CACHE_KEY, getSecureStorageError());
        Type listType = new TypeToken<Map<String, CachedConsentStatus>>() {
        }.getType();
        return objGson.fromJson(serializedCache, listType);
    }

    private synchronized void writeMapToSecureStorage(Map<String, CachedConsentStatus> cacheMap) {
        appInfra.getSecureStorage().storeValueForKey(CONSENT_CACHE_KEY, objGson.toJson(cacheMap), getSecureStorageError());
    }

    @VisibleForTesting
    @NonNull
    SecureStorageInterface.SecureStorageError getSecureStorageError() {
        return new SecureStorageInterface.SecureStorageError();
    }

    class DateTimeSerializer implements JsonSerializer {
        @Override
        public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    class DateTimeDeSerializer implements JsonDeserializer {

        @Override
        public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new DateTime(json.getAsJsonPrimitive().getAsString(), DateTimeZone.UTC);
        }
    }


}
