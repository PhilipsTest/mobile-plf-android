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

import com.philips.platform.ecs.microService.ECSServices;
import com.philips.platform.ecs.microService.error.ECSError;

public class PILConfigureECSServiceFragment extends BaseAPIFragment {

    public void executeRequest() {

        ECSServices ECSServices = new ECSServices(mAppInfraInterface);

        ECSServices.configureECS(new com.philips.platform.ecs.microService.callBack.ECSCallback<Boolean, ECSError>() {
            @Override
            public void onResponse(Boolean result) {
                gotoResultActivity(""+result);
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
