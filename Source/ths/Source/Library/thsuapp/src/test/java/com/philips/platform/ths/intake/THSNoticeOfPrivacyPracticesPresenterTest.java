/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.intake;

import android.support.v4.app.FragmentActivity;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.legal.LegalText;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.manager.SDKCallback;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.ths.BuildConfig;
import com.philips.platform.ths.utility.THSManager;
import com.philips.platform.uid.view.widget.Label;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static com.philips.platform.ths.utility.THSConstants.THS_APPLICATION_ID;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class THSNoticeOfPrivacyPracticesPresenterTest {

    THSNoticeOfPrivacyPracticesPresenter mThsNoticeOfPrivacyPracticesPresenter;

    @Mock
    AWSDK awsdkMock;

    @Mock
    LegalText legalTextMock;

    @Mock
    VisitContext visitContextMock;

    @Mock
    VisitContext thsVisitContext;

    @Mock
    FragmentActivity fragmentActivityMock;

    @Mock
    Label labelMock;

    @Mock
    SDKError sdkErrorMock;

    @Mock
    Throwable throwableMock;

    @Mock
    THSNoticeOfPrivacyPracticesFragment thsNoticeOfPrivacyPracticesFragment;

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
        THSManager.getInstance().setAwsdk(awsdkMock);
        THSManager.getInstance().setVisitContext(thsVisitContext);
        when(appInfraInterface.getTagging()).thenReturn(appTaggingInterface);
        when(appInfraInterface.getTagging().createInstanceForComponent(THS_APPLICATION_ID, BuildConfig.VERSION_NAME)).thenReturn(appTaggingInterface);
        when(appInfraInterface.getLogging()).thenReturn(loggingInterface);
        when(appInfraInterface.getLogging().createInstanceForComponent(THS_APPLICATION_ID, BuildConfig.VERSION_NAME)).thenReturn(loggingInterface);
        when(appInfraInterface.getServiceDiscovery()).thenReturn(serviceDiscoveryMock);
        THSManager.getInstance().setAppInfra(appInfraInterface);

        List list = new ArrayList();
        list.add(legalTextMock);
        when(visitContextMock.getLegalTexts()).thenReturn(list);
        when(thsNoticeOfPrivacyPracticesFragment.getFragmentActivity()).thenReturn(fragmentActivityMock);
        mThsNoticeOfPrivacyPracticesPresenter = new THSNoticeOfPrivacyPracticesPresenter(thsNoticeOfPrivacyPracticesFragment);
    }

    @Test
    public void showLegalTextForNOPP() throws Exception {
        List list = new ArrayList();
        list.add(legalTextMock);
        when(visitContextMock.getLegalTexts()).thenReturn(list);
        THSManager.getInstance().setVisitContext(thsVisitContext);
        mThsNoticeOfPrivacyPracticesPresenter.mTHSVisitContext = thsVisitContext;
        mThsNoticeOfPrivacyPracticesPresenter.showLegalTextForNOPP();
      //  verify(awsdkMock).getLegalText(any(LegalText.class),any(SDKCallback.class));
        verifyNoMoreInteractions(awsdkMock);
    }

    @Test
    public void onEvent() throws Exception {
        mThsNoticeOfPrivacyPracticesPresenter.onEvent(0);
    }

  /*  @Test(expected = NullPointerException.class)
    public void onNoticeOfPrivacyPracticesReceivedSuccess() throws Exception {
        mThsNoticeOfPrivacyPracticesPresenter.onNoticeOfPrivacyPracticesReceivedSuccess("any",sdkErrorMock);
        thsNoticeOfPrivacyPracticesFragment.legalTextsLabel = labelMock;
        verify(thsNoticeOfPrivacyPracticesFragment).hideProgressBar();
    }

    @Test
    public void onNoticeOfPrivacyPracticesReceivedFailure() throws Exception {
        mThsNoticeOfPrivacyPracticesPresenter.onNoticeOfPrivacyPracticesReceivedFailure(throwableMock);
        verify(thsNoticeOfPrivacyPracticesFragment).hideProgressBar();
    }*/

}