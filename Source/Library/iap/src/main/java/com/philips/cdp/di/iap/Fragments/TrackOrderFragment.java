/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.IAPConstant;

public class TrackOrderFragment extends BaseAnimationSupportFragment
        implements View.OnClickListener {

    public static final String TAG = TrackOrderFragment.class.getName();
    private Context mContext;
    private TextView mOrderId;
    private TextView mTrackingId;
    private TextView mBillingName;
    private TextView mBillingAddress;
    private Button mTrackOrderBtn;
    private String mOrderTrackUrl;

    public static TrackOrderFragment createInstance
            (Bundle args, BaseAnimationSupportFragment.AnimationType animType) {
        TrackOrderFragment fragment = new TrackOrderFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.iap_track_my_order, container, false);
        mTrackOrderBtn = (Button) rootView.findViewById(R.id.btn_track);
        mOrderId = (TextView) rootView.findViewById(R.id.tv_track_order_number);
        mTrackingId = (TextView) rootView.findViewById(R.id.tv_track_order_text);
        mBillingName = (TextView) rootView.findViewById(R.id.tv_shipping_first_name);
        mBillingAddress = (TextView) rootView.findViewById(R.id.tv_shipping_address);

        Bundle bundle = getArguments();
        if (null != bundle) {
            if (bundle.containsKey(IAPConstant.PURCHASE_ID))
                mOrderId.setText("#" + bundle.getString(IAPConstant.PURCHASE_ID));
            if (bundle.containsKey(IAPConstant.TRACKING_ID))
                mTrackingId.setText("You can track your package anytime with tracking number " + bundle.getString(IAPConstant.TRACKING_ID));
            if (bundle.containsKey(IAPConstant.DELIVERY_NAME))
                mBillingName.setText(bundle.getString(IAPConstant.DELIVERY_NAME));
            if (bundle.containsKey(IAPConstant.ADD_DELIVERY_ADDRESS))
                mBillingAddress.setText(bundle.getString(IAPConstant.ADD_DELIVERY_ADDRESS));
            if (bundle.containsKey(IAPConstant.ORDER_TRACK_URL))
                mOrderTrackUrl = bundle.getString(IAPConstant.ORDER_TRACK_URL);
        }

        mTrackOrderBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleAndBackButtonVisibility(R.string.iap_track_order_title, true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_track) {
            if (mOrderTrackUrl != null) {
                launchFedex();
            }
        }
    }

    private void launchFedex() {
        Bundle bundle = new Bundle();
        bundle.putString(IAPConstant.ORDER_TRACK_URL, mOrderTrackUrl);

        WebTrackUrl webTrackUrl = new WebTrackUrl();
        webTrackUrl.setArguments(bundle);

        addFragment(WebTrackUrl.createInstance(bundle, AnimationType.NONE), WebTrackUrl.TAG);
    }
}
