package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.ecs.demotestuapp.util.ECSDataHolder;
import com.ecs.demotestuapp.util.PILDataHolder;
import com.philips.platform.ecs.microService.ECSServices;
import com.philips.platform.ecs.microService.callBack.ECSCallback;
import com.philips.platform.ecs.microService.error.ECSError;
import com.philips.platform.ecs.microService.error.ECSException;
import com.philips.platform.ecs.microService.model.filter.ECSSortType;
import com.philips.platform.ecs.microService.model.filter.ECSStockLevel;
import com.philips.platform.ecs.microService.model.filter.ProductFilter;
import com.philips.platform.ecs.microService.model.product.ECSProducts;

import java.util.Arrays;
import java.util.List;

public class PILFetchProductsFragment extends BaseAPIFragment {



    EditText offsetET, limitET, etCategory;
    int offset = 0, limit =20;
    String  category;
    Spinner spinnerSortType, spinnerStockLevel;

    String stockLevelOptions[] = {"( Select - Stock Level )","InStock","OutOfStock","LowStock"};
    String sortOptions[] = {"( Select - Sort By )","topRated","priceAscending","priceDescending","discountPercentageAscending","discountPercentageDescending"};

    @Override
    public void onResume() {
        super.onResume();

        offsetET = getLinearLayout().findViewWithTag("et_one");
        offsetET.setText(offset +"");
        limitET = getLinearLayout().findViewWithTag("et_two");
        limitET.setText(limit +"");
        etCategory = getLinearLayout().findViewWithTag("et_three");
        etCategory.setText("");


        spinnerSortType  = getLinearLayout().findViewWithTag("spinner_sort_type");

        List<String> sortList = Arrays.asList(sortOptions);
        fillSpinner(spinnerSortType,sortList);

        spinnerStockLevel  = getLinearLayout().findViewWithTag("spinner_stock_level");
        List<String> stockLevelList = Arrays.asList(stockLevelOptions);
        fillSpinner(spinnerStockLevel,stockLevelList);
    }



    public void executeRequest() {

        if(!limitET.getText().toString().trim().isEmpty()){
            limit = Integer.valueOf(limitET.getText().toString().trim());
        }

        if(!offsetET.getText().toString().trim().isEmpty()){
            offset = Integer.valueOf(offsetET.getText().toString().trim());
        }

        if(!etCategory.getText().toString().trim().isEmpty()){
            category = etCategory.getText().toString().trim();
        }


        ECSServices microECSServices = new ECSServices(mAppInfraInterface);
        try {

            ProductFilter productFilter= new ProductFilter();
            if(spinnerSortType.getSelectedItem()!=null && spinnerSortType.getSelectedItemPosition()!=0) {
                ECSSortType eCSSortType =   ECSSortType.valueOf(spinnerSortType.getSelectedItem().toString());
                productFilter.setSortType(eCSSortType);
            }

            if(spinnerStockLevel.getSelectedItem()!=null && spinnerStockLevel.getSelectedItemPosition()!=0) {
                ECSStockLevel eCSStockLevel=   ECSStockLevel.valueOf(spinnerStockLevel.getSelectedItem().toString());
                productFilter.setStockLevel(eCSStockLevel);
            }


            ECSCallback ecsCallback=  new ECSCallback<ECSProducts, ECSError>() {
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
            };

            microECSServices.fetchProducts(category, limit, offset,productFilter,ecsCallback );


         /*   microECSServices.fetchProducts(ecsCallback);
            microECSServices.fetchProducts(ecsCallback,category);
            microECSServices.fetchProducts(ecsCallback,category, limit);
            microECSServices.fetchProducts(ecsCallback,category, limit, offset);
            microECSServices.fetchProducts(ecsCallback,category, limit, offset,productFilter);*/

        } catch (ECSException e) {
            e.printStackTrace();
            gotoResultActivity(e.getMessage());
            getProgressBar().setVisibility(View.GONE);
        }

    }
    public void testMethod(ECSCallback<ECSProducts, ECSError> ecsCallback) {
        //productCategory:String?, limit:Int, offset:Int, productFilter: ProductFilter?, ecsCallback :ECSCallback<ECSProducts, ECSError>



    }

    @Override
    public void clearData() {
        ECSDataHolder.INSTANCE.setEcsProducts(null);
    }
}
