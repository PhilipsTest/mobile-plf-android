package com.philips.platform.pim.rest;

import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pim.manager.PIMSettingManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MarketInOptedInRequest implements PIMRequestInterface {

    public static final String OPTIN_URL = "Optin_url";
    public static final String API_KEY = "Api-Key";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String LOCALE = "locale";
    public static final String CONSENT_MARKETING_OPTEDIN = "consentEmailMarketing";

    private Map<String,String> requestData;
    private final String TAG = MarketInOptedInRequest.class.getSimpleName();

    public MarketInOptedInRequest(Map<String,String> requestData) {
        this.requestData = requestData;
    }

    @Override
    public String getUrl() {
        return requestData.get(OPTIN_URL);
    }

    @Override
    public Map<String, String> getHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Api-Key", requestData.get(API_KEY));
        headers.put("Api-Version", "1");
        headers.put("Accept-Encoding", "gzip");
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + requestData.get(ACCESS_TOKEN));
        headers.put("Content-Type", "application/json");
        return headers;
    }

    @Override
    public String getBody() {
        JSONObject bodyJson = new JSONObject();
        JSONObject dataJson = new JSONObject();
        JSONObject attributes = new JSONObject();
        JSONObject consentEmailMarketing = new JSONObject();
        try {
            consentEmailMarketing.put("optedIn", Boolean.parseBoolean(requestData.get(CONSENT_MARKETING_OPTEDIN)));

            attributes.put("consentEmailMarketing", consentEmailMarketing);
            attributes.put("locale", requestData.get(LOCALE));

            dataJson.put("type", "user");
            dataJson.put("attributes", attributes);

            bodyJson.put("data",dataJson);
        } catch (JSONException e) {
            PIMSettingManager.getInstance().getLoggingInterface().log(LoggingInterface.LogLevel.DEBUG, TAG, "Json Exception : " + e.getMessage());
        }
        return bodyJson.toString();
    }

    @Override
    public int getMethodType() {
        return PIMRequest.Method.PATCH;
    }
}
