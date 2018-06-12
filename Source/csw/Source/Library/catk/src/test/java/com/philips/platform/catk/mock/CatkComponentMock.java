/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.catk.mock;

import com.philips.cdp.registration.User;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.catk.ConsentsClient;
import com.philips.platform.catk.injection.CatkComponent;

import com.philips.platform.catk.NetworkController;

import android.content.Context;

import java.util.Map;

public class CatkComponentMock implements CatkComponent {

    public ServiceDiscoveryInterfaceMock getServiceDiscoveryInterface_return;

    public User getUser_return;

    public CatkComponentMock() {
    }

    @Override
    public Context context() {
        return null;
    }

    @Override
    public LoggingInterface getLoggingInterface() {
        return new LoggingInterface() {
            @Override
            public LoggingInterface createInstanceForComponent(String s, String s1) {
                return this;
            }

            @Override
            public void log(LogLevel logLevel, String s, String s1) {

            }

            @Override
            public void log(LogLevel logLevel, String s, String s1, Map<String, ?> map) {

            }

            @Override
            public void setHSDPUserUUID(String userUUID) {

            }

            @Override
            public String getCloudLoggingConsentIdentifier() {
                return null;
            }
        };
    }

    @Override
    public RestInterface getRestInterface() {
        return null;
    }

    @Override
    public User getUser() {
        return getUser_return;
    }

    @Override
    public void inject(NetworkController networkController) {

    }

    @Override
    public void inject(ConsentsClient consentsClient) {

    }

    @Override
    public ServiceDiscoveryInterface getServiceDiscoveryInterface() {
        return getServiceDiscoveryInterface_return;
    }
}
