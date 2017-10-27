package com.philips.cdp2.ews.troubleshooting.resetdevice;

import android.databinding.ObservableField;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.philips.cdp2.ews.R;
import com.philips.cdp2.ews.configuration.BaseContentConfiguration;
import com.philips.cdp2.ews.configuration.TroubleShootContentConfiguration;
import com.philips.cdp2.ews.navigation.Navigator;
import com.philips.cdp2.ews.util.StringProvider;

import javax.inject.Inject;

public class ResetDeviceTroubleshootingViewModel {

    @NonNull private final Navigator navigator;
    @NonNull public final ObservableField<String> title;
    @NonNull public final ObservableField<String> description;
    @NonNull private StringProvider stringProvider;
    @NonNull public final Drawable resetDeviceImage;

    @Inject
    public ResetDeviceTroubleshootingViewModel(@NonNull Navigator navigator, @NonNull StringProvider stringProvider, @NonNull BaseContentConfiguration contentConfiguration,
                                                   @NonNull TroubleShootContentConfiguration troubleShootContentConfiguration) {
        this.navigator = navigator;
        this.stringProvider = stringProvider;
        title = new ObservableField<>(getTitle(troubleShootContentConfiguration, contentConfiguration));
        description = new ObservableField<>(getNote(troubleShootContentConfiguration, contentConfiguration));
        resetDeviceImage = getResetDeviceImage(troubleShootContentConfiguration);
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    @NonNull
    Drawable getResetDeviceImage(@NonNull TroubleShootContentConfiguration troubleShootContentConfiguration) {
        return stringProvider.getImageResource(troubleShootContentConfiguration.getResetDeviceImage());
    }

    public void onDoneButtonClicked() {
        navigator.navigateToCompletingDeviceSetupScreen();
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    @NonNull
    String getTitle(@NonNull TroubleShootContentConfiguration troubleShootContentConfiguration,
                    @NonNull BaseContentConfiguration baseConfig) {
        return stringProvider.getString(troubleShootContentConfiguration.getResetDeviceTitle(),
                baseConfig.getDeviceName());
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    @NonNull
    String getNote (@NonNull TroubleShootContentConfiguration troubleShootContentConfiguration,
                    @NonNull BaseContentConfiguration baseConfig) {
        return stringProvider.getString(troubleShootContentConfiguration.getResetDeviceBody(),
                baseConfig.getDeviceName());
    }


}
