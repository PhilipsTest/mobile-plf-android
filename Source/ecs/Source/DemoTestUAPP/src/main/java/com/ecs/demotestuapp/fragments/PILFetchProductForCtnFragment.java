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

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.ecs.demotestuapp.util.PILDataHolder;
import com.philips.platform.ecs.microService.ECSServices;
import com.philips.platform.ecs.microService.error.ECSError;
import com.philips.platform.ecs.microService.error.ECSException;
import com.philips.platform.ecs.microService.model.product.ECSProduct;

public class PILFetchProductForCtnFragment extends BaseAPIFragment {

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

        ECSServices ECSServices = new ECSServices(mAppInfraInterface);

        String ctn = etCTN.getText().toString().trim();

        try {
            ECSServices.fetchProduct(ctn, new com.philips.platform.ecs.microService.callBack.ECSCallback<ECSProduct, ECSError>() {
                @Override
                public void onResponse(ECSProduct result) {
                    gotoResultActivity(getJsonStringFromObject(result));

                    if( PILDataHolder.INSTANCE.getProductList()!=null) {
                        if( PILDataHolder.INSTANCE.getProductList().getCommerceProducts()!=null) {
                            PILDataHolder.INSTANCE.getProductList().getCommerceProducts().clear();
                            PILDataHolder.INSTANCE.getProductList().getCommerceProducts().add(result);
                        }
                    }
                    getProgressBar().setVisibility(View.GONE);
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

