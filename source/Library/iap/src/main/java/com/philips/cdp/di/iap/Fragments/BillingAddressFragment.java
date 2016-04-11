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
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.address.AddressFields;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.cdp.uikit.customviews.PuiSwitch;

public class BillingAddressFragment extends ShippingAddressFragment {

    private Context mContext;
    private PuiSwitch mSwitchBillingAddress;

    private AddressFields mBillingAddressFields;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        assert rootView != null;
        LinearLayout mSameAsBillingAddress = (LinearLayout) rootView.findViewById(R.id.same_as_shipping_ll);
        mSwitchBillingAddress = (PuiSwitch) rootView.findViewById(R.id.switch_billing_address);

        mTvTitle.setText(getResources().getString(R.string.iap_billing_address));
        mSameAsBillingAddress.setVisibility(View.VISIBLE);

        if (CartModelContainer.getInstance().getShippingAddressFields() != null) {
            mBillingAddressFields = CartModelContainer.getInstance().getShippingAddressFields();
            disableAllFields();
            prePopulateShippingAddress();
            mBtnContinue.setEnabled(true);
        }

        mSwitchBillingAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    disableAllFields();
                    prePopulateShippingAddress();
                    mBtnContinue.setEnabled(true);
                } else {
                    clearAllFields();
                    mBtnContinue.setEnabled(false);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(R.string.iap_address);
        if (mSwitchBillingAddress.isChecked()) {
            disableAllFields();
            mBtnContinue.setEnabled(true);
        }
    }

    private void prePopulateShippingAddress() {
        mIgnoreTextChangeListener = true;
        if (mBillingAddressFields != null) {
            mEtFirstName.setText(mBillingAddressFields.getFirstName());
            mEtLastName.setText(mBillingAddressFields.getLastName());
            mEtSalutation.setText(mBillingAddressFields.getTitleCode());
            mEtAddressLineOne.setText(mBillingAddressFields.getLine1());
            mEtAddressLineTwo.setText(mBillingAddressFields.getLine2());
            mEtTown.setText(mBillingAddressFields.getTown());
            mEtPostalCode.setText(mBillingAddressFields.getPostalCode());
            mEtCountry.setText(mBillingAddressFields.getCountryIsocode());
            mEtEmail.setText(mBillingAddressFields.getEmail());
            mEtPhoneNumber.setText(mBillingAddressFields.getPhoneNumber());

            if (mBillingAddressFields.getRegionIsoCode() != null) {
                mEtState.setText(mBillingAddressFields.getRegionIsoCode());
                mlLState.setVisibility(View.VISIBLE);
            } else {
                mlLState.setVisibility(View.GONE);
            }
        }
        mIgnoreTextChangeListener = false;
    }

    private void clearAllFields() {
        mIgnoreTextChangeListener = true;
        mEtFirstName.setText("");
        mEtLastName.setText("");
        mEtSalutation.setText("");
        mEtAddressLineOne.setText("");
        mEtAddressLineTwo.setText("");
        mEtTown.setText("");
        mEtPostalCode.setText("");
        mEtCountry.setText("");
        mEtEmail.setText("");
        mEtPhoneNumber.setText("");
        mlLState.setVisibility(View.VISIBLE);
        mEtState.setText("");
        mIgnoreTextChangeListener = false;
        enableAllFields();
        enableFocus();
        removeErrorInAllFields();
    }

    private void enableAllFields() {
        setFieldsEnabled(true);
    }

    private void disableAllFields() {
        removeErrorInAllFields();
        disableFocus();
        setFieldsEnabled(false);
    }

    private void enableFocus() {
        setFieldsFocusable(true);
    }

    private void disableFocus() {
        setFieldsFocusable(false);
    }

    private void removeErrorInAllFields() {
        mInlineFormsParent.post(new Runnable() {
            @Override
            public void run() {
                mInlineFormsParent.removeError(mEtFirstName);
                mInlineFormsParent.removeError(mEtLastName);
                mInlineFormsParent.removeError(mEtAddressLineOne);
                mInlineFormsParent.removeError(mEtAddressLineTwo);
                mInlineFormsParent.removeError(mEtTown);
                mInlineFormsParent.removeError(mEtPostalCode);
                mInlineFormsParent.removeError(mEtCountry);
                mInlineFormsParent.removeError(mEtEmail);
                mInlineFormsParent.removeError(mEtPhoneNumber);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onClick(View v) {

        Utility.hideKeypad(mContext);

        if (v == mBtnContinue) {
            mBillingAddressFields = setAddressFields(mBillingAddressFields.clone());
            CartModelContainer.getInstance().setBillingAddress(mBillingAddressFields);
            if (!Utility.isProgressDialogShowing()) {
                IAPAnalytics.trackPage(IAPAnalyticsConstant.ORDER_SUMMARY_PAGE_NAME);
                Bundle bundle = new Bundle();
                bundle.putSerializable(IAPConstant.BILLING_ADDRESS_FIELDS, mBillingAddressFields);
                addFragment(
                        OrderSummaryFragment.createInstance(bundle, AnimationType.NONE), null);
            }
        } else if (v == mBtnCancel) {
            if (getArguments().containsKey(IAPConstant.FROM_PAYMENT_SELECTION) &&
                    getArguments().getBoolean(IAPConstant.FROM_PAYMENT_SELECTION)) {
                IAPAnalytics.trackPage(IAPAnalyticsConstant.PAYMENT_SELECTION_PAGE_NAME);
                getFragmentManager().popBackStackImmediate();
            } else {
                IAPAnalytics.trackPage(IAPAnalyticsConstant.SHOPPING_CART_PAGE_NAME);
                addFragment
                        (ShoppingCartFragment.createInstance(new Bundle(), AnimationType.NONE), null);
            }
        }
    }

    public static BillingAddressFragment createInstance(Bundle args, AnimationType animType) {
        BillingAddressFragment fragment = new BillingAddressFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    private void setFieldsEnabled(boolean enable) {
        mEtFirstName.setEnabled(enable);
        mEtLastName.setEnabled(enable);
        mEtSalutation.setEnabled(enable);
        mEtAddressLineOne.setEnabled(enable);
        mEtAddressLineTwo.setEnabled(enable);
        mEtTown.setEnabled(enable);
        mEtPostalCode.setEnabled(enable);
        mEtCountry.setEnabled(enable);
        if (mlLState.getVisibility() == View.VISIBLE) {
            mEtState.setEnabled(enable);
        }
        mEtEmail.setEnabled(enable);
        mEtPhoneNumber.setEnabled(enable);
    }

    private void setFieldsFocusable(boolean focusable) {
        mEtFirstName.setFocusable(focusable);
        mEtLastName.setFocusable(focusable);
        mEtSalutation.setFocusable(focusable);
        mEtAddressLineOne.setFocusable(focusable);
        mEtAddressLineTwo.setFocusable(focusable);
        mEtTown.setFocusable(focusable);
        mEtPostalCode.setFocusable(focusable);
        mEtCountry.setFocusable(focusable);
        if (mlLState.getVisibility() == View.VISIBLE) {
            mEtState.setFocusable(focusable);
        }
        mEtEmail.setFocusable(focusable);
        mEtPhoneNumber.setFocusable(focusable);

        if (focusable) {
            mEtFirstName.setFocusableInTouchMode(true);
            mEtLastName.setFocusableInTouchMode(true);
            mEtSalutation.setFocusableInTouchMode(true);
            mEtAddressLineOne.setFocusableInTouchMode(true);
            mEtAddressLineTwo.setFocusableInTouchMode(true);
            mEtTown.setFocusableInTouchMode(true);
            mEtPostalCode.setFocusableInTouchMode(true);
            mEtCountry.setFocusableInTouchMode(true);
            if (mlLState.getVisibility() == View.VISIBLE) {
                mEtState.setFocusableInTouchMode(true);
            }
            mEtEmail.setFocusableInTouchMode(true);
            mEtPhoneNumber.setFocusableInTouchMode(true);
        }
    }
}
