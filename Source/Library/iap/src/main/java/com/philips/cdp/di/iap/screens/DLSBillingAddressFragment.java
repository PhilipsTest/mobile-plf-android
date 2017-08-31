package com.philips.cdp.di.iap.screens;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.address.Validator;
import com.philips.cdp.di.iap.controller.AddressController;
import com.philips.cdp.di.iap.controller.PaymentController;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.InputValidator;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.cdp.di.iap.view.SalutationDropDown;
import com.philips.cdp.di.iap.view.StateDropDown;
import com.philips.cdp.uikit.drawable.VectorDrawable;
import com.philips.platform.uid.view.widget.InputValidationLayout;
import com.philips.platform.uid.view.widget.ValidationEditText;

import java.util.regex.Pattern;

/**
 * Created by philips on 8/31/17.
 */

public class DLSBillingAddressFragment extends InAppBaseFragment
        implements View.OnClickListener, AddressController.AddressListener,
        PaymentController.PaymentListener,
        SalutationDropDown.SalutationListener,
        StateDropDown.StateListener, View.OnFocusChangeListener {

    private Context mContext;
    private InputValidationLayout mLlFirstNameBilling;
    private InputValidator inputValidatorFirstNameBilling;
    private InputValidationLayout mLlLastNameBilling;
    private InputValidator inputValidatorLastNameBilling;
    private InputValidationLayout mLlSalutationBilling;
    private InputValidator inputValidatorSalutationBilling;
    private InputValidationLayout mLlAddressLineOneBilling;
    private InputValidator inputValidatorAddressLineOneBilling;
    private InputValidationLayout mLlAddressLineTwoBilling;
    private InputValidator inputValidatorAddressLineTwoBilling;
    private InputValidationLayout mLlTownBilling;
    private InputValidator inputValidatorTownBilling;
    private InputValidationLayout mLlPostalCodeBilling;
    private InputValidator inputValidatorPostalCodeBilling;
    private InputValidationLayout mLlCountryBilling;
    private InputValidator inputValidatorCountryBilling;
    private InputValidationLayout mlLStateBilling;
    private InputValidator inputValidatorStateBilling;
    private InputValidationLayout mLlEmailBilling;
    private InputValidator inputValidatorEmailBilling;
    private InputValidationLayout mLlPhone1Billing;
    private InputValidator inputValidatorPhoneBilling;
    private ValidationEditText mEtFirstNameBilling;
    private ValidationEditText mEtLastNameBilling;
    private ValidationEditText mEtSalutationBilling;
    private ValidationEditText mEtAddressLineOneBilling;
    private ValidationEditText mEtAddressLineTwoBilling;
    private ValidationEditText mEtTownBilling;
    private ValidationEditText mEtPostalCodeBilling;
    private ValidationEditText mEtCountryBilling;
    private ValidationEditText mEtStateBilling;
    private ValidationEditText mEtEmailBilling;
    private ValidationEditText mEtPhone1Billing;
    private SalutationDropDown mSalutationDropDownBilling;
    private PhoneNumberUtil phoneNumberUtil;
    private StateDropDown mStateDropDownBilling;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dls_iap_address_billing, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View rootView) {

        phoneNumberUtil = PhoneNumberUtil.getInstance();

        mLlFirstNameBilling = (InputValidationLayout) rootView.findViewById(R.id.ll_billing_first_name);
        inputValidatorFirstNameBilling = getValidator(Validator.NAME_PATTERN);
        mLlFirstNameBilling.setValidator(inputValidatorFirstNameBilling);

        mLlLastNameBilling = (InputValidationLayout) rootView.findViewById(R.id.ll_billing_last_name);
        inputValidatorLastNameBilling = getValidator(Validator.NAME_PATTERN);
        mLlLastNameBilling.setValidator(inputValidatorLastNameBilling);

        mLlSalutationBilling = (InputValidationLayout) rootView.findViewById(R.id.ll_billing_salutation);
        inputValidatorSalutationBilling = getValidator(Validator.ADDRESS_PATTERN);
        mLlSalutationBilling.setValidator(inputValidatorSalutationBilling);

        mLlAddressLineOneBilling = (InputValidationLayout) rootView.findViewById(R.id.ll_billing_address_line_one);
        inputValidatorAddressLineOneBilling = getValidator(Validator.ADDRESS_PATTERN);
        mLlAddressLineOneBilling.setValidator(inputValidatorAddressLineOneBilling);

        mLlAddressLineTwoBilling = (InputValidationLayout) rootView.findViewById(R.id.ll_billing_address_line_two);
        inputValidatorAddressLineTwoBilling = getValidator(Validator.ADDRESS_PATTERN);
        mLlAddressLineTwoBilling.setValidator(inputValidatorAddressLineTwoBilling);

        mLlTownBilling = (InputValidationLayout) rootView.findViewById(R.id.ll_billing_town);
        inputValidatorTownBilling = getValidator(Validator.TOWN_PATTERN);
        mLlTownBilling.setValidator(inputValidatorTownBilling);

        mLlPostalCodeBilling = (InputValidationLayout) rootView.findViewById(R.id.ll_billing_postal_code);
        inputValidatorPostalCodeBilling = getValidator(Validator.POSTAL_CODE_PATTERN);
        mLlPostalCodeBilling.setValidator(inputValidatorPostalCodeBilling);


        mLlCountryBilling = (InputValidationLayout) rootView.findViewById(R.id.ll_billing_country);
        inputValidatorCountryBilling = getValidator(Validator.COUNTRY_PATTERN);
        mLlCountryBilling.setValidator(inputValidatorCountryBilling);

        mlLStateBilling = (InputValidationLayout) rootView.findViewById(R.id.ll_billing_state);
        inputValidatorStateBilling = getValidator(Validator.NAME_PATTERN);
        mlLStateBilling.setValidator(inputValidatorStateBilling);

        mLlEmailBilling = (InputValidationLayout) rootView.findViewById(R.id.ll_billing_email);
        inputValidatorEmailBilling = getValidator(Validator.EMAIL_PATTERN);
        mLlEmailBilling.setValidator(inputValidatorEmailBilling);

        mLlPhone1Billing = (InputValidationLayout) rootView.findViewById(R.id.ll_billing_phone1);
        inputValidatorPhoneBilling = getValidator(Validator.PHONE_NUMBER_PATTERN);
        mLlPhone1Billing.setValidator(inputValidatorPhoneBilling);


        //For Billing address

        mEtFirstNameBilling = (ValidationEditText) rootView.findViewById(R.id.et_billing_first_name);
        mEtLastNameBilling = (ValidationEditText) rootView.findViewById(R.id.et_billing_last_name);
        mEtSalutationBilling = (ValidationEditText) rootView.findViewById(R.id.et_billing_salutation);
        mEtAddressLineOneBilling = (ValidationEditText) rootView.findViewById(R.id.et_billing_address_line_one);
        mEtAddressLineTwoBilling = (ValidationEditText) rootView.findViewById(R.id.et_billing_address_line_two);
        mEtTownBilling = (ValidationEditText) rootView.findViewById(R.id.et_billing_town);
        mEtPostalCodeBilling = (ValidationEditText) rootView.findViewById(R.id.et_billing_postal_code);
        mEtCountryBilling = (ValidationEditText) rootView.findViewById(R.id.et_billing_country);
        mEtStateBilling = (ValidationEditText) rootView.findViewById(R.id.et_billing_state);
        mEtEmailBilling = (ValidationEditText) rootView.findViewById(R.id.et_billing_email);
        mEtPhone1Billing = (ValidationEditText) rootView.findViewById(R.id.et_billing_phone1);


        mEtCountryBilling.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mEtSalutationBilling.setKeyListener(null);
        mEtStateBilling.setKeyListener(null);


        mEtEmailBilling.setText(HybrisDelegate.getInstance(mContext).getStore().getJanRainEmail());
        mEtEmailBilling.setEnabled(false);

        mEtCountryBilling.setText(HybrisDelegate.getInstance(mContext).getStore().getCountry());
        mEtCountryBilling.setEnabled(false);
        showUSRegions();

        //For billing address fields
        mEtFirstNameBilling.addTextChangedListener(new IAPTextWatcher(mEtFirstNameBilling));
        mEtLastNameBilling.addTextChangedListener(new IAPTextWatcher(mEtLastNameBilling));
        mEtAddressLineOneBilling.addTextChangedListener(new IAPTextWatcher(mEtAddressLineOneBilling));
        mEtAddressLineTwoBilling.addTextChangedListener(new IAPTextWatcher(mEtAddressLineTwoBilling));
        mEtTownBilling.addTextChangedListener(new IAPTextWatcher(mEtTownBilling));
        mEtPostalCodeBilling.addTextChangedListener(new IAPTextWatcher(mEtPostalCodeBilling));
        mEtCountryBilling.addTextChangedListener(new IAPTextWatcher(mEtCountryBilling));
        mEtEmailBilling.addTextChangedListener(new IAPTextWatcher(mEtEmailBilling));
        mEtPhone1Billing.addTextChangedListener(new IAPTextWatcherPhoneBilling(mEtPhone1Billing));
        mEtStateBilling.addTextChangedListener(new IAPTextWatcher(mEtStateBilling));
        mEtSalutationBilling.addTextChangedListener(new IAPTextWatcher(mEtSalutationBilling));


        mEtSalutationBilling.setCompoundDrawables(null, null, Utility.getImageArrow(mContext), null);
        mSalutationDropDownBilling = new SalutationDropDown(mContext, mEtSalutationBilling, this);
        mEtSalutationBilling.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSalutationDropDownBilling.show();
                return false;
            }
        });

        mEtStateBilling.setCompoundDrawables(null, null, Utility.getImageArrow(mContext), null);
        mStateDropDownBilling = new StateDropDown(mContext, mEtStateBilling, this);
        mEtStateBilling.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utility.hideKeypad(mContext);
                mStateDropDownBilling.show();
                return false;
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    InputValidator getValidator(Pattern valid_regex_pattern) {
        return new InputValidator(valid_regex_pattern);
    }

    private void showUSRegions() {
        if (mEtCountryBilling.getText().toString().equals("US")) {
            mlLStateBilling.setVisibility(View.VISIBLE);
        } else {
            mlLStateBilling.setVisibility(View.GONE);
        }
    }



    @Override
    public void onClick(View v) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void onSalutationSelect(View view, String salutation) {

    }

    @Override
    public void onStateSelect(View view, String state) {

    }

    @Override
    public void stateRegionCode(String regionCode) {

    }

    @Override
    public void onGetPaymentDetails(Message msg) {

    }

    @Override
    public void onSetPaymentDetails(Message msg) {

    }

    @Override
    public void onGetRegions(Message msg) {

    }

    @Override
    public void onGetUser(Message msg) {

    }

    @Override
    public void onCreateAddress(Message msg) {

    }

    @Override
    public void onGetAddress(Message msg) {

    }

    @Override
    public void onSetDeliveryAddress(Message msg) {

    }

    @Override
    public void onGetDeliveryModes(Message msg) {

    }

    @Override
    public void onSetDeliveryMode(Message msg) {

    }


    private class IAPTextWatcher implements TextWatcher {
        private EditText mEditText;
        private boolean isInAfterTextChanged;

        public IAPTextWatcher(EditText editText) {
            mEditText = editText;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mEditText != mEtPhone1Billing) {
                validate(mEditText, false);
            }
        }

        public synchronized void afterTextChanged(Editable text) {
            if (mEditText == mEtPhone1Billing) {
                isInAfterTextChanged = true;
                validate(mEditText, false);
                isInAfterTextChanged = false;
            }
        }

    }

    private class IAPTextWatcherPhoneBilling implements TextWatcher {
        private EditText mEditText;
        private boolean isInAfterTextChanged;

        public IAPTextWatcherPhoneBilling(EditText editText) {
            mEditText = editText;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mEditText != mEtPhone1Billing) {
                validate(mEditText, false);
            }
        }

        public synchronized void afterTextChanged(Editable text) {
            if (mEditText == mEtPhone1Billing) {
                isInAfterTextChanged = true;
                validate(mEditText, false);
                isInAfterTextChanged = false;
            }
        }

    }


    private boolean validatePhoneNumber(EditText editText, String country, String number) {
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(number, country);
            boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
            String formattedPhoneNumber = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
            editText.setText(formattedPhoneNumber);
            editText.setSelection(editText.getText().length());
            return isValid;
        } catch (Exception e) {
            IAPLog.d("ShippingAddressFragment", "NumberParseException");
        }
        return false;
    }


    public void validate(View editText, boolean hasFocus) {

        boolean result = true;
        if (editText.getId() == R.id.et_billing_first_name && !hasFocus) {
            result = inputValidatorFirstNameBilling.isValidName(((EditText) editText).getText().toString());
            if (!result) {
                mLlFirstNameBilling.setErrorMessage(R.string.iap_first_name_error);
                mLlFirstNameBilling.showError();
            }
        }
        if (editText.getId() == R.id.et_billing_last_name && !hasFocus) {
            result = inputValidatorLastNameBilling.isValidName(((EditText) editText).getText().toString());
            if (!result) {
                mLlLastNameBilling.setErrorMessage(R.string.iap_last_name_error);
                mLlLastNameBilling.showError();
            }
        }
        if (editText.getId() == R.id.et_billing_town && !hasFocus) {
            result = inputValidatorTownBilling.isValidTown(((EditText) editText).getText().toString());
            if (!result) {
                mLlTownBilling.setErrorMessage(R.string.iap_town_error);
                mLlTownBilling.showError();
            }
        }
        if (editText.getId() == R.id.et_billing_phone1 && !hasFocus) {
            result = validatePhoneNumber(mEtPhone1Billing, HybrisDelegate.getInstance().getStore().getCountry()
                    , mEtPhone1Billing.getText().toString());
            if (!result) {
                mLlPhone1Billing.setErrorMessage(R.string.iap_phone_error);
                mLlPhone1Billing.showError();
            }
        }
        if (editText.getId() == R.id.et_billing_country && !hasFocus) {
            showUSRegions();
            result = inputValidatorCountryBilling.isValidCountry(((EditText) editText).getText().toString());
            if (!result) {
                mLlCountryBilling.setErrorMessage(R.string.iap_country_error);
                mLlCountryBilling.showError();
            }
        }
        if (editText.getId() == R.id.et_billing_postal_code && !hasFocus) {
            result = inputValidatorPostalCodeBilling.isValidPostalCode(((EditText) editText).getText().toString());
            if (!result) {
                mLlPostalCodeBilling.setErrorMessage(R.string.iap_postal_code_error);
                mLlPostalCodeBilling.showError();
            }
        }
        if (editText.getId() == R.id.et_billing_email && !hasFocus) {
            result = inputValidatorEmailBilling.isValidEmail(((EditText) editText).getText().toString());
            if (!result) {
                mLlEmailBilling.setErrorMessage(R.string.iap_email_error);
                mLlEmailBilling.showError();
            }
        }
        if (editText.getId() == R.id.et_billing_address_line_one && !hasFocus) {
            result = inputValidatorAddressLineOneBilling.isValidAddress(((EditText) editText).getText().toString());
            if (!result) {
                mLlAddressLineOneBilling.setErrorMessage(R.string.iap_address_error);
                mLlAddressLineOneBilling.showError();
            }
        }
        if (editText.getId() == R.id.et_billing_address_line_two) {
            result = inputValidatorAddressLineTwoBilling.isValidAddress(((EditText) editText).getText().toString());
            if (mEtAddressLineTwoBilling.getText().toString().trim().equals("")) {
                result = true;
            } else {
                if (!result) {
                    mLlAddressLineTwoBilling.setErrorMessage(R.string.iap_address_error);
                    mLlAddressLineTwoBilling.showError();
                }
            }

        }
        if ((editText.getId() == R.id.et_billing_salutation || editText.getId() == R.id.et_billing_state) && !hasFocus) {

        }


    }
}
