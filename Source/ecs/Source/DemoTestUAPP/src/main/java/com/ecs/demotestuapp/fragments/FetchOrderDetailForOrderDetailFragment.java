package com.ecs.demotestuapp.fragments;


import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.ecs.demotestuapp.util.ECSDemoDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.orders.ECSOrderDetail;

public class FetchOrderDetailForOrderDetailFragment extends BaseAPIFragment {

    public void executeRequest() {

        if(ECSDemoDataHolder.INSTANCE.getEcsOrderDetailFromPlaceOrder()==null){

            Toast.makeText(getActivity(),"Order Detail field can not be empty",Toast.LENGTH_SHORT).show();
            getProgressBar().setVisibility(View.GONE);
            return;
        }

        ECSDemoDataHolder.INSTANCE.getEcsServices().fetchOrderDetail(ECSDemoDataHolder.INSTANCE.getEcsOrderDetailFromPlaceOrder(), new ECSCallback<ECSOrderDetail, Exception>() {
            @Override
            public void onResponse(ECSOrderDetail ecsOrderDetail) {

                ECSDemoDataHolder.INSTANCE.setEcsOrderDetailOfPlaceOrder(ecsOrderDetail);
                gotoResultActivity(getJsonStringFromObject(ecsOrderDetail));
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

    private void fillSpinnerData(Spinner spinner) {
    }

    @Override
    public void clearData() {

    }
}
