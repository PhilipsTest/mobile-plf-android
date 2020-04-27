package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.products.ECSProduct;

public class FetchProductForCtnFragment extends BaseAPIFragment {

    EditText etCTN;

    @Override
    public void onResume() {
        super.onResume();

        etCTN = getLinearLayout().findViewWithTag("et_one");
        if(ECSDataHolder.INSTANCE.getEcsProducts()!=null){
            if(ECSDataHolder.INSTANCE.getEcsProducts().getProducts().size()!=0){
                etCTN.setText(ECSDataHolder.INSTANCE.getEcsProducts().getProducts().get(0).getCode());
            }
        }
    }

    public void executeRequest() {

        String ctn = etCTN.getText().toString().trim();

        ECSDataHolder.INSTANCE.getEcsServices().fetchProduct(ctn, new ECSCallback<ECSProduct, Exception>() {
            @Override
            public void onResponse(ECSProduct ecsProduct) {

                gotoResultActivity(getJsonStringFromObject(ecsProduct));
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

