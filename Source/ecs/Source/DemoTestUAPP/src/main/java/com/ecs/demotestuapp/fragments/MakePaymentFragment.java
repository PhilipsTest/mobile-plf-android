package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.address.ECSAddress;
import com.philips.cdp.di.ecs.model.orders.ECSOrderDetail;
import com.philips.cdp.di.ecs.model.payment.ECSPaymentProvider;

import java.util.ArrayList;
import java.util.List;

public class MakePaymentFragment extends BaseAPIFragment {

    private Spinner spinner2;

    private EditText  etOrderDetailID;

    String orderDetailID = null;
    String billingAddressID = "unknown";

    ECSOrderDetail ecsOrderDetail =null;


    @Override
    public void onResume() {
        super.onResume();

        etOrderDetailID = getLinearLayout().findViewWithTag("et_one");

        if(ECSDataHolder.INSTANCE.getEcsOrderDetail()!=null){
            ecsOrderDetail = ECSDataHolder.INSTANCE.getEcsOrderDetail();
            etOrderDetailID.setText(ECSDataHolder.INSTANCE.getEcsOrderDetail().getCode());
        }

        spinner2 = getLinearLayout().findViewWithTag("spinner_one");

        fillSpinnerData(spinner2);
    }

    public void executeRequest() {

        orderDetailID = getTextFromEditText(etOrderDetailID);

        if(spinner2.getSelectedItem()!=null){
            billingAddressID = (String)spinner2.getSelectedItem();
        }

        ECSAddress ecsAddress = getECSAddress(billingAddressID);

        ECSDataHolder.INSTANCE.getEcsServices().makePayment(ECSDataHolder.INSTANCE.getEcsOrderDetail(), ecsAddress, new ECSCallback<ECSPaymentProvider, Exception>() {
            @Override
            public void onResponse(ECSPaymentProvider ecsPaymentProvider) {

                String jsonString = getJsonStringFromObject(ecsPaymentProvider);
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

    private void fillSpinnerData(Spinner spinner) {

        List<ECSAddress> ecsAddressList = ECSDataHolder.INSTANCE.getEcsAddressList();

        List<String> list = new ArrayList<>();

        if(ecsAddressList!=null) {
            for (ECSAddress ecsAddress : ecsAddressList) {
                list.add(ecsAddress.getId());
            }
        }

        fillSpinner(spinner,list);
    }

    private ECSAddress getECSAddress(String addressID){

        ECSAddress ecsAddress = new ECSAddress() ;

        List<ECSAddress> ecsAddressList = ECSDataHolder.INSTANCE.getEcsAddressList();
        for(ECSAddress ecsAddress1:ecsAddressList){
            if(ecsAddress1.getId().equalsIgnoreCase(addressID)){
                return ecsAddress1;
            }
        }

        return ecsAddress;
    }

    @Override
    public void clearData() {

    }
}
