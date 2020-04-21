package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.retailers.ECSRetailerList;

public class FetchRetailersFragment extends BaseAPIFragment {


    String ctn = null;
    EditText etCtn ;

    @Override
    public void onResume() {
        super.onResume();
        etCtn = getLinearLayout().findViewWithTag("et_one");
    }

    public void executeRequest() {

         if(etCtn.getText()!=null){
             ctn = etCtn.getText().toString();
         }
        ECSDataHolder.INSTANCE.getEcsServices().fetchRetailers(ctn, new ECSCallback<ECSRetailerList, Exception>() {
            @Override
            public void onResponse(ECSRetailerList ecsRetailerList) {

                gotoResultActivity(getJsonStringFromObject(ecsRetailerList));
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