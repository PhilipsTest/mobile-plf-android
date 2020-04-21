package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.orders.ECSOrderHistory;
import com.philips.platform.ecs.model.orders.ECSOrders;
import java.util.ArrayList;
import java.util.List;

public class FetchOrderDetailForOdersFragment extends BaseAPIFragment {



    private Spinner spinnerOrders;

    String orderID = "unknown";

    @Override
    public void onResume() {
        super.onResume();
        spinnerOrders  = getLinearLayout().findViewWithTag("spinner_one");
        fillSpinnerData(spinnerOrders);
    }

    private void fillSpinnerData(Spinner spinner) {

        ECSOrderHistory ecsOrderHistory = ECSDataHolder.INSTANCE.getEcsOrderHistory();

        if(ecsOrderHistory!=null){

            List<String> list = new ArrayList<>();
            List<ECSOrders> orders = ecsOrderHistory.getOrders();

            if(orders !=null){

                for(ECSOrders ecsOrders:orders){
                    list.add(ecsOrders.getCode());
                }
            }
            fillSpinner(spinner,list);
        }

    }

    private ECSOrders getOrderFromOrderID(String orderID){

        ECSOrderHistory ecsOrderHistory = ECSDataHolder.INSTANCE.getEcsOrderHistory();

        if(ecsOrderHistory!=null){

            List<ECSOrders> orders = ecsOrderHistory.getOrders();

            if(orders !=null){

                for(ECSOrders ecsOrders:orders){

                    if(orderID.equalsIgnoreCase(ecsOrders.getCode())){
                        return ecsOrders;
                    }
                }
            }

        }
        return null;
    }



    public void executeRequest() {

        if(spinnerOrders.getSelectedItem()!=null) {
            orderID = (String) spinnerOrders.getSelectedItem();
        }

        ECSOrders ecsOrders = getOrderFromOrderID(orderID);

        if(ecsOrders ==null){

            Toast.makeText(getActivity(),"Order  field can not be empty",Toast.LENGTH_SHORT).show();
            getProgressBar().setVisibility(View.GONE);
            return;
        }

        ECSDataHolder.INSTANCE.getEcsServices().fetchOrderDetail(ecsOrders, new ECSCallback<ECSOrders, Exception>() {
            @Override
            public void onResponse(ECSOrders ecsOrders) {

                String jsonString = getJsonStringFromObject(ecsOrders);
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

    }
}
