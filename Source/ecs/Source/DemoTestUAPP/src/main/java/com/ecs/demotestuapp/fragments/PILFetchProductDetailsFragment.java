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

import com.philips.platform.ecs.microService.MicroECSServices;
import com.philips.platform.ecs.microService.callBack.ECSCallback;
import com.philips.platform.ecs.microService.error.ECSError;
import com.philips.platform.ecs.microService.error.ECSException;
import com.philips.platform.ecs.microService.model.product.ECSProduct;

import java.util.ArrayList;

public class PILFetchProductDetailsFragment extends BaseAPIFragment {


    Spinner spinner;
    String ctn = "HX3631/06";

    @Override
    public void onResume() {
        super.onResume();

        spinner = getLinearLayout().findViewWithTag("spinner_one");
        fillSpinnerData(spinner);
    }

    public void executeRequest() {

        MicroECSServices microECSServices = new MicroECSServices(mAppInfraInterface);

        if(spinner.getSelectedItem()!=null) {
             ctn = spinner.getSelectedItem().toString();
        }

            ECSProduct ecsProduct = new ECSProduct(null,ctn,null);

        if(ecsProduct == null){
            Toast.makeText(getActivity(),"Product field can not be empty",Toast.LENGTH_SHORT).show();
            getProgressBar().setVisibility(View.GONE);
            return;
        }

        try {
            microECSServices.fetchProductDetails(ecsProduct, new ECSCallback<ECSProduct, ECSError>() {
                    @Override
                    public void onResponse(ECSProduct ecsProduct) {
                        gotoResultActivity(getJsonStringFromObject(ecsProduct));
                        getProgressBar().setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(ECSError ecsError) {
                        String errorString = ecsError.toString();
                        gotoResultActivity(errorString);
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
        ctns.add(ctn);
        fillSpinner(spinner,ctns);
    }



 /*  private void fillSpinnerData(Spinner spinner) {
        ArrayList<String> ctns = new ArrayList<>();

        if(ECSDataHolder.INSTANCE.getEcsProducts()!=null){

            List<ECSProduct> products = ECSDataHolder.INSTANCE.getEcsProducts().getProducts();
            if(products.size()!=0) {

                for(ECSProduct ecsProduct:products){
                    ctns.add(ecsProduct.getCode());
                }

                fillSpinner(spinner,ctns);
            }
        }else{
            //add a default product

        }
    }
*/

   /* private ECSProduct getECSProductFromID(String ctn) {

        if(ECSDataHolder.INSTANCE.getEcsProducts()==null){
            return null;
        }
        List<ECSProduct> ecsProducts = ECSDataHolder.INSTANCE.getEcsProducts().getProducts();

        for(ECSProduct ecsProduct:ecsProducts){
            if(ecsProduct.getCode().equalsIgnoreCase(ctn)){
                return ecsProduct;
            }
        }
        return ecsProducts.get(0);
    }*/


    @Override
    public void clearData() {

    }
}
