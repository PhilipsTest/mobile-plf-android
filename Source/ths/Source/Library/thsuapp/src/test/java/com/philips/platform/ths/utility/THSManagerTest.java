/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.platform.ths.utility;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.AWSDKFactory;
import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.Language;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.SDKLocalDate;
import com.americanwell.sdk.entity.SDKPasswordError;
import com.americanwell.sdk.entity.State;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.consumer.ConsumerUpdate;
import com.americanwell.sdk.entity.consumer.DocumentRecord;
import com.americanwell.sdk.entity.consumer.Gender;
import com.americanwell.sdk.entity.enrollment.ConsumerEnrollment;
import com.americanwell.sdk.entity.enrollment.DependentEnrollment;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.entity.insurance.Subscription;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.practice.OnDemandSpecialty;
import com.americanwell.sdk.entity.practice.Practice;
import com.americanwell.sdk.entity.practice.PracticeInfo;
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.visit.Appointment;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.entity.visit.Vitals;
import com.americanwell.sdk.exception.AWSDKInitializationException;
import com.americanwell.sdk.exception.AWSDKInstantiationException;
import com.americanwell.sdk.manager.ConsumerManager;
import com.americanwell.sdk.manager.PracticeProvidersManager;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.americanwell.sdk.manager.VisitManager;
import com.philips.cdp.registration.User;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.ths.BuildConfig;
import com.philips.platform.ths.intake.THSConditionsCallBack;
import com.philips.platform.ths.intake.THSConditionsList;
import com.philips.platform.ths.intake.THSMedication;
import com.philips.platform.ths.intake.THSMedicationCallback;
import com.philips.platform.ths.intake.THSSDKValidatedCallback;
import com.philips.platform.ths.intake.THSUpdateConsumerCallback;
import com.philips.platform.ths.intake.THSUpdateVitalsCallBack;
import com.philips.platform.ths.intake.THSVisitContextCallBack;
import com.philips.platform.ths.intake.THSVitalSDKCallback;
import com.philips.platform.ths.intake.THSVitals;
import com.philips.platform.ths.login.THSAuthentication;
import com.philips.platform.ths.login.THSGetConsumerObjectCallBack;
import com.philips.platform.ths.login.THSLoginCallBack;
import com.philips.platform.ths.pharmacy.THSConsumerShippingAddressCallback;
import com.philips.platform.ths.pharmacy.THSPreferredPharmacyCallback;
import com.philips.platform.ths.pharmacy.THSUpdatePharmacyCallback;
import com.philips.platform.ths.practice.THSPracticesListCallback;
import com.philips.platform.ths.providerdetails.THSProviderDetailsCallback;
import com.philips.platform.ths.providerslist.THSOnDemandSpeciality;
import com.philips.platform.ths.providerslist.THSOnDemandSpecialtyCallback;
import com.philips.platform.ths.providerslist.THSProviderInfo;
import com.philips.platform.ths.providerslist.THSProvidersListCallback;
import com.philips.platform.ths.registration.THSCheckConsumerExistsCallback;
import com.philips.platform.ths.registration.THSRegistrationDetailCallback;
import com.philips.platform.ths.registration.dependantregistration.THSConsumer;
import com.philips.platform.ths.sdkerrors.THSSDKError;
import com.philips.platform.ths.settings.THSGetAppointmentsCallback;
import com.philips.platform.ths.uappclasses.THSCompletionProtocol;
import com.philips.platform.ths.welcome.THSInitializeCallBack;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.philips.platform.ths.utility.THSConstants.THS_APPLICATION_ID;
import static junit.framework.Assert.assertFalse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class
THSManagerTest {
    private THSManager thsManager;

    @Mock
    private THSMedication mTHSMedication;

    @Mock
    private SDKPasswordError sdkPasswordErrorMock;

    @Mock
    private DocumentRecord documentRecordMock;

    @Mock
    private Consumer mConsumerMock;

    @Mock
    private Medication medicationMock;

    private Practice practice;

    private THSProviderInfo providerInfo;

    @Mock
    private THSCompletionProtocol thsCompletionProtocolMock;

    @Mock
    private THSProviderInfo THSProviderInfo;

    @Mock
    private VisitContext visitContextMock;

    @Mock
    private THSConditionsCallBack thsConditionsCallback;

    @Mock
    private THSVitalSDKCallback thsVitalCallBackMock;

    @Mock
    private VisitManager visitManagerMock;

    @Mock
    private PracticeProvidersManager practiseprovidermanagerMock;

    @Mock
    private Context contextMock;

    @Mock
    private THSConsumer thsConsumerMock;

    @Mock
    private Consumer consumerMock;

    @Mock
    private ProviderInfo providerInfoMock;

    @Mock
    private Consumer THSConsumerWrapperMock;

    @Mock
    private AWSDK awsdkMock;

    @Mock
    AWSDKFactory awsdkFactoryMock;

    @Mock
    private THSLoginCallBack<THSAuthentication, THSSDKError> thsLoginCallBack;

    @Mock
    THSInitializeCallBack THSInitializeCallBack;

    @Mock
    SDKCallback sdkCallbackMock;

    @Mock
    private Authentication authentication;

    @Mock
    private SDKError sdkError;

    @Mock
    private Throwable throwable;

    @Captor
    private ArgumentCaptor<SDKCallback<Authentication, SDKError>> requestCaptor;

    @Captor
    private ArgumentCaptor<SDKCallback<Boolean, SDKError>> consumerExisitsCaptor;

    @Captor
    private ArgumentCaptor<SDKCallback> initializationCallback;

    @Captor
    private ArgumentCaptor<SDKCallback<VisitContext,SDKError>> visitContextCaptor;

    @Captor
    private ArgumentCaptor<SDKCallback<Authentication,SDKError>> auth_captor;

    @Captor
    private ArgumentCaptor<SDKCallback<Consumer, SDKPasswordError>> sdkPasswordErrorArgumentCaptor;

    @Captor
    private ArgumentCaptor<SDKCallback<Consumer, SDKError>> getConsumerCaptor;

    @Captor
    private ArgumentCaptor<SDKCallback> vitalsCaptor;

    @Captor
    private ArgumentCaptor<SDKCallback> conditionsCaptor;

    @Captor
    private ArgumentCaptor<SDKCallback> pthRegistrationDetailCallbackArgumentCaptor;

    @Mock
    private THSAuthentication thsAuthentication;

    @Mock
    THSRegistrationDetailCallback THSRegistrationDetailCallbackMock;

    @Mock
    private THSVisitContextCallBack<VisitContext, THSSDKError> thsvisitcontextcallback;

    @Mock
    private ConsumerManager consumerManagerMock;

    @Mock
    VisitContext THSVisitContextMock;

    @Mock
    ConsumerUpdate consumerUpdateMock;

    @Captor
    private ArgumentCaptor<SDKCallback<List<Appointment>, SDKError>> getAppointmentCaptor;

    @Mock
    private THSPracticesListCallback pTHPracticesListCallback;

    @Captor
    private ArgumentCaptor<SDKCallback<List<Practice>, SDKError>> requestPracticeCaptor;

    @Captor
    private ArgumentCaptor<SDKCallback<Vitals, SDKError>> getVitalsCaptor;

    @Mock
    private Vitals vitalsMock;

    @Mock
    private THSProvidersListCallback THSProvidersListCallback;

    @Mock
    THSProvidersListCallback thsProvidersListCallbackMock;

    @Captor
    private ArgumentCaptor<SDKCallback<List<ProviderInfo>, SDKError>> providerListCaptor;

    @Mock
    private THSProviderDetailsCallback THSProviderDetailsCallback;

    @Captor
    private ArgumentCaptor<SDKCallback<Provider, SDKError>> providerDetailsCaptor;

    @Mock
    private THSMedicationCallback.PTHGetMedicationCallback pTHGetMedicationCallbackMock;
    @Captor
    private ArgumentCaptor<SDKCallback<List<Medication>, SDKError>> getMedicationCaptor;

    @Mock
    private THSSDKValidatedCallback pTHSDKValidatedCallback;
    @Captor
    private ArgumentCaptor<SDKValidatedCallback<List<Medication>, SDKError>> getSearchedMedicationCaptor;

    @Mock
    private THSMedicationCallback.PTHUpdateMedicationCallback pTHUpdateMedicationCallback;
    @Captor
    private ArgumentCaptor<SDKCallback<Void, SDKError>> getUpdateMedicationCaptor;

    @Mock
    THSUpdateConsumerCallback pTHUpdateConsumerCallback;

    @Mock
    private OnDemandSpecialty onDemandSpecialtyMock;

    @Mock
    private THSGetAppointmentsCallback thsGetAppointmentsCallback;

    @Mock
    private Appointment appointmentMock;

    @Captor
    private ArgumentCaptor<SDKValidatedCallback<Consumer, SDKPasswordError>> getUpdateConsumerCaptor;

    @Captor
    private ArgumentCaptor<SDKCallback<VisitContext, SDKError>> getVisitContextCaptor;

    @Captor
    private ArgumentCaptor<SDKValidatedCallback<Consumer, SDKError>> getDependentenrollment;

    @Mock
    private AppInfraInterface appInfraInterface;

    @Mock
    private AppTaggingInterface appTaggingInterface;

    @Mock
    private AppConfigurationInterface appConfigurationInterfaceMock;

    @Mock
    private THSGetConsumerObjectCallBack thsGetConsumerObjectCallBackMock;

    @Mock
    private LoggingInterface loggingInterface;

    @Mock
    private ServiceDiscoveryInterface serviceDiscoveryMock;

    @Mock
    private Date dateMock;

    @Mock
    private User userMock;

    @Mock
    private THSCheckConsumerExistsCallback thsCheckConsumerExistsCallbackMock;

    @Mock
    private State stateMock;

    @Mock
    VisitContext thsVisitContextMock;

    @Mock
    private THSOnDemandSpecialtyCallback thsOnDemandSpecialtyCallbackMock;

    @Mock
    private THSOnDemandSpeciality thsOnDemandSpeciality;

    @Mock
    private SDKLocalDate sdkLocalDateMock;

    @Captor
    private ArgumentCaptor<SDKCallback<List<OnDemandSpecialty>, SDKError>> getOndemandSpecialityCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        thsManager = com.philips.platform.ths.utility.THSManager.getInstance();

        THSManager.getInstance().TEST_FLAG = true;
        when(userMock.getHsdpUUID()).thenReturn("abc");
        thsManager.setUser(userMock);
        when(userMock.getGivenName()).thenReturn("ss");
        when(userMock.getEmail()).thenReturn("sss");
        when(userMock.getDateOfBirth()).thenReturn(dateMock);
        when(userMock.getGender()).thenReturn(com.philips.cdp.registration.ui.utils.Gender.FEMALE);
        when(userMock.getHsdpAccessToken()).thenReturn("zz");
        when(userMock.getHsdpUUID()).thenReturn("abc");
        when(userMock.getFamilyName()).thenReturn("sss");

        when(appInfraInterface.getTagging()).thenReturn(appTaggingInterface);
        when(appInfraInterface.getTagging().createInstanceForComponent(THS_APPLICATION_ID, BuildConfig.VERSION_NAME)).thenReturn(appTaggingInterface);
        when(appInfraInterface.getConfigInterface()).thenReturn(appConfigurationInterfaceMock);
        when(appInfraInterface.getLogging()).thenReturn(loggingInterface);
        when(appInfraInterface.getLogging().createInstanceForComponent(THS_APPLICATION_ID, BuildConfig.VERSION_NAME)).thenReturn(loggingInterface);
        when(appInfraInterface.getServiceDiscovery()).thenReturn(serviceDiscoveryMock);
        THSManager.getInstance().setAppInfra(appInfraInterface);

        when(thsAuthentication.getAuthentication()).thenReturn(authentication);
        when(awsdkMock.getConsumerManager()).thenReturn(consumerManagerMock);
        when(thsOnDemandSpeciality.getOnDemandSpecialty()).thenReturn(onDemandSpecialtyMock);
        List list = new ArrayList();
        list.add(medicationMock);
        when(mTHSMedication.getMedicationList()).thenReturn(list);

        when(awsdkMock.getPracticeProvidersManager()).thenReturn(practiseprovidermanagerMock);
        when(awsdkMock.getVisitManager()).thenReturn(visitManagerMock);

        thsManager.setAwsdk(awsdkMock);
        thsManager.setThsConsumer(thsConsumerMock);
        when(thsConsumerMock.getConsumer()).thenReturn(consumerMock);
        when(thsConsumerMock.getHsdpUUID()).thenReturn("123");
        practice = getPractice();
        providerInfo = THSProviderInfo;
        thsManager.setConsumer(consumerMock);
    }

    @Test
    public void getInstance() {
        THSManager THSManager = com.philips.platform.ths.utility.THSManager.getInstance();
        assertThat(THSManager).isNotNull();
        assertThat(THSManager).isInstanceOf(THSManager.class);
    }

    @Test
    public void getAwsdk() throws AWSDKInstantiationException {
        AWSDK awsdk = thsManager.getAwsdk(contextMock);
        assertThat(awsdk).isNotNull();
        assertThat(awsdk).isInstanceOf(AWSDK.class);
    }

    @Test
    public void authenticateOnResponse() throws AWSDKInstantiationException {
        thsManager.authenticate(contextMock, "username", "password", "variable", thsLoginCallBack);
        verify(awsdkMock).authenticate(eq("username"), eq("password"), eq("variable"), requestCaptor.capture());
        SDKCallback<Authentication, SDKError> value = requestCaptor.getValue();
        value.onResponse(authentication, sdkError);

        verify(thsLoginCallBack).onLoginResponse(any(THSAuthentication.class), any(THSSDKError.class));
    }

    @Test
    public void authenticateOnFailure() throws AWSDKInstantiationException {
        thsManager.authenticate(contextMock, "username", "password", "variable", thsLoginCallBack);
        verify(awsdkMock).authenticate(eq("username"), eq("password"), eq("variable"), requestCaptor.capture());
        SDKCallback<Authentication, SDKError> value = requestCaptor.getValue();
        value.onFailure(throwable);

        verify(thsLoginCallBack).onLoginFailure(throwable);
    }

    /*@Test
    public void initializeTeleHealthOnFailure() throws MalformedURLException, AWSDKInstantiationException, AWSDKInitializationException, URISyntaxException {
        thsManager.initializeTeleHealth(contextMock, THSInitializeCallBack);

        verify(awsdkMock).initialize(any(Map.class), initializationCallback.capture());
        SDKCallback value = initializationCallback.getValue();
        value.onFailure(throwable);

        verify(THSInitializeCallBack).onInitializationFailure(throwable);
    }

    @Test
    public void initializeTeleHealthOnResponse() throws MalformedURLException, AWSDKInstantiationException, AWSDKInitializationException, URISyntaxException {
        thsManager.initializeTeleHealth(contextMock, THSInitializeCallBack);

        verify(awsdkMock).initialize(any(Map.class), initializationCallback.capture());
        SDKCallback value = initializationCallback.getValue();
        value.onResponse(any(Object.class), any(SDKError.class));

        verify(THSInitializeCallBack).onInitializationResponse(any(Object.class), any(THSSDKError.class));
    }*/

    @Test
    public void getVisitContext() throws MalformedURLException, AWSDKInstantiationException, AWSDKInitializationException, URISyntaxException {
        when(THSProviderInfo.getProviderInfo()).thenReturn(providerInfoMock);
        when(awsdkMock.getVisitManager()).thenReturn(visitManagerMock);
        thsManager.getVisitContext(contextMock, THSProviderInfo, thsvisitcontextcallback);

        verify(visitManagerMock).getVisitContext(any(Consumer.class), any(ProviderInfo.class), visitContextCaptor.capture());
        SDKCallback value = visitContextCaptor.getValue();
        value.onResponse(visitContextMock, sdkError);

        verify(thsvisitcontextcallback).onResponse(any(VisitContext.class), any(THSSDKError.class));
    }

    @Test
    public void getVisitContextOnFailure() throws MalformedURLException, AWSDKInstantiationException, AWSDKInitializationException, URISyntaxException {
        when(THSProviderInfo.getProviderInfo()).thenReturn(providerInfoMock);
        when(awsdkMock.getVisitManager()).thenReturn(visitManagerMock);
        thsManager.getVisitContext(contextMock, THSProviderInfo, thsvisitcontextcallback);

        verify(visitManagerMock).getVisitContext(any(Consumer.class), any(ProviderInfo.class), visitContextCaptor.capture());
        SDKCallback value = visitContextCaptor.getValue();
        value.onFailure(any(Throwable.class));

        verify(thsvisitcontextcallback).onFailure((Throwable)isNull());
    }

/*    @Test
    public void getVitals() throws AWSDKInstantiationException {
        thsManager.setPTHConsumer(THSConsumerWrapperMock);
        when(THSVisitContextMock.getVisitContext()).thenReturn(visitContextMock);
        when(THSConsumerWrapperMock.getConsumer()).thenReturn(consumerMock);
        when(awsdkMock.getConsumerManager()).thenReturn(consumerManagerMock);
        thsManager.getVitals(contextMock, thsVitalCallBackMock);

        verify(consumerManagerMock).getVitals(any(Consumer.class),any(VisitContext.class), vitalsCaptor.capture());
        SDKCallback value = vitalsCaptor.getValue();
        value.onResponse(any(Vitals.class), any(SDKError.class));

        verify(thsVitalCallBackMock).onResponse(any(THSVitals.class), any(THSSDKError.class));
    }*/

 /*   @Test
    public void getVitalsOnFailure() throws AWSDKInstantiationException {
        thsManager.setPTHConsumer(THSConsumerWrapperMock);
        when(THSVisitContextMock.getVisitContext()).thenReturn(visitContextMock);
        when(THSConsumerWrapperMock.getConsumer()).thenReturn(consumerMock);
        when(awsdkMock.getConsumerManager()).thenReturn(consumerManagerMock);
        thsManager.getVitals(contextMock, thsVitalCallBackMock);

        verify(consumerManagerMock).getVitals(any(Consumer.class),any(VisitContext.class), vitalsCaptor.capture());
        SDKCallback value = vitalsCaptor.getValue();
        value.onFailure(any(Throwable.class));

        verify(thsVitalCallBackMock).onFailure(any(Throwable.class));
    }*/

    @Test
    public void getConditions() throws AWSDKInstantiationException {
        thsManager.setConsumer(THSConsumerWrapperMock);

        when(awsdkMock.getConsumerManager()).thenReturn(consumerManagerMock);

        thsManager.getConditions(contextMock, thsConditionsCallback);

        verify(consumerManagerMock).getConditions(any(Consumer.class), conditionsCaptor.capture());
        SDKCallback value = conditionsCaptor.getValue();
        value.onResponse(any(List.class), any(SDKError.class));

        verify(thsConditionsCallback).onResponse(any(THSConditionsList.class), any(THSSDKError.class));
    }

    @Test
    public void getOnFailure() throws AWSDKInstantiationException {
        thsManager.setConsumer(THSConsumerWrapperMock);

        when(awsdkMock.getConsumerManager()).thenReturn(consumerManagerMock);

        thsManager.getConditions(contextMock, thsConditionsCallback);

        verify(consumerManagerMock).getConditions(any(Consumer.class), conditionsCaptor.capture());
        SDKCallback value = conditionsCaptor.getValue();
        value.onFailure(any(Throwable.class));

        verify(thsConditionsCallback).onFailure((Throwable)isNull());
    }

    @Test
    public void getPracticesOnResponse() throws AWSDKInstantiationException {
        when(awsdkMock.getPracticeProvidersManager()).thenReturn(practiseprovidermanagerMock);
        thsManager.getPractices(contextMock, pTHPracticesListCallback);
        verify(practiseprovidermanagerMock).getPractices(any(Consumer.class), requestPracticeCaptor.capture());
        SDKCallback value = requestPracticeCaptor.getValue();
        value.onResponse(any(List.class), any(SDKError.class));
        value.onFailure(any(Throwable.class));
    }

    @Test
    public void getProviderListTest() throws AWSDKInstantiationException {
        when(awsdkMock.getPracticeProvidersManager()).thenReturn(practiseprovidermanagerMock);
        thsManager.getProviderList(contextMock, practice, THSProvidersListCallback);
        verify(practiseprovidermanagerMock).findProviders(any(Consumer.class), any(Practice.class), (OnDemandSpecialty)isNull(), (String)isNull(), (Set)isNull(), (Set)isNull(), (State)isNull(), (Language)isNull(), (Integer)isNull(), providerListCaptor.capture());

        SDKCallback value = providerListCaptor.getValue();
        //        value.onGetPaymentSuccess(any(List.class), any(SDKError.class));
        value.onFailure(any(Throwable.class));
    }

    @Test
    public void getProviderListTestSuccess() throws AWSDKInstantiationException {
        when(awsdkMock.getPracticeProvidersManager()).thenReturn(practiseprovidermanagerMock);
        thsManager.getProviderList(contextMock, practice, THSProvidersListCallback);
        verify(practiseprovidermanagerMock).findProviders(any(Consumer.class), any(Practice.class), (OnDemandSpecialty)isNull(), (String)isNull(), (Set)isNull(), (Set)isNull(), (State)isNull(), (Language)isNull(), (Integer)isNull(), providerListCaptor.capture());
        SDKCallback value = providerListCaptor.getValue();
        //        value.onGetPaymentSuccess(any(List.class), any(SDKError.class));
        ArrayList list = new ArrayList();
        list.add(providerInfoMock);

        value.onResponse(list, sdkError);
        verify(THSProvidersListCallback).onProvidersListReceived(ArgumentMatchers.<THSProviderInfo>anyList(), any(SDKError.class));
    }

    @Test
    public void getProviderDetailsTest() throws AWSDKInstantiationException {
        when(awsdkMock.getPracticeProvidersManager()).thenReturn(practiseprovidermanagerMock);
        when(THSProviderInfo.getProviderInfo()).thenReturn(providerInfoMock);
        thsManager.getProviderDetails(contextMock, THSProviderInfo, THSProviderDetailsCallback);
        verify(practiseprovidermanagerMock).getProvider(any(ProviderInfo.class), any(Consumer.class), providerDetailsCaptor.capture());
        SDKCallback value = providerDetailsCaptor.getValue();
        value.onResponse(any(Provider.class), any(SDKError.class));
        value.onFailure(any(Throwable.class));
    }

    @Test
    public void getConsumerTest() {
        thsManager.setConsumer(consumerMock);
        Consumer consumer = thsManager.getConsumer(contextMock);
        assertThat(consumer).isNotNull();
        assertThat(consumer).isInstanceOf(Consumer.class);
    }

    public void getMedicationTest() throws AWSDKInstantiationException {
        when(awsdkMock.getConsumerManager()).thenReturn(consumerManagerMock);
        thsManager.getMedication(contextMock, pTHGetMedicationCallbackMock);
        verify(consumerManagerMock).getMedications(any(Consumer.class), getMedicationCaptor.capture());
        SDKCallback value = getMedicationCaptor.getValue();
        value.onResponse(any(List.class), any(SDKError.class));
        value.onFailure(any(Throwable.class));
    }

    @Test
    public void searchMedicationTest() throws AWSDKInstantiationException {
        when(awsdkMock.getConsumerManager()).thenReturn(consumerManagerMock);
        thsManager.searchMedication(contextMock, "dol", pTHSDKValidatedCallback);
        verify(consumerManagerMock).searchMedications(any(Consumer.class), any(String.class), getSearchedMedicationCaptor.capture());
        SDKCallback value = getSearchedMedicationCaptor.getValue();
        //        value.onGetPaymentSuccess(any(List.class), any(SDKError.class));
        //value.onGetPaymentFailure(any(Throwable.class));  //todo
    }

    @Test
    public void updateMedicationTest() throws AWSDKInstantiationException {
        when(awsdkMock.getConsumerManager()).thenReturn(consumerManagerMock);
        thsManager.updateMedication(contextMock, mTHSMedication, pTHUpdateMedicationCallback);
        verify(consumerManagerMock).updateMedications(any(Consumer.class), any(List.class), getUpdateMedicationCaptor.capture());
        SDKCallback value = getUpdateMedicationCaptor.getValue();
        value.onResponse(any(List.class), any(SDKPasswordError.class));
        //        value.onGetPaymentFailure(any(Throwable.class));
    }

    @Test
    public void updateMedicationTestFailure() throws AWSDKInstantiationException {
        when(awsdkMock.getConsumerManager()).thenReturn(consumerManagerMock);
        thsManager.updateMedication(contextMock, mTHSMedication, pTHUpdateMedicationCallback);
        verify(consumerManagerMock).updateMedications(any(Consumer.class), any(List.class), getUpdateMedicationCaptor.capture());
        SDKCallback value = getUpdateMedicationCaptor.getValue();
        value.onFailure(throwable);
        //        verify(pTHUpdateConsumerCallback).onUpdateConsumerFailure(any(Throwable.class));
    }

    // follow up test
  /*  @Test
    public void updateConsumerTest()throws AWSDKInstantiationException {
        when(awsdkMock.getConsumerManager()).thenReturn(consumerManagerMock);
        when(consumerManagerMock.getNewConsumerUpdate(any(Consumer.class))).thenReturn(consumerUpdateMock);
        thsManager.updateConsumer(contextMock, "67767262" ,pTHUpdateConsumerCallback);
        verify(consumerManagerMock).updateConsumer(any(ConsumerUpdate.class),getUpdateConsumerCaptor.capture());
        SDKCallback value = getUpdateConsumerCaptor.getValue();
        value.onGetPaymentSuccess(any(Consumer.class), any(SDKError.class));
        value.onGetPaymentFailure(any(Throwable.class));
    }*/

    Consumer getConsumer() {
        Consumer consumer = new Consumer() {
            @Override
            public String getGender() {
                return Gender.MALE;
            }

            @Override
            public String getAge() {
                return "34";
            }

            @Override
            public String getFormularyRestriction() {
                return null;
            }

            @Override
            public boolean isEligibleForVisit() {
                return false;
            }

            @Override
            public boolean isEnrolled() {
                return true;
            }

            @Override
            public Subscription getSubscription() {
                return null;
            }

            @Override
            public String getPhone() {
                return "7899673388";
            }

            @Override
            public List<Consumer> getDependents() {
                return null;
            }

            @Override
            public boolean isDependent() {
                return false;
            }

            @Override
            public boolean isAppointmentReminderTextsEnabled() {
                return false;
            }

            @Override
            public String getSourceId() {
                return null;
            }

            @Override
            public SDKLocalDate getDob() {
                return null;
            }

            @Override
            public Address getAddress() {
                return null;
            }

            @Override
            public String getConsumerType() {
                return null;
            }

            @Override
            public String getEmail() {
                return null;
            }

            @Override
            public State getLegalResidence() {
                return null;
            }

            @NonNull
            @Override
            public String getFirstName() {
                return "Ravi";
            }

            @Nullable
            @Override
            public String getMiddleInitial() {
                return null;
            }

            @Nullable
            @Override
            public String getMiddleName() {
                return "xyz";
            }

            @NonNull
            @Override
            public String getLastName() {
                return "Kumar";
            }

            @NonNull
            @Override
            public String getFullName() {
                return "Ravi Kumar";
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {

            }
        };
        return consumer;
    }

    public Practice getPractice() {

        Practice practiceObj = new Practice() {
            @Override
            public Address getAddress() {
                return null;
            }

            @Override
            public String getPhone() {
                return null;
            }

            @Override
            public String getFax() {
                return null;
            }

            @Override
            public String getHours() {
                return null;
            }

            @Override
            public String getWelcomeMessage() {
                return null;
            }

            @Override
            public boolean isShowScheduling() {
                return false;
            }

            @Override
            public boolean isShowAvailableNow() {
                return false;
            }

            @Override
            public boolean isPaymentRequiredForScheduledVisits() {
                return false;
            }

            @Nullable
            @Override
            public String getPaymentRequiredForScheduledVisitsText() {
                return null;
            }

            @Override
            public int getRank() {
                return 0;
            }

            @Override
            public String getPracticeType() {
                return "Practice Test";
            }

            @Override
            public boolean hasLogo() {
                return true;
            }

            @Override
            public boolean hasSmallLogo() {
                return true;
            }

            @Override
            public String getImageUrl(boolean b) {
                return "http://";
            }

            @Override
            public String getName() {
                return "TS Patient Test";
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {

            }
        };

        return practiceObj;
    }

    @Test
    public void getTHSVisitCompletionListener() {
        thsManager.setThsCompletionProtocol(thsCompletionProtocolMock);
        final THSCompletionProtocol thsCompletionProtocol = thsManager.getThsCompletionProtocol();
        assertNotNull(thsCompletionProtocol);
        assertThat(thsCompletionProtocol).isInstanceOf(THSCompletionProtocol.class);
    }

    @Test
    public void authenticateMutualAuth() throws MalformedURLException, AWSDKInstantiationException, AWSDKInitializationException, URISyntaxException {
        when(thsConsumerMock.getHsdpUUID()).thenReturn("1234");
        when(thsConsumerMock.getHsdpToken()).thenReturn("122");

        thsManager.authenticateMutualAuthToken(contextMock, thsLoginCallBack);

        verify(awsdkMock).authenticateMutual(anyString(), auth_captor.capture());
        SDKCallback value = auth_captor.getValue();
        value.onResponse(any(Authentication.class), any(SDKError.class));
        verify(thsLoginCallBack).onLoginResponse(any(THSAuthentication.class), any(THSSDKError.class));
    }

    @Test
    public void authenticateMutualAuthFailure() throws MalformedURLException, AWSDKInstantiationException, AWSDKInitializationException, URISyntaxException {
        when(thsConsumerMock.getHsdpUUID()).thenReturn("1234");
        when(thsConsumerMock.getHsdpToken()).thenReturn("122");

        thsManager.authenticateMutualAuthToken(contextMock, thsLoginCallBack);

        verify(awsdkMock).authenticateMutual(anyString(), auth_captor.capture());
        SDKCallback value = auth_captor.getValue();
        value.onFailure(any(Throwable.class));
        verify(thsLoginCallBack).onLoginFailure(null);
    }

    @Test
    public void setServerURL() {
        THSManager.getInstance().setServerURL("sss");
        final String serverURL = THSManager.getInstance().getServerURL();
        assertNotNull(serverURL);
        assert serverURL.equalsIgnoreCase("sss");
    }

    @Test
    public void setTHSDocumentList() {
        ArrayList list = new ArrayList();
        list.add(documentRecordMock);
        THSManager.getInstance().setTHSDocumentList(list);
        final ArrayList<DocumentRecord> thsDocumentList = THSManager.getInstance().getTHSDocumentList();
        assertNotNull(thsDocumentList);
        assert thsDocumentList.size() == 1;
    }

    @Test
    public void completeEnrollmentOnResponse() throws AWSDKInstantiationException {
        thsManager.completeEnrollment(contextMock, thsAuthentication, thsGetConsumerObjectCallBackMock);
        verify(consumerManagerMock).completeEnrollment(any(Authentication.class), (State)isNull(), (String)isNull(), (String)isNull(), sdkPasswordErrorArgumentCaptor.capture());
        final SDKCallback<Consumer, SDKPasswordError> value = sdkPasswordErrorArgumentCaptor.getValue();
        value.onResponse(consumerMock, sdkPasswordErrorMock);
        verify(thsGetConsumerObjectCallBackMock).onReceiveConsumerObject(any(Consumer.class), any(SDKPasswordError.class));
    }

    @Test
    public void completeEnrollmentOnFailure() throws AWSDKInstantiationException {
        thsManager.completeEnrollment(contextMock, thsAuthentication, thsGetConsumerObjectCallBackMock);
        verify(consumerManagerMock).completeEnrollment(any(Authentication.class), (State)isNull(), (String)isNull(), (String)isNull(), sdkPasswordErrorArgumentCaptor.capture());
        final SDKCallback<Consumer, SDKPasswordError> value = sdkPasswordErrorArgumentCaptor.getValue();
        value.onFailure(throwable);
        verify(thsGetConsumerObjectCallBackMock).onError(any(Throwable.class));
    }

    @Test
    public void checkConsumerExists() throws AWSDKInstantiationException {
        thsManager.checkConsumerExists(contextMock, thsCheckConsumerExistsCallbackMock);
        verify(consumerManagerMock).checkConsumerExists(anyString(), consumerExisitsCaptor.capture());
        final SDKCallback<Boolean, SDKError> value = consumerExisitsCaptor.getValue();
        value.onResponse(true, sdkError);
        verify(thsCheckConsumerExistsCallbackMock).onResponse(anyBoolean(), any(THSSDKError.class));
    }

    @Test
    public void checkConsumerExistsNotDependent() throws AWSDKInstantiationException {
        thsManager.getThsConsumer(contextMock).setDependent(false);
        thsManager.checkConsumerExists(contextMock, thsCheckConsumerExistsCallbackMock);
        verify(consumerManagerMock).checkConsumerExists(anyString(), consumerExisitsCaptor.capture());
        final SDKCallback<Boolean, SDKError> value = consumerExisitsCaptor.getValue();
        value.onResponse(true, sdkError);
        verify(thsCheckConsumerExistsCallbackMock).onResponse(anyBoolean(), any(THSSDKError.class));
    }

    @Test
    public void checkConsumerExistsOnFailure() throws AWSDKInstantiationException {
        thsManager.getThsConsumer(contextMock).setDependent(false);
        thsManager.checkConsumerExists(contextMock, thsCheckConsumerExistsCallbackMock);
        verify(consumerManagerMock).checkConsumerExists(anyString(), consumerExisitsCaptor.capture());
        final SDKCallback<Boolean, SDKError> value = consumerExisitsCaptor.getValue();
        value.onFailure(throwable);
        verify(thsCheckConsumerExistsCallbackMock).onFailure(throwable);
    }

    @Test(expected = NullPointerException.class)
    public void enrollConsumer() throws AWSDKInstantiationException {
        thsManager.enrollConsumer(contextMock, dateMock, "ss", "ss", Gender.MALE, stateMock, pTHSDKValidatedCallback);
        verify(consumerManagerMock).enrollConsumer(any(ConsumerEnrollment.class), getUpdateConsumerCaptor.capture());
        final SDKValidatedCallback<Consumer, SDKPasswordError> value = getUpdateConsumerCaptor.getValue();
        value.onResponse(consumerMock, sdkPasswordErrorMock);
        verify(pTHSDKValidatedCallback).onResponse(any(Consumer.class), any(SDKError.class));
    }

    @Test(expected = NullPointerException.class)
    public void enrollConsumerOnFailure() throws AWSDKInstantiationException {
        thsManager.enrollConsumer(contextMock, dateMock, "ss", "ss", Gender.MALE, stateMock, pTHSDKValidatedCallback);
        verify(consumerManagerMock).enrollConsumer(any(ConsumerEnrollment.class), getUpdateConsumerCaptor.capture());
        final SDKValidatedCallback<Consumer, SDKPasswordError> value = getUpdateConsumerCaptor.getValue();
        value.onFailure(throwable);
        verify(pTHSDKValidatedCallback).onFailure(any(Throwable.class));
    }

    @Test(expected = NullPointerException.class)
    public void enrollConsumerOnValidationFailure() throws AWSDKInstantiationException {
        thsManager.enrollConsumer(contextMock, dateMock, "ss", "ss", Gender.MALE, stateMock, pTHSDKValidatedCallback);
        verify(consumerManagerMock).enrollConsumer(any(ConsumerEnrollment.class), getUpdateConsumerCaptor.capture());
        final SDKValidatedCallback<Consumer, SDKPasswordError> value = getUpdateConsumerCaptor.getValue();
        value.onValidationFailure(ArgumentMatchers.<String, String>anyMap());
        verify(pTHSDKValidatedCallback).onValidationFailure(anyMap());
    }

    @Test(expected = NullPointerException.class)
    public void enrollDependentonValidationFailure() throws AWSDKInstantiationException {
        thsManager.setThsConsumer(thsConsumerMock);
        when(thsConsumerMock.getHsdpUUID()).thenReturn("sss");
        thsManager.enrollDependent(contextMock, dateMock, "ddd", "sss", Gender.MALE, stateMock, pTHSDKValidatedCallback);
        verify(consumerManagerMock).enrollDependent(any(DependentEnrollment.class), getDependentenrollment.capture());
        final SDKValidatedCallback<Consumer, SDKError> value = getDependentenrollment.getValue();
        value.onValidationFailure(ArgumentMatchers.<String, String>anyMap());
        pTHSDKValidatedCallback.onValidationFailure(anyMap());
    }

    @Test(expected = NullPointerException.class)
    public void enrollDependentonResponse() throws AWSDKInstantiationException {
        thsManager.setThsConsumer(thsConsumerMock);
        when(thsConsumerMock.getHsdpUUID()).thenReturn("sss");
        thsManager.enrollDependent(contextMock, dateMock, "ddd", "sss", Gender.MALE, stateMock, pTHSDKValidatedCallback);
        verify(consumerManagerMock).enrollDependent(any(DependentEnrollment.class), getDependentenrollment.capture());
        final SDKValidatedCallback<Consumer, SDKError> value = getDependentenrollment.getValue();
        value.onResponse(any(Consumer.class), any(SDKError.class));
        pTHSDKValidatedCallback.onResponse(any(Consumer.class), any(SDKError.class));
    }

    @Test(expected = NullPointerException.class)
    public void enrollDependentonFailure() throws AWSDKInstantiationException {
        thsManager.enrollDependent(contextMock, dateMock, "dd", "dd", Gender.MALE, stateMock, pTHSDKValidatedCallback);
        verify(consumerManagerMock).enrollDependent(any(DependentEnrollment.class), getDependentenrollment.capture());
        final SDKValidatedCallback<Consumer, SDKError> value = getDependentenrollment.getValue();
        value.onFailure(throwable);
        pTHSDKValidatedCallback.onFailure(throwable);
    }

    @Mock
    private PracticeInfo practiceInfoMock;

    @Test
    public void getOnDemandSpecialities() throws AWSDKInstantiationException {
        thsManager.getOnDemandSpecialities(contextMock, practiceInfoMock, "ddd", thsOnDemandSpecialtyCallbackMock);
        verify(practiseprovidermanagerMock).getOnDemandSpecialties(any(Consumer.class), any(PracticeInfo.class), anyString(), getOndemandSpecialityCaptor.capture());
        final SDKCallback<List<OnDemandSpecialty>, SDKError> value = getOndemandSpecialityCaptor.getValue();
        List list = new ArrayList();
        list.add(onDemandSpecialtyMock);

        value.onResponse(list, sdkError);
        verify(thsOnDemandSpecialtyCallbackMock).onResponse(anyList(), any(THSSDKError.class));
    }

    @Test
    public void getOnDemandSpecialitiesOnFailure() throws AWSDKInstantiationException {
        thsManager.getOnDemandSpecialities(contextMock, practiceInfoMock, "ddd", thsOnDemandSpecialtyCallbackMock);
        verify(practiseprovidermanagerMock).getOnDemandSpecialties(any(Consumer.class), any(PracticeInfo.class), anyString(), getOndemandSpecialityCaptor.capture());
        final SDKCallback<List<OnDemandSpecialty>, SDKError> value = getOndemandSpecialityCaptor.getValue();
        List list = new ArrayList();
        list.add(onDemandSpecialtyMock);

        value.onFailure(throwable);
        verify(thsOnDemandSpecialtyCallbackMock).onFailure(any(Throwable.class));
    }

    @Test
    public void getVisitContextWithOnDemandSpeciality() throws AWSDKInstantiationException {
        thsManager.getVisitContextWithOnDemandSpeciality(contextMock, thsOnDemandSpeciality, thsvisitcontextcallback);
        verify(visitManagerMock).getVisitContext(any(Consumer.class), any(OnDemandSpecialty.class), getVisitContextCaptor.capture());
        final SDKCallback<VisitContext, SDKError> value = getVisitContextCaptor.getValue();
        value.onResponse(visitContextMock, sdkError);
        verify(thsvisitcontextcallback).onResponse(any(VisitContext.class), any(THSSDKError.class));
    }

    @Test
    public void getVisitContextWithOnDemandSpecialityOnFailure() throws AWSDKInstantiationException {
        thsManager.getVisitContextWithOnDemandSpeciality(contextMock, thsOnDemandSpeciality, thsvisitcontextcallback);
        verify(visitManagerMock).getVisitContext(any(Consumer.class), any(OnDemandSpecialty.class), getVisitContextCaptor.capture());
        final SDKCallback<VisitContext, SDKError> value = getVisitContextCaptor.getValue();
        value.onFailure(throwable);
        verify(thsvisitcontextcallback).onFailure(any(Throwable.class));
    }

    @Test
    public void getVitals() throws AWSDKInstantiationException {
        thsManager.getVitals(contextMock, thsVitalCallBackMock);
        verify(consumerManagerMock).getVitals(any(Consumer.class), any(VisitContext.class), getVitalsCaptor.capture());
        final SDKCallback<Vitals, SDKError> value = getVitalsCaptor.getValue();
        value.onResponse(vitalsMock, sdkError);
        verify(thsVitalCallBackMock).onResponse(any(THSVitals.class), any(THSSDKError.class));
    }

    @Test
    public void getVitalsFailure() throws AWSDKInstantiationException {
        thsManager.getVitals(contextMock, thsVitalCallBackMock);
        verify(consumerManagerMock).getVitals(any(Consumer.class), any(VisitContext.class), getVitalsCaptor.capture());
        final SDKCallback<Vitals, SDKError> value = getVitalsCaptor.getValue();
        value.onFailure(throwable);
        verify(thsVitalCallBackMock).onFailure(any(Throwable.class));
    }

    @Test
    public void getAppointments() throws AWSDKInstantiationException {
        thsManager.getAppointments(contextMock, sdkLocalDateMock, thsGetAppointmentsCallback);
        verify(consumerManagerMock).getAppointments(any(Consumer.class), any(SDKLocalDate.class), getAppointmentCaptor.capture());
        final SDKCallback<List<Appointment>, SDKError> value = getAppointmentCaptor.getValue();
        List list = new ArrayList();
        list.add(appointmentMock);

        value.onResponse(list, sdkError);
        verify(thsGetAppointmentsCallback).onResponse(anyList(), any(SDKError.class));
    }

    @Test
    public void getAppointmentsOnFailure() throws AWSDKInstantiationException {
        thsManager.getAppointments(contextMock, sdkLocalDateMock, thsGetAppointmentsCallback);
        verify(consumerManagerMock).getAppointments(any(Consumer.class), any(SDKLocalDate.class), getAppointmentCaptor.capture());
        final SDKCallback<List<Appointment>, SDKError> value = getAppointmentCaptor.getValue();
        List list = new ArrayList();
        list.add(appointmentMock);

        value.onFailure(throwable);
        verify(thsGetAppointmentsCallback).onFailure(any(Throwable.class));
    }

    @Test
    public void getConsumerObject() throws AWSDKInstantiationException {
        thsManager.getConsumerObject(contextMock, authentication, thsGetConsumerObjectCallBackMock);
        verify(consumerManagerMock).getConsumer(any(Authentication.class), getConsumerCaptor.capture());
        final SDKCallback<Consumer, SDKError> value = getConsumerCaptor.getValue();
        value.onResponse(consumerMock, sdkError);
        verify(thsGetConsumerObjectCallBackMock).onReceiveConsumerObject(any(Consumer.class), any(SDKError.class));
    }

    @Test
    public void getConsumerObjectFailed() throws AWSDKInstantiationException {
        thsManager.getConsumerObject(contextMock, authentication, thsGetConsumerObjectCallBackMock);
        verify(consumerManagerMock).getConsumer(any(Authentication.class), getConsumerCaptor.capture());
        final SDKCallback<Consumer, SDKError> value = getConsumerCaptor.getValue();
        value.onFailure(throwable);
        verify(thsGetConsumerObjectCallBackMock).onError(any(Throwable.class));
    }

    @Test
    public void getMedication() throws AWSDKInstantiationException {
        thsManager.getMedication(contextMock, pTHGetMedicationCallbackMock);
        verify(consumerManagerMock).getMedications(any(Consumer.class), getMedicationCaptor.capture());
        final SDKCallback<List<Medication>, SDKError> value = getMedicationCaptor.getValue();
        List list = new ArrayList();
        list.add(medicationMock);
        value.onResponse(list, sdkError);
        verify(pTHGetMedicationCallbackMock).onGetMedicationReceived(any(THSMedication.class), any(SDKError.class));
    }

    @Test
    public void getMedicationOnFailure() throws AWSDKInstantiationException {
        thsManager.getMedication(contextMock, pTHGetMedicationCallbackMock);
        verify(consumerManagerMock).getMedications(any(Consumer.class), getMedicationCaptor.capture());
        final SDKCallback<List<Medication>, SDKError> value = getMedicationCaptor.getValue();
        List list = new ArrayList();
        list.add(medicationMock);
        value.onFailure(throwable);
        verify(pTHGetMedicationCallbackMock).onFailure(any(Throwable.class));
    }

    @Test
    public void searchMedication() throws AWSDKInstantiationException {
        thsManager.searchMedication(contextMock, "medname", pTHSDKValidatedCallback);
        verify(consumerManagerMock).searchMedications(any(Consumer.class), anyString(), getSearchedMedicationCaptor.capture());
        final SDKValidatedCallback<List<Medication>, SDKError> value = getSearchedMedicationCaptor.getValue();
        List list = new ArrayList();
        list.add(medicationMock);
        value.onResponse(list, sdkError);
        verify(pTHSDKValidatedCallback).onResponse(any(THSMedication.class), any(SDKError.class));
    }

    @Test
    public void searchMedicationFailure() throws AWSDKInstantiationException {
        thsManager.searchMedication(contextMock, "medname", pTHSDKValidatedCallback);
        verify(consumerManagerMock).searchMedications(any(Consumer.class), anyString(), getSearchedMedicationCaptor.capture());
        final SDKValidatedCallback<List<Medication>, SDKError> value = getSearchedMedicationCaptor.getValue();
        List list = new ArrayList();
        list.add(medicationMock);
        value.onFailure(throwable);
        verify(pTHSDKValidatedCallback).onFailure(any(Throwable.class));
    }

    @Test
    public void searchMedicationonValidationFailure() throws AWSDKInstantiationException {
        thsManager.searchMedication(contextMock, "medname", pTHSDKValidatedCallback);
        verify(consumerManagerMock).searchMedications(any(Consumer.class), anyString(), getSearchedMedicationCaptor.capture());
        final SDKValidatedCallback<List<Medication>, SDKError> value = getSearchedMedicationCaptor.getValue();
        List list = new ArrayList();
        list.add(medicationMock);
        Map map = new HashMap();
        value.onValidationFailure(map);
        verify(pTHSDKValidatedCallback).onValidationFailure(any(Map.class));
    }

    @Mock
    private THSVitals thsVitalsMock;

    @Mock
    private THSUpdateVitalsCallBack thsUpdateVitalsCallBackMock;

    @Captor
    private ArgumentCaptor<SDKValidatedCallback<Void, SDKError>> updateVitalsCaptor;

    @Test
    public void updateVitals() throws AWSDKInstantiationException {
        when(thsVitalsMock.getVitals()).thenReturn(vitalsMock);
        thsManager.updateVitals(contextMock, thsVitalsMock, thsUpdateVitalsCallBackMock);
        verify(consumerManagerMock).updateVitals(any(Consumer.class), any(Vitals.class), (VisitContext)isNull(), updateVitalsCaptor.capture());
        final SDKValidatedCallback<Void, SDKError> value = updateVitalsCaptor.getValue();
        value.onResponse(null, sdkError);
        verify(thsUpdateVitalsCallBackMock).onUpdateVitalsResponse(sdkError);
    }

    @Test
    public void updateVitalsFailure() throws AWSDKInstantiationException {
        when(thsVitalsMock.getVitals()).thenReturn(vitalsMock);
        thsManager.updateVitals(contextMock, thsVitalsMock, thsUpdateVitalsCallBackMock);
        verify(consumerManagerMock).updateVitals(any(Consumer.class), any(Vitals.class), (VisitContext)isNull(), updateVitalsCaptor.capture());
        final SDKValidatedCallback<Void, SDKError> value = updateVitalsCaptor.getValue();
        value.onFailure(throwable);
        verify(thsUpdateVitalsCallBackMock).onUpdateVitalsFailure(any(Throwable.class));
    }

    @Test
    public void updateVitalsValidationFailure() throws AWSDKInstantiationException {
        when(thsVitalsMock.getVitals()).thenReturn(vitalsMock);
        thsManager.updateVitals(contextMock, thsVitalsMock, thsUpdateVitalsCallBackMock);
        verify(consumerManagerMock).updateVitals(any(Consumer.class), any(Vitals.class), (VisitContext)isNull(), updateVitalsCaptor.capture());
        final SDKValidatedCallback<Void, SDKError> value = updateVitalsCaptor.getValue();
        Map map = new HashMap();
        value.onValidationFailure(map);
        verify(thsUpdateVitalsCallBackMock).onUpdateVitalsValidationFailure(ArgumentMatchers.<String, String>anyMap());
    }

    @Mock
    private THSPreferredPharmacyCallback thsPreferredPharmacyCallbackMock;

    @Captor
    private ArgumentCaptor<SDKCallback<Pharmacy, SDKError>> pharmacypreferedcallback;

    @Mock
    private Pharmacy pharmacyMock;

    @Mock
    private THSUpdatePharmacyCallback thsUpdatePharmacyCallback;

    @Test
    public void getConsumerPreferredPharmacy() throws AWSDKInstantiationException {
        thsManager.getConsumerPreferredPharmacy(contextMock, thsPreferredPharmacyCallbackMock);
        verify(consumerManagerMock).getConsumerPharmacy(any(Consumer.class), pharmacypreferedcallback.capture());
        final SDKCallback<Pharmacy, SDKError> value = pharmacypreferedcallback.getValue();
        value.onResponse(pharmacyMock, sdkError);
        verify(thsPreferredPharmacyCallbackMock).onPharmacyReceived(pharmacyMock, sdkError);
    }

    @Test
    public void getConsumerPreferredPharmacyOnFailure() throws AWSDKInstantiationException {
        thsManager.getConsumerPreferredPharmacy(contextMock, thsPreferredPharmacyCallbackMock);
        verify(consumerManagerMock).getConsumerPharmacy(any(Consumer.class), pharmacypreferedcallback.capture());
        final SDKCallback<Pharmacy, SDKError> value = pharmacypreferedcallback.getValue();
        value.onFailure(throwable);
        verify(thsPreferredPharmacyCallbackMock).onFailure(any(Throwable.class));
    }

    @Test
    public void updateConsumerPreferredPharmacy() throws AWSDKInstantiationException {
        thsManager.updateConsumerPreferredPharmacy(contextMock, pharmacyMock, thsUpdatePharmacyCallback);
        verify(consumerManagerMock).updateConsumerPharmacy(any(Consumer.class), any(Pharmacy.class), getUpdateMedicationCaptor.capture());
        final SDKCallback<Void, SDKError> value = getUpdateMedicationCaptor.getValue();
        value.onResponse(null, sdkError);
        verify(thsUpdatePharmacyCallback).onUpdateSuccess(any(SDKError.class));
    }

    @Test
    public void updateConsumerPreferredPharmacyFailure() throws AWSDKInstantiationException {
        thsManager.updateConsumerPreferredPharmacy(contextMock, pharmacyMock, thsUpdatePharmacyCallback);
        verify(consumerManagerMock).updateConsumerPharmacy(any(Consumer.class), any(Pharmacy.class), getUpdateMedicationCaptor.capture());
        final SDKCallback<Void, SDKError> value = getUpdateMedicationCaptor.getValue();
        value.onFailure(throwable);
        verify(thsUpdatePharmacyCallback).onUpdateFailure(any(Throwable.class));
    }

    @Mock
    private THSConsumerShippingAddressCallback thsConsumerShippingAddressCallbackMock;

    @Captor
    private ArgumentCaptor<SDKCallback<Address, SDKError>> getConsumerShippingAddressCaptor;

    @Mock
    private Address addressMock;

    @Test
    public void getConsumerShippingAddress() throws AWSDKInstantiationException {
        thsManager.getConsumerShippingAddress(contextMock, thsConsumerShippingAddressCallbackMock);
        verify(consumerManagerMock).getShippingAddress(any(Consumer.class), getConsumerShippingAddressCaptor.capture());
        final SDKCallback<Address, SDKError> value = getConsumerShippingAddressCaptor.getValue();
        value.onResponse(addressMock, sdkError);
        verify(thsConsumerShippingAddressCallbackMock).onSuccessfulFetch(any(Address.class), any(SDKError.class));
    }

    @Test
    public void getConsumerShippingAddressFailure() throws AWSDKInstantiationException {
        thsManager.getConsumerShippingAddress(contextMock, thsConsumerShippingAddressCallbackMock);
        verify(consumerManagerMock).getShippingAddress(any(Consumer.class), getConsumerShippingAddressCaptor.capture());
        final SDKCallback<Address, SDKError> value = getConsumerShippingAddressCaptor.getValue();
        value.onFailure(throwable);
        verify(thsConsumerShippingAddressCallbackMock).onFailure(any(Throwable.class));
    }

    @Test
    public void getRamdomPassword() {
        final String s = thsManager.generatePasswordRandomly();
        assertNotNull(s);
        assertThat(s).isInstanceOf(String.class);
    }

    @Test
    public void validatePasswordWithSmallCapitalAndDigits() {
        String password = "Aa123";
        boolean isValid = thsManager.validatePassword(password);
        assertTrue(isValid);
    }

    @Test
    public void validatePasswordWithCapitalLettersOnly() {
        String password = "AAA";
        boolean isValid = thsManager.validatePassword(password);
        assertFalse(isValid);
    }

    @Test
    public void validatePasswordWithSmallLettersOnly() {
        String password = "abc";
        boolean isValid = thsManager.validatePassword(password);
        assertFalse(isValid);
    }

    @Test
    public void validatePasswordWithDigitsOnly() {
        String password = "111";
        boolean isValid = thsManager.validatePassword(password);
        assertFalse(isValid);
    }

    @Test
    public void validatePasswordWithSmallAndCapitalLetters() {
        String password = "AAss";
        boolean isValid = thsManager.validatePassword(password);
        assertFalse(isValid);
    }

    @Test
    public void validatePasswordWithSmallAndDigits() {
        String password = "ss12";
        boolean isValid = thsManager.validatePassword(password);
        assertFalse(isValid);
    }

    @Test
    public void validatePasswordWithCapitalAndDigits() {
        String password = "12SS";
        boolean isValid = thsManager.validatePassword(password);
        assertFalse(isValid);
    }

    @Test
    public void shouldValidateABFlowValues() throws Exception {
        thsManager.setOnBoradingABFlow(THSConstants.THS_ONBOARDING_ABFLOW1);
        thsManager.setProviderListABFlow(THSConstants.THS_PROVIDER_LIST_FLOW2);

        assertThat(thsManager.getOnBoradingABFlow()).isEqualTo(THSConstants.THS_ONBOARDING_ABFLOW1);
        assertThat(thsManager.getProviderListABFlow()).isEqualTo(THSConstants.THS_PROVIDER_LIST_FLOW2);

    }
}
