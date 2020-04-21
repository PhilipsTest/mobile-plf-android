package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.orders.ECSOrderDetail;

public class SubmitOrderFragment extends BaseAPIFragment {


    private EditText etCvv;
    String cvv = null;

    @Override
    public void onResume() {
        super.onResume();
        etCvv = getLinearLayout().findViewWithTag("et_one");
    }

    public void executeRequest() {

        if(etCvv.getText()!=null){
            cvv = etCvv.getText().toString();
        }

        ECSDataHolder.INSTANCE.getEcsServices().submitOrder(cvv, new ECSCallback<ECSOrderDetail, Exception>() {
            @Override
            public void onResponse(ECSOrderDetail ecsOrderDetail) {
                ECSDataHolder.INSTANCE.setEcsOrderDetailOfPlaceOrder(ecsOrderDetail);
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


    @Override
    public void clearData() {
        ECSDataHolder.INSTANCE.setEcsOrderDetailOfPlaceOrder(null);
    }
}
