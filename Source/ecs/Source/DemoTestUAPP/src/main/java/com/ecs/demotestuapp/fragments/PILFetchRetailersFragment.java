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

import com.philips.platform.ecs.microService.ECSServices;
import com.philips.platform.ecs.microService.error.ECSException;

public class PILFetchRetailersFragment extends BaseAPIFragment {


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

        ECSServices ECSServices = new ECSServices(mAppInfraInterface);

        try {
            ECSServices.fetchRetailers(ctn, new com.philips.platform.ecs.microService.callBack.ECSCallback<com.philips.platform.ecs.microService.model.retailer.ECSRetailerList, com.philips.platform.ecs.microService.error.ECSError>() {
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


    @Override
    public void clearData() {

    }
}