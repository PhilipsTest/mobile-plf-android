package com.ecs.demotestuapp.util;


import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.microService.model.product.ECSProducts;

public enum PILDataHolder {

    INSTANCE;

    ECSProducts mProductList;
    ECSShoppingCart ecsShoppingCart;

    public ECSShoppingCart getEcsShoppingCart() {
        return ecsShoppingCart;
    }
    public void setEcsShoppingCart(ECSShoppingCart ecsShoppingCart) {
        this.ecsShoppingCart = ecsShoppingCart;
    }




    public ECSProducts getProductList() {
        return mProductList;
    }

    public void setProductList(ECSProducts mProductList) {
        this.mProductList = mProductList;
    }
}
