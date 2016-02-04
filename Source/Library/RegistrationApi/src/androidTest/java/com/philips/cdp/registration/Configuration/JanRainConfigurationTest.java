package com.philips.cdp.registration.Configuration;

import android.test.ActivityInstrumentationTestCase2;

import com.philips.cdp.registration.configuration.JanRainConfiguration;
import com.philips.cdp.registration.configuration.RegistrationClientId;
import com.philips.cdp.registration.ui.traditional.RegistrationActivity;

/**
 * Created by vinayak on 28/01/16.
 */
public class JanRainConfigurationTest extends ActivityInstrumentationTestCase2<RegistrationActivity> {

    //Constructor for instrimental test case
    public JanRainConfigurationTest() {
        super(RegistrationActivity.class);
    }

    public void testJanRainConfigurationConstructor() {

        JanRainConfiguration janRainConfiguration = new JanRainConfiguration(null);

        assertEquals(null, janRainConfiguration.getClientIds());

    }

    public void testJanRainConfigurationSetter() {

        JanRainConfiguration janRainConfiguration = new JanRainConfiguration();
        janRainConfiguration.setClientIds(null);

        assertEquals(null, janRainConfiguration.getClientIds());

    }

    public void testJanRainConfigurationGetter() {

        JanRainConfiguration janRainConfiguration = new JanRainConfiguration();
        janRainConfiguration.setClientIds(new RegistrationClientId());
        assertNotSame(null, janRainConfiguration.getClientIds());

    }
}
