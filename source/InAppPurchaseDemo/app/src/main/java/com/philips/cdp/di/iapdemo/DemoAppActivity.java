package com.philips.cdp.di.iapdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.cdp.di.iap.ShoppingCart.ShoppingCartData;
import com.philips.cdp.di.iap.activity.MainActivity;
import com.philips.cdp.di.iap.session.IAPHandler;
import com.philips.cdp.di.iap.session.IAPHandlerListner;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.Utility;

import java.util.ArrayList;

public class DemoAppActivity extends Activity implements View.OnClickListener, IAPHandlerListner {

    IAPHandler mIapHandler;

    private TextView mCountText = null;

    private ArrayList<ShoppingCartData> mProductArrayList = new ArrayList<>();

    private String[] mCatalogNumbers = {"HX8331/11", "HX8071/10"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_app_layout);

        ListView mProductListView = (ListView) findViewById(R.id.product_list);

        mIapHandler = new IAPHandler();

        FrameLayout mShoppingCart = (FrameLayout) findViewById(R.id.shoppingCart);
        mShoppingCart.setOnClickListener(this);
        populateProduct();

        ProductListAdapter mProductListAdapter = new ProductListAdapter(this, mProductArrayList);
        mProductListView.setAdapter(mProductListAdapter);

        mCountText = (TextView) findViewById(R.id.count_txt);

        mIapHandler.initApp(this, "", "");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!(Utility.isProgressDialogShowing())) {
            if (Utility.isInternetConnected(this)) {
                Utility.showProgressDialog(this, getString(R.string.loading_cart));
                mIapHandler.getCartQuantity(this);
            } else {
                Utility.showNetworkError(this, true);
            }
        }
    }

    private void populateProduct() {
        for (String mCatalogNumber : mCatalogNumbers) {
            ShoppingCartData product = new ShoppingCartData();
            product.setCtnNumber(mCatalogNumber.replaceAll("/", "_")); //need to be checked
            mProductArrayList.add(product);
        }
    }

    void addToCart(String ctnNumber) {
        if (!(Utility.isProgressDialogShowing())) {
            if (Utility.isInternetConnected(this)) {
                Utility.showProgressDialog(this, getString(R.string.adding_to_cart));
                mIapHandler.addItemtoCart(ctnNumber, this);
                IAPLog.d(IAPLog.DEMOAPPACTIVITY, "addItemtoCart");
            } else {
                Utility.showNetworkError(this, true);
            }
        }
    }

    /**
     * Buy Now particular product
     * @param ctnNumber product id
     */
    void buyNow(String ctnNumber) {
        //Buy Now
        addToCart(ctnNumber);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.shoppingCart:
                if (Utility.isInternetConnected(DemoAppActivity.this)) {
                    Intent myIntent = new Intent(DemoAppActivity.this, MainActivity.class);
                    startActivity(myIntent);
                } else {
                    Utility.showNetworkError(DemoAppActivity.this, false);
                }
                break;
        }
    }

    @Override
    public void onGetCartQuantity(final int quantity) {
        mCountText.setText(String.valueOf(quantity));
        Utility.dismissProgressDialog();
    }

    @Override
    public void onAddItemToCart(final String responseStatus) {
        if (responseStatus.equalsIgnoreCase("success")) {
            mIapHandler.getCartQuantity(this);
        } else if (responseStatus.equalsIgnoreCase("noStock")) {
            Toast.makeText(this, getString(R.string.no_stock), Toast.LENGTH_SHORT).show();
        }
        Utility.dismissProgressDialog();
    }
}