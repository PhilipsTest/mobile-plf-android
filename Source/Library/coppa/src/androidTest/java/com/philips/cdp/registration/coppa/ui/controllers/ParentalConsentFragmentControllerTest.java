package com.philips.cdp.registration.coppa.ui.controllers;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.test.InstrumentationTestCase;

import com.philips.cdp.registration.coppa.base.CoppaExtension;

import org.junit.Test;
import org.mockito.Mock;

/**
 * Created by 310230979  on 8/30/2016.
 */
public class ParentalConsentFragmentControllerTest extends InstrumentationTestCase {

    Context mContext;
    ParentalConsentFragmentController mParentalConsentFragmentController;
    @Mock
    CoppaExtension mCoppaExtension;

    @Override
    protected void setUp() throws Exception {
        MultiDex.install(getInstrumentation().getTargetContext());
        super.setUp();
        System.setProperty("dexmaker.dexcache", getInstrumentation()
                .getTargetContext().getCacheDir().getPath());
        mContext = getInstrumentation().getTargetContext();

    }

    @Test
    public void testParentalApprovalFragmentController(){
       /* ParentalConsentFragment parentalApprovalFragment = new ParentalConsentFragment();
        mParentalConsentFragmentController = new ParentalConsentFragmentController(parentalApprovalFragment);
      */  assertNotNull(mContext);    }

}