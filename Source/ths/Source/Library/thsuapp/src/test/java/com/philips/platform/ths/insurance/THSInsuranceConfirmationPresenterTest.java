package com.philips.platform.ths.insurance;

import android.content.Context;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.insurance.SubscriptionUpdateRequest;
import com.americanwell.sdk.manager.ConsumerManager;
import com.philips.platform.ths.R;
import com.philips.platform.ths.registration.dependantregistration.THSConsumer;
import com.philips.platform.ths.sdkerrors.THSSDKError;
import com.philips.platform.ths.utility.THSManager;
import com.philips.platform.uappframework.listener.ActionBarListener;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */


public class THSInsuranceConfirmationPresenterTest {


    THSInsuranceConfirmationPresenter thsInsuranceConfirmationPresenter;
    THSInsuranceConfirmationFragment tHSInsuranceConfirmationFragment;

    Method mMethod;


    @Mock
    AWSDK awsdkMock;

    @Mock
    Consumer consumerMoxk;

    @Mock
    Consumer thsConsumerWrapperMock;

    @Mock
    ConsumerManager consumerManagerMock;

    @Mock
    ActionBarListener actionBarListenerMock;

    @Mock
    THSSDKError thssdkError;

    @Mock
    SDKError sdkErrorMock;

    @Mock
    Map mapMock;


    Throwable throwableMock;

    @Mock
    Context contextMock;

    @Mock
    SubscriptionUpdateRequest subscriptionUpdateRequestMock;

    @Mock
    THSSubscriptionUpdateRequest thsSubscriptionUpdateRequestMock;
    @Mock
    THSConsumer thsConsumerMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        THSManager.getInstance().setAwsdk(awsdkMock);
        THSManager.getInstance().setConsumer(thsConsumerWrapperMock);
        THSManager.getInstance().setThsConsumer(thsConsumerMock);
        when(thsConsumerMock.getConsumer()).thenReturn(consumerMoxk);
        when(awsdkMock.getConsumerManager()).thenReturn(consumerManagerMock);
        tHSInsuranceConfirmationFragment = new THSInsuranceConfirmationFragmentMock();
        tHSInsuranceConfirmationFragment.setActionBarListener(actionBarListenerMock);
        thsInsuranceConfirmationPresenter = new THSInsuranceConfirmationPresenter(tHSInsuranceConfirmationFragment);
    }

    @Test
    public void onEvent() throws Exception {
        thsInsuranceConfirmationPresenter.onEvent( R.id.pth_insurance_confirmation_radio_option_yes);
    }

    @Test
    public void getSubscriptionUpdateRequestWithoutVistContext() throws Exception {
        when(awsdkMock.getConsumerManager().getNewSubscriptionUpdateRequest(thsConsumerMock.getConsumer(),false)).thenReturn(subscriptionUpdateRequestMock);
        Assert.assertNotNull(thsInsuranceConfirmationPresenter.getSubscriptionUpdateRequestWithoutVistContext());

    }

    @Test
    public void onValidationFailure() throws Exception {
        thsInsuranceConfirmationPresenter.onValidationFailure(mapMock);
    }

    @Test
    public void onResponse() throws Exception {
        // when(thssdkError.getSdkError()).thenReturn(sdkErrorMock);
        //when(thsInsuranceConfirmationPresenter.getPaymentMethod()).thenReturn(paymentMethodMock);
        Void avoid = null;
        thsInsuranceConfirmationPresenter.onResponse(avoid,sdkErrorMock);
        verifyNoMoreInteractions(awsdkMock);
    }

    @Test
    public void onFailure() throws Exception {
        thsInsuranceConfirmationPresenter.onFailure(throwableMock);
    }

    @Test
    public void updateInsurance() throws Exception{
        when(awsdkMock.getConsumerManager().getNewSubscriptionUpdateRequest(thsConsumerMock.getConsumer(),false)).thenReturn(subscriptionUpdateRequestMock);
        try {
            mMethod = THSInsuranceConfirmationPresenter.class.getDeclaredMethod("updateInsurance",THSSubscriptionUpdateRequest.class);
            mMethod.setAccessible(true);
            mMethod.invoke(thsInsuranceConfirmationPresenter,thsInsuranceConfirmationPresenter.getSubscriptionUpdateRequestWithoutVistContext());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Assert.fail();
        }

    }

}