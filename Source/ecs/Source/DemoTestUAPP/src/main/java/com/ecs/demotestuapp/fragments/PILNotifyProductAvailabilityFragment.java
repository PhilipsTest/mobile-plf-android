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
import com.philips.platform.ecs.microService.ECSServices;
import com.philips.platform.ecs.microService.callBack.ECSCallback;
import com.philips.platform.ecs.microService.error.ECSError;
import com.philips.platform.ecs.microService.error.ECSException;
import com.philips.platform.pif.DataInterface.USR.UserDataInterfaceException;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;

import java.util.ArrayList;
import java.util.HashMap;

public class PILNotifyProductAvailabilityFragment extends  BaseAPIFragment {

    EditText emailET, ctnET;
    String email ="",ctn = "";

    @Override
    public void onResume() {
        super.onResume();
        emailET = getLinearLayout().findViewWithTag("et_one");
        emailET.setText(email);
        ctnET = getLinearLayout().findViewWithTag("et_two");


        ArrayList<String> userDataMap = new ArrayList<>();
        HashMap<String, Object> userDetails = null;
        userDataMap.add(UserDetailConstants.EMAIL);
        try{
            if(ECSDataHolder.INSTANCE.getUserDataInterface()!=null)
                userDetails = ECSDataHolder.INSTANCE.getUserDataInterface().getUserDetails(userDataMap);

        } catch (UserDataInterfaceException e) {
            e.printStackTrace();
        }

        if(userDetails!=null) {
            String email = (String) userDetails.get(UserDetailConstants.EMAIL);
            emailET.setText(email);
        }

    }


    @Override
    void executeRequest() {
        if(!emailET.getText().toString().trim().isEmpty()){
            email = emailET.getText().toString().trim();
        }else{
            email = "";
        }
        if(!ctnET.getText().toString().trim().isEmpty()){
            ctn = ctnET.getText().toString().trim();
        }else{
            ctn="";
        }

        ECSServices microECSServices = new ECSServices(mAppInfraInterface);
        try{

            ECSCallback ecsCallback= new ECSCallback<Boolean, ECSError>(){

                @Override
                public void onFailure(ECSError ecsError) {
                    gotoResultActivity(ecsError.getErrorCode() +"\n"+ ecsError.getErrorMessage());
                    getProgressBar().setVisibility(View.GONE);

                }

                @Override
                public void onResponse(Boolean result) {
                    gotoResultActivity(getJsonStringFromObject(result));
                    getProgressBar().setVisibility(View.GONE);
                }
            };

            microECSServices.registerForProductAvailability(email,ctn,ecsCallback);

        }catch (ECSException e){
            gotoResultActivity(e.getErrorCode() +"\n"+ e.getMessage());
            getProgressBar().setVisibility(View.GONE);
        }
    }

    @Override
    public void clearData() {
        emailET.setText("");
        ctnET.setText("");
    }
}
