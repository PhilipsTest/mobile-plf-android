/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.consumercare;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import android.widget.Toast;

import com.philips.cdp.digitalcare.CcDependencies;
import com.philips.cdp.digitalcare.CcInterface;
import com.philips.cdp.digitalcare.CcLaunchInput;
import com.philips.cdp.digitalcare.CcSettings;
import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.listeners.CcListener;
import com.philips.cdp.productselection.productselectiontype.ProductModelSelectionType;
import com.philips.cdp.prxclient.PrxConstants;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.appframework.flowmanager.exceptions.ConditionIdNotSetException;
import com.philips.platform.appframework.flowmanager.exceptions.NoConditionFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoEventFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoStateException;
import com.philips.platform.appframework.flowmanager.exceptions.StateIdNotSetException;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.baseapp.base.AbstractAppFrameworkBaseActivity;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.screens.utility.CTNUtil;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;

/**
 * This class contains all initialization & Launching details of Consumer Care
 */
public class SupportFragmentState extends BaseState implements CcListener {
    public static final String TAG = SupportFragmentState.class.getSimpleName();

    final String SUPPORT_PR = "pr";
    private Context activityContext;
    private CcSettings ccSettings;
    private CcLaunchInput ccLaunchInput;
    private FragmentLauncher fragmentLauncher;
    private BaseState baseState;
    private String[] ctnList;
    private CcInterface ccInterface;
    public SupportFragmentState() {
        super(AppStates.SUPPORT);
    }

    /**
     * BaseState overridden methods
     * @param uiLauncher requires the UiLauncher object
     */
    @Override
    public void navigate(UiLauncher uiLauncher) {
        RALog.d(TAG," navigate called ");
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
        ((AbstractAppFrameworkBaseActivity)activityContext).handleFragmentBackStack(null,null,getUiStateData().getFragmentLaunchState());
        updateDataModel();
        launchCC();

        ((AbstractAppFrameworkBaseActivity) activityContext).showOverlayDialog(R.string.RA_DLS_Help_Support_screen_sub, R.mipmap.consumer_care_overlay, SupportFragmentState.TAG);
    }

    public FragmentActivity getFragmentActivity() {
        return fragmentLauncher.getFragmentActivity();
    }

    public AppFrameworkApplication getApplicationContext(){
        return ((AppFrameworkApplication) activityContext.getApplicationContext());
    }

    @Override
    public void init(Context context) {

        activityContext = context;
        ccInterface = getCcInterface();
        if (ccSettings == null) ccSettings = new CcSettings(activityContext);
        if (ccLaunchInput == null) ccLaunchInput = new CcLaunchInput();
        AppInfraInterface appInfraInterface = getApplicationContext().getAppInfra();
        CcDependencies ccDependencies = new CcDependencies(appInfraInterface);
        ccInterface.init(ccDependencies, ccSettings);
    }

    //TODO - As per Raymond communication, we should not go to multiple CTNs because this is going to add one more
    //product selection screen. Which is not considered in for testing by Madan's team.
    public void updateDataModel() {
        RALog.d(TAG," updateDataModel called ");
        String[] ctnList = CTNUtil.getCtnForCountry(getApplicationContext().getAppInfra().getServiceDiscovery().getHomeCountry());
        setCtnList(ctnList);
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
        RALog.d(TAG,"launchCC called ");
        ProductModelSelectionType productsSelection = new com.philips.cdp.productselection.productselectiontype.HardcodedProductList(getCtnList());
        productsSelection.setCatalog(PrxConstants.Catalog.CARE);
        productsSelection.setSector(PrxConstants.Sector.B2C);
        if (ccSettings == null) ccSettings = new CcSettings(activityContext);
        if (ccLaunchInput == null) ccLaunchInput = new CcLaunchInput();
        ccLaunchInput.setProductModelSelectionType(productsSelection);
        ccLaunchInput.setConsumerCareListener(this);

        ccInterface.launch(fragmentLauncher, ccLaunchInput);
    }

    @NonNull
    protected CcInterface getCcInterface() {
        return new CcInterface();
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
                baseState = targetFlowManager.getNextState(targetFlowManager.getState(AppStates.SUPPORT), SUPPORT_PR);
            } catch (NoEventFoundException | NoStateException | NoConditionFoundException | StateIdNotSetException | ConditionIdNotSetException
                    e) {
                RALog.e(TAG , e.getMessage());
                Toast.makeText(getFragmentActivity(), getFragmentActivity().getString(R.string.RA_something_wrong), Toast.LENGTH_SHORT).show();
            }
            if (baseState != null)
                baseState.navigate(new FragmentLauncher(getFragmentActivity(), ((AbstractAppFrameworkBaseActivity) getFragmentActivity()).getContainerId(), (ActionBarListener) getFragmentActivity()));
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
