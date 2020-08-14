package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.ecs.demotestuapp.util.ECSDemoDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSAddress;

import java.util.ArrayList;
import java.util.List;

public class SetDeliveryAddressFragment extends BaseAPIFragment {


    private Spinner spinner;
    String selectedItem = "xyz";

    @Override
    public void onResume() {
        super.onResume();

        spinner = getLinearLayout().findViewWithTag("spinner_one");
        fillSpinnerData(spinner);
    }

    public void executeRequest() {

        ECSAddress ecsAddress = getECSAddress();

        if(ecsAddress == null){
            Toast.makeText(getActivity(),"Address field can not be empty",Toast.LENGTH_SHORT).show();
            getProgressBar().setVisibility(View.GONE);
            return;
        }

        ECSDemoDataHolder.INSTANCE.getEcsServices().setDeliveryAddress(true,ecsAddress, new ECSCallback<Boolean, Exception>() {
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

    private void fillSpinnerData(Spinner spinner) {

        List<ECSAddress> ecsAddressList = ECSDemoDataHolder.INSTANCE.getEcsAddressList();

        if(ecsAddressList ==null) return;

        List<String> list = new ArrayList<>();

        for(ECSAddress ecsAddress:ecsAddressList){
            list.add(ecsAddress.getId());
        }

        fillSpinner(spinner,list);
    }

    public ECSAddress getECSAddress(){

        if(spinner.getSelectedItem()!=null) {
            selectedItem = (String) spinner.getSelectedItem();
        }

        ECSAddress ecsAddress = new ECSAddress() ;
        ecsAddress.setId(selectedItem);

        List<ECSAddress> ecsAddressList = ECSDemoDataHolder.INSTANCE.getEcsAddressList();

        if(ecsAddressList == null) return null;

        for(ECSAddress ecsAddress1:ecsAddressList){
            if(ecsAddress1.getId().equalsIgnoreCase(selectedItem)){
                return ecsAddress1;
            }
        }

        return ecsAddress;
    }


    @Override
    public void clearData() {

    }
}
