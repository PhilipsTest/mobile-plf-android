/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.intake;

import android.content.Context;
import android.os.Bundle;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.manager.ConsumerManager;
import com.americanwell.sdk.manager.SDKCallback;
import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.ths.registration.THSConsumer;
import com.philips.platform.ths.utility.THSManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class THSMedicationPresenterTest {

    THSMedicationPresenter mTHSMedicationPresenter;

    @Mock
    THSMedicationFragment thsMedicationFragmentMock;

    @Mock
    THSMedication thsMedicationMock;

    @Mock
    Medication medicationMock;

    @Mock
    Context contextMock;

    @Mock
    AWSDK awsdkMock;

    @Mock
    THSConsumer thsConsumer;

    @Mock
    Consumer consumer;

    @Mock
    ConsumerManager consumerManagerMock;

    @Mock
    SDKError sdkErrormock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        THSManager.getInstance().setAwsdk(awsdkMock);
        THSManager.getInstance().setPTHConsumer(thsConsumer);
        mTHSMedicationPresenter = new THSMedicationPresenter(thsMedicationFragmentMock);
        thsMedicationFragmentMock.mExistingMedication = thsMedicationMock;
        when(thsMedicationFragmentMock.getContext()).thenReturn(contextMock);
        List list = new ArrayList();
        list.add(medicationMock);
        when(thsMedicationMock.getMedicationList()).thenReturn(list);
        when(thsConsumer.getConsumer()).thenReturn(consumer);
        when(awsdkMock.getConsumerManager()).thenReturn(consumerManagerMock);
        when(thsMedicationFragmentMock.isFragmentAttached()).thenReturn(true);
    }

    @Test
    public void onEvent_ths_intake_medication_continue_button() throws Exception {
        mTHSMedicationPresenter.onEvent(R.id.ths_intake_medication_continue_button);
        verify(consumerManagerMock).updateMedications(any(Consumer.class),anyList(),any(SDKCallback.class));
    }

    @Test
    public void onEvent_ths_existing_medicine_footer_relative_layout() throws Exception {
        mTHSMedicationPresenter.onEvent(R.id.ths_existing_medicine_footer_relative_layout);
        verify(thsMedicationFragmentMock, atLeast(1)).addFragment(any(THSBaseFragment.class),anyString(),any(Bundle.class), anyBoolean());
    }

    @Test
    public void onEvent_ths_intake_medication_skip_step_label() throws Exception {
        mTHSMedicationPresenter.onEvent(R.id.ths_intake_medication_skip_step_label);
        verify(thsMedicationFragmentMock, atLeast(1)).addFragment(any(THSBaseFragment.class),anyString(),any(Bundle.class), anyBoolean());
    }

    @Test
    public void fetchMedication() throws Exception {
        mTHSMedicationPresenter.fetchMedication();
        verify(consumerManagerMock).getMedications(any(Consumer.class),any(SDKCallback.class));
    }

    @Test
    public void addSearchedMedication() throws Exception {
        mTHSMedicationPresenter.addSearchedMedication(medicationMock);
        verify(thsMedicationFragmentMock).addSearchedMedicineToExistingMedication(any(Medication.class));
    }

    @Test
    public void onGetMedicationReceived() throws Exception {
        mTHSMedicationPresenter.onGetMedicationReceived(null,sdkErrormock);
        verify(thsMedicationFragmentMock).showExistingMedicationList(any(THSMedication.class));
    }

}