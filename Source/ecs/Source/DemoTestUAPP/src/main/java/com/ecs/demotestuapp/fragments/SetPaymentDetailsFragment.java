package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;

public class SetPaymentDetailsFragment extends BaseAPIFragment {

    private EditText etPaymentDetail;
    String paymentDetailID = null;

    @Override
    public void onResume() {
        super.onResume();

        etPaymentDetail = getLinearLayout().findViewWithTag("et_one");
    }

    public void executeRequest() {

        if(etPaymentDetail.getText()!=null){
            paymentDetailID = etPaymentDetail.getText().toString();
        }

        ECSDataHolder.INSTANCE.getEcsServices().setPaymentDetails(paymentDetailID, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean aBoolean) {

                gotoResultActivity(aBoolean+"");
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

    }
}
