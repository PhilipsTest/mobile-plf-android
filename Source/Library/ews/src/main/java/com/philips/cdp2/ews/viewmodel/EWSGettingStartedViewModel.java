/*
 * (C) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */

package com.philips.cdp2.ews.viewmodel;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.philips.cdp2.ews.R;
import com.philips.cdp2.ews.configuration.BaseContentConfiguration;
import com.philips.cdp2.ews.configuration.HappyFlowContentConfiguration;
import com.philips.cdp2.ews.microapp.EWSCallbackNotifier;
import com.philips.cdp2.ews.navigation.Navigator;
import com.philips.cdp2.ews.util.StringProvider;

import javax.inject.Inject;

public class EWSGettingStartedViewModel {

    @NonNull public final ObservableField<String> title;
    @NonNull public final ObservableField<String> note;

    @NonNull private final Navigator navigator;
    @NonNull private final StringProvider stringProvider;

    @Inject
    public EWSGettingStartedViewModel(@NonNull final Navigator navigator,
                                      @NonNull final StringProvider stringProvider,
                                      @NonNull final HappyFlowContentConfiguration happyFlowConfig,
                                      @NonNull final BaseContentConfiguration baseConfig) {
        this.navigator = navigator;
        this.stringProvider = stringProvider;
        title = new ObservableField<>(getTitle(happyFlowConfig, baseConfig));
        note = new ObservableField<>(getNote(baseConfig));
    }

    @NonNull
    private String getTitle(@NonNull HappyFlowContentConfiguration happyFlowConfig,
                            @NonNull BaseContentConfiguration baseConfig) {
        return stringProvider.getString(happyFlowConfig.getGettingStartedScreenTitle(),
                baseConfig.getDeviceName());
    }

    @NonNull
    private String getNote(@NonNull BaseContentConfiguration baseConfig) {
        return stringProvider.getString(R.string.label_ews_get_started_description,
                baseConfig.getDeviceName());
    }

    public void onGettingStartedButtonClicked() {
        navigator.navigateToHomeNetworkConfirmationScreen();
    }

    public void onBackPressed() {
        EWSCallbackNotifier.getInstance().onBackPressed();
    }
}