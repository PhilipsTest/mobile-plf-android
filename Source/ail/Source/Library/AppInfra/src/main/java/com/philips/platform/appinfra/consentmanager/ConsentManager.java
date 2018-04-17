/* Copyright (c) Koninklijke Philips N.V., 2018
* All rights are reserved. Reproduction or dissemination
* in whole or in part is prohibited without the prior written
* consent of the copyright holder.
*/
package com.philips.platform.appinfra.consentmanager;

import android.os.Handler;
import android.os.Looper;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.pif.chi.ConsentError;
import com.philips.platform.pif.chi.ConsentHandlerInterface;
import com.philips.platform.pif.chi.FetchConsentTypeStateCallback;
import com.philips.platform.pif.chi.PostConsentTypeCallback;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;
import com.philips.platform.pif.chi.datamodel.ConsentDefinitionStatus;
import com.philips.platform.pif.chi.datamodel.ConsentStates;
import com.philips.platform.pif.chi.datamodel.ConsentStatus;
import com.philips.platform.pif.chi.datamodel.ConsentVersionStates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class ConsentManager implements ConsentManagerInterface {

    private final AppInfra mAppInfra;
    private Map<String, ConsentHandlerInterface> consentHandlerMapping = new HashMap<>();
    private Map<String, ConsentDefinition> consentDefinitionMapping = new HashMap<>();

    public ConsentManager(AppInfra aAppInfra) {
        mAppInfra = aAppInfra;
    }

    @Override
    public synchronized void registerHandler(List<String> consentTypes, ConsentHandlerInterface consentHandlerInterface) {
        for (String consentType : consentTypes) {
            if (consentHandlerMapping.containsKey(consentType))
                throw new RuntimeException("Consent type already exist");
            consentHandlerMapping.put(consentType, consentHandlerInterface);
        }
    }

    //TODO throw exception in case of key does not exist ?
    @Override
    public synchronized void deregisterHandler(List<String> consentTypes) {
        for (String consentType : consentTypes) {
            if (consentHandlerMapping.containsKey(consentType))
                consentHandlerMapping.remove(consentType);
        }
    }

    @Override
    public synchronized void registerConsentDefinitions(List<ConsentDefinition> consentDefinitionList) {
        for (ConsentDefinition consentDefinition : consentDefinitionList) {
            for (String type : consentDefinition.getTypes()) {
                consentDefinitionMapping.put(type, consentDefinition);
            }
        }
    }

    @Override
    public void fetchConsentState(final ConsentDefinition consentDefinition, final FetchConsentCallback callback) throws RuntimeException {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                executeHandlerToFetchConsentState(consentDefinition, callback);
            }
        });
    }

    @Override
    public void fetchConsentStates(final List<ConsentDefinition> consentDefinitions, final FetchConsentsCallback callback) throws RuntimeException {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final CountDownLatch countDownLatch = new CountDownLatch(consentDefinitions.size());
                List<ConsentManagerCallbackListener> consentManagerCallbackListeners = new ArrayList<>();

                for (ConsentDefinition consentDefinition : consentDefinitions) {
                    ConsentManagerCallbackListener listener = new ConsentManagerCallbackListener(countDownLatch);
                    consentManagerCallbackListeners.add(listener);
                    executeHandlerToFetchConsentState(consentDefinition, listener);
                }

                waitTillThreadsGetsCompleted(countDownLatch);
                postResultOnFetchConsents(consentManagerCallbackListeners, callback);
            }
        });
    }

    @Override
    public void storeConsentState(final ConsentDefinition consentDefinition, final boolean status, final PostConsentCallback callback) throws RuntimeException {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final CountDownLatch countDownLatch = new CountDownLatch(consentDefinition.getTypes().size());
                List<ConsentTypeCallbackListener> consentTypeCallbackListeners = new ArrayList<>();

                for (String consentType : consentDefinition.getTypes()) {
                    ConsentTypeCallbackListener listener = new ConsentTypeCallbackListener(countDownLatch);
                    consentTypeCallbackListeners.add(listener);
                    getHandler(consentType).storeConsentTypeState(consentType, status, consentDefinition.getVersion(), listener);
                }

                waitTillThreadsGetsCompleted(countDownLatch);
                postResultOnStoreConsent(consentTypeCallbackListeners, callback);
            }
        });
    }

    @Override
    public void fetchConsentTypeState(final String type, final FetchConsentCallback callback) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                executeHandlerToFetchConsentTypeState(type, callback);
            }
        });
    }

    private ConsentDefinition getConsentDefinitionForType(String consentType) {
        ConsentDefinition consentDefinition = consentDefinitionMapping.get(consentType);
        if (consentDefinition != null) {
            return consentDefinition;
        }
        throw new RuntimeException("ConsentDefinition is not registered for the type " + consentType);
    }

    protected ConsentHandlerInterface getHandler(String consentType) {
        ConsentHandlerInterface handler = consentHandlerMapping.get(consentType);
        if (handler != null) {
            return handler;
        }
        throw new RuntimeException("Handler is not registered for the type " + consentType);
    }

    private void executeHandlerToFetchConsentState(final ConsentDefinition consentDefinition, final FetchConsentCallback callback) throws RuntimeException {
        final CountDownLatch countDownLatch = new CountDownLatch(consentDefinition.getTypes().size());
        List<ConsentTypeCallbackListener> consentTypeCallbackListeners = new ArrayList<>();

        for (String consentType : consentDefinition.getTypes()) {
            ConsentTypeCallbackListener listener = new ConsentTypeCallbackListener(countDownLatch);
            consentTypeCallbackListeners.add(listener);
            getHandler(consentType).fetchConsentTypeState(consentType, listener);
        }

        waitTillThreadsGetsCompleted(countDownLatch);
        postResultOnFetchConsent(consentDefinition, consentTypeCallbackListeners, callback);
    }

    private void executeHandlerToFetchConsentTypeState(final String type, final FetchConsentCallback callback) throws RuntimeException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<ConsentTypeCallbackListener> consentTypeCallbackListeners = new ArrayList<>();

        ConsentTypeCallbackListener listener = new ConsentTypeCallbackListener(countDownLatch);
        consentTypeCallbackListeners.add(listener);
        getHandler(type).fetchConsentTypeState(type, listener);

        waitTillThreadsGetsCompleted(countDownLatch);
        postResultOnFetchConsent(getConsentDefinitionForType(type), consentTypeCallbackListeners, callback);
    }

    private void waitTillThreadsGetsCompleted(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.DEBUG, "", "");
        }
    }

    private void postResultOnFetchConsent(final ConsentDefinition consentDefinition,
                                          final List<ConsentTypeCallbackListener> consentTypeCallbackListeners, final FetchConsentCallback callback) {

        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                ConsentStates consentDefinitionState = null;
                ConsentVersionStates consentDefinitionVersionState = null;
                for (ConsentTypeCallbackListener consentCallbackListener : consentTypeCallbackListeners) {

                    ConsentStatus consentStatus = consentCallbackListener.consentStatus;
                    if (consentStatus == null) {
                        consentStatus = new ConsentStatus(ConsentStates.inactive, 0);
                    }

                    if (consentCallbackListener.consentError != null) {
                        callback.onGetConsentFailed(consentCallbackListener.consentError);
                        return;
                    }

                    ConsentStates consentTypeState = toConsentStatus(consentDefinition, consentStatus);
                    if (consentDefinitionState == null || consentTypeState.compareTo(consentDefinitionState) > 0) {
                        consentDefinitionState = consentTypeState;
                    }
                    ConsentVersionStates consentTypeVersionState = toConsentVersionStatus(consentDefinition, consentStatus);
                    if (consentDefinitionVersionState == null || consentTypeVersionState.compareTo(consentDefinitionVersionState) > 0) {
                        consentDefinitionVersionState = consentTypeVersionState;
                    }
                }
                callback.onGetConsentSuccess(new ConsentDefinitionStatus(consentDefinitionState, consentDefinitionVersionState, consentDefinition));
            }
        });

    }

    private ConsentStates toConsentStatus(ConsentDefinition consentDefinition, ConsentStatus consentStatus) {
        ConsentStates consentState = consentStatus.getConsentState();
        if (consentDefinition.getVersion() > consentStatus.getVersion()) {
            consentState = ConsentStates.inactive;
        }
        return consentState;
    }

    private ConsentVersionStates toConsentVersionStatus(ConsentDefinition consentDefinition, ConsentStatus consentStatus) {
        ConsentVersionStates consentVersionState;
        if (consentDefinition.getVersion() < consentStatus.getVersion()) {
            consentVersionState = ConsentVersionStates.AppVersionIsLower;
        } else if (consentDefinition.getVersion() == consentStatus.getVersion()) {
            consentVersionState = ConsentVersionStates.InSync;
        } else {
            consentVersionState = ConsentVersionStates.AppVersionIsHigher;
        }
        return consentVersionState;
    }

    private void postResultOnFetchConsents(final List<ConsentManagerCallbackListener> consentManagerCallbackListeners, final FetchConsentsCallback callback) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                List<ConsentDefinitionStatus> consentDefinitionStatusList = new ArrayList<>();
                for (ConsentManagerCallbackListener consentManagerCallbackListener : consentManagerCallbackListeners) {
                    if (consentManagerCallbackListener.consentError != null) {
                        callback.onGetConsentsFailed(consentManagerCallbackListener.consentError);
                        return;
                    }
                    consentDefinitionStatusList.add(consentManagerCallbackListener.consentDefinitionStatus);
                }
                callback.onGetConsentsSuccess(consentDefinitionStatusList);
            }
        });

    }

    private void postResultOnStoreConsent(final List<ConsentTypeCallbackListener> consentCallbackListeners, final PostConsentCallback postConsentCallback) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                for (ConsentTypeCallbackListener consentTypeCallbackListener : consentCallbackListeners) {
                    if (consentTypeCallbackListener.consentError != null) {
                        postConsentCallback.onPostConsentFailed(consentTypeCallbackListener.consentError);
                        return;
                    }
                }
                postConsentCallback.onPostConsentSuccess();
            }
        });

    }

    private class ConsentManagerCallbackListener implements FetchConsentCallback {
        CountDownLatch countDownLatch;
        ConsentDefinitionStatus consentDefinitionStatus;
        ConsentError consentError;

        ConsentManagerCallbackListener(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void onGetConsentSuccess(ConsentDefinitionStatus consentDefinitionStatus) {
            this.consentDefinitionStatus = consentDefinitionStatus;
            countDownLatch.countDown();
        }

        @Override
        public void onGetConsentFailed(ConsentError error) {
            consentError = error;
            countDownLatch.countDown();
        }

    }

    private class ConsentTypeCallbackListener implements FetchConsentTypeStateCallback, PostConsentTypeCallback {
        CountDownLatch countDownLatch;
        ConsentError consentError;
        ConsentStatus consentStatus;
        boolean calledBack;

        ConsentTypeCallbackListener(CountDownLatch countDownLatch) {
            this.calledBack = false;
            this.countDownLatch = countDownLatch;
        }

        private void markCalledBack() {
            countDownLatch.countDown();
            calledBack = true;
        }

        @Override
        public synchronized void onPostConsentFailed(ConsentError error) {
            if (!calledBack) {
                consentError = error;
                markCalledBack();
            }
        }

        @Override
        public synchronized void onPostConsentSuccess() {
            if (!calledBack) {
                markCalledBack();
            }
        }

        @Override
        public synchronized void onGetConsentsSuccess(ConsentStatus consentStatus) {
            if (!calledBack) {
                this.consentStatus = consentStatus;
                markCalledBack();
            }
        }

        @Override
        public synchronized void onGetConsentsFailed(ConsentError error) {
            if (!calledBack) {
                consentError = error;
                markCalledBack();
            }
        }
    }
}
