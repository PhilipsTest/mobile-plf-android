/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.Fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.address.AddressFields;
import com.philips.cdp.di.iap.address.Validator;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.controller.AddressController;
import com.philips.cdp.di.iap.controller.PaymentController;
import com.philips.cdp.di.iap.model.ModelConstants;
import com.philips.cdp.di.iap.response.addresses.Addresses;
import com.philips.cdp.di.iap.response.error.Error;
import com.philips.cdp.di.iap.response.payment.PaymentMethod;
import com.philips.cdp.di.iap.response.payment.PaymentMethods;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.session.RequestCode;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.NetworkUtility;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.cdp.di.iap.view.SalutationDropDown;
import com.philips.cdp.di.iap.view.StateDropDown;
import com.philips.cdp.tagging.Tagging;
import com.philips.cdp.uikit.customviews.InlineForms;
import com.philips.cdp.uikit.drawable.VectorDrawable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ShippingAddressFragment extends BaseAnimationSupportFragment
        implements View.OnClickListener, AddressController.AddressListener,
        PaymentController.PaymentListener, InlineForms.Validator,
        AdapterView.OnItemSelectedListener, SalutationDropDown.SalutationListener,
        StateDropDown.StateListener {
    private Context mContext;

    protected LinearLayout mLlFirstname;
    protected LinearLayout mLlLastname;
    protected LinearLayout mLlSalutation;
    protected LinearLayout mLlAdressLineOne;
    protected LinearLayout mLlAddressLineTwo;
    protected LinearLayout mLlTown;
    protected LinearLayout mLlPostalCode;
    protected LinearLayout mLlCountry;
    protected LinearLayout mlLState;
    protected LinearLayout mLlEmail;
    protected LinearLayout mLlPhoneNumber;

    protected TextView mTvTitle;
    protected EditText mEtFirstName;
    protected EditText mEtLastName;
    protected EditText mEtSalutation;
    protected EditText mEtAddressLineOne;
    protected EditText mEtAddressLineTwo;
    protected EditText mEtTown;
    protected EditText mEtPostalCode;
    protected EditText mEtCountry;
    protected EditText mEtState;
    protected EditText mEtEmail;
    protected EditText mEtPhoneNumber;

    protected Button mBtnContinue;
    protected Button mBtnCancel;

    private PaymentController mPaymentController;
    private AddressController mAddressController;
    private AddressFields mShippingAddressFields;

    protected InlineForms mInlineFormsParent;
    private Validator mValidator = null;

    private SalutationDropDown mSalutationDropDown;
    private StateDropDown mStateDropDown;

    private HashMap<String, String> mAddressFieldsHashmap = null;
    private HashMap<String, String> addressHashMap = new HashMap<>();
    private Drawable imageArrow;
    protected boolean mIgnoreTextChangeListener = false;

    PhoneNumberUtil phoneNumberUtil;
    Phonenumber.PhoneNumber phoneNumber = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(this instanceof BillingAddressFragment))
            CartModelContainer.getInstance().setAddressId(null);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.iap_shipping_address_layout, container, false);
        phoneNumberUtil = PhoneNumberUtil.getInstance();
        mInlineFormsParent = (InlineForms) rootView.findViewById(R.id.inlineForms);

        mTvTitle = (TextView) rootView.findViewById(R.id.tv_title);

        mLlFirstname = (LinearLayout) mInlineFormsParent.findViewById(R.id.ll_first_name);
        mLlLastname = (LinearLayout) mInlineFormsParent.findViewById(R.id.ll_last_name);
        mLlSalutation = (LinearLayout) mInlineFormsParent.findViewById(R.id.ll_salutation);
        mLlAdressLineOne = (LinearLayout) mInlineFormsParent.findViewById(R.id.ll_address_line_one);
        mLlAddressLineTwo = (LinearLayout) mInlineFormsParent.findViewById(R.id.ll_address_line_two);
        mLlTown = (LinearLayout) mInlineFormsParent.findViewById(R.id.ll_town);
        mLlPostalCode = (LinearLayout) mInlineFormsParent.findViewById(R.id.ll_postal_code);
        mLlCountry = (LinearLayout) mInlineFormsParent.findViewById(R.id.ll_country);
        mlLState = (LinearLayout) mInlineFormsParent.findViewById(R.id.ll_state);
        mLlEmail = (LinearLayout) mInlineFormsParent.findViewById(R.id.ll_email);
        mLlPhoneNumber = (LinearLayout) mInlineFormsParent.findViewById(R.id.ll_phone_number);

        mEtFirstName = (EditText) mInlineFormsParent.findViewById(R.id.et_first_name);
        mEtLastName = (EditText) mInlineFormsParent.findViewById(R.id.et_last_name);
        mEtSalutation = (EditText) mInlineFormsParent.findViewById(R.id.et_salutation);
        mEtAddressLineOne = (EditText) mInlineFormsParent.findViewById(R.id.et_address_line_one);
        mEtAddressLineTwo = (EditText) mInlineFormsParent.findViewById(R.id.et_address_line_two);
        mEtTown = (EditText) mInlineFormsParent.findViewById(R.id.et_town);
        mEtPostalCode = (EditText) mInlineFormsParent.findViewById(R.id.et_postal_code);
        mEtCountry = (EditText) mInlineFormsParent.findViewById(R.id.et_country);
        mEtState = (EditText) mInlineFormsParent.findViewById(R.id.et_state);
        mEtEmail = (EditText) mInlineFormsParent.findViewById(R.id.et_email);
        mEtPhoneNumber = (EditText) mInlineFormsParent.findViewById(R.id.et_phone_number);

        mEtSalutation.setKeyListener(null);
        mEtState.setKeyListener(null);

        mBtnContinue = (Button) rootView.findViewById(R.id.btn_continue);
        mBtnCancel = (Button) rootView.findViewById(R.id.btn_cancel);

        mBtnContinue.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

        mValidator = new Validator();
        mInlineFormsParent.setValidator(this);

        mAddressController = new AddressController(mContext, this);
        mPaymentController = new PaymentController(mContext, this);
        mShippingAddressFields = new AddressFields();

        mEtEmail.setText(HybrisDelegate.getInstance(getContext()).getStore().getJanRainEmail());
        mEtEmail.setEnabled(false);

        if (this instanceof BillingAddressFragment) {
            mEtCountry.setEnabled(true);
        } else {
            mEtCountry.setText(HybrisDelegate.getInstance(mContext).getStore().getCountry());
            showUSRegions();
            mEtCountry.setEnabled(false);
        }

        mEtFirstName.addTextChangedListener(new IAPTextWatcher(mEtFirstName));
        mEtLastName.addTextChangedListener(new IAPTextWatcher(mEtLastName));
        mEtAddressLineOne.addTextChangedListener(new IAPTextWatcher(mEtAddressLineOne));
        mEtAddressLineTwo.addTextChangedListener(new IAPTextWatcher(mEtAddressLineTwo));
        mEtTown.addTextChangedListener(new IAPTextWatcher(mEtTown));
        mEtPostalCode.addTextChangedListener(new IAPTextWatcher(mEtPostalCode));
        mEtCountry.addTextChangedListener(new IAPTextWatcher(mEtCountry));
        mEtEmail.addTextChangedListener(new IAPTextWatcher(mEtEmail));
        mEtPhoneNumber.addTextChangedListener(new IAPTextWatcher(mEtPhoneNumber));

       /* mEtPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher
                (HybrisDelegate.getInstance(mContext).getStore().getCountry()) {
            @Override
            public synchronized void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (!mIgnoreTextChangeListener) {
                    validate(mEtPhoneNumber, false);
                }
            }
        });*/
        mEtState.addTextChangedListener(new IAPTextWatcher(mEtState));
        mEtSalutation.addTextChangedListener(new IAPTextWatcher(mEtSalutation));

        Bundle bundle = getArguments();
        if (null != bundle && bundle.containsKey(IAPConstant.UPDATE_SHIPPING_ADDRESS_KEY)) {
            updateFields();
        }

        setImageArrow();
        mEtSalutation.setCompoundDrawables(null, null, imageArrow, null);
        mSalutationDropDown = new SalutationDropDown(mContext, mEtSalutation, this);
        mEtState.setCompoundDrawables(null, null, imageArrow, null);
        mStateDropDown = new StateDropDown(mContext, mEtState, this);

        mEtSalutation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSalutationDropDown.show();
                return false;
            }
        });

        mEtState.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mStateDropDown.show();
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setImageArrow() {
        imageArrow = VectorDrawable.create(mContext, R.drawable.iap_product_count_drop_down);
        int width = (int) getResources().getDimension(R.dimen.iap_count_drop_down_icon_width);
        int height = (int) getResources().getDimension(R.dimen.iap_count_drop_down_icon_height);
        imageArrow.setBounds(0, 0, width, height);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onGetPaymentDetails(Message msg) {
        Utility.dismissProgressDialog();
        if (mlLState.getVisibility() == View.VISIBLE)
            mShippingAddressFields.setRegionIsoCode(mEtState.getText().toString());
        if ((msg.obj).equals(NetworkConstants.EMPTY_RESPONSE)) {
            //Track new address creation
            Tagging.trackAction(IAPAnalyticsConstant.SEND_DATA,
                    IAPAnalyticsConstant.SPECIAL_EVENTS, IAPAnalyticsConstant.NEW_SHIPPING_ADDRESS_ADDED);
            CartModelContainer.getInstance().setShippingAddressFields(mShippingAddressFields);
            IAPAnalytics.trackPage(IAPAnalyticsConstant.BILLING_ADDRESS_PAGE_NAME);
            addFragment(
                    BillingAddressFragment.createInstance(new Bundle(), AnimationType.NONE), null);
        } else if ((msg.obj instanceof IAPNetworkError)) {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), getContext());
        } else if ((msg.obj instanceof PaymentMethods)) {
            //Track new address creation
            Tagging.trackAction(IAPAnalyticsConstant.SEND_DATA,
                    IAPAnalyticsConstant.SPECIAL_EVENTS, IAPAnalyticsConstant.NEW_SHIPPING_ADDRESS_ADDED);
            PaymentMethods mPaymentMethods = (PaymentMethods) msg.obj;
            List<PaymentMethod> mPaymentMethodsList = mPaymentMethods.getPayments();
            CartModelContainer.getInstance().setShippingAddressFields(mShippingAddressFields);
            IAPAnalytics.trackPage(IAPAnalyticsConstant.PAYMENT_SELECTION_PAGE_NAME);
            Bundle bundle = new Bundle();
            bundle.putSerializable(IAPConstant.PAYMENT_METHOD_LIST, (Serializable) mPaymentMethodsList);
            addFragment(
                    PaymentSelectionFragment.createInstance(bundle, AnimationType.NONE), null);
        }
    }

    @Override
    public void onClick(final View v) {

        Utility.hideKeypad(mContext);

        if (v == mBtnContinue) {
            //Edit and save address
            if (mBtnContinue.getText().toString().equalsIgnoreCase(getString(R.string.iap_save))) {
                if (!Utility.isProgressDialogShowing()) {
                    Utility.showProgressDialog(mContext, getString(R.string.iap_update_address));
                    HashMap<String, String> addressHashMap = addressPayload();
                    mAddressController.updateAddress(addressHashMap);
                }
            } else {//Add new address
                if (!Utility.isProgressDialogShowing()) {
                    Utility.showProgressDialog(mContext, getString(R.string.iap_please_wait));
                    if (mlLState.getVisibility() == View.GONE)
                        mShippingAddressFields.setRegionIsoCode(null);

                    if (CartModelContainer.getInstance().getAddressId() != null) {
                        HashMap<String, String> updateAddressPayload = addressPayload();
                        if (mlLState.getVisibility() == View.VISIBLE && CartModelContainer.getInstance().getRegionIsoCode() != null)
                            updateAddressPayload.put(ModelConstants.REGION_ISOCODE, CartModelContainer.getInstance().getRegionIsoCode());
                        updateAddressPayload.put(ModelConstants.ADDRESS_ID, CartModelContainer.getInstance().getAddressId());
                        mAddressController.updateAddress(updateAddressPayload);
                    } else {
                        mAddressController.createAddress(mShippingAddressFields);
                    }
                }
            }
        } else if (v == mBtnCancel) {
            if (getArguments().containsKey(IAPConstant.UPDATE_SHIPPING_ADDRESS_KEY)) {
                IAPAnalytics.trackPage(IAPAnalyticsConstant.SHIPPING_ADDRESS_SELECTION_PAGE_NAME);
                getFragmentManager().popBackStackImmediate();
            } else {
                IAPAnalytics.trackPage(IAPAnalyticsConstant.SHOPPING_CART_PAGE_NAME);
                addFragment
                        (ShoppingCartFragment.createInstance(new Bundle(), AnimationType.NONE), null);
            }
        }
    }

    @Override
    public void onGetAddress(Message msg) {
        if (msg.what == RequestCode.UPDATE_ADDRESS) {
            if (msg.obj instanceof IAPNetworkError) {
                Utility.dismissProgressDialog();
                handleError(msg);
            } else {
                if (CartModelContainer.getInstance().getAddressId() == null) {
                    IAPAnalytics.trackPage(IAPAnalyticsConstant.SHIPPING_ADDRESS_SELECTION_PAGE_NAME);
                    Utility.dismissProgressDialog();
                    getFragmentManager().popBackStackImmediate();
                } else {
                    mAddressController.setDeliveryAddress(CartModelContainer.getInstance().getAddressId());
                }
            }
        }
    }

    @Override
    public void onCreateAddress(Message msg) {
        if (msg.obj instanceof Addresses) {
            mBtnContinue.setEnabled(true);
            Addresses mAddresses = (Addresses) msg.obj;
            CartModelContainer.getInstance().setAddressId(mAddresses.getId());
            mAddressController.setDeliveryAddress(mAddresses.getId());
        } else if (msg.obj instanceof IAPNetworkError) {
            Utility.dismissProgressDialog();
            handleError(msg);
        }
    }

    private void showErrorFromServer(Error error) {

        if (error != null) {
            if (error.getSubject() != null) {
                String errorMessage;
                if (error.getSubject().equalsIgnoreCase(ModelConstants.COUNTRY_ISOCODE)) {
                    errorMessage = getResources().getString(R.string.iap_country_error);
                    mInlineFormsParent.setErrorMessage(errorMessage);
                    mInlineFormsParent.showError(mEtCountry);
                } else if (error.getSubject().equalsIgnoreCase(ModelConstants.POSTAL_CODE)) {
                    errorMessage = getResources().getString(R.string.iap_postal_code_error);
                    mInlineFormsParent.setErrorMessage(errorMessage);
                    mInlineFormsParent.showError(mEtPostalCode);
                } else if (error.getSubject().equalsIgnoreCase(ModelConstants.PHONE_1)) {
                    errorMessage = getResources().getString(R.string.iap_phone_error);
                    mInlineFormsParent.setErrorMessage(errorMessage);
                    mInlineFormsParent.showError(mEtPhoneNumber);
                }
                mBtnContinue.setEnabled(false);
            } else if (error.getMessage() != null) {
                NetworkUtility.getInstance().showErrorDialog(getFragmentManager(), getString(R.string.iap_ok),
                        getString(R.string.iap_network_error), error.getMessage());
            } else {
                NetworkUtility.getInstance().showErrorDialog(getFragmentManager(), getString(R.string.iap_ok),
                        getString(R.string.iap_network_error), getString(R.string.iap_check_connection));
            }
        }
    }

    public void checkFields() {
        String firstName = mEtFirstName.getText().toString();
        String lastName = mEtLastName.getText().toString();
        String addressLineOne = mEtAddressLineOne.getText().toString();
        String addressLineTwo = mEtAddressLineTwo.getText().toString();
        String postalCode = mEtPostalCode.getText().toString().replaceAll(" ", "");
        String phoneNumber = mEtPhoneNumber.getText().toString().replaceAll(" ", "");
        String town = mEtTown.getText().toString();
        String country = mEtCountry.getText().toString();
        String email = mEtEmail.getText().toString();

        if (mValidator.isValidName(firstName) && mValidator.isValidName(lastName)
                && mValidator.isValidAddress(addressLineOne) && (addressLineTwo.trim().equals("") || mValidator.isValidAddress(addressLineTwo))
                && mValidator.isValidPostalCode(postalCode)
                && mValidator.isValidEmail(email) && mValidator.isValidPhoneNumber(phoneNumber)
                && mValidator.isValidTown(town) && mValidator.isValidCountry(country)
                && (!mEtSalutation.getText().toString().trim().equalsIgnoreCase(""))
                && (mlLState.getVisibility() == View.GONE || (mlLState.getVisibility() == View.VISIBLE && !mEtState.getText().toString().trim().equalsIgnoreCase("")))) {

            mShippingAddressFields = setAddressFields(mShippingAddressFields);

            mBtnContinue.setEnabled(true);
        } else {
            mBtnContinue.setEnabled(false);
        }
    }

    @Override
    public void validate(View editText, boolean hasFocus) {
        boolean result = true;
        String errorMessage = null;

        if (editText.getId() == R.id.et_first_name && !hasFocus) {
            result = mValidator.isValidName(mEtFirstName.getText().toString());
            errorMessage = getResources().getString(R.string.iap_first_name_error);
        }
        if (editText.getId() == R.id.et_last_name && !hasFocus) {
            result = mValidator.isValidName(mEtLastName.getText().toString());
            errorMessage = getResources().getString(R.string.iap_last_name_error);
        }
        if (editText.getId() == R.id.et_town && !hasFocus) {
            result = mValidator.isValidTown(mEtTown.getText().toString());
            errorMessage = getResources().getString(R.string.iap_town_error);
        }
        if (editText.getId() == R.id.et_phone_number && !hasFocus) {
            result = validatePhoneNumber(HybrisDelegate.getInstance().getStore().getCountry()
                    , mEtPhoneNumber.getText().toString());
            errorMessage = getResources().getString(R.string.iap_phone_error);
        }
        if (editText.getId() == R.id.et_country && !hasFocus) {
            result = mValidator.isValidCountry(mEtCountry.getText().toString());
            errorMessage = getResources().getString(R.string.iap_country_error);
            showUSRegions();
        }
        if (editText.getId() == R.id.et_postal_code && !hasFocus) {
            result = mValidator.isValidPostalCode(mEtPostalCode.getText().toString());
            errorMessage = getResources().getString(R.string.iap_postal_code_error);
        }
        if (editText.getId() == R.id.et_email && !hasFocus) {
            result = mValidator.isValidEmail(mEtEmail.getText().toString());
            errorMessage = getResources().getString(R.string.iap_email_error);
        }
        if (editText.getId() == R.id.et_address_line_one && !hasFocus) {
            result = mValidator.isValidAddress(mEtAddressLineOne.getText().toString());
            errorMessage = getResources().getString(R.string.iap_address_error);
        }
        if (editText.getId() == R.id.et_address_line_two && !hasFocus) {
            if (mEtAddressLineTwo.getText().toString().trim().equals("")) {
                result = true;
            } else {
                result = mValidator.isValidAddress(mEtAddressLineTwo.getText().toString());
                errorMessage = getResources().getString(R.string.iap_address_error);
            }
        }
        if ((editText.getId() == R.id.et_salutation || editText.getId() == R.id.et_state) && !hasFocus) {
            checkFields();
        }

        if (!result) {
            mInlineFormsParent.setErrorMessage(errorMessage);
            mInlineFormsParent.showError((EditText) editText);
            mBtnContinue.setEnabled(false);
        } else {
            mInlineFormsParent.removeError(editText);
            checkFields();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(R.string.iap_address);
        if (CartModelContainer.getInstance().getRegionIsoCode() != null)
            mShippingAddressFields.setRegionIsoCode(CartModelContainer.getInstance().getRegionIsoCode());
    }

    public static ShippingAddressFragment createInstance(Bundle args, AnimationType animType) {
        ShippingAddressFragment fragment = new ShippingAddressFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSetDeliveryAddress(final Message msg) {
        if (msg.obj.equals(IAPConstant.IAP_SUCCESS)) {
            mAddressController.setDeliveryMode();
        } else {
            Utility.dismissProgressDialog();
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), getContext());
        }
    }

    @Override
    public void onSetDeliveryModes(final Message msg) {
        if (msg.obj.equals(IAPConstant.IAP_SUCCESS)) {
            mPaymentController.getPaymentDetails();
        } else {
            Utility.dismissProgressDialog();
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), getContext());
        }
    }

    @Override
    public void onSalutationSelect(String salutation) {
        mEtSalutation.setText(salutation);
    }

    @Override
    public void onStateSelect(String state) {
        mEtState.setText(state);
    }

    @Override
    public void stateRegionCode(String regionCode) {
        mShippingAddressFields.setRegionIsoCode(regionCode);
        if (addressHashMap != null) {
            addressHashMap.put(ModelConstants.REGION_ISOCODE, regionCode);
        }
        CartModelContainer.getInstance().setRegionIsoCode(regionCode);
    }

    private void showUSRegions() {
        if (mEtCountry.getText().toString().equals("US")) {
            mlLState.setVisibility(View.VISIBLE);
        } else {
            mlLState.setVisibility(View.GONE);
        }
    }

    private class IAPTextWatcher implements TextWatcher {
        private EditText mEditText;

        public IAPTextWatcher(EditText editText) {
            mEditText = editText;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mEditText != mEtPhoneNumber && !mIgnoreTextChangeListener) {
                validate(mEditText, false);
            }
        }

        private boolean isInAfterTextChanged;

        public synchronized void afterTextChanged(Editable text) {
            if (mEditText == mEtPhoneNumber && !isInAfterTextChanged) {
                isInAfterTextChanged = true;
                validate(mEditText, false);
                isInAfterTextChanged = false;
            }
        }
    }

    private boolean validatePhoneNumber(String country, String number) {
        try {
            phoneNumber = phoneNumberUtil.parse(number, country);
            boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
            String formattedPhoneNumber = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
            mEtPhoneNumber.setText(formattedPhoneNumber);
            mEtPhoneNumber.setSelection(mEtPhoneNumber.getText().length());
            return isValid;
        } catch (Exception e) {
            IAPLog.d("ShippingAddressFragment", "NumberParseException");
        }
        return false;
    }

    private HashMap<String, String> addressPayload() {
        addressHashMap.put(ModelConstants.FIRST_NAME, mEtFirstName.getText().toString());
        addressHashMap.put(ModelConstants.LAST_NAME, mEtLastName.getText().toString());
        addressHashMap.put(ModelConstants.LINE_1, mEtAddressLineOne.getText().toString());
        addressHashMap.put(ModelConstants.LINE_2, mEtAddressLineTwo.getText().toString());
        addressHashMap.put(ModelConstants.TITLE_CODE, mEtSalutation.getText().toString().toLowerCase(Locale.getDefault()));
        addressHashMap.put(ModelConstants.COUNTRY_ISOCODE, mEtCountry.getText().toString());
        addressHashMap.put(ModelConstants.POSTAL_CODE, mEtPostalCode.getText().toString().replaceAll(" ", ""));
        addressHashMap.put(ModelConstants.TOWN, mEtTown.getText().toString());
        if (mAddressFieldsHashmap != null)
            addressHashMap.put(ModelConstants.ADDRESS_ID, mAddressFieldsHashmap.get(ModelConstants.ADDRESS_ID));
        addressHashMap.put(ModelConstants.DEFAULT_ADDRESS, mEtAddressLineOne.getText().toString());
        addressHashMap.put(ModelConstants.PHONE_1, mEtPhoneNumber.getText().toString().replaceAll(" ", ""));
        addressHashMap.put(ModelConstants.PHONE_2, "");
//        addressHashMap.put(ModelConstants.PHONE_NUMBER, mEtPhoneNumber.getText().toString());
        addressHashMap.put(ModelConstants.EMAIL_ADDRESS, mEtEmail.getText().toString());
        if (mlLState.getVisibility() == View.GONE) {
            addressHashMap.put(ModelConstants.REGION_ISOCODE, null);
        }
        return addressHashMap;
    }

    private void updateFields() {
        Bundle bundle = getArguments();
        mAddressFieldsHashmap = (HashMap<String, String>) bundle.getSerializable(IAPConstant.UPDATE_SHIPPING_ADDRESS_KEY);
        if (null == mAddressFieldsHashmap) {
            return;
        }
        mBtnContinue.setText(getString(R.string.iap_save));

        mEtFirstName.setText(mAddressFieldsHashmap.get(ModelConstants.FIRST_NAME));
        mEtLastName.setText(mAddressFieldsHashmap.get(ModelConstants.LAST_NAME));
        mEtSalutation.setText(mAddressFieldsHashmap.get(ModelConstants.TITLE_CODE));
        mEtAddressLineOne.setText(mAddressFieldsHashmap.get(ModelConstants.DEFAULT_ADDRESS));
        mEtAddressLineTwo.setText(mAddressFieldsHashmap.get(ModelConstants.LINE_2));
        mEtTown.setText(mAddressFieldsHashmap.get(ModelConstants.TOWN));
        mEtPostalCode.setText(mAddressFieldsHashmap.get(ModelConstants.POSTAL_CODE));
        mEtCountry.setText(mAddressFieldsHashmap.get(ModelConstants.COUNTRY_ISOCODE));
        mEtPhoneNumber.setText(mAddressFieldsHashmap.get(ModelConstants.PHONE_NUMBER));
        mEtEmail.setText(mAddressFieldsHashmap.get(ModelConstants.EMAIL_ADDRESS));

        if (mAddressFieldsHashmap.containsKey(ModelConstants.REGION_ISOCODE) &&
                mAddressFieldsHashmap.get(ModelConstants.REGION_ISOCODE) != null) {
            mEtState.setText(mAddressFieldsHashmap.get(ModelConstants.REGION_ISOCODE));
            mlLState.setVisibility(View.VISIBLE);
        } else {
            mlLState.setVisibility(View.GONE);
        }

        if (mAddressFieldsHashmap.containsKey(ModelConstants.REGION_CODE) &&
                mAddressFieldsHashmap.get(ModelConstants.REGION_CODE) != null) {
            addressHashMap.put(ModelConstants.REGION_ISOCODE,
                    mAddressFieldsHashmap.get(ModelConstants.REGION_CODE));
        }

        setRequestFocus();
    }

    private void setRequestFocus() {
        mEtFirstName.requestFocus();
        mEtLastName.requestFocus();
        mEtSalutation.requestFocus();
        mEtAddressLineOne.requestFocus();
        mEtAddressLineTwo.requestFocus();
        mEtTown.requestFocus();
        mEtPostalCode.requestFocus();
        mEtCountry.requestFocus();
        mEtEmail.requestFocus();
        mEtPhoneNumber.requestFocus();
        if (mlLState.getVisibility() == View.VISIBLE) {
            mEtState.requestFocus();
        }
    }

    protected AddressFields setAddressFields(AddressFields addressFields) {
        addressFields.setFirstName(mEtFirstName.getText().toString());
        addressFields.setLastName(mEtLastName.getText().toString());
        addressFields.setTitleCode(mEtSalutation.getText().toString());
        addressFields.setCountryIsocode(mEtCountry.getText().toString());
        addressFields.setLine1(mEtAddressLineOne.getText().toString());
        addressFields.setLine2(mEtAddressLineTwo.getText().toString());
        addressFields.setPostalCode(mEtPostalCode.getText().toString().replaceAll(" ", ""));
        addressFields.setTown(mEtTown.getText().toString());
        addressFields.setPhoneNumber(mEtPhoneNumber.getText().toString().replaceAll(" ", ""));
        addressFields.setEmail(mEtEmail.getText().toString());

        return addressFields;
    }

    private void handleError(Message msg) {
        IAPNetworkError iapNetworkError = (IAPNetworkError) msg.obj;
        if (null != iapNetworkError.getServerError()) {
            for (int i = 0; i < iapNetworkError.getServerError().getErrors().size(); i++) {
                Error error = iapNetworkError.getServerError().getErrors().get(i);
                showErrorFromServer(error);
            }
        } else {
            NetworkUtility.getInstance().showErrorDialog(getFragmentManager(), getString(R.string.iap_ok),
                    getString(R.string.iap_network_error), getString(R.string.iap_check_connection));
        }
    }

    @Override
    public void onGetDeliveryModes(final Message msg) {
        //NOP
    }

    @Override
    public void onSetPaymentDetails(Message msg) {

    }

    @Override
    public void onGetDeliveryAddress(final Message msg) {
        //NOP
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
