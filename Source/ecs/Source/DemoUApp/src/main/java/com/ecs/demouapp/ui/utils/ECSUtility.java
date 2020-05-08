package com.ecs.demouapp.ui.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;


import com.ecs.demouapp.ui.integration.ECSOrderFlowCompletion;
import com.philips.platform.ecs.ECSServices;
import com.philips.platform.ecs.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.model.cart.ECSEntries;
import com.philips.platform.ecs.model.products.PaginationEntity;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;

import java.util.List;

/**
 * Created by philips on 5/2/19.
 */

public class ECSUtility {

    private int maxCartCount;
    private ECSOrderFlowCompletion iapOrderFlowCompletion;
    private boolean isHybrisSupported = true;
    private View bannerView = null;
    private boolean isVoucherEnable = false;
    private UserDataInterface mUserDataInterface;
    private String appName;
    private String localeTag;
    private PaginationEntity paginationEntity;

    private ECSUtility() {
    }

    public int getMaxCartCount() {
        return maxCartCount;
    }

    public void setMaxCartCount(int maxCartCount) {
        this.maxCartCount = maxCartCount;
    }

    public ECSOrderFlowCompletion getIapOrderFlowCompletion() {
        return iapOrderFlowCompletion;
    }

    public void setIapOrderFlowCompletion(ECSOrderFlowCompletion iapOrderFlowCompletion) {
        this.iapOrderFlowCompletion = iapOrderFlowCompletion;
    }

    public boolean isHybrisSupported() {
        return isHybrisSupported;
    }

    public void setHybrisSupported(boolean hybrisSupported) {
        isHybrisSupported = hybrisSupported;
    }

    public View getBannerView() {
        return bannerView;
    }

    public void setBannerView(View bannerView) {
        this.bannerView = bannerView;
    }

    public boolean isVoucherEnable() {
        return isVoucherEnable;
    }

    public void setVoucherEnable(boolean voucherEnable) {
        isVoucherEnable = voucherEnable;
    }

    public UserDataInterface getUserDataInterface() {
        return mUserDataInterface;
    }

    public void setUserDataInterface(UserDataInterface mUserDataInterface) {
       this.mUserDataInterface =  mUserDataInterface;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getLocaleTag() {
        return localeTag;
    }

    public void setLocaleTag(String localeTag) {
        this.localeTag = localeTag;
    }


    public PaginationEntity getPaginationEntity() {
        return paginationEntity;
    }

    public void setPaginationEntity(PaginationEntity paginationEntity) {
        this.paginationEntity = paginationEntity;
    }

    public ECSServices getEcsServices() {
        return ecsServices;
    }

    ECSServices ecsServices;

    public void setEcsService(ECSServices ecsServices) {
        this.ecsServices = ecsServices;
    }

    private static class IAPUtilitySingleton
    {
        private static final ECSUtility INSTANCE = new ECSUtility();
    }

    public static ECSUtility getInstance()
    {
        return IAPUtilitySingleton.INSTANCE;
    }

    public int getQuantity(ECSShoppingCart carts) {
        int totalItems = carts.getTotalItems();
        int quantity = 0;
        if (carts.getEntries() != null) {
            List<ECSEntries> entries = carts.getEntries();
            if (totalItems != 0 && null != entries) {
                for (int i = 0; i < entries.size(); i++) {
                    quantity = quantity + entries.get(i).getQuantity();
                }
            }
        }
        return quantity;
    }


    public static void showECSAlertDialog(Context context, String title, String message ){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void showECSAlertDialog(Context context, String title, Exception exception ){
        String message= getErrorMessageFromException(exception);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static String getErrorMessageFromException(Exception exception){
        String errorMessage = null!=exception.getMessage()?exception.getMessage():exception.toString();
        return errorMessage;
    }

}
