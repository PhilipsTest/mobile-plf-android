package com.philips.cdp.prodreg.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.philips.cdp.prodreg.ProdRegConstants;
import com.philips.cdp.prodreg.RegistrationState;
import com.philips.cdp.prodreg.alert.ProdRegLoadingFragment;
import com.philips.cdp.prodreg.error.ErrorHandler;
import com.philips.cdp.prodreg.error.ProdRegError;
import com.philips.cdp.prodreg.imagehandler.ImageRequestHandler;
import com.philips.cdp.prodreg.listener.DialogOkButtonListener;
import com.philips.cdp.prodreg.listener.ProdRegListener;
import com.philips.cdp.prodreg.model.metadata.ProductMetadataResponseData;
import com.philips.cdp.prodreg.model.summary.Data;
import com.philips.cdp.prodreg.register.LocalRegisteredProducts;
import com.philips.cdp.prodreg.register.ProdRegHelper;
import com.philips.cdp.prodreg.register.Product;
import com.philips.cdp.prodreg.register.RegisteredProduct;
import com.philips.cdp.prodreg.register.UserWithProducts;
import com.philips.cdp.prodreg.util.ProdRegUtil;
import com.philips.cdp.product_registration_lib.R;
import com.philips.cdp.registration.User;
import com.philips.cdp.uikit.customviews.InlineForms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class ProdRegRegistrationFragment extends ProdRegBaseFragment {

    public static final String TAG = ProdRegRegistrationFragment.class.getName();
    private ImageLoader imageLoader;
    private TextView productFriendlyNameTextView, productTitleTextView, productCtnTextView;
    private Button registerButton;
    private ImageView productImageView;
    private ProductMetadataResponseData productMetadataResponseData;
    private Product currentProduct;
    private EditText serial_number_editText, date_EditText;
    private InlineForms serialLayout, purchaseDateLayout;
    private RegisteredProduct registeredProduct;
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            final int mMonthInt = (arg2 + 1);
            String mMonth = getValidatedString(mMonthInt);
            String mDate = getValidatedString(arg3);
            SimpleDateFormat dateFormat = new SimpleDateFormat(getResources().getString(R.string.date_formate));
            final Calendar mCalendar = Calendar.getInstance();
            final String mGetDeviceDate = dateFormat.format(mCalendar.getTime());
            try {
                final String text = arg1 + "-" + mMonth + "-" + mDate;
                final Date mDisplayDate = dateFormat.parse(text);
                final Date mDeviceDate = dateFormat.parse(mGetDeviceDate);
                if (!mDisplayDate.after(mDeviceDate)) {
                    date_EditText.setText(text);
                    if (!ProdRegUtil.isValidDate(text)) {
                        showErrorMessageDate(date_EditText);
                    } else {
                        registerButton.setEnabled(true);
                        purchaseDateLayout.removeError(date_EditText);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public String getActionbarTitle() {
        return getActivity().getString(R.string.PPR_NavBar_Title);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        dismissDialog();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prodreg_single_product, container, false);
        productFriendlyNameTextView = (TextView) view.findViewById(R.id.friendly_name);
        productTitleTextView = (TextView) view.findViewById(R.id.product_title);
        productCtnTextView = (TextView) view.findViewById(R.id.product_ctn);
        serial_number_editText = (EditText) view.findViewById(R.id.serial_edit_text);
        date_EditText = (EditText) view.findViewById(R.id.date_edit_text);
        serialLayout = (InlineForms) view.findViewById(R.id.InlineForms_serial_number);
        purchaseDateLayout = (InlineForms) view.findViewById(R.id.InlineForms_date);
        imageLoader = ImageRequestHandler.getInstance(getActivity().getApplicationContext()).getImageLoader();
        registerButton = (Button) view.findViewById(R.id.btn_register);
        productImageView = (ImageView) view.findViewById(R.id.product_image);
        registerButton.setOnClickListener(onClickRegister());
        date_EditText.setKeyListener(null);
        date_EditText.setOnClickListener(onClickPurchaseDate());
        serial_number_editText.addTextChangedListener(getWatcher());
        return view;
    }

    @Override
    public void onStart() {
        resetErrorDialogIfExists();
        final RegisteredProduct productAlreadyRegistered = registeredProduct.isProductAlreadyRegistered(new LocalRegisteredProducts(getActivity(), new User(getActivity())));
        if (productAlreadyRegistered != null && productAlreadyRegistered.getRegistrationState() != null && productAlreadyRegistered.getRegistrationState() == RegistrationState.REGISTERED) {
            showFragment(new ProdRegSuccessFragment());
        }
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            currentProduct = (Product) bundle.getSerializable(ProdRegConstants.PROD_REG_PRODUCT);
            mapToRegisteredProduct();
            productMetadataResponseData = (ProductMetadataResponseData) bundle.getSerializable(ProdRegConstants.PROD_REG_PRODUCT_METADATA);
            final Data summaryData = (Data) bundle.getSerializable(ProdRegConstants.PROD_REG_PRODUCT_SUMMARY);
            updateSummaryView(summaryData);
            updateProductView();
        } else {
            clearFragmentStack();
        }
    }

    @NonNull
    private TextWatcher getWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            }
            @Override
            public void afterTextChanged(final Editable s) {
                if (isValidSerialNumber(productMetadataResponseData, serial_number_editText.getText().toString())) {
                    serialLayout.removeError(serial_number_editText);
                    registerButton.setEnabled(true);
                } else {
                    showErrorMessageSerialNumber(serial_number_editText);
                }
            }
        };
    }

    @NonNull
    private ProdRegListener getProdRegListener() {
        return new ProdRegListener() {
            @Override
            public void onProdRegSuccess(RegisteredProduct registeredProduct, UserWithProducts userWithProducts) {
                    final FragmentActivity activity = getActivity();
                    if (activity != null && !activity.isFinishing()) {
                        dismissDialog();
                        showFragment(new ProdRegSuccessFragment());
                    }
            }

            @Override
            public void onProdRegFailed(RegisteredProduct registeredProduct, UserWithProducts userWithProducts) {
                    final FragmentActivity activity = getActivity();
                    if (activity != null && !activity.isFinishing()) {
                        dismissDialog();
                        if (registeredProduct.getProdRegError() != ProdRegError.PRODUCT_ALREADY_REGISTERED) {
                            showAlertOnError(registeredProduct.getProdRegError().getCode());
                        } else {
                            showFragment(new ProdRegSuccessFragment());
                        }
                    }
                }
        };
    }

    private void dismissDialog() {
        final FragmentActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            Fragment prev = activity.getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null && prev instanceof DialogFragment) {
                ((DialogFragment) prev).dismissAllowingStateLoss();
            }
        }
    }

    private void updateSummaryView(final Data summaryData) {
        if (summaryData != null) {
            final String familyName = summaryData.getFamilyName();
            final String productTitle = summaryData.getProductTitle();
            productFriendlyNameTextView.setText(familyName != null ? familyName : "");
            productTitleTextView.setText(productTitle != null ? productTitle : "");
            imageLoader.get(summaryData.getImageURL(), ImageLoader.getImageListener(productImageView, R.drawable.prod_reg_default_placeholder, R.drawable.prod_reg_default_placeholder));
        }
    }

    private void updateProductView() {
        if (currentProduct != null) {
            date_EditText.setText(currentProduct.getPurchaseDate());
            serial_number_editText.setText(currentProduct.getSerialNumber());
            final String productCtn = currentProduct.getCtn();
            productCtnTextView.setText(productCtn != null ? productCtn : "");
            handleDateEditTextOnError();
            handleSerialNumberEditTextOnError();
            validateFields();
        }
    }

    private boolean validateFields() {
        final boolean isValidSerialNumber = isValidSerialNumber(productMetadataResponseData, serial_number_editText.getText().toString());
        final boolean isValidDate = isValidDate(productMetadataResponseData, date_EditText.getText().toString());
        if (!isValidDate && !isValidSerialNumber) {
            showErrorMessageDate(date_EditText);
            showErrorMessageSerialNumber(serial_number_editText);
        } else if (!isValidDate) {
            showErrorMessageDate(date_EditText);
        } else if (!isValidSerialNumber) {
            showErrorMessageSerialNumber(serial_number_editText);
        } else {
            registerButton.setEnabled(true);
            return true;
        }
        return false;
    }

    private boolean isValidDate(final ProductMetadataResponseData productMetadataResponseData, final String purchaseDate) {
        boolean isValidDate = true;
        if (productMetadataResponseData != null && productMetadataResponseData.getRequiresDateOfPurchase().equalsIgnoreCase("true")) {
            isValidDate = ProdRegUtil.isValidDate(purchaseDate);
            purchaseDateLayout.setVisibility(View.VISIBLE);
        }
        return isValidDate;
    }

    private void handleSerialNumberEditTextOnError() {
        serialLayout.setValidator(new InlineForms.Validator() {
            @Override
            public void validate(final View editText, final boolean hasFocus) {
                if (isValidSerialNumber(productMetadataResponseData, serial_number_editText.getText().toString())) {
                    serialLayout.removeError(serial_number_editText);
                    registerButton.setEnabled(true);
                } else {
                    showErrorMessageSerialNumber(serial_number_editText);
                }
            }
        });
    }

    private void showErrorMessageSerialNumber(final EditText editTextView) {
        registerButton.setEnabled(false);
        serialLayout.setErrorMessage(new ErrorHandler().getError(getActivity(), ProdRegError.INVALID_SERIALNUMBER.getCode()).getDescription() + productMetadataResponseData.getSerialNumberFormat());
        serialLayout.showError(editTextView);
    }

    private void handleDateEditTextOnError() {
        purchaseDateLayout.setValidator(new InlineForms.Validator() {
            @Override
            public void validate(final View editText, final boolean hasFocus) {
                EditText editTextView = (EditText) editText;
                final String date = editTextView.getText().toString();
                if (!ProdRegUtil.isValidDate(date)) {
                    showErrorMessageDate(editTextView);
                } else {
                    purchaseDateLayout.removeError(date_EditText);
                }
            }
        });
    }

    private void showErrorMessageDate(final EditText editTextView) {
        registerButton.setEnabled(false);
        purchaseDateLayout.setErrorMessage(new ErrorHandler().getError(getActivity(), ProdRegError.INVALID_DATE.getCode()).getDescription());
        purchaseDateLayout.showError(editTextView);
    }

    protected boolean isValidSerialNumber(final ProductMetadataResponseData data, final String serialNumber) {
        if (data != null && data.getRequiresSerialNumber().equalsIgnoreCase("true")) {
            serialLayout.setVisibility(View.VISIBLE);
            if ((ProdRegUtil.isValidSerialNumber(data.getSerialNumberFormat(), serialNumber)))
                return false;
        }
        return true;
    }

    private String getValidatedString(final int value) {
        final String valueString;
        if (value < 10) {
            valueString = getResources().getString(R.string.zero) + value;
        } else {
            valueString = Integer.toString(value);
        }
        return valueString;
    }

    private View.OnClickListener onClickPurchaseDate() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                int mYear;
                int mMonthInt;
                int mDay;
                if (!date_EditText.getText().toString().equalsIgnoreCase("")) {
                    final String[] mEditDisplayDate = date_EditText.getText().toString().split("-");
                    mYear = Integer.parseInt(mEditDisplayDate[0]);
                    mMonthInt = Integer.parseInt(mEditDisplayDate[1]) - 1;
                    mDay = Integer.parseInt(mEditDisplayDate[2]);
                } else {
                    final Calendar mCalendar = Calendar.getInstance();
                    mYear = mCalendar.get(Calendar.YEAR);
                    mMonthInt = mCalendar.get(Calendar.MONTH);
                    mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), myDateListener, mYear, mMonthInt, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        };
    }

    @NonNull
    private View.OnClickListener onClickRegister() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (validateFields()) {
                    showProgressAlertDialog(getActivity().getString(R.string.prod_reg_registering_product));
                    registeredProduct.setPurchaseDate(date_EditText.getText().toString());
                    registeredProduct.setSerialNumber(serial_number_editText.getText().toString());
                    ProdRegHelper prodRegHelper = new ProdRegHelper();
                    prodRegHelper.addProductRegistrationListener(getProdRegListener());
                    prodRegHelper.getSignedInUserWithProducts().registerProduct(registeredProduct);
                }
            }
        };
    }

    private RegisteredProduct mapToRegisteredProduct() {
        if (currentProduct != null) {
            registeredProduct = new RegisteredProduct(currentProduct.getCtn(), currentProduct.getSector(), currentProduct.getCatalog());
            registeredProduct.setSerialNumber(currentProduct.getSerialNumber());
            registeredProduct.setPurchaseDate(currentProduct.getPurchaseDate());
            registeredProduct.sendEmail(currentProduct.getEmail());
        }
        return registeredProduct;
    }

    @Override
    public boolean onBackPressed() {
        final FragmentActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            return clearFragmentStack();
        }
        return true;
    }

    private void showProgressAlertDialog(final String description) {
        final FragmentActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            // Create and show the dialog.
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.commit();
            DialogFragment newFragment = ProdRegLoadingFragment.newInstance(description);
            newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
            getActivity().getSupportFragmentManager().executePendingTransactions();
        }
    }

    @Override
    public DialogOkButtonListener getDialogOkButtonListener() {
        return new DialogOkButtonListener() {
            @Override
            public void onOkButtonPressed() {
                dismissAlertOnError();
            }
        };
    }
}
