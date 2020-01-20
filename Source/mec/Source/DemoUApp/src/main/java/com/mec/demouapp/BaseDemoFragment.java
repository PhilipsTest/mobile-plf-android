package com.mec.demouapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.philips.cdp.di.mec.integration.MECBannerConfigurator;
import com.philips.cdp.di.mec.integration.MECBazaarVoiceInput;
import com.philips.cdp.di.mec.integration.MECDependencies;
import com.philips.cdp.di.mec.integration.MECFlowConfigurator;
import com.philips.cdp.di.mec.integration.MECInterface;
import com.philips.cdp.di.mec.integration.MECLaunchInput;
import com.philips.cdp.di.mec.integration.MECListener;
import com.philips.cdp.di.mec.integration.MECSettings;
import com.philips.cdp.di.mec.screens.reviews.MECBazaarVoiceEnvironment;
import com.philips.cdp.di.mec.utils.MECConstant;
import com.philips.cdp.di.mec.utils.MECDataHolder;
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
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uappframework.listener.BackEventListener;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappSettings;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.view.widget.Button;
import com.philips.platform.uid.view.widget.EditText;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class BaseDemoFragment extends Fragment implements View.OnClickListener, MECListener, BackEventListener,
        UserRegistrationUIEventListener, MECBannerConfigurator, ActionBarListener, CompoundButton.OnCheckedChangeListener {

    private final int DEFAULT_THEME = R.style.Theme_DLS_Blue_UltraLight;
    private LinearLayout mAddCTNLl;
    private EditText mEtCTN, mEtPropositionId;

    private Button mRegister;
    private Button mShopNow;
    private Button mShopNowCategorized;
    private Button mLaunchProductDetail;
    private Button mAddCtn, mBtnSetPropositionId;
    private Button mShopNowCategorizedWithRetailer;
    private ProgressDialog mProgressDialog = null;
    private ArrayList<String> mCategorizedProductList;
    private TextView mCountText;
    private ImageView mBackImage;
    private TextView text;

    private MECInterface mMecInterface;
    private MECLaunchInput mMecLaunchInput;
    private MECSettings mMecSettings;

    private UserDataInterface mUserDataInterface;
    ImageView mCartIcon;

    private ArrayList<String> ignorelistedRetailer;
    private View mLL_propositionId;
    URInterface urInterface;
    private long mLastClickTime = 0;

    private boolean isHybrisEnable = true,isBannerEnabled = true ,isRetailerEnabled = true;
    private ToggleButton toggleBanner,toggleHybris,toggleRetailer;

    private CheckBox bvCheckBox;
    private MECBazaarVoiceInput mecBazaarVoiceInput;
    private TextView versionView;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        urInterface = new URInterface();
        urInterface.init(new MecDemoUAppDependencies(new AppInfra.Builder().build(getContext())), new MecDemoAppSettings(getContext()));

        ignorelistedRetailer = new ArrayList<>();
        rootView = inflater.inflate(R.layout.base_demo_fragment, container, false);
        mEtCTN = rootView.findViewById(R.id.et_add_ctn);
        mAddCTNLl = rootView.findViewById(R.id.ll_ctn);
        bvCheckBox = rootView.findViewById(R.id.bv_checkbox);
        bvCheckBox.setOnCheckedChangeListener(this);

        text = getActivity().findViewById(R.id.mec_demo_app_header_title);
        versionView = getActivity().findViewById(R.id.demoappversion);
        mBackImage = getActivity().findViewById(R.id.mec_demo_app_iv_header_back_button);


        mEtPropositionId = rootView.findViewById(R.id.et_add_proposition_id);
        mBtnSetPropositionId = rootView.findViewById(R.id.btn_set_proposition_id);


        AppInfraInterface appInfra = new AppInfra.Builder().build(getContext());
        AppConfigurationInterface configInterface = appInfra.getConfigInterface();
        AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface.AppConfigurationError();

        String propertyForKey = (String) configInterface.getPropertyForKey("propositionid", "MEC", configError);
        mEtPropositionId.setText(propertyForKey);


        mecBazaarVoiceInput = new MECBazaarVoiceInput();
        mBtnSetPropositionId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                configInterface.setPropertyForKey("propositionid", "MEC", mEtPropositionId.getText().toString(), configError);

                Toast.makeText(getActivity(), "Proposition id is set", Toast.LENGTH_SHORT).show();
                getActivity().finishAffinity();
                System.exit(0);
            }
        });


        toggleBanner = rootView.findViewById(R.id.toggleBanner);

        toggleBanner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                isBannerEnabled = isChecked;
                initializeMECComponant();
            }
        });

        toggleHybris = rootView.findViewById(R.id.toggleHybris);
        toggleHybris.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isHybrisEnable = isChecked;
                initializeMECComponant();
            }
        });


        toggleRetailer = rootView.findViewById(R.id.toggleRetailer);


        toggleRetailer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRetailerEnabled= isChecked;
                initializeMECComponant();
            }
        });


        mRegister = rootView.findViewById(R.id.btn_register);
        mRegister.setOnClickListener(this);



        mShopNow = rootView.findViewById(R.id.btn_shop_now);
        mShopNow.setOnClickListener(this);

        mLaunchProductDetail = rootView.findViewById(R.id.btn_launch_product_detail);
        mLaunchProductDetail.setOnClickListener(this);

        // mShoppingCart = rootView.findViewById(R.id.mec_demo_app_shopping_cart_icon);

        mShopNowCategorized = rootView.findViewById(R.id.btn_categorized_shop_now);
        mShopNowCategorized.setOnClickListener(this);


        mLL_propositionId = rootView.findViewById(R.id.ll_enter_proposition_id);

        mAddCtn = rootView.findViewById(R.id.btn_add_ctn);
        mAddCtn.setOnClickListener(this);


        mShopNowCategorizedWithRetailer = rootView.findViewById(R.id.btn_categorized_shop_now_with_ignore_retailer);
        mShopNowCategorizedWithRetailer.setOnClickListener(this);

        mCartIcon = rootView.findViewById(R.id.mec_demo_app_cart_iv);
        mCountText = rootView.findViewById(R.id.mec_demo_app_item_count);

        mCategorizedProductList = new ArrayList<>();

        mUserDataInterface = urInterface.getUserDataInterface();


        mMecInterface = new MECInterface();
        mMecSettings = new MECSettings(getActivity());
        //actionBar();
        initializeBazaarVoice();
        initializeMECComponant();


        return rootView;

    }

    private void initializeMECComponant() {
        toggleHybris.setVisibility(View.VISIBLE);
        initMEC();

        if (mUserDataInterface != null && mUserDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
            mRegister.setText(this.getString(R.string.log_out));
        } else {
            mRegister.setVisibility(View.VISIBLE);
            // Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    private void initMEC() {
        ignorelistedRetailer.add("Frys.com");
        ignorelistedRetailer.add("Amazon - US");
        ignorelistedRetailer.add("BestBuy.com");

        UappDependencies uappDependencies = new UappDependencies(new AppInfra.Builder().build(getActivity()));
        UappSettings uappSettings = new UappSettings(getContext());

        urInterface.init(uappDependencies, uappSettings);

        MECDependencies mIapDependencies = new MECDependencies(new AppInfra.Builder().build(getActivity()), urInterface.getUserDataInterface());

        try {
            mMecInterface.init(mIapDependencies, mMecSettings);
        } catch (RuntimeException ex) {
        }

        mMecLaunchInput = new MECLaunchInput();
        mMecLaunchInput.setMecListener(this);


        mMecLaunchInput.mecBannerConfigurator = this::getBannerViewProductList;
        mMecLaunchInput.setSupportsHybris(isHybrisEnable);
        mMecLaunchInput.setSupportsRetailer(isRetailerEnabled);
        mMecLaunchInput.mecBazaarVoiceInput = mecBazaarVoiceInput;
    }


    private void displayFlowViews(boolean b) {

        mAddCTNLl.setVisibility(View.VISIBLE);
        mLL_propositionId.setVisibility(View.VISIBLE);
        mShopNowCategorized.setVisibility(View.VISIBLE);
        mLaunchProductDetail.setVisibility(View.VISIBLE);
        mLaunchProductDetail.setEnabled(true);


        mCartIcon.setVisibility(View.VISIBLE);
        mCountText.setVisibility(View.VISIBLE);
        mShopNow.setVisibility(View.VISIBLE);
        mShopNow.setEnabled(true);
        dismissProgressDialog();
    }


    @Override
    public void onStop() {
        super.onStop();
        mCategorizedProductList.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void launchMEC(MECFlowConfigurator.MECLandingView mecLandingView, MECFlowConfigurator pMecFlowConfigurator, ArrayList<String> pIgnoreRetailerList) {

        pMecFlowConfigurator.setLandingView(mecLandingView);
        mMecLaunchInput.setFlowConfigurator(pMecFlowConfigurator);


        try {
            int themeResourceID = new ThemeHelper(getActivity()).getThemeResourceId();
            mMecInterface.launch(new ActivityLauncher
                            (getActivity(), ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_PORTRAIT, null, themeResourceID, null),
                    mMecLaunchInput);

        } catch (RuntimeException exception) {
            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void launchMECasFragment(MECFlowConfigurator.MECLandingView mecLandingView, MECFlowConfigurator pMecFlowConfigurator, ArrayList<String> pIgnoreRetailerList) {

        pMecFlowConfigurator.setLandingView(mecLandingView);
        mMecLaunchInput.setFlowConfigurator(pMecFlowConfigurator);
        mMecInterface.launch(new FragmentLauncher(getActivity(), R.id.container_base_demo, this),
                mMecLaunchInput);

    }

    @Override
    public void onClick(final View view) {
        if (!isClickable()) return;

        if (view == mShopNow) {
            if (getActivity() instanceof LaunchAsActivity) {
                launchMEC(MECFlowConfigurator.MECLandingView.MEC_PRODUCT_LIST_VIEW, new MECFlowConfigurator(), null);
            } else if (getActivity() instanceof LaunchAsFragment) {
                launchMECasFragment(MECFlowConfigurator.MECLandingView.MEC_PRODUCT_LIST_VIEW, new MECFlowConfigurator(), null);
            }
        } else if (view == mLaunchProductDetail) {
            if (null != mCategorizedProductList && mCategorizedProductList.size() > 0) {
                MECFlowConfigurator input = new MECFlowConfigurator();
                input.setCTNs(mCategorizedProductList) ;

                if (getActivity() instanceof LaunchAsActivity) {
                    launchMEC(MECFlowConfigurator.MECLandingView.MEC_PRODUCT_DETAILS_VIEW, input, null);
                } else if (getActivity() instanceof LaunchAsFragment) {
                    launchMECasFragment(MECFlowConfigurator.MECLandingView.MEC_PRODUCT_DETAILS_VIEW, input, null);
                }
            } else {
                Toast.makeText(getActivity(), "Please add CTN", Toast.LENGTH_SHORT).show();
            }
        } else if (view == mShopNowCategorized) {
            if (mCategorizedProductList.size() > 0) {
                MECFlowConfigurator input = new MECFlowConfigurator();
                input.setCTNs(mCategorizedProductList);

                if (getActivity() instanceof LaunchAsActivity) {
                    launchMEC(MECFlowConfigurator.MECLandingView.MEC_CATEGORIZED_PRODUCT_LIST_VIEW, input, null);
                } else if (getActivity() instanceof LaunchAsFragment) {
                    launchMECasFragment(MECFlowConfigurator.MECLandingView.MEC_CATEGORIZED_PRODUCT_LIST_VIEW, input, null);
                }
            } else {
                Toast.makeText(getActivity(), "Please add CTN", Toast.LENGTH_SHORT).show();
            }
        } else if (view == mRegister) {
            if (mRegister.getText().toString().equalsIgnoreCase(this.getString(R.string.log_out))) {
                if (mUserDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
                    mUserDataInterface.logoutSession(new LogoutSessionListener() {
                        @Override
                        public void logoutSessionSuccess() {
                            getActivity().finish();
                        }

                        @Override
                        public void logoutSessionFailed(Error error) {
                            Toast.makeText(getActivity(), "Logout went wrong", Toast.LENGTH_SHORT).show();
                        }

                    });
                } else {
                    Toast.makeText(getActivity(), "User is not logged in", Toast.LENGTH_SHORT).show();
                }
            } else {

                gotoLogInScreen();
            }

        } else if (view == mAddCtn) {
            String str = mEtCTN.getText().toString().toUpperCase().replaceAll("\\s+", "");
            if (!mCategorizedProductList.contains(str)) {
                mCategorizedProductList.add(str);
            }
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            mEtCTN.setText("");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        View v = getActivity().getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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


        ActivityLauncher activityLauncher = new ActivityLauncher(getActivity(), ActivityLauncher.
                ActivityOrientation.SCREEN_ORIENTATION_SENSOR, null, 0, null);
        urInterface.launch(activityLauncher, urLaunchInput);
    }

    private void showToast(int errorCode) {
        String errorText = null;
        if (MECConstant.INSTANCE.getMEC_ERROR_NO_CONNECTION() == errorCode) {
            errorText = "No connection";
        } else if (MECConstant.INSTANCE.getMEC_ERROR_CONNECTION_TIME_OUT() == errorCode) {
            errorText = "Connection time out";
        } else if (MECConstant.INSTANCE.getMEC_ERROR_AUTHENTICATION_FAILURE() == errorCode) {
            errorText = "Authentication failure";
        } else if (MECConstant.INSTANCE.getMEC_ERROR_INSUFFICIENT_STOCK_ERROR() == errorCode) {
            errorText = "Product out of stock";
        } else if (MECConstant.INSTANCE.getMEC_ERROR_INVALID_CTN() == errorCode) {
            errorText = "Invalid ctn";
        }
        if (errorText != null) {
            Toast toast = Toast.makeText(getActivity(), errorText, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    //In-App listener functions
    @Override
    public void onGetCartCount(int count) {


    }

    @Override
    public void onUpdateCartCount() {
        mMecInterface.getProductCartCount(this);
    }

    @Override
    public void updateCartIconVisibility(boolean shouldShow) {

    }

    @Override
    public void onGetCompleteProductList(ArrayList<String> productList) {
        //mShoppingCart.setOnClickListener(this);
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
        initializeMECComponant();
    }

    @Override
    public void onPrivacyPolicyClick(Activity activity) {
    }

    @Override
    public void onTermsAndConditionClick(Activity activity) {
    }

    @Override
    public void onPersonalConsentClick(Activity activity) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(getActivity(), "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(getActivity(), "portrait", Toast.LENGTH_SHORT).show();
        }
    }


    public void showProgressDialog() {
        mProgressDialog = new ProgressDialog(UIDHelper.getPopupThemedContext(getActivity()));
        mProgressDialog.getWindow().setGravity(Gravity.CENTER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait" + "...");

        if ((!mProgressDialog.isShowing()) && !(getActivity()).isFinishing()) {
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
    public View getBannerViewProductList() {
        if (isBannerEnabled) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.banner_view, null);
            return v;
        }
        return null;

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.isPressed()) {
            showDialog();
        }
        initializeMECComponant();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog OptionDialog = builder.create();
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        setBazaarVoiceEnvironmentToProd();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        bvCheckBox.setChecked(!bvCheckBox.isChecked());
                        OptionDialog.dismiss();
                        break;
                }
            }
        };

        builder.setMessage("Are you sure to change Bazaar voice environment ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void setBazaarVoiceEnvironmentToProd() {
        String env = (bvCheckBox.isChecked()) ? MECBazaarVoiceEnvironment.PRODUCTION.toString() : MECBazaarVoiceEnvironment.STAGING.toString();
        SharedPreferences preferences = getActivity().getSharedPreferences("bvEnv", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("BVEnvironment", env);
        editor.commit();
        getActivity().finishAffinity();
        System.exit(0);
    }

    private void initializeBazaarVoice() {
        SharedPreferences shared = getActivity().getSharedPreferences("bvEnv", MODE_PRIVATE);
        String name = (shared.getString("BVEnvironment", MECBazaarVoiceEnvironment.PRODUCTION.toString()));
        if (name.equalsIgnoreCase(MECBazaarVoiceEnvironment.PRODUCTION.toString())) {
            bvCheckBox.setChecked(true);
            mecBazaarVoiceInput = new MECBazaarVoiceInput() {

                @NotNull
                @Override
                public MECBazaarVoiceEnvironment getBazaarVoiceEnvironment() {

                    return MECBazaarVoiceEnvironment.PRODUCTION;

                }

                @NotNull
                @Override
                public String getBazaarVoiceClientID() {

                    return "philipsglobal";

                }

                @NotNull
                @Override
                public String getBazaarVoiceConversationAPIKey() {

                    return "caAyWvBUz6K3xq4SXedraFDzuFoVK71xMplaDk1oO5P4E";

                }
            };
        } else {
            bvCheckBox.setChecked(false);
            mecBazaarVoiceInput = new MECBazaarVoiceInput() {

                @NotNull
                @Override
                public MECBazaarVoiceEnvironment getBazaarVoiceEnvironment() {

                    return MECBazaarVoiceEnvironment.STAGING;

                }

                @NotNull
                @Override
                public String getBazaarVoiceClientID() {

                    return "philipsglobal";

                }

                @NotNull
                @Override
                public String getBazaarVoiceConversationAPIKey() {

                    return "ca23LB5V0eOKLe0cX6kPTz6LpAEJ7SGnZHe21XiWJcshc";
                }
            };
        }

    }

    public void setTextView(TextView tv) {
        text = tv;
    }


    @Override
    public void updateActionBar(int resId, boolean enableBackKey) {
        updateActionBar(getString(resId), enableBackKey);
    }

    /**
     * For setting the title of action bar and to set back key Enabled/Disabled
     *
     * @param resString     The String to be displayed
     * @param enableBackKey To set back key enabled or disabled
     * @since 1.0.0
     */
    @Override
    public void updateActionBar(String resString, boolean enableBackKey) {
        text.setText(resString);
        versionView.setVisibility(View.GONE);
        if (enableBackKey) {
            mBackImage.setVisibility(View.VISIBLE);
            // For arabic, Hebrew and Perssian the back arrow change from left to right
            if ((Locale.getDefault().getLanguage().contentEquals("ar")) || (Locale.getDefault().getLanguage().contentEquals("fa")) || (Locale.getDefault().getLanguage().contentEquals("he"))) {
                mBackImage.setRotation(180);
            }
        } else {
            mBackImage.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean handleBackEvent() {
        return false;
    }
}


