package com.philips.pins.shinelib.dicommsupport;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.philips.pins.shinelib.SHNMapResultListener;
import com.philips.pins.shinelib.SHNResult;
import com.philips.pins.shinelib.SHNResultListener;
import com.philips.pins.shinelib.framework.Timer;
import com.philips.pins.shinelib.utility.SHNLogger;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DiCommPort {

    private static final String TAG = "DiCommPort";
    private static final int POLLING_INTERVAL = 2000;
    @NonNull
    private String name;
    @Nullable
    private Listener listener;
    @Nullable
    private DiCommChannel diCommChannel;

    private boolean isAvailable;
    private Map<String, Object> properties = new HashMap<>();
    private Set<UpdateListener> updateListeners = new HashSet<>();
    private final Timer subscriptionTimer;

    public DiCommPort(@NonNull String name) {
        this.name = name;

        subscriptionTimer = Timer.createTimer(new Runnable() {
            @Override
            public void run() {
                refreshSubscription();
            }
        }, POLLING_INTERVAL);
    }

    public void setDiCommChannel(@Nullable DiCommChannel diCommChannel) {
        this.diCommChannel = diCommChannel;
    }

    public void onChannelAvailabilityChanged(boolean isAvailable) {
        if (isAvailable) {
            if (diCommChannel != null) {
                diCommChannel.reloadProperties(name, new SHNMapResultListener<String, Object>() {
                    @Override
                    public void onActionCompleted(Map<String, Object> properties, @NonNull SHNResult result) {
                        if (result == SHNResult.SHNOk) {
                            DiCommPort.this.properties = properties;
                            setIsAvailable(true);
                        } else {
                            SHNLogger.d(TAG, "Failed to load properties result: " + result);
                        }
                    }
                });
            }
        } else {
            setIsAvailable(false);
        }
    }

    private void setIsAvailable(boolean isAvailable) {
        if (this.isAvailable != isAvailable) {
            this.isAvailable = isAvailable;
            if (isAvailable) {
                if (listener != null) {
                    listener.onPortAvailable(this);
                }
            } else {
                if (listener != null) {
                    listener.onPortUnavailable(this);
                }
            }
        }
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public void reloadProperties(@NonNull final SHNMapResultListener<String, Object> resultListenerMock) {
        if (diCommChannel != null) {
            diCommChannel.reloadProperties(name, new SHNMapResultListener<String, Object>() {
                @Override
                public void onActionCompleted(Map<String, Object> properties, @NonNull SHNResult result) {
                    mergeProperties(properties);
                    resultListenerMock.onActionCompleted(properties, result);
                }
            });
        } else {
            resultListenerMock.onActionCompleted(null, SHNResult.SHNErrorInvalidState);
        }
    }

    public void putProperties(@NonNull Map<String, Object> properties, @NonNull final SHNMapResultListener<String, Object> resultListener) {
        if (diCommChannel != null) {
            diCommChannel.sendProperties(properties, name, new SHNMapResultListener<String, Object>() {
                @Override
                public void onActionCompleted(Map<String, Object> properties, @NonNull SHNResult result) {
                    mergeProperties(properties);
                    resultListener.onActionCompleted(properties, result);
                }
            });
        } else {
            resultListener.onActionCompleted(null, SHNResult.SHNErrorInvalidState);
        }
    }

    public void subscribe(@NonNull UpdateListener updateListener, @NonNull SHNResultListener shnResultListener) {
        if (!updateListeners.contains(updateListener)) {
            updateListeners.add(updateListener);
            if (updateListeners.size() == 1) {
                refreshSubscription();
                SHNLogger.d(TAG, "Started polling properties for port: " + name);
            }
        }
        shnResultListener.onActionCompleted(SHNResult.SHNOk);
    }

    private void refreshSubscription() {
        if (diCommChannel != null) {
            diCommChannel.reloadProperties(name, new SHNMapResultListener<String, Object>() {
                @Override
                public void onActionCompleted(Map<String, Object> properties, @NonNull SHNResult result) {
                    if (result == SHNResult.SHNOk && !updateListeners.isEmpty()) {
                        subscriptionTimer.restart();

                        Map<String, Object> changedProperties = mergeProperties(properties);

                        if (!changedProperties.isEmpty()) {
                            notifyPropertiesChanged(changedProperties);
                        }
                    } else {
                        notifySubscriptionFailed(result);
                    }
                }
            });
        } else {
            notifySubscriptionFailed(SHNResult.SHNErrorInvalidState);
        }
    }

    @NonNull
    private Map<String, Object> mergeProperties(Map<String, Object> properties) {
        Map<String, Object> mergedProperties = new HashMap<>(DiCommPort.this.properties);
        mergedProperties.putAll(properties);

        Map<String, Object> changedProperties = getChangedProperties(mergedProperties);
        DiCommPort.this.properties = mergedProperties;
        return changedProperties;
    }

    @NonNull
    private Map<String, Object> getChangedProperties(Map<String, Object> mergedProperties) {
        Map<String, Object> changedProperties = new HashMap<>();

        for (String key : mergedProperties.keySet()) {
            Object oldValue = this.properties.get(key);
            Object newValue = mergedProperties.get(key);
            if (!oldValue.equals(newValue)) {
                changedProperties.put(key, newValue);
            }
        }
        return changedProperties;
    }

    private void notifyPropertiesChanged(Map<String, Object> properties) {
        Set<UpdateListener> copyOfUpdateListeners = new HashSet<>(updateListeners);
        for (UpdateListener updateListener : copyOfUpdateListeners) {
            updateListener.onPropertiesChanged(properties);
        }
    }

    private void notifySubscriptionFailed(@NonNull SHNResult result) {
        Set<UpdateListener> copyOfUpdateListeners = new HashSet<>(updateListeners);
        for (UpdateListener updateListener : copyOfUpdateListeners) {
            updateListener.onSubscriptionFailed(result);
        }
    }

    public void unsubscribe(UpdateListener updateListener, SHNResultListener shnResultListener) {
        if (updateListeners.contains(updateListener)) {
            updateListeners.remove(updateListener);

            if (updateListeners.isEmpty()) {
                subscriptionTimer.stop();
                SHNLogger.d(TAG, "Stopped polling properties for port: " + name);
            }
            shnResultListener.onActionCompleted(SHNResult.SHNOk);
        } else {
            shnResultListener.onActionCompleted(SHNResult.SHNErrorInvalidState);
        }
    }

    public interface Listener {
        void onPortAvailable(DiCommPort diCommPort);

        void onPortUnavailable(DiCommPort diCommPort);
    }

    public interface UpdateListener {
        void onPropertiesChanged(@NonNull Map<String, Object> properties);

        void onSubscriptionFailed(SHNResult shnResult);
    }
}
