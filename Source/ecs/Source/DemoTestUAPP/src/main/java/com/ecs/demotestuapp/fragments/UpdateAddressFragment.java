package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.ecs.demotestuapp.util.ECSDemoDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSAddress;

import java.util.ArrayList;
import java.util.List;

public class UpdateAddressFragment extends BaseAPIFragment {


    private Spinner spinnerAddressID;
    private String addressID;



    @Override
    public void onResume() {
        super.onResume();

        spinnerAddressID = getLinearLayout().findViewWithTag("spinner_address_id");

        fillSpinnerData(spinnerAddressID);
    }

    public void executeRequest() {

        ECSAddress ecsAddress = getUpdatedAddress();

        if(ecsAddress == null){
            Toast.makeText(getActivity(),"Address field can not be empty",Toast.LENGTH_SHORT).show();
            getProgressBar().setVisibility(View.GONE);
            return;
        }

        ECSDemoDataHolder.INSTANCE.getEcsServices().updateAddress( ecsAddress, new ECSCallback<Boolean, Exception>() {
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

        if(ecsAddressList!=null) {

            List<String> list = new ArrayList<>();

            for (ECSAddress ecsAddress : ecsAddressList) {
                list.add(ecsAddress.getId());
            }

            fillSpinnerForAddressID(spinner, list);
        }
    }

    private ECSAddress getECSAddress(String addressID){

        ECSAddress ecsAddress = new ECSAddress() ;

        List<ECSAddress> ecsAddressList = ECSDemoDataHolder.INSTANCE.getEcsAddressList();
        for(ECSAddress ecsAddress1:ecsAddressList){
            if(ecsAddress1.getId().equalsIgnoreCase(addressID)){
                return ecsAddress1;
            }
        }

        return ecsAddress;
    }

    private void fillSpinnerForAddressID(Spinner spinner, List<String> list){
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, list);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                addressID = list.get(position);
                ECSAddress ecsAddress = getECSAddress(addressID);
                populateAddress(getLinearLayout(),ecsAddress);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
    }



    public ECSAddress getUpdatedAddress(){
        ECSAddress ecsAddress = getECSAddress(getLinearLayout());
        ecsAddress.setId(addressID);
        return ecsAddress;
    }

    @Override
    public void clearData() {

    }
}
