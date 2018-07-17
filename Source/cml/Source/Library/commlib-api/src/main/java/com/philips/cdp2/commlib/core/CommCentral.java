/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */
package com.philips.cdp2.commlib.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.cdp2.commlib.core.appliance.Appliance;
import com.philips.cdp2.commlib.core.appliance.ApplianceFactory;
import com.philips.cdp2.commlib.core.appliance.ApplianceManager;
import com.philips.cdp2.commlib.core.configuration.RuntimeConfiguration;
import com.philips.cdp2.commlib.core.context.TransportContext;
import com.philips.cdp2.commlib.core.discovery.DiscoveryStrategy;
import com.philips.cdp2.commlib.core.exception.MissingPermissionException;
import com.philips.cdp2.commlib.core.exception.TransportUnavailableException;
import com.philips.cdp2.commlib.core.store.ApplianceDatabase;
import com.philips.cdp2.commlib.core.store.NetworkNodeDatabase;
import com.philips.cdp2.commlib.core.store.NetworkNodeDatabaseFactory;
import com.philips.cdp2.commlib.core.util.AppIdProvider;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * CommCentral is the object that holds the CommLib library together and allows you to set CommLib up.
 *
 * @publicApi
 */
public final class CommCentral {
    private static final String TAG = "CommCentral";

    private static WeakReference<CommCentral> instanceWeakReference = new WeakReference<>(null);

    private static final AppIdProvider APP_ID_PROVIDER = new AppIdProvider();

    private final Set<DiscoveryStrategy> discoveryStrategies = new CopyOnWriteArraySet<>();
    @NonNull
    private final ApplianceManager applianceManager;

    @NonNull
    private TransportContext[] transportContexts;

    /**
     * Create a CommCentral. You should only ever create one CommCentral!
     *
     * @param applianceFactory     The ApplianceFactory used to create {@link Appliance}s.
     * @param runtimeConfiguration Way to provide {@link RuntimeConfiguration} for CommLib
     * @param transportContexts    TransportContexts that will be used by the {@link Appliance}s and
     *                             provide {@link DiscoveryStrategy}s. You will need at least one!
     */
    public CommCentral(@NonNull ApplianceFactory applianceFactory, @NonNull RuntimeConfiguration runtimeConfiguration, @NonNull final TransportContext... transportContexts) {
        this(applianceFactory, runtimeConfiguration, null, transportContexts);
    }

    /**
     * Create a CommCentral. You should only ever create one CommCentral!
     *
     * @param applianceFactory     The ApplianceFactory used to create {@link Appliance}s.
     * @param runtimeConfiguration Way to provide {@link RuntimeConfiguration} for CommLib
     * @param applianceDatabase    Way to provide {@link ApplianceDatabase} for CommLib
     * @param transportContexts    TransportContexts that will be used by the {@link Appliance}s and
     *                             provide {@link DiscoveryStrategy}s. You will need at least one!
     */
    public CommCentral(@NonNull ApplianceFactory applianceFactory, @NonNull RuntimeConfiguration runtimeConfiguration, @Nullable ApplianceDatabase applianceDatabase, @NonNull final TransportContext... transportContexts) {
        initialiseStrategies(transportContexts);
        NetworkNodeDatabase database = NetworkNodeDatabaseFactory.create(runtimeConfiguration);
        // Setup ApplianceManager
        this.applianceManager = new ApplianceManager(discoveryStrategies, applianceFactory, database, applianceDatabase);
    }

    /**
     * Create a CommCentral. You should only ever create one CommCentral!
     *
     * @param applianceFactory     The ApplianceFactory used to create {@link Appliance}s.
     * @param runtimeConfiguration Way to provide {@link RuntimeConfiguration} for CommLib
     * @param applianceDatabase    Way to provide {@link ApplianceDatabase} for CommLib
     * @param databaseFetcher      Way to provide {@link NetworkNodeDatabase} for CommLib
     * @param transportContexts    TransportContexts that will be used by the {@link Appliance}s and
     *                             provide {@link DiscoveryStrategy}s. You will need at least one!
     */
    public CommCentral(@NonNull ApplianceFactory applianceFactory, @NonNull RuntimeConfiguration runtimeConfiguration, @Nullable ApplianceDatabase applianceDatabase, @Nullable DatabaseFetcher databaseFetcher, @NonNull final TransportContext... transportContexts) {
        initialiseStrategies(transportContexts);
        NetworkNodeDatabase database = databaseFetcher == null ? new NetworkNodeDatabaseFetcher().getNetworkNodeDatabase(runtimeConfiguration) : databaseFetcher.getNetworkNodeDatabase(runtimeConfiguration);
        // Setup ApplianceManager
        this.applianceManager = new ApplianceManager(discoveryStrategies, applianceFactory, database, applianceDatabase);
    }

    /**
     * Start discovery for all transports.
     *
     * @throws MissingPermissionException    thrown if additional permissions are required.
     */
    public void startDiscovery() throws MissingPermissionException {
        startDiscovery(Collections.<String>emptySet());
    }

    /**
     * Start discovery for all transports and filter for specific model ids.
     *
     * @param modelIds set of model ids which should be filtered for.
     * @throws MissingPermissionException    thrown if additional permissions are required.
     */
    public void startDiscovery(@NonNull Set<String> modelIds) throws MissingPermissionException {
        DICommLog.d(TAG, "Starting discovery for model ids: " + modelIds.toString());

        for (DiscoveryStrategy strategy : this.discoveryStrategies) {
            strategy.start(modelIds);
        }
    }

    /**
     * Stop discovery for all transports.
     */
    public void stopDiscovery() {
        for (DiscoveryStrategy strategy : this.discoveryStrategies) {
            strategy.stop();
        }
    }

    /**
     * Clear all discovered {@link Appliance}s.
     * <p>
     * This can be invoked regardless of discovery being started or not.
     */
    public void clearDiscoveredAppliances() {
        for (DiscoveryStrategy strategy : this.discoveryStrategies) {
            strategy.clearDiscoveredNetworkNodes();
        }
    }

    /**
     * Get the {@link ApplianceManager} for this {@link CommCentral}.
     * <p>
     * This will always return the same {@link ApplianceManager}.
     *
     * @return the {@link ApplianceManager} belonging to this {@link CommCentral}.
     */
    @NonNull
    public ApplianceManager getApplianceManager() {
        return applianceManager;
    }

    /**
     * Get the {@link AppIdProvider} for this app.
     * <p>
     * The {@link AppIdProvider} stores the unique identifier for this app.
     *
     * @return AppIdProvider for this app.
     */
    public static AppIdProvider getAppIdProvider() {
        return APP_ID_PROVIDER;
    }

    /**
     * Returns the transport context of the correct type known to CommCentral, or an exception if no such context is present.
     *
     * @param clazz parameter that defines the type of TransportContext you are looking for.
     * @return The transport context of the correct type, that was passed to CommCentral at construction time.
     * @throws TransportUnavailableException If no transport context of the correct type is known to CommCentral.
     */
    public <T extends TransportContext> T getTransportContext(Class<T> clazz) throws TransportUnavailableException {
        for (TransportContext context: transportContexts) {
            if (context.getClass().equals(clazz)){
                return clazz.cast(context);
            }
        }

        throw new TransportUnavailableException("Requested transport context is not available");
    }

    private void initialiseStrategies(@NonNull final TransportContext... transportContexts) {
        if (instanceWeakReference.get() == null) {
            instanceWeakReference = new WeakReference<>(this);
        } else {
            throw new UnsupportedOperationException("Only one instance allowed.");
        }
        // Setup transport contexts
        if (transportContexts.length == 0) {
            throw new IllegalArgumentException("This class needs to be constructed with at least one transport context.");
        }

        this.transportContexts = transportContexts;
        // Setup discovery strategies
        for (TransportContext transportContext : transportContexts) {
            DiscoveryStrategy discoveryStrategy = transportContext.getDiscoveryStrategy();
            if (discoveryStrategy != null) {
                discoveryStrategies.add(discoveryStrategy);
            }
        }
    }

    private class NetworkNodeDatabaseFetcher implements DatabaseFetcher {
        @Override
        public NetworkNodeDatabase getNetworkNodeDatabase(@NonNull RuntimeConfiguration runtimeConfiguration) {
            return NetworkNodeDatabaseFactory.create(runtimeConfiguration);
        }
    }
}
