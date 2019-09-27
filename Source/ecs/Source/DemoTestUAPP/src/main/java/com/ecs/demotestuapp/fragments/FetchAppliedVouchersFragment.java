package com.ecs.demotestuapp.fragments;

import android.view.View;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.voucher.ECSVoucher;

import java.util.List;

public class FetchAppliedVouchersFragment extends BaseAPIFragment {



    public void executeRequest() {

        ECSDataHolder.INSTANCE.getEcsServices().fetchAppliedVouchers(new ECSCallback<List<ECSVoucher>, Exception>() {
            @Override
            public void onResponse(List<ECSVoucher> ecsVouchers) {

                ECSDataHolder.INSTANCE.setVouchers(ecsVouchers);
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
