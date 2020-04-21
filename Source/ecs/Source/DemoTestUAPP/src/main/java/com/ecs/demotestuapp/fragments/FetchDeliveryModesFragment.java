package com.ecs.demotestuapp.fragments;
import android.view.View;
import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSDeliveryMode;

import java.util.List;

public class FetchDeliveryModesFragment extends BaseAPIFragment {



    public void executeRequest() {
        ECSDataHolder.INSTANCE.getEcsServices().fetchDeliveryModes(new ECSCallback<List<ECSDeliveryMode>, Exception>() {
            @Override
            public void onResponse(List<ECSDeliveryMode> ecsDeliveryModes) {

                ECSDataHolder.INSTANCE.setEcsDeliveryModes(ecsDeliveryModes);
                String jsonString = getJsonStringFromObject(ecsDeliveryModes);
                gotoResultActivity(jsonString);
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
        ECSDataHolder.INSTANCE.setEcsDeliveryModes(null);
    }
}