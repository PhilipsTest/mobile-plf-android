package com.ecs.demotestuapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class SetDeliveryAddressFragment extends BaseFragment {

    private LinearLayout linearLayout;
    private SubgroupItem subgroupItem;

    private Button btn_execute;

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    private ProgressBar progressBar;
    private Spinner spinner;

    String selectedItem = "xyz";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.input_fragment, container, false);

        linearLayout = rootView.findViewById(R.id.ll_container);

        Bundle bundle = getActivity().getIntent().getExtras();
        subgroupItem = (SubgroupItem) bundle.getSerializable("sub_group");
        inflateLayout(linearLayout,subgroupItem);


        btn_execute = rootView.findViewById(R.id.btn_execute);
        progressBar = rootView.findViewById(R.id.progressBar);

        spinner = linearLayout.findViewWithTag("spinner_one");
        fillSpinnerData(spinner);

        btn_execute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                executeRequest();
            }
        });

        return rootView;
    }

    public void executeRequest() {

        if(spinner.getSelectedItem()!=null) {
           selectedItem = (String) spinner.getSelectedItem();
        }

        ECSAddress ecsAddress = getECSAddress();

        ECSDataHolder.INSTANCE.getEcsServices().setDeliveryAddress(ecsAddress, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean aBoolean) {
                gotoResultActivity(aBoolean+"");
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

        List<String> list = new ArrayList<>();

        for(ECSAddress ecsAddress:ecsAddressList){
            list.add(ecsAddress.getId());
        }

        fillSpinner(spinner,list);
    }

    public ECSAddress getECSAddress(){

        ECSAddress ecsAddress = new ECSAddress() ;
        ecsAddress.setId(selectedItem);

        List<ECSAddress> ecsAddressList = ECSDataHolder.INSTANCE.getEcsAddressList();
        for(ECSAddress ecsAddress1:ecsAddressList){
            if(ecsAddress1.getId().equalsIgnoreCase(selectedItem)){
                return ecsAddress1;
            }
        }

        return ecsAddress;
    }


}
