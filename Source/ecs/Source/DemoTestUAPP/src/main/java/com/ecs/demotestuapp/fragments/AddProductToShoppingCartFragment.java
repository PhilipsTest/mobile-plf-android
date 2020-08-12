package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.ecs.demotestuapp.util.ECSDemoDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.model.products.ECSProduct;

import java.util.ArrayList;
import java.util.List;

public class AddProductToShoppingCartFragment extends BaseAPIFragment {



    Spinner spinner;

    @Override
    public void onResume() {
        super.onResume();
        spinner = getLinearLayout().findViewWithTag("spinner_one");
        fillSpinnerData(spinner);

    }

    public void executeRequest() {

        String ctn ="xyz";
        if(spinner.getSelectedItem()!=null){
            ctn = spinner.getSelectedItem().toString();
        }

        ECSProduct ecsProduct = getECSProductFromID(ctn);

        if(ecsProduct == null){
            Toast.makeText(getActivity(),"ECSProduct field can not be empty",Toast.LENGTH_SHORT).show();
            getProgressBar().setVisibility(View.GONE);
            return;
        }

        ECSDemoDataHolder.INSTANCE.getEcsServices().addProductToShoppingCart(ecsProduct, new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart ecsShoppingCart) {
                ECSDemoDataHolder.INSTANCE.setEcsShoppingCart(ecsShoppingCart);
                gotoResultActivity(getJsonStringFromObject(ecsProduct));
                getProgressBar().setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e, ECSError ecsError) {
                String errorString = getFailureString(e, ecsError);
                gotoResultActivity(errorString);
                getProgressBar().setVisibility(View.GONE);
            }
        });


    }

    private void fillSpinnerData(Spinner spinner) {
        ArrayList<String> ctns = new ArrayList<>();

        if(ECSDemoDataHolder.INSTANCE.getEcsProducts()!=null){

            List<ECSProduct> products = ECSDemoDataHolder.INSTANCE.getEcsProducts().getProducts();
            if(products.size()!=0) {

                for(ECSProduct ecsProduct:products){
                    ctns.add(ecsProduct.getCode());
                }

                fillSpinner(spinner,ctns);
            }
        }
    }

    private ECSProduct getECSProductFromID(String ctn) {

        if(ECSDemoDataHolder.INSTANCE.getEcsProducts() == null){
            return null;
        }
        List<ECSProduct> ecsProducts = ECSDemoDataHolder.INSTANCE.getEcsProducts().getProducts();

        for(ECSProduct ecsProduct:ecsProducts){
            if(ecsProduct.getCode().equalsIgnoreCase(ctn)){
                return ecsProduct;
            }
        }


        return null;
    }


    @Override
    public void clearData() {
        ECSDemoDataHolder.INSTANCE.setEcsShoppingCart(null);
    }
}


