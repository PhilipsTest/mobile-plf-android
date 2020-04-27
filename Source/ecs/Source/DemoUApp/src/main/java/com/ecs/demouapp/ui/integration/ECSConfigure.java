package com.ecs.demouapp.ui.integration;


import com.ecs.demouapp.ui.utils.ECSConstant;
import com.ecs.demouapp.ui.utils.ECSLog;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.ecs.demouapp.ui.utils.Utility;
import com.philips.platform.ecs.ECSServices;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.util.ECSConfiguration;
import com.philips.platform.uappframework.launcher.UiLauncher;

public class ECSConfigure {
    public static final String COMPLETE_PRODUCT_LIST = "completeProductList";
    private ECSSettings mIAPSettings;
    private boolean isCartVisible;

    ECSConfigure(ECSSettings pIAPSettings) {
        mIAPSettings = pIAPSettings;
    }

    void configureECS(final UiLauncher pUiLauncher, final ECSHandler pIAPHandler, final ECSLaunchInput pIapLaunchInput, final ECSListener iapListener, final String entry) {

        ECSServices ecsServices = ECSUtility.getInstance().getEcsServices();

        ecsServices.configureECS(new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {


                //Condition for launching IAP screens
                if (iapListener == null && entry == null) {
                    if (ECSConfiguration.INSTANCE.getBaseURL() == null || ECSConfiguration.INSTANCE.getBaseURL().isEmpty() || !ECSUtility.getInstance().isHybrisSupported()) {
                        mIAPSettings.setUseLocalData(true);
                    }
                    launchingIAP(pIAPHandler, pUiLauncher, pIapLaunchInput);
                } else {
                    //Condition for returning gatCartCount API and getCompleteProductlist API
                    if (ECSConfiguration.INSTANCE.getBaseURL() == null || !ECSUtility.getInstance().isHybrisSupported()) {
                        mIAPSettings.setUseLocalData(true);
                    } else {
                        // TODO Retailer view hence making the userLocalData to true
                        mIAPSettings.setUseLocalData(false);

                        if (entry.equalsIgnoreCase(COMPLETE_PRODUCT_LIST))

                            try {
                                pIAPHandler.getExposedAPIImplementor().getCompleteProductList(iapListener);
                            } catch (Exception e) {
                                ECSLog.e(ECSLog.LOG, e.getMessage());
                            }
                        else {
                            pIAPHandler.getExposedAPIImplementor().getProductCartCount(iapListener);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

                try {
                    iapListener.onFailure(ECSConstant.IAP_ERROR_SERVER_ERROR);
                }catch(Exception e){

                }
                ECSLog.i(ECSLog.LOG, "ServiceDiscoveryInterface ==errorvalues " + error.getMessage());

            }
        });

    }

    public void launchingIAP(ECSHandler pIAPHandler, UiLauncher pUiLauncher, ECSLaunchInput pUappLaunchInput) {
        Utility.setVoucherCode(pUappLaunchInput.getVoucher());
        ECSUtility.getInstance().setMaxCartCount(pUappLaunchInput.getMaxCartCount());
        ECSUtility.getInstance().setHybrisSupported(pUappLaunchInput.isHybrisSupported());
        ECSUtility.getInstance().setIapOrderFlowCompletion(pUappLaunchInput.getIapOrderFlowCompletion());
        if (pUappLaunchInput.getECSBannerEnabler() != null) {
            ECSUtility.getInstance().setBannerView(pUappLaunchInput.getECSBannerEnabler().getBannerView());
        }
        if (!mIAPSettings.isUseLocalData()) {
            pIAPHandler.initIAP(pUiLauncher, pUappLaunchInput);
        } else {
            pIAPHandler.launchIAP(pUiLauncher, pUappLaunchInput);
        }
    }


    public void getCartVisiblityByConfigUrl(final ECSListener listener, final ECSHandler iapHandler) {

        ECSUtility.getInstance().getEcsServices().configureECS(new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {

                if (ECSConfiguration.INSTANCE.getBaseURL() == null || ECSUtility.getInstance().isHybrisSupported() == false) {
                    mIAPSettings.setUseLocalData(true);
                    isCartVisible = false;
                } else {
                    mIAPSettings.setUseLocalData(false);
                    isCartVisible = true;
                }
                listener.onSuccess(isCartVisible);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

                ECSLog.i(ECSLog.LOG, "ServiceDiscoveryInterface ==errorvalues " + error.getMessage());
                listener.onFailure(ECSConstant.IAP_ERROR_SERVER_ERROR);
            }
        });

    }

}
