package com.ecs.demotestuapp.fragments;


import android.view.View;
import android.widget.Toast;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.address.ECSAddress;
import java.util.List;

public class CreateAndFetchAddressFragment extends CreateAddressFragment {
    public void executeRequest() {

        ECSAddress createdAddress = getCreatedAddress();

        if(createdAddress == null){
            Toast.makeText(getActivity(),"Address field can not be empty",Toast.LENGTH_SHORT).show();
            getProgressBar().setVisibility(View.GONE);
            return;
        }

        ECSDataHolder.INSTANCE.getEcsServices().createAndFetchAddress(createdAddress, new ECSCallback<List<ECSAddress>, Exception>() {
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

}
