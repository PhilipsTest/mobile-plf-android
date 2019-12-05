/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.configuration.Configuration;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.dao.Country;
import com.philips.cdp.registration.events.SocialProvider;
import com.philips.platform.appinfra.abtestclient.ABTestClientInterface;
import com.philips.platform.appinfra.utility.AIUtility;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class RegUtility {
    private static String TAG = "RegUtility";

    private static long createAccountStartTime;

    private static UIFlow uiFlow;

    public static void setUiFlow(UIFlow uiFlowOverride) {
        uiFlow = uiFlowOverride;
    }

    public static int getCheckBoxPadding(Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        int padding;
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN ||
                android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1) {
            padding = (int) (35 * scale + 0.5f);
        } else {
            padding = (int) (10 * scale + 0.5f);
        }
        return padding;
    }


    //Temp: Code (For the existing usage on other fragments)

    /**
     * @param termsAndConditionsAcceptance
     * @param activity
     * @param termsAndConditionClickListener
     * @since 1.0.0
     */
    public static void linkifyTermsandCondition(
            TextView termsAndConditionsAcceptance,
            final Activity activity, ClickableSpan termsAndConditionClickListener) {

        String termsAndCondition = activity.getString(R.string.USR_DLS_TermsAndConditionsAcceptanceText);
        String acceptTermsAndCondition = activity.getString(R.string.USR_TermsAndConditionsText);
        termsAndCondition = String.format(termsAndCondition, acceptTermsAndCondition);
        termsAndConditionsAcceptance.setText(termsAndCondition);
        String terms = activity.getString(R.string.USR_TermsAndConditionsText);
        setupLinkify(termsAndConditionsAcceptance, activity, termsAndConditionClickListener, termsAndCondition, terms);
    }

    /**
     * @param termsAndConditionsAcceptance
     * @param activity
     * @param termsAndConditionClickListener
     * @since 1.0.0
     */
    public static void linkifyTermsandCondition(
            CheckBox termsAndConditionsAcceptance,
            final Activity activity, ClickableSpan termsAndConditionClickListener) {

        String termsAndCondition = activity.getString(R.string.USR_DLS_TermsAndConditionsAcceptanceText);
        String acceptTermsAndCondition = "\n" + activity.getString(R.string.USR_DLS_TermsAndConditionsText);
        termsAndCondition = String.format(termsAndCondition, acceptTermsAndCondition);
        termsAndConditionsAcceptance.setText(termsAndCondition);
        String terms = activity.getString(R.string.USR_DLS_TermsAndConditionsText);
        setupLinkify(termsAndConditionsAcceptance, activity, termsAndConditionClickListener, termsAndCondition, terms);
    }

    /**
     * @param receivePhilipsNewsView
     * @param activity
     * @param receivePhilipsNewsClickListener
     * @since 1.0.0
     */
    public static void linkifyPhilipsNews(TextView receivePhilipsNewsView,
                                          final Activity activity, ClickableSpan
                                                  receivePhilipsNewsClickListener) {
        String receivePhilipsNews = activity.getString(R.string.USR_DLS_OptIn_Promotional_Message_Line1);
        String doesThisMeanStr = activity.getString(R.string.USR_Receive_Philips_News_Meaning_lbltxt);
        receivePhilipsNews = receivePhilipsNews + "\n" + doesThisMeanStr;
        receivePhilipsNewsView.setText(receivePhilipsNews);
        String link = activity.getString(R.string.USR_Receive_Philips_News_Meaning_lbltxt);
        setupLinkify(receivePhilipsNewsView, activity, receivePhilipsNewsClickListener, receivePhilipsNews, link);
    }

    /**
     * @param receivePersonalConsent
     * @param activity
     * @param receivePersonalConsentClickListener
     * @param contentConfiguration
     * @since 1904
     */
    public static void linkifyPersonalConsent(TextView receivePersonalConsent,
                                              final Activity activity, ClickableSpan
                                                      receivePersonalConsentClickListener, RegistrationContentConfiguration contentConfiguration) {
        ConsentDefinition personalConsentDefinition = contentConfiguration.getPersonalConsentDefinition();
        if (personalConsentDefinition == null) return;
        String receivePhilipsNews = activity.getString(personalConsentDefinition.getText());
        String doesThisMeanStr = activity.getString(R.string.USR_Receive_Philips_News_Meaning_lbltxt);
        receivePhilipsNews = receivePhilipsNews + "\n" + doesThisMeanStr;
        receivePersonalConsent.setText(receivePhilipsNews);
        String link = activity.getString(R.string.USR_Receive_Philips_News_Meaning_lbltxt);
        setupLinkify(receivePersonalConsent, activity, receivePersonalConsentClickListener, receivePhilipsNews, link);
    }

    /**
     * @param receivePhilipsNewsView
     * @param activity
     * @param receivePhilipsNewsClickListener
     * @since 1.0.0
     */
    public static void linkifyPhilipsNewsMarketing(TextView receivePhilipsNewsView,
                                                   final Activity activity, ClickableSpan
                                                           receivePhilipsNewsClickListener) {
        String receivePhilipsNews = activity.getString(R.string.USR_DLS_OptIn_Promotional_Message_Line1);
        String doesThisMeanStr = activity.getString(R.string.USR_Receive_Philips_News_Meaning_lbltxt);
        receivePhilipsNews = receivePhilipsNews + "\n" + doesThisMeanStr;
        receivePhilipsNewsView.setText(receivePhilipsNews);
        String link = "\n" + activity.getString(R.string.USR_Receive_Philips_News_Meaning_lbltxt);
        setupLinkify(receivePhilipsNewsView, activity, receivePhilipsNewsClickListener, receivePhilipsNews, link);
    }

    /**
     * @param accountSettingPhilipsNews
     * @param activity
     * @param accountSettingsPhilipsClickListener
     * @param moreAccountSettings
     * @param link
     * @since 1.0.0
     */
    private static void setupLinkify(TextView accountSettingPhilipsNews, Activity activity,
                                     ClickableSpan accountSettingsPhilipsClickListener,
                                     String moreAccountSettings, String link) {
        SpannableString spanableString = new SpannableString(moreAccountSettings);
        int termStartIndex = moreAccountSettings.toLowerCase().indexOf(
                link.toLowerCase());
        spanableString.setSpan(accountSettingsPhilipsClickListener, termStartIndex,
                termStartIndex + link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //   removeUnderlineFromLink(spanableString);
        accountSettingPhilipsNews.setText(spanableString);
        accountSettingPhilipsNews.setMovementMethod(LinkMovementMethod.getInstance());
        accountSettingPhilipsNews.setLinkTextColor(ContextCompat.getColor
                (activity, R.color.reg_hyperlink_highlight_color));
        accountSettingPhilipsNews.setHighlightColor(ContextCompat.getColor
                (activity, android.R.color.transparent));
    }

    /**
     * @param spanableString
     * @since 1.0.0
     */
    private static void removeUnderlineFromLink(SpannableString spanableString) {
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

    /**
     * @param registrationEnv
     * @return Configuration
     * @since 1.0.0
     */

    public static Configuration getConfiguration(String registrationEnv) {
        if (registrationEnv == null) {
            return Configuration.EVALUATION;
        }
        if (registrationEnv.equalsIgnoreCase(Configuration.DEVELOPMENT.getValue()))
            return Configuration.DEVELOPMENT;

        if (registrationEnv.equalsIgnoreCase(Configuration.PRODUCTION.getValue()))
            return Configuration.PRODUCTION;

        if (registrationEnv.equalsIgnoreCase(Configuration.STAGING.getValue()))
            return Configuration.STAGING;

        if (registrationEnv.equalsIgnoreCase(Configuration.TESTING.getValue()))
            return Configuration.TESTING;

        return Configuration.EVALUATION;
    }

    /**
     * @return UIFlow
     * @since 1.0.0
     */
    public static UIFlow getUiFlow() {

        if (uiFlow != null) {
            return uiFlow;
        }

        ABTestClientInterface abTestClientInterface = RegistrationConfiguration.getInstance().getComponent().getAbTestClientInterface();
        String flowType = abTestClientInterface.getTestValue(RegConstants.DOT_RECEIVE_MARKETING_OPT_IN, UIFlow.FLOW_A.getValue(),
                ABTestClientInterface.UPDATETYPE.APP_UPDATE);
        if (flowType.equalsIgnoreCase(UIFlow.FLOW_B.getValue())) {
            return UIFlow.FLOW_B;
        }
        return UIFlow.FLOW_A;
    }

    /**
     * @param providers
     * @since 1.0.0
     */
    public static void checkIsValidSignInProviders(HashMap<String, ArrayList<String>> providers) {
        if (providers != null) {
            for (Map.Entry<String, ArrayList<String>> entry : providers.entrySet()) {
                String countryKeyCode = entry.getKey();
                ArrayList<String> value = entry.getValue();
                for (String val : value) {
                    if (providers.get(countryKeyCode).contains(SocialProvider.TWITTER)) {
                        throw new RuntimeException(SocialProvider.TWITTER +
                                " Provider is not supporting");
                    }
                }
            }
        }
    }

    /**
     * @return AccountStartTime
     * @since 1.0.0
     */
    public static long getCreateAccountStartTime() {
        return createAccountStartTime;
    }

    public static void setCreateAccountStartTime(long createAccountStartTime) {
        RegUtility.createAccountStartTime = createAccountStartTime;
    }

    /**
     * @param local : This parameter passes language and Country Code to Check weather Counter code
     *              is US or Not
     * @return Country code US return true  or False
     */
    public static boolean isCountryUS(String local) {
        if (local != null && local.length() == 5) {
            return local.substring(3, 5).equalsIgnoreCase(RegConstants.COUNTRY_CODE_US);
        }
        return false;
    }

    /**
     * @return defaultSupportedHomeCountries
     * @since 1.0.0
     */
    public static String[] getDefaultSupportedHomeCountries() {
        return defaultSupportedHomeCountries;
    }

    private static String[] defaultSupportedHomeCountries = new String[]{"AE", "BH", "BY","EG", "KW", "LB", "OM", "QA", "EG", "RW", "BG", "CZ", "DK", "AT", "CH", "DE", "GR", "AU", "CA", "GB", "HK", "ID", "IE", "IN", "MY", "NZ", "PH", "PK", "SA", "SG", "US", "ZA", "AR", "CL", "CO", "ES", "MX", "PE", "EE", "FI", "BE", "FR", "HR", "HU", "IT", "JP", "KR", "LT", "LV", "NL", "NO", "PL", "BR", "PT", "RO", "RU", "UA", "SI", "SK", "SE", "TH", "TR", "VN", "CN", "TW"};


    /**
     * @return List of Supported Countries
     * @since 1.0.0
     */
    public static List<String> supportedCountryList() {
        ArrayList<String> defaultCountries = new ArrayList<String>(Arrays.asList(RegUtility.getDefaultSupportedHomeCountries()));
        List<String> supportedHomeCountries = RegistrationConfiguration.getInstance().getSupportedHomeCountry();
        List<String> serviceDiscoveryCountries = RegistrationConfiguration.getInstance().getServiceDiscoveryCountries();
        if (serviceDiscoveryCountries != null && serviceDiscoveryCountries.size() > 0) {
            for (String country : serviceDiscoveryCountries) {
                String countryCode = country.toUpperCase();
                if (!defaultCountries.contains(countryCode)) {
                    defaultCountries.add(countryCode);
                }
                if (null != supportedHomeCountries && !supportedHomeCountries.contains(countryCode)) {
                    supportedHomeCountries.add(countryCode);
                }
            }
        }
        if (null != supportedHomeCountries) {
            List<String> filteredCountryList = new ArrayList<String>(supportedHomeCountries);
            filteredCountryList.retainAll(defaultCountries);
            if (filteredCountryList.size() > 0) {
                return filteredCountryList;
            }
        }
        return defaultCountries;
    }

    /**
     * @return FallbackCountryCode
     * @since 1.0.0
     */
    @NonNull
    public static String getFallbackCountryCode() {
        String fallbackCountry = RegistrationConfiguration.getInstance().getFallBackHomeCountry();
        String selectedCountryCode;
        if (null != fallbackCountry) {
            selectedCountryCode = fallbackCountry;
        } else {
            selectedCountryCode = RegConstants.COUNTRY_CODE_US;
        }
        return selectedCountryCode.toUpperCase();
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static void showErrorMessage(Activity parentActivity) {
        parentActivity.runOnUiThread(() -> Toast.makeText(parentActivity, parentActivity.getResources().getString(R.string.USR_JanRain_Server_ConnectionLost_ErrorMsg), Toast.LENGTH_SHORT).show());
    }

    public static void handleDynamicPermissionChange(Activity registrationActivity) {
        registrationActivity.finishAffinity();
        Intent i = registrationActivity.getBaseContext().getPackageManager().getLaunchIntentForPackage(registrationActivity.getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        registrationActivity.startActivity(i);
    }

    public static Country getCountry(String mSelectedCountryCode, Context mContext) {

        String key = getCountryKey(mSelectedCountryCode);
        String countryName = new Locale("", mSelectedCountryCode).getDisplayCountry();

        int identifier = mContext.getResources().getIdentifier(key, "string", mContext.getApplicationContext().getPackageName());

        if (identifier != 0) {
            try {
                countryName = mContext.getApplicationContext().getString(identifier);

            } catch (Exception resourcesNotFoundException) {
                RLog.d(TAG, "getCountry" + resourcesNotFoundException.getMessage());
            }
        }

        return new Country(mSelectedCountryCode, countryName);

    }

    private static String getCountryKey(String mSelectedCountryCode) {

        return "USR_Country_" + mSelectedCountryCode;
    }

}
