package com.philips.platform.prdemoapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.philips.cdp.prodreg.listener.RegisteredProductsListener;
import com.philips.cdp.prodreg.register.ProdRegHelper;
import com.philips.cdp.prodreg.register.RegisteredProduct;
import com.philips.platform.prdemoapp.adaptor.ProductAdapter;
import com.philips.platform.prdemoapplibrary.R;

import java.util.List;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class ProductListFragment extends Fragment {

    public static final String TAG = ProductListFragment.class.getName();
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registered_list, container, false);
        init(view);
        return view;
    }

    private void init(final View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        final TextView mVersion = (TextView) view.findViewById(R.id.txt_version);
        final ProdRegHelper prodRegHelper = new ProdRegHelper();
        mVersion.setText("versionName :" + prodRegHelper.getLibVersion());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
        ProdRegHelper prodRegHelper = new ProdRegHelper();
        prodRegHelper.getSignedInUserWithProducts(getContext()).getRegisteredProducts(new RegisteredProductsListener() {
            @Override
            public void getRegisteredProducts(final List<RegisteredProduct> registeredProducts, final long timeStamp) {
                final OnItemClickListener onItemClickListener = new OnItemClickListener() {
                    @Override
                    public void onItemClick(final RegisteredProduct registeredProduct) {
                        Bundle bundle = new Bundle();
                        bundle.putString("ctn", registeredProduct.getCtn());
                        bundle.putString("date", registeredProduct.getPurchaseDate());
                        bundle.putString("serial", registeredProduct.getSerialNumber());
                        ManualRegistrationFragment manualRegistrationFragment = new ManualRegistrationFragment();
                        manualRegistrationFragment.setArguments(bundle);
                        showFragment(manualRegistrationFragment);
                    }
                };
                final ProductAdapter productAdapter = new ProductAdapter(getActivity(), registeredProducts, onItemClickListener);
                progressBar.setVisibility(View.GONE);
                mRecyclerView.setAdapter(productAdapter);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showFragment(final Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.parent_layout, fragment,
                ManualRegistrationFragment.TAG);
        fragmentTransaction.addToBackStack(ManualRegistrationFragment.TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public interface OnItemClickListener {
        void onItemClick(RegisteredProduct item);
    }
}
