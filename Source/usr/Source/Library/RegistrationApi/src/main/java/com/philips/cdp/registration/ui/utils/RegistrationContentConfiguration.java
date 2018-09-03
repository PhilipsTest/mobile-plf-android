package com.philips.cdp.registration.ui.utils;

import java.io.*;


public class RegistrationContentConfiguration implements Serializable {

    private final String TAG = RegistrationContentConfiguration.class.getSimpleName();

    private static final long serialVersionUID = 1128016096756071383L;

    private String valueForRegistrationTitle;

    private String valueForEmailVerification;

    private String valueForRegistrationDescription;

    private String optInTitleText;

    private String optInQuessionaryText;

    private String optInDetailDescription;

    private String optInBannerText;

    private boolean enableLastName;

    private boolean enableContinueWithouAccount;

    private String optInActionBarText;

    private int showMarketImage;

    public String getValueForRegistrationDescription() {
        return valueForRegistrationDescription;
    }

    public void setValueForRegistrationDescription(String valueForRegistrationDescription) {
        RLog.d(TAG, "valueForRegistrationDescription :" + valueForRegistrationDescription);
        this.valueForRegistrationDescription = valueForRegistrationDescription;
    }

    public String getOptInActionBarText() {
        return optInActionBarText;
    }

    public void setOptInActionBarText(String optInActionBarText) {
        RLog.d(TAG, "optInActionBarText :" + optInActionBarText);
        this.optInActionBarText = optInActionBarText;
    }


    public String getValueForRegistrationTitle() {
        return valueForRegistrationTitle;
    }

    public void setValueForRegistrationTitle(String valueForRegistrationTitle) {
        RLog.d(TAG, "valueForRegistrationTitle :" + valueForRegistrationTitle);
        this.valueForRegistrationTitle = valueForRegistrationTitle;
    }

    public String getValueForEmailVerification() {
        return valueForEmailVerification;
    }

    public void setValueForEmailVerification(String valueForEmailVerification) {
        RLog.d(TAG, "valueForEmailVerification :" + valueForEmailVerification);
        this.valueForEmailVerification = valueForEmailVerification;
    }

    public String getOptInTitleText() {
        return optInTitleText;
    }

    public void setOptInTitleText(String optInTitleText) {
        RLog.d(TAG, "optInTitleText :" + optInTitleText);
        this.optInTitleText = optInTitleText;
    }

    public String getOptInQuessionaryText() {
        return optInQuessionaryText;
    }

    public void setOptInQuessionaryText(String optInQuessionaryText) {
        RLog.d(TAG, "optInQuessionaryText :" + optInQuessionaryText);
        this.optInQuessionaryText = optInQuessionaryText;
    }

    public String getOptInDetailDescription() {
        return optInDetailDescription;
    }

    public void setOptInDetailDescription(String optInDetailDescription) {
        RLog.d(TAG, "optInDetailDescription :" + optInDetailDescription);
        this.optInDetailDescription = optInDetailDescription;
    }

    public String getOptInBannerText() {
        return optInBannerText;
    }

    public void setOptInBannerText(String optInBannerText) {
        RLog.d(TAG, "optInBannerText :" + optInBannerText);
        this.optInBannerText = optInBannerText;
    }


    public boolean getEnableLastName() {
        return enableLastName;
    }

    public void enableLastName(boolean hideCreateAccountLastName) {
        RLog.d(TAG, "enableLastName :" + hideCreateAccountLastName);
        this.enableLastName = hideCreateAccountLastName;
    }

    public boolean getEnableContinueWithouAccount() {
        return enableContinueWithouAccount;
    }

    public void enableContinueWithouAccount(boolean hideHomeScreenContinueWithouAccount) {
        RLog.d(TAG, "enableContinueWithouAccount :" + hideHomeScreenContinueWithouAccount);
        this.enableContinueWithouAccount = hideHomeScreenContinueWithouAccount;
    }

    public int getEnableMarketImage() {
        return showMarketImage;
    }

    public void enableMarketImage(int imageId) {
        RLog.d(TAG, "enableImageId :" + imageId);
        this.showMarketImage = imageId;
    }

}
