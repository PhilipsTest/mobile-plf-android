package com.philips.dhpclient.util.test;

import android.test.ActivityUnitTestCase;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.philips.dhpclient.util.DateUtil;

/**
 * Created by 310202337 on 10/7/2015.
 */
public class DateUtilTest extends InstrumentationTestCase {



    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", getInstrumentation()
                .getTargetContext().getCacheDir().getPath());

    }

    @SmallTest
    public void testgetStimeStamp(){

        assertNotNull(DateUtil.getTimestamp());

    }
}
