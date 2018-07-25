package com.philips.cdp2.commlib.core.store;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp2.commlib.core.configuration.RuntimeConfiguration;
import com.philips.platform.appinfra.AppInfraInterface;

import java.io.File;

public final class NetworkNodeDatabaseFactory {

    private NetworkNodeDatabaseFactory() {
    }

    @NonNull
    public static NetworkNodeDatabase create(final @Nullable RuntimeConfiguration runtimeConfiguration) {
        DatabaseHelper dbHelper = createNetworkNodeDBHelper(runtimeConfiguration);

        final NetworkNodeDatabase database = new NetworkNodeDatabase(dbHelper);

        migrate(runtimeConfiguration, database, dbHelper);

        return database;
    }

    @NonNull
    private static DatabaseHelper createNetworkNodeDBHelper(final @Nullable RuntimeConfiguration runtimeConfiguration) {
        DatabaseHelper dbHelper = null;

        if (runtimeConfiguration != null) {
            AppInfraInterface appInfraInterface = runtimeConfiguration.getAppInfraInterface();

            if (appInfraInterface != null) {
                dbHelper = new SecureNetworkNodeDatabaseHelper(appInfraInterface);
            }
        }

        if (dbHelper == null) {
            dbHelper = new NonSecureNetworkNodeDatabaseHelper();
        }
        return dbHelper;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void migrate(@Nullable final RuntimeConfiguration runtimeConfiguration, @NonNull final NetworkNodeDatabase newDatabase, @NonNull final DatabaseHelper dbHelper) {
        if (runtimeConfiguration == null || dbHelper instanceof NonSecureNetworkNodeDatabaseHelper) {
            return;
        }

        File oldDatabaseFile = runtimeConfiguration.getContext().getDatabasePath(NonSecureNetworkNodeDatabaseHelper.DB_NAME);
        if (oldDatabaseFile.exists()) {
            NonSecureNetworkNodeDatabaseHelper oldDBHelper = new NonSecureNetworkNodeDatabaseHelper();
            final NetworkNodeDatabase oldDatabase = new NetworkNodeDatabase(oldDBHelper);
            for (NetworkNode networkNode : oldDatabase.getAll()) {
                newDatabase.save(networkNode);
            }

            oldDatabaseFile.delete();
        }
    }
}
