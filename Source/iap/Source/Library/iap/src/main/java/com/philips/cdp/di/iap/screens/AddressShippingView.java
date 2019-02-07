package com.philips.cdp.di.iap.screens;

import android.app.Activity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.address.AddressFields;
import com.philips.cdp.di.iap.address.Validator;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.InputValidator;
import com.philips.cdp.di.iap.utils.ModelConstants;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.cdp.di.iap.view.SalutationDropDown;
import com.philips.cdp.di.iap.view.StateDropDown;
import com.philips.platform.uid.view.widget.InputValidationLayout;
import com.philips.platform.uid.view.widget.ValidationEditText;

import java.util.HashMap;

/* Copyright (c) Koninklijke Philips N.V., 2017
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

public class AddressShippingView
        implements SalutationDropDown.SalutationListener,
        StateDropDown.StateListener {

    private final AddressPresenter addressPresenter;
    private final View view;
    private Activity mContext;
    private InputValidationLayout mLlFirstName;
    private InputValidator inputValidatorFirstName;
    private InputValidationLayout mLlLastName;
    private InputValidator inputValidatorLastName;
    private InputValidationLayout mLlAddressLineOne;
    private InputValidationLayout mLlHouseNo;
    private InputValidator inputValidatorAddressLineOne;
    private InputValidator inputValidatorHouseNo;
    private InputValidationLayout mLlTown;
    private InputValidator inputValidatorTown;
    private InputValidationLayout mLlPostalCode;
    private InputValidator inputValidatorPostalCode;
    private InputValidationLayout mLlCountry;
    private InputValidator inputValidatorCountry;
    private InputValidationLayout mlLState;
    private InputValidationLayout mLlEmail;
    private InputValidator inputValidatorEmail;
    private InputValidationLayout mLlPhone1;
    private InputValidator inputValidatorPhone;
    private ValidationEditText mEtFirstName;
    private ValidationEditText mEtLastName;
    private ValidationEditText mEtSalutation;
    private ValidationEditText mEtAddressLineOne;
    private ValidationEditText mEtHouseNo;
    private ValidationEditText mEtTown;
    private ValidationEditText mEtPostalCode;
    private ValidationEditText mEtCountry;
    private ValidationEditText mEtState;
    private ValidationEditText mEtEmail;
    private ValidationEditText mEtPhone1;
    private TextView tvState,tvHouseNo,tvFirstName,tvLastName,tvSalutation,tvAddressLineOne,tvTown,tvPostalCode,tvCountry,tvEmail,tvPhone;
    private Validator mValidator;
    private SalutationDropDown mSalutationDropDown;
    private StateDropDown mStateDropDown;

    private AddressFields shippingAddressFields;
    private String mRegionIsoCode;

    AddressContractor addressContractor;


    public AddressShippingView(AddressPresenter addressPresenter) {
        this.addressPresenter = addressPresenter;
        addressContractor = addressPresenter.getAddressContractor();
        this.mContext = addressContractor.getActivityContext();
        this.view = addressContractor.getShippingAddressView();
        initializeViews(view);
    }


    private void initializeViews(View rootView) {

        shippingAddressFields = new AddressFields();
        mValidator = new Validator();

        tvFirstName = rootView.findViewById(R.id.label_first_name);
        mLlFirstName = rootView.findViewById(R.id.ll_first_name);
        mEtFirstName = rootView.findViewById(R.id.et_first_name);

        mLlLastName = rootView.findViewById(R.id.ll_last_name);
        mEtLastName = rootView.findViewById(R.id.et_last_name);
        tvLastName = rootView.findViewById(R.id.lable_last_name);

        tvSalutation = rootView.findViewById(R.id.tv_salutation);
        InputValidationLayout mLlSalutation = rootView.findViewById(R.id.ll_salutation);
        mEtSalutation = rootView.findViewById(R.id.et_salutation);


        tvAddressLineOne = rootView.findViewById(R.id.label_address1);
        mLlAddressLineOne = rootView.findViewById(R.id.ll_address_line_one);
        mEtAddressLineOne = rootView.findViewById(R.id.et_address_line_one);

        tvTown = rootView.findViewById(R.id.label_town);
        mLlTown = rootView.findViewById(R.id.ll_town);
        mEtTown = rootView.findViewById(R.id.et_town);

        tvPostalCode = rootView.findViewById(R.id.label_postal_code);
        mLlPostalCode = rootView.findViewById(R.id.ll_postal_code);
        mEtPostalCode = rootView.findViewById(R.id.et_postal_code);


        tvCountry = rootView.findViewById(R.id.label_country);
        mLlCountry = rootView.findViewById(R.id.ll_country);
        mEtCountry = rootView.findViewById(R.id.et_country);


        tvEmail = rootView.findViewById(R.id.label_email);
        mLlEmail = rootView.findViewById(R.id.ll_email);
        mEtEmail = rootView.findViewById(R.id.et_email);


        tvPhone = rootView.findViewById(R.id.label_phone);
        mLlPhone1 = rootView.findViewById(R.id.ll_phone1);
        mEtPhone1 = rootView.findViewById(R.id.et_phone1);

        tvHouseNo = rootView.findViewById(R.id.label_house_no);
        mEtHouseNo = rootView.findViewById(R.id.et_house_no);
        mLlHouseNo = rootView.findViewById(R.id.ll_house_no);


        mlLState = rootView.findViewById(R.id.ll_state);
        tvState = rootView.findViewById(R.id.lebel_state);
        mEtState = rootView.findViewById(R.id.et_state);





        if(addressContractor.isFirstNameEnabled()) {
            setViewVisible(mLlFirstName,tvFirstName,mEtFirstName);
            inputValidatorFirstName = new InputValidator(Validator.NAME_PATTERN);
            mEtFirstName.setText(HybrisDelegate.getInstance(mContext).getStore().getGivenName());
            mLlFirstName.setValidator(inputValidatorFirstName);
            mEtFirstName.addTextChangedListener(new IAPTextWatcher(mEtFirstName));
        }else{
            setViewInVisible(mLlFirstName,tvFirstName,mEtFirstName);
        }

        if(addressContractor.isLastNameEnabled()) {
            setViewVisible(mLlLastName,tvLastName,mEtLastName);
            inputValidatorLastName = new InputValidator(Validator.NAME_PATTERN);
            mLlLastName.setValidator(inputValidatorLastName);
            mEtLastName.addTextChangedListener(new IAPTextWatcher(mEtLastName));
            String familyName = HybrisDelegate.getInstance(mContext).getStore().getFamilyName();
            if (!TextUtils.isEmpty(familyName) && !familyName.equalsIgnoreCase("null")) {
                mEtLastName.setText(familyName);
            } else {
                mEtLastName.setText("");
            }
        }else{
            setViewInVisible(mLlLastName,tvLastName,mEtLastName);
        }


        if(addressContractor.isSalutationEnabled()) {
            setViewVisible(mLlSalutation,tvSalutation,mEtSalutation);
            InputValidator inputValidatorSalutation = new InputValidator(Validator.ADDRESS_PATTERN);
            mLlSalutation.setValidator(inputValidatorSalutation);

            mEtSalutation.setKeyListener(null);
            mEtSalutation.addTextChangedListener(new IAPTextWatcher(mEtSalutation));
            mEtSalutation.setCompoundDrawables(null, null, Utility.getImageArrow(mContext), null);
            mSalutationDropDown = new SalutationDropDown(mContext, mEtSalutation, this);

            mEtSalutation.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mSalutationDropDown.show();
                    return false;
                }
            });

        }else{
            setViewInVisible(mLlSalutation,tvSalutation,mEtSalutation);
        }


        if(addressContractor.isAddressLineOneEnabled()){
            setViewVisible(mLlAddressLineOne,tvAddressLineOne,mEtAddressLineOne);
            inputValidatorAddressLineOne = new InputValidator(Validator.ADDRESS_PATTERN);
            mLlAddressLineOne.setValidator(inputValidatorAddressLineOne);
            mEtAddressLineOne.addTextChangedListener(new IAPTextWatcher(mEtAddressLineOne));
        }else{
            setViewInVisible(mLlAddressLineOne,tvAddressLineOne,mEtAddressLineOne);
        }

        if(addressContractor.isTownEnabled()) {
            setViewVisible(mLlTown,tvTown,mEtTown);
            tvTown.setVisibility(View.VISIBLE);
            mLlTown.setVisibility(View.VISIBLE);
            inputValidatorTown = new InputValidator(Validator.TOWN_PATTERN);
            mLlTown.setValidator(inputValidatorTown);
            mEtTown.addTextChangedListener(new IAPTextWatcher(mEtTown));
        }else{
            setViewInVisible(mLlTown,tvTown,mEtTown);
        }

        if(addressContractor.isPostalCodeEnabled()) {
            setViewVisible(mLlPostalCode,tvPostalCode,mEtPostalCode);
            inputValidatorPostalCode = new InputValidator(Validator.POSTAL_CODE_PATTERN);
            mLlPostalCode.setValidator(inputValidatorPostalCode);
            mEtPostalCode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            mEtPostalCode.addTextChangedListener(new IAPTextWatcher(mEtPostalCode));
        }else{
            setViewInVisible(mLlPostalCode,tvPostalCode,mEtPostalCode);
        }

        if(addressContractor.isCountryEnabled()) {
            setViewVisible(mLlCountry,tvCountry,mEtCountry);
            inputValidatorCountry = new InputValidator(Validator.COUNTRY_PATTERN);
            mLlCountry.setValidator(inputValidatorCountry);
            mEtCountry.setText(HybrisDelegate.getInstance(mContext).getStore().getCountry());
            mEtCountry.setEnabled(false);
            mEtCountry.addTextChangedListener(new IAPTextWatcher(mEtCountry));
        }else{
            setViewInVisible(mLlCountry,tvCountry,mEtCountry);
        }

        if(addressContractor.isEmailEnabled()) {
            setViewVisible(mLlEmail,tvEmail,mEtEmail);
            inputValidatorEmail = new InputValidator(Validator.EMAIL_PATTERN);
            mLlEmail.setValidator(inputValidatorEmail);
            mEtEmail.setText(HybrisDelegate.getInstance(mContext).getStore().getJanRainEmail());
            mEtEmail.setEnabled(false);
            mEtEmail.addTextChangedListener(new IAPTextWatcher(mEtEmail));
        }else{
            setViewInVisible(mLlEmail,tvEmail,mEtEmail);
        }

        if(addressContractor.isPhoneNumberEnabled()) {
            setViewVisible(mLlPhone1,tvPhone,mEtPhone1);
            tvPhone.setVisibility(View.VISIBLE);
            mLlPhone1.setVisibility(View.VISIBLE);
            inputValidatorPhone = new InputValidator(Validator.PHONE_NUMBER_PATTERN);
            mLlPhone1.setValidator(inputValidatorPhone);
            mEtPhone1.addTextChangedListener(new IAPTextWatcherPhone(mEtPhone1));
        }else{
            setViewInVisible(mLlPhone1,tvPhone,mEtPhone1);
        }


        if(addressContractor.isHouseNoEnabled()) {
            setViewVisible(mLlHouseNo,tvHouseNo,mEtHouseNo);
            inputValidatorHouseNo = new InputValidator(Validator.ADDRESS_PATTERN);
            mLlHouseNo.setValidator(inputValidatorHouseNo);
            mEtHouseNo.addTextChangedListener(new IAPTextWatcher(mEtHouseNo));
        }else{
            setViewInVisible(mLlHouseNo,tvHouseNo,mEtHouseNo);
        }


        if (addressContractor.isStateEnabled()) {

            InputValidator inputValidatorState = new InputValidator(Validator.NAME_PATTERN);
            mlLState.setValidator(inputValidatorState);

            mEtState.setKeyListener(null);
            mEtState.addTextChangedListener(new IAPTextWatcher(mEtState));

            CartModelContainer.getInstance().setAddessStateVisible(true);
            setViewVisible(mlLState,tvState,mEtState);
            mEtState.setCompoundDrawables(null, null, Utility.getImageArrow(mContext), null);
            mStateDropDown = new StateDropDown(this);

            mStateDropDown.createPopUp(mEtState, mContext);


            mEtState.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Utility.hideKeypad(mContext);
                    mStateDropDown.show();
                    return false;
                }
            });
        }else{
            setViewInVisible(mlLState,tvState,mEtState);
        }
    }

    @Override
    public void onSalutationSelect(View view, String salutation) {
        mEtSalutation.setText(salutation);
        mEtSalutation.setCompoundDrawables(null, null, Utility.getImageArrow(mContext), null);
    }

    @Override
    public void onStateSelect(View view, String state) {
        mEtState.setText(state);
    }

    @Override
    public void stateRegionCode(String regionCode) {
        mRegionIsoCode = regionCode;
        shippingAddressFields.setRegionIsoCode(regionCode);
    }


    private class IAPTextWatcher implements TextWatcher {
        private EditText mEditText;

        public IAPTextWatcher(EditText editText) {
            mEditText = editText;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Do Nothing
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mEditText != mEtPhone1) {
                validateShippingAddress(mEditText, false);
            }
        }

        public synchronized void afterTextChanged(Editable text) {
            if (mEditText == mEtPhone1) {
                validateShippingAddress(mEditText, false);
            }
        }

    }

    private class IAPTextWatcherPhone implements TextWatcher {
        private EditText mEditText;
        private boolean isInAfterTextChanged;
        private boolean mIgnoreTextChangeListener = false;

        public IAPTextWatcherPhone(EditText editText) {
            mEditText = editText;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mEditText != mEtPhone1 && !mIgnoreTextChangeListener) {
                validateShippingAddress(mEditText, false);
            }
        }

        public synchronized void afterTextChanged(Editable text) {
            if (mEditText == mEtPhone1 && !isInAfterTextChanged && !mIgnoreTextChangeListener) {
                isInAfterTextChanged = true;
                validateShippingAddress(mEditText, false);
                isInAfterTextChanged = false;
            }
        }

    }

    public void validateShippingAddress(View editText, boolean hasFocus) {
        boolean result = true;
        if (editText.getId() == R.id.et_first_name && !hasFocus && editText.getVisibility() == View.VISIBLE) {
            result = inputValidatorFirstName.isValidName(((EditText) editText).getText().toString());
            if (!result) {
                mLlFirstName.setErrorMessage(R.string.iap_first_name_error);
                mLlFirstName.showError();
            }
        }
        if (editText.getId() == R.id.et_last_name && !hasFocus && editText.getVisibility() == View.VISIBLE) {
            result = inputValidatorLastName.isValidName(mEtLastName.getText().toString());
            if (!result) {
                mLlLastName.setErrorMessage(R.string.iap_last_name_error);
                mLlLastName.showError();
            }
        }
        if (editText.getId() == R.id.et_town && !hasFocus && editText.getVisibility() == View.VISIBLE) {
            result = inputValidatorTown.isValidTown(mEtTown.getText().toString());
            if (!result) {
                mLlTown.setErrorMessage(R.string.iap_town_error);
                mLlTown.showError();
            }
        }
        if (editText.getId() == R.id.et_phone1 && !hasFocus && mEtPhone1.getText() != null && editText.getVisibility() == View.VISIBLE) {
            result = addressPresenter.validatePhoneNumber(mEtPhone1, HybrisDelegate.getInstance().getStore().getCountry()
                    , mEtPhone1.getText().toString());
            if (!result) {
                mLlPhone1.setErrorMessage(R.string.iap_phone_error);
                mLlPhone1.showError();
            }
        }
        if (editText.getId() == R.id.et_country && !hasFocus && editText.getVisibility() == View.VISIBLE) {
            result = inputValidatorCountry.isValidCountry(mEtCountry.getText().toString());
            if (!result) {
                mLlCountry.setErrorMessage(R.string.iap_country_error);
                mLlCountry.showError();
            }
        }
        if (editText.getId() == R.id.et_postal_code && !hasFocus && editText.getVisibility() == View.VISIBLE) {
            result = inputValidatorPostalCode.isValidPostalCode(mEtPostalCode.getText().toString());
            if (!result) {
                mLlPostalCode.setErrorMessage(R.string.iap_postal_code_error);
                mLlPostalCode.showError();
            }
        }
        if (editText.getId() == R.id.et_email && !hasFocus && editText.getVisibility() == View.VISIBLE) {
            result = inputValidatorEmail.isValidEmail(mEtEmail.getText().toString());
            if (!result) {
                mLlEmail.setErrorMessage(R.string.iap_email_error);
                mLlEmail.showError();
            }
        }
        if (editText.getId() == R.id.et_address_line_one && !hasFocus && editText.getVisibility() == View.VISIBLE) {
            result = inputValidatorAddressLineOne.isValidAddress(mEtAddressLineOne.getText().toString());
            if (!result) {
                mLlAddressLineOne.setErrorMessage(R.string.iap_address_error);
                mLlAddressLineOne.showError();
            }
        }

        if (editText.getId() == R.id.et_house_no && !hasFocus && editText.getVisibility() == View.VISIBLE) {
            result = inputValidatorHouseNo.isValidAddress(mEtHouseNo.getText().toString());
            if (!result) {
                mLlHouseNo.setErrorMessage(R.string.iap_house_no_error);
                mLlHouseNo.showError();
            }
        }

        if ((editText.getId() == R.id.et_salutation || editText.getId() == R.id.et_state) && !hasFocus && editText.getVisibility() == View.VISIBLE) {
            checkFields();
        }

        if (!result) {
            addressContractor.setContinueButtonState(false);
        } else {
            checkFields();
        }

    }

    public boolean checkFields() {
        String firstName = mEtFirstName.getText().toString();
        String lastName = mEtLastName.getText().toString();
        String addressLineOne = mEtAddressLineOne.getText().toString();
        String houseNo = mEtHouseNo.getText().toString();
        String postalCode = mEtPostalCode.getText().toString().replaceAll(" ", "");
        String phone1 = mEtPhone1.getText().toString().replaceAll(" ", "");
        String town = mEtTown.getText().toString();
        String country = mEtCountry.getText().toString();
        String email = mEtEmail.getText().toString();


        if (isValidEntry(firstName, lastName, addressLineOne, houseNo, postalCode, phone1, town, country, email)) {

            setAddressFields(shippingAddressFields);
            IAPLog.d(IAPLog.LOG, shippingAddressFields.toString());
            if (addressContractor.getCheckBoxState() && shippingAddressFields != null) {
                addressContractor.setContinueButtonState(true);
                addressContractor.setShippingAddressFilledStatus(true);
                addressContractor.setShippingAddressFields(shippingAddressFields);
            } else if (shippingAddressFields != null && addressContractor.isBillingAddressFilled()) {
                addressContractor.setContinueButtonState(true);
                addressContractor.setShippingAddressFilledStatus(true);
            } else {
                addressContractor.setShippingAddressFilledStatus(false);
            }

            return true;
        } else {
            addressContractor.setShippingAddressFilledStatus(false);
        }
        return false;
    }

    private boolean isValidEntry(String firstName, String lastName, String addressLineOne, String houseNo, String postalCode, String phone1, String town, String country, String email) {
        boolean isValid =  (mEtFirstName.getVisibility() == View.GONE || mValidator.isValidName(firstName) )
                && (mEtLastName.getVisibility() == View.GONE || mValidator.isValidName(lastName))
                && (mEtAddressLineOne.getVisibility() == View.GONE || mValidator.isValidAddress(addressLineOne))
                && (mEtHouseNo.getVisibility()== View.GONE || mValidator.isValidAddress(houseNo))
                && (mEtPostalCode.getVisibility()==View.GONE || mValidator.isValidPostalCode(postalCode))
                && (mEtEmail.getVisibility() == View.GONE || mValidator.isValidEmail(email))
                && (mEtPhone1.getVisibility() == View.GONE || mValidator.isValidPhoneNumber(phone1))
                && (mEtTown.getVisibility() == View.GONE || mValidator.isValidTown(town))
                && (mEtCountry.getVisibility() == View.GONE || mValidator.isValidCountry(country))
                && (mEtSalutation.getVisibility() == View.GONE || !mEtSalutation.getText().toString().trim().equalsIgnoreCase(""))
                && (mlLState.getVisibility() == View.GONE || (mlLState.getVisibility() == View.VISIBLE && !mEtState.getText().toString().trim().equalsIgnoreCase("")));
        return isValid;
    }

    protected AddressFields setAddressFields(AddressFields shippingAddressFields) {
        setAddressFiledsFromEditTexts(shippingAddressFields);

        if (mlLState.getVisibility() == View.VISIBLE) {
            shippingAddressFields.setRegionIsoCode(mRegionIsoCode);
            shippingAddressFields.setRegionName(mEtState.getText().toString());
        } else {
            shippingAddressFields.setRegionIsoCode(null);
            shippingAddressFields.setRegionName(null);
        }
        return shippingAddressFields;
    }

    private void setAddressFiledsFromEditTexts(AddressFields shippingAddressFields) {
        shippingAddressFields.setFirstName(mEtFirstName.getText().toString().trim());
        shippingAddressFields.setLastName(mEtLastName.getText().toString().trim());
        String englishSalutation = addressPresenter.getEnglishSalutation((mEtSalutation.getText().toString().trim())).getField();
        shippingAddressFields.setTitleCode(englishSalutation);
        shippingAddressFields.setCountryIsocode(mEtCountry.getText().toString().trim());
        shippingAddressFields.setLine1(mEtAddressLineOne.getText().toString().trim());
        shippingAddressFields.setHouseNumber(mEtHouseNo.getText().toString().trim());
        shippingAddressFields.setPostalCode(mEtPostalCode.getText().toString().replaceAll(" ", ""));
        shippingAddressFields.setTown(mEtTown.getText().toString().trim());
        shippingAddressFields.setPhone1(mEtPhone1.getText().toString().replaceAll(" ", ""));
        shippingAddressFields.setPhone2(mEtPhone1.getText().toString().replaceAll(" ", ""));
        shippingAddressFields.setEmail(mEtEmail.getText().toString().trim());
    }

    void updateFields(HashMap<String, String> mAddressFieldsHashmap) {

        if (mAddressFieldsHashmap == null) return;
        addressPresenter.setContinueButtonState(true);
        addressContractor.setContinueButtonText(mContext.getString(R.string.iap_save));
        CartModelContainer.getInstance().setAddressId(mAddressFieldsHashmap.get(ModelConstants.ADDRESS_ID));

        mEtFirstName.setText(mAddressFieldsHashmap.get(ModelConstants.FIRST_NAME));
        mEtLastName.setText(mAddressFieldsHashmap.get(ModelConstants.LAST_NAME));
        mEtSalutation.setText(mAddressFieldsHashmap.get(ModelConstants.TITLE_CODE));
        mEtAddressLineOne.setText(addressPresenter.addressWithNewLineIfNull(mAddressFieldsHashmap.get(ModelConstants.LINE_1)));
        mEtHouseNo.setText(mAddressFieldsHashmap.get(ModelConstants.HOUSE_NO));
        mEtTown.setText(mAddressFieldsHashmap.get(ModelConstants.TOWN));
        mEtPostalCode.setText(mAddressFieldsHashmap.get(ModelConstants.POSTAL_CODE));
        mEtCountry.setText(mAddressFieldsHashmap.get(ModelConstants.COUNTRY_ISOCODE));
        mEtPhone1.setText(mAddressFieldsHashmap.get(ModelConstants.PHONE_1));
        mEtEmail.setText(mAddressFieldsHashmap.get(ModelConstants.EMAIL_ADDRESS));

        if (mAddressFieldsHashmap.containsKey(ModelConstants.REGION_CODE) &&
                mAddressFieldsHashmap.get(ModelConstants.REGION_CODE) != null) {
            String code = mAddressFieldsHashmap.get(ModelConstants.REGION_CODE);
            String stateCode = code.substring(code.length() - 2);
            mEtState.setText(stateCode);
            mlLState.setVisibility(View.VISIBLE);
        } else {
            mEtState.setVisibility(View.GONE);
            mlLState.setVisibility(View.GONE);
        }
    }

    void setViewVisible(View... args) {

        for (View view : args) {
            view.setVisibility(View.VISIBLE);
        }
    }
    void setViewInVisible(View... args) {

        for (View view : args) {
            view.setVisibility(View.GONE);
        }
    }
}
