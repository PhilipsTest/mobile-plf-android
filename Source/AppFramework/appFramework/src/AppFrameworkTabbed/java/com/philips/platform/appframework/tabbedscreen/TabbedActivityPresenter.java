/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.appframework.tabbedscreen;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.philips.platform.appframework.R;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.base.FragmentView;
import com.philips.platform.baseapp.base.UIBasePresenter;
import com.philips.platform.baseapp.base.UIStateData;
import com.philips.platform.baseapp.screens.utility.Constants;
import com.philips.platform.uappframework.launcher.FragmentLauncher;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class id used for loading various fragments that are supported by home activity ,
 * based on user selection this class loads the next state of the application.
 */
public class TabbedActivityPresenter extends UIBasePresenter{

    /* event to state map */
    final String HOME_SETTINGS = "settings";
    final String HOME_IAP = "iap";
    final String HOME_SUPPORT = "support";
    final String SHOPPING_CART = "shopping_cart";
    final String HOME_ABOUT = "about";
    final String HOME_FRAGMENT = "home_fragment";
    final String HOME_DATA_SYNC = "data_sync";
    final String SUPPORT_PR = "pr";
    /*Event ID */
    private final int MENU_OPTION_HOME = 0;
    private final int MENU_OPTION_SETTINGS = 1;
    private final int MENU_OPTION_SHOP = 2;
    private final int MENU_OPTION_SUPPORT = 3;
    private final int MENU_OPTION_ABOUT = 4;
    private final int MENU_OPTION_DATA_SYNC = 5;
    private final int MENU_OPTION_PR = 6;
    private FragmentView fragmentView;
    private AppFrameworkApplication appFrameworkApplication;
    private FragmentLauncher fragmentLauncher;
    private BaseState baseState;

    public TabbedActivityPresenter(final FragmentView fragmentView) {
        super(fragmentView);
        this.fragmentView = fragmentView;
        setState(AppStates.TAB_HOME);
    }

    /**
     * This methods handles all click events done on hamburger menu
     * Any changes for hamburger menu options should be made here
     */
    @Override
    public void onEvent(int componentID) {
        appFrameworkApplication = getApplicationContext();
        String eventState = getEventState(componentID);
        BaseFlowManager targetFlowManager = getApplicationContext().getTargetFlowManager();
        baseState = targetFlowManager.getNextState(targetFlowManager.getState(AppStates.TAB_HOME), eventState);
        baseState.setStateListener(this);
        baseState.setUiStateData(setStateData(componentID));
        fragmentLauncher = getFragmentLauncher();
        baseState.navigate(fragmentLauncher);
    }

    protected UIStateData setStateData(final int componentID) {
        switch (componentID) {
            case MENU_OPTION_HOME:
                UIStateData homeStateData = new UIStateData();
                homeStateData.setFragmentLaunchType(Constants.ADD_HOME_FRAGMENT);
                return homeStateData;
            case MENU_OPTION_SETTINGS:
                UIStateData settingsStateData = new UIStateData();
                settingsStateData.setFragmentLaunchType(Constants.ADD_FROM_HAMBURGER);
                return settingsStateData;
            case MENU_OPTION_SHOP:
                UIStateData iapStateData = new UIStateData();
                iapStateData.setFragmentLaunchType(Constants.CLEAR_TILL_HOME);
                return iapStateData;
            case MENU_OPTION_SUPPORT:
                UIStateData supportStateData = new UIStateData();
                supportStateData.setFragmentLaunchType(Constants.CLEAR_TILL_HOME);
                return supportStateData;
            case MENU_OPTION_ABOUT:
                UIStateData aboutStateData = new UIStateData();
                aboutStateData.setFragmentLaunchType(Constants.ADD_FROM_HAMBURGER);
                return aboutStateData;
            // Commented as part of Plan A removal.
            /*case Constants.UI_SHOPPING_CART_BUTTON_CLICK:
                IAPState.InAppStateData uiStateDataModel = new IAPState().new InAppStateData();
                uiStateDataModel.setIapFlow(IAPState.IAP_SHOPPING_CART_VIEW);
                uiStateDataModel.setCtnList(getCtnList());
                return uiStateDataModel;*/
            case MENU_OPTION_PR:
                UIStateData prStateDataModel = new UIStateData();
                return prStateDataModel;
            case MENU_OPTION_DATA_SYNC:
                UIStateData syncStateData = new UIStateData();
                syncStateData.setFragmentLaunchType(Constants.ADD_FROM_HAMBURGER);
                return syncStateData;
            default:
                homeStateData = new UIStateData();
                homeStateData.setFragmentLaunchType(Constants.ADD_HOME_FRAGMENT);
                return homeStateData;
        }
    }

    public void showFragment(Fragment fragment, String fragmentTag) {
        int containerId = R.id.frame_container;

        try {
            FragmentTransaction fragmentTransaction = fragmentView.getFragmentActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(containerId, fragment, fragmentTag);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            //Logger.e(TAG, "IllegalStateException" + e.getMessage());
            e.printStackTrace();
        }
    }

    @NonNull
    protected ArrayList<String> getCtnList() {
        return new ArrayList<>(Arrays.asList(fragmentView.getFragmentActivity().getResources().getStringArray(R.array.iap_productselection_ctnlist)));
    }

    protected AppFrameworkApplication getApplicationContext() {
        return (AppFrameworkApplication) fragmentView.getFragmentActivity().getApplicationContext();
    }

    protected FragmentLauncher getFragmentLauncher() {
        fragmentLauncher = new FragmentLauncher(fragmentView.getFragmentActivity(), fragmentView.getContainerId(), fragmentView.getActionBarListener());
        return fragmentLauncher;
    }


    // TODO: Deepthi, is this expected? deviation from ios i think.
    private String getEventState(int componentID) {

        switch (componentID) {
            case MENU_OPTION_HOME:
                return HOME_FRAGMENT;
            case MENU_OPTION_SETTINGS:
                return HOME_SETTINGS;
            case MENU_OPTION_SHOP:
                return HOME_IAP;
            case MENU_OPTION_SUPPORT:
                return HOME_SUPPORT;
            case MENU_OPTION_ABOUT:
                return HOME_ABOUT;
            // Commented as part of Plan A removal.
           /* case Constants.UI_SHOPPING_CART_BUTTON_CLICK:
                return SHOPPING_CART;*/
            case MENU_OPTION_PR:
                return SUPPORT_PR;
            case MENU_OPTION_DATA_SYNC:
                return  HOME_DATA_SYNC;
            default:
                return HOME_FRAGMENT;
        }
    }
}
