/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.coppa.test;

import android.support.multidex.MultiDex;
import android.test.ActivityInstrumentationTestCase2;

import com.philips.cdp.registration.coppa.ui.activity.RegistrationCoppaActivity;

/**
 * Created by 310202337 on 3/28/2016.
 */
public class ConsentUpdaterTest extends ActivityInstrumentationTestCase2<RegistrationCoppaActivity> {

    public ConsentUpdaterTest() {
        super(RegistrationCoppaActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        MultiDex.install(getInstrumentation().getTargetContext());
        super.setUp();
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
    }



}
