package com.philips.platform.baseapp.screens.Optin;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.philips.cdp.registration.configuration.RegistrationLaunchMode;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.ui.utils.RegistrationContentConfiguration;
import com.philips.cdp.registration.ui.utils.UIFlow;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URLaunchInput;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.abtestclient.ABTestClientInterface;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;


public class MarketingOptin extends BaseState implements UserRegistrationUIEventListener {

    Context context;
    private FragmentLauncher fragmentLauncher;
    public static String AB_TEST_OPTIN_IMAGE_KEY = "optin_image";

    public MarketingOptin() {
        super(AppStates.MY_DETAILS_STATE);
    }

    @Override
    public void navigate(UiLauncher uiLauncher) {

        fragmentLauncher = (FragmentLauncher) uiLauncher;
        URLaunchInput urLaunchInput = new URLaunchInput();
        urLaunchInput.setEndPointScreen(RegistrationLaunchMode.MARKETING_OPT);
        urLaunchInput.setRegistrationFunction(RegistrationFunction.Registration);
        urLaunchInput.setRegistrationContentConfiguration(getRegistrationContentConfiguration());
        urLaunchInput.setUIFlow(UIFlow.FLOW_B);
        urLaunchInput.enableAddtoBackStack(true);
        urLaunchInput.setUserRegistrationUIEventListener(this);
        URInterface urInterface = new URInterface();
        urInterface.launch(fragmentLauncher,urLaunchInput);

    }

    RegistrationContentConfiguration getRegistrationContentConfiguration() {
        RegistrationContentConfiguration registrationContentConfiguration = new RegistrationContentConfiguration();
        ABTestClientInterface abTesting = getAppInfra().getAbTesting();
        String testValue = abTesting.getTestValue(AB_TEST_OPTIN_IMAGE_KEY, context.getString(R.string.Ra_default_value), ABTestClientInterface.UPDATETYPE.APP_UPDATE);
        if (testValue.equalsIgnoreCase(context.getString(R.string.RA_abTesting_Sonicare))) {
            registrationContentConfiguration.enableMarketImage(R.drawable.abtesting_sonicare);
            registrationContentConfiguration.setOptInTitleText(context.getString(R.string.RA_mkt_optin_title_text));
            registrationContentConfiguration.setOptInQuessionaryText(context.getString(R.string.RA_quessionary_text));
        } else if(testValue.equalsIgnoreCase(context.getString(R.string.RA_abTesting_Kitchen))){
            registrationContentConfiguration.enableMarketImage(R.drawable.abtesting_kitchen);
        } else {
            registrationContentConfiguration.enableMarketImage(R.drawable.abtesting_norelco);
        }
        return registrationContentConfiguration;
    }
    @Override
    public void init(Context context) {
        this.context = context;
    }
    protected AppInfraInterface getAppInfra() {
        return ((AppFrameworkApplication) context).getAppInfra();
    }

    @Override
    public void updateDataModel() {

    }

    @Override
    public void onUserRegistrationComplete(Activity activity) {
        Toast.makeText(context, "Counted Successfully", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPrivacyPolicyClick(Activity activity) {

    }

    @Override
    public void onTermsAndConditionClick(Activity activity) {

    }

    @Override
    public void onPersonalConsentClick(Activity activity) {

    }
}
