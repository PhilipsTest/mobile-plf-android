/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */

package com.ecs.demotestuapp.fragments;

import android.view.View;

import com.philips.platform.ecs.microService.MicroECSServices;
import com.philips.platform.ecs.microService.callBack.ECSCallback;
import com.philips.platform.ecs.microService.error.ECSError;
import com.philips.platform.ecs.microService.model.config.ECSConfig;

public class ConfigureECSToGetConfigurationMicroServiceFragment extends BaseAPIFragment {
    public void executeRequest() {

        MicroECSServices microECSServices = new MicroECSServices(mAppInfraInterface);

        microECSServices.configureECSToGetConfiguration(new ECSCallback<ECSConfig, ECSError>() {
            @Override
            public void onResponse(ECSConfig result) {

                gotoResultActivity(getJsonStringFromObject(result));
                getProgressBar().setVisibility(View.GONE);
            }

            @Override
            public void onFailure(ECSError ecsError) {

                gotoResultActivity(ecsError.getErrorMessage());
                getProgressBar().setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void clearData() {

    }
}
