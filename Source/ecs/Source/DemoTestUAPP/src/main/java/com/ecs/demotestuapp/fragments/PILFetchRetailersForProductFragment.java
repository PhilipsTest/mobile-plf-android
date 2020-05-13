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

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.microService.MicroECSServices;
import com.philips.platform.ecs.microService.error.ECSException;
import com.philips.platform.ecs.model.products.ECSProduct;

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

        MicroECSServices microECSServices = new MicroECSServices(mAppInfraInterface);

        ECSProduct ecsProduct = getECSProductFromID(ctn);

        if(ecsProduct == null){
            Toast.makeText(getActivity(),"Product field can not be empty",Toast.LENGTH_SHORT).show();
            getProgressBar().setVisibility(View.GONE);
            return;
        }

        //TODO

        com.philips.platform.ecs.microService.model.product.ECSProduct ecsProduct1 = new com.philips.platform.ecs.microService.model.product.ECSProduct(null,"HX3631/06",null);


        try {
            microECSServices.fetchRetailers(ecsProduct1, new com.philips.platform.ecs.microService.callBack.ECSCallback<com.philips.platform.ecs.microService.model.retailers.ECSRetailerList, com.philips.platform.ecs.microService.error.ECSError>() {
                @Override
                public void onResponse(com.philips.platform.ecs.microService.model.retailers.ECSRetailerList result) {

                    gotoResultActivity(getJsonStringFromObject(result));
                    getProgressBar().setVisibility(View.GONE);
                }

                @Override
                public void onFailure(com.philips.platform.ecs.microService.error.ECSError ecsError) {

                    gotoResultActivity(ecsError.getErrorMessage());
                    getProgressBar().setVisibility(View.GONE);
                }
            });
        } catch (ECSException e) {
            e.printStackTrace();

            gotoResultActivity(e.getMessage());
            getProgressBar().setVisibility(View.GONE);
        }

    }


    private void fillSpinnerData(Spinner spinner) {
        ArrayList<String> ctns = new ArrayList<>();

        if (ECSDataHolder.INSTANCE.getEcsProducts() != null) {

            List<ECSProduct> products = ECSDataHolder.INSTANCE.getEcsProducts().getProducts();
            if (products.size() != 0) {

                for (ECSProduct ecsProduct : products) {
                    ctns.add(ecsProduct.getCode());
                }

                fillSpinner(spinner, ctns);
            }
        }
    }


    private ECSProduct getECSProductFromID(String ctn) {

        if(ECSDataHolder.INSTANCE.getEcsProducts() ==null){
            return null;
        }

        List<ECSProduct> ecsProducts = ECSDataHolder.INSTANCE.getEcsProducts().getProducts();

        for (ECSProduct ecsProduct : ecsProducts) {
            if (ecsProduct.getCode().equalsIgnoreCase(ctn)) {
                return ecsProduct;
            }
        }


        return null;
    }

    @Override
    public void clearData() {

    }
}