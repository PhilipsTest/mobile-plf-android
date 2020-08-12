package com.ecs.demotestuapp.fragments;

import android.view.View;

import com.ecs.demotestuapp.util.ECSDemoDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSUserProfile;

public class FetchUserProfileFragment extends BaseAPIFragment {



    public void executeRequest() {

        ECSDemoDataHolder.INSTANCE.getEcsServices().fetchUserProfile(new ECSCallback<ECSUserProfile, Exception>() {
            @Override
            public void onResponse(ECSUserProfile ecsUserProfile) {

                gotoResultActivity(getJsonStringFromObject(ecsUserProfile));
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
