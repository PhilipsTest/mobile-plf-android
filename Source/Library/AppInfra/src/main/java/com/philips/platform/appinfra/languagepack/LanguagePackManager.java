package com.philips.platform.appinfra.languagepack;

import android.os.Build;
import android.os.LocaleList;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.languagepack.model.LanguageList;
import com.philips.platform.appinfra.languagepack.model.LanguageModel;
import com.philips.platform.appinfra.languagepack.model.LanguagePackMetadata;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.appinfra.rest.request.JsonObjectRequest;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;

import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import static com.philips.platform.appinfra.languagepack.LanguagePackConstants.LANGUAGE_PACK_CONFIG_SERVICE_ID_KEY;


public class LanguagePackManager implements LanguagePackInterface {

	private AppInfra mAppInfra;
	private RestInterface mRestInterface;
	private LanguageList mLanguageList;
	private LanguageModel selectedLanguageModel;
	private LanguagePackUtil languagePackUtil;

	public LanguagePackManager(AppInfra appInfra) {
		mAppInfra = appInfra;
		mRestInterface = appInfra.getRestClient();
		mLanguageList = new LanguageList();
		languagePackUtil = new LanguagePackUtil(appInfra.getAppInfraContext());
	}

	@Override
	public void refresh(final OnRefreshListener aILPRefreshResult) {

		AppConfigurationInterface appConfigurationInterface = mAppInfra.getConfigInterface();
		AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface.AppConfigurationError();
		String languagePackServiceId = (String) appConfigurationInterface.getPropertyForKey(LANGUAGE_PACK_CONFIG_SERVICE_ID_KEY, "APPINFRA", configError);
		ServiceDiscoveryInterface mServiceDiscoveryInterface = mAppInfra.getServiceDiscovery();

		mServiceDiscoveryInterface.getServiceUrlWithCountryPreference(languagePackServiceId, new ServiceDiscoveryInterface.OnGetServiceUrlListener() {
			@Override
			public void onSuccess(URL url) {
				String languagePackConfigURL = url.toString();

				mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "AILP_URL", url.toString()); // US requirement to show language pack URL

				JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, languagePackConfigURL, null,
						new Response.Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "AILP_URL", response.toString());
								if (null != response) {
									Gson gson = new Gson();
									mLanguageList = gson.fromJson(response.toString(), LanguageList.class);
									String url = getPreferredLocaleURL();
									downloadLanguagePack(url, aILPRefreshResult);
								}
							}
						}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						String errorcode = null != error.networkResponse ? error.networkResponse.statusCode + "" : "";
						String errMsg = " Error Code:" + errorcode + " , Error Message:" + error.toString();
						mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "AILP_URL", errMsg);
						aILPRefreshResult.onError(OnRefreshListener.AILPRefreshResult.RefreshFailed, errMsg);

					}
				}, null, null, null);
				mRestInterface.getRequestQueue().add(jsonRequest);
			}

			@Override
			public void onError(ERRORVALUES error, String message) {
				mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO,
						"AILP_URL", " Error Code:" + error.toString() + " , Error Message:" + message);
				String errMsg = " Error Code:" + error + " , Error Message:" + error.toString();
				mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "AILP_URL", errMsg);
				aILPRefreshResult.onError(OnRefreshListener.AILPRefreshResult.RefreshFailed, errMsg);

			}
		});

	}

    private void downloadLanguagePack(String url, final OnRefreshListener aILPRefreshResult) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "AILP_URL",
                                "Language Pack Json: " + response.toString());
                        languagePackUtil.saveFile(response.toString(), LanguagePackConstants.LOCALE_FILE_DOWNLOADED);
                        languagePackUtil.saveLocaleMetaData(selectedLanguageModel);
                        aILPRefreshResult.onSuccess(OnRefreshListener.AILPRefreshResult.RefreshedFromServer);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String errorcode = null != error.networkResponse ? error.networkResponse.statusCode + "" : "";
                String errMsg = " Error Code:" + errorcode + " , Error Message:" + error.toString();
                mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "AILP_URL", errMsg);
                aILPRefreshResult.onError(OnRefreshListener.AILPRefreshResult.RefreshFailed, errMsg);
            }
        }, null, null, null);
        mRestInterface.getRequestQueue().add(jsonObjectRequest);
    }

	private String getLocaleList() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return LocaleList.getDefault().toString();
		} else {
			return mAppInfra.getInternationalization().getUILocaleString();
		}
	}

	private String getPreferredLocaleURL() {

		ArrayList<LanguageModel> languageModels = mLanguageList.getLanguages();
		ArrayList<String> deviceLocaleList = new ArrayList<>(Arrays.asList(getLocaleList().split(",")));
		LanguageModel langModel = new LanguageModel();
		for (String deviceLocale : deviceLocaleList) {
			for (LanguageModel model : languageModels) {
				if (model.getLocale().equalsIgnoreCase(deviceLocale)) {
                    selectedLanguageModel = model;
					langModel.setRemoteVersion(model.getRemoteVersion());
					langModel.setLocale(model.getLocale());
					langModel.setUrl(model.getUrl());
					return model.getUrl();
				} else if (model.getLocale().substring(0, 2).intern().equalsIgnoreCase
						(deviceLocale.substring(0, 2).intern())) {
                    selectedLanguageModel = model;
					langModel.setRemoteVersion(model.getRemoteVersion());
					langModel.setLocale(model.getLocale());
					langModel.setUrl(model.getUrl());
					return model.getUrl();
				} else if (deviceLocale.contains(model.getLocale().substring(0, 2))) {
                    selectedLanguageModel = model;
					langModel.setRemoteVersion(model.getRemoteVersion());
					langModel.setLocale(model.getLocale());
					langModel.setUrl(model.getUrl());
					return model.getUrl();
				}

			}
		}


		//	String defaultlocale = new String("en_GB");
//		if (languageModels.contains(defaultlocale)) {
//			int index = languageModels.indexOf("en_GB");
//			return languageModels.get(index).getUrl();
//		}
		return null;
	}

	public void activate(OnActivateListener onActivateListener) {
		File file = languagePackUtil.getLanguagePackFilePath(LanguagePackConstants.LOCALE_FILE_INFO);
		Gson gson = new Gson();
		LanguagePackMetadata languagePackMetadata = gson.fromJson(languagePackUtil.readFile(file), LanguagePackMetadata.class);
		Log.d(getClass() + "", languagePackMetadata.getLocale() + "---" + languagePackMetadata.getUrl() + "-----" + languagePackMetadata.getVersion());
		onActivateListener.onSuccess(file.getAbsolutePath());
	}

}
