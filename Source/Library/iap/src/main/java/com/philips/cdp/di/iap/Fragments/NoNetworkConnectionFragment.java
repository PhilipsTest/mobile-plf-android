/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.NetworkUtility;

public class NoNetworkConnectionFragment extends InAppBaseFragment {
    public static final String TAG = NoNetworkConnectionFragment.class.getSimpleName();

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.iap_network_error_view, container, false);

        Button iap_btnTryAgain = (Button) rootview.findViewById(R.id.iap_btnTryAgain);
        iap_btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtility.getInstance().isNetworkAvailable(getActivity())) {
                    getFragmentManager().popBackStackImmediate();
                }
            }
        });
        return rootview;
    }

    public static InAppBaseFragment createInstance(Bundle bundle, AnimationType animType) {
        NoNetworkConnectionFragment fragment = new NoNetworkConnectionFragment();
        bundle.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(bundle);

        return fragment;
    }
}
