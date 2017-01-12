/*
 * © Koninklijke Philips N.V., 2015, 2016, 2017.
 *   All rights reserved.
 */

package com.philips.cdp.dicommclient.port;

import com.philips.cdp2.commlib.core.communication.CommunicationStrategy;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

abstract class DICommListPort<T extends DICommListEntryPort<?>> extends DICommPort<Object> {

    private Map<String, T> mListEntryPorts = new ConcurrentHashMap<>();
    private Set<DICommListPortChangedListener> mListPortChangedListeners = new CopyOnWriteArraySet<>();

    public DICommListPort(CommunicationStrategy communicationStrategy) {
        super(communicationStrategy);
    }

    public abstract T createNewListEntryPort(String listPortName, String portKey);

    public int getNumberOfListEntryPorts() {
        return mListEntryPorts.size();
    }

    public T getListEntryPort(String identifier) {
        if (mListEntryPorts.containsKey(identifier)) {
            return mListEntryPorts.get(identifier);
        }
        return null;
    }

    public void addNewListEntryPort() {
        // TODO:DICOMM Refactor, check how to add entry port
    }

    public void removeListEntryPort(String identifier) {
        // TODO:DICOMM Refactor, check how to remove entry port
    }

    @Override
    public boolean isResponseForThisPort(String jsonResponse) {
        // TODO DIComm refacor - implement method
        return false;
    }

    @Override
    protected void processResponse(String jsonResponse) {
        // TODO DIComm refactor - implement method

        // Create a hashmap (key value) of processed ports

        // Loop over all JSON elements
        // if property -> update port property
        // if json -> put into hashmap

        // Loop over Hashmap of existing ports
        // if port not in new hashmap -> call port removed listener

        // Loop over hashmap of new ports
        // if port in existing hashmap -> update port
        // if port in new hashmap -> create new port and call add listener
    }

    public void registerListPortChangedListener(DICommListPortChangedListener listener) {
        mListPortChangedListeners.add(listener);
    }

    public void unRegisterListPortChangedListener(DICommListPortChangedListener listener) {
        mListPortChangedListeners.remove(listener);
    }

    private void notifyListenersOnEntryPortAdded(DICommListEntryPort<?> addedEntryPort) {
        for (DICommListPortChangedListener listener : mListPortChangedListeners) {
            listener.onListEntryPortAdded(addedEntryPort);
        }
    }

    private void notifyListenersOnEntryPortRemoved(DICommListEntryPort<?> removedEntryPort) {
        for (DICommListPortChangedListener listener : mListPortChangedListeners) {
            listener.onListEntryPortRemoved(removedEntryPort);
        }
    }
}
