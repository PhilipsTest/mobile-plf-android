/**
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */

package com.philips.platform.ews.startconnectwithdevice;

import android.databinding.ObservableField;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.philips.platform.ews.R;
import com.philips.platform.ews.configuration.BaseContentConfiguration;
import com.philips.platform.ews.configuration.HappyFlowContentConfiguration;
import com.philips.platform.ews.confirmwifi.ConfirmWifiNetworkViewModel;
import com.philips.platform.ews.navigation.Navigator;
import com.philips.platform.ews.tagging.EWSTagger;
import com.philips.platform.ews.tagging.Page;
import com.philips.platform.ews.tagging.Tag;
import com.philips.platform.ews.util.StringProvider;
import com.philips.platform.ews.wifi.WiFiUtil;

import javax.inject.Inject;

public class StartConnectWithDeviceViewModel {

    @NonNull
    public final ObservableField<String> title;
    @NonNull
    public final ObservableField<String> body;
    @NonNull
    public final ObservableField<String> description;
    @Nullable
    private ConfirmWifiNetworkViewModel.ViewCallback viewCallback;
    @NonNull
    public final Drawable image;
    @NonNull
    private final Navigator navigator;
    @NonNull
    private final WiFiUtil wiFiUtil;
    @NonNull
    private final StringProvider stringProvider;
    @NonNull
    private final BaseContentConfiguration baseConfig;
    @NonNull
    private final EWSTagger ewsTagger;


    @Inject
    public StartConnectWithDeviceViewModel(@NonNull final Navigator navigator,
                                           @NonNull final StringProvider stringProvider,
                                           @NonNull final WiFiUtil wiFiUtil,
                                           @NonNull final HappyFlowContentConfiguration happyFlowConfig,
                                           @NonNull final BaseContentConfiguration baseConfig,
                                           @NonNull final EWSTagger ewsTagger) {
        this.navigator = navigator;
        this.stringProvider = stringProvider;
        this.baseConfig = baseConfig;
        title = new ObservableField<>(getTitle(happyFlowConfig, baseConfig));
        body = new ObservableField<>(getBody(baseConfig));
        description = new ObservableField<>(getDescription(baseConfig));
        image = getImage(happyFlowConfig);
        this.wiFiUtil = wiFiUtil;
        this.ewsTagger = ewsTagger;
    }

    protected void setViewCallback(@Nullable ConfirmWifiNetworkViewModel.ViewCallback viewCallback) {
        this.viewCallback = viewCallback;
    }

    @VisibleForTesting
    @NonNull
    public String getTitle(@NonNull HappyFlowContentConfiguration happyFlowConfig,
                           @NonNull BaseContentConfiguration baseConfig) {
        return stringProvider.getString(happyFlowConfig.getGettingStartedScreenTitle(),
                baseConfig.getDeviceName());
    }

    @VisibleForTesting
    @NonNull
    public String getDescription(@NonNull BaseContentConfiguration baseConfig) {
        return stringProvider.getString(R.string.label_ews_get_started_description,
                baseConfig.getDeviceName());
    }

    @VisibleForTesting
    @NonNull
    public String getBody(@NonNull BaseContentConfiguration baseConfig) {
        return stringProvider.getString(R.string.label_ews_get_started_body,
                baseConfig.getDeviceName());
    }

    @NonNull
    @VisibleForTesting
    Drawable getImage(@NonNull HappyFlowContentConfiguration happyFlowContentConfiguration) {
        return stringProvider.getImageResource(happyFlowContentConfiguration.getGettingStartedScreenImage());
    }

    public void onGettingStartedButtonClicked() {
        if (viewCallback != null && !wiFiUtil.isHomeWiFiEnabled()) {
            viewCallback.showTroubleshootHomeWifiDialog(baseConfig, ewsTagger);
        } else {
            tapGetStarted();
            navigator.navigateToHomeNetworkConfirmationScreen();
        }
    }

    private void tapGetStarted() {
        ewsTagger.trackActionSendData(Tag.KEY.SPECIAL_EVENTS, Tag.ACTION.GET_STARTED);
    }

    public void trackPageName() {
        ewsTagger.trackPage(Page.GET_STARTED);
    }

    public void onDestroy() {
        if (navigator.getFragmentNavigator().shouldFinish()) {
            ewsTagger.pauseLifecycleInfo();
        }

    }
}