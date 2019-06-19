
package com.iap.demouapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.integration.IAPBannerEnabler;
import com.philips.cdp.di.iap.integration.IAPDependencies;
import com.philips.cdp.di.iap.integration.IAPFlowInput;
import com.philips.cdp.di.iap.integration.IAPInterface;
import com.philips.cdp.di.iap.integration.IAPLaunchInput;
import com.philips.cdp.di.iap.integration.IAPListener;
import com.philips.cdp.di.iap.integration.IAPMockInterface;
import com.philips.cdp.di.iap.integration.IAPOrderFlowCompletion;
import com.philips.cdp.di.iap.integration.IAPSettings;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.IAPUtility;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.ui.utils.RegistrationContentConfiguration;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URLaunchInput;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pif.DataInterface.USR.listeners.LogoutSessionListener;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappSettings;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.ThemeUtils;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.view.widget.Button;
import com.philips.platform.uid.view.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.philips.cdp.di.iap.utils.Utility.hideKeypad;


public class DemoAppActivity extends AppCompatActivity implements View.OnClickListener, IAPListener,
        UserRegistrationUIEventListener, IAPMockInterface, IAPOrderFlowCompletion, IAPBannerEnabler {

    private final String TAG = DemoAppActivity.class.getSimpleName();
    private final int DEFAULT_THEME = R.style.Theme_DLS_Blue_UltraLight;
    private LinearLayout mAddCTNLl, mLL_voucher;
    private FrameLayout mShoppingCart;
    private EditText mEtCTN, mEtVoucherCode, mEtPropositionId;

    private Button mRegister;
    private Button mShopNow;
    private Button mShopNowCategorized;
    private Button mBuyDirect;
    private Button mPurchaseHistory;
    private Button mLaunchProductDetail;
    private Button mAddCtn, mBtn_add_voucher, mBtnSetPropositionId;
    private Button mShopNowCategorizedWithRetailer;
    private ProgressDialog mProgressDialog = null;
    private ArrayList<String> mCategorizedProductList;
    private TextView mTitleTextView;
    private TextView mCountText;

    private IAPInterface mIapInterface;
    private IAPLaunchInput mIapLaunchInput;
    private IAPSettings mIAPSettings;
    private UserDataInterface mUserDataInterface;
    ImageView mCartIcon;
    Boolean isCartVisible;
    String voucherCode;

    private ArrayList<String> ignorelistedRetailer;
    private View mLL_propositionId;
    URInterface urInterface;
    private long mLastClickTime = 0;
    private ToggleButton toggleMock;
    private boolean enableMock = false;
    EditText mEtMaxCartCount;
    private ToggleButton toggleHybris;
    private boolean isHybrisEnable = true;
    private ToggleButton toggleBanner;
    private boolean isBannerEnabled = false;
    private ToggleButton toggleListener;
    private boolean isToggleListener = false;
    private RadioGroup rgVoucher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initTheme();
        super.onCreate(savedInstanceState);

        urInterface = new URInterface();
        urInterface.init(new IapDemoUAppDependencies(new AppInfra.Builder().build(getApplicationContext())), new IapDemoAppSettings(getApplicationContext()));

        ignorelistedRetailer = new ArrayList<>();
        IAPLog.enableLogging(true);
        setContentView(R.layout.demo_app_layout);

        showAppVersion();
        mEtCTN = findViewById(R.id.et_add_ctn);
        mEtVoucherCode = findViewById(R.id.et_add_voucher);
        mAddCTNLl = findViewById(R.id.ll_ctn);


        mEtPropositionId = findViewById(R.id.et_add_proposition_id);
        mBtnSetPropositionId = findViewById(R.id.btn_set_proposition_id);


        AppInfraInterface appInfra = new AppInfra.Builder().build(getApplicationContext());
        AppConfigurationInterface configInterface = appInfra.getConfigInterface();
        AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface.AppConfigurationError();

        String propertyForKey = (String) configInterface.getPropertyForKey("propositionid", "IAP", configError);
        mEtPropositionId.setText(propertyForKey);

        mBtnSetPropositionId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                configInterface.setPropertyForKey("propositionid", "IAP", mEtPropositionId.getText().toString(), configError);

                Toast.makeText(DemoAppActivity.this, "Proposition id is set", Toast.LENGTH_SHORT).show();
                finishAffinity();
                System.exit(0);
            }
        });

        toggleMock = findViewById(R.id.toggleMock);

        toggleMock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                enableMock = isChecked;
                mIAPSettings.setIapMockInterface((IAPMockInterface) DemoAppActivity.this);
                initializeIAPComponant();
            }
        });

        toggleBanner = findViewById(R.id.toggleBanner);

        toggleBanner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                isBannerEnabled = isChecked;
                initializeIAPComponant();
            }
        });

        toggleHybris = findViewById(R.id.toggleHybris);
        toggleHybris.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isHybrisEnable = isChecked;
                initializeIAPComponant();
            }
        });

        rgVoucher = findViewById(R.id.rg_voucher);

        rgVoucher.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rb_null) {
                    IAPUtility.getInstance().setVoucherEnable(false);
                }
                if (checkedId == R.id.rb_disable) {
                    IAPUtility.getInstance().setVoucherEnable(false);
                }
                if (checkedId == R.id.rb_enabble) {
                    IAPUtility.getInstance().setVoucherEnable(true);
                }
            }
        });


        toggleListener = findViewById(R.id.toggleListener);

        toggleListener.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isToggleListener = isChecked;
                initializeIAPComponant();
            }
        });

        Button btnSetMaxCount = findViewById(R.id.btn_set_max_Count);

        btnSetMaxCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeIAPComponant();
            }
        });

        mEtMaxCartCount = findViewById(R.id.et_max_cart_count);

        mRegister = findViewById(R.id.btn_register);
        mRegister.setOnClickListener(this);

        mBuyDirect = findViewById(R.id.btn_buy_direct);
        mBuyDirect.setOnClickListener(this);

        mShopNow = findViewById(R.id.btn_shop_now);
        mShopNow.setOnClickListener(this);

        mPurchaseHistory = findViewById(R.id.btn_purchase_history);
        mPurchaseHistory.setOnClickListener(this);

        mLaunchProductDetail = findViewById(R.id.btn_launch_product_detail);
        mLaunchProductDetail.setOnClickListener(this);

        mShoppingCart = findViewById(R.id.shopping_cart_icon);


        mShopNowCategorized = findViewById(R.id.btn_categorized_shop_now);
        mShopNowCategorized.setOnClickListener(this);


        mLL_voucher = findViewById(R.id.ll_voucher);
        mLL_propositionId = findViewById(R.id.ll_enter_proposition_id);

        mAddCtn = findViewById(R.id.btn_add_ctn);
        mAddCtn.setOnClickListener(this);

        mBtn_add_voucher = findViewById(R.id.btn_add_voucher);
        mBtn_add_voucher.setOnClickListener(this);

        mShopNowCategorizedWithRetailer = findViewById(R.id.btn_categorized_shop_now_with_ignore_retailer);
        mShopNowCategorizedWithRetailer.setOnClickListener(this);

        mCartIcon = findViewById(R.id.cart_iv);
        mCountText = findViewById(R.id.item_count);

        mCategorizedProductList = new ArrayList<>();



      //  ["HD9745/90","HD9630/90","HD9240/90","HD9621/90","HD9651/90","HD9650/90R1","HD9652/90","HD9910/20","HD9654/90",
        //  "HD9216/80","HD9630/20","HD9220/20","HD9621/80","HD9750/90","HD9750/20","HD9762/90","HD9216/80R1","HD9621/70","HD9741/10"]

       /* mCategorizedProductList.add("HD9745/90000");
        mCategorizedProductList.add("HD9630/90");
        mCategorizedProductList.add("HD9240/90");
        mCategorizedProductList.add("HD9621/90");
        mCategorizedProductList.add("HD9651/90");
        mCategorizedProductList.add("HD9650/90");
        mCategorizedProductList.add("HD9910/20");
        mCategorizedProductList.add("HD9654/90");
        mCategorizedProductList.add("HD9216/80");
        mCategorizedProductList.add("HD9630/20");
        mCategorizedProductList.add("HD9220/20");
        mCategorizedProductList.add("HD9621/80");
        mCategorizedProductList.add("HD9750/20");
        mCategorizedProductList.add("HD9216/80R1");
        mCategorizedProductList.add("HD9621/70");
        mCategorizedProductList.add("HD9741/10");*/

        mUserDataInterface = urInterface.getUserDataInterface();


        //Integration interface
        mIapInterface = new IAPInterface();
        mIAPSettings = new IAPSettings(this);
        mIAPSettings.setIapMockInterface(this);
        actionBar();
        initializeIAPComponant();
    }

    private void initializeIAPComponant() {
        toggleHybris.setVisibility(View.VISIBLE);
        initIAP();

        if (mUserDataInterface != null && mUserDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
            mRegister.setText(this.getString(R.string.log_out));
        } else {
            mRegister.setVisibility(View.VISIBLE);
           // Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void initIAP() {
        ignorelistedRetailer.add("Frys.com");
        ignorelistedRetailer.add("Amazon - US");
        ignorelistedRetailer.add("BestBuy.com");

        UappDependencies uappDependencies = new UappDependencies(new AppInfra.Builder().build(this));
        UappSettings uappSettings = new UappSettings(getApplicationContext());

        urInterface.init(uappDependencies, uappSettings);

        IAPDependencies mIapDependencies = new IAPDependencies(new AppInfra.Builder().build(this), urInterface.getUserDataInterface());

        try {
            mIapInterface.init(mIapDependencies, mIAPSettings);
        } catch (RuntimeException ex) {
            IAPLog.d(TAG, ex.getMessage());
        }
        mIapLaunchInput = new IAPLaunchInput();

        mIapLaunchInput.setHybrisSupported(isHybrisEnable);
        if (!TextUtils.isEmpty(mEtMaxCartCount.getText().toString().trim())) {
            mIapLaunchInput.setMaxCartCount(Integer.parseInt(mEtMaxCartCount.getText().toString().trim()));
        }
        mIapLaunchInput.setIapBannerEnabler(this);
        mIapLaunchInput.setIapListener(this);
        if (isToggleListener) {
            mIapLaunchInput.setIapOrderFlowCompletion(this);
        } else {
            mIapLaunchInput.setIapOrderFlowCompletion(null);
        }
        IAPUtility.getInstance().setHybrisSupported(isHybrisEnable);
        displayUIOnCartVisible();
    }

    private void displayUIOnCartVisible() {
        if(isUserLoggedIn()) {
            showProgressDialog();
            mIapInterface.isCartVisible(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //This is added to clear pagination data from app memory . This should be taken in tech debt .
        CartModelContainer.getInstance().clearProductList();
        IAPUtility.getInstance().resetPegination();


        if(isUserLoggedIn()) {
            try {
                mIapInterface.getProductCartCount(this);
            }catch (Exception e){

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mShoppingCart.setOnClickListener(this);
    }

    private void onResumeRetailer() {
        mAddCTNLl.setVisibility(View.VISIBLE);
        mLL_voucher.setVisibility(View.VISIBLE);
        mLL_propositionId.setVisibility(View.VISIBLE);
        mShopNowCategorizedWithRetailer.setVisibility(View.VISIBLE);
        mShopNowCategorizedWithRetailer.setText(String.format(getString(R.string.categorized_shop_now_ignore_retailer), ignorelistedRetailer.get(0)));
        mShopNowCategorized.setVisibility(View.VISIBLE);
        mLaunchProductDetail.setVisibility(View.VISIBLE);
        mLaunchProductDetail.setEnabled(true);
        mCartIcon.setVisibility(View.GONE);
        mCountText.setVisibility(View.GONE);
        mShopNow.setVisibility(View.GONE);
        mPurchaseHistory.setVisibility(View.GONE);
        mShoppingCart.setVisibility(View.GONE);
    }

    private void displayFlowViews(boolean b) {

        mAddCTNLl.setVisibility(View.VISIBLE);
        mLL_voucher.setVisibility(View.VISIBLE);
        mLL_propositionId.setVisibility(View.VISIBLE);
        mShopNowCategorizedWithRetailer.setVisibility(View.VISIBLE);
        mShopNowCategorizedWithRetailer.setText(String.format(getString(R.string.categorized_shop_now_ignore_retailer), ignorelistedRetailer.get(0)));
        mShopNowCategorized.setVisibility(View.VISIBLE);
        mLaunchProductDetail.setVisibility(View.VISIBLE);
        mLaunchProductDetail.setEnabled(true);


        mCartIcon.setVisibility(View.VISIBLE);
        mCountText.setVisibility(View.VISIBLE);
        mShopNow.setVisibility(View.VISIBLE);
        mShopNow.setEnabled(true);
        mPurchaseHistory.setVisibility(View.VISIBLE);
        mPurchaseHistory.setEnabled(true);
        mShoppingCart.setVisibility(View.VISIBLE);

        dismissProgressDialog();
        mIapInterface.getProductCartCount(this);

        if (b) {
            mCartIcon.setVisibility(View.VISIBLE);
            mCountText.setVisibility(View.VISIBLE);
            mShoppingCart.setVisibility(View.VISIBLE);
            mIapInterface.getProductCartCount(this);
        } else {
            mCartIcon.setVisibility(View.GONE);
            mCountText.setVisibility(View.GONE);
            mShoppingCart.setVisibility(View.GONE);
            dismissProgressDialog();
        }

    }

    private void initTheme() {
        int themeResourceID = new ThemeHelper(this).getThemeResourceId();
        int themeIndex = themeResourceID;
        if (themeIndex <= 0) {
            themeIndex = DEFAULT_THEME;
        }
        getTheme().applyStyle(themeIndex, true);
        UIDHelper.init(new ThemeConfiguration(this, ContentColor.ULTRA_LIGHT, NavigationColor.BRIGHT, AccentRange.ORANGE));

    }

    @Override
    protected void onStop() {
        super.onStop();
        mCategorizedProductList.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void actionBar() {
        FrameLayout frameLayout = findViewById(R.id.iap_header_back_button);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onBackPressed();
            }
        });

        ImageView mBackImage = findViewById(R.id.iap_iv_header_back_button);
        Drawable mBackDrawable = VectorDrawableCompat.create(getResources(), R.drawable.back_arrow, getTheme());
        mBackImage.setBackground(mBackDrawable);
        mTitleTextView = findViewById(R.id.iap_header_title);
        setTitle(getString(R.string.iap_app_name));
        mShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickable())
                    launchIAP(IAPLaunchInput.IAPFlows.IAP_SHOPPING_CART_VIEW, null, null);
            }
        });
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mTitleTextView.setText(title);
    }

    private void launchIAP(int pLandingViews, IAPFlowInput pIapFlowInput, ArrayList<String> pIgnoreRetailerList) {

        if (pIgnoreRetailerList == null)
            mIapLaunchInput.setIAPFlow(pLandingViews, pIapFlowInput, voucherCode);
        else
            mIapLaunchInput.setIAPFlow(pLandingViews, pIapFlowInput, voucherCode, pIgnoreRetailerList);

        try {
            int themeResourceID = new ThemeHelper(this).getThemeResourceId();
            mIapInterface.launch(new ActivityLauncher
                            (this, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_PORTRAIT, null, themeResourceID, null),
                    mIapLaunchInput);

        } catch (RuntimeException exception) {
            Toast.makeText(DemoAppActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(final View view) {
        if (!isClickable()) return;

        if (view == mShoppingCart) {
            launchIAP(IAPLaunchInput.IAPFlows.IAP_SHOPPING_CART_VIEW, null, null);
        } else if (view == mShopNow) {
            launchIAP(IAPLaunchInput.IAPFlows.IAP_PRODUCT_CATALOG_VIEW, null, null);
        } else if (view == mPurchaseHistory) {
            launchIAP(IAPLaunchInput.IAPFlows.IAP_PURCHASE_HISTORY_VIEW, null, null);
        } else if (view == mLaunchProductDetail) {

            if (null != mCategorizedProductList && mCategorizedProductList.size() > 0) {
                IAPFlowInput iapFlowInput = new IAPFlowInput(mCategorizedProductList.get(mCategorizedProductList.size() - 1).toString().toUpperCase().replaceAll("\\s+", ""));
                launchIAP(IAPLaunchInput.IAPFlows.IAP_PRODUCT_DETAIL_VIEW, iapFlowInput, null);
            } else {
                Toast.makeText(DemoAppActivity.this, "Please add CTN", Toast.LENGTH_SHORT).show();
            }
        } else if (view == mShopNowCategorized) {
            if (mCategorizedProductList.size() > 0) {
                IAPFlowInput input = new IAPFlowInput(mCategorizedProductList);
                launchIAP(IAPLaunchInput.IAPFlows.IAP_PRODUCT_CATALOG_VIEW, input, null);
            } else {
                Toast.makeText(DemoAppActivity.this, "Please add CTN", Toast.LENGTH_SHORT).show();
            }
        } else if (view == mShopNowCategorizedWithRetailer) {
            if (mCategorizedProductList.size() > 0) {
                IAPFlowInput input = new IAPFlowInput(mCategorizedProductList);
                Toast.makeText(this, "Given retailer list will ignore in" + ignorelistedRetailer.get(0) + "Retailer list Screen.", Toast.LENGTH_SHORT).show();
                launchIAP(IAPLaunchInput.IAPFlows.IAP_PRODUCT_CATALOG_VIEW, input, ignorelistedRetailer);
            } else {
                Toast.makeText(DemoAppActivity.this, "Please add CTN", Toast.LENGTH_SHORT).show();
            }
        } else if (view == mBuyDirect) {
            IAPFlowInput iapFlowInput =
                    new IAPFlowInput(mEtCTN.getText().toString().toUpperCase().replaceAll("\\s+", ""));
            launchIAP(IAPLaunchInput.IAPFlows.IAP_BUY_DIRECT_VIEW, iapFlowInput, null);
            mEtCTN.setText("");
            hideKeypad(this);
        } else if (view == mRegister) {
            if (mRegister.getText().toString().equalsIgnoreCase(this.getString(R.string.log_out))) {
                if (mUserDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
                    mUserDataInterface.logoutSession(new LogoutSessionListener() {
                        @Override
                        public void logoutSessionSuccess() {
                            finish();
                        }

                        @Override
                        public void logoutSessionFailed(Error error) {
                            Toast.makeText(DemoAppActivity.this, "Logout went wrong", Toast.LENGTH_SHORT).show();
                        }

                    });
                } else {
                    Toast.makeText(DemoAppActivity.this, "User is not logged in", Toast.LENGTH_SHORT).show();
                }
            } else {

                gotoLogInScreen();
            }

        } else if (view == mAddCtn) {
            String str = mEtCTN.getText().toString().toUpperCase().replaceAll("\\s+", "");
            if (!mCategorizedProductList.contains(str)) {
                mCategorizedProductList.add(str);
            }
            mEtCTN.setText("");
            hideKeypad(this);
        } else if (view == mBtn_add_voucher) {
            if (mEtVoucherCode.getText().toString().length() > 0) {
                voucherCode = mEtVoucherCode.getText().toString();
            }
            mEtVoucherCode.setText("");
        }
    }

    private void gotoLogInScreen() {

        URLaunchInput urLaunchInput = new URLaunchInput();
        urLaunchInput.setUserRegistrationUIEventListener(this);
        urLaunchInput.enableAddtoBackStack(true);
        RegistrationContentConfiguration contentConfiguration = new RegistrationContentConfiguration();
        contentConfiguration.enableLastName(true);
        contentConfiguration.enableContinueWithouAccount(true);
        RegistrationConfiguration.getInstance().setPrioritisedFunction(RegistrationFunction.Registration);
        urLaunchInput.setRegistrationContentConfiguration(contentConfiguration);
        urLaunchInput.setRegistrationFunction(RegistrationFunction.Registration);


        ActivityLauncher activityLauncher = new ActivityLauncher(this, ActivityLauncher.
                ActivityOrientation.SCREEN_ORIENTATION_SENSOR, null, 0, null);
        urInterface.launch(activityLauncher, urLaunchInput);


    }

    private void displayViews() {
        mAddCTNLl.setVisibility(View.VISIBLE);
        mLL_voucher.setVisibility(View.VISIBLE);
        mLL_propositionId.setVisibility(View.VISIBLE);
        mShopNowCategorized.setVisibility(View.VISIBLE);
        mShopNowCategorizedWithRetailer.setVisibility(View.VISIBLE);
        mShopNowCategorizedWithRetailer.setText(String.format(getString(R.string.categorized_shop_now_ignore_retailer), ignorelistedRetailer.get(0)));
        mShopNow.setVisibility(View.VISIBLE);
        mShopNow.setEnabled(true);
        mLaunchProductDetail.setVisibility(View.VISIBLE);
        mLaunchProductDetail.setEnabled(true);
    }

    private void hideViews() {
        mCountText.setVisibility(View.GONE);
        mShoppingCart.setVisibility(View.GONE);
        mAddCTNLl.setVisibility(View.GONE);
        mLL_voucher.setVisibility(View.GONE);
        // mLL_propositionId.setVisibility(View.GONE);
        mShopNow.setVisibility(View.GONE);
        mBuyDirect.setVisibility(View.GONE);
        mLaunchProductDetail.setVisibility(View.GONE);
        mPurchaseHistory.setVisibility(View.GONE);
        mShopNowCategorized.setVisibility(View.GONE);
        mShopNowCategorizedWithRetailer.setVisibility(View.GONE);
    }

    private void showAppVersion() {
        String code = null;
        try {
            code = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            IAPLog.e(IAPLog.LOG, e.getMessage());
        }
        TextView versionView = findViewById(R.id.appversion);
        versionView.setText(String.valueOf(code));
    }

    private void showToast(int errorCode) {
        String errorText = null;
        if (IAPConstant.IAP_ERROR_NO_CONNECTION == errorCode) {
            errorText = "No connection";
        } else if (IAPConstant.IAP_ERROR_CONNECTION_TIME_OUT == errorCode) {
            errorText = "Connection time out";
        } else if (IAPConstant.IAP_ERROR_AUTHENTICATION_FAILURE == errorCode) {
            errorText = "Authentication failure";
        } else if (IAPConstant.IAP_ERROR_INSUFFICIENT_STOCK_ERROR == errorCode) {
            errorText = "Product out of stock";
        } else if (IAPConstant.IAP_ERROR_INVALID_CTN == errorCode) {
            errorText = "Invalid ctn";
        }
        if (errorText != null) {
            Toast toast = Toast.makeText(this, errorText, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    //In-App listener functions
    @Override
    public void onGetCartCount(int count) {
        if (count > 0) {
            mCountText.setText(count + "");
            mCountText.setVisibility(View.VISIBLE);
        } else if (count == 0) {
            mCountText.setVisibility(View.GONE);
        } else if (count == -1) {
            //Plan B
            mShoppingCart.setVisibility(View.GONE);
        }

        try {
            mIapInterface.getCompleteProductList(this);
        } catch (Exception e) {
            IAPLog.e(IAPLog.LOG, e.getMessage());
        }

    }

    @Override
    public void onUpdateCartCount() {
        mIapInterface.getProductCartCount(this);
    }

    @Override
    public void updateCartIconVisibility(boolean shouldShow) {
        if (shouldShow) {
            mShoppingCart.setVisibility(View.VISIBLE);
            mCountText.setVisibility(View.VISIBLE);
        } else {
            mShoppingCart.setVisibility(View.GONE);
            mCountText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetCompleteProductList(ArrayList<String> productList) {
        mShoppingCart.setOnClickListener(this);
        dismissProgressDialog();
    }

    @Override
    public void onSuccess() {
        dismissProgressDialog();
    }

    @Override
    public void onSuccess(boolean bool) {
        displayFlowViews(bool);
    }

    @Override
    public void onFailure(int errorCode) {
        showToast(errorCode);
        dismissProgressDialog();
    }


    //User Registration interface functions
    @Override
    public void onUserRegistrationComplete(Activity activity) {
        activity.finish();
        mRegister.setText(this.getString(R.string.log_out));
        initializeIAPComponant();
    }

    @Override
    public void onPrivacyPolicyClick(Activity activity) {
    }

    @Override
    public void onTermsAndConditionClick(Activity activity) {
    }

    void showScreenSizeInDp() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        Toast.makeText(this, "Screen width in dp is :" + dpWidth, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    public void showProgressDialog() {
        mProgressDialog = new ProgressDialog(UIDHelper.getPopupThemedContext(this));
        mProgressDialog.getWindow().setGravity(Gravity.CENTER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait" + "...");

        if ((!mProgressDialog.isShowing()) && !(DemoAppActivity.this).isFinishing()) {
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.progressbar_dls);
        }
    }


    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    boolean isClickable() {

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        return true;
    }

    @Override
    public boolean isMockEnabled() {
        return enableMock;
    }

    @Override
    public JSONObject GetMockJson(String fileName) {
        fileName = fileName + ".json";
        return getResponseJson(fileName);
    }

    @Override
    public JSONObject GetProductCatalogResponse() {
        return getResponseJson("product.json");
    }

    @Override
    public JSONObject OAuthResponse() {
        return getResponseJson("bearerAuth.json");
    }


    public JSONObject getResponseJson(String fileName) {

        String jsonString = loadJSONFromAsset(fileName);

        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = this.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            return null;
        } catch (Exception e) {
            return null;
        }
        return json;
    }

    @Override
    public void didPlaceOrder() {
        Toast.makeText(this, "Order is placed ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void didCancelOrder() {
        Toast.makeText(this, "Order is Cancelled ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean shouldPopToProductList() {
        return false;
    }

    @Override
    public View getBannerView() {
        if (isBannerEnabled) {
            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.banner_view, null);
            return v;
        }
        return null;
    }

    boolean isUserLoggedIn(){
        return  mUserDataInterface != null && mUserDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN ;
    }
}
