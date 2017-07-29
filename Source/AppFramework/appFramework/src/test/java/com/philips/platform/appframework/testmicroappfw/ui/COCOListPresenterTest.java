package com.philips.platform.appframework.testmicroappfw.ui;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.appframework.testmicroappfw.data.TestConfigManager;
import com.philips.platform.appframework.testmicroappfw.models.Chapter;
import com.philips.platform.appframework.testmicroappfw.models.CommonComponent;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.base.FragmentView;
import com.philips.platform.uappframework.launcher.UiLauncher;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by philips on 29/07/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class COCOListPresenterTest {

    @Mock
    Context context;

    @Mock
    COCOListContract.View view;


    COCOListPresenter cocoListPresenter;

    @Mock
    FragmentView fragmentView;

    @Mock
    TestConfigManager testConfigManager;

    @Mock
    Chapter chapter;

    @Captor
    ArgumentCaptor<TestConfigManager.TestConfigCallback> testConfigCallbackArgumentCaptor;

    TestConfigManager.TestConfigCallback testConfigCallback;

    @Mock
    AppFrameworkApplication appFrameworkApplication;

    @Mock
    BaseFlowManager flowManager;

    @Mock
    BaseState baseState;

    @Mock
    BaseState newBaseState;

    @Mock
    FragmentActivity fragmentActivity;


    @Before
    public void setUp(){
        when(fragmentView.getFragmentActivity()).thenReturn(fragmentActivity);
        cocoListPresenter=new COCOListPresenter(fragmentView,testConfigManager,context,view){
            @Override
            protected AppFrameworkApplication getApplicationContext() {
                return appFrameworkApplication;
            }
        };
        cocoListPresenter.onStateComplete(baseState);
        cocoListPresenter.onEvent(0);
    }

    @Test
    public void loadCoCoList() throws Exception {
        cocoListPresenter.loadCoCoList(chapter);
        verify(testConfigManager).getCoCoList(eq(chapter),testConfigCallbackArgumentCaptor.capture());
        testConfigCallback=testConfigCallbackArgumentCaptor.getValue();
        testConfigCallback.onChaptersLoaded(new ArrayList<Chapter>());
        testConfigCallback.onCOCOLoadError();
        testConfigCallback.onCOCOLoaded(new ArrayList<CommonComponent>());
        verify(view).displayCoCoList(any(ArrayList.class));

    }

    @Test
    public void onEvent() throws Exception {
//        when(cocoListPresenter.getApplicationContext()).thenReturn(appFrameworkApplication);
        when(appFrameworkApplication.getTargetFlowManager()).thenReturn(flowManager);
        when(flowManager.getState(AppStates.TEST_MICROAPP)).thenReturn(baseState);
        when(flowManager.getNextState(baseState,"TestInAppPurhcaseEvent")).thenReturn(newBaseState);
        cocoListPresenter.onEvent(COCOListPresenter.IAP_DEMO_APP);
        verify(newBaseState).navigate(any(UiLauncher.class));
    }

    @Test
    public void getEventState() throws Exception {
        Assert.assertEquals(COCOListPresenter.TEST_IAP_EVENT,cocoListPresenter.getEventState(COCOListPresenter.IAP_DEMO_APP));
        Assert.assertEquals(COCOListPresenter.TEST_APP_INFRA_EVENT,cocoListPresenter.getEventState(COCOListPresenter.APP_INFRA_DEMO_APP));
        Assert.assertEquals(COCOListPresenter.TEST_CC_EVENT,cocoListPresenter.getEventState(COCOListPresenter.CC_DEMO_APP));
        Assert.assertEquals(COCOListPresenter.TEST_DS_EVENT,cocoListPresenter.getEventState(COCOListPresenter.DS_DEMO_APP));
        Assert.assertEquals(COCOListPresenter.TEST_PR_EVENT,cocoListPresenter.getEventState(COCOListPresenter.PRODUCT_REGISTRATION));
        Assert.assertEquals(COCOListPresenter.TEST_BLUE_LIB_DEMO_APP_EVENT,cocoListPresenter.getEventState(COCOListPresenter.BLUE_LIB_DEMO_APP));
        Assert.assertEquals(COCOListPresenter.TEST_DICOMM_EVENT,cocoListPresenter.getEventState(COCOListPresenter.DICOMM_APP));
        Assert.assertEquals(COCOListPresenter.TEST_UR_EVENT,cocoListPresenter.getEventState(COCOListPresenter.USER_REGISTRATION_STANDARD));
        Assert.assertEquals(COCOListPresenter.TEST_UAPP_EVENT,cocoListPresenter.getEventState(COCOListPresenter.UAPP_FRAMEWORK_DEMO));
    }

    @Test
    public void onStateComplete() throws Exception {

    }

    @Test
    public void onEvent1() throws Exception {

    }

    @Test
    public void getApplicationContext() throws Exception {

    }

    @Test
    public void getFragmentLauncher() throws Exception {

    }

}