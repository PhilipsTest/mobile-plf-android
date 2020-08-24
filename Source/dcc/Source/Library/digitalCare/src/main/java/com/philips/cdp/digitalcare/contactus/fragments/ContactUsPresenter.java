package com.philips.cdp.digitalcare.contactus.fragments;

import android.view.View;

import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.contactus.models.CdlsPhoneModel;
import com.philips.cdp.digitalcare.contactus.models.CdlsResponseModel;
import com.philips.cdp.digitalcare.contactus.parser.CdlsParsingCallback;
import com.philips.cdp.digitalcare.contactus.parser.CdlsResponseParser;
import com.philips.cdp.digitalcare.request.RequestData;
import com.philips.cdp.digitalcare.request.ResponseCallback;
import com.philips.cdp.digitalcare.util.DigiCareLogger;
import com.philips.cdp.digitalcare.util.DigitalCareConstants;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;
import com.philips.platform.appinfra.tagging.ErrorCategory;
import com.philips.platform.appinfra.tagging.TaggingError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


public class ContactUsPresenter implements ResponseCallback, Observer {

    private ContactUsContract contract;

    private CdlsResponseModel mCdlsParsedResponse = null;

    private boolean isFirstTimeCdlsCall = true;
    private static final String TAG = ContactUsPresenter.class.getSimpleName();

    public ContactUsPresenter(ContactUsContract contract) {
        this.contract = contract;
    }

    private final CdlsParsingCallback mParsingCompletedCallback = new CdlsParsingCallback() {
        @Override
        public void onCdlsParsingComplete(final CdlsResponseModel response) {
            if (response != null && response.getSuccess()) {
                mCdlsParsedResponse = response;
                updateUi();
            } else {
                if (isFirstTimeCdlsCall) {
                    isFirstTimeCdlsCall = false;
                    requestCdlsData();
                } else {
                    contract.fadeoutButtons();
                }
            }
        }
    };

    public void parseCdlsResponse(String response) {
        final CdlsResponseParser cdlsResponseParser = new CdlsResponseParser(
                mParsingCompletedCallback);
        cdlsResponseParser.parseCdlsResponse(response);
    }

    public void updateUi() {
        if (mCdlsParsedResponse.getSuccess()) {
            final CdlsPhoneModel phoneModel = mCdlsParsedResponse.getPhone();
            if (phoneModel != null) {
                if (phoneModel.getPhoneNumber() != null) {
                    contract.showCallPhilipsBtn();
                }
                contract.enableBottomText();
                final StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(phoneModel.getOpeningHoursWeekdays())
                        .append(phoneModel.getOpeningHoursSaturday())
                        .append(phoneModel.getOpeningHoursSunday())
                        .append(phoneModel.getOptionalData1())
                        .append(phoneModel.getOptionalData2())
                        .append(phoneModel.getmPhoneTariff());
                contract.enableBottomText();
                contract.setTextCallPhilipsBtn(mCdlsParsedResponse.getPhone().getPhoneNumber());
                contract.updateFirstRowSharePreference(stringBuilder, mCdlsParsedResponse.getPhone()
                        .getPhoneNumber());
            }
        } else if (isCdlsResponseModelNull()) {
            contract.fadeoutButtons();
        } else {
            contract.fadeoutButtons();
        }
    }

    private boolean isCdlsResponseModelNull() {
        return mCdlsParsedResponse.getError() != null;
    }

    protected void requestCdlsData() {
        contract.startProgressDialog();
        if (isCdlsUrlNull()) {
            String url = getCdlsUrl();
            RequestData requestData = new RequestData();
            requestData.setRequestUrl(url);
            requestData.setResponseCallback(this);
            requestData.execute();
        }
    }

    public boolean isCdlsUrlNull() {
        return getCdlsUrl() != null;
    }

    protected String getCdlsUrl() {
        return DigitalCareConfigManager.getInstance().getCdlsUrl();
    }

    @Override
    public void onResponseReceived(String response) {
        if (contract.isViewAdded()) {
            contract.closeProgressDialog();
            if (response != null && contract.isViewAdded()) {
                parseCdlsResponse(response);
            } else {
                if (isFirstTimeCdlsCall) {
                    isFirstTimeCdlsCall = false;
                    requestCdlsData();
                } else {
                    contract.fadeoutButtons();
                }
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        requestCdlsData();
    }

    public void initialiseSD(String appName) {

        ArrayList<String> var1 = new ArrayList<>();
        var1.add(DigitalCareConstants.SERVICE_ID_CC_CDLS);
        var1.add(DigitalCareConstants.SERVICE_ID_CC_EMAILFROMURL);
        var1.add(DigitalCareConstants.SERVICE_ID_CC_TWITTER);
        var1.add(DigitalCareConstants.SERVICE_ID_CC_FB);
        var1.add(DigitalCareConstants.SERVICE_ID_CC_LIVECHAT);

        HashMap<String, String> productInfoMap = new HashMap<String, String>();

        productInfoMap.put(DigitalCareConstants.KEY_PRODUCT_SECTOR, DigitalCareConfigManager.getInstance().getConsumerProductInfo().getSector());
        productInfoMap.put(DigitalCareConstants.KEY_PRODUCT_CATALOG, DigitalCareConfigManager.getInstance().getConsumerProductInfo().getCatalog());
        productInfoMap.put(DigitalCareConstants.KEY_PRODUCT_CATEGORY, DigitalCareConfigManager.getInstance().getConsumerProductInfo().getCategory());
        productInfoMap.put(DigitalCareConstants.KEY_APPNAME, appName);


        DigitalCareConfigManager.getInstance().getAPPInfraInstance().getServiceDiscovery().getServicesWithCountryPreference(var1, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveryService> map) {

                ServiceDiscoveryService serviceDiscoveryService = map.get("cc.emailformurl");
                if (serviceDiscoveryService != null) {
                    DigitalCareConfigManager.getInstance().setEmailUrl(serviceDiscoveryService.getConfigUrls());
                    DigiCareLogger.d(TAG, "Response from Service Discovery : Service ID : 'cc.emailformurl' - " + serviceDiscoveryService.getConfigUrls());
                }

                if (DigitalCareConfigManager.getInstance().getConsumerProductInfo().getCategory() != null && !DigitalCareConfigManager.getInstance().getConsumerProductInfo().getCategory().isEmpty()) {
                    serviceDiscoveryService = map.get("cc.cdls");
                    if (serviceDiscoveryService != null) {
                        DigitalCareConfigManager.getInstance().setCdlsUrl(serviceDiscoveryService.getConfigUrls());
                        DigiCareLogger.d(TAG, "Response from Service Discovery : Service ID : 'cc.cdls' - " + serviceDiscoveryService.getConfigUrls());
                    }
                }

                serviceDiscoveryService = map.get("cc.twitterurl");
                if (serviceDiscoveryService != null) {
                    DigitalCareConfigManager.getInstance().setTwitterUrl(serviceDiscoveryService.getConfigUrls());
                    DigiCareLogger.d(TAG, "Response from Service Discovery : Service ID : 'cc.twitterurl' - " + serviceDiscoveryService.getConfigUrls());
                }

                serviceDiscoveryService = map.get("cc.facebookurl");
                if (serviceDiscoveryService != null) {
                    DigitalCareConfigManager.getInstance().setFbUrl(serviceDiscoveryService.getConfigUrls());
                    DigiCareLogger.d(TAG, "Response from Service Discovery : Service ID : 'cc.facebookurl' - " + serviceDiscoveryService.getConfigUrls());
                }

                serviceDiscoveryService = map.get("cc.livechaturl");
                if (serviceDiscoveryService != null) {
                    String configUrls = serviceDiscoveryService.getConfigUrls();
                    DigitalCareConfigManager.getInstance().setSdLiveChatUrl(configUrls);
                    contract.updateLiveChatButton(configUrls == null ? View.GONE : View.VISIBLE);
                    DigiCareLogger.d(TAG, "Response from Service Discovery : Service ID : 'cc.livechaturl' - " + serviceDiscoveryService.getConfigUrls());
                } else {
                    contract.updateLiveChatButton(View.GONE);
                }


            }

            @Override
            public void onError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES errorvalues, String s) {
//                DigitalCareConfigManager.getInstance().getTaggingInterface().trackActionWithInfo(AnalyticsConstants.ACTION_SET_ERROR, AnalyticsConstants.ACTION_KEY_TECHNICAL_ERROR, "DCC:".concat(s));
                if (errorvalues.name().equals(ERRORVALUES.NO_NETWORK.name()))
                    DigitalCareConfigManager.getInstance().getTaggingInterface().trackErrorAction(ErrorCategory.INFORMATIONAL_ERROR, new TaggingError(s));
                else
                    DigitalCareConfigManager.getInstance().getTaggingInterface().trackErrorAction(ErrorCategory.TECHNICAL_ERROR, new TaggingError(s));
            }
        }, productInfoMap);

    }

}
