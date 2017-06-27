/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.screens;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.adapters.BuyFromRetailersAdapter;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.response.retailers.StoreEntity;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.IAPConstant;

import java.util.ArrayList;
import java.util.UUID;

public class BuyFromRetailersFragment extends InAppBaseFragment implements BuyFromRetailersAdapter.BuyFromRetailersListener {

    public static final String TAG = BuyFromRetailersFragment.class.getName();

    private Context mContext;
    private RecyclerView mRecyclerView;
    private ArrayList<StoreEntity> mStoreEntity;
    private static final String ICELEADS_HATCH = "iceleads";
    private static final String CHANNEL_ADVISOR = "wheretobuy";
    private static final String CHANNEL_SIGHT = "channelsight";

    public static BuyFromRetailersFragment createInstance(Bundle args, InAppBaseFragment.AnimationType animType) {
        BuyFromRetailersFragment fragment = new BuyFromRetailersFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.iap_retailers_view, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.iap_retailer_list);
        if (getArguments().getSerializable(IAPConstant.IAP_RETAILER_INFO) != null)
            mStoreEntity = (ArrayList<StoreEntity>) getArguments().getSerializable(IAPConstant.IAP_RETAILER_INFO);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        IAPAnalytics.trackPage(IAPAnalyticsConstant.RETAILERS_LIST_PAGE_NAME);
        setTitleAndBackButtonVisibility(R.string.iap_retailer_title, true);
        if (mStoreEntity != null) {
            BuyFromRetailersAdapter mAdapter = new BuyFromRetailersAdapter(mContext, getFragmentManager(), mStoreEntity, this);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onClickAtRetailer(String buyURL, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(IAPConstant.IAP_BUY_URL, uuidWithSupplierLink(buyURL));
        bundle.putString(IAPConstant.IAP_STORE_NAME, name);
        addFragment(WebBuyFromRetailers.createInstance(bundle, AnimationType.NONE), null);
    }

    private String uuidWithSupplierLink(String buyURL) {
        String supplierLinkWithUUID = null;
        if (buyURL.contains(ICELEADS_HATCH)) {
            supplierLinkWithUUID = buyURL + "&CID=";
        } else if (buyURL.contains(CHANNEL_ADVISOR)) {
            supplierLinkWithUUID = buyURL + "&guid=";
        } else if (buyURL.contains(CHANNEL_SIGHT)) {
            supplierLinkWithUUID = buyURL + "&subTag=";
        }
        return supplierLinkWithUUID + String.valueOf(UUID.randomUUID());
    }
}
