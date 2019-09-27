package com.ecs.demotestuapp.fragments;

import android.view.View;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.address.ECSUserProfile;

public class FetchUserProfileFragment extends BaseAPIFragment {



    public void executeRequest() {

        ECSDataHolder.INSTANCE.getEcsServices().fetchUserProfile(new ECSCallback<ECSUserProfile, Exception>() {
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
