package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.ecs.demotestuapp.util.PILDataHolder;
import com.philips.platform.ecs.microService.MicroECSServices;
import com.philips.platform.ecs.microService.callBack.ECSCallback;
import com.philips.platform.ecs.microService.error.ECSError;
import com.philips.platform.ecs.microService.error.ECSException;
import com.philips.platform.ecs.microService.model.filter.ECSSortType;
import com.philips.platform.ecs.microService.model.filter.ProductFilter;
import com.philips.platform.ecs.microService.model.product.ECSProducts;

public class PILFetchProductsFragment extends BaseAPIFragment {



    EditText etPageNumber,etPageSize, etModifiedSince, etCategory;
    int  pageSize = 20,pageNumber =0;
    String modifiedSince , category;

    @Override
    public void onResume() {
        super.onResume();

        etPageNumber = getLinearLayout().findViewWithTag("et_one");
        etPageNumber.setText(pageNumber+"");
        etPageSize = getLinearLayout().findViewWithTag("et_two");
        etPageSize.setText(pageSize+"");
        etCategory = getLinearLayout().findViewWithTag("et_three");
        etModifiedSince = getLinearLayout().findViewWithTag("et_four");

    }

    public void executeRequest() {

        if(!etPageSize.getText().toString().trim().isEmpty()){
            pageSize = Integer.valueOf(etPageSize.getText().toString().trim());
        }

        if(!etPageNumber.getText().toString().trim().isEmpty()){
            pageNumber = Integer.valueOf(etPageNumber.getText().toString().trim());
        }

        if(!etCategory.getText().toString().trim().isEmpty()){
            category = etCategory.getText().toString().trim();
        }

        if(!etModifiedSince.getText().toString().trim().isEmpty()){
            modifiedSince = etModifiedSince.getText().toString().trim();
        }

        MicroECSServices microECSServices = new MicroECSServices(mAppInfraInterface);
        try {
            /*todo
            * These inputs to move in DemoUApp UI
            * */
            ProductFilter productFilter= new ProductFilter();
            productFilter.setSortType(ECSSortType.priceDescending);
            if(null!=modifiedSince)productFilter.setModifiedSince(modifiedSince);
           // productFilter.setStockLevel( ECSStockLevel.InStock);
            microECSServices.fetchProducts(category,pageNumber, pageSize,productFilter, new ECSCallback<ECSProducts, ECSError>() {
                @Override
                public void onResponse(ECSProducts result) {
                    PILDataHolder.INSTANCE.setProductList(result);
                    gotoResultActivity(getJsonStringFromObject(result));
                    getProgressBar().setVisibility(View.GONE);

                }

                @Override
                public void onFailure(ECSError ecsError) {
                    String errorString = ecsError.getErrorMessage();
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


    @Override
    public void clearData() {
        ECSDataHolder.INSTANCE.setEcsProducts(null);
    }
}
