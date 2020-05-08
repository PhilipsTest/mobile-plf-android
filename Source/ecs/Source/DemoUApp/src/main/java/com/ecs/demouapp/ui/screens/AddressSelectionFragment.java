/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.screens;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.adapters.AddressSelectionAdapter;
import com.ecs.demouapp.ui.address.AddressFields;
import com.ecs.demouapp.ui.analytics.ECSAnalytics;
import com.ecs.demouapp.ui.analytics.ECSAnalyticsConstant;
import com.ecs.demouapp.ui.container.CartModelContainer;
import com.ecs.demouapp.ui.controller.AddressController;
import com.ecs.demouapp.ui.controller.PaymentController;
import com.ecs.demouapp.ui.eventhelper.EventHelper;
import com.ecs.demouapp.ui.eventhelper.EventListener;
import com.ecs.demouapp.ui.session.IAPNetworkError;
import com.ecs.demouapp.ui.session.NetworkConstants;
import com.ecs.demouapp.ui.session.RequestCode;
import com.ecs.demouapp.ui.utils.AlertListener;
import com.ecs.demouapp.ui.utils.ECSConstant;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.ecs.demouapp.ui.utils.ModelConstants;
import com.ecs.demouapp.ui.utils.NetworkUtility;
import com.ecs.demouapp.ui.utils.Utility;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.model.address.ECSDeliveryMode;
import com.philips.platform.ecs.model.payment.ECSPayment;
import com.philips.platform.pif.DataInterface.USR.UserDataInterfaceException;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddressSelectionFragment extends InAppBaseFragment implements AddressController.AddressListener,
        EventListener, PaymentController.PaymentListener, AlertListener {

    public static final String TAG = AddressSelectionFragment.class.getName();
    private Context mContext;

    private RecyclerView mAddressListView;
    private AddressController mAddressController;
    private RelativeLayout mLinearLayout;
    AddressSelectionAdapter mAdapter;
    List<ECSAddress> mAddresses = new ArrayList<>();

    private boolean mIsAddressUpdateAfterDelivery;
    private String mJanRainEmail;

    private ECSDeliveryMode mDeliveryMode;


    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ecs_address_selection, container, false);

        mAddressListView = view.findViewById(R.id.shipping_addresses);
        mAddressController = new AddressController(mContext, this);

        ArrayList<String> emails = new ArrayList<>();
        emails.add(UserDetailConstants.EMAIL);

        try {
            HashMap<String, Object> userDetails = ECSUtility.getInstance().getUserDataInterface().getUserDetails(emails);
            mJanRainEmail =(String) userDetails.get(UserDetailConstants.EMAIL);
        } catch (UserDataInterfaceException e) {
        e.printStackTrace();
    }
        registerEvents();

        Bundle bundle = getArguments();
        mDeliveryMode = bundle.getParcelable(ECSConstant.SET_DELIVERY_MODE);

        mAdapter = new AddressSelectionAdapter(mAddresses,mJanRainEmail);
        mAddressListView.setAdapter(mAdapter);
        TextView tv_checkOutSteps = view.findViewById(R.id.tv_checkOutSteps);
        mLinearLayout = view.findViewById(R.id.parent_container);
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
        ECSAnalytics.trackPage(ECSAnalyticsConstant.SHIPPING_ADDRESS_SELECTION_PAGE_NAME);
        setTitleAndBackButtonVisibility(R.string.iap_checkout, true);
        setCartIconVisibility(false);

        if (isNetworkConnected()) {
            getAddresses();
        }

    }

    public void getAddresses() {
        String msg = mContext.getString(R.string.iap_please_wait);
        createCustomProgressBar(mLinearLayout, BIG);
        mAddressController.getAddresses();
    }

    public void registerEvents() {
        EventHelper.getInstance().registerEventNotification(ECSConstant.ADDRESS_SELECTION_EVENT_EDIT, this);
        EventHelper.getInstance().registerEventNotification(ECSConstant.ADDRESS_SELECTION_EVENT_DELETE, this);
        EventHelper.getInstance().registerEventNotification(ECSConstant.ADD_NEW_ADDRESS, this);
        EventHelper.getInstance().registerEventNotification(ECSConstant.DELIVER_TO_THIS_ADDRESS, this);
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
        hideProgressBar();
        if (mIsAddressUpdateAfterDelivery) {
            mIsAddressUpdateAfterDelivery = false;
            return;
        }

        if(msg.obj instanceof Boolean){

            if((Boolean) msg.obj){
                ECSUtility.showECSAlertDialog(mContext,"Error","Address set successfully");
            }else{
                ECSUtility.showECSAlertDialog(mContext,"Error","error setting Default Address");
            }

        }else if (msg.obj instanceof IAPNetworkError) {
            ECSUtility.showECSAlertDialog(mContext,"Error",((IAPNetworkError) msg.obj).getMessage());
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
            moveToShoppingCart();
        } else if (msg.obj instanceof Exception){
            ECSUtility.showECSAlertDialog(mContext,"Error",((Exception) msg.obj));
            moveToShoppingCart();
         }else {
            if (msg.what == RequestCode.DELETE_ADDRESS) {
                if (mAdapter.getOptionsClickPosition() != -1)
                    mAddresses.remove(mAdapter.getOptionsClickPosition());
                mAdapter.setAddresses(mAddresses);
                mAdapter.notifyDataSetChanged();
                return;
            } else if (isVisible()) {
                if (msg.obj instanceof List) {
                    List list = (List) msg.obj;

                    if(list.get(0) instanceof ECSAddress){
                        mAddresses = (List<ECSAddress>)  list;
                    }
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
        if ((msg.obj instanceof Boolean) ) {
            if((Boolean)msg.obj) { // set Delivery Address is success

                if (mDeliveryMode != null) {
                    mIsAddressUpdateAfterDelivery = true;
                    checkPaymentDetails();
                }else{
                    mAddressController.getDeliveryModes();
                }
            }
        } else {
            //TODO remove checkPaymentDetails from here
            //Temp work to test Payment call .
            hideProgressBar();
            ECSUtility.showECSAlertDialog(mContext,"Error", String.valueOf(msg.obj));
        }
    }

    @Override
    public void onGetDeliveryModes(Message msg) {
        if ((msg.obj instanceof List )) {
            List<ECSDeliveryMode> deliveryModeList = (List<ECSDeliveryMode>) msg.obj;
            mAddressController.setDeliveryMode(deliveryModeList.get(0));
        }
        handleDeliveryMode(msg, mAddressController);
    }

    @Override
    public void onSetDeliveryMode(final Message msg) {

        if(msg.obj instanceof Boolean){
            if((boolean)msg.obj){
                mIsAddressUpdateAfterDelivery = true;
                checkPaymentDetails();
            }
        }else{
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
            hideProgressBar();
        }
    }

    @Override
    public void onGetPaymentDetails(Message msg) {
        ECSAddress address = retrieveSelectedAddress();
        hideProgressBar();
        if (msg.obj instanceof String) {

            if(msg.obj.equals(NetworkConstants.EMPTY_RESPONSE)) {
                AddressFields selectedAddress = Utility.prepareAddressFields(address, mJanRainEmail);
                CartModelContainer.getInstance().setShippingAddressFields(selectedAddress);

                Bundle bundle = new Bundle();
                bundle.putBoolean(ECSConstant.ADD_BILLING_ADDRESS, true);
                bundle.putSerializable(ECSConstant.UPDATE_BILLING_ADDRESS_KEY, updateAddress(address));

                addFragment(AddressFragment.createInstance(bundle, AnimationType.NONE),
                        AddressFragment.TAG, true);
            }
        } else if ((msg.obj instanceof Exception)) {
            ECSUtility.showECSAlertDialog(mContext,"Error",((Exception) msg.obj));
        } else if ((msg.obj instanceof List)) {
            AddressFields selectedAddress = Utility.prepareAddressFields(retrieveSelectedAddress(), mJanRainEmail);
            CartModelContainer.getInstance().setShippingAddressFields(selectedAddress);
            List<ECSPayment> mPaymentMethodsList = (List<ECSPayment>) msg.obj;

            Bundle bundle = new Bundle();
            bundle.putSerializable(ECSConstant.UPDATE_BILLING_ADDRESS_KEY, updateAddress(address));
            bundle.putSerializable(ECSConstant.PAYMENT_METHOD_LIST, (Serializable) mPaymentMethodsList);
            addFragment(PaymentSelectionFragment.createInstance(bundle, AnimationType.NONE), PaymentSelectionFragment.TAG,true);
        }
    }

    @Override
    public void onEventReceived(final String event) {
        if (!TextUtils.isEmpty(event)) {
            if (ECSConstant.ADDRESS_SELECTION_EVENT_EDIT.equals(event)) {
                int pos = mAdapter.getOptionsClickPosition();
                ECSAddress address = mAddresses.get(pos);
                HashMap<String, String> addressHashMap = updateAddress(address);
                moveToShippingAddressFragment(addressHashMap);
            } else if (ECSConstant.ADDRESS_SELECTION_EVENT_DELETE.equals(event) && isNetworkConnected()) {
                Utility.showActionDialog(mContext, getString(R.string.iap_ok), getString(R.string.iap_cancel)
                        , getString(R.string.iap_confirm), getString(R.string.iap_product_remove_address), getFragmentManager(), this);

            }
        }
        if (event.equalsIgnoreCase(ECSConstant.ADD_NEW_ADDRESS)) {
            Bundle args = new Bundle();
            args.putBoolean(ECSConstant.IS_SECOND_USER, true);
            if (mDeliveryMode != null)
                args.putParcelable(ECSConstant.SET_DELIVERY_MODE, mDeliveryMode);
            addFragment(AddressFragment.createInstance(args, AnimationType.NONE),
                    AddressFragment.TAG,true);
        } else if (event.equalsIgnoreCase(ECSConstant.DELIVER_TO_THIS_ADDRESS)) {

            createCustomProgressBar(mLinearLayout, BIG);
            mAddressController.setDeliveryAddress(retrieveSelectedAddress());
            CartModelContainer.getInstance().setAddressId(retrieveSelectedAddress().getId());
            CartModelContainer.getInstance().setAddressIdFromDelivery(retrieveSelectedAddress().getId());
        }
    }

    public void checkPaymentDetails() {
        PaymentController paymentController = new PaymentController(mContext, this);
        paymentController.getPaymentDetails();
    }

    private void deleteShippingAddress() {
        createCustomProgressBar(mLinearLayout, BIG);
        int pos = mAdapter.getOptionsClickPosition();
        mAddressController.deleteAddress(mAddresses.get(pos));
    }

    private HashMap<String, String> updateAddress(ECSAddress address) {
        HashMap<String, String> addressHashMap = new HashMap<>();

        String titleCode = address.getTitleCode();

        if (titleCode.trim().length() > 0)
            titleCode = titleCode.substring(0, 1).toUpperCase(Locale.getDefault()) + titleCode.substring(1);

        addressHashMap.put(ModelConstants.FIRST_NAME, address.getFirstName());
        addressHashMap.put(ModelConstants.LAST_NAME, address.getLastName());
        addressHashMap.put(ModelConstants.TITLE_CODE, titleCode);
        addressHashMap.put(ModelConstants.COUNTRY_ISOCODE, address.getCountry().getIsocode());
        addressHashMap.put(ModelConstants.LINE_1, address.getLine1());
        addressHashMap.put(ModelConstants.HOUSE_NO, address.getHouseNumber());
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
        extras.putSerializable(ECSConstant.UPDATE_SHIPPING_ADDRESS_KEY, payload);
        if (mDeliveryMode != null)
            extras.putParcelable(ECSConstant.SET_DELIVERY_MODE, mDeliveryMode);
        addFragment(AddressFragment.createInstance(extras, AnimationType.NONE),
                AddressFragment.TAG,true);
    }

    private ECSAddress retrieveSelectedAddress() {
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
        EventHelper.getInstance().unregisterEventNotification(ECSConstant.ADDRESS_SELECTION_EVENT_EDIT, this);
        EventHelper.getInstance().unregisterEventNotification(ECSConstant.ADDRESS_SELECTION_EVENT_DELETE, this);
        EventHelper.getInstance().unregisterEventNotification(ECSConstant.ADD_NEW_ADDRESS, this);
        EventHelper.getInstance().unregisterEventNotification(ECSConstant.DELIVER_TO_THIS_ADDRESS, this);
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

    @Override
    public void onPositiveBtnClick() {
        deleteShippingAddress();
    }

    @Override
    public void onNegativeBtnClick() {
        // Do Nothing
    }
}