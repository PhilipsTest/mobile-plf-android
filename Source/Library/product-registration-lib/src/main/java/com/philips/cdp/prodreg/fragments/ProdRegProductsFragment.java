/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.cdp.prodreg.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.philips.cdp.prodreg.adaptor.ProdRegProductsAdapter;
import com.philips.cdp.prodreg.listener.RegisteredProductsListener;
import com.philips.cdp.prodreg.register.ProdRegHelper;
import com.philips.cdp.prodreg.register.Product;
import com.philips.cdp.prodreg.register.RegisteredProduct;
import com.philips.cdp.product_registration_lib.R;

import java.util.List;

public class ProdRegProductsFragment extends ProdRegBaseFragment {

    public static final String TAG = ProdRegProductsFragment.class.getName();

    public interface OnItemClickListener {
        void onItemClick(RegisteredProduct item);
    }

    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;

    @Override
    public int getActionbarTitleResId() {
        return R.string.PPR_NavBar_Title;
    }

    @Override
    public boolean getBackButtonState() {
        return true;
    }

    @Override
    public List<RegisteredProduct> getRegisteredProducts() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prodreg_registered_products, container, false);
        init(view);
        return view;
    }

    private void init(final View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
        ProdRegHelper prodRegHelper = new ProdRegHelper();
        prodRegHelper.init(getActivity());
        prodRegHelper.getSignedInUserWithProducts().getRegisteredProducts(new RegisteredProductsListener() {
            @Override
            public void getRegisteredProducts(final List<RegisteredProduct> registeredProducts, final long timeStamp) {
                final OnItemClickListener onItemClickListener = new OnItemClickListener() {
                    @Override
                    public void onItemClick(final RegisteredProduct registeredProduct) {
                        Product product = new Product(registeredProduct.getCtn(), registeredProduct.getSector(), registeredProduct.getCatalog());
                        product.setSerialNumber(registeredProduct.getSerialNumber());
                        invokeProdRegFragment(product);
                    }
                };
                final ProdRegProductsAdapter productAdapter = new ProdRegProductsAdapter(getActivity(), registeredProducts, onItemClickListener);
                progressBar.setVisibility(View.GONE);
                mRecyclerView.setAdapter(productAdapter);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void invokeProdRegFragment(Product product) {
        ProdRegRegistrationFragment prodRegRegistrationFragment = new ProdRegRegistrationFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        prodRegRegistrationFragment.setArguments(bundle);
        showFragment(prodRegRegistrationFragment);
    }
}
