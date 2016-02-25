/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.Fragments;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.address.AddressController;
import com.philips.cdp.di.iap.address.AddressSelectionAdapter;
import com.philips.cdp.di.iap.eventhelper.EventHelper;
import com.philips.cdp.di.iap.eventhelper.EventListener;
import com.philips.cdp.di.iap.model.ModelConstants;
import com.philips.cdp.di.iap.response.addresses.Addresses;
import com.philips.cdp.di.iap.response.addresses.GetShippingAddressData;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.session.RequestCode;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.NetworkUtility;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.cdp.di.iap.view.EditDeletePopUP;

import java.util.HashMap;
import java.util.List;

public class AddressSelectionFragment extends BaseAnimationSupportFragment implements AddressController.AddressListener,
        EventListener {
    private RecyclerView mAddressListView;
    private AddressController mAddrController;
    AddressSelectionAdapter mAdapter;
    private List<Addresses> mAddresses;
    private Button mCancelButton;

    @Override
    protected void updateTitle() {
        setTitle(R.string.iap_shipping_address);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.iap_address_selection, container, false);
        mAddressListView = (RecyclerView) view.findViewById(R.id.shipping_addresses);
        mAddrController = new AddressController(getContext(), this);
        mCancelButton = (Button) view.findViewById(R.id.btn_cancel);
        bindCancelListener();
        sendShippingAddressesRequest();

        EventHelper.getInstance().registerEventNotification(EditDeletePopUP.EVENT_EDIT, this);
        EventHelper.getInstance().registerEventNotification(EditDeletePopUP.EVENT_DELETE, this);
        EventHelper.getInstance().registerEventNotification(IAPConstant.ORDER_SUMMARY_FRAGMENT, this);
        EventHelper.getInstance().registerEventNotification(IAPConstant.SHIPPING_ADDRESS_FRAGMENT, this);
        return view;
    }

    public void bindCancelListener() {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //Go back to shopping cart
                moveToShoppingCart();
            }
        });
    }

    private void moveToShoppingCart() {
        getMainActivity().addFragmentAndRemoveUnderneath(ShoppingCartFragment.createInstance(new Bundle(), AnimationType.NONE), false);
    }

    private void sendShippingAddressesRequest() {
        String msg = getContext().getString(R.string.iap_please_wait);
        Utility.showProgressDialog(getContext(), msg);
        mAddrController.getShippingAddresses();
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
        updateTitle();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventHelper.getInstance().unregisterEventNotification(EditDeletePopUP.EVENT_EDIT, this);
        EventHelper.getInstance().unregisterEventNotification(EditDeletePopUP.EVENT_DELETE, this);
        EventHelper.getInstance().unregisterEventNotification(IAPConstant.ORDER_SUMMARY_FRAGMENT, this);
        EventHelper.getInstance().unregisterEventNotification(IAPConstant.SHIPPING_ADDRESS_FRAGMENT, this);
    }

    @Override
    public void onFetchAddressSuccess(final Message msg) {
        if (msg.what == RequestCode.DELETE_ADDRESS) {
            mAddresses.remove(mAdapter.getOptionsClickPosition());
            mAdapter.setAddresses(mAddresses);
            mAdapter.notifyDataSetChanged();
        } else {
            GetShippingAddressData shippingAddresses = (GetShippingAddressData) msg.obj;
            mAddresses = shippingAddresses.getAddresses();
            mAdapter = new AddressSelectionAdapter(getContext(), mAddresses);
            mAddressListView.setAdapter(mAdapter);
        }
        Utility.dismissProgressDialog();
    }

    @Override
    public void onFetchAddressFailure(final Message msg) {
        // TODO: 2/19/2016 Fix error case scenario
        Utility.dismissProgressDialog();
        moveToShoppingCart();
    }

    @Override
    public void onCreateAddress(boolean isSuccess) {

    }

    @Override
    public void onSetDeliveryAddress(final Message msg) {

    }

    @Override
    public void onGetDeliveryAddress(final Message msg) {

    }

    @Override
    public void onSetDeliveryModes(final Message msg) {

    }

    @Override
    public void onGetDeliveryModes(final Message msg) {

    }

    public static AddressSelectionFragment createInstance(final Bundle args, final AnimationType animType) {
        AddressSelectionFragment fragment = new AddressSelectionFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected AnimationType getDefaultAnimationType() {
        return AnimationType.NONE;
    }

    @Override
    public void raiseEvent(final String event) {

    }

    @Override
    public void onEventReceived(final String event) {
        IAPLog.d(IAPLog.SHIPPING_ADDRESS_FRAGMENT, "onEventReceived = " + event);
        if (!TextUtils.isEmpty(event)) {
            if (EditDeletePopUP.EVENT_EDIT.equals(event)) {
                HashMap<String, String> addressHashMap = updateShippingAddress();
                moveToShippingAddressFragment(addressHashMap);
            } else if (EditDeletePopUP.EVENT_DELETE.equals(event)) {
                deleteShippingAddress();
            }
        }
        if (event.equalsIgnoreCase(IAPConstant.ORDER_SUMMARY_FRAGMENT)) {
            getMainActivity().addFragmentAndRemoveUnderneath(
                    OrderSummaryFragment.createInstance(new Bundle(), AnimationType.NONE), false);
        }
        if (event.equalsIgnoreCase(IAPConstant.SHIPPING_ADDRESS_FRAGMENT)) {
            Bundle args = new Bundle();
            args.putBoolean(IAPConstant.IS_SECOND_USER, true);
            getMainActivity().addFragmentAndRemoveUnderneath(ShippingAddressFragment.createInstance(args, AnimationType.NONE), false);
        }
    }

    private void deleteShippingAddress() {
        if (Utility.isInternetConnected(getContext())) {
            if (!Utility.isProgressDialogShowing())
                Utility.showProgressDialog(getContext(), getString(R.string.iap_delete_address));
            int pos = mAdapter.getOptionsClickPosition();
            mAddrController.deleteAddress(mAddresses.get(pos).getId());
        } else {
            NetworkUtility.getInstance().showNetworkError(getContext());
        }
    }

    private HashMap updateShippingAddress() {
        int pos = mAdapter.getOptionsClickPosition();
        Addresses address = mAddresses.get(pos);
        HashMap<String, String> addressHashMap = new HashMap<>();
        addressHashMap.put(ModelConstants.FIRST_NAME, address.getFirstName());
        addressHashMap.put(ModelConstants.LAST_NAME, address.getLastName());
        addressHashMap.put(ModelConstants.TITLE_CODE, address.getTitle());
        addressHashMap.put(ModelConstants.COUNTRY_ISOCODE, address.getCountry().getIsocode());
        addressHashMap.put(ModelConstants.LINE_1, address.getLine1());
        addressHashMap.put(ModelConstants.LINE_2, address.getLine2());
        addressHashMap.put(ModelConstants.POSTAL_CODE, address.getPostalCode());
        addressHashMap.put(ModelConstants.TOWN, address.getTown());
        addressHashMap.put(ModelConstants.ADDRESS_ID, address.getId());
        addressHashMap.put(ModelConstants.DEFAULT_ADDRESS, address.getLine1());
        addressHashMap.put(ModelConstants.PHONE_NUMBER, address.getPhone());
        return addressHashMap;
    }

    private void moveToShippingAddressFragment(final HashMap<String, String> payload) {
        Bundle extras = new Bundle();
        extras.putSerializable(IAPConstant.UPDATE_SHIPPING_ADDRESS_KEY, payload);
        getMainActivity().addFragmentAndRemoveUnderneath(ShippingAddressFragment.createInstance(extras, AnimationType.NONE), false);
    }
}