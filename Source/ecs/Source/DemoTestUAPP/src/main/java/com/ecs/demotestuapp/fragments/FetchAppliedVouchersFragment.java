package com.ecs.demotestuapp.fragments;

import android.view.View;

import com.ecs.demotestuapp.util.ECSDemoDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.voucher.ECSVoucher;

import java.util.List;

public class FetchAppliedVouchersFragment extends BaseAPIFragment {



    public void executeRequest() {

        ECSDemoDataHolder.INSTANCE.getEcsServices().fetchAppliedVouchers(new ECSCallback<List<ECSVoucher>, Exception>() {
            @Override
            public void onResponse(List<ECSVoucher> ecsVouchers) {

                ECSDemoDataHolder.INSTANCE.setVouchers(ecsVouchers);
                String jsonString = getJsonStringFromObject(ecsVouchers);
                gotoResultActivity(jsonString);
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
