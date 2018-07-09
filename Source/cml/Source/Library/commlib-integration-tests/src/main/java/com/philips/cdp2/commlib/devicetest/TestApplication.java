/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.commlib.devicetest;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.philips.cdp.cloudcontroller.DefaultCloudController;
import com.philips.cdp.cloudcontroller.api.CloudController;
import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.cdp2.commlib.ble.context.BleTransportContext;
import com.philips.cdp2.commlib.cloud.context.CloudTransportContext;
import com.philips.cdp2.commlib.core.CommCentral;
import com.philips.cdp2.commlib.core.appliance.ApplianceFactory;
import com.philips.cdp2.commlib.core.configuration.RuntimeConfiguration;
import com.philips.cdp2.commlib.devicetest.appliance.ReferenceApplianceFactory;
import com.philips.cdp2.commlib.lan.context.LanTransportContext;
import com.philips.platform.appinfra.AppInfraInterface;

public class TestApplication extends Application {

    private static CommCentral commCentral;
    private static CloudController cloudController;

    public CommCentral getCommCentral() {
        if (commCentral == null) {
            final AppInfraInterface appInfraInterface = new TestAppInfra();
            final RuntimeConfiguration runtimeConfiguration = new RuntimeConfiguration(this, appInfraInterface);

            cloudController = setupCloudController(this);

            final BleTransportContext bleTransportContext = new BleTransportContext(runtimeConfiguration, false);
            final LanTransportContext lanTransportContext = new LanTransportContext(runtimeConfiguration);
            final CloudTransportContext cloudTransportContext = new CloudTransportContext(runtimeConfiguration, cloudController);

            final ApplianceFactory applianceFactory = new ReferenceApplianceFactory(bleTransportContext, lanTransportContext, cloudTransportContext);

            commCentral = new CommCentral(applianceFactory, runtimeConfiguration, bleTransportContext, lanTransportContext, cloudTransportContext);
        }

        return commCentral;
    }

    public CloudController getCloudController() {
        return cloudController;
    }

    @NonNull
    private CloudController setupCloudController(final @NonNull Context context) {
        final CloudController cloudController = new DefaultCloudController(context, new CommlibUappKpsConfigurationInfo());

        String ICPClientVersion = cloudController.getICPClientVersion();
        DICommLog.i(DICommLog.ICPCLIENT, "ICPClientVersion :" + ICPClientVersion);

        return cloudController;
    }

}
