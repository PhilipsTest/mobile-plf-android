/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.screens;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.ViewGroup;


import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.address.AddressFields;
import com.ecs.demouapp.ui.container.CartModelContainer;
import com.ecs.demouapp.ui.controller.BuyDirectController;

import com.ecs.demouapp.ui.session.IAPNetworkError;
import com.ecs.demouapp.ui.session.NetworkConstants;
import com.ecs.demouapp.ui.utils.ECSConstant;
import com.ecs.demouapp.ui.utils.ECSLog;
import com.ecs.demouapp.ui.utils.NetworkUtility;
import com.philips.platform.ecs.model.address.ECSAddress;

import com.philips.platform.ecs.model.address.ECSDeliveryMode;
import com.philips.platform.ecs.model.address.ECSUserProfile;
import com.philips.platform.ecs.model.address.GetDeliveryModes;
import com.philips.platform.ecs.model.payment.ECSPayment;
import com.philips.platform.ecs.model.payment.PaymentMethods;
import com.philips.platform.ecs.model.region.RegionsList;

import java.util.ArrayList;
import java.util.Locale;

public class BuyDirectFragment extends InAppBaseFragment implements
        BuyDirectController.BuyDirectListener, ErrorDialogFragment.ErrorDialogListener {
    public static final String TAG = BuyDirectFragment.class.getName();
    BuyDirectController mBuyDirectController;
    Context mContext;
    private String mCTN;
    private ECSPayment mPaymentMethod;
    private ErrorDialogFragment mErrorDialogFragment;
    private ViewGroup mView;

    public static BuyDirectFragment createInstance(Bundle args, AnimationType animType) {
        BuyDirectFragment fragment = new BuyDirectFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        Bundle bundle = getArguments();
        mCTN = bundle.getString(ECSConstant.IAP_PRODUCT_CATALOG_NUMBER_FROM_VERTICAL);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBuyDirectController = new BuyDirectController(mContext, this);
        mView = (ViewGroup) getView();
        createCustomProgressBar(mView, BIG);
        //TODO
       // mBuyDirectController.createCart();

    }

    @Override
    public void onCreateCart(Message msg) {
        if ((msg.obj instanceof IAPNetworkError)) {
            handleError(msg);
        } else {
            //TODO
            //mBuyDirectController.addToCart(mCTN);
        }
    }

    @Override
    public void onAddToCart(Message msg) {
        if ((msg.obj instanceof IAPNetworkError)) {
            handleError(msg);
        } else {
            //TODO
            //mBuyDirectController.fetchRegions();
        }
    }

    @Override
    public void onGetRegions(Message msg) {
        if (msg.obj instanceof IAPNetworkError) {
            handleError(msg);
        } else if (msg.obj instanceof RegionsList) {
            //TODO
           // CartModelContainer.getInstance().setRegionList((RegionsList) msg.obj);
           // mBuyDirectController.getUser();
        } else if ((msg.obj).equals(NetworkConstants.EMPTY_RESPONSE)) {
            //TODO
            //mBuyDirectController.getUser();
        }
    }

    @Override
    public void onGetUser(Message msg) {

        if (msg.obj instanceof IAPNetworkError) {
            handleError(msg);
        } else if (msg.obj instanceof ECSUserProfile) {
            ECSUserProfile user = (ECSUserProfile) msg.obj;
            ECSAddress defaultAddress = user.getDefaultAddress();
            if (defaultAddress != null) {
                setAddressField(defaultAddress);
            } else {
                hideProgressBar();
                addFragment(
                        AddressFragment.createInstance(new Bundle(), AnimationType.NONE), AddressFragment.TAG,true);
            }
        }
    }

    @Override
    public void onSetDeliveryAddress(Message msg) {
        if ((msg.obj instanceof IAPNetworkError)) {
            handleError(msg);
        } else {
           // mBuyDirectController.fetchDeliveryModes();
        }
    }

    @Override
    public void onGetDeliveryMode(Message msg) {
        if ((msg.obj instanceof IAPNetworkError)) {
            handleError(msg);
        } else {
            GetDeliveryModes deliveryModes = (GetDeliveryModes) msg.obj;
            ArrayList<ECSDeliveryMode> deliveryModesList = (ArrayList<ECSDeliveryMode>) deliveryModes.getDeliveryModes();
            CartModelContainer.getInstance().setDeliveryModes(deliveryModesList);
            //TODO
            //mBuyDirectController.setDeliveryMode(deliveryModesList.get(0).getCode());
        }
    }

    @Override
    public void onSetDeliveryMode(Message msg) {
        if ((msg.obj instanceof IAPNetworkError)) {
            handleError(msg);
        } else {
            //TODO
            //mBuyDirectController.getPaymentMode();
        }
    }

    @Override
    public void onGetPaymentMode(Message msg) {

        if ((msg.obj).equals(NetworkConstants.EMPTY_RESPONSE)) {
            hideProgressBar();
//            addFragment(
//                    BillingAddressFragment.createInstance(new Bundle(),
//                            AnimationType.NONE), BillingAddressFragment.TAG);
        } else if ((msg.obj instanceof IAPNetworkError)) {
            handleError(msg);
        } else if ((msg.obj instanceof PaymentMethods)) {
            PaymentMethods paymentMethods = (PaymentMethods) msg.obj;
            mPaymentMethod = paymentMethods.getPayments().get(0);
            if (mPaymentMethod != null) {
                //TODO
                //mBuyDirectController.setPaymentMode(mPaymentMethod.getId());
            }
        }
    }

    @Override
    public void onSetPaymentMode(Message msg) {
        hideProgressBar();
        if ((msg.obj instanceof IAPNetworkError)) {
            handleError(msg);
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ECSConstant.SELECTED_PAYMENT, mPaymentMethod);
            addFragment(OrderSummaryFragment.createInstance(bundle, AnimationType.NONE), OrderSummaryFragment.TAG,true);
        }
    }

    @Override
    public void onDeleteCart(Message msg) {

    }

    private void setAddressField(ECSAddress address) {
        String mAddressId = address.getId();
        AddressFields addressFields = new AddressFields();
        addressFields.setFirstName(address.getFirstName());
        addressFields.setLastName(address.getLastName());
        addressFields.setLine1(address.getLine1());
        addressFields.setPhone1(address.getPhone1());
        addressFields.setPhone2(address.getPhone2());
        addressFields.setPostalCode(address.getPostalCode());
        addressFields.setEmail(address.getEmail());
        String titleCode = address.getTitleCode();
        addressFields.setTitleCode(titleCode.substring(0, 1).toUpperCase(Locale.getDefault())
                + titleCode.substring(1));
        addressFields.setTown(address.getTown());
        if (address.getRegion() != null) {
            addressFields.setRegionName(address.getRegion().getName());
            CartModelContainer.getInstance().setRegionIsoCode(address.getRegion().getIsocode());
        }
        addressFields.setCountryIsocode(address.getCountry().getIsocode());
        CartModelContainer.getInstance().setShippingAddressFields(addressFields);
        CartModelContainer.getInstance().setAddressId(mAddressId);
        //TODO
        //mBuyDirectController.setDeliveryAddress(mAddressId);
    }

    private void handleError(Message msg) {
        hideProgressBar();
        showErrorDialog(msg);
    }

    private void showErrorDialog(Message msg) {
        IAPNetworkError error = (IAPNetworkError) msg.obj;
        Bundle bundle = new Bundle();
        bundle.putString(ECSConstant.SINGLE_BUTTON_DIALOG_TEXT, mContext.getString(R.string.iap_ok));
        bundle.putString(ECSConstant.SINGLE_BUTTON_DIALOG_TITLE,
                NetworkUtility.getInstance().getErrorTitleMessageFromErrorCode(mContext, error.getIAPErrorCode()));
        bundle.putString(ECSConstant.SINGLE_BUTTON_DIALOG_DESCRIPTION,
                NetworkUtility.getInstance().getErrorDescriptionMessageFromErrorCode(mContext, error));
        if (mErrorDialogFragment == null) {
            mErrorDialogFragment = new ErrorDialogFragment();
            mErrorDialogFragment.setErrorDialogListener(this);
            mErrorDialogFragment.setArguments(bundle);
            mErrorDialogFragment.setShowsDialog(false);
        }
        try {
            mErrorDialogFragment.show(getFragmentManager(), "NetworkErrorDialog");
            mErrorDialogFragment.setShowsDialog(true);
        } catch (Exception e) {
            ECSLog.e(ECSLog.LOG, e.getMessage());
        }
    }

    @Override
    public void onDialogOkClick() {
        moveToVerticalAppByClearingStack();
    }
}
