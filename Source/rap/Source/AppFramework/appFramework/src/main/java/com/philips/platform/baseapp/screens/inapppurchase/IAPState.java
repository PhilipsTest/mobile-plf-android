/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.inapppurchase;

import android.content.Context;
import android.widget.Toast;

import com.philips.cdp.di.iap.integration.IAPDependencies;
import com.philips.cdp.di.iap.integration.IAPFlowInput;
import com.philips.cdp.di.iap.integration.IAPInterface;
import com.philips.cdp.di.iap.integration.IAPLaunchInput;
import com.philips.cdp.di.iap.integration.IAPListener;
import com.philips.cdp.di.iap.integration.IAPSettings;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.appframework.homescreen.HamburgerActivity;
import com.philips.platform.baseapp.base.AbstractAppFrameworkBaseActivity;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.screens.utility.IndexSelectionListener;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;

import java.util.ArrayList;

import static com.philips.cdp.di.iap.utils.IAPConstant.IAP_ERROR_AUTHENTICATION_FAILURE;
import static com.philips.cdp.di.iap.utils.IAPConstant.IAP_ERROR_CONNECTION_TIME_OUT;
import static com.philips.cdp.di.iap.utils.IAPConstant.IAP_ERROR_INVALID_CTN;
import static com.philips.cdp.di.iap.utils.IAPConstant.IAP_ERROR_NO_CONNECTION;

/**
 * This class contains all initialization & Launching details of IAP
 */
public abstract class IAPState extends BaseState implements IAPListener {
    public static final String TAG =  IAPState.class.getSimpleName();

    /**
     IAP flow constants, values for IAP views should start from 4000 series
     */
    public static final int IAP_CATALOG_VIEW = 4001;
    protected int launchType;
    protected ArrayList<String> ctnList = null;
    public static final int IAP_PURCHASE_HISTORY_VIEW = 4002;
    public static final int IAP_SHOPPING_CART_VIEW = 4003;
    protected Context activityContext;
    private Context applicationContext;
    private IAPInterface iapInterface;
    protected FragmentLauncher fragmentLauncher;
    private boolean isCartVisible = false;
    IAPSettings iapSettings;

    public IAPState() {
        super(AppStates.IAP);
    }

    public IAPInterface getIapInterface() {
        return iapInterface;
    }

    @Override
    public void navigate(UiLauncher uiLauncher) {
        RALog.d(TAG," navigate ");
        fragmentLauncher = (FragmentLauncher) uiLauncher;
        activityContext = fragmentLauncher.getFragmentActivity();
        ((AbstractAppFrameworkBaseActivity)activityContext).handleFragmentBackStack(null,null,getUiStateData().getFragmentLaunchState());
        setLaunchType(IAPState.IAP_CATALOG_VIEW);
        updateDataModel();
        if (getApplicationContext().isShopingCartVisible) {         // for hybris flow
            setListener();
        }else {
            launchIAP();
        }
    }

    private int getIAPFlowType(int iapFlowType) {
        switch (iapFlowType) {
            case IAPState.IAP_CATALOG_VIEW:
                return IAPLaunchInput.IAPFlows.IAP_PRODUCT_CATALOG_VIEW;
            case IAPState.IAP_PURCHASE_HISTORY_VIEW:
                return IAPLaunchInput.IAPFlows.IAP_PURCHASE_HISTORY_VIEW;
            case IAPState.IAP_SHOPPING_CART_VIEW:
                return IAPLaunchInput.IAPFlows.IAP_SHOPPING_CART_VIEW;
            default:
                return IAPLaunchInput.IAPFlows.IAP_PRODUCT_CATALOG_VIEW;
        }
    }


    public ArrayList<String> getCtnList() {
        final boolean shopingCartVisible = ((AppFrameworkApplication) applicationContext).isShopingCartVisible();
        if (!shopingCartVisible) {
            return ctnList;
        } else {
            return ((ArrayList<String>)null);
        }
    }

    public void setCtnList(ArrayList<String> ctnList) {
        this.ctnList = ctnList;
    }

    public int getLaunchType() {
        return launchType;
    }

    public void setLaunchType(int launchType) {
        this.launchType = getIAPFlowType(launchType);
    }

    protected void launchIAP() {
        RALog.d(TAG," launchIAP ");
        IAPInterface iapInterface = getApplicationContext().getIap().getIapInterface();
        IAPFlowInput iapFlowInput = new IAPFlowInput(getCtnList());
        IAPLaunchInput iapLaunchInput = new IAPLaunchInput();
        iapLaunchInput.setIAPFlow(getLaunchType(), iapFlowInput,null);
        iapLaunchInput.setIapListener(this);
        try {
            ((AbstractAppFrameworkBaseActivity) activityContext).hideProgressBar();
            iapInterface.launch(fragmentLauncher, iapLaunchInput);
            shouldOverBeVisible();
        } catch (RuntimeException e) {
            //TODO: Deepthi - M -  not to show toast msg from exception, we need to defined string messages for all errors - (Had sent mail to Thiru long time ago. NO response. Will send another one to Bopanna)
            RALog.e(TAG,e.getMessage());
            if (activityContext instanceof IndexSelectionListener) {
                ((IndexSelectionListener) activityContext).updateSelectionIndex(0);
            }
            Toast.makeText(activityContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void shouldOverBeVisible() {
        final boolean isShopingCartVisible = ((AppFrameworkApplication) activityContext.getApplicationContext()).isShopingCartVisible;
        if(!isShopingCartVisible) {
            ((AbstractAppFrameworkBaseActivity) fragmentLauncher.getFragmentActivity()).showOverlayDialog(R.string.RA_DLS_Help_Philips_Shop,
                    R.mipmap.philips_shop_overlay, IAPState.TAG);
        }
    }

    protected AppFrameworkApplication getApplicationContext() {
        if(activityContext != null) {
            return (AppFrameworkApplication) activityContext.getApplicationContext();
        }else{
            return null;
        }
    }

    @Override
    public void init(Context context) {
        RALog.d(TAG," init IAP ");
        applicationContext = context;
        iapInterface = new IAPInterface();
        iapSettings = new IAPSettings(applicationContext);
        IAPDependencies iapDependencies = new IAPDependencies(((AppFrameworkApplication)applicationContext).getAppInfra(),((AppFrameworkApplication) applicationContext).getUserRegistrationState().getUserDataInterface());
        try {
            iapInterface.init(iapDependencies, iapSettings);
        }catch (RuntimeException ex){
            RALog.d(TAG,ex.getMessage());
        }
    }

    public void isCartVisible() {
        iapInterface.isCartVisible(this);
    }

    public void setListener() {
            RALog.d(TAG, "Setting Listener");
            ((AbstractAppFrameworkBaseActivity) activityContext).showProgressBar();
            getApplicationContext().getIap().getIapInterface().getCompleteProductList(this);
    }
    @Override
    public void onGetCartCount(int i) {
        ((HamburgerActivity) activityContext).cartIconVisibility(i);
    }

    @Override
    public void onUpdateCartCount() {

    }

    @Override
    public void updateCartIconVisibility(boolean b) {
        isCartVisible = b;
        ((AppFrameworkApplication) applicationContext).setShopingCartVisible((isCartVisible));
    }

    @Override
    public void onGetCompleteProductList(ArrayList<String> arrayList) {
        launchIAP();
        RALog.d(TAG, "List fetched successfully");
    }

    @Override
    public void onSuccess() {
        ((AbstractAppFrameworkBaseActivity) activityContext).hideProgressBar();
    }

    @Override
    public void onSuccess(boolean isCartVisible) {
        this.isCartVisible = isCartVisible;
        ((AppFrameworkApplication) applicationContext).setShopingCartVisible((isCartVisible));
    }

    @Override
    public void onFailure(int i) {
        if(activityContext != null) {
            ((AbstractAppFrameworkBaseActivity) activityContext).hideProgressBar();
        }

        if(getApplicationContext() == null){
            return;
        }

        switch (i){
            case IAP_ERROR_NO_CONNECTION:
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.RA_DLS_check_internet_connectivity), Toast.LENGTH_LONG).show();
                break;
            case IAP_ERROR_CONNECTION_TIME_OUT:
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.RA_DLS_check_internet_connectivity), Toast.LENGTH_LONG).show();
                break;
            case IAP_ERROR_AUTHENTICATION_FAILURE:
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.iap_auth_error), Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.RA_something_wrong), Toast.LENGTH_LONG).show();
                break;
        }
    }
}
