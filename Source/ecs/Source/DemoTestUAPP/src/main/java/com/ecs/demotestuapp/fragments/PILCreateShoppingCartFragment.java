package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;

import com.philips.platform.ecs.microService.ECSServices;
import com.philips.platform.ecs.microService.callBack.ECSCallback;
import com.philips.platform.ecs.microService.error.ECSError;
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart;

public class PILCreateShoppingCartFragment extends BaseAPIFragment {

    EditText ctnET, quantityET;
    String ctn="";
    int quantity=1;

    @Override
    public void onResume() {
        super.onResume();
        ctnET= getLinearLayout().findViewWithTag("et_one");
        ctnET.setText(ctn);
        quantityET= getLinearLayout().findViewWithTag("et_two");

    }

    @Override
    void executeRequest() {
        if(!ctnET.getText().toString().trim().isEmpty()){
            ctn=ctnET.getText().toString().trim();
        }
        if(!quantityET.getText().toString().trim().isEmpty()){
            quantity = Integer.valueOf(quantityET.getText().toString().trim());
        }

        ECSServices microECSServices = new ECSServices(mAppInfraInterface);
        try{

            ECSCallback ecsCallback= new ECSCallback<ECSShoppingCart, ECSError>(){

                @Override
                public void onFailure(ECSError ecsError) {
                    String errorString = ecsError.getErrorMessage();
                    gotoResultActivity(errorString);
                    getProgressBar().setVisibility(View.GONE);
                }

                @Override
                public void onResponse(ECSShoppingCart result) {
                    gotoResultActivity(getJsonStringFromObject(result));
                    getProgressBar().setVisibility(View.GONE);
                }
            };



            microECSServices.createECSShoppingCart(ctn,quantity,ecsCallback);

        }catch (Exception e){
            e.printStackTrace();
            gotoResultActivity(e.getMessage());
            getProgressBar().setVisibility(View.GONE);
        }

    }


    @Override
    public void clearData() {
        ctnET.setText("");
        quantityET.setText("");
    }
}
