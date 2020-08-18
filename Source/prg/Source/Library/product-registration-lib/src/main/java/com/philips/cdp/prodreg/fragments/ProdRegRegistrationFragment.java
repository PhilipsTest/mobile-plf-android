/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.prodreg.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.toolbox.ImageLoader;
import com.philips.cdp.prodreg.constants.AnalyticsConstants;
import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.constants.ProdRegError;
import com.philips.cdp.prodreg.constants.RegistrationState;
import com.philips.cdp.prodreg.error.ErrorHandler;
import com.philips.cdp.prodreg.error.ProdRegErrorMap;
import com.philips.cdp.prodreg.imagehandler.ImageRequestHandler;
import com.philips.cdp.prodreg.launcher.PRUiHelper;
import com.philips.cdp.prodreg.listener.ProdRegListener;
import com.philips.cdp.prodreg.listener.RegisteredProductsListener;
import com.philips.cdp.prodreg.logging.ProdRegLogger;
import com.philips.cdp.prodreg.register.ProdRegRegistrationController;
import com.philips.cdp.prodreg.register.RegisteredProduct;
import com.philips.cdp.prodreg.register.UserWithProducts;
import com.philips.cdp.prodreg.tagging.ProdRegTagging;
import com.philips.cdp.prodreg.util.ProdRegUtil;
import com.philips.cdp.prodreg.util.ProgressAlertDialog;
import com.philips.cdp.product_registration_lib.R;
import com.philips.cdp.prxclient.datamodels.summary.Data;
import com.philips.platform.uid.text.utils.UIDClickableSpan;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.DialogConstants;
import com.philips.platform.uid.view.widget.AlertDialogFragment;
import com.philips.platform.uid.view.widget.Button;
import com.philips.platform.uid.view.widget.InputValidationLayout;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.ProgressBarButton;
import com.philips.platform.uid.view.widget.ValidationEditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ProdRegRegistrationFragment extends ProdRegBaseFragment implements ProdRegRegistrationController.RegisterControllerCallBacks {

    public static final String TAG = ProdRegRegistrationFragment.class.getName();
    private ImageLoader imageLoader;
    private LinearLayout dateParentLayout, serialNumberParentLayout;
    private Label productTitleTextView, productCtnTextView;
    private ImageView productImageView;
    private ValidationEditText date_EditText;
    private ProdRegRegistrationController prodRegRegistrationController;
    private boolean textWatcherCalled = false;
    private ProgressBarButton registerButton;
    private FragmentActivity mActivity;
    //private Label tickIcon;
    //private Dialog dialog;
    private DatePickerDialog datePickerDialog;
    private Label findSerialTextView;
    private InputValidationLayout serial_input_field, date_input_field;
    private ValidationEditText field_serial;
    private String minDate;
    AlertDialogFragment alertDialogFragment;
    Bundle bundle;
    private boolean isFirstLaunch;
    private String purchaseDateStr = "";
    ProdRegUtil prodRegUtil;
    private static final long serialVersionUID = -6635233525340545671L;


    @SuppressWarnings("SimpleDateFormat")
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            final int mMonthInt = (arg2 + 1);
            String mMonth = prodRegUtil.getValidatedString(mMonthInt);
            String mDate = prodRegUtil.getValidatedString(arg3);
            SimpleDateFormat dateFormat = new SimpleDateFormat(ProdRegConstants.PROD_REG_DATE_FORMAT_SERVER);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            final Calendar mCalendar = Calendar.getInstance();
            final String mGetDeviceDate = dateFormat.format(mCalendar.getTime());
            try {
                final String text = arg1 + "-" + mMonth + "-" + mDate;
                final Date mDisplayDate = dateFormat.parse(text);
                final Date mDeviceDate = dateFormat.parse(mGetDeviceDate);
                if (!mDisplayDate.after(mDeviceDate)) {
                    purchaseDateStr = text;
                    date_EditText.setText(prodRegUtil.getDisplayDate(text));
                    prodRegRegistrationController.isValidDate(text);
                }
            } catch (ParseException e) {
                ProdRegLogger.e(TAG, e.getMessage());
            }
        }
    };


    @Override
    public int getActionbarTitleResId() {
        return R.string.PRG_NavBar_Title;
    }

    @Override
    public String getActionbarTitle() {
        return getString(R.string.PRG_NavBar_Title);
    }

    @Override
    public boolean getBackButtonState() {
        return true;
    }

    @Override
    public List<RegisteredProduct> getRegisteredProducts() {
        return prodRegRegistrationController.getRegisteredProducts();
    }

    public RegisteredProduct getRegisteredProduct() {
        return prodRegRegistrationController.getRegisteredProduct();
    }


    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        setRetainInstance(true);
        prodRegRegistrationController = new ProdRegRegistrationController(this, mActivity);
        dismissLoadingDialog();
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prodreg_single_product, container, false);
        UIDHelper.injectCalligraphyFonts();
        dateParentLayout = view.findViewById(R.id.prg_registerScreen_dateOfPurchase_Layout);
        serialNumberParentLayout = view.findViewById(R.id.prg_registerScreen_serialNumber_layout);
        productTitleTextView = view.findViewById(R.id.prg_registerScreen_productTitle_label);
        productCtnTextView = view.findViewById(R.id.prg_registerScreen_ctn_label);
        date_input_field = view.findViewById(R.id.prg_registerScreen_dateOfPurchase_validationLayout);
        date_EditText = view.findViewById(R.id.prg_registerScreen_dateOfPurchase_validationEditText);
        imageLoader = ImageRequestHandler.getInstance(mActivity.getApplicationContext()).getImageLoader();
        registerButton = view.findViewById(R.id.prg_registerScreen_register_button);
        productImageView = view.findViewById(R.id.prg_registerScreen_product_image);

        registerButton.setOnClickListener(onClickRegister());
        date_EditText.setKeyListener(null);
        date_EditText.setOnTouchListener(onClickPurchaseDate());
        ProdRegTagging.trackPage(AnalyticsConstants.PRG_REGISTER_PRODUCT);
        findSerialTextView = view.findViewById(R.id.prg_registerScreen_findSerialNumber_Label);
        makeTextViewHyperlink(findSerialTextView);
        serial_input_field = view.findViewById(R.id.prg_registerScreen_serialNumber_validationLayout);
        field_serial = view.findViewById(R.id.prg_registerScreen_serialNumber_validationEditText);

        prodRegUtil = new ProdRegUtil();
        return view;
    }

    @NonNull
    private View.OnClickListener onClickContinue() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                clearFragmentStack();
                handleCallBack(false);
                unRegisterProdRegListener();
            }
        };
    }


    @Override
    public void onResume() {
        Fragment f = this.getActivity().getSupportFragmentManager().findFragmentById(getId());
        if(!(f instanceof ProdRegRegistrationFragment)){
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        textWatcherCalled = false;
        bundle = getArguments();
        if (bundle != null) {
            isFirstLaunch = bundle.getBoolean(ProdRegConstants.PROD_REG_IS_FIRST_LAUNCH);
        }
        setRetainInstance(true);
        final FragmentActivity activity = getActivity();
        prodRegRegistrationController = new ProdRegRegistrationController(this, activity);
        if (savedInstanceState == null) {
            showLoadingDialog();
        } else {
            prodRegRegistrationController.setLaunchedRegistration(savedInstanceState.getBoolean(ProdRegConstants.IS_SIGN_IN_CALLED, false));
        }
        serial_input_field.setValidator(new InputValidationLayout.Validator() {
            @Override
            public boolean validate(CharSequence charSequence) {
                if (charSequence.length() == 0)
                    return true;
                else
                    return prodRegRegistrationController.isValidSerialNumber(field_serial.getText().toString());
            }
        });
        date_input_field.setValidator(new InputValidationLayout.Validator() {
            @Override
            public boolean validate(CharSequence charSequence) {
                if (charSequence.length() == 0)
                    return true;
                else
                    return prodRegRegistrationController.isValidDate(purchaseDateStr);
            }
        });
        prodRegRegistrationController.process(bundle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(ProdRegConstants.PROGRESS_STATE, prodRegRegistrationController.isApiCallingProgress());
        outState.putBoolean(ProdRegConstants.PRODUCT_REGISTERED, prodRegRegistrationController.isProductRegistered());
        outState.putBoolean(ProdRegConstants.IS_SIGN_IN_CALLED, prodRegRegistrationController.isLaunchedRegistration());
        super.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

//    @NonNull
//    private TextWatcher getWatcher() {
//        return new TextWatcher() {
//            @Override
//            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
//            }
//
//            @Override
//            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
//                ValidateSerialNumber();
//            }
//
//            @Override
//            public void afterTextChanged(final Editable s) {
//                ValidateSerialNumber();
//            }
//        };
//    }

    private void ValidateSerialNumber() {
        if (textWatcherCalled) {
            prodRegRegistrationController.isValidSerialNumber(field_serial.getText().toString());
            textWatcherCalled = true;
        }
    }

    private void handleSerialNumberEditTextOnError() {
        field_serial.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                prodRegRegistrationController.isValidSerialNumber(field_serial.getText().toString());
            }
        });
    }

    private void showErrorMessageSerialNumber() {
        findSerialTextView.setVisibility(View.VISIBLE);
        if (!isVisible()) {
            return;
        }
        hideProgressDialog();
        if (field_serial.length() == 0) {
            serial_input_field.showError();
            serial_input_field.setErrorMessage(getString(R.string.PRG_Please_Enter_SerialNum_Txtfldtxt));
        } else if (field_serial.length() != 0) {
            serial_input_field.showError();
            serial_input_field.setErrorMessage(getString(R.string.PRG_Invalid_SerialNum_ErrMsg));
        } else {
            serial_input_field.hideError();
        }

    }

    private void handleDateEditTextOnError() {
        date_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (!hasFocus)
                    prodRegRegistrationController.isValidDate(purchaseDateStr);
            }
        });
    }

    private void showErrorMessageDate() {
        if (!isVisible()) {
            return;
        }
        hideProgressDialog();
        if (date_EditText.length() != 0 || (date_EditText.length() == 0 && registerButton.isClickable())) {
            date_input_field.showError();
            final ProdRegErrorMap error = new ErrorHandler().getError(mActivity,
                    ProdRegError.INVALID_DATE.getCode());
            ProdRegLogger.e("Product Registration", "Invalid date");
            date_input_field.setErrorMessage(error.getDescription());
        } else {
            date_input_field.hideError();
        }

    }

    /**
     * Sets a hyperlink style to the textview.
     */
    private void makeTextViewHyperlink(Label findSerialTextView) {
        UIDClickableSpan clickableSpan = new UIDClickableSpan(new Runnable() {
            @Override
            public void run() {
                prodRegRegistrationController.onClickFindSerialNumber();
            }
        });
        SpannableString string = SpannableString.valueOf(findSerialTextView.getText());
        string.setSpan(clickableSpan, 0, string.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        clickableSpan.shouldDrawUnderline(false);
        findSerialTextView.setText(string);
    }

    private View.OnTouchListener onClickPurchaseDate() {
        return new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (datePickerDialog != null && datePickerDialog.isShowing()) {
                    return false;
                }
                if (!registerButton.isClickable()) {
                    return false;
                }
                if (!isVisible()) {
                    return false;
                }
                field_serial.setFocusable(false);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int mYear = 0;
                    int mMonthInt = 0;
                    int mDay = 0;
                    if (!purchaseDateStr.equalsIgnoreCase("")) {
                        final String[] mEditDisplayDate = purchaseDateStr.toString().split("-");
                        mYear = Integer.parseInt(mEditDisplayDate[0]);
                        mMonthInt = Integer.parseInt(mEditDisplayDate[1]) - 1;
                        mDay = Integer.parseInt(mEditDisplayDate[2]);
                    } else {
                        final Calendar mCalendar = Calendar.getInstance();
                        mYear = mCalendar.get(Calendar.YEAR);
                        mMonthInt = mCalendar.get(Calendar.MONTH);
                        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
                    }
                    datePickerDialog = new DatePickerDialog(mActivity, R.style.UIDDatePickerDialogTheme,
                            myDateListener, mYear, mMonthInt, mDay);
                    DateFormat formatter = new SimpleDateFormat(ProdRegConstants.PROD_REG_DATE_FORMAT_SERVER, Locale.ENGLISH);

                    long dateInLong = 0;
                    try {
                        final ProdRegUtil prodRegUtil = new ProdRegUtil();
                        datePickerDialog.getDatePicker().setMaxDate(prodRegUtil.getMaxDate());
                        if (minDate != null) {
                            Date date = formatter.parse(minDate);
                            dateInLong = date.getTime();
                            datePickerDialog.getDatePicker().setMinDate(dateInLong);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    datePickerDialog.show();
                    hideProgressDialog();
                    field_serial.setFocusableInTouchMode(true);
                    return true;
                }
                return false;
            }
        };
    }

    @NonNull
    private View.OnClickListener onClickRegister() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (datePickerDialog != null && datePickerDialog.isShowing()) {
                    return;
                }
                if (alertDialogFragment != null && alertDialogFragment.isVisible()) {
                    return;
                }

                hideKeyboard();
                intiateRegistration();
                // registerButton.setEnabled(false);
            }
        };
    }

    @Override
    public boolean handleBackEvent() {
        if (mActivity != null && !mActivity.isFinishing()) {
            final boolean fragmentStack = clearFragmentStack();
            handleCallBack(true);
            hideKeyboard();
            unRegisterProdRegListener();
            return fragmentStack;
        }
        return true;
    }


    @Override
    public void exitProductRegistration() {
        clearFragmentStack();
        unRegisterProdRegListener();
    }

    @Override
    public void showAlertOnError(int responseCode) {
        if (!isVisible()) return;
        hideProgressDialog();
        super.showAlertOnError(responseCode);
    }

    @Override
    public void buttonClickable(boolean isClickable) {
        registerButton.setClickable(isClickable);
    }

    @Override
    public void showFragment(Fragment fragment) {
        super.showFragment(fragment);
    }

    @Override
    public void isValidDate(boolean validDate) {
        if (validDate) {
            date_input_field.hideError();
        } else
            showErrorMessageDate();
    }

    @Override
    public void isValidSerialNumber(boolean validSerialNumber) {
//        if (validSerialNumber) {
//            findSerialTextView.setVisibility(View.GONE);
//        } else
        if (!validSerialNumber)
            showErrorMessageSerialNumber();
    }

    String imageURL;

    @Override
    public void setSummaryView(final Data summaryData) {
        try {
            if (summaryData != null && isVisible()) {
                final String productTitle = summaryData.getProductTitle();
                if (!TextUtils.isEmpty(productTitle)) {
                    productTitleTextView.setVisibility(View.VISIBLE);
                    productTitleTextView.setText(productTitle);
                } else {
                    productTitleTextView.setVisibility(View.GONE);
                }
                minDate = summaryData.getSop();
                int width = getResources().getDisplayMetrics().widthPixels;
                productImageView.getLayoutParams().height = width;

                imageURL = summaryData.getImageURL();
                imageLoader.get(imageURL, ImageLoader.getImageListener(productImageView,
                        R.drawable.product_placeholder, R.drawable.product_placeholder));
                productImageView.requestLayout();
                //          field_serial.addTextChangedListener(getWatcher());
            }
            buttonClickable(true);
        } catch (Exception e) {
            ProdRegLogger.e(TAG, e.getMessage());
            handleBackEvent();
        }
    }

    @Override
    public void setProductView(final RegisteredProduct registeredProduct) {
        if (registeredProduct.getPurchaseDate() != null) {
            purchaseDateStr = registeredProduct.getPurchaseDate();
        }
        date_EditText.setText(prodRegUtil.getDisplayDate(registeredProduct.getPurchaseDate()));
        field_serial.setText(registeredProduct.getSerialNumber());
        final String productCtn = registeredProduct.getCtn();
        if (!TextUtils.isEmpty(registeredProduct.getCtn())) {
            productCtnTextView.setVisibility(View.VISIBLE);
            productCtnTextView.setText(productCtn);
        } else {
            productCtnTextView.setVisibility(View.GONE);
        }

        handleDateEditTextOnError();
        handleSerialNumberEditTextOnError();
    }

    @Override
    public void requireFields(final boolean requireDate, final boolean requireSerialNumber) {
        if (requireDate) {
            dateParentLayout.setVisibility(View.VISIBLE);
            ProdRegTagging.trackAction(AnalyticsConstants.SEND_DATA, AnalyticsConstants.SPECIAL_EVENTS, AnalyticsConstants.PURCHASE_DATE_REQUIRED);
        }
        if (requireSerialNumber) {
            serialNumberParentLayout.setVisibility(View.VISIBLE);
            ProdRegTagging.trackAction(AnalyticsConstants.SEND_DATA, AnalyticsConstants.SPECIAL_EVENTS, AnalyticsConstants.PRG_SERIAL_NUMBER_REQUIRED);
        }
    }

    @Override
    public void logEvents(final String tag, final String data) {
        ProdRegLogger.v(tag, data);
    }

    @Override
    public void tagEvents(final String event, final String key, final String value) {
        ProdRegTagging.trackAction(event, key, value);
    }


    @SuppressWarnings("deprecation")
    @Override
    public void showSuccessLayout() {
        hideProgressDialog();
        ProdRegSuccessFragment prodRegSuccessFragment = new ProdRegSuccessFragment();
        bundle.putString(ProdRegConstants.PROD_REG_FIRST_IMAGE_ID, imageURL);
        bundle.putString(ProdRegConstants.PROD_REG_CTN, productCtnTextView.getText().toString());
        bundle.putString(ProdRegConstants.PROD_REG_TITLE, productTitleTextView.getText().toString());
        bundle.putString(ProdRegConstants.PROD_REG_WARRANTY, this.getRegisteredProduct().getEndWarrantyDate());
        prodRegSuccessFragment.setArguments(bundle);
        showFragment(prodRegSuccessFragment);
    }

    @Override
    public void hideProgress() {
        hideProgressDialog();

    }

    /**
     * should be moved to dls dialog
     **/
    @Override
    public void showAlreadyRegisteredDialog(RegisteredProduct registeredProduct) {
        try {

            if (alertDialogFragment != null && alertDialogFragment.isVisible()) {
                return;
            }
            if (!isVisible()) {
                return;
            }
            hideProgressDialog();
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            LayoutInflater lf = layoutInflater.cloneInContext(UIDHelper.getPopupThemedContext(getContext()));
            View view = lf.inflate(R.layout.prodreg_already_registered_dialog, null);
            final AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(getContext())
                    .setDialogType(DialogConstants.TYPE_DIALOG)
                    .setDialogView(view)
                    .setCancelable(true);
            alertDialogFragment = builder.create();
            alertDialogFragment.show(getFragmentManager(), "prg_registerfrag");
            Label serialNumberTitle = view.findViewById(R.id.serial_number_title_message);
            Label serialNumberRegisteredOn = view.findViewById(R.id.serial_number_registered_message);
            Label serialNumberWarranty = view.findViewById(R.id.serial_number_warranty_message);
            serialNumberTitle.setText(getString(R.string.PRG_This_Serial_No).concat(" ").concat(registeredProduct.getSerialNumber()).concat(" ").concat(getString(R.string.PRG_Already_Registered)));
            Button changeSerialNumber = view.findViewById(R.id.button_continue);
            Button closeDialog = view.findViewById(R.id.closeButton);
            if (!TextUtils.isEmpty(registeredProduct.getPurchaseDate())) {
                serialNumberRegisteredOn.setVisibility(View.VISIBLE);
                serialNumberRegisteredOn.setText(prodRegUtil.generateSpannableText(getString(R.string.PRG_registered_on), " " + prodRegUtil.getDisplayDate(registeredProduct.getPurchaseDate())));

            }
            if (!TextUtils.isEmpty(registeredProduct.getEndWarrantyDate())) {
                serialNumberWarranty.setVisibility(View.VISIBLE);
                serialNumberWarranty.setText(prodRegUtil.generateSpannableText(getString(R.string.PRG_warranty_until), " " + prodRegUtil.getDisplayDate(registeredProduct.getEndWarrantyDate())));
            }
            changeSerialNumber.setOnClickListener(v -> {
                ProdRegTagging.trackAppNotification(AnalyticsConstants.PRG_PRODUCT_ALREADY_REGISTERED, "continue");
                alertDialogFragment.dismiss();
                clearFragmentStack();
                hideProgressDialog();
                handleCallBack(true);
                unRegisterProdRegListener();
            });
            closeDialog.setOnClickListener(v -> {
                alertDialogFragment.dismiss();
                ProdRegTagging.trackAppNotification(AnalyticsConstants.PRG_PRODUCT_ALREADY_REGISTERED, "close");
            });
        } catch (Exception e) {
            ProdRegLogger.i("Exception ***", "" + e.getMessage());
        }

    }


    @Override
    public void dismissLoadingDialog() {
        dismissProdRegLoadingDialog();
    }

    @Override
    public void showLoadingDialog() {
        showProdRegLoadingDialog(getString(R.string.PRG_Looking_For_Products_Lbltxt), "prg_dialog");
    }

    @Override
    public void onStop() {
        super.onStop();
        //dismissDialogs();
    }

    private ProgressAlertDialog mProgressDialog;


    private void intiateRegistration() {
        if (serialNumberParentLayout.getVisibility() == View.VISIBLE) {
            if (!prodRegRegistrationController.isValidSerialNumber(field_serial.getText().toString())) {
                return;
            }
        }

        if (dateParentLayout.getVisibility() == View.VISIBLE) {
            if (!prodRegRegistrationController.isValidDate(purchaseDateStr)) {
                return;
            }
        }

        if (isVisible() && registerButton.isClickable()) {
            buttonClickable(false);
            intializeProgressAlertDialog();
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
                registerButton.setText(getResources().getString(R.string.PRG_Registering_Products_Lbltxt));
                prodRegRegistrationController.registerProduct(purchaseDateStr,
                        field_serial.getText().toString());
            }

        }
    }

    void intializeProgressAlertDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressAlertDialog(getActivity(), R.style.prg_Custom_loaderTheme);
            mProgressDialog.setCancelable(false);
        }
    }

    private void hideProgressDialog() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isVisible()) {
                    buttonClickable(true);
                    registerButton.setText(getResources().getString(R.string.PRG_Register_Btntxt));
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.cancel();
                    }
                }
            }
        });
    }

    UserWithProducts userWithProducts;
    RegisteredProduct registeredProduct1;

    @Override
    public void updateProductCache() {
        if (isVisible()) {
            intializeProgressAlertDialog();
            if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }

            userWithProducts = new UserWithProducts(getContext(), new ProdRegListener() {
                @Override
                public void onProdRegSuccess(RegisteredProduct registeredProduct, UserWithProducts userWithProduct) {
                    registeredProduct1 = registeredProduct;
                }

                @Override
                public void onProdRegFailed(RegisteredProduct registeredProduct, UserWithProducts userWithProduct) {

                }
            }, PRUiHelper.getInstance().getUserDataInstance());
            userWithProducts.getRegisteredProducts(getRegisteredProductsListener(getRegisteredProduct()));
        }
    }

    @NonNull
    RegisteredProductsListener getRegisteredProductsListener(final RegisteredProduct registeredProduct) {
        return new RegisteredProductsListener() {
            @Override
            public void getRegisteredProducts(final List<RegisteredProduct> registeredProducts, final long timeStamp) {
                {
                    hideProgressDialog();
                    RegisteredProduct ctnRegistered = userWithProducts.isCtnRegistered(registeredProducts, getRegisteredProduct());
                    if (null != ctnRegistered && ctnRegistered.getRegistrationState() == RegistrationState.REGISTERED) {
                        showAlreadyRegisteredDialog(ctnRegistered);
                    }
                }
            }

        };
    }
}
