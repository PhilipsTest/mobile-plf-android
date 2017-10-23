package com.philips.platform.ths.pharmacy;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.entity.Address;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.pharmacy.PharmacyType;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.manager.ConsumerManager;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.ths.BuildConfig;
import com.philips.platform.ths.CustomRobolectricRunnerAmwel;
import com.philips.platform.ths.intake.THSVisitContext;
import com.philips.platform.ths.registration.THSConsumer;
import com.philips.platform.ths.utility.THSManager;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static com.philips.platform.ths.utility.THSConstants.THS_APPLICATION_ID;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(CustomRobolectricRunnerAmwel.class) @Config(shadows = THSShadowMapFragment.class)
public class THSPharmacyListFragmentTest {

    @Mock
    AWSDK awsdkMock;

    @Mock
    THSConsumer thsConsumer;

    @Mock
    THSVisitContext pthVisitContext;

    @Mock
    Consumer consumerMock;

    @Mock
    VisitContext visitManagerMock;

    @Mock
    ActionBarListener actionBarListenerMock;

    @Mock
    THSPharmacyListFragment thsPharmacyListFragmentMock;

    @Mock
    FragmentLauncher fragmentLauncherMock;

    @Mock
    ConsumerManager consumerManagerMock;

    @Mock
    Pharmacy pharmacy;
    @Mock
    THSShippingAddressFragment thsShippingAddressFragment;
    @Mock
    Address address;

    @Mock
    FragmentActivity activityMock;


    @Mock
    AppInfraInterface appInfraInterface;

    @Mock
    AppTaggingInterface appTaggingInterface;

    THSPharmacyListFragment thsPharmacyListFragment;

    @Mock
    FragmentManager fragmentManagerMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ShadowLog.stream = System.out;
        THSManager.getInstance().setAwsdk(awsdkMock);

        when(thsConsumer.getConsumer()).thenReturn(consumerMock);
        when(awsdkMock.getConsumerManager()).thenReturn(consumerManagerMock);


        when(appInfraInterface.getTagging()).thenReturn(appTaggingInterface);
        when(appInfraInterface.getTagging().createInstanceForComponent(THS_APPLICATION_ID, BuildConfig.VERSION_NAME)).thenReturn(appTaggingInterface);
        THSManager.getInstance().setAppInfra(appInfraInterface);

        thsShippingAddressFragment = new THSShippingAddressFragment();
        thsShippingAddressFragment.setActionBarListener(actionBarListenerMock);
        thsShippingAddressFragment.setConsumerAndAddress(thsConsumer,address);
        thsShippingAddressFragment.setFragmentLauncher(fragmentLauncherMock);

        thsPharmacyListFragment = new THSPharmacyListFragment();
        thsPharmacyListFragment.setActionBarListener(actionBarListenerMock);
        thsPharmacyListFragment.setFragmentLauncher(fragmentLauncherMock);

    }

    @Test
    public void launchPharmacyListFragment(){
        SupportFragmentTestUtil.startFragment(thsPharmacyListFragment);
    }

    @Test
    public void testValidateForMailOrder(){
        when(pharmacy.getType()).thenReturn(PharmacyType.MailOrder);
        thsPharmacyListFragmentMock.validateForMailOrder(pharmacy);
        verify(thsPharmacyListFragmentMock).validateForMailOrder(eq(pharmacy));
    }

    @Test
    public void testShowShippingFragment(){
        thsPharmacyListFragmentMock.showShippingFragment();
        verify(thsPharmacyListFragmentMock).showShippingFragment();
    }

    @Test
    public void testLaunchInsuranceCostSummary(){
        thsPharmacyListFragmentMock.launchInsuranceCostSummary();
        verify(thsPharmacyListFragmentMock).launchInsuranceCostSummary();
    }

    @Test
    public void testHandleBackEvent(){
        thsPharmacyListFragmentMock.handleBackEvent();
        verify(thsPharmacyListFragmentMock).handleBackEvent();
        when(thsPharmacyListFragmentMock.handleBackEvent()).thenReturn(false);
        assertEquals(thsPharmacyListFragmentMock.handleBackEvent(),false);
    }

    @Test
    public void testHideSelectedPharmacy(){
        thsPharmacyListFragmentMock.hideSelectedPharmacy();
        verify(thsPharmacyListFragmentMock).hideSelectedPharmacy();
        when(thsPharmacyListFragmentMock.hideSelectedPharmacy()).thenReturn(true);
        assertEquals(thsPharmacyListFragmentMock.hideSelectedPharmacy(),true);
    }

    @Test
    public void testShowSelectedPharmacyDetails(){
        thsPharmacyListFragmentMock.showSelectedPharmacyDetails(pharmacy);
        verify(thsPharmacyListFragmentMock).showSelectedPharmacyDetails(Matchers.eq(pharmacy));
    }

    @Test
    public void testSwtichView(){
        thsPharmacyListFragmentMock.switchView();
        verify(thsPharmacyListFragmentMock,atLeastOnce()).switchView();
    }
}
