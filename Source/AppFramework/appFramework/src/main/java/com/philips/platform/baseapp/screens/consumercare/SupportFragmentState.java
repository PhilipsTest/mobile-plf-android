/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.consumercare;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.philips.cdp.digitalcare.CcDependencies;
import com.philips.cdp.digitalcare.CcInterface;
import com.philips.cdp.digitalcare.CcLaunchInput;
import com.philips.cdp.digitalcare.CcSettings;
import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.listeners.CcListener;
import com.philips.cdp.localematch.enums.Catalog;
import com.philips.cdp.localematch.enums.Sector;
import com.philips.cdp.productselection.productselectiontype.ProductModelSelectionType;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.appframework.flowmanager.base.UIStateListener;
import com.philips.platform.appframework.flowmanager.exceptions.ConditionIdNotSetException;
import com.philips.platform.appframework.flowmanager.exceptions.NoConditionFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoEventFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoStateException;
import com.philips.platform.appframework.flowmanager.exceptions.StateIdNotSetException;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.base.AppFrameworkBaseActivity;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class contains all initialization & Launching details of Consumer Care
 */
public class SupportFragmentState extends BaseState implements CcListener {
    final String SUPPORT_PR = "pr";
    private Context activityContext;
    private CcSettings ccSettings;
    private CcLaunchInput ccLaunchInput;
    private FragmentLauncher fragmentLauncher;
    private BaseState baseState;
    private String[] ctnList;
    public SupportFragmentState() {
        super(AppStates.SUPPORT);
    }

    /**
     * BaseState overridden methods
     * @param uiLauncher requires the UiLauncher object
     */
    @Override
    public void navigate(UiLauncher uiLauncher) {
        fragmentLauncher = (FragmentLauncher) uiLauncher;
        this.activityContext = getFragmentActivity();
                /*
        * This method call is required for the scenario, when UR screen launched
        * but login is not done.
        * UR screen itself sets Home country.
        *
        * Lets say UR screen, country is changed to China, but login not done,
        * and user will go to ConsumerCare scree. So this case features should belongs to
        * China only.
        */
        getApplicationContext().determineChinaFlow();

        DigitalCareConfigManager.getInstance().registerCcListener(this);
        ((AppFrameworkBaseActivity)activityContext).handleFragmentBackStack(null,null,getUiStateData().getFragmentLaunchState());
        updateDataModel();
        launchCC();
    }

    public FragmentActivity getFragmentActivity() {
        return fragmentLauncher.getFragmentActivity();
    }

    public AppFrameworkApplication getApplicationContext(){
        return (AppFrameworkApplication) getFragmentActivity().getApplicationContext();
    }

    @Override
    public void init(Context context) {
    }

    //TODO - As per Raymond communication, we should not go to multiple CTNs because this is going to add one more
    //product selection screen. Which is not considered in for testing by Madan's team.
    public void updateDataModel() {
        if(getApplicationContext().isChinaFlow()) {
            String[] ctnList = new String[new ArrayList<>(Arrays.asList(activityContext.getResources().getStringArray(R.array.productselection_ctnlist_china))).size()];
            ctnList = (new ArrayList<>(Arrays.asList(activityContext.getResources().getStringArray(R.array.productselection_ctnlist_china))).toArray(ctnList));
            setCtnList(ctnList);
        }
        else {
            String[] ctnList = new String[new ArrayList<>(Arrays.asList(activityContext.getResources().getStringArray(R.array.productselection_ctnlist))).size()];
            ctnList = (new ArrayList<>(Arrays.asList(activityContext.getResources().getStringArray(R.array.productselection_ctnlist))).toArray(ctnList));
            setCtnList(ctnList);
        }
    }

    public String[] getCtnList() {
        return ctnList;
    }

    public void setCtnList(String[] ctnList) {
        this.ctnList = null;
        this.ctnList = ctnList;
    }
    private void launchCC()
    {

        ProductModelSelectionType productsSelection = new com.philips.cdp.productselection.productselectiontype.HardcodedProductList(getCtnList());
        productsSelection.setCatalog(Catalog.CARE);
        productsSelection.setSector(Sector.B2C);
        final CcInterface ccInterface = new CcInterface();

        if (ccSettings == null) ccSettings = new CcSettings(activityContext);
        if (ccLaunchInput == null) ccLaunchInput = new CcLaunchInput();
        ccLaunchInput.setProductModelSelectionType(productsSelection);
        ccLaunchInput.setConsumerCareListener(this);

        AppInfraInterface appInfraInterface = getApplicationContext().getAppInfra();
        CcDependencies ccDependencies = new CcDependencies(appInfraInterface);

        ccInterface.init(ccDependencies, ccSettings);

        if(getApplicationContext().isChinaFlow()) {
            ccLaunchInput.setLiveChatUrl("http://ph-china.livecom.cn/webapp/index.html?app_openid=ph_6idvd4fj&token=PhilipsTest");
        }
        else{
            ccLaunchInput.setLiveChatUrl(null);
        }

        ccInterface.launch(fragmentLauncher, ccLaunchInput);
    }

    /**
     * Registering for UIStateListener callbacks
     * @param uiStateListener
     */
    public void registerUIStateListener(UIStateListener uiStateListener) {

    }


    /**
     * CcListener interface implementation methods
     * @param s
     * @return
     */
    @Override
    public boolean onMainMenuItemClicked(String s) {
        if (s.equalsIgnoreCase("RA_Product_Registration_Text")) {
            BaseFlowManager targetFlowManager = getApplicationContext().getTargetFlowManager();
            try {
                baseState = targetFlowManager.getNextState(targetFlowManager.getCurrentState(), SUPPORT_PR);
            } catch (NoEventFoundException | NoStateException | NoConditionFoundException | StateIdNotSetException | ConditionIdNotSetException
                    e) {
                Log.d(getClass() + "", e.getMessage());
                Toast.makeText(getFragmentActivity(), getFragmentActivity().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
            }
            if (baseState != null)
                baseState.navigate(new FragmentLauncher(getFragmentActivity(), ((AppFrameworkBaseActivity) getFragmentActivity()).getContainerId(), (ActionBarListener) getFragmentActivity()));
            return true;
        }
        return false;
    }

    @Override
    public boolean onProductMenuItemClicked(String s) {
        return false;
    }

    @Override
    public boolean onSocialProviderItemClicked(String s) {
        return false;
    }
}
