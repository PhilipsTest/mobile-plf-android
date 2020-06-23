package com.ecs.demotestuapp.util;


import com.philips.platform.ecs.microService.model.product.ECSProducts;

public enum PILDataHolder {

    INSTANCE;

    ECSProducts mProductList;


    public ECSProducts getProductList() {
        return mProductList;
    }

    public void setProductList(ECSProducts mProductList) {
        this.mProductList = mProductList;
    }
}
