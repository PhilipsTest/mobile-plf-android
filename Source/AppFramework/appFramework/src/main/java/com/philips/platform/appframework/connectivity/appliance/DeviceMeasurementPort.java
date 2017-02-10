/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.appframework.connectivity.appliance;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.philips.cdp.dicommclient.port.DICommPort;
import com.philips.cdp2.commlib.core.communication.CommunicationStrategy;

public class DeviceMeasurementPort extends DICommPort<DeviceMeasurementPortProperties> {
    private static final String TAG = "DeviceMeasurementPort";

    private static final String DEVICE_MEASUREMENT_PORT_NAME = "devicemeasurement";
    private static final int DEVICE_MEAUREMENT_PRODUCT_ID = 1;

    public DeviceMeasurementPort(CommunicationStrategy communicationStrategy) {
        super(communicationStrategy);
    }

    @Override
    public boolean isResponseForThisPort(String jsonResponse) {
        return (parseResponse(jsonResponse) != null);
    }

    @Override
    protected void processResponse(String jsonResponse) {
        DeviceMeasurementPortProperties deviceMeasurementPortProperties = parseResponse(jsonResponse);
        if (deviceMeasurementPortProperties != null) {
            setPortProperties(deviceMeasurementPortProperties);
            return;
        }
    }

    @Override
    protected String getDICommPortName() {
        return DEVICE_MEASUREMENT_PORT_NAME;
    }

    @Override
    protected int getDICommProductId() {
        return DEVICE_MEAUREMENT_PRODUCT_ID;
    }

    @Override
    public boolean supportsSubscription() {
        return false;
    }

    private DeviceMeasurementPortProperties parseResponse(String response) {
        if (response == null || response.isEmpty()) {
            return null;
        }
        Gson gson = new GsonBuilder().create();
        DeviceMeasurementPortProperties deviceMeasurementPortProperties = null;
        try {
            deviceMeasurementPortProperties = gson.fromJson(response, DeviceMeasurementPortProperties.class);
        } catch (JsonSyntaxException | JsonIOException e) {
            Log.e(TAG, e.getMessage());
        }

        return deviceMeasurementPortProperties;
    }
}
