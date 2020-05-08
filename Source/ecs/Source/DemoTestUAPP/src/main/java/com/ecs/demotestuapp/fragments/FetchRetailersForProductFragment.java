package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.products.ECSProduct;
import com.philips.platform.ecs.model.retailers.ECSRetailerList;

import java.util.ArrayList;
import java.util.List;

public class FetchRetailersForProductFragment extends BaseAPIFragment {



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

        ECSProduct ecsProduct = getECSProductFromID(ctn);

        if(ecsProduct == null){
            Toast.makeText(getActivity(),"Product field can not be empty",Toast.LENGTH_SHORT).show();
            getProgressBar().setVisibility(View.GONE);
            return;
        }

        ECSDataHolder.INSTANCE.getEcsServices().fetchRetailers(ecsProduct, new ECSCallback<ECSRetailerList, Exception>() {
            @Override
            public void onResponse(ECSRetailerList ecsRetailerList) {

                gotoResultActivity(getJsonStringFromObject(ecsRetailerList));
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