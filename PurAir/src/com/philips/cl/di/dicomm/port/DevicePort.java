package com.philips.cl.di.dicomm.port;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.philips.cl.di.dev.pa.datamodel.DevicePortProperties;
import com.philips.cl.di.dev.pa.newpurifier.NetworkNode;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dicomm.communication.CommunicationStrategy;

public class DevicePort extends DICommPort<DevicePortProperties> {

    private final String DEVICEPORT_NAME = "device";
    private final int DEVICEPORT_PRODUCTID = 1;

    public DevicePort(NetworkNode networkNode, CommunicationStrategy communicationStrategy) {
        super(networkNode, communicationStrategy);
    }

    @Override
    public boolean isResponseForThisPort(String response) {
        return (parseResponse(response) == null);
    }

    @Override
    public void processResponse(String response) {
        DevicePortProperties devicePortInfo = parseResponse(response);
        if (devicePortInfo != null) {
            setPortProperties(devicePortInfo);
            return;
        }
        ALog.e(ALog.DEVICEPORT, "DevicePort Info should never be NULL");
    }

    @Override
    public String getDICommPortName() {
        return DEVICEPORT_NAME;
    }

    @Override
    public int getDICommProductId() {
        return DEVICEPORT_PRODUCTID;
    }

    @Override
    public boolean supportsSubscription() {
        // TODO DIComm Refactor check if subscription to deviceport is necessary
        return false;
    }

    private DevicePortProperties parseResponse(String response) {
        if (response == null || response.isEmpty()) {
            return null;
        }
        Gson gson = new GsonBuilder().create();
        DevicePortProperties devicePortInfo = null;
        try {
            devicePortInfo = gson.fromJson(response, DevicePortProperties.class);
        } catch (JsonSyntaxException e) {
            ALog.e(ALog.PARSER, "JsonSyntaxException");
        } catch (JsonIOException e) {
            ALog.e(ALog.PARSER, "JsonIOException");
        } catch (Exception e2) {
            ALog.e(ALog.PARSER, "Exception");
        }
        return devicePortInfo;
    }

}
