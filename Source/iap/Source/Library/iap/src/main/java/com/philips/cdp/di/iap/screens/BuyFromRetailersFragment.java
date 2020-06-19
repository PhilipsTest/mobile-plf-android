/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.screens;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.adapters.BuyFromRetailersAdapter;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.response.retailers.StoreEntity;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.uid.view.widget.RecyclerViewSeparatorItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class BuyFromRetailersFragment extends InAppBaseFragment implements BuyFromRetailersAdapter.BuyFromRetailersListener {

    public static final String TAG = BuyFromRetailersFragment.class.getName();
    private static final long serialVersionUID = -5058484910842221415L;

    private Context mContext;
    private RecyclerView mRecyclerView;
    private ArrayList<StoreEntity> mStoreEntity;
    private String param;
    private Bundle mBundle;

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

        mRecyclerView = rootView.findViewById(R.id.iap_retailer_list);
        if (getArguments().getSerializable(IAPConstant.IAP_RETAILER_INFO) != null)
            mStoreEntity = (ArrayList<StoreEntity>) getArguments().getSerializable(IAPConstant.IAP_RETAILER_INFO);
        mBundle=getArguments();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        IAPAnalytics.trackPage(IAPAnalyticsConstant.RETAILERS_LIST_PAGE_NAME);
        setTitleAndBackButtonVisibility(R.string.iap_select_retailer, true);
        setCartIconVisibility(false);
        if (mStoreEntity != null) {
            BuyFromRetailersAdapter mAdapter = new BuyFromRetailersAdapter(mContext, getFragmentManager(), mStoreEntity, this,mBundle);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addItemDecoration(new RecyclerViewSeparatorItemDecoration(getContext()));
            tagRetailerList(mStoreEntity);
        }
    }

    @Override
    public void onClickAtRetailer(String buyURL,  StoreEntity storeEntity) {
        param = storeEntity.getXactparam();
        Bundle bundle = new Bundle();
        bundle.putString(IAPConstant.IAP_BUY_URL, uuidWithSupplierLink(buyURL));
        bundle.putString(IAPConstant.IAP_STORE_NAME, storeEntity.getName());
        bundle.putBoolean(IAPConstant.IAP_IS_PHILIPS_SHOP, new Utility().isPhilipsShop(storeEntity));
        addFragment(WebBuyFromRetailers.createInstance(bundle, AnimationType.NONE), null,true);
    }

    private String uuidWithSupplierLink(String buyURL) {
        AppConfigurationInterface mConfigInterface = CartModelContainer.getInstance().getAppInfraInstance().getConfigInterface();
        AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface.AppConfigurationError();

        String propositionId = (String) mConfigInterface.getPropertyForKey("propositionid", "IAP", configError);

        if (configError.getErrorCode() != null) {
            IAPLog.e(IAPLog.LOG, "VerticalAppConfig ==loadConfigurationFromAsset " + configError.getErrorCode().toString());
        }

        String supplierLinkWithUUID = buyURL.concat("&wtbSource=mobile_").concat(propositionId).concat("&").concat(param).concat("=");

        return supplierLinkWithUUID + String.valueOf(UUID.randomUUID());
    }

    private void tagRetailerList(ArrayList<StoreEntity> storeEntities){
        if(storeEntities.size()>0) {
            String retailerList = "";
            for (StoreEntity storeEntity : storeEntities) {
                retailerList += "|" + storeEntity.getName();
            }
            retailerList=retailerList.substring(1,retailerList.length()-1);

            final HashMap<String, String> map = new HashMap<>();
            map.put(IAPAnalyticsConstant.RETAILER_LIST, retailerList);
            String ProductInfo = IAPAnalytics.mCategory;
            ProductInfo+=";"+mBundle.getString("productCTN","");
            ProductInfo+=";"+mBundle.getInt("productStock",0);
            ProductInfo+=";"+mBundle.getString("productPrice","0.0");
            map.put(IAPAnalyticsConstant.PRODUCTS, ProductInfo);
            IAPAnalytics.trackMultipleActions(IAPAnalyticsConstant.SEND_DATA, map);
        }

    }

}
