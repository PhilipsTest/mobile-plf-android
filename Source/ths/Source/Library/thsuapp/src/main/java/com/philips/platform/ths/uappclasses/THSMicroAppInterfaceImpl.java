package com.philips.platform.ths.uappclasses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.ths.activity.THSLaunchActivity;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.ths.consent.THSLocationConsentProvider;
import com.philips.platform.ths.init.THSInitFragment;
import com.philips.platform.ths.utility.THSConstants;
import com.philips.platform.ths.utility.THSManager;
import com.philips.platform.uappframework.UappInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;
import com.philips.platform.uappframework.uappinput.UappSettings;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ColorRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.ThemeConfig;
import com.philips.platform.uid.thememanager.ThemeConfiguration;

import java.util.Collections;
import java.util.List;

import static com.philips.platform.ths.utility.THSConstants.KEY_DEEP_LINKING_FLOW;

@SuppressWarnings("unchecked")
public class THSMicroAppInterfaceImpl implements UappInterface {
    protected Context context;
    protected AppInfraInterface appInfra;
    static final long serialVersionUID = 1153L;
    /**
     * @param uappDependencies - App dependencies
     * @param uappSettings     - App settings
     */
    @Override
    public void init(@NonNull final UappDependencies uappDependencies, final UappSettings uappSettings) {
        this.context = uappSettings.getContext();
        appInfra = uappDependencies.getAppInfra();
       // final ConsentHandlerInterface consentHandlerInterface = new ConsentInteractor(ConsentsClient.getInstance());
        appInfra.getConsentManager().deregisterHandler(Collections.singletonList(THSLocationConsentProvider.THS_LOCATION));
     //   appInfra.getConsentManager().registerHandler(Collections.singletonList(THSLocationConsentProvider.THS_LOCATION), consentHandlerInterface);
    }

    /**
     * @param uiLauncher - Launcher to differentiate activity or fragment
     */
    @Override
    public void launch(final UiLauncher uiLauncher, final UappLaunchInput uappLaunchInput) {
        THSMicroAppLaunchInput thsMicroAppLaunchInput;
        boolean isDeepLinkingFlow = false;
        if( uappLaunchInput instanceof THSMicroAppLaunchInput){
            thsMicroAppLaunchInput=(THSMicroAppLaunchInput)uappLaunchInput;
            THSManager.getInstance().setThsCompletionProtocol(thsMicroAppLaunchInput.getThsCompletionProtocol());
            isDeepLinkingFlow=thsMicroAppLaunchInput.isAppointmentFlow();
        }
        THSManager.getInstance().setAppInfra(appInfra);
        if (uiLauncher instanceof ActivityLauncher) {
            Intent intent = new Intent(context, THSLaunchActivity.class);
            intent.putExtra(THSConstants.KEY_ACTIVITY_THEME, ((ActivityLauncher) uiLauncher).getUiKitTheme());
            intent.putExtra(KEY_DEEP_LINKING_FLOW,isDeepLinkingFlow);
            if(themeConfigurationExists((ActivityLauncher) uiLauncher)) {
                intent.putExtras(getThemeConfigsIntent((ActivityLauncher) uiLauncher));
            }
            if(null != ((ActivityLauncher) uiLauncher).getScreenOrientation()) {
                ActivityLauncher.ActivityOrientation activityOrientation = ((ActivityLauncher) uiLauncher).getScreenOrientation();
                intent.putExtra(THSConstants.KEY_ORIENTATION, activityOrientation);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            final FragmentLauncher fragmentLauncher = (FragmentLauncher) uiLauncher;
            FragmentTransaction fragmentTransaction = (fragmentLauncher.getFragmentActivity()).getSupportFragmentManager().beginTransaction();
            THSBaseFragment thsBaseFragment;
            thsBaseFragment = new THSInitFragment();
            lauchFirstFragment(thsBaseFragment,fragmentLauncher,fragmentTransaction,isDeepLinkingFlow);
        }
    }

    private boolean themeConfigurationExists(ActivityLauncher uiLauncher) {
        return null != uiLauncher.getDlsThemeConfiguration();
    }

    private Intent getThemeConfigsIntent(ActivityLauncher activityLauncher) {
        ThemeConfiguration themeConfiguration = activityLauncher.getDlsThemeConfiguration();
        List<ThemeConfig> configurations = themeConfiguration.getConfigurations();
        return getConfigurationIntent(configurations);
    }

    private Intent getConfigurationIntent(List<ThemeConfig> configurations) {
        Intent intent = new Intent();
        for (ThemeConfig config : configurations) {
            if(config instanceof ColorRange) {
                intent.putExtra(THSConstants.KEY_COLOR_RANGE, ((ColorRange)config));
            }
            if (config instanceof ContentColor) {
                intent.putExtra(THSConstants.KEY_CONTENT_COLOR, ((ContentColor)config));
            }
            if(config instanceof NavigationColor) {
                intent.putExtra(THSConstants.KEY_NAVIGATION_COLOR, ((NavigationColor)config));
            }
            if(config instanceof AccentRange) {
                intent.putExtra(THSConstants.KEY_ACCENT_RANGE, ((AccentRange)config));
            }
        }
        return intent;
    }

    private void lauchFirstFragment(THSBaseFragment thsBaseFragment,FragmentLauncher fragmentLauncher, FragmentTransaction fragmentTransaction,boolean isDeeplinkingFlow) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_DEEP_LINKING_FLOW,isDeeplinkingFlow);
        thsBaseFragment.setArguments(bundle);
        thsBaseFragment.setActionBarListener(fragmentLauncher.getActionbarListener());
        thsBaseFragment.setFragmentLauncher(fragmentLauncher);
        fragmentTransaction.replace(fragmentLauncher.getParentContainerResourceID(), thsBaseFragment, THSInitFragment.TAG).
                addToBackStack(THSInitFragment.TAG).commitAllowingStateLoss();
    }
}
