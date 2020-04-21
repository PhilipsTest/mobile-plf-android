package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.orders.ECSOrderDetail;

public class FetchOrderDetailFragment extends BaseAPIFragment {


    EditText etOrderDetailID;
    String orderDetailID = null;

    @Override
    public void onResume() {
        super.onResume();
        etOrderDetailID = getLinearLayout().findViewWithTag("et_one");
    }

    public void executeRequest() {

        if(etOrderDetailID.getText()!=null) {
             orderDetailID = etOrderDetailID.getText().toString().trim();
        }

        ECSDataHolder.INSTANCE.getEcsServices().fetchOrderDetail(orderDetailID, new ECSCallback<ECSOrderDetail, Exception>() {
            @Override
            public void onResponse(ECSOrderDetail ecsOrderDetail) {
                ECSDataHolder.INSTANCE.setEcsOrderDetailOrderHistory(ecsOrderDetail);
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

    }
}

