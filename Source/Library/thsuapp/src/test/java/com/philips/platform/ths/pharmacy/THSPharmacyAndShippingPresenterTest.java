package com.philips.platform.ths.pharmacy;

import android.os.Bundle;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.manager.ConsumerManager;
import com.philips.platform.ths.CustomRobolectricRunnerAmwel;
import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.ths.registration.THSConsumer;
import com.philips.platform.ths.utility.THSManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(CustomRobolectricRunnerAmwel.class)
public class THSPharmacyAndShippingPresenterTest {
    THSPharmacyAndShippingPresenter thsPharmacyAndShippingPresenter;

    @Mock
    THSPharmacyShippingViewInterface thsPharmacyShippingViewInterface;

    @Mock
    THSConsumer thsConsumer;

    @Mock
    Consumer consumer;

    @Mock
    AWSDK awsdkMock;

    @Mock
    ConsumerManager consumerManagerMock;


    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        thsPharmacyAndShippingPresenter = new THSPharmacyAndShippingPresenter(thsPharmacyShippingViewInterface);
        THSManager.getInstance().setAwsdk(awsdkMock);
        when(awsdkMock.getConsumerManager()).thenReturn(consumerManagerMock);
        THSManager.getInstance().setPTHConsumer(thsConsumer);
        when(thsConsumer.getConsumer()).thenReturn(consumer);
    }

    @Test
    public void testOnEvent(){
        thsPharmacyAndShippingPresenter.onEvent(R.id.ps_edit_pharmacy);
        verify(thsPharmacyShippingViewInterface).startSearchPharmacy();
        thsPharmacyAndShippingPresenter.onEvent(R.id.ps_edit_consumer_shipping_address);
        verify(thsPharmacyShippingViewInterface).startEditShippingAddress();
        thsPharmacyAndShippingPresenter.onEvent(R.id.ths_ps_continue_button);
        verify(thsPharmacyShippingViewInterface).addFragment(any(THSBaseFragment.class),any(String.class),any(Bundle.class), true);
    }
}
