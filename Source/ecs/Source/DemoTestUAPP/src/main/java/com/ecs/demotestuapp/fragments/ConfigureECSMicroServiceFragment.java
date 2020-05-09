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

public class ConfigureECSMicroServiceFragment extends BaseAPIFragment {

    public void executeRequest() {

        MicroECSServices microECSServices = new MicroECSServices(mAppInfraInterface);

        microECSServices.configureECS(new com.philips.platform.ecs.microService.callBack.ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                gotoResultActivity(""+result);
                getProgressBar().setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception ecsError) {
                gotoResultActivity(ecsError.getMessage());
                getProgressBar().setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void clearData() {

    }
}
