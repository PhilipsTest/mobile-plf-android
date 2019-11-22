package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;
import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.orders.ECSOrderHistory;


public class FetchOrderHistoryFragment extends BaseAPIFragment {


    EditText etPageNumber,etPageSize;
    int  pageSize = 20,pageNumber =0;
    @Override
    public void onResume() {
        super.onResume();

        etPageNumber = getLinearLayout().findViewWithTag("et_one");
        etPageNumber.setText(pageNumber+"");
        etPageSize = getLinearLayout().findViewWithTag("et_two");
        etPageSize.setText(pageSize+"");
    }

    public void executeRequest() {

        if(!etPageSize.getText().toString().trim().isEmpty()){
            pageSize = Integer.valueOf(etPageSize.getText().toString().trim());
        }

        if(!etPageNumber.getText().toString().trim().isEmpty()){
            pageNumber = Integer.valueOf(etPageNumber.getText().toString().trim());
        }


        ECSDataHolder.INSTANCE.getEcsServices().fetchOrderHistory(pageNumber, pageSize, new ECSCallback<ECSOrderHistory, Exception>() {
            @Override
            public void onResponse(ECSOrderHistory ecsOrderHistory) {

                gotoResultActivity(getJsonStringFromObject(ecsOrderHistory));
                ECSDataHolder.INSTANCE.setEcsOrderHistory(ecsOrderHistory);
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
        ECSDataHolder.INSTANCE.setEcsOrderHistory(null);
    }
}
