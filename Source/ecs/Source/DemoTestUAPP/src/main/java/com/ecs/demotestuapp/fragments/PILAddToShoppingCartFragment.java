package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;

import com.ecs.demotestuapp.util.PILDataHolder;
import com.philips.platform.ecs.microService.ECSServices;
import com.philips.platform.ecs.microService.callBack.ECSCallback;
import com.philips.platform.ecs.microService.error.ECSError;
import com.philips.platform.ecs.microService.error.ECSException;
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart;

public class PILAddToShoppingCartFragment extends  BaseAPIFragment {

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
        }else{
            quantity=1;
        }

        ECSServices microECSServices = new ECSServices(mAppInfraInterface);
        try{

            ECSCallback ecsCallback= new ECSCallback<ECSShoppingCart, ECSError>(){

                @Override
                public void onFailure(ECSError ecsError) {
                    gotoResultActivity(ecsError.getErrorCode() +"\n"+ ecsError.getErrorMessage());
                    getProgressBar().setVisibility(View.GONE);

                }

                @Override
                public void onResponse(ECSShoppingCart result) {
                    gotoResultActivity(getJsonStringFromObject(result));
                    PILDataHolder.INSTANCE.setEcsShoppingCart(result);
                    getProgressBar().setVisibility(View.GONE);
                }
            };



            microECSServices.addProductToShoppingCart(ctn,quantity,ecsCallback);

        }catch (ECSException e){
            gotoResultActivity(e.getErrorCode() +"\n"+ e.getMessage());
            getProgressBar().setVisibility(View.GONE);
        }
    }

    @Override
    public void clearData() {
        ctnET.setText("");
        quantityET.setText("");
    }
}
