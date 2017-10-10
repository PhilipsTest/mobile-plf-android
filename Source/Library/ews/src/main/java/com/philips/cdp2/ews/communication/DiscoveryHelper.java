package com.philips.cdp2.ews.communication;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.philips.cdp2.commlib.core.CommCentral;
import com.philips.cdp2.commlib.core.appliance.Appliance;
import com.philips.cdp2.commlib.core.appliance.ApplianceManager;
import com.philips.cdp2.commlib.core.exception.MissingPermissionException;
import com.philips.cdp2.ews.logger.EWSLogger;

public class DiscoveryHelper {

    private static final String TAG = "DiscoveryHelper";

    public interface DiscoveryCallback {
        void onApplianceFound(Appliance appliance);
    }

    @NonNull private final CommCentral commCentral;

    @Nullable private DiscoveryCallback callback;

    @NonNull private final ApplianceManager.ApplianceListener<Appliance>
            applianceListener = new ApplianceManager.ApplianceListener<Appliance>() {
        @Override
        public void onApplianceFound(@NonNull Appliance appliance) {
            if (callback != null) {
                callback.onApplianceFound(appliance);
            }
        }

        @Override
        public void onApplianceUpdated(@NonNull Appliance appliance) {
            // TODO check if called during our flow
        }

        @Override
        public void onApplianceLost(@NonNull Appliance appliance) {
            // TODO check if called during our flow
        }
    };

    public DiscoveryHelper(@NonNull CommCentral commCentral) {
        this.commCentral = commCentral;
    }

    public void startDiscovery(@NonNull DiscoveryCallback callback) {
        this.callback = callback;
        try {
            commCentral.getApplianceManager().addApplianceListener(applianceListener);
            commCentral.startDiscovery();
        } catch (MissingPermissionException e) {
            EWSLogger.e(TAG, "Starting LAN discovery threw MissingPermissionException exception " +
                    e.getLocalizedMessage());
        }
    }

    public void stopDiscovery() {
        commCentral.stopDiscovery();
        callback = null;
    }
}
