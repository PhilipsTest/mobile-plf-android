/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.modularui.stateimpl;

import android.content.Context;
import android.widget.Toast;

import com.philips.cdp.di.iap.integration.IAPDependencies;
import com.philips.cdp.di.iap.integration.IAPFlowInput;
import com.philips.cdp.di.iap.integration.IAPInterface;
import com.philips.cdp.di.iap.integration.IAPLaunchInput;
import com.philips.cdp.di.iap.integration.IAPSettings;
import com.philips.cdp.di.iap.session.IAPListener;
import com.philips.platform.appframework.AppFrameworkApplication;
import com.philips.platform.appframework.AppFrameworkBaseActivity;
import com.philips.platform.modularui.statecontroller.BaseState;
import com.philips.platform.modularui.statecontroller.UIStateData;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;

import java.util.ArrayList;

/**
 * This class contains all initialization & Launching details of IAP
 */
public class IAPState extends BaseState {

    /**
     IAP flow constants, values for IAP views should start from 4000 series
     */
    public static final int IAP_CATALOG_VIEW = 4001;
    public static final int IAP_PURCHASE_HISTORY_VIEW = 4002;
    public static final int IAP_SHOPPING_CART_VIEW = 4003;
    private Context activityContext;
    private Context applicationContext;
    private IAPInterface iapInterface;
    private FragmentLauncher fragmentLauncher;

    public IAPState() {
        super(BaseState.UI_IAP_SHOPPING_FRAGMENT_STATE);
    }

    public IAPInterface getIapInterface() {
        return iapInterface;
    }

    @Override
    public void navigate(UiLauncher uiLauncher) {
        fragmentLauncher = (FragmentLauncher) uiLauncher;
        activityContext = fragmentLauncher.getFragmentActivity();
        ((AppFrameworkBaseActivity)activityContext).handleFragmentBackStack(null,null,getUiStateData().getFragmentLaunchState());
        launchIAP();
    }

    private int getIAPFlowType(int iapFlowType){
        switch (iapFlowType){
            case IAPState.IAP_CATALOG_VIEW:return IAPLaunchInput.IAPFlows.IAP_PRODUCT_CATALOG_VIEW;
            case IAPState.IAP_PURCHASE_HISTORY_VIEW:return IAPLaunchInput.IAPFlows.IAP_PURCHASE_HISTORY_VIEW;
            case IAPState.IAP_SHOPPING_CART_VIEW:return IAPLaunchInput.IAPFlows.IAP_SHOPPING_CART_VIEW;
            default:return IAPState.IAP_CATALOG_VIEW;
        }
    }
    private void launchIAP() {
        IAPInterface iapInterface = ((AppFrameworkApplication) activityContext.getApplicationContext()).getIap().getIapInterface();
        IAPFlowInput iapFlowInput = new IAPFlowInput(((InAppStateData)getUiStateData()).getCtnList());
        IAPLaunchInput iapLaunchInput = new IAPLaunchInput();
        iapLaunchInput.setIAPFlow(getIAPFlowType(((InAppStateData)getUiStateData()).getIapFlow()), iapFlowInput);
        iapLaunchInput.setIapListener((IAPListener) fragmentLauncher.getFragmentActivity());
        try {
            iapInterface.launch(fragmentLauncher, iapLaunchInput);

        } catch (RuntimeException e) {
            //TODO: Deepthi - M -  not to show toast msg from exception, we need to defined string messages for all errors
            Toast.makeText(activityContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void init(Context context) {
        applicationContext=context;
        iapInterface = new IAPInterface();
        IAPSettings iapSettings = new IAPSettings(applicationContext);
        IAPDependencies iapDependencies = new IAPDependencies(AppFrameworkApplication.appInfra);
        iapSettings.setUseLocalData(false);
        iapInterface.init(iapDependencies, iapSettings);
    }

    /**
     * Data Model for CoCo is defined here to have minimal import files.
     */
    public class InAppStateData extends UIStateData {

        private int iapFlow;
        private ArrayList<String> ctnList = null;

        public ArrayList<String> getCtnList() {
            return ctnList;
        }

        public void setCtnList(ArrayList<String> ctnList) {
            this.ctnList = ctnList;
        }

        public int getIapFlow() {
            return iapFlow;
        }

        public void setIapFlow(int iapFlow) {
            this.iapFlow = iapFlow;
        }

    }
}
