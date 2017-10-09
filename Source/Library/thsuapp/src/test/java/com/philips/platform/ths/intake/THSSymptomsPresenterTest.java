package com.philips.platform.ths.intake;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.UploadAttachment;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.consumer.DocumentRecord;
import com.americanwell.sdk.entity.legal.LegalText;
import com.americanwell.sdk.entity.practice.OnDemandSpecialty;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.exception.AWSDKInitializationException;
import com.americanwell.sdk.exception.AWSDKInstantiationException;
import com.americanwell.sdk.manager.ConsumerManager;
import com.americanwell.sdk.manager.SDKCallback;
import com.americanwell.sdk.manager.SDKValidatedCallback;
import com.americanwell.sdk.manager.VisitManager;
import com.philips.platform.appinfra.FileUtils;
import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.ths.providerslist.THSOnDemandSpeciality;
import com.philips.platform.ths.providerslist.THSProviderInfo;
import com.philips.platform.ths.registration.THSConsumer;
import com.philips.platform.ths.sdkerrors.THSSDKError;
import com.philips.platform.ths.utility.THSFileUtils;
import com.philips.platform.ths.utility.THSManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class THSSymptomsPresenterTest {

    THSSymptomsPresenter pthSymptomsPresenter;

    @Mock
    THSSymptomsFragment pTHBaseViewMock;

    @Mock
    THSProviderInfo pthProviderInfo;

    @Mock
    ConsumerManager consumerManagerMock;

    @Mock
    THSConsumer pthConsumer;

    @Mock
    AWSDK awsdk;

    @Mock
    FragmentActivity activityMock;

    @Mock
    THSSDKError pthsdkError;

    @Mock
    THSVisitContext pthVisitContext;

    @Mock
    ProviderInfo providerInfo;

    @Mock
    VisitContext visitContext;

    @Mock
    LegalText legalText;

    @Mock
    Throwable throwable;

    @Mock
    FragmentActivity fragmentActivity;

    @Mock
    VisitManager visitManagerMock;

    @Mock
    Consumer consumerMock;

    @Mock
    Uri uriMock;

    @Mock
    THSFileUtils fileUtilsMock;

    @Mock
    UploadAttachment uploadAttachmentMock;

    @Mock
    OnDemandSpecialty onDemandSpecialtyMock;

    @Mock
    THSOnDemandSpeciality thsOnDemandSpeciality;

    @Mock
    Map mapMock;

    @Mock
    DocumentRecord documentRecordMock;

    @Mock
    SDKError sdkErrorMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        pthSymptomsPresenter = new THSSymptomsPresenter(pTHBaseViewMock,pthProviderInfo);
        THSManager.getInstance().setAwsdk(awsdk);
        when(pTHBaseViewMock.getFragmentActivity()).thenReturn(activityMock);
        when(pTHBaseViewMock.isFragmentAttached()).thenReturn(true);
    }

    @Test
    public void onResponse() throws Exception {
        List list = new ArrayList();
        list.add(legalText);
        when(pthVisitContext.getLegalTexts()).thenReturn(list);
        pthSymptomsPresenter.onResponse(pthVisitContext, pthsdkError);
        verify(pTHBaseViewMock).addTopicsToView(pthVisitContext);
//        verify(legalText).setAccepted(true);
    }

    @Test
    public void onFailure() throws Exception {
        pthSymptomsPresenter.onFailure(throwable);
    }

    @Test
    public void getVisitContext() throws Exception {
        when(pthVisitContext.getVisitContext()).thenReturn(visitContext);
        when(pthProviderInfo.getProviderInfo()).thenReturn(providerInfo);
        when(pthConsumer.getConsumer()).thenReturn(consumerMock);
        when(pTHBaseViewMock.getFragmentActivity()).thenReturn(fragmentActivity);
        when(awsdk.getVisitManager()).thenReturn(visitManagerMock);
        THSManager.getInstance().setPTHConsumer(pthConsumer);
        THSManager.getInstance().setVisitContext(pthVisitContext);
        pthSymptomsPresenter.getVisitContext();
        verify(visitManagerMock).getVisitContext(any(Consumer.class),any(ProviderInfo.class),any(SDKCallback.class));
    }

    @Test
    public void getVisitContextThrowsMalformedURLException() throws Exception {
        when(pthVisitContext.getVisitContext()).thenReturn(visitContext);
        when(pthProviderInfo.getProviderInfo()).thenReturn(providerInfo);
        when(pthConsumer.getConsumer()).thenReturn(consumerMock);
        when(pTHBaseViewMock.getFragmentActivity()).thenReturn(fragmentActivity);
        when(awsdk.getVisitManager()).thenReturn(visitManagerMock);
        doThrow(MalformedURLException.class).when(awsdk).getVisitManager();
        pthSymptomsPresenter.getVisitContext();
        verifyNoMoreInteractions(visitManagerMock);
    }

    @Test
    public void getVisitContextThrowsURISyntaxException() throws Exception {
        when(pthVisitContext.getVisitContext()).thenReturn(visitContext);
        when(pthProviderInfo.getProviderInfo()).thenReturn(providerInfo);
        when(pthConsumer.getConsumer()).thenReturn(consumerMock);
        when(pTHBaseViewMock.getFragmentActivity()).thenReturn(fragmentActivity);
        when(awsdk.getVisitManager()).thenReturn(visitManagerMock);
        doThrow(URISyntaxException.class).when(awsdk).getVisitManager();
        pthSymptomsPresenter.getVisitContext();
        verifyNoMoreInteractions(visitManagerMock);
    }

    @Test
    public void getVisitContextThrowsAWSDKInstantiationException() throws Exception {
        when(pthVisitContext.getVisitContext()).thenReturn(visitContext);
        when(pthProviderInfo.getProviderInfo()).thenReturn(providerInfo);
        when(pthConsumer.getConsumer()).thenReturn(consumerMock);
        when(pTHBaseViewMock.getFragmentActivity()).thenReturn(fragmentActivity);
        when(awsdk.getVisitManager()).thenReturn(visitManagerMock);
        doThrow(AWSDKInstantiationException.class).when(awsdk).getVisitManager();
        pthSymptomsPresenter.getVisitContext();
        verifyNoMoreInteractions(visitManagerMock);
    }

    @Test
    public void getVisitContextThrowsAWSDKInitializationException() throws Exception {
        when(pthVisitContext.getVisitContext()).thenReturn(visitContext);
        when(pthProviderInfo.getProviderInfo()).thenReturn(providerInfo);
        when(pthConsumer.getConsumer()).thenReturn(consumerMock);
        when(pTHBaseViewMock.getFragmentActivity()).thenReturn(fragmentActivity);
        when(awsdk.getVisitManager()).thenReturn(visitManagerMock);
        doThrow(AWSDKInitializationException.class).when(awsdk).getVisitManager();
        pthSymptomsPresenter.getVisitContext();
        verifyNoMoreInteractions(visitManagerMock);
    }

    @Test
    public void onEventTest(){
        pthSymptomsPresenter.onEvent(R.id.continue_btn);
        verify(pTHBaseViewMock).addFragment(any(THSBaseFragment.class),anyString(),any(Bundle.class));
    }

    @Test
    public void uploadDocuments() throws IOException {
        when(pTHBaseViewMock.getFragmentActivity()).thenReturn(fragmentActivity);
        pthSymptomsPresenter.fileUtils = fileUtilsMock;
        THSManager.getInstance().setPTHConsumer(pthConsumer);
        when(pthConsumer.getConsumer()).thenReturn(consumerMock);
        when(awsdk.getConsumerManager()).thenReturn(consumerManagerMock);
        when(fileUtilsMock.getUploadAttachment(fragmentActivity, awsdk, uriMock)).thenReturn(uploadAttachmentMock);
        pthSymptomsPresenter.uploadDocuments(uriMock);
        verify(consumerManagerMock).addHealthDocument(any(Consumer.class), any(UploadAttachment.class), any(SDKValidatedCallback.class));
    }

    @Test
    public void fetchHealthDocuments(){
        pthSymptomsPresenter.fetchHealthDocuments();
    }

    @Test
    public void getVisitContextThsProviderInfoNull(){
        pthSymptomsPresenter = new THSSymptomsPresenter(pTHBaseViewMock,null);
        when(pthVisitContext.getVisitContext()).thenReturn(visitContext);
        when(pthProviderInfo.getProviderInfo()).thenReturn(providerInfo);
        when(pthConsumer.getConsumer()).thenReturn(consumerMock);
        when(pTHBaseViewMock.getFragmentActivity()).thenReturn(fragmentActivity);
        when(awsdk.getVisitManager()).thenReturn(visitManagerMock);
        THSManager.getInstance().setPTHConsumer(pthConsumer);
        THSManager.getInstance().setVisitContext(pthVisitContext);
        pthSymptomsPresenter.getVisitContext();
        verify(visitManagerMock).getVisitContext(any(Consumer.class),any(ProviderInfo.class),any(SDKCallback.class));
    }

    @Test
    public void getfirstAvailableProvider() throws AWSDKInstantiationException {
        THSManager.getInstance().setPTHConsumer(pthConsumer);
        when(pthConsumer.getConsumer()).thenReturn(consumerMock);
        when(thsOnDemandSpeciality.getOnDemandSpecialty()).thenReturn(onDemandSpecialtyMock);
        when(pTHBaseViewMock.getContext()).thenReturn(fragmentActivity);
        when(awsdk.getVisitManager()).thenReturn(visitManagerMock);
        pthSymptomsPresenter.getfirstAvailableProvider(thsOnDemandSpeciality);
        verify(visitManagerMock).getVisitContext(any(Consumer.class),any(OnDemandSpecialty.class),any(SDKCallback.class));
    }

    @Test
    public void onUploadValidationFailure(){
        pthSymptomsPresenter.onUploadValidationFailure(mapMock);
        verify(pTHBaseViewMock).showToast(anyString());
    }

    @Test
    public void onUploadDocumentSuccess(){
        pthSymptomsPresenter.onUploadDocumentSuccess(documentRecordMock,sdkErrorMock);
        verify(pTHBaseViewMock).showToast(anyString());
    }

    @Test
    public void onUploadDocumentError(){
        pthSymptomsPresenter.onUploadDocumentSuccess(documentRecordMock,null);
        verify(pTHBaseViewMock).showToast(anyString());
    }

    @Test
    public void onError(){
        pthSymptomsPresenter.onFailure(throwable);
        verify(pTHBaseViewMock).hideProgressBar();
    }

    @Test
    public void onError1(){
        pthSymptomsPresenter.onError(throwable);
        verify(pTHBaseViewMock).hideProgressBar();
    }
}
