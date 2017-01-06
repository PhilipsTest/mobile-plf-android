package com.philips.cdp.registration.coppa.base;

import android.support.multidex.MultiDex;
import android.test.InstrumentationTestCase;

import org.junit.Test;

/**
 * Created by 310243576 on 8/20/2016.
 */
public class CoppaResendErrorTest extends InstrumentationTestCase{
    CoppaResendError mCoppaResendError ;

    @Override
    protected void setUp() throws Exception {
        MultiDex.install(getInstrumentation().getTargetContext());
        super.setUp();
        System.setProperty("dexmaker.dexcache", getInstrumentation()
                .getTargetContext().getCacheDir().getPath());
    mCoppaResendError = new CoppaResendError();
    }

    @Test
    public void testGetErrorCode() throws Exception {
        mCoppaResendError.setErrorCode(1);
        assertEquals(1,mCoppaResendError.getErrorCode());

    }

    @Test
    public void testGetErrorDesc() throws Exception {
     mCoppaResendError.setErrorDesc("TestError");
        assertEquals("TestError",mCoppaResendError.getErrorDesc());

    }
}