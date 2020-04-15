package com.philips.cdp.registration.ui.traditional;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.infra.ServiceDiscoveryWrapper;
import com.philips.cdp.registration.app.tagging.AppTaggingPages;
import com.philips.cdp.registration.configuration.AppConfiguration;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.events.EventHelper;
import com.philips.cdp.registration.events.EventListener;
import com.philips.cdp.registration.events.NetworkStateListener;
import com.philips.cdp.registration.events.SocialProvider;
import com.philips.cdp.registration.handlers.SocialLoginProviderHandler;
import com.philips.cdp.registration.listener.WeChatAuthenticationListener;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.LoginFailureNotification;
import com.philips.cdp.registration.ui.utils.NetworkUtility;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegPreferenceUtility;
import com.philips.cdp.registration.ui.utils.RegUtility;
import com.philips.cdp.registration.ui.utils.UIFlow;
import com.philips.cdp.registration.wechat.WeChatAuthenticator;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.pif.chi.datamodel.ConsentStates;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.philips.cdp.registration.app.tagging.AppTagging.trackPage;
import static com.philips.cdp.registration.ui.utils.RegConstants.SOCIAL_PROVIDER_FACEBOOK;
import static com.philips.cdp.registration.ui.utils.RegConstants.SOCIAL_PROVIDER_WECHAT;
import static com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface.AIL_HOME_COUNTRY;

public class HomePresenter implements NetworkStateListener, SocialLoginProviderHandler, EventListener {

    private String TAG = "HomePresenter";

    @Inject
    NetworkUtility networkUtility;

    @Inject
    AppConfiguration appConfiguration;

    @Inject
    ServiceDiscoveryInterface serviceDiscoveryInterface;

    @Inject
    ServiceDiscoveryWrapper serviceDiscoveryWrapper;

    @Inject
    User user;
    CountryUpdateReceiver countryUpdateReceiver;

    private HomeContract homeContract;


    HomePresenter(HomeContract homeContract, CallbackManager mCallbackManager) {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
        this.homeContract = homeContract;
        RegistrationHelper.getInstance().registerNetworkStateListener(this);
        EventHelper.getInstance()
                .registerEventNotification(RegConstants.JANRAIN_INIT_SUCCESS, this);
        EventHelper.getInstance()
                .registerEventNotification(RegConstants.JANRAIN_INIT_FAILURE, this);
    }


    void cleanUp() {
        homeContract.unRegisterWechatReceiver();
        RegistrationHelper.getInstance().unRegisterNetworkListener(this);
        EventHelper.getInstance().unregisterEventNotification(RegConstants.JANRAIN_INIT_SUCCESS,
                this);
        EventHelper.getInstance().unregisterEventNotification(RegConstants.JANRAIN_INIT_FAILURE,
                this);
    }

    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        if (isOnline) {
            homeContract.enableControlsOnNetworkConnectionArraival();
        } else {
            homeContract.enableControlsOnNetworkStatus();
        }
    }


    void updateHomeControls() {
        if (networkUtility.isNetworkAvailable()) {
            homeContract.enableControlsOnNetworkConnectionArraival();
        } else {
            homeContract.enableControlsOnNetworkStatus();
        }
    }

    void configureCountrySelection() {
        String mShowCountrySelection = appConfiguration.getShowCountrySelection();
        RLog.d(TAG, " Country Show Country Selection :" + mShowCountrySelection);
        if (mShowCountrySelection != null) {
            if (mShowCountrySelection.equalsIgnoreCase("false")) {
                homeContract.hideCountrySelctionLabel();
            } else {
                homeContract.showCountrySelctionLabel();
            }
        }
    }


    void initServiceDiscovery() {
        serviceDiscoveryInterface.getHomeCountry(new ServiceDiscoveryInterface.OnGetHomeCountryListener() {
            @Override
            public void onSuccess(String s, SOURCE source) {
                RLog.d(TAG, " getHomeCountry Success :" + s);
                String selectedCountryCode;
                if (RegUtility.supportedCountryList().contains(s.toUpperCase())) {
                    selectedCountryCode = s.toUpperCase();
                } else {
                    selectedCountryCode = RegUtility.getFallbackCountryCode();
                }
                serviceDiscoveryInterface.setHomeCountry(selectedCountryCode);
                homeContract.updateHomeCountry(selectedCountryCode);

            }

            @Override
            public void onError(ERRORVALUES errorvalues, String s) {
                RLog.e(TAG, "getHomeCountry : Country Error :" + s);
                String selectedCountryCode = RegUtility.getFallbackCountryCode();
                serviceDiscoveryInterface.setHomeCountry(selectedCountryCode);
                homeContract.updateHomeCountry(selectedCountryCode);
            }
        });
    }

    List<String> getProviders(String countryCode) {
        return RegistrationConfiguration.getInstance().getProvidersForCountry(countryCode);
    }

    private boolean isWeChatAppRegistered;
    private String mWeChatAppId;
    private String mWeChatAppSecret;
    private IWXAPI mWeChatApi;

    private String weChat = "WECHAT";


    void registerWeChatApp() {
        mWeChatAppId = appConfiguration.getWeChatAppId();
        mWeChatAppSecret = appConfiguration.getWeChatAppSecret();
        RLog.d(weChat, weChat + " mWeChatAppId " + mWeChatAppId + weChat + "Secret" + mWeChatAppSecret);

        if (mWeChatAppId != null && mWeChatAppSecret != null) {
            mWeChatApi = WXAPIFactory.createWXAPI(homeContract.getActivityContext(),
                    mWeChatAppId, false);
            mWeChatApi.registerApp(mWeChatAppSecret);
            isWeChatAppRegistered = mWeChatApi.registerApp(mWeChatAppId);
            homeContract.registerWechatReceiver();
        }
    }

    boolean isWeChatAuthenticate() {
        if (!mWeChatApi.isWXAppInstalled()) {
            homeContract.wechatAppNotInstalled();
            return false;
        }
        if (mWeChatApi.getWXAppSupportAPI() == 0) {
            homeContract.wechatAppNotSupported();
            return false;
        }
        return isWeChatAppRegistered;
    }

    void startWeChatAuthentication() {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "123456";
        mWeChatApi.sendReq(req);
    }


    void handleWeChatCode(String pWeChatCode) {
        RLog.d(TAG, "WECHAT : Code: " + pWeChatCode);
        WeChatAuthenticator weChatAuthenticator = new WeChatAuthenticator();
        weChatAuthenticator.getWeChatResponse(mWeChatAppId, mWeChatAppSecret, pWeChatCode,
                new WeChatAuthenticationListener() {
                    @Override
                    public void onSuccess(final JSONObject jsonObj) {
                        try {
                            final String token = jsonObj.getString("access_token");
                            final String openId = jsonObj.getString("openid");
                            RLog.d(TAG, "WECHAT : token " + token + " openid " + openId);
                            user.loginUserUsingSocialNativeProvider(homeContract.getActivityContext(),
                                    "wechat", token, openId, HomePresenter.this, "");
                        } catch (JSONException e) {
                            homeContract.wechatAuthenticationSuccessParsingError();
                            RLog.e(TAG, "WECHAT :handleWeChatCode : Error wechatAuthenticationSuccessParsingError" + e.getMessage());
                        }
                    }

                    @Override
                    public void onFail() {
                        homeContract.wechatAuthenticationFailError();
                        RLog.e(TAG, "WECHAT : handleWeChatCode : Error wechatAuthenticationFailError ");
                    }
                });
    }


    @Override
    public void onLoginFailedWithTwoStepError(final JSONObject prefilledRecord,
                                              final String socialRegistrationToken) {
        RLog.d(TAG, "Login failed with two step error" + "JSON OBJECT :"
                + prefilledRecord);
        EventBus.getDefault().post(new LoginFailureNotification());
        homeContract.createSocialAccount(prefilledRecord, socialRegistrationToken);
    }


    @Override
    public void onLoginFailedWithMergeFlowError(final String mergeToken, final String existingProvider,
                                                final String conflictingIdentityProvider, String conflictingIdpNameLocalized,
                                                String existingIdpNameLocalized, final String emailId) {

        EventBus.getDefault().post(new LoginFailureNotification());
        homeContract.mergeSocialAccount(existingProvider, mergeToken, conflictingIdentityProvider, emailId);


    }


    @Override
    public void onContinueSocialProviderLoginSuccess() {
        RLog.d(TAG, "onContinueSocialProviderLoginSuccess");
        completeRegistation();
        homeContract.updateUIState();

    }

    @Override
    public void onContinueSocialProviderLoginFailure(
            final UserRegistrationFailureInfo userRegistrationFailureInfo) {
        EventBus.getDefault().post(new LoginFailureNotification());
        homeContract.SocialLoginFailure(userRegistrationFailureInfo);

    }

    @Override
    public void onLoginSuccess() {
        homeContract.loginSuccess();
    }

    @Override
    public void onLoginFailedWithError(final UserRegistrationFailureInfo userRegistrationFailureInfo) {
        EventBus.getDefault().post(new LoginFailureNotification());
        homeContract.loginFailed(userRegistrationFailureInfo);
    }

    private HomePresenter.FLOWDELIGATE deligateFlow = HomePresenter.FLOWDELIGATE.DEFAULT;

    void setFlowDeligate(FLOWDELIGATE deligateFlow) {
        this.deligateFlow = deligateFlow;
    }

    void navigateToScreen() {

        if (isEmailAvailable() && isMobileNoAvailable() && !isEmailVerified()) {
            homeContract.naviagteToAccountActivationScreen();
            return;
        }
        if ((isEmailVerified() || isMobileVerified())) {
            completeRegistation();
        } else {
            if (isEmailAvailable()) {
                homeContract.naviagteToAccountActivationScreen();
            } else if (isMobileNoAvailable() && !isMobileVerified()) {
                homeContract.naviagteToMobileAccountActivationScreen();
            } else {
                homeContract.genericError();
            }
        }

    }


    void completeRegistation() {

        if (homeContract.getActivityContext() == null) {
            homeContract.loginFailed(null);
            return;
        }

        String emailorMobile;
        if (FieldsValidator.isValidEmail(user.getEmail())) {
            emailorMobile = user.getEmail();
        } else {
            emailorMobile = user.getMobile();
        }

       // RegistrationConfiguration.getInstance().isCustomOptin()


        if (emailorMobile != null
                && (RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired() &&
                RegPreferenceUtility.getPreferenceValue(homeContract.getActivityContext(), RegConstants.TERMS_N_CONDITIONS_ACCEPTED, emailorMobile))
                && (!RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired())
                && (RegistrationConfiguration.getInstance().isCustomOptoin() || RegistrationConfiguration.getInstance().isSkipOptin())
                && (RegUtility.getUiFlow() == UIFlow.FLOW_B)) {
            homeContract.registrationCompleted();
            return;
        }else  if (emailorMobile != null && ((RegistrationConfiguration.getInstance().isTermsAndConditionsAcceptanceRequired() &&
                !RegPreferenceUtility.getPreferenceValue(homeContract.getActivityContext(), RegConstants.TERMS_N_CONDITIONS_ACCEPTED, emailorMobile) || !user.getReceiveMarketingEmail())
                || (RegistrationConfiguration.getInstance().isPersonalConsentAcceptanceRequired() ))) {

            homeContract.navigateToAcceptTermsScreen();
            return;
        }
        homeContract.registrationCompleted();
    }

    boolean isNetworkAvailable() {
        return networkUtility.isNetworkAvailable();
    }

    void onSelectCountry(String countryName, String code) {
        setFlowDeligate(HomePresenter.FLOWDELIGATE.DEFAULT);
        if (countryUpdateReceiver != null)
            serviceDiscoveryInterface.unRegisterHomeCountrySet(countryUpdateReceiver);
        countryUpdateReceiver = new CountryUpdateReceiver(countryName);
        serviceDiscoveryInterface.registerOnHomeCountrySet(countryUpdateReceiver);
        if (networkUtility.isNetworkAvailable()) {
            serviceDiscoveryInterface.setHomeCountry(code);
        }
    }

    public class CountryUpdateReceiver extends BroadcastReceiver {
        private String countryName;

        public CountryUpdateReceiver(String countryName) {
            this.countryName = countryName;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ServiceDiscoveryInterface.AIL_SERVICE_DISCOVERY_HOMECOUNTRY_CHANGE_ACTION)) {
                String countryCode = (String) intent.getExtras().get(AIL_HOME_COUNTRY);
                Log.v(getClass() + "", "Home country changed to " + countryCode);
                RegistrationHelper.getInstance().setCountryCode(countryCode);
                homeContract.countryChangeStarted();
                getLocaleServiceDiscoveryByCountry(countryName);
            }
        }
    }

    void registerFaceBookCallBack() {
        homeContract.getURFaceBookUtility().registerFaceBookCallBack();
    }

    public void startAccessTokenAuthForFacebook() {
        if (AccessToken.getCurrentAccessToken() != null) {
            homeContract.getURFaceBookUtility().startAccessTokenAuthForFacebook(user, homeContract.getActivityContext(), this, AccessToken.getCurrentAccessToken().getToken(), null);
        } else {
            RLog.d(TAG, "onFaceBookEmailReceived : Facebook AccessToken null");
            homeContract.genericError();
        }
    }

    public enum FLOWDELIGATE {
        DEFAULT, CREATE, LOGIN, SOCIALPROVIDER;
    }

    void setProvider(String provider) {
        this.provider = provider;
    }

    boolean isMergePossible(String provider) {
        return user.handleMergeFlowError(provider);
    }

    String getProvider() {
        return provider;
    }

    String provider;

    @Override
    public void onEventReceived(String event) {
        RLog.d(TAG, "HomeFragment :onCounterEventReceived" +
                " isHomeFragment :onCounterEventReceived is : " + event);
        if (RegConstants.JANRAIN_INIT_SUCCESS.equals(event)) {
            homeContract.initSuccess();
            if (deligateFlow == FLOWDELIGATE.LOGIN) {
                homeContract.navigateToLogin();
            }
            if (deligateFlow == FLOWDELIGATE.CREATE) {
                homeContract.navigateToCreateAccount();
            }
            if (deligateFlow == FLOWDELIGATE.SOCIALPROVIDER) {
                homeContract.handleBtnClickableStates(false);
                if (provider.equalsIgnoreCase(SOCIAL_PROVIDER_WECHAT)) {
                    if (isWeChatAuthenticate()) {
                        homeContract.startWeChatAuthentication();
                    } else {
                        homeContract.switchToControlView();
                    }
                } else if (RegistrationConfiguration.getInstance().isFacebookSDKSupport() && provider.equalsIgnoreCase(SOCIAL_PROVIDER_FACEBOOK)) {
                    homeContract.startFaceBookLogin();
                } else {
                    homeContract.socialProviderLogin();
                }
            }
            deligateFlow = FLOWDELIGATE.DEFAULT;
        } else if (RegConstants.JANRAIN_INIT_FAILURE.equals(event)) {
            homeContract.initFailed();
            deligateFlow = FLOWDELIGATE.DEFAULT;
        }
    }

    void startSocialLogin() {
        user.loginUserUsingSocialProvider(homeContract.getActivityContext(), provider, this, null);
    }

    void trackSocialProviderPage() {
        if (provider == null) {
            return;
        }
        if (provider.equalsIgnoreCase(SocialProvider.FACEBOOK)) {
            trackPage(AppTaggingPages.FACEBOOK);
        } else if (provider.equalsIgnoreCase(SocialProvider.GOOGLE_PLUS)) {
            trackPage(AppTaggingPages.GOOGLE_PLUS);
        } else if (provider.equalsIgnoreCase(SocialProvider.TWITTER)) {
            trackPage(AppTaggingPages.TWITTER);
        } else if (provider.equalsIgnoreCase(SocialProvider.WECHAT)) {
            trackPage(AppTaggingPages.WECHAT);
        }
    }


    BroadcastReceiver getMessageReceiver() {
        return messageReceiver;
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int error_code = intent.getIntExtra(RegConstants.WECHAT_ERR_CODE, 0);
            String weChatCode = intent.getStringExtra(RegConstants.WECHAT_CODE);
            RLog.d(TAG, "WECHAT :BroadcastReceiver Got message: " + error_code + " " + weChatCode);
            switch (error_code) {
                case BaseResp.ErrCode.ERR_OK:
                    if (weChatCode != null) {
                        homeContract.startWeChatLogin(weChatCode);
                    } else {
                        RLog.d(TAG, "WECHAT : errorcode = " + weChatCode);
                    }
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    homeContract.wechatAutheticationCanceled();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    homeContract.wechatAutheticationCanceled();
                    break;
            }
        }
    };


    private boolean isEmailAvailable() {
        return user.getEmail() != null && FieldsValidator.isValidEmail(user.getEmail());
    }


    private boolean isMobileNoAvailable() {
        return user.getMobile() != null && FieldsValidator.isValidMobileNumber(user.getMobile());
    }

    boolean isEmailVerified() {
        return user.isEmailVerified();
    }

    boolean isMobileVerified() {
        return user.isMobileVerified();
    }


    private void getLocaleServiceDiscovery(final String countryName) {
        serviceDiscoveryWrapper.getServiceLocaleWithLanguagePreferenceSingle("userreg.janrain.api.v2")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(String verificationUrl) {
                        RLog.d(TAG, "getLocaleServiceDiscovery onSuccess verificationUrl : " + verificationUrl);
                        if (!verificationUrl.isEmpty()) {
                            homeContract.updateAppLocale(verificationUrl, countryName);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        RLog.d(TAG, "getLocaleServiceDiscovery : onError ");
                        getLocaleServiceDiscoveryByCountry(countryName);
                    }
                });
    }

    private void getLocaleServiceDiscoveryByCountry(String countryName) {
        serviceDiscoveryWrapper.getServiceLocaleWithCountryPreferenceSingle("userreg.janrain.api.v2")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(String verificationUrl) {
                        RLog.d(TAG, "getLocaleServiceDiscoveryByCountry onSuccess ");
                        homeContract.updateAppLocale(verificationUrl, countryName);
                    }

                    @Override
                    public void onError(Throwable e) {
                        RLog.e(TAG, "getLocaleServiceDiscoveryByCountry : onError " + e.getMessage());
                        homeContract.localeServiceDiscoveryFailed();
                    }
                });
    }


}
