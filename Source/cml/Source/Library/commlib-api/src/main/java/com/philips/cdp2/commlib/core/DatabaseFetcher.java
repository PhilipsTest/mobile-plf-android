package com.philips.cdp2.commlib.core;

import android.support.annotation.NonNull;

import com.philips.cdp2.commlib.core.configuration.RuntimeConfiguration;
import com.philips.cdp2.commlib.core.store.NetworkNodeDatabase;

public interface DatabaseFetcher {
    NetworkNodeDatabase getNetworkNodeDatabase(@NonNull RuntimeConfiguration runtimeConfiguration);
}
