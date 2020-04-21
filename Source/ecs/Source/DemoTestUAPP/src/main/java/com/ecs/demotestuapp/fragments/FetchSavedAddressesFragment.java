package com.ecs.demotestuapp.fragments;

import android.view.View;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSAddress;

import java.util.List;

public class FetchSavedAddressesFragment extends BaseAPIFragment {

    public void executeRequest() {

        ECSDataHolder.INSTANCE.getEcsServices().fetchSavedAddresses(new ECSCallback<List<ECSAddress>, Exception>() {
            @Override
            public void onResponse(List<ECSAddress> ecsAddressList) {

                ECSDataHolder.INSTANCE.setEcsAddressList(ecsAddressList);
                gotoResultActivity(getJsonStringFromObject(ecsAddressList));
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
        ECSDataHolder.INSTANCE.setEcsAddressList(null);
    }
}
