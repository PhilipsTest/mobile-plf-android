package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.philips.platform.ecs.microService.ECSServices;
import com.philips.platform.ecs.microService.callBack.ECSCallback;
import com.philips.platform.ecs.microService.error.ECSError;
import com.philips.platform.ecs.microService.error.ECSException;
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.microService.model.cart.Item;

public class PILUpdateShoppingCartFragment extends  BaseAPIFragment {
    EditText  quantityET;
    int quantity=1;
    Spinner itemsSpinner; //todo
    Item item;


    @Override
    public void onResume() {
        super.onResume();
        quantityET= getLinearLayout().findViewWithTag("et_one");

    }

    @Override
    void executeRequest() {
        if(!quantityET.getText().toString().trim().isEmpty()){
            quantity = Integer.valueOf(quantityET.getText().toString().trim());
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
                    getProgressBar().setVisibility(View.GONE);
                }
            };


            microECSServices.updateShoppingCart(item,quantity,ecsCallback);

        }catch (ECSException e){
            gotoResultActivity(e.getErrorCode() +"\n"+ e.getMessage());
            getProgressBar().setVisibility(View.GONE);
        }
    }

    @Override
    public void clearData() {

    }
}
