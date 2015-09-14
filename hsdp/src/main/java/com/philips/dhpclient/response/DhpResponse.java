package com.philips.dhpclient.response;

import java.util.*;

import com.philips.dhpclient.util.MapUtils;

public class DhpResponse {
    public static DhpResponse SUCCESS;

    static {
        Map<String, Object> SUCCESS_MAP = new HashMap<String, Object>();
        SUCCESS_MAP.put("responseCode", "200");
        SUCCESS_MAP.put("responseMessage", "Success");

        SUCCESS = new DhpResponse(SUCCESS_MAP);
    }

    public final Map<String, Object> rawResponse;
    public final String responseCode;
    public final String message;

    public DhpResponse(Map<String, Object> rawResponse) {
        this.rawResponse = rawResponse;
        this.responseCode = MapUtils.extract(rawResponse, "responseCode");
        this.message = MapUtils.extract(rawResponse, "responseMessage");
    }

    public DhpResponse(String responseCode, Map<String, Object> rawResponse) {
        this.rawResponse = rawResponse;
        this.responseCode = responseCode;
        this.message = MapUtils.extract(rawResponse, "responseMessage");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DhpResponse response = (DhpResponse) o;
        return Objects.equals(rawResponse, response.rawResponse) &&
               Objects.equals(responseCode, response.responseCode) &&
               Objects.equals(message, response.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawResponse, responseCode, message);
    }

    @Override
    public String toString() {
        return "DhpResponse{" +
               "rawResponse=" + rawResponse +
               ", responseCode='" + responseCode + '\'' +
               ", message='" + message + '\'' +
               '}';
    }
}
