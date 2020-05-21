/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.screens;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.activity.IAPActivity;
import com.philips.cdp.di.iap.session.NetworkConstants;

public class EmptyPurchaseHistoryFragment extends InAppBaseFragment
        implements View.OnClickListener {
    public static final String TAG = EmptyPurchaseHistoryFragment.class.getName();
    private static final long serialVersionUID = -5954433745026762067L;
    private Button mContinueShoppingBtn;

    public static EmptyPurchaseHistoryFragment createInstance
            (Bundle args, InAppBaseFragment.AnimationType animType) {
        EmptyPurchaseHistoryFragment fragment = new EmptyPurchaseHistoryFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.iap_empty_puchase_history, container, false);
        mContinueShoppingBtn = rootView.findViewById(R.id.btn_continue_shopping);
        mContinueShoppingBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleAndBackButtonVisibility(R.string.iap_my_orders, false);
        setCartIconVisibility(false);
    }


    @Override
    public boolean handleBackEvent() {
        Fragment fragment = getFragmentManager().findFragmentByTag(ProductCatalogFragment.TAG);
        if (fragment == null && getActivity() != null && getActivity() instanceof IAPActivity) {
            finishActivity();
        } else {
            getFragmentManager().popBackStack();
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_continue_shopping) {
            if (!isNetworkConnected()) return;
            addFragment(ProductCatalogFragment.createInstance(new Bundle(),
                    AnimationType.NONE), ProductCatalogFragment.TAG,true);

        }
    }
}
