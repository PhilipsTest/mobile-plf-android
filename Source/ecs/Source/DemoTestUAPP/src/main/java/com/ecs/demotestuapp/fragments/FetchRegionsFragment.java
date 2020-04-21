package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.region.ECSRegion;
import com.philips.platform.ecs.util.ECSConfiguration;

import java.util.List;

public class FetchRegionsFragment extends BaseAPIFragment {


    String countryISO = null;
    EditText etCountryISO ;

    @Override
    public void onResume() {
        super.onResume();
        etCountryISO = getLinearLayout().findViewWithTag("et_one");
        etCountryISO.setText(ECSConfiguration.INSTANCE.getCountry());
    }

    public void executeRequest() {

        if(etCountryISO.getText() != null){
            countryISO =  etCountryISO.getText().toString();
        }

        if(countryISO == null){
            Toast.makeText(getActivity(),"Country field can not be empty",Toast.LENGTH_SHORT).show();
            getProgressBar().setVisibility(View.GONE);
            return;
        }

        ECSDataHolder.INSTANCE.getEcsServices().fetchRegions(countryISO,new ECSCallback<List<ECSRegion>, Exception>() {
            @Override
            public void onResponse(List<ECSRegion> ecsRegions) {

                ECSDataHolder.INSTANCE.setEcsRegions(ecsRegions);
                gotoResultActivity(getJsonStringFromObject(ecsRegions));
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
        ECSDataHolder.INSTANCE.setEcsRegions(null);
    }
}

