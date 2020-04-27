package com.ecs.demotestuapp.fragments;

import android.view.View;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;

public class ConfigureECSFragment extends BaseAPIFragment {

    public void executeRequest() {

        ECSDataHolder.INSTANCE.getEcsServices().configureECS(new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean aBoolean) {
               gotoResultActivity(""+aBoolean);
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
