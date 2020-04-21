/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.screens;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.adapters.BuyFromRetailersAdapter;
import com.ecs.demouapp.ui.analytics.ECSAnalytics;
import com.ecs.demouapp.ui.analytics.ECSAnalyticsConstant;
import com.ecs.demouapp.ui.container.CartModelContainer;
import com.ecs.demouapp.ui.session.NetworkConstants;
import com.ecs.demouapp.ui.utils.ECSConstant;
import com.ecs.demouapp.ui.utils.ECSLog;
import com.ecs.demouapp.ui.utils.Utility;
import com.philips.platform.ecs.model.retailers.ECSRetailer;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.uid.view.widget.RecyclerViewSeparatorItemDecoration;

import java.util.ArrayList;
import java.util.UUID;

public class BuyFromRetailersFragment extends InAppBaseFragment implements BuyFromRetailersAdapter.BuyFromRetailersListener {

    public static final String TAG = BuyFromRetailersFragment.class.getName();

    private Context mContext;
    private RecyclerView mRecyclerView;
    private ArrayList<ECSRetailer> mStoreEntity;
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
        View rootView = inflater.inflate(R.layout.ecs_retailers_view, container, false);

        mRecyclerView = rootView.findViewById(R.id.iap_retailer_list);
        if (getArguments().getSerializable(ECSConstant.IAP_RETAILER_INFO) != null)
            mStoreEntity = (ArrayList<ECSRetailer>) getArguments().getSerializable(ECSConstant.IAP_RETAILER_INFO);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ECSAnalytics.trackPage(ECSAnalyticsConstant.RETAILERS_LIST_PAGE_NAME);
        setTitleAndBackButtonVisibility(R.string.iap_select_retailer, true);
        setCartIconVisibility(false);
        if (mStoreEntity != null) {
            BuyFromRetailersAdapter mAdapter = new BuyFromRetailersAdapter(mContext, getFragmentManager(), mStoreEntity, this);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addItemDecoration(new RecyclerViewSeparatorItemDecoration(getContext()));
        }
    }

    @Override
    public void onClickAtRetailer(String buyURL,  ECSRetailer storeEntity) {
        Bundle bundle = new Bundle();
        bundle.putString(ECSConstant.IAP_BUY_URL, uuidWithSupplierLink(buyURL));
        bundle.putString(ECSConstant.IAP_STORE_NAME, storeEntity.getName());
        bundle.putBoolean(ECSConstant.IAP_IS_PHILIPS_SHOP, new Utility().isPhilipsShop(storeEntity));
        addFragment(WebBuyFromRetailers.createInstance(bundle, AnimationType.NONE), null,true);
    }

    private String uuidWithSupplierLink(String buyURL) {
        AppConfigurationInterface mConfigInterface = CartModelContainer.getInstance().getAppInfraInstance().getConfigInterface();
        AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface.AppConfigurationError();

        String propositionId = (String) mConfigInterface.getPropertyForKey("propositionid", "IAP", configError);

        if (configError.getErrorCode() != null) {
            ECSLog.e(ECSLog.LOG, "VerticalAppConfig ==loadConfigurationFromAsset " + configError.getErrorCode().toString());
        }

        String supplierLinkWithUUID = null;
        if (buyURL.contains(ICELEADS_HATCH)) {
            supplierLinkWithUUID = buyURL + "&CID=";
        } else if (buyURL.contains(CHANNEL_ADVISOR)) {
            supplierLinkWithUUID = buyURL + "&guid=";
        } else if (buyURL.contains(CHANNEL_SIGHT)) {
            supplierLinkWithUUID = buyURL + "&subTag=";
        }
        return supplierLinkWithUUID + String.valueOf(UUID.randomUUID() + "&wtbSource=" + propositionId);
    }

}
