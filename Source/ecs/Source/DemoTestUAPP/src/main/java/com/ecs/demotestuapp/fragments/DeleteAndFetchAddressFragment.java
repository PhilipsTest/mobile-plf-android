package com.ecs.demotestuapp.fragments;


import android.view.View;
import android.widget.Toast;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSAddress;

import java.util.List;

public class DeleteAndFetchAddressFragment extends DeleteAddressFragment {

    public void executeRequest() {

        ECSAddress ecsAddress = getECSAddress();

        if(ecsAddress == null){
            Toast.makeText(getActivity(),"Address field can not be empty",Toast.LENGTH_SHORT).show();
            getProgressBar().setVisibility(View.GONE);
            return;
        }


        ECSDataHolder.INSTANCE.getEcsServices().deleteAndFetchAddress(ecsAddress, new ECSCallback<List<ECSAddress>, Exception>() {
            @Override
            public void onResponse(List<ECSAddress> ecsAddresses) {

                ECSDataHolder.INSTANCE.setEcsAddressList(ecsAddresses);
                gotoResultActivity(getJsonStringFromObject(ecsAddresses));
                getProgressBar().setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e, ECSError ecsError) {

                String errorString = getFailureString(e, ecsError);
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
