/*
 *  Copyright (c) Koninklijke Philips N.V., 2017
 *  All rights are reserved. Reproduction or dissemination
 *  in whole or in part is prohibited without the prior written
 *  consent of the copyright holder.
 */

package com.philips.platform.appframework.ui;

import android.content.Context;
import androidx.annotation.NonNull;

import com.philips.platform.appframework.data.TestConfigManager;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.appframework.flowmanager.base.UIStateListener;
import com.philips.platform.appframework.flowmanager.exceptions.ConditionIdNotSetException;
import com.philips.platform.appframework.flowmanager.exceptions.NoConditionFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoEventFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoStateException;
import com.philips.platform.appframework.flowmanager.exceptions.StateIdNotSetException;
import com.philips.platform.appframework.models.Chapter;
import com.philips.platform.appframework.models.CommonComponent;
import com.philips.platform.baseapp.base.AbstractUIBasePresenter;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.base.FragmentView;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.uappframework.launcher.FragmentLauncher;

import java.util.ArrayList;


/**
 * Created by philips on 13/02/17.
 */

public class COCOListPresenter extends AbstractUIBasePresenter implements COCOListContract.UserActionsListener, UIStateListener {

    public static final String USER_REGISTRATION_STANDARD = "Test User Registration Demo";
    public static final String PRODUCT_REGISTRATION = "Test Product Registration Demo";
    public static final String IAP_DEMO_APP = "Test In App Purchase Demo";
    public static final String MEC_DEMO_APP = "Test In App Purchase 2.0 Demo";
    public static final String ECS_DEMO_TEST_APP = "Test ECS Test Demo";
    public static final String THS_DEMO_APP = "Test THS Demo";
    public static final String PIM_DEMO_APP = "Test PIM Demo";
    public static final String CCB_DEMO_APP = "Test CCB Demo";
    public static final String NEURA_DEMO_APP = "Test Neura Demo";
    public static final String USER_REGISTRATION_COPPA = "User Registration(Coppa)";
    public static final String DS_DEMO_APP = "Test Data Services Demo";
    public static final String MYA_DEMO_APP = "Test My Account Service Demo";
    public static final String CC_DEMO_APP = "Test Consumer Care Demo";
//    public static final String DLS_DEMO_APP = "Test DLS Demo";
    public static final String UAPP_FRAMEWORK_DEMO = "Test uApp Demo";
    public static final String DICOMM_APP = "Test ConArtist Demo";
    public static final String BLUE_LIB_DEMO_APP = "Test ShineLib Demo";
    public static final String REFERENCE_NODE_CONNECTIVITY = "Reference Node Connectivity";
    public static final String EASY_WIFI_SETUP = "Easy Wifi Setup";
    public static final String DEMO_APP_INFRA = "Test App Infra Demo";
    public static final String DEVICE_PAIRING_DEMO_APP = "Test Device Pairing Demo";
    public static final String TEST_DS_EVENT = "TestDataServicesEvent";
    public static final String TEST_MYA_EVENT = "TestMyAccountServiceEvent";
    public static final String TEST_IAP_EVENT = "TestInAppPurhcaseEvent";
    public static final String TEST_MEC_EVENT = "TestMobileEcommerceEvent";
    public static final String TEST_ECS_TEST_EVENT = "TestECSTestEvent";
    public static final String TEST_PIM_DEMO_EVENT = "TestPimDemoEvent";
    public static final String TEST_CCB_DEMO_EVENT = "TestCCBDemoEvent";
    public static final String TEST_PR_EVENT = "TestProductRegistrationEvent";
    public static final String TEST_CC_EVENT = "TestConsumerCareEvent";
    public static final String TEST_UR_EVENT = "TestUserRegistrationEvent";
    public static final String TEST_DICOMM_EVENT = "TestDicommClientEvent";
    public static final String TEST_BLUE_LIB_DEMO_APP_EVENT = "TestBlueLibEvent";
    public static final String TEST_REFERENCE_NODE_CONNECTIVITY = "connectivity";
    public static final String TEST_EWS = "ews";
    public static final String TEST_DEVICE_PAIRING_EVENT = "device_pairing";
    public static final String TEST_APP_INFRA_EVENT = "AppInfra";
//    public static final String TEST_DLS_APP = "DLS";
    public static final String TEST_THS_DEMO_EVENT = "TestThsDemoEvent";
//    public static final String TEST_NEURA_DEMO_EVENT = "TestNeuraDemoEvent";
    private static final String TAG = COCOListPresenter.class.getSimpleName();
    public final COCOListContract.View cocoListContractView;
    private BaseState baseState;

    private FragmentView view;

    private Context context;

    private TestConfigManager testConfigManager;

    public COCOListPresenter(FragmentView view, TestConfigManager testConfigManager, Context context, COCOListContract.View cocoListContractView) {
        super(view);
        this.view = view;
        this.cocoListContractView = cocoListContractView;
        this.context = context;
        this.testConfigManager = testConfigManager;
    }


    @Override
    public void loadCoCoList(Chapter chapter) {
        testConfigManager.getCoCoList(chapter, new TestConfigManager.TestConfigCallback() {
            @Override
            public void onChaptersLoaded(ArrayList<Chapter> chaptersList) {

            }

            @Override
            public void onCOCOLoaded(ArrayList<CommonComponent> commonComponentsList) {
                cocoListContractView.displayCoCoList(commonComponentsList);
            }

            @Override
            public void onCOCOLoadError() {

            }
        });
    }

    public void onEvent(String cocoName) {
        String eventState = getEventState(cocoName);
        RALog.d(TAG, "Event state:" + eventState);
        try {
            BaseFlowManager targetFlowManager = getApplicationContext().getTargetFlowManager();
            baseState = targetFlowManager.getNextState(targetFlowManager.getState(AppStates.TEST_MICROAPP), eventState);

            if (baseState != null) {
                    baseState.init(context);
                    baseState.navigate(getFragmentLauncher());
//                }
            }
        } catch (NoEventFoundException | NoStateException | NoConditionFoundException | StateIdNotSetException | ConditionIdNotSetException
                e) {
            RALog.d(getClass() + "", e.getMessage());
        }
    }

    protected String getEventState(String cocoName) {
        switch (cocoName) {
            case IAP_DEMO_APP:
                return TEST_IAP_EVENT;
            case MEC_DEMO_APP:
                return TEST_MEC_EVENT;
            case ECS_DEMO_TEST_APP:
                return TEST_ECS_TEST_EVENT;
            case DS_DEMO_APP:
                return TEST_DS_EVENT;
            case MYA_DEMO_APP:
                return TEST_MYA_EVENT;
            case PRODUCT_REGISTRATION:
                return TEST_PR_EVENT;
            case USER_REGISTRATION_STANDARD:
                return TEST_UR_EVENT;
            case CC_DEMO_APP:
                return TEST_CC_EVENT;
            case DICOMM_APP:
                return TEST_DICOMM_EVENT;
            case BLUE_LIB_DEMO_APP:
                return TEST_BLUE_LIB_DEMO_APP_EVENT;
            case REFERENCE_NODE_CONNECTIVITY:
                return TEST_REFERENCE_NODE_CONNECTIVITY;
            case EASY_WIFI_SETUP:
                return TEST_EWS;
            case DEVICE_PAIRING_DEMO_APP:
                return TEST_DEVICE_PAIRING_EVENT;
            case DEMO_APP_INFRA:
                return TEST_APP_INFRA_EVENT;
            case CCB_DEMO_APP:
                return TEST_CCB_DEMO_EVENT;
//            case DLS_DEMO_APP:
//                return TEST_DLS_APP;
            case THS_DEMO_APP:
                return TEST_THS_DEMO_EVENT;
            /*case NEURA_DEMO_APP:
                return TEST_NEURA_DEMO_EVENT;*/
            case PIM_DEMO_APP:
                return TEST_PIM_DEMO_EVENT;
            default:
                return TEST_APP_INFRA_EVENT;
        }
    }

    
    @Override
    public void onStateComplete(BaseState baseState) {

    }

    @Override
    public void onEvent(int componentID) {

    }

    protected AppFrameworkApplication getApplicationContext() {
        return (AppFrameworkApplication) view.getFragmentActivity().getApplicationContext();
    }

    @NonNull
    protected FragmentLauncher getFragmentLauncher() {
        return new FragmentLauncher(view.getFragmentActivity(), view.getContainerId(), view.getActionBarListener());
    }
}
