package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.voucher.ECSVoucher;

import java.util.List;

public class RemoveVoucherFragment extends BaseAPIFragment {


    EditText etVoucherCode;
    String voucherCode = null;

    @Override
    public void onResume() {
        super.onResume();

        etVoucherCode = getLinearLayout().findViewWithTag("et_one");
    }

    public void executeRequest() {

        if(etVoucherCode.getText()!=null)
            voucherCode = etVoucherCode.getText().toString().trim();

        ECSDataHolder.INSTANCE.getEcsServices().removeVoucher(voucherCode, new ECSCallback<List<ECSVoucher>, Exception>() {
            @Override
            public void onResponse(List<ECSVoucher> ecsVouchers) {
                ECSDataHolder.INSTANCE.setVouchers(ecsVouchers);
                gotoResultActivity(getJsonStringFromObject(ecsVouchers));
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
        ECSDataHolder.INSTANCE.setVouchers(null);
    }
}


