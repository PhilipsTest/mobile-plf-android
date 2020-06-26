package com.ecs.demotestuapp.fragments;

import android.view.View;

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
                    getProgressBar().setVisibility(View.GONE);
                }

                @Override
                public void onFailure(ECSError ecsError) {
                    String errorString = ecsError.getErrorMessage();
                    gotoResultActivity(errorString);
                    getProgressBar().setVisibility(View.GONE);
                }
            });
        } catch (ECSException e) {

            e.printStackTrace();
            gotoResultActivity(e.getMessage());
            getProgressBar().setVisibility(View.GONE);
        }
    }

    @Override
    public void clearData() {

    }
}
