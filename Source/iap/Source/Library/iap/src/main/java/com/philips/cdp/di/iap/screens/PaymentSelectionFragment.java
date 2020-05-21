/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.screens;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.adapters.PaymentMethodsAdapter;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.controller.PaymentController;
import com.philips.cdp.di.iap.eventhelper.EventHelper;
import com.philips.cdp.di.iap.eventhelper.EventListener;
import com.philips.cdp.di.iap.response.payment.PaymentMethod;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.NetworkUtility;

import java.util.HashMap;
import java.util.List;

public class PaymentSelectionFragment extends InAppBaseFragment
        implements EventListener, PaymentController.PaymentListener {
    public static final String TAG = PaymentSelectionFragment.class.getName();
    private static final long serialVersionUID = 2998872356511710059L;
    private Context mContext;
    private RecyclerView mPaymentMethodsRecyclerView;
    private PaymentMethodsAdapter mPaymentMethodsAdapter;
    private List<PaymentMethod> mPaymentMethodList;
    private PaymentController mPaymentController;
    private LinearLayout mParentLayout;
    TextView tvCheckOutSteps;
    TextView tvSelectHeader;

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.iap_payment_method, container, false);
        mPaymentMethodsRecyclerView = view.findViewById(R.id.recycler_payment_method);

        tvCheckOutSteps= view.findViewById(R.id.tv_checkOutSteps);
        tvCheckOutSteps.setText(String.format(mContext.getString(R.string.iap_checkout_steps),"2"));

        tvSelectHeader= view.findViewById(R.id.tv_select_header);
        tvSelectHeader.setText(getContext().getString(R.string.iap_checkout_payment_method));

        mParentLayout = view.findViewById(R.id.payment_container);
        Bundle bundle = getArguments();
        if (bundle.containsKey(IAPConstant.PAYMENT_METHOD_LIST)) {
            mPaymentMethodList = (List<PaymentMethod>) bundle.getSerializable(IAPConstant.PAYMENT_METHOD_LIST);
        }

        mPaymentMethodsAdapter = new PaymentMethodsAdapter(mContext, mPaymentMethodList);
        mPaymentMethodsRecyclerView.setAdapter(mPaymentMethodsAdapter);

        mPaymentController = new PaymentController(mContext, this);

        EventHelper.getInstance().registerEventNotification(IAPConstant.USE_PAYMENT, this);
        EventHelper.getInstance().registerEventNotification(IAPConstant.ADD_NEW_PAYMENT, this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mPaymentMethodsRecyclerView.setLayoutManager(layoutManager);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        IAPAnalytics.trackPage(IAPAnalyticsConstant.PAYMENT_SELECTION_PAGE_NAME);
        setTitleAndBackButtonVisibility(R.string.iap_payment, true);
        setCartIconVisibility(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventHelper.getInstance().unregisterEventNotification(IAPConstant.USE_PAYMENT, this);
        EventHelper.getInstance().unregisterEventNotification(IAPConstant.ADD_NEW_PAYMENT, this);
    }

    public static PaymentSelectionFragment createInstance(final Bundle args, final AnimationType animType) {
        PaymentSelectionFragment fragment = new PaymentSelectionFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    private PaymentMethod selectedPaymentMethod() {
        int selectedPosition = mPaymentMethodsAdapter.getSelectedPosition();
        return mPaymentMethodList.get(selectedPosition);
    }

    @Override
    public void onEventReceived(String event) {
        if (event.equalsIgnoreCase(IAPConstant.USE_PAYMENT)) {
            setPaymentDetail();
        } else if (event.equalsIgnoreCase(IAPConstant.ADD_NEW_PAYMENT)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(IAPConstant.FROM_PAYMENT_SELECTION, true);
            final HashMap<String,String> value = (HashMap<String, String>)getArguments().getSerializable(IAPConstant.UPDATE_BILLING_ADDRESS_KEY);
            bundle.putSerializable(IAPConstant.UPDATE_BILLING_ADDRESS_KEY, value);
            //Load shipping address from SetDelivery Address with check boxed
            addFragment(AddressFragment.createInstance(bundle, AnimationType.NONE),
                    AddressFragment.TAG,true);
//            addFragment(BillingAddressFragment.createInstance(bundle, AnimationType.NONE),
//                    BillingAddressFragment.TAG);
        }
    }

    private void setPaymentDetail() {
        createCustomProgressBar(mParentLayout, BIG);
        IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA, IAPAnalyticsConstant.PAYMENT_METHOD,
                selectedPaymentMethod().getCardType().getCode());
        mPaymentController.setPaymentDetails(selectedPaymentMethod().getId());

    }

    @Override
    public void onGetPaymentDetails(Message msg) {

    }

    @Override
    public void onSetPaymentDetails(Message msg) {
        hideProgressBar();
        if (msg.obj instanceof IAPNetworkError) {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(IAPConstant.SELECTED_PAYMENT, selectedPaymentMethod());
            addFragment(OrderSummaryFragment.createInstance(bundle, AnimationType.NONE), OrderSummaryFragment.TAG,true);
        }
    }
}
