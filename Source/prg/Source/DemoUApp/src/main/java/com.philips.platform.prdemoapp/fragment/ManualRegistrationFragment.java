package com.philips.platform.prdemoapp.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.constants.ProdRegError;
import com.philips.cdp.prodreg.launcher.PRDependencies;
import com.philips.cdp.prodreg.launcher.PRInterface;
import com.philips.cdp.prodreg.launcher.PRLaunchInput;
import com.philips.cdp.prodreg.launcher.PRSettings;
import com.philips.cdp.prodreg.launcher.PRUiHelper;
import com.philips.cdp.prodreg.listener.ProdRegUiListener;
import com.philips.cdp.prodreg.logging.ProdRegLogger;
import com.philips.cdp.prodreg.register.Product;
import com.philips.cdp.prodreg.register.RegisteredProduct;
import com.philips.cdp.prodreg.register.UserWithProducts;
import com.philips.cdp.prodreg.util.ProdRegUtil;
import com.philips.cdp.prxclient.PrxConstants;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.prdemoapp.PRDemoAppuAppDependencies;
import com.philips.platform.prdemoapp.PRDemoAppuAppSettings;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;
import com.philips.platform.prdemoapp.activity.MainActivity;
import com.philips.platform.prdemoapp.theme.fragments.BaseFragment;
import com.philips.platform.prdemoapplibrary.R;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class ManualRegistrationFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = ManualRegistrationFragment.class.getName();
    private ToggleButton toggleButton;
    private ToggleButton mandatoryRegToggleBtn;
    private EditText mFriendlyName, mSerialNumber, mPurchaseDate, mCtn, mandatoryEditText;
    private Calendar mCalendar;
    private Button pr_activity_a, pr_activity_b, pr_fragment_a, pr_fragment_b;
    private boolean eMailConfiguration = false;
    private boolean mandatoryConfiguration = false;
    private FragmentActivity fragmentActivity;
    private LinearLayout mandatoryTextViewLayout;
    private final String userRegServiceID = "userreg.janrain.api.v2";
    private PRLaunchInput prLaunchInput;

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            Log.d(TAG, "Response Data : " + arg1 + "-" + arg2 + "-" + arg3);
            final int mMonthInt = (arg2 + 1);
            String mMonth;
            if (mMonthInt < 10) {
                mMonth = getResources().getString(R.string.zero) + mMonthInt;
            } else {
                mMonth = Integer.toString(mMonthInt);
            }
            String mDate;
            if (arg3 < 10) {
                mDate = getResources().getString(R.string.zero) + arg3;
            } else {
                mDate = Integer.toString(arg3);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat(ProdRegConstants.PROD_REG_DATE_FORMAT_SERVER);
            mCalendar = Calendar.getInstance();
            final String mGetDeviceDate = dateFormat.format(mCalendar.getTime());
            Date mDisplayDate;
            Date mDeviceDate;
            try {
                final String text =   arg1 + "-" + mMonth + "-" +mDate;
                mDisplayDate = dateFormat.parse(text);
                mDeviceDate = dateFormat.parse(mGetDeviceDate);
                if (mDisplayDate.after(mDeviceDate)) {
                    Log.d(TAG, " Response Data : " + "Error in Date");
                } else {

                    mPurchaseDate.setText(text);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_activity_main, container, false);
        init(view);
        return view;
    }

    private void init(final View view) {
        fragmentActivity = getActivity();
        mSerialNumber = (EditText) view.findViewById(R.id.edt_serial_number);
        mPurchaseDate = (EditText) view.findViewById(R.id.edt_purchase_date);
        mFriendlyName = (EditText) view.findViewById(R.id.friendly_name_edittext);
        mCtn = (EditText) view.findViewById(R.id.edt_ctn);
        pr_activity_a = (Button) view.findViewById(R.id.pr_activity_a);
        pr_activity_b = (Button) view.findViewById(R.id.pr_activity_b);
        pr_fragment_a = (Button) view.findViewById(R.id.pr_fragment_a);
        pr_fragment_b = (Button) view.findViewById(R.id.pr_fragment_b);
        toggleButton = (ToggleButton) view.findViewById(R.id.toggbutton);
        mandatoryRegToggleBtn = (ToggleButton) view.findViewById(R.id.mandatoryTogglebutton);
        mandatoryTextViewLayout = (LinearLayout) view.findViewById(R.id.mandatoryTextViewLayout);
        mandatoryEditText = (EditText) view.findViewById(R.id.mandatoryEditText);
        mandatoryEditText.setText("default text");

        setOnClickListeners();
        toggleButton.setChecked(eMailConfiguration);
        mandatoryRegToggleBtn.setChecked(mandatoryConfiguration);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCtn.setText(bundle.getString("ctn") != null ? bundle.getString("ctn") : "");
            mSerialNumber.setText(bundle.getString("serial") != null ? bundle.getString("serial") : "");
            mPurchaseDate.setText(bundle.getString("date") != null ? bundle.getString("date") : "");
        }
    }

    private void setOnClickListeners() {
        pr_activity_a.setOnClickListener(this);
        pr_activity_b.setOnClickListener(this);
        pr_fragment_a.setOnClickListener(this);
        pr_fragment_b.setOnClickListener(this);
        mPurchaseDate.setOnClickListener(this);
        toggleButton.setOnClickListener(this);
        mandatoryRegToggleBtn.setOnClickListener(this);
    }
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//                    Log.e("gif--","fragment back key is clicked");
//                    getActivity().getSupportFragmentManager().popBackStack("ManualRegistrationFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    return true;
//                }
//                return false;
//            }
//        });
//    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.pr_activity_a) {
            makeRegistrationRequest(true, "app_flow");

        } else if (i == R.id.pr_activity_b) {
            makeRegistrationRequest(true, "user_flow");

        } else if (i == R.id.pr_fragment_a) {
            makeRegistrationRequest(false, "app_flow");

        } else if (i == R.id.pr_fragment_b) {
            makeRegistrationRequest(false, "user_flow");

        } else if (i == R.id.toggbutton) {
            eMailConfiguration = toggleButton.isChecked();

        } else if(i == R.id.mandatoryTogglebutton){
            mandatoryConfiguration = mandatoryRegToggleBtn.isChecked();
            if(mandatoryConfiguration) {
                mandatoryTextViewLayout.setVisibility(View.VISIBLE);

            }else{
                mandatoryTextViewLayout.setVisibility(View.GONE);
            }
        }else if (i == R.id.edt_purchase_date) {
            onClickPurchaseDate();

        } else {
        }
    }

    private void makeRegistrationRequest(final boolean isActivity, final String type) {
        registerProduct(isActivity, type);
    }

    public void onClickPurchaseDate() {
        int mYear;
        int mMonthInt;
        int mDay;
        if (!mPurchaseDate.getText().toString().equalsIgnoreCase("")) {
            final String[] mEditDisplayDate = mPurchaseDate.getText().toString().split("-");
            mYear = Integer.parseInt(mEditDisplayDate[0]);
            mMonthInt = Integer.parseInt(mEditDisplayDate[1]) - 1;
            mDay = Integer.parseInt(mEditDisplayDate[2]);
        } else {
            mCalendar = Calendar.getInstance();
            mYear = mCalendar.get(Calendar.YEAR);
            mMonthInt = mCalendar.get(Calendar.MONTH);
            mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(fragmentActivity, myDateListener, mYear, mMonthInt, mDay);
        final ProdRegUtil prodRegUtil = new ProdRegUtil();
        datePickerDialog.getDatePicker().setMinDate(prodRegUtil.getMinDate());
        datePickerDialog.getDatePicker().setMaxDate(prodRegUtil.getMaxDate());
        datePickerDialog.show();
    }

    private void registerProduct(final boolean isActivity, final String type) {
        Product product = new Product(mCtn.getText().toString(), PrxConstants.Sector.B2C, PrxConstants.Catalog.CONSUMER);
        product.setSerialNumber(mSerialNumber.getText().toString());
        product.setPurchaseDate(mPurchaseDate.getText().toString());
        product.setFriendlyName(mFriendlyName.getText().toString());
        product.sendEmail(eMailConfiguration);
        initServiceDiscoveryLocale();
        invokeProdRegFragment(product, isActivity, type);
    }

    private void initServiceDiscoveryLocale() {
        AppInfraInterface appInfra = PRUiHelper.getInstance().getAppInfraInstance();
        final ServiceDiscoveryInterface serviceDiscoveryInterface = appInfra.getServiceDiscovery();

        //serviceDiscoveryInterface.getServiceLocaleWithCountryPreference();
        ArrayList<String> serviceIDList = new ArrayList<>();
        serviceIDList.add(userRegServiceID);
        serviceDiscoveryInterface.getServicesWithLanguagePreference(serviceIDList, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                String locale = urlMap.get(userRegServiceID).getLocale();
                if(null == locale){
                    Toast.makeText(fragmentActivity, "Not able to set country code since locale is null", Toast.LENGTH_SHORT).show();
                }else {
                    PRUiHelper.getInstance().setLocale(locale);
                    String localeArr[] = locale.split("_");
                    PRUiHelper.getInstance().setCountryCode(localeArr[1].trim().toUpperCase());
                }
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                Toast.makeText(fragmentActivity, message, Toast.LENGTH_SHORT).show();
            }
        },null);

    }

    private void invokeProdRegFragment(Product product, final boolean isActivity, final String type) {
        ArrayList<Product> products = new ArrayList<>();
        products.add(product);

        PRDemoAppuAppDependencies appuAppDependencies = new PRDemoAppuAppDependencies(PRUiHelper.getInstance().getAppInfraInstance());
        PRDemoAppuAppSettings appuAppSettings = new PRDemoAppuAppSettings(getContext());
        URInterface urInterface = new URInterface();
        urInterface.init(appuAppDependencies,appuAppSettings);

        PRInterface prInterface = new PRInterface();
        PRSettings prSettings = new PRSettings(getContext());
        PRDependencies prDependencies = new PRDependencies(PRUiHelper.getInstance().getAppInfraInstance(),urInterface.getUserDataInterface());
        try {
            prInterface.init(prDependencies, prSettings);
        }catch (RuntimeException ex){
            ProdRegLogger.d(TAG,ex.getMessage());
        }

        if (!isActivity) {
           FragmentLauncher fragLauncher = new FragmentLauncher(
                    fragmentActivity, R.id.mainContainer, new ActionBarListener() {
                @Override
                public void updateActionBar(@StringRes final int i, final boolean b) {
                    MainActivity mainActivity = (MainActivity) fragmentActivity;
                    mainActivity.setTitle(i);
                }

                @Override
                public void updateActionBar(final String s, final boolean b) {

                }
            });
            fragLauncher.setCustomAnimation(0, 0);
            if (type.equalsIgnoreCase("app_flow")) {
                prLaunchInput = new PRLaunchInput(products, true);
            } else {
                prLaunchInput = new PRLaunchInput(products, false);
            }
            prLaunchInput.setProdRegUiListener(getProdRegUiListener());

            prLaunchInput.setBackgroundImageResourceId(R.drawable.pr_config1);

            prLaunchInput.setMandatoryProductRegistration(!mandatoryConfiguration);
            prLaunchInput.setMandatoryRegisterButtonText(mandatoryEditText.getText().toString());

            prInterface.launch(fragLauncher, prLaunchInput);
        } else {
            ActivityLauncher activityLauncher = new ActivityLauncher(getActivity(), ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED,  ((MainActivity) getActivity()).getThemeConfig(), ((MainActivity) getActivity()).getThemeResourceId(), null);

            if (type.equalsIgnoreCase("app_flow")) {
                prLaunchInput = new PRLaunchInput(products, true);
            } else {
                prLaunchInput = new PRLaunchInput(products, false);
            }
            prLaunchInput.setProdRegUiListener(getProdRegUiListener());
            prLaunchInput.setBackgroundImageResourceId(R.drawable.pr_config1);
            prLaunchInput.setMandatoryProductRegistration(!mandatoryConfiguration);
            prLaunchInput.setMandatoryRegisterButtonText(mandatoryEditText.getText().toString());
            prInterface.launch(activityLauncher, prLaunchInput);
        }
    }

    @NonNull
    private ProdRegUiListener getProdRegUiListener() {
        return new ProdRegUiListener() {
            @Override
            public void onProdRegContinue(final List<RegisteredProduct> registeredProduct, final UserWithProducts userWithProduct) {
                ProdRegLogger.v(TAG, registeredProduct.get(0).getRegistrationState() + "");
            }

            @Override
            public void onProdRegBack(final List<RegisteredProduct> registeredProduct, final UserWithProducts userWithProduct) {
                ProdRegLogger.v(TAG, registeredProduct.get(0).getProdRegError() + "");
            }

            @Override
            public void onProdRegFailed(final ProdRegError prodRegError) {
                ProdRegLogger.v(TAG, prodRegError.getDescription() + "");
                if (prodRegError == ProdRegError.USER_NOT_SIGNED_IN) {
                    if(getActivity() != null)
                        Toast.makeText(getActivity(), prodRegError.getDescription(), Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.prg_app_name));
    }

    @Override
    public void onStop() {
        super.onStop();
        hideKeyboard();
    }

    protected void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public int getPageTitle() {
        return 0;
    }

}
