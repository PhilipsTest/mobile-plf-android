
package com.philips.cdp.registration.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.philips.cdp.localematch.LocaleMatchListener;
import com.philips.cdp.localematch.PILLocale;
import com.philips.cdp.localematch.PILLocaleManager;
import com.philips.cdp.localematch.enums.Catalog;
import com.philips.cdp.localematch.enums.LocaleMatchError;
import com.philips.cdp.localematch.enums.Platform;
import com.philips.cdp.localematch.enums.Sector;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.errormapping.CheckLocale;

public abstract class RegistrationSettings implements LocaleMatchListener {

    private static final String FLOW_STANDARD = "standard";

    private static final String FLOW_COPPA = "coppa";

    protected String mProductRegisterUrl = null;

    protected String mProductRegisterListUrl = null;

    protected String mPreferredCountryCode = null;

    protected String mPreferredLangCode = null;

    protected String mResendConsentUrl = null;

    protected String mRegisterCoppaActivationUrl = null;

    protected String mRegisterBaseCaptureUrl = null;

    public String REGISTRATION_USE_PRODUCTION = "REGISTRATION_USE_PRODUCTION";

    public String REGISTRATION_USE_EVAL = "REGISTRATION_USE_EVAL";

    public String REGISTRATION_USE_DEVICE = "REGISTRATION_USE_DEVICE";

    public static final String REGISTRATION_API_PREFERENCE = "REGAPI_PREFERENCE";

    public static final String MICROSITE_ID = "microSiteID";

    protected String mCountryCode;

    protected String mLanguageCode;
    protected Context mContext = null;
    String mCaptureClientId = null;
    String mLocale = null;

    public  void intializeRegistrationSettings(Context context, String captureClientId,
                                                       String locale){
        storeMicrositeId(context);

        mCaptureClientId = captureClientId;
        mLocale = locale;
        mContext = context;

        assignLanguageAndCountryCode(locale);

        refreshLocale(this);
    }

    public abstract void initialiseConfigParameters(String locale);

    public String getProductRegisterUrl() {
        return mProductRegisterUrl;
    }

    public String getProductRegisterListUrl() {
        return mProductRegisterListUrl;
    }

    public String getPreferredCountryCode() {
        return mPreferredCountryCode;
    }

    public String getPreferredLangCode() {
        return mPreferredLangCode;
    }

    public String getFlowName() {
        if (RegistrationConfiguration.getInstance().isCoppaFlow()) {
            return FLOW_COPPA;
        } else {
            return FLOW_STANDARD;
        }

    }

    public String getRegisterCoppaActivationUrl() {
        return mRegisterCoppaActivationUrl;
    }

    public String getResendConsentUrl() {
        return mResendConsentUrl;
    }

    public String getmRegisterBaseCaptureUrl() {
        return mRegisterBaseCaptureUrl;
    }

    protected void storeMicrositeId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(REGISTRATION_API_PREFERENCE, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(MICROSITE_ID, RegistrationConfiguration.getInstance().getPilConfiguration().getMicrositeId());
        editor.commit();
    }

    protected void assignLanguageAndCountryCode(String locale) {
        String localeArr[] = locale.split("_");

        if (localeArr != null && localeArr.length > 1) {
            mLanguageCode = localeArr[0].toLowerCase();
            mCountryCode = localeArr[1].toUpperCase();
        } else {
            mLanguageCode = "en";
            mCountryCode = "US";
        }
    }


    @Override
    public void onLocaleMatchRefreshed(String locale) {

        PILLocaleManager manager = new PILLocaleManager();


        PILLocale pilLocaleInstance = null;
        if (RegistrationConfiguration.getInstance().isCoppaFlow()) {
         /*  pilLocaleInstance = manager.currentLocaleWithLanguageFallbackForPlatform(mContext, locale,
                    Platform.JANRAIN, Sector.B2C, Catalog.COPPA);*/
        } else {
            pilLocaleInstance = manager.currentLocaleWithLanguageFallbackForPlatform(mContext, locale,
                    Platform.JANRAIN, Sector.B2C, Catalog.MOBILE);
        }


        if (null != pilLocaleInstance) {
            Log.i("LolaleMatch",
                    "REGAPI, onLocaleMatchRefreshed from app RESULT = "
                            + pilLocaleInstance.getCountrycode()
                            + pilLocaleInstance.getLanguageCode()
                            + pilLocaleInstance.getLocaleCode());

            initialiseConfigParameters(
                    pilLocaleInstance.getLanguageCode().toLowerCase() + "-"
                            + pilLocaleInstance.getCountrycode().toUpperCase());
        } else {
            Log.i("LolaleMatch", "REGAPI, onLocaleMatchRefreshed from app RESULT = NULL");
            String localeCode = mLanguageCode + "-" + mCountryCode;
            if ("zh-TW".equals(localeCode)) {
                localeCode = "zh-HK";
            }
            initialiseConfigParameters(localeCode);
        }

    }

    @Override
    public void onErrorOccurredForLocaleMatch(LocaleMatchError error) {
        Log.i("LolaleMatch", "REGAPI, onErrorOccurredForLocaleMatch error = " + error);
        String verifiedLocale = verifyInputLocale(mLanguageCode + "-" + mCountryCode);
        initialiseConfigParameters(verifiedLocale);
    }


    private String verifyInputLocale(String locale) {
        CheckLocale checkLocale = new CheckLocale();
        String localeCode = checkLocale.checkLanguage(locale);

        if ("zh-TW".equals(localeCode)) {
            localeCode = "zh-HK";
        }
        return localeCode;
    }

    public void refreshLocale(LocaleMatchListener localeMatchListener) {
        PILLocaleManager localeManager = new PILLocaleManager();
        localeManager.init(mContext, localeMatchListener);
        localeManager.refresh(mContext, mLanguageCode, mCountryCode);
    }
}
