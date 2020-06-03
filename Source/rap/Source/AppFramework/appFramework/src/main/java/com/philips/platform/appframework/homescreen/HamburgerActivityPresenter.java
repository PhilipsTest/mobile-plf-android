/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appframework.homescreen;

import android.widget.Toast;

import com.philips.platform.appframework.R;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.appframework.flowmanager.exceptions.ConditionIdNotSetException;
import com.philips.platform.appframework.flowmanager.exceptions.NoConditionFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoEventFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoStateException;
import com.philips.platform.appframework.flowmanager.exceptions.StateIdNotSetException;
import com.philips.platform.baseapp.base.AbstractUIBasePresenter;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.base.FragmentView;
import com.philips.platform.baseapp.screens.utility.Constants;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.uappframework.launcher.FragmentLauncher;

/**
 * This class id used for loading various fragments that are supported by home activity ,
 * based on user selection this class loads the next state of the application.
 */
public class HamburgerActivityPresenter extends AbstractUIBasePresenter {
    public static final String TAG = HamburgerActivityPresenter.class.getSimpleName();

    private FragmentView fragmentView;
    private FragmentLauncher fragmentLauncher;
    private BaseState baseState;

    public HamburgerActivityPresenter(final FragmentView fragmentView) {
        super(fragmentView);
        this.fragmentView = fragmentView;
        setState(AppStates.HAMBURGER_HOME);
    }

    /**
     * This methods handles all click events done on hamburger menu
     * Any changes for hamburger menu options should be made here
     */
    @Override
    public void onEvent(int componentID) {
        String eventState = getEventState(componentID);

        try {
            BaseFlowManager targetFlowManager = getApplicationContext().getTargetFlowManager();
            if (targetFlowManager == null) {
                Toast.makeText(fragmentView.getFragmentActivity(), fragmentView.getFragmentActivity().getString(R.string.RA_something_wrong), Toast.LENGTH_SHORT).show();
                return;
            }
            baseState = targetFlowManager.getNextState(targetFlowManager.getState(AppStates.HAMBURGER_HOME), eventState);
            if (null != baseState) {
                baseState.setUiStateData(setStateData(baseState.getStateID()));
                fragmentLauncher = getFragmentLauncher();
                baseState.init(fragmentLauncher.getFragmentActivity().getApplicationContext());
                baseState.navigate(fragmentLauncher);
            }
        } catch (NoEventFoundException | NoStateException | NoConditionFoundException | StateIdNotSetException | ConditionIdNotSetException
                e) {
            RALog.d(TAG, e.getMessage());
            Toast.makeText(fragmentView.getFragmentActivity(), fragmentView.getFragmentActivity().getString(R.string.RA_something_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    protected AppFrameworkApplication getApplicationContext() {
        return (AppFrameworkApplication) fragmentView.getFragmentActivity().getApplicationContext();
    }

    protected FragmentLauncher getFragmentLauncher() {
        fragmentLauncher = new FragmentLauncher(fragmentView.getFragmentActivity(), fragmentView.getContainerId(), fragmentView.getActionBarListener());
        return fragmentLauncher;
    }

    // TODO: Deepthi, is this expected? deviation from ios i think. - (Rakesh -As disscussed. Needed to convert int ID for views into Strings )
    public String getEventState(int componentID) {

        switch (componentID) {
            case MENU_OPTION_HOME:
                return HOME_FRAGMENT;
            case MENU_OPTION_SHOP:
                return HOME_IAP;
            case MENU_OPTION_SUPPORT:
                return HOME_SUPPORT;
            case MENU_OPTION_ABOUT:
                return HOME_ABOUT;
            case MENU_OPTION_SELECT_COUNTRY:
                return HOME_COUNTRY_SELECTION;
            case Constants.SHOPPING_CART_CLICK:
                return SHOPPING_CART;
            case MENU_OPTION_TEST_MICROAPP:
                return HOME_TEST_MICROAPP;
            case Constants.LOGIN_BUTTON_CLICK_CONSTANT:
                return HAMBURGER_LOGIN;
            case Constants.LOGOUT_BUTTON_CLICK_CONSTANT:
                return HAMBURGER_LOGOUT;
            case Constants.HAMBURGER_MY_ACCOUNT_CLICK:
                return HOME_MYACCOUNT;
            case Constants.THS_DEEP_LINKING_EVENT_ID:
                return Constants.THS_DEEP_LINKING_EVENT;
            default:
                return HOME_FRAGMENT;
        }
    }
}
