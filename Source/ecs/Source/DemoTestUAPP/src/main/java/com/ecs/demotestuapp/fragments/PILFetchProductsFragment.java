package com.ecs.demotestuapp.fragments;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.ecs.demotestuapp.model.FilterStateItem;
import com.ecs.demotestuapp.util.ECSDemoDataHolder;
import com.ecs.demotestuapp.util.PILDataHolder;
import com.philips.platform.ecs.microService.ECSServices;
import com.philips.platform.ecs.microService.callBack.ECSCallback;
import com.philips.platform.ecs.microService.error.ECSError;
import com.philips.platform.ecs.microService.error.ECSException;
import com.philips.platform.ecs.microService.model.filter.ECSSortType;
import com.philips.platform.ecs.microService.model.filter.ECSStockLevel;
import com.philips.platform.ecs.microService.model.filter.ProductFilter;
import com.philips.platform.ecs.microService.model.product.ECSProducts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PILFetchProductsFragment extends BaseAPIFragment {



    EditText offsetET, limitET, etCategory;
    CheckBox cbInStock, cbLowStock, cbOutOFStock;

    int offset = 0, limit = 20;
    String category;
    Spinner spinnerSortType, spinnerStockLevel;
    ArrayList<FilterStateItem> stockLevelList;
    String stockLevelOptions[] = {"( Select - Stock Level )", "InStock", "OutOfStock", "LowStock"};
    String sortOptions[] = {"( Select - Sort By )", "topRated", "priceAscending", "priceDescending", "discountPercentageAscending", "discountPercentageDescending"};

    @Override
    public void onResume() {
        super.onResume();

        offsetET = getLinearLayout().findViewWithTag("et_one");
        offsetET.setText(offset +"");
        limitET = getLinearLayout().findViewWithTag("et_two");
        limitET.setText(limit +"");
        etCategory = getLinearLayout().findViewWithTag("et_three");
        etCategory.setText("");

        cbInStock = getLinearLayout().findViewWithTag("checkBox_one");
        cbLowStock = getLinearLayout().findViewWithTag("checkBox_two");
        cbOutOFStock = getLinearLayout().findViewWithTag("checkBox_three");


        spinnerSortType  = getLinearLayout().findViewWithTag("spinner_sort_type");

        List<String> sortList = Arrays.asList(sortOptions);
        fillSpinner(spinnerSortType,sortList);

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


        Set<ECSStockLevel> stockLevelItems = new HashSet<>();

        if(cbInStock.isChecked()) stockLevelItems.add(ECSStockLevel.InStock);
        if(cbLowStock.isChecked()) stockLevelItems.add(ECSStockLevel.LowStock);
        if(cbOutOFStock.isChecked()) stockLevelItems.add(ECSStockLevel.OutOfStock);


        ECSSortType eCSSortType = null;
        if (spinnerSortType.getSelectedItem() != null && spinnerSortType.getSelectedItemPosition() != 0) {
             eCSSortType = ECSSortType.valueOf(spinnerSortType.getSelectedItem().toString());
        }
        ProductFilter productFilter = new ProductFilter(eCSSortType, stockLevelItems);
        productFilter.setSortType(eCSSortType);
        productFilter.setStockLevelSet(stockLevelItems);
        ECSServices microECSServices = new ECSServices(mAppInfraInterface);

        try {

            ECSCallback ecsCallback = new ECSCallback<ECSProducts, ECSError>() {
                @Override
                public void onResponse(ECSProducts result) {
                    PILDataHolder.INSTANCE.setProductList(result);
                    gotoResultActivity(getJsonStringFromObject(result));
                    getProgressBar().setVisibility(View.GONE);

                }

                @Override
                public void onFailure(ECSError ecsError) {
                    gotoResultActivity(ecsError.getErrorCode() +"\n"+ ecsError.getErrorMessage());
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
            gotoResultActivity(e.getErrorCode() +"\n"+ e.getMessage());
            getProgressBar().setVisibility(View.GONE);
        }

    }
    public void testMethod(ECSCallback<ECSProducts, ECSError> ecsCallback) {
        //productCategory:String?, limit:Int, offset:Int, productFilter: ProductFilter?, ecsCallback :ECSCallback<ECSProducts, ECSError>


    }

    @Override
    public void clearData() {
        ECSDemoDataHolder.INSTANCE.setEcsProducts(null);
    }
}
