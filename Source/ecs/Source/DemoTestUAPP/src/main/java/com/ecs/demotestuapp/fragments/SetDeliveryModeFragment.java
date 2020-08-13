package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.ecs.demotestuapp.util.ECSDemoDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSDeliveryMode;

import java.util.ArrayList;
import java.util.List;

public class SetDeliveryModeFragment extends BaseAPIFragment {


    private Spinner spinner;

    private String selectedDeliveryID = "xyz";



    @Override
    public void onResume() {
        super.onResume();

        spinner = getLinearLayout().findViewWithTag("spinner_one");
        fillSpinnerData(spinner);

    }

    private void fillSpinnerData(Spinner spinner) {

        ArrayList<String> deliveryModeCodes = new ArrayList<>();

        if (ECSDemoDataHolder.INSTANCE.getEcsProducts() != null) {

            List<ECSDeliveryMode> ecsDeliveryModes = ECSDemoDataHolder.INSTANCE.getEcsDeliveryModes();
            if(ecsDeliveryModes!=null) {
                if (ecsDeliveryModes.size() != 0) {

                    for (ECSDeliveryMode ecsDeliveryMode : ecsDeliveryModes) {
                        deliveryModeCodes.add(ecsDeliveryMode.getCode() + "");
                    }

                    fillSpinner(spinner, deliveryModeCodes);
                }
            }
        }
    }

    private ECSDeliveryMode getDeliveryMode(String deliveryModeID){

        if (ECSDemoDataHolder.INSTANCE.getEcsProducts() != null) {

            List<ECSDeliveryMode> ecsDeliveryModes = ECSDemoDataHolder.INSTANCE.getEcsDeliveryModes();
            if (ecsDeliveryModes.size() != 0) {

                for (ECSDeliveryMode ecsDeliveryMode : ecsDeliveryModes) {

                    if(ecsDeliveryMode.getCode().equalsIgnoreCase(deliveryModeID)){
                        return ecsDeliveryMode;
                    }
                }


            }
        }
        return null;
    }

    public void executeRequest() {

        if(spinner.getSelectedItem()!=null) {
            selectedDeliveryID = (String) spinner.getSelectedItem();
        }

        ECSDeliveryMode deliveryMode = getDeliveryMode(selectedDeliveryID);

        if(deliveryMode == null){
            Toast.makeText(getActivity(),"delivery field can not be empty",Toast.LENGTH_SHORT).show();
            getProgressBar().setVisibility(View.GONE);
            return;
        }


        ECSDemoDataHolder.INSTANCE.getEcsServices().setDeliveryMode(deliveryMode, new ECSCallback<Boolean, Exception>() {
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

    @Override
    public void clearData() {

    }
}
