package com.philips.cdp.registration.ui.customviews;

import android.support.multidex.MultiDex;
import android.test.InstrumentationTestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class XPasswordTest extends InstrumentationTestCase{

    XPassword xPassword;
    @Before
    public void setUp() throws Exception {
        MultiDex.install(getInstrumentation().getTargetContext());
        xPassword= new XPassword(getInstrumentation().getContext());
    }
    @Test
    public void testAssert(){
        assertNotNull(xPassword);
    }
}