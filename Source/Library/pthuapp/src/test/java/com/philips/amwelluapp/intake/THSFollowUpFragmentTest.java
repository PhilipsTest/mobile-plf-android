package com.philips.amwelluapp.intake;

import com.americanwell.sdk.AWSDK;
import com.philips.amwelluapp.CustomRobolectricRunnerAmwel;
import com.philips.amwelluapp.activity.PTHLaunchActivity;
import com.philips.amwelluapp.utility.PTHManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

/**
 * Created by philips on 7/6/17.
 */


@RunWith(CustomRobolectricRunnerAmwel.class)
public class THSFollowUpFragmentTest {

    private PTHLaunchActivity mActivity;
    private THSFollowUpFragment mTHSFollowUpFragment;


    @Mock
    AWSDK awsdkMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ShadowLog.stream = System.out;
        PTHManager.getInstance().setAwsdk(awsdkMock);
        mActivity = Robolectric.buildActivity(PTHLaunchActivity.class).create().get();
        Assert.assertNotNull(mActivity);
        mTHSFollowUpFragment = new THSFollowUpFragment();
        Assert.assertNotNull(mTHSFollowUpFragment);
    }

    @Test
    public void getFragmentActivity() throws Exception {
        SupportFragmentTestUtil.startFragment(mTHSFollowUpFragment);
        Assert.assertNotNull(mTHSFollowUpFragment.getFragmentActivity());

    }


}