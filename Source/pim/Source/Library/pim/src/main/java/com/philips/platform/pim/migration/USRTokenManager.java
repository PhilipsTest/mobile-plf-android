package com.philips.platform.pim.migration;

import androidx.core.util.Pair;
import android.text.TextUtils;
import android.util.Base64;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;
import com.philips.platform.appinfra.timesync.TimeInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pim.listeners.RefreshUSRTokenListener;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.pim.rest.PIMRestClient;
import com.philips.platform.pim.rest.RefreshUSRTokenRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static com.philips.platform.appinfra.logging.LoggingInterface.LogLevel.DEBUG;

class USRTokenManager {
    private static final String JR_CAPTURE_REFRESH_SECRET = "jr_capture_refresh_secret";
    private static final String JR_CAPTURE_SIGNED_IN_USER = "jr_capture_signed_in_user";
    private static final String JR_CAPTURE_FLOW = "jr_capture_flow";
    private String TAG = PIMMigrationManager.class.getSimpleName();
    private final String USR_BASEURL = "userreg.janrain.api.v2";
    private String signedInUser;
    private LoggingInterface mLoggingInterface;
    private AppInfraInterface appInfraInterface;

    USRTokenManager(AppInfraInterface mAppInfraInterface) {
        this.appInfraInterface = mAppInfraInterface;
        mLoggingInterface = PIMSettingManager.getInstance().getLoggingInterface();
    }

    void fetchRefreshedAccessToken(RefreshUSRTokenListener refreshUSRTokenListener) {
        downloadUserUrlFromSD(new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                if (urlMap != null && urlMap.get(USR_BASEURL) != null && urlMap.get(USR_BASEURL).getConfigUrls() != null) {
                    ServiceDiscoveryService serviceDiscoveryService = urlMap.get(USR_BASEURL);
                    String baseurl = serviceDiscoveryService.getConfigUrls();
                    String refreshUrl = baseurl + "/oauth/refresh_access_token";
                    String locale = serviceDiscoveryService.getLocale();
                    mLoggingInterface.log(DEBUG, TAG, "downloadUserUrlFromSD onSuccess. Refresh Url : " + baseurl + " Locale : " + locale);
                    refreshUSRAccessToken(refreshUrl, locale, refreshUSRTokenListener);
                    mLoggingInterface.log(DEBUG, TAG, "downloadUserUrlFromSD onSuccess");
                } else {
                    mLoggingInterface.log(DEBUG, TAG, "Migration Failed!! " + " Error in downloadUserUrlFromSD : " + "Not able to fetch config url");
                }
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                mLoggingInterface.log(DEBUG, TAG, "Migration Failed!! " + " Error in downloadUserUrlFromSD : " + message);
            }
        });
    }

    private String fetchDataFromSecureStorage(String jrCaptureSignedInUserKey) {
        return appInfraInterface.getSecureStorage().fetchValueForKey(jrCaptureSignedInUserKey, new SecureStorageInterface.SecureStorageError());
    }

    private Object getClientIdFromConfig() {
        return appInfraInterface.getConfigInterface().getPropertyForKey("JanRainConfiguration.RegistrationClientID", "PIM", new AppConfigurationInterface.AppConfigurationError());
    }

    private void downloadUserUrlFromSD(ServiceDiscoveryInterface.OnGetServiceUrlMapListener serviceUrlMapListener) {
        ArrayList<String> serviceIdList = new ArrayList<>();
        serviceIdList.add(USR_BASEURL);
        appInfraInterface.getServiceDiscovery().getServicesWithCountryPreference(serviceIdList, serviceUrlMapListener, null);
    }

    private String getUSRAccessToken() {
        if (signedInUser == null)
            return null;
        else {
            try {
                JSONObject jsonObject = new JSONObject(signedInUser);
                return jsonObject.getString("accessToken");
            } catch (JSONException e) {
                mLoggingInterface.log(DEBUG, TAG, "USR Access token failed to parse " + e.getMessage());
                return null;
            }
        }
    }

    private <L, R> Collection<L> map(Collection<R> collection, Function<L, R> f) {
        Collection<L> retCollection;
        try {
            retCollection = collection.getClass().newInstance();
        } catch (InstantiationException ignore) {
            retCollection = new ArrayList<L>();
        } catch (IllegalAccessException ignore) {
            retCollection = new ArrayList<L>();
        }

        for (R e : collection) retCollection.add(f.operate(e));

        return retCollection;
    }

    private String paramsToString(Set<Pair<String, String>> bodyParams, String encoder) {
        Collection<String> paramPairs = map(bodyParams, val -> {
            try {
                return val.first.concat("=").concat(URLEncoder.encode(val.second, encoder));
            } catch (UnsupportedEncodingException e) {
                mLoggingInterface.log(DEBUG, TAG, "paramsToString failed with error : " + e.getMessage());
                return null;
            }
        });
        return TextUtils.join("&", paramPairs);
    }

    private void refreshUSRAccessToken(String refreshUrl, String locale, RefreshUSRTokenListener refreshUSRTokenListener) {
        if (signedInUser == null) {
            mLoggingInterface.log(DEBUG, TAG, "Migration Failed!! " + "Signed_in_user not found");
            return;
        }

        String date = getUTCdatetimeAsString();
        String legacyToken = getUSRAccessToken();
        mLoggingInterface.log(DEBUG, TAG, "USR AccessToken : " + legacyToken);

        HashSet<Pair<String, String>> params = getParams(locale, date, legacyToken);

        makeRefreshUSRTokenRequest(refreshUrl, refreshUSRTokenListener, params);
    }

    private void makeRefreshUSRTokenRequest(String refreshUrl, RefreshUSRTokenListener refreshUSRTokenListener, HashSet<Pair<String, String>> params) {
        RefreshUSRTokenRequest refreshLegacyTokenRequest = new RefreshUSRTokenRequest(refreshUrl, paramsToString(params, "UTF-8"));
        PIMRestClient pimRestClient = new PIMRestClient(PIMSettingManager.getInstance().getRestClient());
        pimRestClient.invokeRequest(refreshLegacyTokenRequest, response -> {
            try {
                JSONObject tokenObject = new JSONObject(response);
                String usrAccessToken = tokenObject.getString("access_token");
                mLoggingInterface.log(DEBUG, TAG, "Refresh USR token success. New Access Token :" + usrAccessToken);
                refreshUSRTokenListener.onRefreshTokenSuccess(usrAccessToken);
            } catch (JSONException e) {
                refreshUSRTokenListener.onRefreshTokenFailed(new Error(e.hashCode(), e.getMessage()));
            }
        }, error -> {
            refreshUSRTokenListener.onRefreshTokenFailed(new Error(error.hashCode(), error.getLocalizedMessage()));
            mLoggingInterface.log(DEBUG, TAG, "Migration Failed!! " + "Body for refresh token request : " + error.toString());
        });
    }

    private HashSet<Pair<String, String>> getParams(String locale, String date, String legacyToken) {
        HashSet<Pair<String, String>> params = new HashSet<>();
        params.add(new Pair<>("locale", parseLocale(locale)));
        params.add(new Pair<>("signature", getRefreshSignature(date, legacyToken)));
        params.add(new Pair<>("date", date));
        params.add(new Pair<>("flow", "standard"));
        params.add(new Pair<>("flow_version", "HEAD"));
        params.add(new Pair<>("access_token", legacyToken));
        params.add(new Pair<>("client_id", PIMSettingManager.getInstance().getPimOidcConfigration().getLegacyClientID()));
        return params;
    }

    private String parseLocale(String locale) {
        if (locale.contains("_")) {
            String[] splitLocal = locale.split("_");
            locale = splitLocal[0] + "-" + splitLocal[1];
        }
        return locale;
    }

    private String getUTCdatetimeAsString() {
        String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ROOT);
        sdf.setTimeZone(TimeZone.getTimeZone(TimeInterface.UTC));
        TimeInterface timeInterface = appInfraInterface.getTime();
        if (timeInterface != null) {
            return sdf.format(timeInterface.getUTCTime());
        }
        return null;
    }

    private String getRefreshSignature(String date, String accessToken) {
        String refresh_secret = fetchDataFromSecureStorage(JR_CAPTURE_REFRESH_SECRET);
        if (refresh_secret == null) {
            mLoggingInterface.log(DEBUG, TAG, "refresh secret is null");
            return null;
        }
        String stringToSign = "refresh_access_token\n" + date + "\n" + accessToken + "\n";
        byte[] hash;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            byte[] refreshSecret = refresh_secret.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secret = new SecretKeySpec(refreshSecret, mac.getAlgorithm());
            mac.init(secret);
            hash = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        } catch (InvalidKeyException | NoSuchAlgorithmException var7) {
            mLoggingInterface.log(DEBUG, TAG, "getRefreshSignature failed");
            return null;
        }
        return Base64.encodeToString(hash, 2);
    }

    boolean isUSRUserAvailable() {
        signedInUser = fetchDataFromSecureStorage(JR_CAPTURE_SIGNED_IN_USER);
        return signedInUser != null;
    }

    void deleteUSRFromSecureStorage() {
        appInfraInterface.getSecureStorage().removeValueForKey(JR_CAPTURE_SIGNED_IN_USER);
        appInfraInterface.getSecureStorage().removeValueForKey(JR_CAPTURE_FLOW);
        appInfraInterface.getSecureStorage().removeValueForKey(JR_CAPTURE_REFRESH_SECRET);
    }

    /**
     * A first-class function stand-in
     *
     * @param <L> the return type of the function
     * @param <R> the parameter type of the function
     */
    interface Function<L, R> {
        /**
         * The function's implementation
         *
         * @param arg
         * @return the function's evaluation on arg
         */
        L operate(R arg);
    }
}
