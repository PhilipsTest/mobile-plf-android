/*
 * © Koninklijke Philips N.V., 2015.
 *   All rights reserved.
 */

package com.philips.cdp.dicommclient.port.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.philips.cdp.dicommclient.communication.CommunicationStrategy;
import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp.dicommclient.port.DICommPort;
import com.philips.cdp.dicommclient.util.DICommLog;

import java.util.HashMap;
import java.util.Map;

public class WifiUIPort extends DICommPort<WifiUIPortProperties> {

    private static final String KEY_SETUP = "setup";
    private static final String KEY_CONNECTION = "connection";
    private final String WIFIUIPORT_NAME = "wifiui";
    private final int WIFIUIPORT_PRODUCTID = 1;

    public WifiUIPort(NetworkNode networkNode, CommunicationStrategy communicationStrategy) {
        super(networkNode, communicationStrategy);
    }

    @Override
    public boolean isResponseForThisPort(String jsonResponse) {
        return (parseResponse(jsonResponse) != null);
    }

    @Override
    public void processResponse(String jsonResponse) {
        WifiUIPortProperties properties = parseResponse(jsonResponse);
        if (properties != null) {
            setPortProperties(properties);
            return;
        }
        DICommLog.e(DICommLog.WIFIUIPORT, "WifiUI port properties should never be NULL");
    }

    @Override
    public String getDICommPortName() {
        return WIFIUIPORT_NAME;
    }

    @Override
    public int getDICommProductId() {
        return WIFIUIPORT_PRODUCTID;
    }

    @Override
    public boolean supportsSubscription() {
        // TODO DIComm Refactor check if subscription to deviceport is necessary
        return false;
    }

    private WifiUIPortProperties parseResponse(String response) {
        if (response == null || response.isEmpty()) {
            return null;
        }
        Gson gson = new GsonBuilder().create();
        WifiUIPortProperties properties = null;
        try {
            properties = gson.fromJson(response, WifiUIPortProperties.class);
        } catch (JsonSyntaxException e) {
            DICommLog.e(DICommLog.WIFIUIPORT, "JsonSyntaxException");
        } catch (JsonIOException e) {
            DICommLog.e(DICommLog.WIFIUIPORT, "JsonIOException");
        } catch (Exception e2) {
            DICommLog.e(DICommLog.WIFIUIPORT, "Exception");
        }
        return properties;
    }

    public void disableDemoMode() {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put(KEY_SETUP, "inactive");
        dataMap.put(KEY_CONNECTION, "disconnected");
        putProperties(dataMap);
    }
}
