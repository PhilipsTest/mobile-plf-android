/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.platform.ths.pharmacy;


import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.Country;
import com.americanwell.sdk.entity.State;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.manager.ConsumerManager;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.ths.BuildConfig;
import com.philips.platform.ths.R;
import com.philips.platform.ths.registration.dependantregistration.THSConsumer;
import com.philips.platform.ths.utility.THSManager;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uid.view.widget.UIPicker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.List;

import static com.philips.platform.ths.utility.THSConstants.THS_APPLICATION_ID;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class THSShippingAddressFragmentTest {

    @Mock
    Address address;

    @Mock
    Pharmacy pharmacy;

    @Mock
    Consumer thsConsumerWrapper;

    @Mock
    AWSDK awsdkMock;

    @Mock
    VisitContext pthVisitContext;

    @Mock
    Consumer consumerMock;

    @Mock
    VisitContext visitManagerMock;

    @Mock
    ActionBarListener actionBarListenerMock;

    @Mock
    ConsumerManager consumerManagerMock;

    @Mock
    State stateMock;

    @Mock
    Address addressMock;

    @Mock
    THSShippingAddressFragmentMock thsShippingAddressFragmentMock;

    @Mock
    FragmentActivity activityMock;

    @Mock
    FragmentManager fragmentManagerMock;

    @Mock
    FragmentLauncher fragmentLauncherMock;

    THSShippingAddressFragmentMock thsShippingAddressFragment;

    @Mock
    List<State> stateListMock;

    @Mock
    List<Country> countryListMock;

    @Mock
    Context applicationContextMock;

    @Mock
    Country countryMock;

    @Mock
    AppInfraInterface appInfraInterface;

    @Mock
    AppTaggingInterface appTaggingInterface;

    @Mock
    LoggingInterface loggingInterface;

    @Mock
    ServiceDiscoveryInterface serviceDiscoveryMock;

    @Mock
    THSShippingAddressPresenter thsShippingAddressPresenterMock;

    @Mock
    THSConsumer thsConsumerMock;

    @Mock
    UIPicker UIPickerMock;

    @Before
    public void setUp() throws  Exception{
        MockitoAnnotations.initMocks(this);
        ShadowLog.stream = System.out;
        THSManager.getInstance().setAwsdk(awsdkMock);

        THSManager.getInstance().setThsConsumer(thsConsumerMock);
        THSManager.getInstance().setThsParentConsumer(thsConsumerMock);
        when(thsConsumerMock.getConsumer()).thenReturn(consumerMock);

        THSManager.getInstance().setConsumer(thsConsumerWrapper);
        THSManager.getInstance().setVisitContext(pthVisitContext);

        when(awsdkMock.getConsumerManager()).thenReturn(consumerManagerMock);

        thsShippingAddressFragment = new THSShippingAddressFragmentMock();
        when(thsShippingAddressFragmentMock.getActivity()).thenReturn(activityMock);
        when(thsShippingAddressFragmentMock.getActivity().getApplicationContext()).thenReturn(applicationContextMock);
        when(thsShippingAddressFragmentMock.getSupportedCountries()).thenReturn(countryListMock);
        when(awsdkMock.getConsumerManager().getValidShippingStates(countryMock)).thenReturn(stateListMock);
        when(thsShippingAddressFragmentMock.getValidShippingStates(countryListMock)).thenReturn(stateListMock);

        when(appInfraInterface.getTagging()).thenReturn(appTaggingInterface);
        when(appInfraInterface.getTagging().createInstanceForComponent(THS_APPLICATION_ID, BuildConfig.VERSION_NAME)).thenReturn(appTaggingInterface);
        when(appInfraInterface.getLogging()).thenReturn(loggingInterface);
        when(appInfraInterface.getLogging().createInstanceForComponent(THS_APPLICATION_ID, BuildConfig.VERSION_NAME)).thenReturn(loggingInterface);
        when(appInfraInterface.getServiceDiscovery()).thenReturn(serviceDiscoveryMock);
        THSManager.getInstance().setAppInfra(appInfraInterface);

        thsShippingAddressFragment.setConsumerAndAddress(thsConsumerWrapper,address);
        thsShippingAddressFragment.setFragmentLauncher(fragmentLauncherMock);

        when(countryListMock.size()).thenReturn(1);
        when(stateListMock.size()).thenReturn(1);
        when(countryListMock.get(0)).thenReturn(countryMock);
        thsShippingAddressFragment.supportedCountries = countryListMock;
        when(awsdkMock.getSupportedCountries()).thenReturn(countryListMock);
        when(consumerManagerMock.getValidShippingStates(countryMock)).thenReturn(stateListMock);
        //when(thsShippingAddressFragment.getUiPicker()).thenReturn(UIPickerMock);
       // when(thsShippingAddressFragmentMock.getUiPicker()).thenReturn(UIPickerMock);
    }

    @Test
    public void validateStringAddressNull(){
        SupportFragmentTestUtil.startFragment(thsShippingAddressFragment);
        boolean value = thsShippingAddressFragment.validateString(null);
        assertEquals(false,value);
    }

    @Test
    public void validateStringAdressValid(){
        SupportFragmentTestUtil.startFragment(thsShippingAddressFragment);
        boolean value = thsShippingAddressFragment.validateString("Testing");
        assertEquals(true,value);
    }
    @Test
    public void validateStringAdressLengthValid(){
        SupportFragmentTestUtil.startFragment(thsShippingAddressFragment);
        boolean value = thsShippingAddressFragment.validateString("testingngngngngngngngngngngngng");
        assertEquals(false,value);
    }

    @Test
    public void testUpdateContinueButton(){
        SupportFragmentTestUtil.startFragment(thsShippingAddressFragment);
        thsShippingAddressFragment.updateContinueBtnState();
        assertEquals(false,thsShippingAddressFragment.updateAddressButton.isEnabled());
    }

    @Test
    public void testOnClick(){
        when(awsdkMock.getNewAddress()).thenReturn(addressMock);
        SupportFragmentTestUtil.startFragment(thsShippingAddressFragment);
        thsShippingAddressFragment.thsShippingAddressPresenter = thsShippingAddressPresenterMock;
        final View viewById = thsShippingAddressFragment.getView().findViewById(R.id.update_shipping_address);
        viewById.performClick();
        verify(thsShippingAddressPresenterMock).updateShippingAddress(any(Address.class));

    }
}
