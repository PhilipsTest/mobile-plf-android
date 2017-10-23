package com.philips.cdp2.ews.settingdeviceinfo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.philips.cdp.dicommclient.port.DICommPortListener;
import com.philips.cdp.dicommclient.port.common.DevicePort;
import com.philips.cdp.dicommclient.request.Error;
import com.philips.cdp2.ews.appliance.EWSGenericAppliance;
import com.philips.cdp2.ews.util.TextUtil;

import javax.inject.Inject;
import javax.inject.Named;

public class DeviceFriendlyNameChanger {

    @NonNull
    private final EWSGenericAppliance appliance;
    @Nullable
    private DeviceFriendlyNameChanger.Callback callback;
    @NonNull
    private final DICommPortListener<DevicePort> portListener = new DICommPortListener<DevicePort>() {
        @Override
        public void onPortUpdate(DevicePort devicePort) {
            notifySuccess();
        }

        @Override
        public void onPortError(DevicePort devicePort, Error error, @Nullable String s) {
            notifyFailure();
        }
    };

    @Inject
    public DeviceFriendlyNameChanger(@NonNull @Named("ews.temporary.appliance") EWSGenericAppliance appliance) {
        this.appliance = appliance;
    }

    public void changeFriendlyName(@NonNull String newFriendlyName, @NonNull DeviceFriendlyNameChanger.Callback callback) {
        this.callback = callback;
        if (!TextUtil.isEmpty(newFriendlyName)) {
            DevicePort devicePort = appliance.getDevicePort();
            devicePort.addPortListener(portListener);
            devicePort.setDeviceName(newFriendlyName);
        } else {
            notifySuccess();
        }
    }

    public void clear() {
        callback = null;
    }

    private void notifySuccess() {
        if (callback != null) {
            callback.onFriendlyNameChangingSuccess();
        }
    }

    private void notifyFailure() {
        if (callback != null) {
            callback.onFriendlyNameChangingFailed();
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    @Nullable
    Callback getCallback() {
        return callback;
    }

    public interface Callback {
        void onFriendlyNameChangingSuccess();

        void onFriendlyNameChangingFailed();
    }
}
