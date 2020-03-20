package com.philips.cdp.registration.settings;

import com.janrain.android.Jump;
import com.janrain.android.JumpConfig;
import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.configuration.ClientIDConfiguration;
import com.philips.cdp.registration.configuration.Configuration;
import com.philips.cdp.registration.configuration.HSDPConfiguration;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.events.EventHelper;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.ThreadUtils;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;


public class RegistrationSettingsURL extends RegistrationSettings {

    private String TAG = "RegistrationSettingsURL";

    @Inject
    HSDPConfiguration hsdpConfiguration;

    @Inject
    ServiceDiscoveryInterface serviceDiscoveryInterface;

    @Inject
    SecureStorageInterface secureStorage;

    public RegistrationSettingsURL() {
        RegistrationConfiguration.getInstance().getComponent().inject(this);
    }

    private static final String PRODUCT_REGISTER_URL = "https://acc.philips.co.uk/prx/registration/";
    private static final String PRODUCT_REGISTER_LIST_URL = "https://acc.philips.co.uk/prx/registration.registeredProducts/";


    private static final String EVAL_PRX_RESEND_CONSENT_URL = "https://acc.usa.philips.com/prx/registration/resendConsentMail";

    private static final String DEV_PRX_RESEND_CONSENT_URL = "https://dev.philips.com/prx/registration/resendConsentMail";


    private static final String PROD_PRX_RESEND_CONSENT_URL = "https://www.usa.philips.com/prx/registration/resendConsentMail";

    private static final String PROD_PRODUCT_REGISTER_URL = "https://www.philips.co.uk/prx/registration/";

    private static final String PROD_PRODUCT_REGISTER_LIST_URL = "https://www.philips.co.uk/prx/registration.registeredProducts/";

    private static final String TEST_PRX_RESEND_CONSENT_URL = "https://tst.usa.philips.com/prx/registration/resendConsentMail";

    private static final String HSDP_BASE_URL_SERVICE_ID = "userreg.hsdp.userserv";

    private static final String STAGE_PRODUCT_REGISTER_URL = "https://acc.philips.co.uk/prx/registration/";

    private static final String STAGE_PRODUCT_REGISTER_LIST_URL = "https://acc.philips.co.uk/prx/registration.registeredProducts/";

    private static final String STAGE_PRX_RESEND_CONSENT_URL = "https://acc.usa.philips.com/prx/registration/resendConsentMail";

    private static final String EVAL_CAPTURE_FLOW_VERSION = "HEAD";

    private JumpConfig jumpConfig;

    private String langCode;

    private String countryCode;

    private static boolean isMobileFlow;

    /**
     * {@code initialiseConfigParameters} method builds configuration for information in {@code EvalRegistrationSettings}
     *
     * @param locale used to add the requested locale to the configuration
     */
    @Override
    public void initialiseConfigParameters(final String locale) {
        jumpConfig = new JumpConfig();
        jumpConfig.captureClientId = mCaptureClientId;
        jumpConfig.captureFlowName = "standard";
        jumpConfig.captureTraditionalRegistrationFormName = "registrationForm";
        jumpConfig.captureEnableThinRegistration = false;
        jumpConfig.captureSocialRegistrationFormName = "socialRegistrationForm";
        jumpConfig.captureForgotPasswordFormName = "forgotPasswordForm";
        jumpConfig.captureEditUserProfileFormName = "editProfileForm";
        jumpConfig.captureResendEmailVerificationFormName = "resendVerificationForm";
        jumpConfig.captureTraditionalSignInFormName = "userInformationForm";
        jumpConfig.traditionalSignInType = Jump.TraditionalSignInType.EMAIL;
        jumpConfig.captureFlowVersion = EVAL_CAPTURE_FLOW_VERSION;


        initializePRXLinks(RegistrationConfiguration.getInstance().getRegistrationEnvironment());


        String localeArr[] = locale.split("-");

        if (localeArr != null && localeArr.length > 1) {
            langCode = localeArr[0];
            countryCode = localeArr[1];
        } else {
            langCode = "en";
            countryCode = "US";
        }
        initServiceDiscovery(locale);
    }


    private void initializePRXLinks(String registrationEnv) {
        if (registrationEnv == null) {
            mProductRegisterUrl = PRODUCT_REGISTER_URL;
            mProductRegisterListUrl = PRODUCT_REGISTER_LIST_URL;
            mResendConsentUrl = EVAL_PRX_RESEND_CONSENT_URL;
            return;
        }
        if (registrationEnv.equalsIgnoreCase(Configuration.DEVELOPMENT.getValue())) {
            mProductRegisterUrl = PRODUCT_REGISTER_URL;
            mProductRegisterListUrl = PRODUCT_REGISTER_LIST_URL;
            mResendConsentUrl = DEV_PRX_RESEND_CONSENT_URL;
            return;
        }
        if (registrationEnv.equalsIgnoreCase(Configuration.PRODUCTION.getValue())) {
            mProductRegisterUrl = PROD_PRODUCT_REGISTER_URL;
            mProductRegisterListUrl = PROD_PRODUCT_REGISTER_LIST_URL;
            mResendConsentUrl = PROD_PRX_RESEND_CONSENT_URL;
            return;
        }
        if (registrationEnv.equalsIgnoreCase(Configuration.STAGING.getValue())) {
            mProductRegisterUrl = STAGE_PRODUCT_REGISTER_URL;
            mProductRegisterListUrl = STAGE_PRODUCT_REGISTER_LIST_URL;
            mResendConsentUrl = STAGE_PRX_RESEND_CONSENT_URL;
            return;
        }
        if (registrationEnv.equalsIgnoreCase(Configuration.TESTING.getValue())) {
            mProductRegisterUrl = PRODUCT_REGISTER_URL;
            mProductRegisterListUrl = PRODUCT_REGISTER_LIST_URL;
            mResendConsentUrl = TEST_PRX_RESEND_CONSENT_URL;
        }
    }

    private void initServiceDiscovery(final String locale) {

        ArrayList<String> serviceIdList = new ArrayList<>();
        serviceIdList.add("userreg.janrain.api.v2");
        serviceIdList.add("userreg.landing.emailverif");
        serviceIdList.add("userreg.landing.resetpass");
        serviceIdList.add("userreg.janrain.cdn.v2");
        serviceIdList.add("userreg.janrain.engage.v2");
        serviceIdList.add("userreg.smssupported");
        serviceIdList.add(HSDP_BASE_URL_SERVICE_ID);

        serviceDiscoveryInterface.getServicesWithCountryPreference(serviceIdList, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> resultMap) {

                setHSDPBaseUrl(resultMap);

                ServiceDiscoveryService serviceDiscoveyService = resultMap.get("userreg.janrain.api.v2");
                if (serviceDiscoveyService != null && serviceDiscoveyService.getConfigUrls() != null) {
                    String urlLocal = serviceDiscoveyService.getConfigUrls();
                    String janrainURL = urlLocal.substring(8);//Please don't remove this line.\

                    ClientIDConfiguration clientIDConfiguration = new ClientIDConfiguration();
                    if (janrainURL.equalsIgnoreCase("philips.capture.cn.janrain.com")) {
                        jumpConfig.captureDomain = "philips-cn.capture.cn.janrain.com";
                        jumpConfig.engageAppId = clientIDConfiguration.getEngageId(RegistrationConfiguration.getInstance().getRegistrationEnvironment()+"_"+countryCode);
                        jumpConfig.captureAppId = clientIDConfiguration.getCaptureId(RegistrationConfiguration.getInstance().getRegistrationEnvironment()+"_"+countryCode);
                    } else if(countryCode.equalsIgnoreCase("CN")) {
                        jumpConfig.captureDomain = janrainURL;
                        jumpConfig.engageAppId = clientIDConfiguration.getEngageId(RegistrationConfiguration.getInstance().getRegistrationEnvironment()+"_"+countryCode);
                        jumpConfig.captureAppId = clientIDConfiguration.getCaptureId(RegistrationConfiguration.getInstance().getRegistrationEnvironment()+"_"+countryCode);
                    }else if(countryCode.equalsIgnoreCase("RU")) {
                        jumpConfig.captureDomain = janrainURL;
                        jumpConfig.engageAppId = clientIDConfiguration.getEngageId(RegistrationConfiguration.getInstance().getRegistrationEnvironment()+"_"+countryCode);
                        jumpConfig.captureAppId = clientIDConfiguration.getCaptureId(RegistrationConfiguration.getInstance().getRegistrationEnvironment()+"_"+countryCode);
                    }else {
                        jumpConfig.captureDomain = janrainURL;
                        jumpConfig.engageAppId = clientIDConfiguration.getEngageId(RegistrationConfiguration.getInstance().getRegistrationEnvironment());
                        jumpConfig.captureAppId = clientIDConfiguration.getCaptureId(RegistrationConfiguration.getInstance().getRegistrationEnvironment());
                    }
                    RLog.d(TAG, " onSuccess  : userreg.janrain.api :" + urlLocal);
                    if (jumpConfig.engageAppId == null || jumpConfig.captureAppId == null) {
                        ThreadUtils.postInMainThread(mContext, () -> EventHelper.getInstance().notifyEventOccurred(RegConstants.JANRAIN_INIT_FAILURE));
                        return;
                    }

                    RLog.d(TAG, " onSuccess  : userreg.engageid :" + clientIDConfiguration.getEngageId(urlLocal));
                    RLog.d(TAG, " onSuccess  : userreg.captureid :" + clientIDConfiguration.getCaptureId(urlLocal));

                } else {
                    RLog.e(TAG, " onError  : userreg.janrain.api");
                    ThreadUtils.postInMainThread(mContext, () -> EventHelper.getInstance().notifyEventOccurred(RegConstants.JANRAIN_INIT_FAILURE));
                    return;
                }

                serviceDiscoveyService = resultMap.get("userreg.landing.emailverif");
                if (serviceDiscoveyService != null && serviceDiscoveyService.getConfigUrls() != null) {
                    jumpConfig.captureRedirectUri = serviceDiscoveyService.getConfigUrls();
                    RLog.d(TAG, " onSuccess  : userreg.landing.emailverif :"
                            + serviceDiscoveyService.getConfigUrls());
                    RLog.d(TAG, " onSuccess  : userreg.landing.emailverif :"
                            + jumpConfig.captureRedirectUri);

                } else {
                    RLog.d(TAG, " onError  : userreg.landing.emailverif :");
                    ThreadUtils.postInMainThread(mContext, () -> EventHelper.getInstance().notifyEventOccurred(RegConstants.JANRAIN_INIT_FAILURE));
                    return;
                }

                serviceDiscoveyService = resultMap.get("userreg.landing.resetpass");
                if (serviceDiscoveyService != null && serviceDiscoveyService.getConfigUrls() != null) {
                    String modifiedUrl = serviceDiscoveyService.getConfigUrls().
                            replaceAll("c-w", "myphilips");
                    //https://philips-cn.capture.cn.janrain.com/
                    jumpConfig.captureRecoverUri = modifiedUrl;
                    RLog.d(TAG, " onSuccess  : userreg.landing.resetpass :"
                            + modifiedUrl);
                    RLog.d(TAG, " onSuccess  : userreg.landing.resetpass :"
                            + jumpConfig.captureRecoverUri);
                } else {
                    RLog.d(TAG, " onError  : userreg.landing.resetpass : ");
                    ThreadUtils.postInMainThread(mContext, () -> EventHelper.getInstance().notifyEventOccurred(RegConstants.JANRAIN_INIT_FAILURE));
                    return;
                }

                serviceDiscoveyService = resultMap.get("userreg.janrain.cdn.v2");
                if (serviceDiscoveyService != null && serviceDiscoveyService.getConfigUrls() != null) {
                    RLog.d(TAG, " onSuccess  : userreg.janrain.cdn :" +
                            serviceDiscoveyService.getConfigUrls());
                    jumpConfig.downloadFlowUrl = serviceDiscoveyService.getConfigUrls();
                } else {
                    RLog.d(TAG, " onError  : userreg.janrain.cdn : ");
                    ThreadUtils.postInMainThread(mContext, () -> EventHelper.getInstance().notifyEventOccurred(RegConstants.JANRAIN_INIT_FAILURE));
                    return;
                }

                serviceDiscoveyService = resultMap.get("userreg.smssupported");
                if (serviceDiscoveyService != null && serviceDiscoveyService.getConfigUrls() != null) {
                    String smsSupport = serviceDiscoveyService.getConfigUrls();
                    setMobileFlow(true);
                    RLog.d(TAG, " onSuccess  : userreg.smssupported :" +
                            smsSupport);
                    jumpConfig.captureLocale = locale;
                    //Must for mobile create account
                    jumpConfig.captureTraditionalSignInFormName = "userInformationMobileForm";
                } else {
                    RLog.d(TAG, " onError  : userreg.smssupported :" +
                            "Service Deiscover inis at non China local");
                    setMobileFlow(false);
                    jumpConfig.captureLocale = locale;
                    mPreferredCountryCode = countryCode;
                    mPreferredLangCode = langCode;
                    initialize();
                    return;
                }

                serviceDiscoveyService = resultMap.get("userreg.janrain.engage.v2");
                if (serviceDiscoveyService != null && serviceDiscoveyService.getConfigUrls() != null) {
                    RLog.d(TAG, " onSuccess  : userreg.janrain.engage :" + serviceDiscoveyService.getConfigUrls());
                    jumpConfig.engageAppUrl = serviceDiscoveyService.getConfigUrls().substring(8);

                    mPreferredCountryCode = countryCode;
                    mPreferredLangCode = langCode;
                    initialize();
                    RLog.d(TAG, " MobileFlow : " + isMobileFlow());
                } else {
                    RLog.d(TAG, " onError  : userreg.janrain.engage : ");
                    initialize();
                    return;
                }
            }

            private void initialize() {
                try {
                    RLog.d(TAG, "jumpConfig : " + jumpConfig.toString());
                    String s = secureStorage.fetchValueForKey("jr_capture_flow", new SecureStorageInterface.SecureStorageError());
                    Jump.reinitialize(mContext, jumpConfig);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof RuntimeException && (!(e instanceof IllegalStateException))) {
                        mContext.deleteFile("jr_capture_flow");
                        Jump.reinitialize(mContext, jumpConfig);
                    }
                }
            }

            @Override
            public void onError(ERRORVALUES errorvalues, String s) {
                RLog.d(TAG, " onError  : RegistrationConfigurationFailed:ServiceDiscovery " + s);
                AppTagging.trackAction(AppTagingConstants.SEND_DATA, AppTagingConstants.SPECIAL_EVENTS,
                        AppTagingConstants.FAILURE_SERVICEDISCOVERY + s);

                ThreadUtils.postInMainThread(mContext, () -> EventHelper.getInstance().notifyEventOccurred(RegConstants.JANRAIN_INIT_FAILURE));
            }
        },null);


    }

    private void setHSDPBaseUrl(Map<String, ServiceDiscoveryService> resultMap) {
        ServiceDiscoveryService serviceDiscoveyService;
        serviceDiscoveyService = resultMap.get(HSDP_BASE_URL_SERVICE_ID);
        if (serviceDiscoveyService != null && serviceDiscoveyService.getConfigUrls() != null) {
            RLog.d(TAG, "setHSDPBaseUrl: serviceDiscovery " + serviceDiscoveyService.getConfigUrls() + " map " + resultMap);
            hsdpConfiguration.setBaseUrlServiceDiscovery(serviceDiscoveyService.getConfigUrls());
        }
    }

    public boolean isMobileFlow() {
        return isMobileFlow;
    }

    public static void setMobileFlow(boolean mobileFlow) {
        isMobileFlow = mobileFlow;
    }
}
