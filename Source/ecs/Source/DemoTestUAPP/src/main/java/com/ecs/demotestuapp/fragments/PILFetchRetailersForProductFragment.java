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
import android.widget.Spinner;
import android.widget.Toast;

import com.ecs.demotestuapp.util.PILDataHolder;
import com.philips.platform.ecs.microService.ECSServices;
import com.philips.platform.ecs.microService.error.ECSException;
import com.philips.platform.ecs.microService.model.product.ECSProduct;

import java.util.ArrayList;
import java.util.List;

public class PILFetchRetailersForProductFragment extends BaseAPIFragment {



    Spinner spinner;
    String ctn = "unknown";

    @Override
    public void onResume() {
        super.onResume();

        spinner = getLinearLayout().findViewWithTag("spinner_one");
        fillSpinnerData(spinner);
    }

    public void executeRequest() {

        if (spinner.getSelectedItem() != null) {
            ctn = spinner.getSelectedItem().toString();
        }

        ECSServices ECSServices = new ECSServices(mAppInfraInterface);

        ECSProduct ecsProduct = getECSProductFromID(ctn);

        if(ecsProduct == null){
            Toast.makeText(getActivity(),"Product field can not be empty",Toast.LENGTH_SHORT).show();
            getProgressBar().setVisibility(View.GONE);
            return;
        }


        try {
            ECSServices.fetchRetailers(ecsProduct, new com.philips.platform.ecs.microService.callBack.ECSCallback<com.philips.platform.ecs.microService.model.retailer.ECSRetailerList, com.philips.platform.ecs.microService.error.ECSError>() {
                @Override
                public void onResponse(com.philips.platform.ecs.microService.model.retailer.ECSRetailerList result) {

                    gotoResultActivity(getJsonStringFromObject(result));
                    getProgressBar().setVisibility(View.GONE);
                }

                @Override
                public void onFailure(com.philips.platform.ecs.microService.error.ECSError ecsError) {

                    gotoResultActivity(ecsError.getErrorCode() +"\n"+ ecsError.getErrorMessage());
                    getProgressBar().setVisibility(View.GONE);
                }
            });
        } catch (ECSException e) {
            gotoResultActivity(e.getErrorCode() +"\n"+ e.getMessage());
            getProgressBar().setVisibility(View.GONE);
        }

    }


    private void fillSpinnerData(Spinner spinner) {
        ArrayList<String> ctns = new ArrayList<>();

        if (PILDataHolder.INSTANCE.getProductList() != null) {

            List<ECSProduct> products =PILDataHolder.INSTANCE.getProductList().getCommerceProducts();
            if (products.size() != 0) {

                for (ECSProduct ecsProduct : products) {
                    ctns.add(ecsProduct.getCtn());
                }

                fillSpinner(spinner, ctns);
            }
        }
    }


    private ECSProduct getECSProductFromID(String ctn) {

        if(PILDataHolder.INSTANCE.getProductList() ==null){
            return null;
        }

        List<ECSProduct> ecsProducts = PILDataHolder.INSTANCE.getProductList().getCommerceProducts();

        for (ECSProduct ecsProduct : ecsProducts) {
            if (ecsProduct.getCtn().equalsIgnoreCase(ctn)) {
                return ecsProduct;
            }
        }


        return null;
    }

    @Override
    public void clearData() {

    }
}