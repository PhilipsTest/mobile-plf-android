package com.ecs.demotestuapp.fragments;

import android.view.View;

import com.ecs.demotestuapp.util.PILDataHolder;
import com.philips.platform.ecs.microService.ECSServices;
import com.philips.platform.ecs.microService.callBack.ECSCallback;
import com.philips.platform.ecs.microService.error.ECSError;
import com.philips.platform.ecs.microService.error.ECSException;
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart;

public class PILFetchShoppingCartFragment extends BaseAPIFragment {


    @Override
    void executeRequest() {
        ECSServices microECSServices = new ECSServices(mAppInfraInterface);

        try {
            microECSServices.fetchShoppingCart(new ECSCallback<ECSShoppingCart, ECSError>() {
                @Override
                public void onResponse(ECSShoppingCart result) {
                    gotoResultActivity(getJsonStringFromObject(result));
                    PILDataHolder.INSTANCE.setEcsShoppingCart(result);
                    getProgressBar().setVisibility(View.GONE);
                }

                @Override
                public void onFailure(ECSError ecsError) {
                    gotoResultActivity(ecsError.getErrorCode() +"\n"+ ecsError.getErrorMessage());
                    getProgressBar().setVisibility(View.GONE);
                }
            });
        } catch (ECSException e) {

            gotoResultActivity(e.getErrorCode() +"\n"+ e.getMessage());
            getProgressBar().setVisibility(View.GONE);
        }
    }

    @Override
    public void clearData() {

    }
}
