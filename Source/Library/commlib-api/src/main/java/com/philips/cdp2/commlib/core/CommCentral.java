/*
 * Copyright (c) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.commlib.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.cdp2.commlib.core.appliance.Appliance;
import com.philips.cdp2.commlib.core.appliance.ApplianceFactory;
import com.philips.cdp2.commlib.core.appliance.ApplianceManager;
import com.philips.cdp2.commlib.core.context.TransportContext;
import com.philips.cdp2.commlib.core.discovery.DiscoveryStrategy;
import com.philips.cdp2.commlib.core.exception.MissingPermissionException;
import com.philips.cdp2.commlib.core.exception.TransportUnavailableException;
import com.philips.cdp2.commlib.core.store.ApplianceDatabase;
import com.philips.cdp2.commlib.core.store.NetworkNodeDatabase;
import com.philips.cdp2.commlib.core.util.AppIdProvider;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static java.util.Objects.requireNonNull;

/**
 * CommCentral is the object that holds the CommLib library together and allows you to set CommLib up.
 *
 * @publicApi
 */
public final class CommCentral {
    private static final String TAG = "CommCentral";

    private static final AppIdProvider APP_ID_PROVIDER = new AppIdProvider();

    @NonNull
    private final ApplianceFactory applianceFactory;
    private final Set<DiscoveryStrategy> discoveryStrategies = new CopyOnWriteArraySet<>();
    @NonNull
    private final ApplianceManager applianceManager;

    /**
     * Create a CommCentral. You should only ever create one CommCentral!
     *
     * @param applianceFactory  The ApplianceFactory used to create {@link Appliance}s.
     * @param transportContexts TransportContexts that will be used by the {@link Appliance}s and
     *                          provide {@link DiscoveryStrategy}s. You will need at least one!
     */
    public CommCentral(@NonNull ApplianceFactory applianceFactory, @NonNull final TransportContext... transportContexts) {
        this(applianceFactory, null, transportContexts);
    }

    /**
     * Create a CommCentral. You should only ever create one CommCentral!
     *
     * @param applianceFactory  The ApplianceFactory used to create {@link Appliance}s.
     * @param applianceDatabase The ApplianceDatabase used to persist {@link Appliance} state.
     * @param transportContexts TransportContexts that will be used by the {@link Appliance}s and
     *                          provide {@link DiscoveryStrategy}s. You will need at least one!
     */
    public CommCentral(@NonNull ApplianceFactory applianceFactory, @Nullable ApplianceDatabase applianceDatabase, @NonNull final TransportContext... transportContexts) {
        this.applianceFactory = requireNonNull(applianceFactory);

        // Setup transport contexts
        if (transportContexts.length == 0) {
            throw new IllegalArgumentException("This class needs to be constructed with at least one transport context.");
        }

        // Setup discovery strategies
        for (TransportContext transportContext : transportContexts) {
            DiscoveryStrategy discoveryStrategy = transportContext.getDiscoveryStrategy();
            if (discoveryStrategy != null) {
                discoveryStrategies.add(discoveryStrategy);
            }
        }

        // Setup ApplianceManager
        this.applianceManager = new ApplianceManager(discoveryStrategies, applianceFactory, new NetworkNodeDatabase(), applianceDatabase);
    }

    public void startDiscovery() throws MissingPermissionException, TransportUnavailableException {
        startDiscovery(Collections.<String>emptySet());
    }

    public void startDiscovery(@NonNull Set<String> modelIds) throws MissingPermissionException, TransportUnavailableException {
        DICommLog.d(TAG, "Starting discovery for model ids: " + modelIds.toString());

        for (DiscoveryStrategy strategy : this.discoveryStrategies) {
            strategy.start(applianceFactory.getSupportedDeviceTypes(), modelIds);
        }
    }

    public void stopDiscovery() {
        for (DiscoveryStrategy strategy : this.discoveryStrategies) {
            strategy.stop();
        }
    }

    public ApplianceManager getApplianceManager() {
        return applianceManager;
    }

    public static AppIdProvider getAppIdProvider() {
        return APP_ID_PROVIDER;
    }
}
