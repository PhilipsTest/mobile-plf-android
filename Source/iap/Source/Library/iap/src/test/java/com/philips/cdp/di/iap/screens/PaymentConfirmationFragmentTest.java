package com.philips.cdp.di.iap.screens;

import android.content.Context;
import android.os.Bundle;

import com.philips.cdp.di.iap.BuildConfig;
import com.philips.cdp.di.iap.CustomRobolectricRunner;
import com.philips.cdp.di.iap.TestUtils;
import com.philips.cdp.di.iap.utils.ModelConstants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class PaymentConfirmationFragmentTest {
    private Context mContext;
    PaymentConfirmationFragment paymentConfirmationFragment;

    @Before
    public void setUp() {
        initMocks(this);

        mContext = RuntimeEnvironment.application;
        TestUtils.getStubbedStore();
        TestUtils.getStubbedHybrisDelegate();
        paymentConfirmationFragment = PaymentConfirmationFragment.createInstance(new Bundle(), InAppBaseFragment.AnimationType.NONE);

    }

    @Test
    public void shouldDisplayAddressSelectionFragment() {
        Bundle bundle=new Bundle();
        bundle.putBoolean(ModelConstants.PAYMENT_SUCCESS_STATUS,true);
        bundle.putString(ModelConstants.ORDER_NUMBER,"12345");
        paymentConfirmationFragment.setArguments(bundle);
        SupportFragmentTestUtil.startFragment(paymentConfirmationFragment);
    }

    @Test
    public void shouldDisplayAddressSelectionFragmentWhenPAYMENT_SUCCESS_STATUSisFalse() {
        Bundle bundle=new Bundle();
        bundle.putBoolean(ModelConstants.PAYMENT_SUCCESS_STATUS,false);
        bundle.putString(ModelConstants.ORDER_NUMBER,"12345");
        paymentConfirmationFragment.setArguments(bundle);
        SupportFragmentTestUtil.startFragment(paymentConfirmationFragment);
    }


}