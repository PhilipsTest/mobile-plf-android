
package com.philips.cdp.registration.settings;

import android.content.Context;
import android.util.Log;

import com.philips.cdp.localematch.LocaleMatchListener;
import com.philips.cdp.localematch.PILLocale;
import com.philips.cdp.localematch.PILLocaleManager;
import com.philips.cdp.localematch.enums.Catalog;
import com.philips.cdp.localematch.enums.LocaleMatchError;
import com.philips.cdp.localematch.enums.Platform;
import com.philips.cdp.localematch.enums.Sector;
import com.philips.cdp.registration.errormapping.CheckLocale;

public class LocaleMatchHelper implements LocaleMatchListener {

	private String mCountryCode;

	private String mLanguageCode;

	private Context mContext = null;

	private String LOG_TAG = "RegistrationAPI";

	RegistrationHelper mHelper = RegistrationHelper.getInstance();

	public LocaleMatchHelper(Context mContext, String mLanguageCode, String mCountryCode) {
		super();
		this.mCountryCode = mCountryCode;
		this.mLanguageCode = mLanguageCode;
		this.mContext = mContext;

		refreshLocale();
	}

	private void refreshLocale() {
		PILLocaleManager localeManager = new PILLocaleManager();
		localeManager.init(mContext, this);
		localeManager.refresh(mContext, mLanguageCode, mCountryCode);
	}

	@Override
	public void onLocaleMatchRefreshed(String locale) {

		PILLocaleManager manager = new PILLocaleManager();
		PILLocale pilLocaleInstance = manager.currentLocaleWithLanguageFallbackForPlatform(mContext,locale,
		        Platform.JANRAIN, Sector.B2C, Catalog.MOBILE);

		if (null != pilLocaleInstance) {
			Log.i(LOG_TAG,
			        "REGAPI, onLocaleMatchRefreshed from app RESULT = "
			                + pilLocaleInstance.getCountrycode()
			                + pilLocaleInstance.getLanguageCode()
			                + pilLocaleInstance.getLocaleCode());

			mHelper.getRegistrationSettings().initialiseConfigParameters(
			        pilLocaleInstance.getLanguageCode().toLowerCase() + "-"
			                + pilLocaleInstance.getCountrycode().toUpperCase());
		} else {
			Log.i(LOG_TAG, "REGAPI, onLocaleMatchRefreshed from app RESULT = NULL");
			String verifiedLocale = verifyInputLocale(mLanguageCode + "-" + mCountryCode);
			mHelper.getRegistrationSettings().initialiseConfigParameters(verifiedLocale);
		}

	}

	@Override
	public void onErrorOccurredForLocaleMatch(LocaleMatchError error) {
		Log.i(LOG_TAG, "REGAPI, onErrorOccurredForLocaleMatch error = " + error);
		String verifiedLocale = verifyInputLocale(mLanguageCode + "-" + mCountryCode);
		mHelper.getRegistrationSettings().initialiseConfigParameters(verifiedLocale);
	}


	private String verifyInputLocale(String locale) {
		CheckLocale checkLocale = new CheckLocale();
		String localeCode = checkLocale.checkLanguage(locale);

		if ("zh-TW".equals(localeCode)) {
			localeCode = "zh-HK";
		}
		return localeCode;
	}

}
