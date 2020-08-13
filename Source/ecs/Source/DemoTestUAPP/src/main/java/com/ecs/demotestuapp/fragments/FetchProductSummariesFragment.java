package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ecs.demotestuapp.util.ECSDemoDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.products.ECSProduct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FetchProductSummariesFragment extends BaseAPIFragment {


    EditText etCTN;

    @Override
    public void onResume() {
        super.onResume();

        etCTN = getLinearLayout().findViewWithTag("et_one");
        etCTN.setHint("Add comma separated ctns");
        /*if(ECSDataHolder.INSTANCE.getEcsProducts()!=null){
            if(ECSDataHolder.INSTANCE.getEcsProducts().getProducts().size()!=0){
                etCTN.setText(ECSDataHolder.INSTANCE.getEcsProducts().getProducts().get(0).getCode());
            }
        }*/
    }

    public void executeRequest() {

        String ctn = etCTN.getText().toString().trim();

        if(ctn.isEmpty()){
            Toast.makeText(getActivity(),"CTN field can not be empty",Toast.LENGTH_SHORT).show();
            getProgressBar().setVisibility(View.GONE);
            return;
        }

        String[] split = ctn.split(",");

        List<String> al = new ArrayList<String>();
        al = Arrays.asList(split);

        ECSDemoDataHolder.INSTANCE.getEcsServices().fetchProductSummaries(al, new ECSCallback<List<ECSProduct>, Exception>() {
            @Override
            public void onResponse(List<ECSProduct> ecsProducts) {
                gotoResultActivity(getJsonStringFromObject(ecsProducts));
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

