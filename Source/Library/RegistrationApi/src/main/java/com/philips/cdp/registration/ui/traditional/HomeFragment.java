/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.traditional;

import android.app.ProgressDialog;
import android.content.*;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.*;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.text.style.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.*;
import com.philips.cdp.registration.app.infra.ServiceDiscoveryWrapper;
import com.philips.cdp.registration.app.tagging.*;
import com.philips.cdp.registration.configuration.*;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.events.*;
import com.philips.cdp.registration.events.EventListener;
import com.philips.cdp.registration.handlers.SocialProviderLoginHandler;
import com.philips.cdp.registration.settings.*;
import com.philips.cdp.registration.ui.customviews.*;
import com.philips.cdp.registration.ui.customviews.countrypicker.*;
import com.philips.cdp.registration.ui.traditional.mobile.MobileVerifyCodeFragment;
import com.philips.cdp.registration.ui.utils.*;
import com.philips.cdp.registration.wechat.*;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.*;

import org.json.*;

import java.util.*;

import javax.inject.Inject;

import butterknife.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class HomeFragment extends RegistrationBaseFragment implements OnClickListener,
        NetworStateListener, SocialProviderLoginHandler, EventListener {

    public static final String WECHAT = "wechat";
    private static final int AUTHENTICATION_FAILED = -30;
    private static final int LOGIN_FAILURE = -1;

    @Inject
    NetworkUtility networkUtility;

    @Inject
    AppConfiguration appConfiguration;

    @Inject
    ServiceDiscoveryInterface serviceDiscoveryInterface;

    @Inject
    ServiceDiscoveryWrapper serviceDiscoveryWrapper;

    private final CompositeDisposable disposable = new CompositeDisposable();

    @BindView(R2.id.usr_startScreen_createAccount_Button)
    Button mBtnCreateAccount;

    @BindView(R2.id.usr_startScreen_Login_Button)
    Button mBtnMyPhilips;

    @BindView(R2.id.usr_startScreen_title_label)
    TextView mTvWelcome;

    @BindView(R2.id.usr_startScreen_valueAdd_label)
    TextView mTvWelcomeDesc;

    TextView mTvWelcomeNeedAccount;

    @BindView(R2.id.usr_startScreen_Social_Container)
    LinearLayout mLlSocialProviderBtnContainer;

    @BindView(R2.id.reg_error_msg)
    XRegError mRegError;

    User mUser;
    String mProvider;

    @BindView(R2.id.usr_startScreen_progressBar)
    ProgressBar mPbJanrainInit;
    Context mContext;
    ScrollView mSvRootLayout;
    ProgressDialog mProgressDialog;

    @BindView(R2.id.usr_StartScreen_country_label)
    TextView mCountryDisplay;

    @BindView(R2.id.usr_StartScreen_privacyNotice_label)
    TextView privacyPolicy;

    boolean isWeChatAppRegistered;
    String mWeChatAppId;
    String mWeChatAppSecret;
    IWXAPI mWeChatApi;
    String mWeChatCode;
    String mShowCountrySelection;
    String mLocale;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        URInterface.getComponent().inject(this);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onCreateView");
        mContext = getRegistrationFragment().getParentActivity().getApplicationContext();
        EventHelper.getInstance()
                .registerEventNotification(RegConstants.JANRAIN_INIT_SUCCESS, this);
        EventHelper.getInstance()
                .registerEventNotification(RegConstants.JANRAIN_INIT_FAILURE, this);
        EventHelper.getInstance()
                .registerEventNotification(RegConstants.WECHAT_AUTH, this);
        RegistrationHelper.getInstance().registerNetworkStateListener(this);
        RLog.i(RLog.EVENT_LISTENERS,
                "HomeFragment register: NetworStateListener,JANRAIN_INIT_SUCCESS,JANRAIN_INIT_FAILURE,PARSING_COMPLETED");
        View view;
        if (false || RegistrationConfiguration.getInstance().getPrioritisedFunction().equals(RegistrationFunction.Registration)) {
            view = inflater.inflate(R.layout.reg_fragment_home_create_top, container, false);
        } else {
            view = inflater.inflate(R.layout.reg_fragment_home_login_top, container, false);
        }
        ButterKnife.bind(this, view);
        mSvRootLayout = (ScrollView) view.findViewById(R.id.sv_root_layout);
        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(getActivity(), R.style.reg_Custom_loaderTheme);
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        mProgressDialog.setCancelable(false);
        initUI(view);
        handleOrientation(view);
        registerWeChatApp();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onResume");
//        makeProviderButtonsClickable();
    }

    private void makeProviderButtonsClickable() {
        ViewGroup providerButtonGroup = mLlSocialProviderBtnContainer;
        for (int i = 0; i < providerButtonGroup.getChildCount(); i++) {
            View childView = providerButtonGroup.getChildAt(i);
            if (childView instanceof XProviderButton) {
                childView.setClickable(true);
            }
        }
    }


    protected void handleWeChatCode(final String code) {
        RLog.i("WECHAT", String.format("WeChat Code: ", code));
        WeChatAuthenticator weChatAuthenticator = new WeChatAuthenticator();
        weChatAuthenticator.getWeChatResponse(mWeChatAppId, mWeChatAppSecret, code,
                new WeChatAuthenticationListener() {
                    @Override
                    public void onSuccess(final JSONObject jsonObj) {
                        try {
                            final String token = jsonObj.getString("access_token");
                            final String openId = jsonObj.getString("openid");
                            RLog.i("WECHAT body", "token " + token + " openid " + openId);
                            User user = new User(mContext);
                            user.loginUserUsingSocialNativeProvider(getRegistrationFragment().
                                            getParentActivity(),
                                    "wechat", token, openId, HomeFragment.this, "");
                        } catch (JSONException e) {
                            makeProgressInvisible();
                            hideProgressDialog();
                        }
                    }

                    @Override
                    public void onFail() {
                        makeProgressInvisible();
                        hideProgressDialog();
                        mRegError.setError(mContext.
                                getString(R.string.reg_JanRain_Server_Connection_Failed));
                        scrollViewAutomatically(mRegError, mSvRootLayout);
                    }
                });
    }

    @Override
    public void onDestroy() {
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onDestroy");
        RegistrationHelper.getInstance().unRegisterNetworkListener(this);
        EventHelper.getInstance().unregisterEventNotification(RegConstants.JANRAIN_INIT_SUCCESS,
                this);
        EventHelper.getInstance().unregisterEventNotification(RegConstants.JANRAIN_INIT_FAILURE,
                this);
        EventHelper.getInstance().unregisterEventNotification(RegConstants.WECHAT_AUTH,
                this);

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);
        RLog.i(RLog.EVENT_LISTENERS,
                "HomeFragment unregister: NetworStateListener,JANRAIN_INIT_SUCCESS,JANRAIN_INIT_FAILURE,PARSING_COMPLETED");
        super.onDestroy();
    }

    private void handleSocialProviders(final String countryCode) {
        RLog.d("HomeFragment : ", "handleSocialProviders method country code : " + countryCode);
        if (null != RegistrationConfiguration.getInstance().getProvidersForCountry(countryCode)) {
            mLlSocialProviderBtnContainer.post(new Runnable() {

                @Override
                public void run() {
                    mLlSocialProviderBtnContainer.removeAllViews();
                    List<String> providers = new ArrayList<String>();
                    providers = RegistrationConfiguration.getInstance().getProvidersForCountry(countryCode);
                    if (null != providers) {
                        for (int i = 0; i < providers.size(); i++) {
                            inflateEachProviderBtn(providers.get(i));
                        }
                        RLog.d("HomeFragment", "social providers : " + providers);
                    }
                    handleUiState();
                }
            });
        }
    }

    private void inflateEachProviderBtn(String provider) {

        try {
            String providerName = "reg_" + provider;
            String providerDrawable = "reg_" + provider + "_ic";

            System.out.println("provider " + providerName + " " + providerDrawable);

            int resourceId = getRegistrationFragment().getParentActivity().getResources().getIdentifier(providerName, "string",
                    getRegistrationFragment().getParentActivity().getPackageName());

            int drawableId = getRegistrationFragment().getParentActivity().getResources().getIdentifier(providerDrawable, "string",
                    getRegistrationFragment().getParentActivity().getPackageName());

            System.out.println("provider id " + resourceId + "" + drawableId);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMarginStart(16);
            params.setMarginEnd(16);

            mLlSocialProviderBtnContainer.addView(getProviderBtn(provider, resourceId, drawableId), params);

        } catch (Exception e) {
            RLog.e("HomeFragment", "Inflate Buttons exception :" + e.getMessage());
        }
    }

    private Button getProviderBtn(final String providerName, int providerNameStringId,
                                  int providerLogoDrawableId) {
        final com.philips.platform.uid.view.widget.Button socialButton = new com.philips.platform.uid.view.widget.Button(mContext);
        socialButton.setText(providerNameStringId);

        socialButton.setEnabled(true);
        if (networkUtility.isNetworkAvailable()
                && UserRegistrationInitializer.getInstance().isJanrainIntialized()) {
            socialButton.setEnabled(true);
        } else {
            socialButton.setEnabled(false);
        }
        socialButton.setOnClickListener(v -> {

            trackPage(AppTaggingPages.CREATE_ACCOUNT);

            RLog.d(RLog.ONCLICK, "HomeFragment : " + providerName);
            if (mRegError.isShown()) mRegError.hideError();
            if (networkUtility.isNetworkAvailable()) {
                mFlowId = 3;
                mProvider = providerName;
                if (UserRegistrationInitializer.getInstance().isJanrainIntialized()) {
                    if (!providerName.equalsIgnoreCase(WECHAT)) {
//                            providerBtn.showProgressBar();
                    } else {
                        socialButton.setClickable(false);
                    }
                    callSocialProvider(providerName);
                } else {
                    showProgressDialog();
                    RegistrationHelper.getInstance().initializeUserRegistration(mContext);
                }

            } else {
                scrollViewAutomatically(mRegError, mSvRootLayout);
                enableControls(false);
                mRegError.setError(mContext.getResources().getString(R.string.reg_NoNetworkConnection));
            }
        });
        return socialButton;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        RLog.d(RLog.FRAGMENT_LIFECYCLE, "HomeFragment : onConfigurationChanged");
        setCustomParams(config);
    }

    private void initUI(View view) {
        consumeTouch(view);
        mTvWelcome.setText(getString(R.string.reg_Welcome_Welcome_lbltxt));
        mBtnCreateAccount.setOnClickListener(this);
        mBtnMyPhilips.setOnClickListener(this);
        mCountryDisplay.setText("Country: " + RegistrationHelper.getInstance().getLocale(mContext).getDisplayCountry());
        mCountryDisplay.setOnClickListener(this);

        mPbJanrainInit.setClickable(false);
        mPbJanrainInit.setEnabled(false);

        mUser = new User(mContext);
        linkifyPrivacyPolicy(privacyPolicy);
        linkifyPrivacyPolicy(mCountryDisplay);
//        styliseCountrySelection();
        handleUiState();
        initServiceDiscovery();
        showCountrySelection();
    }

    private void initServiceDiscovery() {
        serviceDiscoveryInterface.getHomeCountry(new ServiceDiscoveryInterface.OnGetHomeCountryListener() {
            @Override
            public void onSuccess(String s, SOURCE source) {
                RLog.d(RLog.SERVICE_DISCOVERY, " Country Sucess :" + s);
                mCountryDisplay.setText("Country: " + new Locale("", s.toUpperCase()).getDisplayCountry());
                handleSocialProviders(s.toUpperCase());
            }

            @Override
            public void onError(ERRORVALUES errorvalues, String s) {
                RLog.d(RLog.SERVICE_DISCOVERY, " Country Error :" + s);
                String fallbackCountry = RegistrationConfiguration.getInstance().getFallBackHomeCountry();
                String selectedCountryCode = null;
                if (null != fallbackCountry) {
                    serviceDiscoveryInterface.setHomeCountry(fallbackCountry.toUpperCase());
                    selectedCountryCode = fallbackCountry;
                } else {
                    serviceDiscoveryInterface.setHomeCountry(RegConstants.COUNTRY_CODE_US);
                    selectedCountryCode = RegConstants.COUNTRY_CODE_US;
                }
                mCountryDisplay.setText("Country: " + new Locale(Locale.getDefault().getLanguage(),
                        selectedCountryCode).getDisplayCountry());
            }
        });
    }

    private void showCountrySelection() {
        mShowCountrySelection = appConfiguration.getShowCountrySelection();
        RLog.d(RLog.SERVICE_DISCOVERY, " Country Show Country Selection :" + mShowCountrySelection);
        if (mShowCountrySelection != null) {
            if (mShowCountrySelection.equalsIgnoreCase("false")) {
            }
        }
    }

    @Override
    public void onClick(View v) {
        /**
         * Library does not include resource constants after ADT 14 Link
         * :http://tools.android.com/tips/non-constant-fields
         */
        if (mRegError.isShown()) mRegError.hideError();
        if (v.getId() == R.id.usr_startScreen_createAccount_Button) {
            RLog.d(RLog.ONCLICK, "HomeFragment : Create Account");
            trackMultipleActionsRegistration();
            launchCreateAccountFragment();
        } else if (v.getId() == R.id.usr_startScreen_Login_Button) {
            RLog.d(RLog.ONCLICK, "HomeFragment : My Philips");
            trackMultipleActionsLogin(AppTagingConstants.MY_PHILIPS);
            launchSignInFragment();
        } else if (v.getId() == R.id.usr_StartScreen_country_label) {
            handleCountrySelection();
        }
    }

    final CountryPicker picker = new CountryPicker();

    private void handleCountrySelection() {
        if (networkUtility.isNetworkAvailable()) {
            picker.setListener(new CountryChangeListener() {

                @Override
                public void onSelectCountry(String name, String code) {

                    RLog.i(RLog.ONCLICK, "HomeFragment :Country Name: " + name + " - Code: ");
                    changeCountry(name, code.trim().toUpperCase());
                    picker.getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    picker.dismiss();

                }
            });


            if (picker != null && picker.getDialog() != null
                    && picker.getDialog().isShowing()) {
            } else {
                try {
                    picker.show(getRegistrationFragment().getFragmentManager(), "COUNTRY_PICKER");
                } catch (Exception e) {
                    //Nop
                }
            }
        } else {
            handleUiState();
        }
    }

    private void changeCountry(String countryName, String countryCode) {
        if (networkUtility.isNetworkAvailable()) {
            serviceDiscoveryInterface.setHomeCountry(countryCode);
            RegistrationHelper.getInstance().setCountryCode(countryCode);
            RLog.d(RLog.SERVICE_DISCOVERY, " Country :" + countryCode.length());
            showProgressDialog();

            RLog.d(RLog.SERVICE_DISCOVERY, " Country :" + RegistrationHelper.getInstance().getCountryCode());

            getLocaleServiceDiscovery(countryName);
        }
    }

    private void getLocaleServiceDiscovery(final String countryName) {
        serviceDiscoveryWrapper.getServiceLocaleWithLanguagePreferenceSingle("userreg.janrain.api")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(String verificationUrl) {
                        if (!verificationUrl.isEmpty()) {
                            updateAppLocale(verificationUrl, countryName);
                            return;
                        }
                        getLocaleServiceDiscoveryByCountry(countryName);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getLocaleServiceDiscoveryByCountry(countryName);
                    }
                });
    }

    private void getLocaleServiceDiscoveryByCountry(String countryName) {
        serviceDiscoveryWrapper.getServiceLocaleWithCountryPreferenceSingle("userreg.janrain.api")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(String verificationUrl) {
                        updateAppLocale(verificationUrl, countryName);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ThreadUtils.postInMainThread(mContext, () -> EventHelper.getInstance().notifyEventOccurred(RegConstants.JANRAIN_INIT_SUCCESS));
                        hideProgressDialog();
                        mRegError.setError(mContext.getString(R.string.reg_Generic_Network_Error));
                        scrollViewAutomatically(mRegError, mSvRootLayout);

                    }
                });
    }

    private void updateAppLocale(String localeString, String countryName) {
        mLocale = localeString;
        RLog.d(RLog.SERVICE_DISCOVERY, "STRING S : " + mLocale);
        String localeArr[] = mLocale.toString().split("_");
        RegistrationHelper.getInstance().initializeUserRegistration(mContext);
        RegistrationHelper.getInstance().setLocale(localeArr[0].trim(), localeArr[1].trim());
        RLog.d(RLog.SERVICE_DISCOVERY, "Change Country code :" + RegistrationHelper.getInstance().getCountryCode());
        handleSocialProviders(RegistrationHelper.getInstance().getCountryCode());
        mCountryDisplay.setText("Country: " + countryName);
        hideProgressDialog();
    }

    int mFlowId = 0;//1 for create account 2 :Philips sign in 3 : Social login

    private void launchSignInFragment() {
        trackPage(AppTaggingPages.SIGN_IN_ACCOUNT);
        if (UserRegistrationInitializer.getInstance().isJanrainIntialized()) {
            getRegistrationFragment().addFragment(new SignInAccountFragment());
            return;
        }
        if (networkUtility.isNetworkAvailable()) {
            showProgressDialog();
            mFlowId = 2;
            RegistrationHelper.getInstance().initializeUserRegistration(mContext);
        }
    }

    private void launchCreateAccountFragment() {
        trackPage(AppTaggingPages.CREATE_ACCOUNT);
        if (UserRegistrationInitializer.getInstance().isJanrainIntialized()) {
            showCreateAccountFragment();
            return;
        }
        if (networkUtility.isNetworkAvailable()) {
            showProgressDialog();
            mFlowId = 1;
            RegistrationHelper.getInstance().initializeUserRegistration(mContext);
        }
    }

    private void showCreateAccountFragment() {
        getRegistrationFragment().addFragment(new CreateAccountFragment());
    }

    private void makeProgressVisible() {
        if (getView() != null) {
            getView().findViewById(R.id.sv_root_layout).setVisibility(View.INVISIBLE);
            getView().findViewById(R.id.ll_root_layout).setVisibility(View.VISIBLE);
        }
    }

    private void makeProgressInvisible() {
        if (getView() != null) {
            getView().findViewById(R.id.sv_root_layout).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.ll_root_layout).setVisibility(View.INVISIBLE);
        }
    }

    private void callSocialProvider(String providerName) {
        RLog.d("HomeFragment", "callSocialProvider method provider name :" + providerName);
        if (null == mUser) {
            return;
        }
        if (networkUtility.isNetworkAvailable()) {
            trackMultipleActionsLogin(providerName);
            trackSocialProviderPage();
            if (!UserRegistrationInitializer.getInstance().isRegInitializationInProgress()) {
                if (providerName.equalsIgnoreCase(WECHAT)) {
                    if (isWeChatAuthenticate()) {
//                        makeProgressVisible();
                        startWeChatAuthentication();
                    } else {
                        hideProviderProgress();
                    }
                    return;
                } else {
                    makeProgressVisible();
                    mUser.loginUserUsingSocialProvider(getActivity(), providerName, this, null);
                }

                return;
            }
            makeProgressVisible();
            RegistrationHelper.getInstance().initializeUserRegistration(mContext);
        }
    }

    private boolean isWeChatAuthenticate() {
        if (!mWeChatApi.isWXAppInstalled()) {
            makeProviderButtonsClickable();
            final String formatedString = String.format(mContext.getText(R.string.reg_App_NotInstalled_AlertMessage).toString(),
                    mContext.getText(R.string.reg_wechat));
            Toast.makeText(mContext, formatedString
                    , Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!mWeChatApi.isWXAppSupportAPI()) {
            makeProviderButtonsClickable();
            Toast.makeText(mContext, mContext.getText(R.string.reg_Provider_Not_Supported)
                    , Toast.LENGTH_SHORT).show();
            return false;
        }
        return isWeChatAppRegistered;
    }

    private void startWeChatAuthentication() {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "123456";
        mWeChatApi.sendReq(req);
    }

    @Override
    public void setViewParams(Configuration config, int width) {
//        applyParams(config, mTvWelcome, width);
//        applyParams(config, mTvWelcomeDesc, width);
//        applyParams(config, mTvWelcomeDesc, width);
//        applyParams(config, mTvWelcomeNeedAccount, width);
    }

    @Override
    protected void handleOrientation(View view) {
        handleOrientationOnView(view);
    }

    @Override
    public int getTitleResourceId() {
        return R.string.reg_SigIn_TitleTxt;
    }

    @Override
    public void onEventReceived(String event) {
        RLog.i(RLog.EVENT_LISTENERS, "HomeFragment :onCounterEventReceived" +
                " isHomeFragment :onCounterEventReceived is : " + event);
        if (RegConstants.JANRAIN_INIT_SUCCESS.equals(event)) {
            hideProgressDialog();
            if (mFlowId == 1) {
                showCreateAccountFragment();
                mFlowId = 0;
                return;
            }
            if (mFlowId == 2) {
                getRegistrationFragment().addFragment(new SignInAccountFragment());
                mFlowId = 0;
                return;
            }
            if (mFlowId == 3) {
                if (mProvider.equalsIgnoreCase("wechat")) {
                    if (isWeChatAuthenticate()) {
//                        makeProgressVisible();
                        makeProgressInvisible();
                        hideProgressDialog();
                        hideProviderProgress();
                        startWeChatAuthentication();
                    } else {
                        hideProviderProgress();
                    }
                } else {
                    mUser.loginUserUsingSocialProvider(getActivity(), mProvider, this, null);
                }

                mFlowId = 0;
            }
        } else if (RegConstants.JANRAIN_INIT_FAILURE.equals(event)) {
            makeProgressInvisible();
            hideProgressDialog();
            hideProviderProgress();
            mFlowId = 0;
        } else if (RegConstants.WECHAT_AUTH.equals(event)) {
            if (mWeChatCode != null) {
                makeProgressVisible();
                handleWeChatCode(mWeChatCode);
            }
        }
    }

    private void handleUiState() {
        if (networkUtility.isNetworkAvailable()) {
            mRegError.hideError();
            enableControls(true);
        } else {
            mRegError.setError(mContext.getResources().getString(R.string.reg_NoNetworkConnection));
            enableControls(false);
            scrollViewAutomatically(mRegError, mSvRootLayout);
        }
    }

    private void enableControls(boolean clickableState) {
        if (clickableState) {
            mRegError.hideError();
        }
        handleBtnClickableStates(clickableState);
    }

    private void handleBtnClickableStates(boolean state) {
        mBtnCreateAccount.setEnabled(state);
        enableSocialProviders(state);
        mBtnMyPhilips.setEnabled(state);
        if (state) {
//            mBtnMyPhilips.setProviderTextColor(R.color.reg_btn_text_enable_color);
            return;
        }
//        mBtnMyPhilips.setProviderTextColor(R.color.reg_btn_text_disabled_color);
    }

    private void enableSocialProviders(boolean enableState) {
        for (int i = 0; i < mLlSocialProviderBtnContainer.getChildCount(); i++) {
            mLlSocialProviderBtnContainer.getChildAt(i).setEnabled(enableState);
        }
    }

    private void linkifyTermAndPolicy(TextView pTvPrivacyPolicy) {
        if (!RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired()) {
            linkifyPrivacyPolicy(pTvPrivacyPolicy);
        } else {
            linifyPrivacyPolicyAndTerms(pTvPrivacyPolicy);
        }
    }

    private void linifyPrivacyPolicyAndTerms(TextView pTvPrivacyPolicy) {
        String privacyPolicyText = getString(R.string.reg_LegalNoticeText_With_Terms_And_Conditions);
        privacyPolicyText = String.format(privacyPolicyText,
                getString(R.string.reg_PrivacyNoticeText),
                getString(R.string.reg_TermsAndConditionsText));
        mTvWelcomeDesc.setText(privacyPolicyText);

        String privacy = mContext.getResources().getString(R.string.reg_PrivacyNoticeText);
        String terms = mContext.getResources().getString(R.string.reg_TermsAndConditionsText);

        SpannableString spanableString = new SpannableString(privacyPolicyText);

        int privacyStartIndex = privacyPolicyText.toLowerCase().indexOf(
                privacy.toLowerCase());


        spanableString.setSpan(privacyClickListener, privacyStartIndex, privacyStartIndex +
                        privacy.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        int termStartIndex = privacyPolicyText.toLowerCase().indexOf(
                terms.toLowerCase());


        spanableString.setSpan(termsClickListener, termStartIndex, termStartIndex + terms.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        removeUnderlineFromLink(spanableString);

        pTvPrivacyPolicy.setText(spanableString);
        pTvPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        pTvPrivacyPolicy.setLinkTextColor(ContextCompat.getColor(getContext(),
                R.color.reg_hyperlink_highlight_color));
        pTvPrivacyPolicy.setHighlightColor(ContextCompat.getColor(getContext(),
                android.R.color.transparent));
    }

    private ClickableSpan privacyClickListener = new ClickableSpan() {

        @Override
        public void onClick(View widget) {
            if (mRegError.isShown()) mRegError.hideError();
            handlePrivacyPolicy();
        }

    };

    private ClickableSpan termsClickListener = new ClickableSpan() {

        @Override
        public void onClick(View widget) {
            if (mRegError.isShown()) mRegError.hideError();
            handleTermsCondition();
        }

    };

    private void linkifyPrivacyPolicy(TextView pTvPrivacyPolicy) {
        String privacy = pTvPrivacyPolicy.getText().toString();

//                mContext.getResources().getString(R.string.reg_PrivacyNoticeText);
//        privacyPolicy.setText(privacy);


        SpannableString spanableString = new SpannableString(privacy);

        spanableString.setSpan(privacyClickListener, 0, privacy.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        removeUnderlineFromLink(spanableString);

        pTvPrivacyPolicy.setText(spanableString);
        pTvPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        pTvPrivacyPolicy.setLinkTextColor(ContextCompat.getColor(getContext(),
                R.color.reg_hyperlink_highlight_color));
        pTvPrivacyPolicy.setHighlightColor(ContextCompat.getColor(getContext(),
                android.R.color.transparent));
    }

    private void styliseCountrySelection() {
        SpannableString content = new SpannableString(mCountryDisplay.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        mCountryDisplay.setText(content);
//        resetPasswordLabel.setOnClickListener(view -> launchResetPasswordFragment());
    }

    private void removeUnderlineFromLink(SpannableString spanableString) {
        for (ClickableSpan u : spanableString.getSpans(0, spanableString.length(),
                ClickableSpan.class)) {
            spanableString.setSpan(new UnderlineSpan() {

                public void updateDrawState(TextPaint tp) {
                    tp.setUnderlineText(false);
                }
            }, spanableString.getSpanStart(u), spanableString.getSpanEnd(u), 0);
        }

        for (URLSpan u : spanableString.getSpans(0, spanableString.length(), URLSpan.class)) {
            spanableString.setSpan(new UnderlineSpan() {

                public void updateDrawState(TextPaint tp) {
                    tp.setUnderlineText(false);
                }
            }, spanableString.getSpanStart(u), spanableString.getSpanEnd(u), 0);
        }
    }

    private void handlePrivacyPolicy() {
        getRegistrationFragment().getUserRegistrationUIEventListener().
                onPrivacyPolicyClick(getRegistrationFragment().getParentActivity());
    }

    private void handleTermsCondition() {
        getRegistrationFragment().getUserRegistrationUIEventListener().
                onTermsAndConditionClick(getRegistrationFragment().getParentActivity());
    }

    @Override
    public void onLoginSuccess() {

        handleLoginSuccess();

    }

    private void handleLoginSuccess() {
        trackActionStatus(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                AppTagingConstants.SUCCESS_LOGIN);
        RLog.i(RLog.CALLBACK, "HomeFragment : onLoginSuccess");
        hideProviderProgress();
        enableControls(true);

        boolean isEmailAvailable = mUser.getEmail() != null && FieldsValidator.isValidEmail(mUser.getEmail());
        boolean isMobileNoAvailable = mUser.getMobile() != null && FieldsValidator.isValidMobileNumber(mUser.getMobile());
        if (isEmailAvailable && isMobileNoAvailable && !mUser.isEmailVerified()) {
            launchAccountActivationFragment();
            return;
        }

        if ((mUser.isEmailVerified() || mUser.isMobileVerified())) {
            launchWelcomeFragment();
        } else {
            if (isEmailAvailable) {
                launchAccountActivationFragment();
            } else {
                launchMobileVerifyCodeFragment();
            }
        }
    }

    private void launchMobileVerifyCodeFragment() {
        getRegistrationFragment().addFragment(new MobileVerifyCodeFragment());
        trackPage(AppTaggingPages.MOBILE_VERIFY_CODE);
    }

    private void launchAccountActivationFragment() {
        getRegistrationFragment().launchAccountActivationFragmentForLogin();
    }

    private void launchWelcomeFragment() {
        String emailorMobile;
        if (FieldsValidator.isValidEmail(mUser.getEmail())) {
            emailorMobile = mUser.getEmail();
        } else {
            emailorMobile = mUser.getMobile();
        }
        if (emailorMobile != null && RegistrationConfiguration.getInstance().
                isTermsAndConditionsAcceptanceRequired() &&
                !RegPreferenceUtility.getStoredState(mContext, emailorMobile) || !mUser.getReceiveMarketingEmail()) {
            launchAlmostDoneForTermsAcceptanceFragment();
            return;
        }
        trackPage(AppTaggingPages.WELCOME);
        getRegistrationFragment().addWelcomeFragmentOnVerification();
    }


    private void launchAlmostDoneForTermsAcceptanceFragment() {
        trackPage(AppTaggingPages.ALMOST_DONE);
        getRegistrationFragment().addAlmostDoneFragmentforTermsAcceptance();
    }

    private void hideProviderProgress() {

        if (getView() == null) {
            return;
        }

        getView().findViewById(R.id.sv_root_layout).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.ll_root_layout).setVisibility(View.INVISIBLE);

        if (null != getView().findViewWithTag(mProvider)) {
            XProviderButton providerButton = (XProviderButton) getView().findViewWithTag(mProvider);
            providerButton.hideProgressBar();
        }
    }

    @Override
    public void onLoginFailedWithError(final UserRegistrationFailureInfo userRegistrationFailureInfo) {
        handleLoginFailedWithError(userRegistrationFailureInfo);
    }

    private void handleLoginFailedWithError(UserRegistrationFailureInfo
                                                    userRegistrationFailureInfo) {
        RLog.i(RLog.CALLBACK, "HomeFragment : onLoginFailedWithError : code :" +
                userRegistrationFailureInfo.getErrorCode());
        trackPage(AppTaggingPages.HOME);
        hideProviderProgress();
        enableControls(true);
        //Temp fix need to be changed
        if (userRegistrationFailureInfo.getErrorCode() == AUTHENTICATION_FAILED) {
            mRegError.setError(mContext.getString(R.string.reg_JanRain_Server_Connection_Failed));
            scrollViewAutomatically(mRegError, mSvRootLayout);
        } else {
            mRegError.setError(mContext.getString(R.string.reg_Generic_Network_Error));
            scrollViewAutomatically(mRegError, mSvRootLayout);
        }
    }

    @Override
    public void onLoginFailedWithTwoStepError(final JSONObject prefilledRecord,
                                              final String socialRegistrationToken) {
        RLog.i(RLog.CALLBACK, "HomeFragment : onLoginFailedWithTwoStepError");
        hideProviderProgress();
        enableControls(true);
        RLog.i("HomeFragment", "Login failed with two step error" + "JSON OBJECT :"
                + prefilledRecord);
        launchAlmostDoneFragment(prefilledRecord, socialRegistrationToken);
    }

    private void launchAlmostDoneFragment(JSONObject prefilledRecord, String socialRegistrationToken) {
        trackPage(AppTaggingPages.ALMOST_DONE);
        getRegistrationFragment().addAlmostDoneFragment(prefilledRecord, mProvider,
                socialRegistrationToken);
    }

    @Override
    public void onLoginFailedWithMergeFlowError(final String mergeToken, final String existingProvider,
                                                final String conflictingIdentityProvider, String conflictingIdpNameLocalized,
                                                String existingIdpNameLocalized, final String emailId) {
        handleLoginFailedWithMergeFlowError(existingProvider, mergeToken, conflictingIdentityProvider, emailId);
    }

    private void handleLoginFailedWithMergeFlowError(String existingProvider, String mergeToken, String conflictingIdentityProvider, String emailId) {
        hideProviderProgress();
        enableControls(true);
        if (mUser.handleMergeFlowError(existingProvider)) {
            launchMergeAccountFragment(mergeToken, conflictingIdentityProvider, emailId);
        } else {
            mProvider = existingProvider;
            Bundle bundle = new Bundle();
            bundle.putString(RegConstants.SOCIAL_PROVIDER, conflictingIdentityProvider);
            bundle.putString(RegConstants.CONFLICTING_SOCIAL_PROVIDER, existingProvider);
            bundle.putString(RegConstants.SOCIAL_MERGE_TOKEN, mergeToken);
            bundle.putString(RegConstants.SOCIAL_MERGE_EMAIL, emailId);
            launchSocialToSocialMergeAccountFragment(bundle);
        }
    }

    private void launchMergeAccountFragment(String mergeToken, String existingProvider, String emailId) {
        trackPage(AppTaggingPages.MERGE_ACCOUNT);
        getRegistrationFragment().addMergeAccountFragment(mergeToken, existingProvider, emailId);
    }

    private void launchSocialToSocialMergeAccountFragment(Bundle bundle) {
        trackPage(AppTaggingPages.MERGE_SOCIAL_ACCOUNT);
        getRegistrationFragment().addMergeSocialAccountFragment(bundle);
    }

    @Override
    public void onContinueSocialProviderLoginSuccess() {

        RLog.i(RLog.CALLBACK, "HomeFragment : onContinueSocialProviderLoginSuccess");
        hideProviderProgress();
        enableControls(true);
        launchWelcomeFragment();
    }

    @Override
    public void onContinueSocialProviderLoginFailure(
            final UserRegistrationFailureInfo userRegistrationFailureInfo) {
        handleContinueSocialProviderLoginFailure(userRegistrationFailureInfo);
    }

    private void handleContinueSocialProviderLoginFailure(UserRegistrationFailureInfo userRegistrationFailureInfo) {
        RLog.i(RLog.CALLBACK, "HomeFragment : onContinueSocialProviderLoginFailure");
        trackSocialProviderPage();
        hideProviderProgress();
        enableControls(true);
    }

    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        RLog.i(RLog.NETWORK_STATE, "HomeFragment :onNetWorkStateReceived state :" + isOnline);
        if (!isOnline) {
            hideProviderProgress();
        }
        handleUiState();
    }

    private void trackSocialProviderPage() {
        if (mProvider == null) {
            return;
        }
        if (mProvider.equalsIgnoreCase(SocialProvider.FACEBOOK)) {
            trackPage(AppTaggingPages.FACEBOOK);
        } else if (mProvider.equalsIgnoreCase(SocialProvider.GOOGLE_PLUS)) {
            trackPage(AppTaggingPages.GOOGLE_PLUS);
        } else if (mProvider.equalsIgnoreCase(SocialProvider.TWITTER)) {
            trackPage(AppTaggingPages.TWITTER);
        } else if (mProvider.equalsIgnoreCase(SocialProvider.WECHAT)) {
            trackPage(AppTaggingPages.WECHAT);
        }
    }


    private void showProgressDialog() {
        if (!(getActivity().isFinishing()) && (mProgressDialog != null)) mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int error_code = intent.getIntExtra(RegConstants.WECHAT_ERR_CODE, 0);
            String weChatCode = intent.getStringExtra(RegConstants.WECHAT_CODE);
            RLog.d("WECHAT", "BroadcastReceiver Got message: " + error_code + " " + weChatCode);
            switch (error_code) {
                case BaseResp.ErrCode.ERR_OK:
                    mWeChatCode = weChatCode;
                    ThreadUtils.postInMainThread(context, () -> EventHelper.getInstance().notifyEventOccurred(RegConstants.WECHAT_AUTH));
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    RLog.d("WECHAT", "WeChat - User canceled the request");
                    makeProgressInvisible();
                    hideProviderProgress();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    RLog.d("WECHAT", "WeChat - User denied the request");
                    makeProgressInvisible();
                    hideProviderProgress();
                    break;
            }
        }
    };

    private void registerWeChatApp() {
        mWeChatAppId = appConfiguration.getWeChatAppId();
        mWeChatAppSecret = appConfiguration.getWeChatAppSecret();
        RLog.d("WECHAT", "weChatId " + mWeChatAppId + " WechatSecrete " + mWeChatAppSecret);

        if (mWeChatAppId != null && mWeChatAppSecret != null) {
            mWeChatApi = WXAPIFactory.createWXAPI(getRegistrationFragment().getParentActivity(),
                    mWeChatAppId, false);
            mWeChatApi.registerApp(mWeChatAppSecret);
            isWeChatAppRegistered = mWeChatApi.registerApp(mWeChatAppId);
            LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver,
                    new IntentFilter(RegConstants.WE_CHAT_AUTH));
        }
    }
}
