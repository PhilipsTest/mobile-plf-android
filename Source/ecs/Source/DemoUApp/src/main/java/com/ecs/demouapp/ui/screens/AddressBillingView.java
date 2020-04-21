package com.ecs.demouapp.ui.screens;
import android.app.Activity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.address.AddressFields;
import com.ecs.demouapp.ui.address.Validator;
import com.ecs.demouapp.ui.container.CartModelContainer;
import com.ecs.demouapp.ui.utils.ECSLog;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.ecs.demouapp.ui.utils.InputValidator;
import com.ecs.demouapp.ui.utils.ModelConstants;
import com.ecs.demouapp.ui.utils.Utility;
import com.ecs.demouapp.ui.view.SalutationDropDown;
import com.ecs.demouapp.ui.view.StateDropDown;
import com.philips.platform.ecs.util.ECSConfiguration;
import com.philips.platform.pif.DataInterface.USR.UserDataInterfaceException;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;
import com.philips.platform.uid.view.widget.InputValidationLayout;
import com.philips.platform.uid.view.widget.ValidationEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* Copyright (c) Koninklijke Philips N.V., 2017
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

public class AddressBillingView
        implements SalutationDropDown.SalutationListener,
        StateDropDown.StateListener {


    private final AddressPresenter addressPresenter;
    private final Activity mContext;
    private final View view;

    private InputValidationLayout mLlFirstNameBilling;
    private InputValidator inputValidatorFirstNameBilling;
    private InputValidationLayout mLlLastNameBilling;
    private InputValidator inputValidatorLastNameBilling;
    private InputValidationLayout mLlAddressLineOneBilling;
    private InputValidator inputValidatorAddressLineOneBilling;

    private InputValidationLayout mLlHouseNoBilling;
    private InputValidator inputValidatorHouseNoBilling;

    private InputValidationLayout mLlTownBilling;


    private AddressContractor addressContractor;
    private TextView tvState, tvHouseNo, tvFirstName, tvLastName, tvSalutation, tvAddressLineOne, tvTown, tvPostalCode, tvCountry, tvEmail, tvPhone;

    HashMap<String, Object> userDetails;
    public AddressBillingView(AddressPresenter addressPresenter) {
        ArrayList<String> userDataMap = new ArrayList<>();

        userDataMap.add(UserDetailConstants.GIVEN_NAME);
        userDataMap.add(UserDetailConstants.FAMILY_NAME);
        userDataMap.add(UserDetailConstants.EMAIL);
        try{
             userDetails =   ECSUtility.getInstance().getUserDataInterface().getUserDetails(userDataMap);
        } catch (UserDataInterfaceException e) {
            e.printStackTrace();
        }


        this.addressPresenter = addressPresenter;
        addressContractor = addressPresenter.getAddressContractor();
        this.mContext = this.addressPresenter.getAddressContractor().getActivityContext();
        this.view = this.addressPresenter.getAddressContractor().getBillingAddressView();
        initializeViews(view);
    }

    private InputValidator inputValidatorTownBilling;
    private InputValidationLayout mLlPostalCodeBilling;
    private InputValidator inputValidatorPostalCodeBilling;
    private InputValidationLayout mLlCountryBilling;
    private InputValidator inputValidatorCountryBilling;
    private InputValidationLayout mlLStateBilling;
    private InputValidationLayout mLlEmailBilling;
    private InputValidator inputValidatorEmailBilling;
    private InputValidationLayout mLlPhone1Billing;
    private ValidationEditText mEtFirstNameBilling;
    private ValidationEditText mEtLastNameBilling;
    private ValidationEditText mEtSalutationBilling;
    private ValidationEditText mEtAddressLineOneBilling;
    private ValidationEditText mEtHouseNoBilling;
    private ValidationEditText mEtTownBilling;
    private ValidationEditText mEtPostalCodeBilling;
    private ValidationEditText mEtCountryBilling;
    private ValidationEditText mEtStateBilling;
    private ValidationEditText mEtEmailBilling;
    private ValidationEditText mEtPhone1Billing;
    private SalutationDropDown mSalutationDropDownBilling;
    private StateDropDown mStateDropDownBilling;
    private Validator mValidator;
    private AddressFields billingAddressFields;
    private String mRegionIsoCode;


    private void initializeViews(View rootView) {



        billingAddressFields = new AddressFields();
        mValidator = new Validator();

        mLlFirstNameBilling = rootView.findViewById(R.id.ll_billing_first_name);
        mEtFirstNameBilling = rootView.findViewById(R.id.et_billing_first_name);
        tvFirstName = rootView.findViewById(R.id.label_billing_first_name);

        mLlLastNameBilling = rootView.findViewById(R.id.ll_billing_last_name);
        mEtLastNameBilling = rootView.findViewById(R.id.et_billing_last_name);
        tvLastName = rootView.findViewById(R.id.label_billing_lastname);

        final InputValidationLayout mLlSalutationBilling = rootView.findViewById(R.id.ll_billing_salutation);
        mEtSalutationBilling = rootView.findViewById(R.id.et_billing_salutation);
        tvSalutation = rootView.findViewById(R.id.label_billing_salutation);

        mLlAddressLineOneBilling = rootView.findViewById(R.id.ll_billing_address_line_one);
        mEtAddressLineOneBilling = rootView.findViewById(R.id.et_billing_address_line_one);
        tvAddressLineOne = rootView.findViewById(R.id.label_billing_address1);


        mLlHouseNoBilling = rootView.findViewById(R.id.ll_house_no);
        mEtHouseNoBilling = rootView.findViewById(R.id.et_billing_house_no);
        tvHouseNo = rootView.findViewById(R.id.label_house_no);

        mLlTownBilling = rootView.findViewById(R.id.ll_billing_town);
        mEtTownBilling = rootView.findViewById(R.id.et_billing_town);
        tvTown = rootView.findViewById(R.id.label_billing_town);

        mLlPostalCodeBilling = rootView.findViewById(R.id.ll_billing_postal_code);
        mEtPostalCodeBilling = rootView.findViewById(R.id.et_billing_postal_code);
        tvPostalCode = rootView.findViewById(R.id.label_billing_postalCode);

        mLlCountryBilling = rootView.findViewById(R.id.ll_billing_country);
        mEtCountryBilling = rootView.findViewById(R.id.et_billing_country);
        tvCountry = rootView.findViewById(R.id.label_billing_country);

        mlLStateBilling = rootView.findViewById(R.id.ll_billing_state);
        tvState = rootView.findViewById(R.id.label_billing_state);
        mEtStateBilling = rootView.findViewById(R.id.et_billing_state);

        mLlEmailBilling = rootView.findViewById(R.id.ll_billing_email);
        mEtEmailBilling = rootView.findViewById(R.id.et_billing_email);
        tvEmail = rootView.findViewById(R.id.label_billing_email);

        mLlPhone1Billing = rootView.findViewById(R.id.ll_billing_phone1);
        mEtPhone1Billing = rootView.findViewById(R.id.et_billing_phone1);
        tvPhone = rootView.findViewById(R.id.label_billing_phone);


        if (addressContractor.isFirstNameEnabled()) {
            setViewVisible(mLlFirstNameBilling, tvFirstName ,mEtFirstNameBilling);
            inputValidatorFirstNameBilling = new InputValidator(Validator.NAME_PATTERN);
            mLlFirstNameBilling.setValidator(inputValidatorFirstNameBilling);


            String firstname=  (String) userDetails.get(UserDetailConstants.GIVEN_NAME);
            mEtFirstNameBilling.setText(firstname);
            mEtFirstNameBilling.addTextChangedListener(new IAPTextWatcher(mEtFirstNameBilling));
        }else{
            setViewInVisible(mLlFirstNameBilling, tvFirstName ,mEtFirstNameBilling);
        }


        if (addressContractor.isLastNameEnabled()) {
            setViewVisible(mLlLastNameBilling, tvLastName,mEtLastNameBilling);
            inputValidatorLastNameBilling = new InputValidator(Validator.NAME_PATTERN);
            mLlLastNameBilling.setValidator(inputValidatorLastNameBilling);
            String familyName=  (String) userDetails.get(UserDetailConstants.FAMILY_NAME);

            if (!TextUtils.isEmpty(familyName) && !familyName.equalsIgnoreCase("null")) {
                mEtLastNameBilling.setText(familyName);
            } else {
                mEtLastNameBilling.setText("");
            }
            mEtLastNameBilling.addTextChangedListener(new IAPTextWatcher(mEtLastNameBilling));
        }else{
            setViewInVisible(mLlLastNameBilling, tvLastName,mEtLastNameBilling);
        }

        if (addressContractor.isSalutationEnabled()) {
            setViewVisible(mLlSalutationBilling, tvSalutation,mEtSalutationBilling);
            final InputValidator inputValidatorSalutationBilling = new InputValidator(Validator.ADDRESS_PATTERN);
            mLlSalutationBilling.setValidator(inputValidatorSalutationBilling);
            mEtSalutationBilling.setKeyListener(null);
            mEtSalutationBilling.setCompoundDrawables(null, null, Utility.getImageArrow(mContext), null);
            mSalutationDropDownBilling = new SalutationDropDown(mContext, mEtSalutationBilling, this);
            mEtSalutationBilling.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mSalutationDropDownBilling.show();
                    return false;
                }
            });
            mEtSalutationBilling.addTextChangedListener(new IAPTextWatcher(mEtSalutationBilling));
        }else{
            setViewInVisible(mLlSalutationBilling, tvSalutation,mEtSalutationBilling);
        }

        if (addressContractor.isAddressLineOneEnabled()) {
            setViewVisible(mLlAddressLineOneBilling, tvAddressLineOne,mEtAddressLineOneBilling);
            inputValidatorAddressLineOneBilling = new InputValidator(Validator.ADDRESS_PATTERN);
            mLlAddressLineOneBilling.setValidator(inputValidatorAddressLineOneBilling);
            mEtAddressLineOneBilling.addTextChangedListener(new IAPTextWatcher(mEtAddressLineOneBilling));
        }else{
            setViewInVisible(mLlAddressLineOneBilling, tvAddressLineOne,mEtAddressLineOneBilling);
        }

        if (addressContractor.isHouseNoEnabled()) {
            setViewVisible(mLlHouseNoBilling, tvHouseNo,mEtHouseNoBilling);
            inputValidatorHouseNoBilling = new InputValidator(Validator.ADDRESS_PATTERN);
            mLlHouseNoBilling.setValidator(inputValidatorHouseNoBilling);
            mEtHouseNoBilling.addTextChangedListener(new IAPTextWatcher(mEtHouseNoBilling));
        }else{
            setViewInVisible(mLlHouseNoBilling, tvHouseNo,mEtHouseNoBilling);
        }

        if (addressContractor.isTownEnabled()) {
            setViewVisible(mLlTownBilling, tvTown,mEtTownBilling);
            inputValidatorTownBilling = new InputValidator(Validator.TOWN_PATTERN);
            mLlTownBilling.setValidator(inputValidatorTownBilling);
            mEtTownBilling.addTextChangedListener(new IAPTextWatcher(mEtTownBilling));
        }else{
            setViewInVisible(mLlTownBilling, tvTown,mEtTownBilling);
        }

        if (addressContractor.isPostalCodeEnabled()) {
            setViewVisible(mLlPostalCodeBilling, tvPostalCode,mEtPostalCodeBilling);
            inputValidatorPostalCodeBilling = new InputValidator(Validator.POSTAL_CODE_PATTERN);
            mLlPostalCodeBilling.setValidator(inputValidatorPostalCodeBilling);
            mEtPostalCodeBilling.addTextChangedListener(new IAPTextWatcher(mEtPostalCodeBilling));
        }else{
            setViewInVisible(mLlPostalCodeBilling, tvPostalCode,mEtPostalCodeBilling);
        }

        enableStateFields();

        if (addressContractor.isEmailEnabled()) {
            setViewVisible(mLlEmailBilling, tvEmail, mEtEmailBilling);
            inputValidatorEmailBilling = new InputValidator(Validator.EMAIL_PATTERN);
            mLlEmailBilling.setValidator(inputValidatorEmailBilling);

            String email=  (String) userDetails.get(UserDetailConstants.EMAIL);
            mEtEmailBilling.setText(email);

            mEtEmailBilling.setEnabled(false);
            mEtEmailBilling.addTextChangedListener(new IAPTextWatcher(mEtEmailBilling));
        }else{
            setViewInVisible(mLlEmailBilling, tvEmail, mEtEmailBilling);
        }

        if (addressContractor.isPhoneNumberEnabled()) {
            setViewVisible(mLlPhone1Billing, tvPhone, mEtPhone1Billing);
            InputValidator inputValidatorPhoneBilling = new InputValidator(Validator.PHONE_NUMBER_PATTERN);
            mLlPhone1Billing.setValidator(inputValidatorPhoneBilling);
            mEtPhone1Billing.addTextChangedListener(new IAPTextWatcherPhoneBilling(mEtPhone1Billing));
        }else{
            setViewInVisible(mLlPhone1Billing, tvPhone, mEtPhone1Billing);
        }


        if (addressContractor.isCountryEnabled()) {
            setViewVisible(mLlCountryBilling, tvCountry, mEtCountryBilling);
            inputValidatorCountryBilling = new InputValidator(Validator.COUNTRY_PATTERN);
            mLlCountryBilling.setValidator(inputValidatorCountryBilling);
            mEtCountryBilling.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            mEtCountryBilling.setText(ECSConfiguration.INSTANCE.getCountry());
            mEtCountryBilling.setEnabled(false);
        }else{
            setViewInVisible(mLlCountryBilling, tvCountry, mEtCountryBilling);
        }

    }

    private void enableStateFields() {
        if (addressContractor.isStateEnabled()) {
            setViewVisible(mlLStateBilling, tvState,mEtStateBilling);
            InputValidator inputValidatorStateBilling = new InputValidator(Validator.NAME_PATTERN);
            mlLStateBilling.setValidator(inputValidatorStateBilling);
            mEtStateBilling.setKeyListener(null);
            mEtStateBilling.addTextChangedListener(new IAPTextWatcher(mEtStateBilling));
            CartModelContainer.getInstance().setAddessStateVisible(true);

            mEtStateBilling.setCompoundDrawables(null, null, Utility.getImageArrow(mContext), null);
            mStateDropDownBilling = new StateDropDown(this);

            mStateDropDownBilling.createPopUp(mEtStateBilling, mContext);

            mEtStateBilling.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Utility.hideKeypad(mContext);
                    mStateDropDownBilling.show();
                    return false;
                }
            });
        }else{

            setViewInVisible(mlLStateBilling, tvState,mEtStateBilling);
        }
    }

    @Override
    public void onSalutationSelect(View view, String salutation) {
        mEtSalutationBilling.setText(salutation);
        mEtSalutationBilling.setCompoundDrawables(null, null, Utility.getImageArrow(mContext), null);
    }

    @Override
    public void onStateSelect(View view, String state) {
        mEtStateBilling.setText(state);
    }

    @Override
    public void stateRegionCode(String regionCode) {
        mRegionIsoCode = regionCode;
        billingAddressFields.setRegionIsoCode(regionCode);
    }

    public void updateFields(Map<String, String> mAddressFieldsHashmap) {
        if (mAddressFieldsHashmap == null) return;
        addressPresenter.setContinueButtonState(true);
        CartModelContainer.getInstance().setAddressId(mAddressFieldsHashmap.get(ModelConstants.ADDRESS_ID));

        mEtFirstNameBilling.setText(mAddressFieldsHashmap.get(ModelConstants.FIRST_NAME));
        mEtLastNameBilling.setText(mAddressFieldsHashmap.get(ModelConstants.LAST_NAME));
        mEtSalutationBilling.setText(mAddressFieldsHashmap.get(ModelConstants.TITLE_CODE));
        mEtAddressLineOneBilling.setText(addressPresenter.addressWithNewLineIfNull(mAddressFieldsHashmap.get(ModelConstants.LINE_1)));
        mEtHouseNoBilling.setText(mAddressFieldsHashmap.get(ModelConstants.HOUSE_NO));
        mEtTownBilling.setText(mAddressFieldsHashmap.get(ModelConstants.TOWN));
        mEtPostalCodeBilling.setText(mAddressFieldsHashmap.get(ModelConstants.POSTAL_CODE));
        mEtCountryBilling.setText(mAddressFieldsHashmap.get(ModelConstants.COUNTRY_ISOCODE));
        mEtPhone1Billing.setText(mAddressFieldsHashmap.get(ModelConstants.PHONE_1));
        mEtEmailBilling.setText(mAddressFieldsHashmap.get(ModelConstants.EMAIL_ADDRESS));

        if (mAddressFieldsHashmap.containsKey(ModelConstants.REGION_CODE) &&
                mAddressFieldsHashmap.get(ModelConstants.REGION_CODE) != null) {
            final String code = mAddressFieldsHashmap.get(ModelConstants.REGION_CODE);
            final String stateCode = code.substring(code.length() - 2);
            mEtStateBilling.setText(stateCode);
            mlLStateBilling.setVisibility(View.VISIBLE);
        } else {
            mlLStateBilling.setVisibility(View.GONE);
        }

        if (mAddressFieldsHashmap.containsKey(ModelConstants.REGION_CODE) &&
                mAddressFieldsHashmap.get(ModelConstants.REGION_CODE) != null) {
            mAddressFieldsHashmap.put(ModelConstants.REGION_ISOCODE,
                    mAddressFieldsHashmap.get(ModelConstants.REGION_CODE));
        }
    }

    private class IAPTextWatcher implements TextWatcher {
        private EditText mEditText;

        IAPTextWatcher(EditText editText) {
            mEditText = editText;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //  Do nothing
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mEditText != mEtPhone1Billing) {
                validate(mEditText, false);
            }
        }

        public synchronized void afterTextChanged(Editable text) {
            if (mEditText == mEtPhone1Billing) {
                validate(mEditText, false);
            }
        }
    }

    protected boolean mIgnoreTextChangeListener = false;

    private class IAPTextWatcherPhoneBilling implements TextWatcher {
        private EditText mEditText;
        private boolean isInAfterTextChanged;

        IAPTextWatcherPhoneBilling(EditText editText) {
            mEditText = editText;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Do Nothing
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mEditText != mEtPhone1Billing && !mIgnoreTextChangeListener) {
                validate(mEditText, false);
            }
        }

        public synchronized void afterTextChanged(Editable text) {
            if (mEditText == mEtPhone1Billing && !isInAfterTextChanged && !mIgnoreTextChangeListener) {
                isInAfterTextChanged = true;
                validate(mEditText, false);
                isInAfterTextChanged = false;
            }
        }
    }

    public void validate(View editText, boolean hasFocus) {

        boolean result = true;
        if (editText.getId() == R.id.et_billing_first_name && !hasFocus && addressContractor.isFirstNameEnabled()) {
            result = inputValidatorFirstNameBilling.isValidName(((EditText) editText).getText().toString());
            if (!result) {
                mLlFirstNameBilling.setErrorMessage(R.string.iap_first_name_error);
                mLlFirstNameBilling.showError();
            }
        }
        if (editText.getId() == R.id.et_billing_last_name && !hasFocus && addressContractor.isLastNameEnabled()) {
            result = inputValidatorLastNameBilling.isValidName(((EditText) editText).getText().toString());
            if (!result) {
                mLlLastNameBilling.setErrorMessage(R.string.iap_last_name_error);
                mLlLastNameBilling.showError();
            }
        }
        if (editText.getId() == R.id.et_billing_town && !hasFocus && addressContractor.isTownEnabled()) {
            result = inputValidatorTownBilling.isValidTown(((EditText) editText).getText().toString());
            if (!result) {
                mLlTownBilling.setErrorMessage(R.string.iap_town_error);
                mLlTownBilling.showError();
            }
        }
        if (editText.getId() == R.id.et_billing_phone1 && !hasFocus && addressContractor.isPhoneNumberEnabled()) {
            result = addressPresenter.validatePhoneNumber(mEtPhone1Billing, ECSConfiguration.INSTANCE.getCountry()
                    , mEtPhone1Billing.getText().toString());
            if (!result) {
                mLlPhone1Billing.setErrorMessage(R.string.iap_phone_error);
                mLlPhone1Billing.showError();
            }
        }
        if (editText.getId() == R.id.et_billing_country && !hasFocus && addressContractor.isCountryEnabled()) {
            result = inputValidatorCountryBilling.isValidCountry(((EditText) editText).getText().toString());
            if (!result) {
                mLlCountryBilling.setErrorMessage(R.string.iap_country_error);
                mLlCountryBilling.showError();
            }
        }
        if (editText.getId() == R.id.et_billing_postal_code && !hasFocus && addressContractor.isPostalCodeEnabled()) {
            result = inputValidatorPostalCodeBilling.isValidPostalCode(((EditText) editText).getText().toString());
            if (!result) {
                mLlPostalCodeBilling.setErrorMessage(R.string.iap_postal_code_error);
                mLlPostalCodeBilling.showError();
            }
        }
        if (editText.getId() == R.id.et_billing_email && !hasFocus && addressContractor.isEmailEnabled()) {
            result = inputValidatorEmailBilling.isValidEmail(((EditText) editText).getText().toString());
            if (!result) {
                mLlEmailBilling.setErrorMessage(R.string.iap_email_error);
                mLlEmailBilling.showError();
            }
        }
        if (editText.getId() == R.id.et_billing_address_line_one && !hasFocus && addressContractor.isAddressLineOneEnabled()) {
            result = inputValidatorAddressLineOneBilling.isValidAddress(((EditText) editText).getText().toString());
            if (!result) {
                mLlAddressLineOneBilling.setErrorMessage(R.string.iap_address_error);
                mLlAddressLineOneBilling.showError();
            }
        }
        if (editText.getId() == R.id.et_house_no && !hasFocus && addressContractor.isHouseNoEnabled()) {
            result = inputValidatorHouseNoBilling.isValidAddress(((EditText) editText).getText().toString());
            if (!result) {
                mLlHouseNoBilling.setErrorMessage(R.string.iap_address_error);
                mLlHouseNoBilling.showError();
            }
        }
        if ((editText.getId() == R.id.et_billing_salutation || editText.getId() == R.id.et_billing_state) && !hasFocus && addressContractor.isSalutationEnabled()) {
            checkBillingAddressFields();
        }
        if (!result) {
            addressPresenter.setContinueButtonState(false);
        } else {
            checkBillingAddressFields();
        }


    }

    public boolean checkBillingAddressFields() {
        final String firstName = mEtFirstNameBilling.getText().toString();
        final String lastName = mEtLastNameBilling.getText().toString();
        final String addressLineOne = mEtAddressLineOneBilling.getText().toString();
        final String houseNo = mEtHouseNoBilling.getText().toString();
        final String postalCode = mEtPostalCodeBilling.getText().toString().trim();
        final String phone1 = mEtPhone1Billing.getText().toString().replaceAll(" ", "");
        final String town = mEtTownBilling.getText().toString();
        final String country = mEtCountryBilling.getText().toString();
        final String email = mEtEmailBilling.getText().toString();


        if (isValidEntry(firstName, lastName, addressLineOne, houseNo, postalCode, phone1, town, country, email)) {

            setBillingAddressFields(billingAddressFields);
            ECSLog.d(ECSLog.LOG, billingAddressFields.toString());
            if (billingAddressFields != null) {
                addressPresenter.setBillingAddressFields(billingAddressFields);
                addressContractor.setBillingAddressFilledStatus(true);
                if (addressContractor.isShippingAddressFilled() || addressContractor.isAddressFilledFromDeliveryAddress() || addressContractor.isBillingAddressFilled()) {
                    addressPresenter.setContinueButtonState(true);
                } else {
                    addressPresenter.setContinueButtonState(false);
                }
                return true;
            } else {
                addressContractor.setBillingAddressFilledStatus(false);
                addressPresenter.setContinueButtonState(false);
            }
        } else {
            addressContractor.setBillingAddressFilledStatus(false);
            addressPresenter.setContinueButtonState(false);
        }
        return false;
    }

    private boolean isValidEntry(String firstName, String lastName, String addressLineOne, String houseNo, String postalCode, String phone1, String town, String country, String email) {
        boolean isValid =
                (mEtFirstNameBilling.getVisibility() == View.GONE || mValidator.isValidName(firstName))
                        && (mEtLastNameBilling.getVisibility() == View.GONE || mValidator.isValidName(lastName))
                        && (mEtAddressLineOneBilling.getVisibility() == View.GONE || mValidator.isValidAddress(addressLineOne))
                        && (mEtHouseNoBilling.getVisibility() == View.GONE || mValidator.isValidAddress(houseNo))
                        && (mEtHouseNoBilling.getVisibility() == View.GONE || mValidator.isValidPostalCode(postalCode))
                        && (mEtEmailBilling.getVisibility() == View.GONE || mValidator.isValidEmail(email))
                        && (mEtPhone1Billing.getVisibility() == View.GONE || mValidator.isValidPhoneNumber(phone1))
                        && (mEtTownBilling.getVisibility() == View.GONE || mValidator.isValidTown(town))
                        && (mEtCountryBilling.getVisibility() == View.GONE || mValidator.isValidCountry(country))
                        && (mEtSalutationBilling.getVisibility() == View.GONE || (!mEtSalutationBilling.getText().toString().trim().equalsIgnoreCase("")))
                        && (mlLStateBilling.getVisibility() == View.GONE || (mlLStateBilling.getVisibility() == View.VISIBLE && !mEtStateBilling.getText().toString().trim().isEmpty()));
        return isValid;
    }

    protected AddressFields setBillingAddressFields(AddressFields billingAddressFields) {
        billingAddressFields.setFirstName(mEtFirstNameBilling.getText().toString().trim());
        billingAddressFields.setLastName(mEtLastNameBilling.getText().toString().trim());
        String englishSalutation = addressPresenter.getEnglishSalutation((mEtSalutationBilling.getText().toString().trim())).getField();
        billingAddressFields.setTitleCode(englishSalutation);
        billingAddressFields.setCountryIsocode(mEtCountryBilling.getText().toString().trim());
        billingAddressFields.setLine1(mEtAddressLineOneBilling.getText().toString().trim());
        billingAddressFields.setHouseNumber(mEtHouseNoBilling.getText().toString().trim());
        billingAddressFields.setPostalCode(mEtPostalCodeBilling.getText().toString().trim());
        billingAddressFields.setTown(mEtTownBilling.getText().toString().trim());
        billingAddressFields.setPhone1(mEtPhone1Billing.getText().toString().replaceAll(" ", ""));
        billingAddressFields.setPhone2(mEtPhone1Billing.getText().toString().replaceAll(" ", ""));
        billingAddressFields.setEmail(mEtEmailBilling.getText().toString().trim());


        if (mlLStateBilling.getVisibility() == View.VISIBLE) {
            this.billingAddressFields.setRegionIsoCode(mRegionIsoCode);
            this.billingAddressFields.setRegionName(mEtStateBilling.getText().toString());
        } else {
            this.billingAddressFields.setRegionIsoCode(null);
            this.billingAddressFields.setRegionName(null);
        }
        return this.billingAddressFields;
    }

    void clearAllFields() {
        mIgnoreTextChangeListener = true;
        mEtFirstNameBilling.setText("");
        mEtLastNameBilling.setText("");
        mEtSalutationBilling.setText("");
        mEtAddressLineOneBilling.setText("");
        mEtHouseNoBilling.setText("");
        mEtTownBilling.setText("");
        mEtPostalCodeBilling.setText("");
        mEtPhone1Billing.setText("");
        mEtStateBilling.setText("");
        if (addressContractor.isStateEnabled()) {
            tvState.setVisibility(View.VISIBLE);
            mlLStateBilling.setVisibility(View.VISIBLE);
            mEtStateBilling.setVisibility(View.VISIBLE);
        } else {
            mlLStateBilling.setVisibility(View.GONE);
            mEtStateBilling.setVisibility(View.GONE);
        }
        mIgnoreTextChangeListener = false;
    }

    void disableAllFields() {
        setFieldsEnabled(false);
        disableFocus();
    }

    void enableAllFields() {
        setFieldsEnabled(true);
        enableFocus();
    }

    private void enableFocus() {
        setFieldsFocusable(true);
    }

    private void disableFocus() {
        setFieldsFocusable(false);
    }

    private void setFieldsEnabled(boolean enable) {
        mEtFirstNameBilling.setEnabled(enable);
        mEtLastNameBilling.setEnabled(enable);
        mEtSalutationBilling.setEnabled(enable);
        mEtAddressLineOneBilling.setEnabled(enable);
        mEtHouseNoBilling.setEnabled(enable);
        mEtTownBilling.setEnabled(enable);
        mEtPostalCodeBilling.setEnabled(enable);
        if (addressContractor.isStateEnabled()) {
            tvState.setVisibility(View.VISIBLE);
            mlLStateBilling.setVisibility(View.VISIBLE);
            mEtStateBilling.setEnabled(enable);
            mEtStateBilling.setVisibility(View.VISIBLE);
        }
        mEtPhone1Billing.setEnabled(enable);
    }

    private void setFieldsFocusable(boolean focusable) {
        mEtFirstNameBilling.setFocusable(focusable);
        mEtLastNameBilling.setFocusable(focusable);
        mEtSalutationBilling.setFocusable(focusable);
        mEtAddressLineOneBilling.setFocusable(focusable);
        mEtHouseNoBilling.setFocusable(focusable);
        mEtTownBilling.setFocusable(focusable);
        mEtPostalCodeBilling.setFocusable(focusable);
        if (addressContractor.isStateEnabled()) {
            mEtStateBilling.setFocusable(focusable);
        }
        mEtPhone1Billing.setFocusable(focusable);

        if (focusable) {
            mEtFirstNameBilling.setFocusableInTouchMode(true);
            mEtLastNameBilling.setFocusableInTouchMode(true);
            mEtSalutationBilling.setFocusableInTouchMode(true);
            mEtAddressLineOneBilling.setFocusableInTouchMode(true);
            mEtHouseNoBilling.setFocusableInTouchMode(true);
            mEtTownBilling.setFocusableInTouchMode(true);
            mEtPostalCodeBilling.setFocusableInTouchMode(true);
            if (addressContractor.isStateEnabled()) {
                mEtStateBilling.setFocusableInTouchMode(true);
            }
            mEtPhone1Billing.setFocusableInTouchMode(true);
        }

        mEtCountryBilling.setFocusable(false);
        mEtCountryBilling.setFocusableInTouchMode(false);
        mEtEmailBilling.setFocusableInTouchMode(false);
        mEtEmailBilling.setFocusable(false);
        mEtEmailBilling.setEnabled(false);
        mEtCountryBilling.setEnabled(false);
    }

    void prePopulateShippingAddress() {
        mIgnoreTextChangeListener = true;

        if (billingAddressFields != null) {
            mEtFirstNameBilling.setText(billingAddressFields.getFirstName());
            mEtLastNameBilling.setText(billingAddressFields.getLastName());
            mEtSalutationBilling.setText(billingAddressFields.getTitleCode());
            mEtAddressLineOneBilling.setText(addressPresenter.addressWithNewLineIfNull(billingAddressFields.getLine1()));
            mEtHouseNoBilling.setText(billingAddressFields.getHouseNumber());
            mEtTownBilling.setText(billingAddressFields.getTown());
            mEtPostalCodeBilling.setText(billingAddressFields.getPostalCode());
            mEtCountryBilling.setText(ECSConfiguration.INSTANCE.getCountry());
            String email=  (String) userDetails.get(UserDetailConstants.EMAIL);
            mEtEmailBilling.setText(email);

            if (addressContractor.isStateEnabled() && billingAddressFields.getRegionName() != null) {
                mEtStateBilling.setVisibility(View.VISIBLE);
                mEtStateBilling.setText(billingAddressFields.getRegionName());
                mlLStateBilling.setVisibility(View.VISIBLE);
            } else {
                mlLStateBilling.setVisibility(View.GONE);
            }
            mIgnoreTextChangeListener = false;
            mEtPhone1Billing.setText(billingAddressFields.getPhone1());
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
