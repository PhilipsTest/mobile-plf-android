package com.philips.cdp.di.iap.Fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.ShoppingCart.ShoppingCartData;
import com.philips.cdp.di.iap.adapters.OrderProductAdapter;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.IAPLog;

import java.util.ArrayList;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class OrderSummaryFragment extends BaseAnimationSupportFragment {
    private RecyclerView mOrderListView;
    private OrderProductAdapter mAdapter;
    private ArrayList<ShoppingCartData> mShoppingCartDataList;

    @Override
    public void onResume() {
        super.onResume();
        setTitle(R.string.iap_order_summary);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mOrderListView.setLayoutManager(layoutManager);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.iap_order_summary_fragment, container, false);
        IAPLog.d(IAPLog.ORDER_SUMMARY_FRAGMENT, "OrderSummaryFragment ");
        setShoppingCartAdaptor(rootView);
        return rootView;
    }

    private void setShoppingCartAdaptor(final View rootView) {
        mOrderListView = (RecyclerView) rootView.findViewById(R.id.order_summary);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mOrderListView.setLayoutManager(layoutManager);
        mShoppingCartDataList = new ArrayList<ShoppingCartData>();
        mShoppingCartDataList = CartModelContainer.getInstance().getShoppingCartData();
        IAPLog.d(IAPLog.ORDER_SUMMARY_FRAGMENT, "Shopping Cart list = " + mShoppingCartDataList);
        mAdapter = new OrderProductAdapter(getContext(), mShoppingCartDataList);
        mOrderListView.setAdapter(mAdapter);
    }

    public static OrderSummaryFragment createInstance(Bundle args, AnimationType animType) {
        OrderSummaryFragment fragment = new OrderSummaryFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);

        return fragment;
    }
}
