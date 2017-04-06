/*
 * (C) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.cdpp.dicommtestapp;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.philips.cdp.dicommclient.discovery.DICommClientWrapper;
import com.philips.cdp2.commlib.ble.context.BleTransportContext;
import com.philips.cdp2.commlib.lan.context.LanTransportContext;
import com.philips.cdpp.dicommtestapp.appliance.GenericApplianceFactory;
import com.philips.cdpp.dicommtestapp.background.BackgroundConnectionService;
import com.philips.cdpp.dicommtestapp.strategy.BleCommStrategy;
import com.philips.cdpp.dicommtestapp.strategy.CommStrategy;
import com.philips.cdpp.dicommtestapp.strategy.WlanCommStrategy;

import java.util.ArrayList;
import java.util.List;

public class DiCommTestApp extends Application {
    private static final String TAG = "DiCommTestApp";

    private GenericApplianceFactory applianceFactory;
    private BleTransportContext bleTransportContext;
    private LanTransportContext lanTransportContext;
    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "onServiceConnected: service connected");
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "onServiceDisconnected: service disconnected");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        bleTransportContext = new BleTransportContext(this, true);
        lanTransportContext = new LanTransportContext(this);
        applianceFactory = new GenericApplianceFactory();

        if (DICommClientWrapper.getContext() == null) {
            DICommClientWrapper.initializeDICommLibrary(this, applianceFactory, null, null);
        }

        doBindService();
    }

    public List<CommStrategy> getAvailableContexts() {
        List<CommStrategy> contexts = new ArrayList<>();

        BleCommStrategy bleStrategy = new BleCommStrategy(bleTransportContext);
        contexts.add(bleStrategy);

        WlanCommStrategy wifiStrategy = new WlanCommStrategy(lanTransportContext);
        contexts.add(wifiStrategy);

        return contexts;
    }

    private void doBindService() {
        Intent serviceIntent = new Intent(this, BackgroundConnectionService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    public GenericApplianceFactory getApplianceFactory() {
        return this.applianceFactory;
    }
}
