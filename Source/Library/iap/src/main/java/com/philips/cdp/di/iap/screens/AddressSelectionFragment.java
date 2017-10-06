/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.screens;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.adapters.AddressSelectionAdapter;
import com.philips.cdp.di.iap.address.AddressFields;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.controller.AddressController;
import com.philips.cdp.di.iap.controller.PaymentController;
import com.philips.cdp.di.iap.eventhelper.EventHelper;
import com.philips.cdp.di.iap.eventhelper.EventListener;
import com.philips.cdp.di.iap.response.addresses.Addresses;
import com.philips.cdp.di.iap.response.addresses.DeliveryModes;
import com.philips.cdp.di.iap.response.addresses.GetShippingAddressData;
import com.philips.cdp.di.iap.response.payment.PaymentMethod;
import com.philips.cdp.di.iap.response.payment.PaymentMethods;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.session.RequestCode;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.ModelConstants;
import com.philips.cdp.di.iap.utils.NetworkUtility;
import com.philips.cdp.di.iap.utils.Utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddressSelectionFragment extends InAppBaseFragment implements AddressController.AddressListener,
        EventListener, PaymentController.PaymentListener {

    public static final String TAG = AddressSelectionFragment.class.getName();
    private Context mContext;

    private RecyclerView mAddressListView;
    private AddressController mAddressController;
    AddressSelectionAdapter mAdapter;
    List<Addresses> mAddresses = new ArrayList<>();

    private boolean mIsAddressUpdateAfterDelivery;
    private String mJanRainEmail;

    private DeliveryModes mDeliveryMode;


    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.iap_address_selection, container, false);

        mAddressListView = (RecyclerView) view.findViewById(R.id.shipping_addresses);
        mAddressController = new AddressController(mContext, this);
        mJanRainEmail = HybrisDelegate.getInstance(mContext).getStore().getJanRainEmail();
        registerEvents();

        Bundle bundle = getArguments();
        mDeliveryMode = bundle.getParcelable(IAPConstant.SET_DELIVERY_MODE);

       /* if (bundle.containsKey(IAPConstant.ADDRESS_LIST)) {
            mAddresses = (List<Addresses>) bundle.getSerializable(IAPConstant.ADDRESS_LIST);
        }

        */
        mAdapter = new AddressSelectionAdapter(mAddresses);
        mAddressListView.setAdapter(mAdapter);
        TextView tv_checkOutSteps = (TextView) view.findViewById(R.id.tv_checkOutSteps);
        tv_checkOutSteps.setText(String.format(mContext.getString(R.string.iap_checkout_steps), "1"));

        return view;
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
        mAddressListView.setLayoutManager(layoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        IAPAnalytics.trackPage(IAPAnalyticsConstant.SHIPPING_ADDRESS_SELECTION_PAGE_NAME);
        setTitleAndBackButtonVisibility(R.string.iap_address, true);

        if (isNetworkConnected()) {
            getAddresses();
        }

    }

    private void getAddresses() {
        String msg = mContext.getString(R.string.iap_please_wait);
        if (!isProgressDialogShowing()) {
            showProgressDialog(mContext, msg);
            mAddressController.getAddresses();
        }
    }

    public void registerEvents() {
        EventHelper.getInstance().registerEventNotification(IAPConstant.ADDRESS_SELECTION_EVENT_EDIT, this);
        EventHelper.getInstance().registerEventNotification(IAPConstant.ADDRESS_SELECTION_EVENT_DELETE, this);
        EventHelper.getInstance().registerEventNotification(IAPConstant.ADD_NEW_ADDRESS, this);
        EventHelper.getInstance().registerEventNotification(IAPConstant.DELIVER_TO_THIS_ADDRESS, this);
    }

    @Override
    public boolean handleBackEvent() {
        return false;
    }

    private void moveToShoppingCart() {
        showFragment(ShoppingCartFragment.TAG);
    }

    @Override
    public void onGetAddress(Message msg) {
        if (mIsAddressUpdateAfterDelivery) {
            mIsAddressUpdateAfterDelivery = false;
            return;
        }

        dismissProgressDialog();
        if (msg.obj instanceof IAPNetworkError) {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
            moveToShoppingCart();
        } else {
            if (msg.what == RequestCode.DELETE_ADDRESS) {
                if (mAdapter.getOptionsClickPosition() != -1)
                    mAddresses.remove(mAdapter.getOptionsClickPosition());
                mAdapter.setAddresses(mAddresses);
                mAdapter.notifyDataSetChanged();
                return;
            } else if (isVisible()) {
                if (msg.obj instanceof GetShippingAddressData) {
                    GetShippingAddressData shippingAddresses = (GetShippingAddressData) msg.obj;
                    mAddresses = shippingAddresses.getAddresses();
                }
            }
            mAdapter.setAddresses(mAddresses);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateAddress(Message msg) {
        //Do Nothing
    }

    @Override
    public void onSetDeliveryAddress(final Message msg) {
        if (msg.obj.equals(IAPConstant.IAP_SUCCESS)) {
            Addresses selectedAddress = retrieveSelectedAddress();
            mIsAddressUpdateAfterDelivery = true;
            mAddressController.setDefaultAddress(selectedAddress);
            if (mDeliveryMode == null)
                mAddressController.getDeliveryModes();
            else
                checkPaymentDetails();
        } else {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
            dismissProgressDialog();
        }
    }

    @Override
    public void onGetDeliveryModes(Message msg) {
        handleDeliveryMode(msg, mAddressController);
    }

    @Override
    public void onSetDeliveryMode(final Message msg) {
        if (msg.obj.equals(IAPConstant.IAP_SUCCESS)) {
            checkPaymentDetails();
        } else {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
            dismissProgressDialog();
        }
    }

    @Override
    public void onGetPaymentDetails(Message msg) {
        Addresses address = retrieveSelectedAddress();
        dismissProgressDialog();
        if ((msg.obj).equals(NetworkConstants.EMPTY_RESPONSE)) {

            AddressFields selectedAddress = Utility.prepareAddressFields(address, mJanRainEmail);
            CartModelContainer.getInstance().setShippingAddressFields(selectedAddress);

            Bundle bundle = new Bundle();
            bundle.putBoolean(IAPConstant.ADD_BILLING_ADDRESS, true);
            bundle.putSerializable(IAPConstant.UPDATE_BILLING_ADDRESS_KEY, updateAddress(address));

            addFragment(DLSAddressFragment.createInstance(bundle, AnimationType.NONE),
                    DLSAddressFragment.TAG);
        } else if ((msg.obj instanceof IAPNetworkError)) {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
        } else if ((msg.obj instanceof PaymentMethods)) {
            AddressFields selectedAddress = Utility.prepareAddressFields(retrieveSelectedAddress(), mJanRainEmail);
            CartModelContainer.getInstance().setShippingAddressFields(selectedAddress);
            PaymentMethods mPaymentMethods = (PaymentMethods) msg.obj;
            List<PaymentMethod> mPaymentMethodsList = mPaymentMethods.getPayments();

            Bundle bundle = new Bundle();
            bundle.putSerializable(IAPConstant.UPDATE_BILLING_ADDRESS_KEY, updateAddress(address));
            bundle.putSerializable(IAPConstant.PAYMENT_METHOD_LIST, (Serializable) mPaymentMethodsList);
            addFragment(PaymentSelectionFragment.createInstance(bundle, AnimationType.NONE), PaymentSelectionFragment.TAG);
        }
    }

    @Override
    public void onEventReceived(final String event) {
        if (!TextUtils.isEmpty(event)) {
            if (IAPConstant.ADDRESS_SELECTION_EVENT_EDIT.equals(event)) {
                int pos = mAdapter.getOptionsClickPosition();
                Addresses address = mAddresses.get(pos);
                HashMap<String, String> addressHashMap = updateAddress(address);
                moveToShippingAddressFragment(addressHashMap);
            } else if (IAPConstant.ADDRESS_SELECTION_EVENT_DELETE.equals(event) && isNetworkConnected()) {
                deleteShippingAddress();
            }
        }
        if (event.equalsIgnoreCase(IAPConstant.ADD_NEW_ADDRESS)) {
            Bundle args = new Bundle();
            args.putBoolean(IAPConstant.IS_SECOND_USER, true);
            if (mDeliveryMode != null)
                args.putParcelable(IAPConstant.SET_DELIVERY_MODE, mDeliveryMode);
            addFragment(DLSAddressFragment.createInstance(args, AnimationType.NONE),
                    DLSAddressFragment.TAG);
        } else if (event.equalsIgnoreCase(IAPConstant.DELIVER_TO_THIS_ADDRESS)) {
            if (!isProgressDialogShowing()) {
                showProgressDialog(mContext, getResources().getString(R.string.iap_please_wait));
                mAddressController.setDeliveryAddress(retrieveSelectedAddress().getId());
                CartModelContainer.getInstance().setAddressId(retrieveSelectedAddress().getId());

            }
        }
    }

    public void checkPaymentDetails() {
        PaymentController paymentController = new PaymentController(mContext, this);
        paymentController.getPaymentDetails();
    }

    private void deleteShippingAddress() {
        if (!isProgressDialogShowing()) {
            showProgressDialog(mContext, getString(R.string.iap_please_wait));
            int pos = mAdapter.getOptionsClickPosition();
            mAddressController.deleteAddress(mAddresses.get(pos).getId());
        }
    }

    private HashMap<String, String> updateAddress(Addresses address) {
        HashMap<String, String> addressHashMap = new HashMap<>();

        String titleCode = address.getTitleCode();

        if (titleCode.trim().length() > 0)
            titleCode = titleCode.substring(0, 1).toUpperCase(Locale.getDefault()) + titleCode.substring(1);

        addressHashMap.put(ModelConstants.FIRST_NAME, address.getFirstName());
        addressHashMap.put(ModelConstants.LAST_NAME, address.getLastName());
        addressHashMap.put(ModelConstants.TITLE_CODE, titleCode);
        addressHashMap.put(ModelConstants.COUNTRY_ISOCODE, address.getCountry().getIsocode());
        addressHashMap.put(ModelConstants.LINE_1, address.getLine1());
        addressHashMap.put(ModelConstants.LINE_2, address.getLine2());
        addressHashMap.put(ModelConstants.POSTAL_CODE, address.getPostalCode());
        addressHashMap.put(ModelConstants.TOWN, address.getTown());
        addressHashMap.put(ModelConstants.ADDRESS_ID, address.getId());
        addressHashMap.put(ModelConstants.PHONE_1, address.getPhone1());
        addressHashMap.put(ModelConstants.PHONE_2, address.getPhone1());

        if (address.getRegion() != null) {
            addressHashMap.put(ModelConstants.REGION_ISOCODE, address.getRegion().getIsocodeShort());
            addressHashMap.put(ModelConstants.REGION_CODE, address.getRegion().getIsocode());
        }

        if (address.getEmail() != null)
            addressHashMap.put(ModelConstants.EMAIL_ADDRESS, address.getEmail());
        else
            addressHashMap.put(ModelConstants.EMAIL_ADDRESS, mJanRainEmail);

        return addressHashMap;
    }

    private void moveToShippingAddressFragment(final HashMap<String, String> payload) {
        Bundle extras = new Bundle();
        extras.putSerializable(IAPConstant.UPDATE_SHIPPING_ADDRESS_KEY, payload);
        if (mDeliveryMode != null)
            extras.putParcelable(IAPConstant.SET_DELIVERY_MODE, mDeliveryMode);
        addFragment(DLSAddressFragment.createInstance(extras, AnimationType.NONE),
                DLSAddressFragment.TAG);
    }

    private Addresses retrieveSelectedAddress() {
        int pos = mAdapter.getSelectedPosition();
        return mAddresses.get(pos);
    }

    public static AddressSelectionFragment createInstance(final Bundle args, final AnimationType animType) {
        AddressSelectionFragment fragment = new AddressSelectionFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        unregisterEvents();
        super.onDestroy();
    }

    public void unregisterEvents() {
        EventHelper.getInstance().unregisterEventNotification(IAPConstant.ADDRESS_SELECTION_EVENT_EDIT, this);
        EventHelper.getInstance().unregisterEventNotification(IAPConstant.ADDRESS_SELECTION_EVENT_DELETE, this);
        EventHelper.getInstance().unregisterEventNotification(IAPConstant.ADD_NEW_ADDRESS, this);
        EventHelper.getInstance().unregisterEventNotification(IAPConstant.DELIVER_TO_THIS_ADDRESS, this);
    }

    @Override
    public void onSetPaymentDetails(Message msg) {
        // Do Nothing
    }

    @Override
    public void onGetRegions(Message msg) {
        // Do Nothing
    }

    @Override
    public void onGetUser(Message msg) {
        // Do Nothing
    }
}