/*
 * © Koninklijke Philips N.V., 2015, 2016.
 *   All rights reserved.
 */

package com.philips.cdp.dicommclient.communication;

import com.philips.cdp.dicommclient.request.Error;
import com.philips.cdp.dicommclient.request.ResponseHandler;
import com.philips.cdp.dicommclient.subscription.SubscriptionEventListener;

import java.util.Map;

public class NullCommunicationStrategy extends CommunicationStrategy {

    @Override
    public void getProperties(String portName, int productId, ResponseHandler responseHandler) {
        responseHandler.onError(Error.NOT_CONNECTED, null);
    }

    @Override
    public void putProperties(Map<String, Object> dataMap, String portName,
                              int productId, ResponseHandler responseHandler) {
        responseHandler.onError(Error.NOT_CONNECTED, null);
    }

    @Override
    public void addProperties(Map<String, Object> dataMap, String portName, int productId, ResponseHandler responseHandler) {
        responseHandler.onError(Error.NOT_CONNECTED, null);
    }

    @Override
    public void deleteProperties(String portName, int productId, ResponseHandler responseHandler) {
        responseHandler.onError(Error.NOT_CONNECTED, null);
    }

    @Override
    public void subscribe(String portName, int productId, int subscriptionTtl, ResponseHandler responseHandler) {
        responseHandler.onError(Error.NOT_CONNECTED, null);
    }

    @Override
    public void unsubscribe(String portName, int productId, ResponseHandler responseHandler) {
        responseHandler.onError(Error.NOT_CONNECTED, null);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void enableCommunication(
            SubscriptionEventListener subscriptionEventListener) {
    }

    @Override
    public void disableCommunication() {
    }
}
