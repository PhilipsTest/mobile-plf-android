package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.ecs.demotestuapp.util.ECSDemoDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.model.region.ECSRegion;

import java.util.ArrayList;
import java.util.List;

public class CreateAddressFragment extends BaseAPIFragment {

    private Spinner spinnerSalutation,spinnerState;


    @Override
    public void onResume() {
        super.onResume();

        spinnerSalutation = getLinearLayout().findViewWithTag("spinner_salutation");
        spinnerState = getLinearLayout().findViewWithTag("spinner_state");

        fillSpinnerDataForSalutation(spinnerSalutation);
        fillSpinnerDataForState(spinnerState);

        prepopulateText(getLinearLayout());
    }

    public void executeRequest() {


        ECSAddress createdAddress = getCreatedAddress();

        if(createdAddress == null){
            Toast.makeText(getActivity(),"Address field can not be empty",Toast.LENGTH_SHORT).show();
            getProgressBar().setVisibility(View.GONE);
            return;
        }

        ECSDemoDataHolder.INSTANCE.getEcsServices().createAddress(createdAddress, new ECSCallback<ECSAddress, Exception>() {
            @Override
            public void onResponse(ECSAddress ecsAddress) {
                gotoResultActivity(getJsonStringFromObject(ecsAddress));
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

    private void fillSpinnerDataForSalutation(Spinner spinner) {
        List<String> list = new ArrayList<>();
        list.add("Mr.");
        list.add("Ms.");

        fillSpinner(spinner,list);
    }

    private void fillSpinnerDataForState(Spinner spinner) {

        List<ECSRegion> ecsRegions = ECSDemoDataHolder.INSTANCE.getEcsRegions();
        List<String> list = new ArrayList<String>();

        for(ECSRegion ecsRegion:ecsRegions){
            list.add(ecsRegion.getName());
        }

        fillSpinner(spinner,list);
    }

    public ECSAddress getCreatedAddress(){
        return  getECSAddress(getLinearLayout());
    }

    @Override
    public void clearData() {

    }
}
