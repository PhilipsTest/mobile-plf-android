package com.philips.cdp.registration.ui.traditional;

import com.philips.cdp.registration.RegistrationApiInstrumentationBase;

import org.junit.Before;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class RegistrationFragmentTest extends RegistrationApiInstrumentationBase {
    RegistrationFragment registrationFragment;
    @Before
    public void setUp() throws Exception {
        super.setUp();
        registrationFragment= new RegistrationFragment();
    }

}