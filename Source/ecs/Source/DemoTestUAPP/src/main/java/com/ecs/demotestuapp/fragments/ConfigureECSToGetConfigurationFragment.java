package com.ecs.demotestuapp.fragments;
import android.view.View;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.config.ECSConfig;

public class ConfigureECSToGetConfigurationFragment extends BaseAPIFragment {
    public void executeRequest() {

        ECSDataHolder.INSTANCE.getEcsServices().configureECSToGetConfiguration(new ECSCallback<ECSConfig, Exception>() {
            @Override
            public void onResponse(ECSConfig ecsConfig) {
                gotoResultActivity(getJsonStringFromObject(ecsConfig));
                getProgressBar().setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e, ECSError ecsError) {

                String errorString = getFailureString(e,ecsError);

                gotoResultActivity(errorString);
                getProgressBar().setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void clearData() {

    }
}
