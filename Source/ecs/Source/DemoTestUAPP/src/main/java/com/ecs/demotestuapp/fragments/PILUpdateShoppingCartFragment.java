package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.ecs.demotestuapp.util.PILDataHolder;
import com.philips.platform.ecs.microService.ECSServices;
import com.philips.platform.ecs.microService.callBack.ECSCallback;
import com.philips.platform.ecs.microService.error.ECSError;
import com.philips.platform.ecs.microService.error.ECSException;
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.microService.model.cart.ECSItem;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class PILUpdateShoppingCartFragment extends  BaseAPIFragment {
    EditText  quantityET;
    int quantity=1;
    Spinner itemsSpinner; //todo
    List<ECSItem> items;
    ECSItem item;


    @Override
    public void onResume() {
        super.onResume();
        quantityET= getLinearLayout().findViewWithTag("et_one");
        itemsSpinner  = getLinearLayout().findViewWithTag("spinner_items");
        setSpinner();
    }

    private void setSpinner(){
      items= PILDataHolder.INSTANCE.getEcsShoppingCart().getData().getAttributes().getItems();
        ListIterator<ECSItem> it = items.listIterator();
        List<String> ctnList = new ArrayList<String>();
        ctnList.add("( Select - CTN )");
        while(it.hasNext()){
            ctnList.add(it.next().getCtn());
        }
        fillSpinner(itemsSpinner,ctnList);
    }

    @Override
    void executeRequest() {
        if(!quantityET.getText().toString().trim().isEmpty()){
            quantity = Integer.valueOf(quantityET.getText().toString().trim());
        }

        ECSServices microECSServices = new ECSServices(mAppInfraInterface);
        try{

            if(itemsSpinner.getSelectedItem()!=null && itemsSpinner.getSelectedItemPosition()!=0) {
                item= items.get(itemsSpinner.getSelectedItemPosition()-1);
            }

            if(item==null){
                getProgressBar().setVisibility(View.GONE);
                return;
            }

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
