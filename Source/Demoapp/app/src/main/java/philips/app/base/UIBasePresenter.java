/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package philips.app.base;

import com.philips.platform.appframework.flowmanager.base.UIStateData;

import flowmanager.AppStates;
import flowmanager.screens.utility.Constants;

/**
 * This class aims to handle events inside the states and also events when a particular state is loaded
 */
abstract public class UIBasePresenter {
    /*Event ID */
    protected static final int MENU_OPTION_HOME = 0;
    /*protected final int MENU_OPTION_SETTINGS = 1;
    protected final int MENU_OPTION_SHOP = 2;
    protected final int MENU_OPTION_SUPPORT = 3;*/
    protected final int MENU_OPTION_ABOUT = 1;
   /* protected final int MENU_OPTION_DATA_SYNC = 5;
    protected final int MENU_OPTION_PR = 7;
    protected final int MENU_OPTION_CONNECTIVITY = 6;*/

    /* event to state map */
    protected final String HOME_SETTINGS = "settings";
    protected final String HOME_IAP = "iap";
    protected final String HOME_SUPPORT = "support";
    protected final String SHOPPING_CART = "shopping_cart";
    protected final String HOME_ABOUT = "about";
    protected final String HOME_FRAGMENT = "home_fragment";
    protected final String HOME_DATA_SYNC = "data_sync";
    protected final String SUPPORT_PR = "pr";
    protected final String CONNECTIVITY = "connectivity";

    private UIView uiView;

    public UIBasePresenter(final UIView uiView) {
        this.uiView = uiView;
    }

    /**
     * The onclick of objects in a particular state can be defined here
     * @param componentID The Id of any button or widget or any other component
     *
     */
    public abstract void onEvent(int componentID);

    /**
     * For seeting the current state , so that flow manager is updated with current state
     * @param stateID requires AppFlowState ID
     */
    public void setState(String stateID){

    }

    protected UIStateData setStateData(final String componentID) {
        switch (componentID) {
            case AppStates.HOME_FRAGMENT:
                UIStateData homeStateData = new UIStateData();
                homeStateData.setFragmentLaunchType(Constants.ADD_HOME_FRAGMENT);
                return homeStateData;

            case AppStates.ABOUT:
                UIStateData aboutStateData = new UIStateData();
                aboutStateData.setFragmentLaunchType(Constants.ADD_FROM_HAMBURGER);
                return aboutStateData;
            // Commented as part of Plan A removal.
            /*case Constants.UI_SHOPPING_CART_BUTTON_CLICK:
                IAPState.InAppStateData uiStateDataModel = new IAPState().new InAppStateData();
                uiStateDataModel.setIapFlow(IAPState.IAP_SHOPPING_CART_VIEW);
                uiStateDataModel.setCtnList(getCtnList());
                return uiStateDataModel;*/
            /*case AppStates.PR:
                UIStateData prStateDataModel = new UIStateData();
                return prStateDataModel;
            case AppStates.DATA_SYNC:
                UIStateData syncStateData = new UIStateData();
                syncStateData.setFragmentLaunchType(Constants.ADD_FROM_HAMBURGER);
                return syncStateData;
            case AppStates.CONNECTIVITY:
                UIStateData connectivityStateData = new UIStateData();
                connectivityStateData.setFragmentLaunchType(Constants.ADD_FROM_HAMBURGER);
                return connectivityStateData;*/
            default:
                homeStateData = new UIStateData();
                homeStateData.setFragmentLaunchType(Constants.ADD_HOME_FRAGMENT);
                return homeStateData;
        }
    }
}
