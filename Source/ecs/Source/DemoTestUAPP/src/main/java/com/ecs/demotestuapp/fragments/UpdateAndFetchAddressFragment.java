package com.ecs.demotestuapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.ecs.demotestuapp.R;
import com.ecs.demotestuapp.jsonmodel.SubgroupItem;
import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.address.ECSAddress;

import java.util.ArrayList;
import java.util.List;

public class UpdateAndFetchAddressFragment extends BaseFragment {

    private LinearLayout linearLayout;
    private SubgroupItem subgroupItem;

    private Button btn_execute;
    private ProgressBar progressBar;
    private Spinner spinnerAddressID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.input_fragment, container, false);

        linearLayout = rootView.findViewById(R.id.ll_container);

        Bundle bundle = getActivity().getIntent().getExtras();
        subgroupItem = (SubgroupItem) bundle.getSerializable("sub_group");
        inflateLayout(linearLayout,subgroupItem);

        spinnerAddressID = linearLayout.findViewWithTag("spinner_address_id");

        fillSpinnerData(spinnerAddressID);

        btn_execute = rootView.findViewById(R.id.btn_execute);
        progressBar = rootView.findViewById(R.id.progressBar);


        btn_execute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                executeRequest();
            }
        });

        return rootView;
    }

    private void executeRequest() {

        ECSAddress ecsAddress = getECSAddress(linearLayout);

        ECSDataHolder.INSTANCE.getEcsServices().updateAndFetchAddress( ecsAddress, new ECSCallback<List<ECSAddress>, Exception>() {
            @Override
            public void onResponse(List<ECSAddress> ecsAddresses) {

                ECSDataHolder.INSTANCE.setEcsAddressList(ecsAddresses);
                gotoResultActivity(getJsonStringFromObject(ecsAddresses));
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e, ECSError ecsError) {
                String errorString = getFailureString(e, ecsError);
                gotoResultActivity(errorString);
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    private void fillSpinnerData(Spinner spinner) {

        List<ECSAddress> ecsAddressList = ECSDataHolder.INSTANCE.getEcsAddressList();

        if(ecsAddressList!=null) {
            List<String> list = new ArrayList<>();

            for (ECSAddress ecsAddress : ecsAddressList) {
                list.add(ecsAddress.getId());
            }

            fillSpinner(spinner, list);
        }
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

    public void fillSpinner(Spinner spinner, List<String> list){
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, list);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String addressID = list.get(position);
                ECSAddress ecsAddress = getECSAddress(addressID);
                populateAddress(linearLayout,ecsAddress);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
    }

}
