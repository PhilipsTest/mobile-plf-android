package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.cart.ECSEntries;
import com.philips.platform.ecs.model.cart.ECSShoppingCart;

import java.util.ArrayList;
import java.util.List;

public class UpdateShoppingCartFragment extends BaseAPIFragment {


    EditText etQuantity;
    Spinner spinner;

    @Override
    public void onResume() {
        super.onResume();

        etQuantity = getLinearLayout().findViewWithTag("et_one");
        etQuantity.setText(1+"");
        spinner = getLinearLayout().findViewWithTag("spinner_one");

        fillSpinnerData(spinner);
    }

    public void executeRequest() {

        int quantity = 0;
        try {
            quantity = Integer.valueOf(etQuantity.getText().toString().trim());
        } catch (Exception e) {

        }

        String entryNumber = "123";

        if(spinner.getSelectedItem()!=null){
            entryNumber = spinner.getSelectedItem().toString();
        }

        ECSEntries ecsEntriesFromID = getECSEntriesFromID(entryNumber);

        if(ecsEntriesFromID == null){
            Toast.makeText(getActivity(),"ECSEntries Field is empty",Toast.LENGTH_SHORT).show();
            getProgressBar().setVisibility(View.GONE);
            return;
        }

        ECSDataHolder.INSTANCE.getEcsServices().updateShoppingCart(quantity, ecsEntriesFromID, new ECSCallback<ECSShoppingCart, Exception>() {
            @Override
            public void onResponse(ECSShoppingCart ecsShoppingCart) {

                ECSDataHolder.INSTANCE.setEcsShoppingCart(ecsShoppingCart);
                gotoResultActivity(getJsonStringFromObject(ecsShoppingCart));
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

        if (ECSDataHolder.INSTANCE.getEcsShoppingCart() != null) {

            List<ECSEntries> entries = ECSDataHolder.INSTANCE.getEcsShoppingCart().getEntries();

            if (entries!=null && entries.size() != 0) {

                for (ECSEntries ecsEntries : entries) {
                    ctns.add(ecsEntries.getProduct().getCode() + "");
                }

                fillSpinner(spinner, ctns);
            }
        }
    }

    private ECSEntries getECSEntriesFromID(String ctn) {

        if(ECSDataHolder.INSTANCE.getEcsShoppingCart()==null) return  null;

        List<ECSEntries> entries = ECSDataHolder.INSTANCE.getEcsShoppingCart().getEntries();
        if (entries.size() != 0) {

            for (ECSEntries ecsEntries : entries) {
                if (ctn.equalsIgnoreCase(ecsEntries.getProduct().getCode() + "")) {
                    return ecsEntries;
                }
            }

        }
        return null;
    }

    @Override
    public void clearData() {
        ECSDataHolder.INSTANCE.setEcsShoppingCart(null);
    }
}
