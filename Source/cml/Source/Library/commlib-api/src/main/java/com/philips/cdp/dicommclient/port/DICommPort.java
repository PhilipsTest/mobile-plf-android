/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp.dicommclient.port;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.google.gson.Gson;
import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp.dicommclient.request.Error;
import com.philips.cdp.dicommclient.request.ResponseHandler;
import com.philips.cdp.dicommclient.subscription.SubscriptionEventListener;
import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.cdp2.commlib.core.appliance.Appliance;
import com.philips.cdp2.commlib.core.communication.CommunicationStrategy;
import com.philips.cdp2.commlib.core.port.PortProperties;
import com.philips.cdp2.commlib.core.util.GsonProvider;
import com.philips.cdp2.commlib.core.util.HandlerProvider;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * A DiComm Port on an {@link Appliance}.
 * <p>
 * Ports hold a set of {@link PortProperties} and can have methods to perform complex actions
 * on a port. It is possible to subscribe to changes on a port to get informed when the port's
 * properties have changed.
 *
 * @param <T> The {@link PortProperties} associated with this port.
 * @publicApi
 */
public abstract class DICommPort<T extends PortProperties> {

    private final String LOG_TAG = getClass().getSimpleName();

    public static final int SUBSCRIPTION_TTL_S = 300;

    @VisibleForTesting
    static final long SUBSCRIPTION_TTL_MS = TimeUnit.SECONDS.toMillis(SUBSCRIPTION_TTL_S);

    protected final Gson gson = GsonProvider.get();

    private Handler resubscriptionHandler = HandlerProvider.createHandler();
    private boolean isRequestInProgress;

    private boolean mIsApplyingChanges;
    private boolean mGetPropertiesRequested;
    private boolean mSubscribeRequested;
    private boolean mUnsubscribeRequested;
    private boolean isSubscribed = false;
    private final Object mResubscribeLock = new Object();
    private T mPortProperties;
    private final Map<String, Object> mPutPropertiesMap = new ConcurrentHashMap<>();

    private final Set<DICommPortListener> mPortListeners = new CopyOnWriteArraySet<>();

    private NetworkNode networkNode;

    protected CommunicationStrategy communicationStrategy;

    private final Runnable resubscriptionRunnable = new Runnable() {
        @Override
        public void run() {
            refreshSubscriptionIfNecessary();
        }
    };

    private final SubscriptionEventListener subscriptionEventListener = new SubscriptionEventListener() {
        @Override
        public void onSubscriptionEventReceived(String portName, String data) {
            if (getDICommPortName().equals(portName)) {
                DICommLog.d(LOG_TAG, "Handling subscription event: " + data);

                handleResponse(data);
            }
        }

        @Override
        public void onSubscriptionEventDecryptionFailed(String portName) {
            if (getDICommPortName().equals(portName)) {
                DICommLog.w(LOG_TAG, "Subscription event decryption failed, scheduling a reload instead.");

                reloadProperties();
            }
        }
    };

    private PropertyChangeListener networkNodeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(NetworkNode.KEY_BOOT_ID)) {
                refreshSubscriptionIfNecessary();
            }
        }
    };

    /**
     * Constructs a DICommPort instance
     * @param communicationStrategy CommunicationStrategy The communication strategy for the port to use.
     */
    public DICommPort(@NonNull final CommunicationStrategy communicationStrategy) {
        this.communicationStrategy = communicationStrategy;
    }

    /**
     * Reads a JSON String and updates its internal values
     * @param jsonResponse String The raw JSON String
     */
    protected abstract void processResponse(String jsonResponse);

    /**
     * Returns the name of the DICommPort.
     * This should be equal to the name of the port defined in the appliance.
     * @return String The DICommPort's name
     */
    public abstract String getDICommPortName();

    /**
     * Returns the ID of the product the DICommPort lives in.
     * @return integer
     */
    protected abstract int getDICommProductId();

    /**
     * Returns true if the DICommPort allows incoming subscriptions, false otherwise.
     * @return boolean
     */
    public abstract boolean supportsSubscription();

    /**
     * Get the properties for this port, possibly triggering a {@link DICommPort#reloadProperties()} when they are not yet available.
     *
     * @return The locally available properties, or null if not available yet.
     */
    public T getPortProperties() {
        if (mPortProperties == null) {
            reloadProperties();
        }
        return mPortProperties;
    }

    protected void setPortProperties(T portProperties) {
        mGetPropertiesRequested = false;
        mPortProperties = portProperties;
    }

    /**
     * Updates a property within the DICommPort on the appliance.
     * @param key String The name of the property to update
     * @param value String The value to update the property with
     */
    public void putProperties(String key, String value) {
        DICommLog.d(LOG_TAG, "request putProperties - " + key + " : " + value);
        mPutPropertiesMap.put(key, value);
        tryToPerformNextRequest();
    }

    /**
     * Updates a property within the DICommPort on the appliance.
     * @param key String The name of the property to update
     * @param value int The value to update the property with
     */
    public void putProperties(String key, int value) {
        DICommLog.d(LOG_TAG, "request putProperties - " + key + " : " + value);
        mPutPropertiesMap.put(key, value);
        tryToPerformNextRequest();
    }

    /**
     * Updates a property within the DICommPort on the appliance.
     * @param key String The name of the property to update
     * @param value boolean The value to update the property with
     */
    public void putProperties(String key, boolean value) {
        DICommLog.d(LOG_TAG, "request putProperties - " + key + " : " + value);
        mPutPropertiesMap.put(key, value);
        tryToPerformNextRequest();
    }

    /**
     * Updates all properties in the given <code>dataMap</code> with the associated values.
     * @param dataMap Map<String, Object> The map of updated property values
     */
    public void putProperties(Map<String, Object> dataMap) {
        DICommLog.d(LOG_TAG, "request putProperties - multiple key values");
        mPutPropertiesMap.putAll(dataMap);
        tryToPerformNextRequest();
    }

    /**
     * Synchronize local port properties with the (remote) appliance.
     */
    public void reloadProperties() {
        DICommLog.d(LOG_TAG, "request reloadProperties");
        mGetPropertiesRequested = true;
        tryToPerformNextRequest();
    }

    /**
     * Subscribes to all changes of the DICommPort on the appliance.
     */
    public void subscribe() {
        if (mSubscribeRequested) return;
        DICommLog.d(LOG_TAG, "request subscribe");

        this.communicationStrategy.addSubscriptionEventListener(subscriptionEventListener);

        mSubscribeRequested = true;
        isSubscribed = true;

        resubscriptionHandler.removeCallbacks(resubscriptionRunnable);
        resubscriptionHandler.postDelayed(resubscriptionRunnable, SUBSCRIPTION_TTL_MS);

        tryToPerformNextRequest();
    }

    /**
     * Cancels subscription to changes of the DICommPort on the appliance.
     */
    public void unsubscribe() {
        DICommLog.d(LOG_TAG, "request unsubscribe");

        this.communicationStrategy.removeSubscriptionEventListener(subscriptionEventListener);

        mUnsubscribeRequested = true;
        stopResubscribe();
        tryToPerformNextRequest();
    }

    /**
     * Prevents subscription to changes of DICommPort to be renewed.
     */
    public void stopResubscribe() {
        DICommLog.d(LOG_TAG, "stop resubscribing");

        synchronized (mResubscribeLock) {
            isSubscribed = false;
        }
        resubscriptionHandler.removeCallbacks(resubscriptionRunnable);
    }

    private void refreshSubscriptionIfNecessary() {
        synchronized (mResubscribeLock) {
            if (isSubscribed) {
                subscribe();
            }
        }
    }

    /**
     * Adds a listener to all the port's changes
     * @param listener DICommPortListener The listener
     */
    public void addPortListener(DICommPortListener listener) {
        mPortListeners.add(listener);
    }

    /**
     * Removes a listener to all the port's changes
     * @param listener DICommPortListener The listener
     */
    public void removePortListener(DICommPortListener listener) {
        mPortListeners.remove(listener);
    }

    @SuppressWarnings("unchecked")
    private void notifyPortListenersOnUpdate() {
        for (DICommPortListener listener : mPortListeners) {
            listener.onPortUpdate(this);
        }
    }

    @SuppressWarnings("unchecked")
    private void notifyPortListenersOnError(Error error, String errorData) {
        for (DICommPortListener listener : mPortListeners) {
            listener.onPortError(this, error, errorData);
        }
    }

    private void tryToPerformNextRequest() {
        if (isRequestInProgress) {
            DICommLog.d(LOG_TAG, "Trying to perform next request - Another request already in progress");
            return;
        }
        DICommLog.d(LOG_TAG, "Trying to perform next request - Performing next request");
        isRequestInProgress = true;

        if (isPutPropertiesRequested()) {
            performPutProperties();
        } else if (isSubscribeRequested()) {
            performSubscribe();
        } else if (isUnsubcribeRequested()) {
            performUnsubscribe();
        } else if (isGetPropertiesRequested()) {
            performGetProperties();
        } else {
            isRequestInProgress = false;
        }
    }

    private void setIsApplyingChanges(boolean isApplyingChanges) {
        DICommLog.d(LOG_TAG, isApplyingChanges ? "Started applying changes" : "Stopped applying changes");
        this.mIsApplyingChanges = isApplyingChanges;
    }

    boolean isApplyingChanges() {
        return mIsApplyingChanges;
    }

    private boolean isPutPropertiesRequested() {
        return !mPutPropertiesMap.isEmpty();
    }

    private boolean isGetPropertiesRequested() {
        return mGetPropertiesRequested;
    }

    private boolean isSubscribeRequested() {
        return mSubscribeRequested;
    }

    private boolean isUnsubcribeRequested() {
        return mUnsubscribeRequested;
    }

    private void requestCompleted() {
        isRequestInProgress = false;
        tryToPerformNextRequest();
    }

    void handleResponse(String data) {
        mGetPropertiesRequested = false;
        processResponse(data);
        notifyPortListenersOnUpdate();
    }

    private void performPutProperties() {
        final Map<String, Object> propertiesToSend = Collections.unmodifiableMap(new HashMap<>(mPutPropertiesMap));
        mPutPropertiesMap.clear();

        DICommLog.i(LOG_TAG, "putProperties");
        setIsApplyingChanges(true);
        this.communicationStrategy.putProperties(propertiesToSend, getDICommPortName(), getDICommProductId(), new ResponseHandler() {

            @Override
            public void onSuccess(String data) {
                if (!isPutPropertiesRequested()) {
                    setIsApplyingChanges(false);
                }
                handleResponse(data);
                DICommLog.i(LOG_TAG, "putProperties - success");
                requestCompleted();
            }

            public void onError(Error error, String errorData) {
                if (!isPutPropertiesRequested()) {
                    setIsApplyingChanges(false);
                }
                notifyPortListenersOnError(error, errorData);
                DICommLog.e(LOG_TAG, "putProperties - error");
                requestCompleted();
            }
        });
    }

    private void performGetProperties() {
        DICommLog.i(LOG_TAG, "getProperties");
        this.communicationStrategy.getProperties(getDICommPortName(), getDICommProductId(), new ResponseHandler() {

            @Override
            public void onSuccess(String data) {
                handleResponse(data);
                DICommLog.i(LOG_TAG, "getProperties - success");
                requestCompleted();
            }

            @Override
            public void onError(Error error, String errorData) {
                mGetPropertiesRequested = false;
                notifyPortListenersOnError(error, errorData);
                DICommLog.e(LOG_TAG, "getProperties - error");
                requestCompleted();
            }
        });
    }

    private void performSubscribe() {
        DICommLog.i(LOG_TAG, "perform subscribe");
        this.communicationStrategy.subscribe(getDICommPortName(), getDICommProductId(), SUBSCRIPTION_TTL_S, new ResponseHandler() {

            @Override
            public void onSuccess(String data) {
                mSubscribeRequested = false;
                handleResponse(data);
                DICommLog.i(LOG_TAG, "subscribe - success");
                requestCompleted();
            }

            @Override
            public void onError(Error error, String errorData) {
                mSubscribeRequested = false;
                notifyPortListenersOnError(error, errorData);
                DICommLog.e(LOG_TAG, "subscribe - error");
                requestCompleted();
            }
        });
    }

    private void performUnsubscribe() {
        DICommLog.i(LOG_TAG, "perform unsubscribe");
        this.communicationStrategy.unsubscribe(getDICommPortName(), getDICommProductId(), new ResponseHandler() {

            @Override
            public void onSuccess(String data) {
                mUnsubscribeRequested = false;
                handleResponse(data);
                DICommLog.i(LOG_TAG, "unsubscribe - success");
                requestCompleted();
            }

            @Override
            public void onError(Error error, String errorData) {
                mUnsubscribeRequested = false;
                notifyPortListenersOnError(error, errorData);
                DICommLog.e(LOG_TAG, "unsubscribe - error");
                requestCompleted();
            }
        });
    }

    public void setNetworkNode(@NonNull final NetworkNode networkNode) {
        this.networkNode = networkNode;
        this.networkNode.addPropertyChangeListener(networkNodeListener);
    }
}
