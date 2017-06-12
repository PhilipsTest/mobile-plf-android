package com.philips.dhpclient.response;

import android.support.multidex.MultiDex;
import android.test.InstrumentationTestCase;

import org.junit.Before;

import java.util.Map;

/**
 * Created by 310243576 on 8/24/2016.
 */
public class DhpAuthenticationResponseTest extends InstrumentationTestCase {
    DhpAuthenticationResponse mDhpAuthenticationResponse;
    DhpAuthenticationResponse mDhpAuthenticationResponse1;

    Map<String, Object> mRawResponse;


    @Before
    public void setUp() throws Exception {
        MultiDex.install(getInstrumentation().getTargetContext());
        super.setUp();
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());

        mDhpAuthenticationResponse = new DhpAuthenticationResponse(mRawResponse);
        mDhpAuthenticationResponse1 = new DhpAuthenticationResponse(mRawResponse);



    }

    public void testDhpAuthenticationResponse()
    {
        assertNotNull(mDhpAuthenticationResponse);
        mDhpAuthenticationResponse = new DhpAuthenticationResponse("accessToken","refreshToken",1221,"userId",mRawResponse) ;
        assertNotNull(mDhpAuthenticationResponse);
        assertTrue(mDhpAuthenticationResponse.equals(mDhpAuthenticationResponse));
        assertFalse(mDhpAuthenticationResponse.equals(mDhpAuthenticationResponse1));
        assertFalse(mDhpAuthenticationResponse.equals(mRawResponse));
        assertFalse(mDhpAuthenticationResponse.equals(null));

        assertNotNull(mDhpAuthenticationResponse.hashCode());
        assertNotNull(mDhpAuthenticationResponse.toString());
    }



}