package com.philips.platform.appframework.testmicroappfw.ui;

import android.content.Context;
import android.support.annotation.NonNull;

import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.appframework.flowmanager.base.UIStateListener;
import com.philips.platform.appframework.flowmanager.exceptions.ConditionIdNotSetException;
import com.philips.platform.appframework.flowmanager.exceptions.NoConditionFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoEventFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoStateException;
import com.philips.platform.appframework.flowmanager.exceptions.StateIdNotSetException;
import com.philips.platform.appframework.testmicroappfw.data.TestConfigManager;
import com.philips.platform.appframework.testmicroappfw.models.Chapter;
import com.philips.platform.appframework.testmicroappfw.models.CommonComponent;
import com.philips.platform.baseapp.base.AbstractUIBasePresenter;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.base.FragmentView;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.uappframework.launcher.FragmentLauncher;

import java.util.ArrayList;


/**
 * Created by philips on 13/02/17.
 */

public class COCOListPresenter extends AbstractUIBasePresenter implements COCOListContract.UserActionsListener,UIStateListener{

    private static final  String TAG=COCOListPresenter.class.getSimpleName();

    private static final String UAPP_FRAMEWORK_DEMO = "Micro App Framework";
    private static final String USER_REGISTRATION_STANDARD = "User Registration(Standard)";
    private static final String USER_REGISTRATION_COPPA = "User Registration(Coppa)";
    private static final String DS_DEMO_APP="Data Services";
    private static final String CC_DEMO_APP = "Consumer Care";
    private static final String DLS_DEMO_APP = "DLS";

    private static final String TEST_DS_EVENT="TestDataServicesEvent";
    private static final String TEST_IAP_EVENT="TestInAppPurhcaseEvent";
    private static final String TEST_PR_EVENT="TestProductRegistrationEvent";
    private static final String TEST_CC_EVENT="TestConsumerCareEvent";
    private static final String TEST_UAPP_EVENT="TestUAPPFrameworkEvent";
    private static final String TEST_UR_EVENT="TestUserRegistrationEvent";
    private static final String TEST_APP_INFRA_EVENT="TestAppInfraEvent";
    private final COCOListContract.View cocoListContractView;

    private BaseState baseState;

    private FragmentView view;

    private Context context;

    private TestConfigManager testConfigManager;

    private static final String IAP_DEMO_APP="In App Purchase";

    private static final String APP_INFRA_DEMO_APP="App Infra";

    private static final String PRODUCT_REGISTRATION="Product Registration";


    public COCOListPresenter(FragmentView view, TestConfigManager testConfigManager, Context context,COCOListContract.View cocoListContractView) {
        super(view);
        this.view = view;
        this.cocoListContractView=cocoListContractView;
        this.context = context;
        this.testConfigManager=testConfigManager;
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
        RALog.d(TAG,"Event state:"+eventState);
        try {
            BaseFlowManager targetFlowManager = getApplicationContext().getTargetFlowManager();
            baseState = targetFlowManager.getNextState(targetFlowManager.getState(AppStates.TEST_MICROAPP),eventState);

            if (baseState != null) {
                baseState.init(context.getApplicationContext());
                baseState.navigate(getFragmentLauncher());
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
            case DS_DEMO_APP:
                return TEST_DS_EVENT;
            case PRODUCT_REGISTRATION:
                return TEST_PR_EVENT;
            case USER_REGISTRATION_STANDARD:
                return TEST_UR_EVENT;
            case CC_DEMO_APP:
                return TEST_CC_EVENT;
            case UAPP_FRAMEWORK_DEMO:
                return TEST_UAPP_EVENT;
            case APP_INFRA_DEMO_APP:
                return TEST_APP_INFRA_EVENT;
            default:
                return APP_INFRA_DEMO_APP;
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
