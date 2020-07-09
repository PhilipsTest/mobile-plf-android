/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */

package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ecs.demotestuapp.util.PILDataHolder;
import com.philips.platform.ecs.microService.ECSServices;
import com.philips.platform.ecs.microService.callBack.ECSCallback;
import com.philips.platform.ecs.microService.error.ECSError;
import com.philips.platform.ecs.microService.error.ECSException;
import com.philips.platform.ecs.microService.model.product.ECSProduct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PILFetchProductSummariesFragment extends BaseAPIFragment {


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

        ECSServices ECSServices = new ECSServices(mAppInfraInterface);

        try {
            ECSServices.fetchProductSummaries(al, new ECSCallback<List<ECSProduct>, ECSError>() {
                @Override
                public void onResponse(List<ECSProduct> ecsProducts) {
                    gotoResultActivity(getJsonStringFromObject(ecsProducts));
                    getProgressBar().setVisibility(View.GONE);

                    if( PILDataHolder.INSTANCE.getProductList()!=null) {
                        if( PILDataHolder.INSTANCE.getProductList().getCommerceProducts()!=null) {
                            PILDataHolder.INSTANCE.getProductList().getCommerceProducts().clear();
                            PILDataHolder.INSTANCE.getProductList().getCommerceProducts().addAll(ecsProducts);
                        }
                    }
                }

                @Override
                public void onFailure(ECSError ecsError) {

                    gotoResultActivity(ecsError.getErrorCode() +"\n"+ ecsError.getErrorMessage());
                    getProgressBar().setVisibility(View.GONE);
                }
            });
        } catch (ECSException e) {

            gotoResultActivity(e.getErrorCode() +"\n"+ e.getMessage());
            getProgressBar().setVisibility(View.GONE);

        }

    }

    @Override
    public void clearData() {

    }
}

