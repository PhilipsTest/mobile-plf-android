/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.screens;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.session.NetworkConstants;
import com.ecs.demouapp.ui.utils.ECSConstant;
import com.philips.platform.ecs.util.ECSConfiguration;


public class CancelOrderFragment extends InAppBaseFragment {

    public static final String TAG = CancelOrderFragment.class.getName();
    private String phoneNumber;
    private Context mContext;

    public static CancelOrderFragment createInstance
            (Bundle args, InAppBaseFragment.AnimationType animType) {
        CancelOrderFragment fragment = new CancelOrderFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ecs_cancel_order, container, false);

        Button phoneNumberText = rootView.findViewById(R.id.bt_phone_number);
        TextView cancelOrderId = rootView.findViewById(R.id.tv_cancel_order_history_title);
        TextView refOrderText = rootView.findViewById(R.id.iap_cancel_order_ref);
        TextView openingTimingText = rootView.findViewById(R.id.tv_opening_timings);

        Bundle bundle = getArguments();
        if (null != bundle) {
            phoneNumber = bundle.getString(ECSConstant.CUSTOMER_CARE_NUMBER);

            //TODO
            phoneNumberText.setText(mContext.getString(R.string.iap_call) + " " + PhoneNumberUtils.formatNumber(phoneNumber,
                    ECSConfiguration.INSTANCE.getCountry()));
            String weekdaysTiming = bundle.getString(ECSConstant.CUSTOMER_CARE_WEEKDAYS_TIMING);
            String saturdayTiming = bundle.getString(ECSConstant.CUSTOMER_CARE_SATURDAY_TIMING);
            openingTimingText.setText(weekdaysTiming + "\n" + saturdayTiming);
            cancelOrderId.setText(String.format(mContext.getString(R.string.iap_cancel_order_number), bundle.getString(ECSConstant.IAP_ORDER_ID)));
            refOrderText.setText(String.format(mContext.getString(R.string.iap_cancel_order_dls_for_your_ref_sg), bundle.getString(ECSConstant.IAP_ORDER_ID)));
        }
        phoneNumberText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                String p = "tel:" + PhoneNumberUtils.formatNumber(phoneNumber,
                        ECSConfiguration.INSTANCE.getCountry());
                i.setData(Uri.parse(p));
                startActivity(i);
            }
        });

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
        setTitleAndBackButtonVisibility(R.string.iap_cancel_your_order, true);
        setCartIconVisibility(false);
    }

    @Override
    public boolean handleBackEvent() {
        return false;
    }
}
