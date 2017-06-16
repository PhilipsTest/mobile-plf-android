/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.commlib.lan.communication;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.philips.cdp.dicommclient.networknode.ConnectionState;
import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp.dicommclient.request.Error;
import com.philips.cdp.dicommclient.request.ExchangeKeyRequest;
import com.philips.cdp.dicommclient.request.GetKeyRequest;
import com.philips.cdp.dicommclient.request.Request;
import com.philips.cdp.dicommclient.request.RequestQueue;
import com.philips.cdp.dicommclient.request.ResponseHandler;
import com.philips.cdp.dicommclient.security.DISecurity;
import com.philips.cdp.dicommclient.security.DISecurity.EncryptionDecryptionFailedListener;
import com.philips.cdp.dicommclient.subscription.LocalSubscriptionHandler;
import com.philips.cdp.dicommclient.subscription.SubscriptionEventListener;
import com.philips.cdp.dicommclient.subscription.UdpEventReceiver;
import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.cdp2.commlib.core.communication.CommunicationStrategy;
import com.philips.cdp2.commlib.lan.security.SslPinTrustManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import static com.philips.cdp.dicommclient.util.DICommLog.LOCALREQUEST;

public class LanCommunicationStrategy extends CommunicationStrategy {
    private RequestQueue mRequestQueue;
    private DISecurity diSecurity;
    private boolean isKeyExchangeOngoing;
    private LocalSubscriptionHandler mLocalSubscriptionHandler;
    private final NetworkNode networkNode;
    @Nullable
    private SSLContext sslContext;

    private EncryptionDecryptionFailedListener mEncryptionDecryptionFailedListener = new EncryptionDecryptionFailedListener() {

        @Override
        public void onDecryptionFailed(NetworkNode networkNode) {
            triggerKeyExchange(networkNode);
        }

        @Override
        public void onEncryptionFailed(NetworkNode networkNode) {
            triggerKeyExchange(networkNode);
        }
    };

    private final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            DICommLog.d(LOCALREQUEST, String.format(Locale.US, "NetworkNode changed: property=%s, old value=%s, new value=%s", evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));

            // TODO Do whatever needed when the pin changes
        }
    };

    public LanCommunicationStrategy(@NonNull final NetworkNode networkNode) {
        this.networkNode = networkNode;
        this.networkNode.addPropertyChangeListener(propertyChangeListener);

        if (networkNode.isHttps()) {
            sslContext = createSSLContext();
        }

        this.diSecurity = new DISecurity(networkNode);
        this.diSecurity.setEncryptionDecryptionFailedListener(mEncryptionDecryptionFailedListener);

        mRequestQueue = createRequestQueue();
        mLocalSubscriptionHandler = new LocalSubscriptionHandler(diSecurity, UdpEventReceiver.getInstance());
    }

    @VisibleForTesting
    RequestQueue createRequestQueue() {
        return new RequestQueue();
    }

    @Override
    public void getProperties(String portName, int productId, ResponseHandler responseHandler) {
        exchangeKeyIfNecessary(networkNode);
        Request request = new LanRequest(networkNode, sslContext, portName, productId, LanRequestType.GET, null, responseHandler, diSecurity);
        mRequestQueue.addRequest(request);
    }

    @Override
    public void putProperties(Map<String, Object> dataMap, String portName, int productId, ResponseHandler responseHandler) {
        exchangeKeyIfNecessary(networkNode);
        Request request = new LanRequest(networkNode, sslContext, portName, productId, LanRequestType.PUT, dataMap, responseHandler, diSecurity);
        mRequestQueue.addRequest(request);
    }

    @Override
    public void addProperties(Map<String, Object> dataMap, String portName, int productId, ResponseHandler responseHandler) {
        exchangeKeyIfNecessary(networkNode);
        Request request = new LanRequest(networkNode, sslContext, portName, productId, LanRequestType.POST, dataMap, responseHandler, diSecurity);
        mRequestQueue.addRequest(request);
    }

    @Override
    public void deleteProperties(String portName, int productId, ResponseHandler responseHandler) {
        exchangeKeyIfNecessary(networkNode);
        Request request = new LanRequest(networkNode, sslContext, portName, productId, LanRequestType.DELETE, null, responseHandler, diSecurity);
        mRequestQueue.addRequest(request);
    }

    @Override
    public void subscribe(String portName, int productId, int subscriptionTtl, ResponseHandler responseHandler) {
        exchangeKeyIfNecessary(networkNode);
        Request request = new LanRequest(networkNode, sslContext, portName, productId, LanRequestType.POST, getSubscriptionData(subscriptionTtl), responseHandler, diSecurity);
        mRequestQueue.addRequest(request);
    }

    @Override
    public void unsubscribe(String portName, int productId,
                            ResponseHandler responseHandler) {
        exchangeKeyIfNecessary(networkNode);
        Request request = new LanRequest(networkNode, sslContext, portName, productId, LanRequestType.DELETE, getUnsubscriptionData(), responseHandler, diSecurity);
        mRequestQueue.addRequest(request);
    }

    @Override
    public boolean isAvailable() {
        return networkNode.getConnectionState().equals(ConnectionState.CONNECTED_LOCALLY);
    }

    @Nullable
    private synchronized SSLContext createSSLContext() {
        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new SslPinTrustManager(networkNode)}, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            DICommLog.e(DICommLog.LOCALREQUEST, "Error initializing SSL context: " + e.getMessage());
        }
        return context;
    }

    private void triggerKeyExchange(NetworkNode networkNode) {
        networkNode.setEncryptionKey(null);
        exchangeKeyIfNecessary(networkNode);
    }

    private void exchangeKeyIfNecessary(NetworkNode networkNode) {
        if (networkNode.getEncryptionKey() == null && !isKeyExchangeOngoing) {
            doKeyExchange(networkNode);
        }
    }

    private void doKeyExchange(final @NonNull NetworkNode networkNode) {
        ResponseHandler responseHandler = new ResponseHandler() {

            @Override
            public void onSuccess(String key) {
                networkNode.setEncryptionKey(key);
                isKeyExchangeOngoing = false;
            }

            @Override
            public void onError(Error error, String errorData) {
                isKeyExchangeOngoing = false;
            }
        };

        final Request request = networkNode.isHttps() ?
                new GetKeyRequest(networkNode, sslContext, responseHandler) :
                new ExchangeKeyRequest(networkNode, responseHandler);

        isKeyExchangeOngoing = true;
        mRequestQueue.addRequestInFrontOfQueue(request);
    }

    @Override
    public void enableCommunication(SubscriptionEventListener subscriptionEventListener) {
        mLocalSubscriptionHandler.enableSubscription(networkNode, subscriptionEventListener);
    }

    @Override
    public void disableCommunication() {
        mLocalSubscriptionHandler.disableSubscription();
    }
}
