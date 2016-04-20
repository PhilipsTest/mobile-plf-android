
package com.philips.cdp.coppa.registration;

import android.app.Application;

import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.tagging.Tagging;

import java.util.Locale;

public class RegistrationCoppaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RLog.d(RLog.APPLICATION, "RegistrationCoppaApplication : onCreate");
        RLog.d(RLog.JANRAIN_INITIALIZE, "RegistrationCoppaApplication : Janrain initialization with locale : " + Locale.getDefault());
        Tagging.enableAppTagging(true);
        Tagging.setTrackingIdentifier("integratingApplicationAppsId");
        Tagging.setLaunchingPageName("demoapp:home");
        RegistrationConfiguration.getInstance().setPrioritisedFunction(RegistrationFunction.Registration);

        Locale locale;
        String languageCode = Locale.getDefault().getLanguage();
        String countryCode = Locale.getDefault().getCountry();

        if (languageCode != null && countryCode != null) {
            locale = new Locale(languageCode.toLowerCase(), countryCode.toUpperCase());
        } else {
            throw new RuntimeException("Please check your locale is correct");
        }

        if (locale != null) {
            RegistrationHelper.getInstance().initializeUserRegistration(this, locale);
            Tagging.init( this, "Philips Registration");
        }


    }

}

