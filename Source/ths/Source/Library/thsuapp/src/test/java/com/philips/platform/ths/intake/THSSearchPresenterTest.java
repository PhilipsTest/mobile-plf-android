/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.platform.ths.intake;

import android.support.v4.app.FragmentActivity;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.entity.Language;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.State;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.practice.OnDemandSpecialty;
import com.americanwell.sdk.entity.practice.Practice;
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.provider.ProviderType;
import com.americanwell.sdk.manager.ConsumerManager;
import com.americanwell.sdk.manager.PracticeProvidersManager;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.ths.BuildConfig;
import com.philips.platform.ths.providerslist.THSProviderInfo;
import com.philips.platform.ths.registration.dependantregistration.THSConsumer;
import com.philips.platform.ths.utility.THSManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.philips.platform.ths.utility.THSConstants.THS_APPLICATION_ID;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class THSSearchPresenterTest {

    private THSSearchPresenter mTHSSearchPresenter;

    @Mock
    private THSSearchFragment thsSearchFragment;

    @Mock
    AWSDK awsdkMock;

    @Mock
    Provider providerMock;

    @Mock
    private Practice practiceMock;

    @Mock
    private Map mapMock;

    @Mock
    Medication medicationMock;

    @Mock
    private THSMedication thsMedicationMock;

    @Mock
    private Throwable throwableMock;

    @Mock
    private SDKError sdkErrorMock;

    @Mock
    private Consumer thsConsumerWrapperMock;

    @Mock
    Consumer consumerMock;

    @Mock
    private FragmentActivity fragmentActivityMock;

    @Mock
    private PracticeProvidersManager practiceProvidersManager;

    @Mock
    ConsumerManager consumerManagerMock;

    @Mock
    private THSProviderInfo thsProviderInfoMock;

    @Mock
    ProviderInfo providerInfoMock;

    @Mock
    private Pharmacy pharmacyMock;

    @Mock
    private THSConsumer thsConsumer;

    @Mock
    AppInfraInterface appInfraInterface;

    @Mock
    AppTaggingInterface appTaggingInterface;

    @Mock
    LoggingInterface loggingInterface;


    @Mock
    ServiceDiscoveryInterface serviceDiscoveryMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(thsSearchFragment.getFragmentActivity()).thenReturn(fragmentActivityMock);
        THSManager.getInstance().setConsumer(thsConsumerWrapperMock);
        THSManager.getInstance().setAwsdk(awsdkMock);

        when(appInfraInterface.getTagging()).thenReturn(appTaggingInterface);
        when(appInfraInterface.getTagging().createInstanceForComponent(THS_APPLICATION_ID, BuildConfig.VERSION_NAME)).thenReturn(appTaggingInterface);
        when(appInfraInterface.getLogging()).thenReturn(loggingInterface);
        when(appInfraInterface.getLogging().createInstanceForComponent(THS_APPLICATION_ID, BuildConfig.VERSION_NAME)).thenReturn(loggingInterface);
        when(appInfraInterface.getServiceDiscovery()).thenReturn(serviceDiscoveryMock);
        THSManager.getInstance().setAppInfra(appInfraInterface);


        THSManager.getInstance().setThsConsumer(thsConsumer);
        when(thsConsumer.getConsumer()).thenReturn(consumerMock);
        THSManager.getInstance().setThsParentConsumer(thsConsumer);
        when(awsdkMock.getConsumerManager()).thenReturn(consumerManagerMock);
        when(awsdkMock.getPracticeProvidersManager()).thenReturn(practiceProvidersManager);
        mTHSSearchPresenter = new THSSearchPresenter(thsSearchFragment);
        when(thsSearchFragment.isFragmentAttached()).thenReturn(true);
    }

    @Test
    public void onValidationFailure() throws Exception {
        mTHSSearchPresenter.onValidationFailure(mapMock);
    }

    @Test
    public void onResponse() throws Exception {
        mTHSSearchPresenter.onResponse(thsMedicationMock, sdkErrorMock);
    }

    @Test
    public void onFailure() throws Exception {
        when(thsSearchFragment.isFragmentAttached()).thenReturn(false);
        mTHSSearchPresenter.onFailure(throwableMock);
        //        verifyNoMoreInteractions(thsSearchFragment);
    }

    @Test
    public void onEvent() throws Exception {
        mTHSSearchPresenter.onEvent(0);
    }

    @Test
    public void searchProviders() throws Exception {
        mTHSSearchPresenter.searchProviders("abc", practiceMock);
        verify(practiceProvidersManager).findProviders(any(Consumer.class), any(Practice.class), (OnDemandSpecialty)isNull(), anyString(), (Set<String>)isNull(), (Set<ProviderType>)isNull(), (State)isNull(), (Language)isNull(), (Integer)isNull(), any(SDKCallback.class));
        // verify(practiceProvidersManager).findFutureAvailableProviders(any(Consumer.class),any(PracticeInfo.class),anyString(),any(Language.class),any(Date.class),anyInt(),anyInt(),any(SDKCallback.class));
    }

    @Test
    public void searchMedication() throws Exception {
        mTHSSearchPresenter.searchMedication("abc");
        verify(consumerManagerMock).searchMedications(any(Consumer.class), anyString(), any(SDKValidatedCallback.class));
    }

    @Test
    public void searchPharmacy() throws Exception {
        mTHSSearchPresenter.searchPharmacy("abs");
        verify(thsSearchFragment).getFragmentActivity();
    }

    @Test(expected = NullPointerException.class)
    public void onProvidersListReceived() throws Exception {
        List list = new ArrayList();
        list.add(thsProviderInfoMock);
        mTHSSearchPresenter.onProvidersListReceived(list, sdkErrorMock);
    }

    @Test
    public void onProvidersListFetchError() throws Exception {
        when(thsSearchFragment.isFragmentAttached()).thenReturn(false);
        mTHSSearchPresenter.onProvidersListFetchError(throwableMock);
        //        verifyNoMoreInteractions(thsSearchFragment);
    }

    @Test
    public void onPharmacyListReceived() throws Exception {
        List list = new ArrayList();
        list.add(pharmacyMock);
        when(thsSearchFragment.isFragmentAttached()).thenReturn(true);
        mTHSSearchPresenter.onPharmacyListReceived(list, sdkErrorMock);
    }

}
