package com.philips.cdp.di.iapdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.cdp.di.iap.integration.IAPDependencies;
import com.philips.cdp.di.iap.integration.IAPLaunchInput;
import com.philips.cdp.di.iap.integration.IAPLauncher;
import com.philips.cdp.di.iap.session.IAPHandlerListener;
import com.philips.cdp.di.iap.session.IAPHandlerProductListListener;
import com.philips.cdp.di.iap.utils.AppInfraHelper;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.localematch.PILLocaleManager;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.listener.UserRegistrationListener;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.RegistrationLaunchHelper;
import com.philips.cdp.uikit.UiKitActivity;
import com.philips.platform.uappframework.launcher.ActivityLauncher;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;

import java.util.ArrayList;
import java.util.List;


public class DemoAppActivity extends UiKitActivity implements View.OnClickListener,
        UserRegistrationListener, AdapterView.OnItemSelectedListener {

    private final int DEFAULT_THEME = R.style.Theme_Philips_DarkPink_WhiteBackground;

    //We require this to track for hiding the cart icon in demo app
    //private IAPSettings mIAPSettings;
    // private IAPHandler mIapHandler;
    private CountryPreferences mCountryPreference;

    private LinearLayout mSelectCountryLl, mAddCTNLl;
    private FrameLayout mShoppingCart;
    private Spinner mSpinner;
    private Button mShopNow;
    private Button mBuyDirect;
    private Button mPurchaseHistory;
    private Button mFragmentLaunch;
    private Button mLaunchProductDetail;
    private Button mShopNowCategorized;
    private EditText mEtCTN;
    private ArrayList<String> mProductList = new ArrayList<>();
    private ArrayList<String> mCategorizedList = new ArrayList<>();

    private int mSelectedCountryIndex;
    private ProgressDialog mProgressDialog = null;
    private TextView mTitleTextView;
    private TextView mCountText;

    private IAPLauncher mIapLauncher;
    private IAPLaunchInput mIapLaunchInput;
    private ArrayList<String> mCTNs;
    private IAPDependencies mIapDependencies;
    //private uAppListener uAppListener;
    private IAPHandlerProductListListener mGetCompleteProductListener = new IAPHandlerProductListListener() {

        @Override
        public void onFailure(int errorCode) {
            dismissProgressDialog();
            IAPLog.d(IAPLog.LOG, "Server error" + errorCode);
        }

        @Override
        public void onSuccess(ArrayList<String> productList) {
            dismissProgressDialog();
            mProductList = productList;
            IAPLog.d(IAPLog.LOG, "Product List =" + productList.toString());
        }
    };
    private IAPHandlerListener mProductCountListener = new IAPHandlerListener() {
        @Override
        public void onSuccess(final int count) {
            if (count > 0) {
                mCountText.setText(String.valueOf(count));
                mCountText.setVisibility(View.VISIBLE);
            } else {
                mCountText.setVisibility(View.GONE);
            }
            dismissProgressDialog();
        }

        @Override
        public void onFailure(final int errorCode) {
            dismissProgressDialog();
            showToast(errorCode);
        }
    };
    private IAPHandlerListener mBuyProductListener = new IAPHandlerListener() {
        @Override
        public void onSuccess(final int count) {
            dismissProgressDialog();
        }

        @Override
        public void onFailure(final int errorCode) {
            dismissProgressDialog();
            showToast(errorCode);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(DEFAULT_THEME);
        super.onCreate(savedInstanceState);
        //Set Action Bar to vertical
        addActionBar();
        setContentView(R.layout.demo_app_layout);
        showAppVersion();
        mCTNs = new ArrayList<>();
        mCTNs.add("HX8331/11");
        Button mRegister = (Button) findViewById(R.id.btn_register);
        mRegister.setOnClickListener(this);
        mFragmentLaunch = (Button) findViewById(R.id.btn_fragment_launch);
        mFragmentLaunch.setOnClickListener(this);
        mFragmentLaunch.setVisibility(View.GONE);

        mBuyDirect = (Button) findViewById(R.id.btn_buy_direct);
        mBuyDirect.setOnClickListener(this);

        mShopNow = (Button) findViewById(R.id.btn_shop_now);
        mShopNow.setOnClickListener(this);

        mPurchaseHistory = (Button) findViewById(R.id.btn_purchase_history);
        mPurchaseHistory.setOnClickListener(this);

        mLaunchProductDetail = (Button) findViewById(R.id.btn_launch_product_detail);
        mLaunchProductDetail.setOnClickListener(this);

        mShoppingCart = (FrameLayout) findViewById(R.id.shopping_cart_icon);
        mShoppingCart.setOnClickListener(this);

        Button mAddCtn = (Button) findViewById(R.id.btn_add_ctn);
        mAddCtn.setOnClickListener(this);

        mEtCTN = (EditText) findViewById(R.id.et_add_ctn);

        mShopNowCategorized = (Button) findViewById(R.id.btn_categorized_shop_now);
        mShopNowCategorized.setOnClickListener(this);

        mAddCTNLl = (LinearLayout) findViewById(R.id.ll_ctn);

//        mCountText = (TextView) findViewById(R.id.count_txt);

        RegistrationHelper.getInstance().registerUserRegistrationListener(this);

//        mIAPSettings = new IAPSettings("US", "en", DEFAULT_THEME);
//        mIapHandler = IAPHandler.init(this, mIAPSettings);

        mIapLauncher = new IAPLauncher();
        mIapLaunchInput = new IAPLaunchInput();
        mIapDependencies = new IAPDependencies("en", "US");
        mIapLaunchInput.setUseLocalData(false);
        mIapLauncher.init(this, mIapDependencies);

        mSelectCountryLl = (LinearLayout) findViewById(R.id.select_country);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setOnItemSelectedListener(this);

        List<String> countries = new ArrayList<>();
        countries.add("Select Country");
        countries.add("US");
        countries.add("UK");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(dataAdapter);

        mCountryPreference = new CountryPreferences(this);
        mSpinner.setSelection(mCountryPreference.getSelectedCountryIndex());

        /*Pls uncommnet when vertical wants to get complete product list from hybris*/

        mIapLaunchInput.setUseLocalData(false);
        if (!mIapLaunchInput.isUseLocalData()) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIapLauncher.getCompleteProductList(mGetCompleteProductListener);
                }
            }, 1000);
        }
    }

    private void addActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        IAPLog.d(IAPLog.BASE_FRAGMENT_ACTIVITY, "DemoAppActivity == onCreate");
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);

        View mCustomView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.action_bar, null); // layout which contains your button.

        FrameLayout frameLayout = (FrameLayout) mCustomView.findViewById(R.id.iap_header_back_button);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onBackPressed();
            }
        });
        ImageView arrowImage = (ImageView) mCustomView.findViewById(R.id.iap_iv_header_back_button);
        //noinspection deprecation
        arrowImage.setBackground(getResources().getDrawable(R.drawable.back_arrow));

        mTitleTextView = (TextView) mCustomView.findViewById(R.id.iap_header_title);
        setTitle(getString(R.string.app_name));

        mCountText = (TextView) mCustomView.findViewById(R.id.item_count);

        mActionBar.setCustomView(mCustomView, params);
        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mTitleTextView.setText(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /** Should be commented for debug builds */
        final String HOCKEY_APP_ID = "dc402a11ae984bd18f99c07d9b4fe6a4";
        CrashManager.register(this, HOCKEY_APP_ID, new CrashManagerListener() {
            public boolean shouldAutoUploadCrashes() {
                return !IAPLog.isLoggingEnabled();
            }
        });

        init();
    }

    private void init() {
        User mUser = new User(this);
        if (mUser.isUserSignIn()) {
            displayViews();
            if (mSelectedCountryIndex > 0) {
                showProgressDialog();
                mIapLauncher.getProductCartCount(mProductCountListener);
            }
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.shopping_cart_icon:
                if (isNetworkAvailable(DemoAppActivity.this)) {
                    AppInfraHelper.getInstance().getIapTaggingInterface().setPreviousPage("demoapp:home");
                    // mIapHandler.launchIAP(IAPConstant.IAPLandingViews.IAP_SHOPPING_CART_VIEW, null, mBuyProductListener);
                    mIapLaunchInput.setIAPFlow(IAPLaunchInput.IAPFlows.IAP_SHOPPING_CART_VIEW, null);
                    mIapLauncher.launch(new ActivityLauncher(ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_PORTRAIT, DEFAULT_THEME), mIapLaunchInput, mBuyProductListener);

                } else {
                    Toast.makeText(DemoAppActivity.this, "Network unavailable", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_register:
                IAPLog.d(IAPLog.DEMOAPPACTIVITY, "DemoActivity : Registration");
                RegistrationHelper.getInstance().getAppTaggingInterface().setPreviousPage("demoapp:home");
                RegistrationLaunchHelper.launchDefaultRegistrationActivity(this);
                break;
            case R.id.btn_shop_now:
                if (isNetworkAvailable(DemoAppActivity.this)) {
                    AppInfraHelper.getInstance().getIapTaggingInterface().setPreviousPage("demoapp:home");
                    // mIapHandler.launchIAP(IAPConstant.IAPLandingViews.IAP_PRODUCT_CATALOG_VIEW, null, null);
                    mIapLaunchInput.setIAPFlow(IAPLaunchInput.IAPFlows.IAP_PRODUCT_CATALOG_VIEW, null);
                    mIapLauncher.launch(new ActivityLauncher(ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_PORTRAIT, DEFAULT_THEME), mIapLaunchInput, null);
                } else {
                    Toast.makeText(DemoAppActivity.this, "Network unavailable", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_purchase_history:
                if (isNetworkAvailable(DemoAppActivity.this)) {
                    AppInfraHelper.getInstance().getIapTaggingInterface().setPreviousPage("demoapp:home");
                    //  mIapHandler.launchIAP(IAPConstant.IAPLandingViews.IAP_PURCHASE_HISTORY_VIEW, null, null);
                    mIapLaunchInput.setIAPFlow(IAPLaunchInput.IAPFlows.IAP_PURCHASE_HISTORY_VIEW, null);
                    mIapLauncher.launch(new ActivityLauncher(ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_PORTRAIT, DEFAULT_THEME), mIapLaunchInput, null);
                } else {
                    Toast.makeText(DemoAppActivity.this, "Network unavailable", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_fragment_launch:
                // mIAPSettings.setUseLocalData(false);
                Intent intent = new Intent(this, LauncherFragmentActivity.class);
                this.startActivity(intent);
                break;
            case R.id.btn_launch_product_detail:
                if (isNetworkAvailable(DemoAppActivity.this)) {
                    try {
                        if (!mProductList.isEmpty()) {
                            String ctn = mProductList.get(0);
                            IAPLog.d(IAPLog.LOG, "Product CTN : " + ctn);
                            AppInfraHelper.getInstance().getIapTaggingInterface().setPreviousPage("demoapp:home");
                            // mIapHandler.launchIAP(IAPConstant.IAPLandingViews.IAP_PRODUCT_DETAIL_VIEW, ctn, null);
                            mIapLaunchInput.setIAPFlow(IAPLaunchInput.IAPFlows.IAP_SHOPPING_CART_VIEW, mCTNs);
                            mIapLauncher.launch(new ActivityLauncher(ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_PORTRAIT, DEFAULT_THEME), mIapLaunchInput, null);
                        } else {
                            Toast.makeText(DemoAppActivity.this, "Please add CTN", Toast.LENGTH_SHORT).show();
                        }
                    } catch (RuntimeException e) {
                        Toast.makeText(DemoAppActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DemoAppActivity.this, "Network unavailable", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_categorized_shop_now:
                if (isNetworkAvailable(DemoAppActivity.this)) {
                    if (mCategorizedList != null && !mCategorizedList.isEmpty()) {
                        IAPLog.d(IAPLog.LOG, "Product List : " + mCategorizedList);
                        AppInfraHelper.getInstance().getIapTaggingInterface().setPreviousPage("demoapp:home");
                        mIapLaunchInput.setIAPFlow(IAPLaunchInput.IAPFlows.IAP_PRODUCT_CATALOG_VIEW, mCTNs);
                        mIapLauncher.launch(new ActivityLauncher(ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_PORTRAIT, DEFAULT_THEME), mIapLaunchInput, null);
                        // mIapHandler.launchCategorizedCatalog(mCategorizedList);
                    } else {
                        Toast.makeText(DemoAppActivity.this, "Please add CTN", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DemoAppActivity.this, "Network unavailable", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_add_ctn:
                String str = mEtCTN.getText().toString().toUpperCase().replaceAll("\\s+", "");
                if (!mCategorizedList.contains(str)) {
                    mCategorizedList.add(str);
                    Toast.makeText(DemoAppActivity.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DemoAppActivity.this, "Product is duplicate", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_buy_direct:
                if (isNetworkAvailable(DemoAppActivity.this)) {
                    try {
                        String ctn = mEtCTN.getText().toString().toUpperCase().replaceAll("\\s+", "");
                        // mIapHandler.buyDirect(ctn);
                        mIapLaunchInput.setIAPFlow(IAPLaunchInput.IAPFlows.IAP_BUY_DIRECT_VIEW, mCTNs);
                        mIapLauncher.launch(new ActivityLauncher(ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_PORTRAIT, DEFAULT_THEME), mIapLaunchInput, null);
                    } catch (RuntimeException e) {
                        Toast.makeText(DemoAppActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DemoAppActivity.this, "Network unavailable", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onUserRegistrationComplete(Activity activity) {
        displayViews();
        activity.finish();
    }

    @Override
    public void onPrivacyPolicyClick(Activity activity) {
    }

    @Override
    public void onTermsAndConditionClick(Activity activity) {
    }

    @Override
    public void onUserLogoutSuccess() {
        hideViews();
    }

    @Override
    public void onUserLogoutFailure() {
    }

    @Override
    public void onUserLogoutSuccessWithInvalidAccessToken() {
    }

    private void showToast(int errorCode) {
        String errorText = "Server error";
        if (IAPConstant.IAP_ERROR_NO_CONNECTION == errorCode) {
            errorText = "No connection";
        } else if (IAPConstant.IAP_ERROR_CONNECTION_TIME_OUT == errorCode) {
            errorText = "Connection time out";
        } else if (IAPConstant.IAP_ERROR_AUTHENTICATION_FAILURE == errorCode) {
            errorText = "Authentication failure";
        } else if (IAPConstant.IAP_ERROR_INSUFFICIENT_STOCK_ERROR == errorCode) {
            errorText = "Product out of stock";
        }
        Toast toast = Toast.makeText(this, errorText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedCountryIndex = position;
        mCountryPreference.saveCountryPrefrence(position);

        if (position == 0) {
            mShoppingCart.setVisibility(View.INVISIBLE);
            mShopNow.setVisibility(View.GONE);
            mBuyDirect.setVisibility(View.GONE);
            mPurchaseHistory.setVisibility(View.GONE);
            mFragmentLaunch.setVisibility(View.GONE);
            mShopNowCategorized.setVisibility(View.GONE);
            mLaunchProductDetail.setVisibility(View.GONE);
            return;
        }
        mPurchaseHistory.setVisibility(View.VISIBLE);
        mLaunchProductDetail.setVisibility(View.VISIBLE);
        mFragmentLaunch.setVisibility(View.VISIBLE);
        mShoppingCart.setVisibility(View.VISIBLE);
        mShopNow.setVisibility(View.VISIBLE);
        mBuyDirect.setVisibility(View.VISIBLE);
        mShopNowCategorized.setVisibility(View.VISIBLE);
        mShopNow.setEnabled(true);
        mBuyDirect.setEnabled(true);
        mLaunchProductDetail.setEnabled(true);
        mPurchaseHistory.setEnabled(true);

        String mSelectedCountry = parent.getItemAtPosition(position).toString();
        if (mSelectedCountry.equals("UK"))
            mSelectedCountry = "GB";
        setLocale("en", mSelectedCountry);

        //mIAPSettings = new IAPSettings(mSelectedCountry, "en", DEFAULT_THEME);
        // setUseLocalData(); //Uncomment to support PlanB

        // mIapHandler = IAPHandler.init(this, mIAPSettings);
        mIapLaunchInput.setUseLocalData(false);
        mIapLauncher.init(this, mIapDependencies);
        updateCartIcon();
        if (!mIapLaunchInput.isUseLocalData()) {
            showProgressDialog();
            mIapLauncher.getProductCartCount(mProductCountListener);
            mPurchaseHistory.setVisibility(View.VISIBLE);
        } else
            mPurchaseHistory.setVisibility(View.GONE);
    }

    private void setLocale(String languageCode, String countryCode) {
        PILLocaleManager localeManager = new PILLocaleManager(DemoAppActivity.this);
        localeManager.setInputLocale(languageCode, countryCode);
    }

    private void updateCartIcon() {
        //if (shouldUseLocalData()) {
        if (mIapLaunchInput.isUseLocalData()) {
            mShoppingCart.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

//    public void setUseLocalData() {
//        mIAPSettings.setUseLocalData(true);
//    }
//
//    public boolean shouldUseLocalData() {
//        return mIAPSettings.isUseLocalData();
//    }

    private void displayViews() {
        mAddCTNLl.setVisibility(View.VISIBLE);
        mSelectCountryLl.setVisibility(View.VISIBLE);
    }

    private void hideViews() {
        mCountText.setVisibility(View.GONE);
        mFragmentLaunch.setVisibility(View.GONE);
        mShoppingCart.setVisibility(View.INVISIBLE);
        mSelectCountryLl.setVisibility(View.GONE);
        mAddCTNLl.setVisibility(View.GONE);
        mShopNow.setVisibility(View.GONE);
        mBuyDirect.setVisibility(View.GONE);
        mLaunchProductDetail.setVisibility(View.GONE);
        mPurchaseHistory.setVisibility(View.GONE);
        mShopNowCategorized.setVisibility(View.GONE);
        mSelectedCountryIndex = 0;
        mCountryPreference.clearCountryPreference();
        mSpinner.setSelection(0);
    }

    private void showAppVersion() {
        String code = "";
        try {
            code = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView versionView = (TextView) findViewById(R.id.appversion);
        versionView.setText(String.valueOf(code));
    }

    public boolean isNetworkAvailable(Context pContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.iap_please_wait) + "...");
        }
        if ((!mProgressDialog.isShowing()) && !isFinishing()) {
            mProgressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing() && !isFinishing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }


}