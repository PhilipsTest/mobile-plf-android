package com.ecs.demotestuapp.fragments;

import android.view.View;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.payment.ECSPayment;

import java.util.List;

public class FetchPaymentsDetailsFragment extends BaseAPIFragment {

    public void executeRequest() {
        ECSDataHolder.INSTANCE.getEcsServices().fetchPaymentsDetails(new ECSCallback<List<ECSPayment>, Exception>() {
            @Override
            public void onResponse(List<ECSPayment> ecsPayments) {

                gotoResultActivity(getJsonStringFromObject(ecsPayments));
                ECSDataHolder.INSTANCE.setEcsPayments(ecsPayments);
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
        ECSDataHolder.INSTANCE.setEcsPayments(null);
    }
}
