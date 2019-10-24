package com.philips.cdp.registration.hsdp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.philips.cdp.registration.configuration.HSDPConfiguration;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.ServerTime;
import com.philips.platform.appinfra.apisigning.HSDPPHSApiSigning;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;

class HsdpRequestClient {
    private String TAG = HsdpRequestClient.class.getSimpleName();
    private final static ObjectMapper JSON_MAPPER;

    static {
        JSON_MAPPER = new ObjectMapper();
        JSON_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private HSDPConfiguration hsdpConfiguration;

    HsdpRequestClient(HSDPConfiguration hsdpConfiguration) {
        this.hsdpConfiguration = hsdpConfiguration;
    }

    private void sign(Map<String, String> headers, String url, String queryParams, String httpMethod, String body) {
        HSDPPHSApiSigning hsdpphsApiSigning = new HSDPPHSApiSigning(hsdpConfiguration.getHsdpSharedId(), hsdpConfiguration.getHsdpSecretId());
        String authHeaderValue = hsdpphsApiSigning.createSignature(httpMethod, queryParams, headers, url, body);
        headers.put("Authorization", authHeaderValue);
    }

    private String asJsonString(Object request) {
        try {
            return JSON_MAPPER.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    Map<String, Object> sendSignedRequestForSocialLogin(String httpMethod, String apiEndpoint, String queryParams, Map<String, String> headers, Object body) {
        String bodyString = asJsonString(body);
        addSignedDateHeader(headers);

        sign(headers, apiEndpoint, queryParams, httpMethod, bodyString);
        URI uri = URI.create(hsdpConfiguration.getHsdpBaseUrl() + apiEndpoint + queryParams(queryParams));
        return sendRestRequest(httpMethod, uri, headers, bodyString);
    }

    Map<String, Object> sendRestRequest(String apiEndpoint, String queryParams, Map<String, String> headers, Object body) {
        String bodyString = asJsonString(body);
        URI uri = URI.create(hsdpConfiguration.getHsdpBaseUrl() + apiEndpoint + queryParams(queryParams));
        return sendRestRequest("PUT", uri, headers, bodyString);
    }

    private Map<String, Object> sendRestRequest(String httpMethod, URI uri, Map<String, String> headers, String body) {
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        Map<String, Object> rawResponse = null;
        try {
            rawResponse = establishConnection(uri, httpMethod, headers, body);
        } catch (IOException e) {
            RLog.e(TAG, "sendRestRequest : Exception Occured :" + e.getMessage());
        } catch (IllegalArgumentException e){
            RLog.e(TAG, "sendRestRequest : Exception Occured :" + e.getMessage());
        }

        return rawResponse;

    }

    private Map<String, Object> establishConnection(URI uri, String httpMethod, Map<String, String> headers, String body) throws IOException {
        HttpURLConnection urlConnection = openHttpURLConnection(uri);
        try {
            urlConnection.setRequestMethod(httpMethod);
            addRequestHeaders(headers, urlConnection);

            addRequestBody(body, urlConnection);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return readStream(in);
        } finally {
            urlConnection.disconnect();
        }

    }

    private Map readStream(InputStream in) throws IOException {
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);

        in.close();

        return JSON_MAPPER.readValue(responseStrBuilder.toString(), Map.class);
    }

    private HttpURLConnection openHttpURLConnection(URI uri) throws IOException {
        return (HttpURLConnection) uri.toURL().openConnection();
    }

    private void addRequestHeaders(Map<String, String> headers, HttpURLConnection urlConnection) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private void addRequestBody(String body, HttpURLConnection urlConnection) throws IOException {
        urlConnection.setDoOutput(true);
        urlConnection.setChunkedStreamingMode(0);

        OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
        writeStream(out, body);
    }

    private void writeStream(OutputStream out, String body) throws IOException {
        out.write(body.getBytes(Charset.forName("UTF-8")));
        out.flush();
    }

    private void addSignedDateHeader(Map<String, String> headers) {
        headers.put("SignedDate", UTCDatetimeAsString());
    }

    private String queryParams(String queryParamString) {
        if (queryParamString == null || queryParamString.isEmpty())
            return "";

        return "?" + queryParamString;
    }

    private String UTCDatetimeAsString() {
        return ServerTime.getCurrentUTCTimeWithFormat(ServerTime.DATE_FORMAT);
    }

}
