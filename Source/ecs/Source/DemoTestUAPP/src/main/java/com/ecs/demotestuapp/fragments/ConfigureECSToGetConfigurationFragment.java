package com.ecs.demotestuapp.fragments;
import android.view.View;

import com.philips.platform.ecs.microService.ECSServices;
import com.philips.platform.ecs.microService.callBack.ECSCallback;
import com.philips.platform.ecs.microService.error.ECSError;
import com.philips.platform.ecs.microService.model.config.ECSConfig;

public class ConfigureECSToGetConfigurationFragment extends BaseAPIFragment {
    public void executeRequest() {

        ECSServices ECSServices = new ECSServices(mAppInfraInterface);

        ECSServices.configureECS(new ECSCallback<com.philips.platform.ecs.microService.model.config.ECSConfig, com.philips.platform.ecs.microService.error.ECSError>() {
            @Override
            public void onResponse(ECSConfig result) {
                gotoResultActivity(getJsonStringFromObject(result));
                getProgressBar().setVisibility(View.GONE);
            }

            @Override
            public void onFailure(ECSError ecsError) {
                gotoResultActivity(ecsError.getErrorCode() +"\n"+ ecsError.getErrorMessage());
                getProgressBar().setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void clearData() {

    }
}
