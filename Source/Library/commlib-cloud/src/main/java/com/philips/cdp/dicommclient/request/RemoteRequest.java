/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp.dicommclient.request;

import android.support.annotation.NonNull;
import android.util.Log;

import com.philips.cdp.cloudcontroller.api.CloudController;
import com.philips.cdp.cloudcontroller.api.listener.DcsResponseListener;
import com.philips.cdp.cloudcontroller.api.listener.PublishEventListener;
import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.cdp2.commlib.core.util.GsonProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;

public class RemoteRequest extends Request implements DcsResponseListener, PublishEventListener {

    private static final String TAG = "RemoteRequest";

    private static final int CPP_DEVICE_CONTROL_TIMEOUT = 30000;
    private static final int SUCCESS = 0;
    private static String BASEDATA_PORTS = "{ \"product\":\"%d\",\"port\":\"%s\",\"data\":%s}";
    private static final String DICOMM_REQUEST = "DICOMM-REQUEST";
    private static int REQUEST_PRIORITY = 20;
    private static int REQUEST_TTL = 5;
    private final String cppId;

    private String mResponse;
    private int mMessageId;
    private String mConversationId;
    private String mPortName;
    private int mProductId;

    private CloudController cloudController;
    private final RemoteRequestType mRequestType;

    public RemoteRequest(String cppId, String portName, int productId, RemoteRequestType requestType, Map<String, Object> dataMap, ResponseHandler responseHandler, final CloudController cloudController) {
        super(dataMap, responseHandler);
        this.cppId = cppId;
        this.cloudController = cloudController;
        mRequestType = requestType;
        mPortName = portName;
        mProductId = productId;
    }

    private String createDataToSend(String portName, int productId, Map<String, Object> dataMap) {
        String data = GsonProvider.get().toJson(dataMap, Map.class);
        String dataToSend = String.format(Locale.US, BASEDATA_PORTS, productId, portName, data);

        DICommLog.i(DICommLog.REMOTEREQUEST, "Data to send: " + dataToSend);
        return dataToSend;
    }

    @Override
    public Response execute() {
        DICommLog.d(DICommLog.REMOTEREQUEST, "Start request REMOTE");
        cloudController.addDCSResponseListener(this);
        cloudController.addPublishEventListener(this);

        String mEventData = createDataToSend(mPortName, mProductId, mDataMap);
        mMessageId = cloudController.publishEvent(mEventData, DICOMM_REQUEST, mRequestType.getMethod(),
                "", REQUEST_PRIORITY, REQUEST_TTL, cppId);
        try {
            long startTime = System.currentTimeMillis();
            synchronized (this) {
                wait(CPP_DEVICE_CONTROL_TIMEOUT);
            }
            if ((System.currentTimeMillis() - startTime) > CPP_DEVICE_CONTROL_TIMEOUT) {
                DICommLog.e(DICommLog.REMOTEREQUEST, "Timeout occured");
            }
        } catch (InterruptedException e) {
            // NOP
        }

        cloudController.removePublishEventListener(this);
        cloudController.removeDCSResponseListener(this);

        if (mResponse == null) {
            DICommLog.e(DICommLog.REMOTEREQUEST, "Request failed - null reponse, failed to publish event or request timeout");
            DICommLog.d(DICommLog.REMOTEREQUEST, "Stop request REMOTE - Failure");
            return new Response(null, Error.REQUEST_FAILED, mResponseHandler);
        }

        DICommLog.i(DICommLog.REMOTEREQUEST, "Received data: " + mResponse);
        DICommLog.d(DICommLog.REMOTEREQUEST, "Stop request REMOTE - Success");

        mResponse = extractData(mResponse);

        return new Response(mResponse, null, mResponseHandler);
    }

    @Override
    public void onDCSResponseReceived(final @NonNull String dcsResponse, final @NonNull String conversationId) {
        if (mConversationId != null && mConversationId.equals(conversationId)) {
            DICommLog.i(DICommLog.REMOTEREQUEST, "DCSEvent received from the right request");
            mResponse = dcsResponse;
            synchronized (this) {
                DICommLog.i(DICommLog.REMOTEREQUEST, "Notified on DCS Response");
                notify();
            }
        } else {
            DICommLog.i(DICommLog.REMOTEREQUEST, "DCSEvent received from different request - ignoring");
        }
    }

    @Override
    public void onPublishEventReceived(int status, int messageId, final @NonNull String conversationId) {
        if (mMessageId == messageId) {
            DICommLog.i(DICommLog.REMOTEREQUEST, "Publish event received from the right request - status: " + status);
            if (status == SUCCESS) {
                mConversationId = conversationId;
            } else {
                synchronized (this) {
                    DICommLog.e(DICommLog.REMOTEREQUEST, "Publish Event Failed");
                    notify();
                }
            }
        } else {
            DICommLog.i(DICommLog.REMOTEREQUEST, "Publish event received from different request - ignoring");
        }
    }

    private String extractData(final String data) {
        String res = data;

        try {
            JSONObject jsonObject = new JSONObject(data);
            int status = jsonObject.getInt("status");
            JSONObject dataObject = jsonObject.optJSONObject("data");

            if (status > 0) {
                Log.e(TAG, "extractData: code received: " + status + "");
            } else if (dataObject == null) {
                Log.e(TAG, "extractData: no data received: " + data + "");
            } else {
                res = dataObject.toString();
            }
        } catch (JSONException e) {
            DICommLog.i(DICommLog.REMOTEREQUEST, "JSONException: " + e.getMessage());
        }

        return res;
    }
}
