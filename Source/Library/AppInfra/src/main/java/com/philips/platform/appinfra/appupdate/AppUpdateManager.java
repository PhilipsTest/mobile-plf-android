/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.appupdate;


import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraLogEventID;
import com.philips.platform.appinfra.FileUtils;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.appupdate.model.AppUpdateModel;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.rest.request.JsonObjectRequest;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.timesync.TimeSyncSntpClient;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class AppUpdateManager implements AppUpdateInterface {

	private Context mContext;
	private AppInfra mAppInfra;
	private Handler mHandler;
	private AppUpdateModel mAppUpdateModel;
	private Gson mGson;
	private FileUtils mFileUtils;
	public static final String APPUPDATE_DATE_FORMAT = "yyyy-MM-dd";


	public AppUpdateManager(AppInfra appInfra) {
		this.mAppInfra = appInfra;
		this.mContext = appInfra.getAppInfraContext();
		init();
	}

	private void init() {
		mGson = new Gson();
		mFileUtils = new FileUtils(mContext);
		mAppUpdateModel = getAppUpdateModel();
	}


	protected void downloadAppUpdate(final String appUpdateUrl, final OnRefreshListener refreshListener) {
		JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, appUpdateUrl, null,
				getJsonResponseListener(refreshListener), getJsonErrorListener(refreshListener), null, null, null);
		mAppInfra.getRestClient().getRequestQueue().add(jsonRequest);
	}

	@NonNull
	protected Response.ErrorListener getJsonErrorListener(final OnRefreshListener refreshListener) {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				final String errorcode = null != error.networkResponse ? error.networkResponse.statusCode + "" : "";
				final String errMsg = " Error Code:" + errorcode + " , Error Message:" + error.toString();
				mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "AI AppUpdate_URL", errMsg);
				refreshListener.onError(OnRefreshListener.AIAppUpdateRefreshResult.AppUpdate_REFRESH_FAILED, errMsg);
			}
		};
	}

	protected Response.Listener<JSONObject> getJsonResponseListener(final OnRefreshListener refreshListener) {
		return new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "AI AppUpate", response.toString());
				try {
					final JSONObject resp = response.getJSONObject("android");
					if (resp != null) {
						mHandler = getHandler(mContext);
						new Thread(new Runnable() {
							@Override
							public void run() {
								mFileUtils.saveFile(resp.toString(), AppUpdateConstants.LOCALE_FILE_DOWNLOADED, AppUpdateConstants.APPUPDATE_PATH);
								processResponse(resp.toString(), refreshListener);
							}
						}).start();
					} else {
						refreshListener.onError(OnRefreshListener.AIAppUpdateRefreshResult.AppUpdate_REFRESH_FAILED,
								"Android appupdate info is missing in response");
					}
				} catch (JSONException e) {
					refreshListener.onError(OnRefreshListener.AIAppUpdateRefreshResult.AppUpdate_REFRESH_FAILED, "JSON EXCEPTION");
					mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "AI AppUpdate_URL", "JSON EXCEPTION");
				}
			}
		};
	}

	private void processResponse(String response, OnRefreshListener refreshListener) {
		mAppUpdateModel = mGson.fromJson(response, AppUpdateModel.class);
		if (mAppUpdateModel != null) {
			mHandler.post(postRefreshSuccess(refreshListener, OnRefreshListener.AIAppUpdateRefreshResult.AppUpdate_REFRESH_SUCCESS));
		} else {
			mHandler.post(postRefreshError(refreshListener, OnRefreshListener.AIAppUpdateRefreshResult.AppUpdate_REFRESH_FAILED, "Parsing Issue"));
		}
	}

	private Runnable postRefreshSuccess(final OnRefreshListener aIRefreshResult,
	                                    final OnRefreshListener.AIAppUpdateRefreshResult ailpRefreshResult) {
		return new Runnable() {
			@Override
			public void run() {
				if (aIRefreshResult != null)
					aIRefreshResult.onSuccess(ailpRefreshResult);
			}
		};
	}

	private Runnable postRefreshError(final OnRefreshListener aIRefreshResult,
	                                  final OnRefreshListener.AIAppUpdateRefreshResult ailpRefreshResult, final String errorDescription) {
		return new Runnable() {
			@Override
			public void run() {
				if (aIRefreshResult != null)
					aIRefreshResult.onError(ailpRefreshResult, errorDescription);
			}
		};
	}

	private AppUpdateModel getAppUpdateModel() {
		if (mAppUpdateModel == null) {
			mAppUpdateModel = mGson.fromJson(mFileUtils.readFile(getAppUpdatefromCache(AppUpdateConstants.LOCALE_FILE_DOWNLOADED
					, AppUpdateConstants.APPUPDATE_PATH)), AppUpdateModel.class);
		}
		return mAppUpdateModel;
	}

	private File getAppUpdatefromCache(String fileName, String filePath) {
		return mFileUtils.getFilePath(fileName, filePath);
	}

	private Handler getHandler(Context context) {
		return new Handler(context.getMainLooper());
	}

	@Override
	public void refresh(final OnRefreshListener refreshListener) {
		try {
			final String appupdateServiceId = getServiceIdFromAppConfig();
			if (appupdateServiceId == null) {
				refreshListener.onError(OnRefreshListener.AIAppUpdateRefreshResult.AppUpdate_REFRESH_FAILED, "Could not read service id");
			} else {
				ServiceDiscoveryInterface mServiceDiscoveryInterface = mAppInfra.getServiceDiscovery();
				mServiceDiscoveryInterface.getServiceUrlWithCountryPreference(appupdateServiceId, getServiceDiscoveryListener(refreshListener));
			}
		} catch (IllegalArgumentException exception) {
			refreshListener.onError(OnRefreshListener.AIAppUpdateRefreshResult.AppUpdate_REFRESH_FAILED, "App configuration error");
		}
	}

	protected String getServiceIdFromAppConfig() {
		final AppConfigurationInterface appConfigurationInterface = getAppConfigurationInterface();
		final AppConfigurationInterface.AppConfigurationError configError = getAppConfigurationError();
		return (String) appConfigurationInterface.getPropertyForKey("appUpdate.serviceId", "appinfra", configError);
	}

	@NonNull
	private AppConfigurationInterface.AppConfigurationError getAppConfigurationError() {
		return new AppConfigurationInterface.AppConfigurationError();
	}

	private  AppConfigurationInterface getAppConfigurationInterface() {
		return mAppInfra.getConfigInterface();
	}

	@NonNull
	protected ServiceDiscoveryInterface.OnGetServiceUrlListener getServiceDiscoveryListener
			(final OnRefreshListener refreshListener) {
		return new ServiceDiscoveryInterface.OnGetServiceUrlListener() {
			@Override
			public void onSuccess(URL url) {
				final String appUpdateURL = url.toString();
				mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "AppUpdate_URL", url.toString());
				downloadAppUpdate(appUpdateURL, refreshListener);
			}

			@Override
			public void onError(ERRORVALUES error, String message) {
				mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO,
						"AppUpdate_URL", " Error Code:" + error.toString() + " , Error Message:" + message);
				final String errMsg = " Error Code:" + error + " , Error Message:" + error.toString();
				mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "AI AppUpdate_URL", errMsg);
				refreshListener.onError(OnRefreshListener.AIAppUpdateRefreshResult.AppUpdate_REFRESH_FAILED, errMsg);
			}
		};
	}


	protected String getAppVersion() {
		return mAppInfra.getAppIdentity().getAppVersion();
	}

	@Override
	public boolean isDeprecated() {
		if (getAppUpdateModel() != null && getAppUpdateModel().getVersion() != null) {
			String minVer = getAppUpdateModel().getVersion().getMinimumVersion();
			String deprecatedVersion = getAppUpdateModel().getVersion().getDeprecatedVersion();
			String deprecationDate = getAppUpdateModel().getVersion().getDeprecationDate();

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			formatter.setTimeZone(TimeZone.getTimeZone(TimeSyncSntpClient.UTC));
			try {
				Date deprecationdate = formatter.parse(deprecationDate);
				Date currentDate = new Date();
				return AppUpdateVersion.isAppVerionLessthanCloud(getAppVersion(), minVer) ||
						AppUpdateVersion.isBothVersionSame(getAppVersion(), deprecatedVersion) && currentDate.after(deprecationdate);
			} catch (ParseException e) {
				mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "AI AppUpdate_URL", "Parse Exception");
			}
		}
		return false;
	}

	@Override
	public boolean isToBeDeprecated() {
		if (getAppUpdateModel() != null && getAppUpdateModel().getVersion() != null) {
			String minVer = getAppUpdateModel().getVersion().getMinimumVersion();
			String deprecatedVer = getAppUpdateModel().getVersion().getDeprecatedVersion();
			return AppUpdateVersion.isAppVerionLessthanCloud(minVer, getAppVersion()) &&
					AppUpdateVersion.isAppVersionLessthanEqualsto(getAppVersion(), deprecatedVer);
		}
		return false;
	}

	@Override
	public boolean isUpdateAvailable() {
		if (getAppUpdateModel() != null && getAppUpdateModel().getVersion() != null) {
			String currentVer = getAppUpdateModel().getVersion().getCurrentVersion();
			return AppUpdateVersion.isAppVerionLessthanCloud(getAppVersion(), currentVer);
		}
		return false;
	}

	@Override
	public String getDeprecateMessage() {
		if (getAppUpdateModel() != null && getAppUpdateModel().getMessages() != null) {
			return getAppUpdateModel().getMessages().getMinimumVersionMessage();
		}
		return null;
	}

	@Override
	public String getToBeDeprecatedMessage() {
		if (getAppUpdateModel() != null && getAppUpdateModel().getMessages() != null) {
			return getAppUpdateModel().getMessages().getDeprecatedVersionMessage();
		}
		return null;
	}

	@Override
	public Date getToBeDeprecatedDate() {
		if (getAppUpdateModel() != null && getAppUpdateModel().getVersion() != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(APPUPDATE_DATE_FORMAT
					, Locale.ENGLISH);
			formatter.setTimeZone(TimeZone.getTimeZone(TimeSyncSntpClient.UTC));
			try {
				return formatter.parse(getAppUpdateModel().getVersion().getDeprecationDate());
			} catch (ParseException e) {
				mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO,
						"AI AppUpdate_URL", "Date Parse Exception");
			}
		}
		return null;
	}

	@Override
	public String getUpdateMessage() {
		if (getAppUpdateModel() != null && getAppUpdateModel().getMessages() != null) {
			return getAppUpdateModel().getMessages().getCurrentVersionMessage();
		}
		return null;
	}

	@Override
	public String getMinimumVersion() {
		if (getAppUpdateModel() != null && getAppUpdateModel().getVersion() != null) {
			return getAppUpdateModel().getVersion().getMinimumVersion();
		}
		return null;
	}

	@Override
	public String getMinimumOSverion() {
		if (getAppUpdateModel() != null && getAppUpdateModel().getRequirements() != null) {
			return getAppUpdateModel().getRequirements().getMinimumOSVersion();
		}
		return null;
	}



	public void appInfraRefresh() {
		File appupdateCache = getAppUpdatefromCache(AppUpdateConstants.LOCALE_FILE_DOWNLOADED
				, AppUpdateConstants.APPUPDATE_PATH);
		if (appupdateCache != null && appupdateCache.exists() && appupdateCache.length() > 0) {
			mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, AppInfraLogEventID.AI_APPINFRA,
					"appdate info already downloaded");
			return;
		}
		try {
			Object isappUpdateRq = getAutoRefreshValue(mAppInfra.getConfigInterface() ,mAppInfra);
			if (isappUpdateRq != null && isappUpdateRq instanceof Boolean) {
				final Boolean isautorefreshEnabled = (Boolean) isappUpdateRq;
				if (isautorefreshEnabled) {
					refresh(new OnRefreshListener() {
						@Override
						public void onError(AIAppUpdateRefreshResult error, String message) {
							mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_APPINFRA,
									"AppConfiguration Auto refresh failed- AppUpdate" + " " + error);
						}

						@Override
						public void onSuccess(AIAppUpdateRefreshResult result) {
							mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, AppInfraLogEventID.AI_APPINFRA,
									"AppConfiguration Auto refresh success- AppUpdate" + " " + result);
						}
					});
				} else {
					mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_APPINFRA,
							"AppConfiguration Auto refresh failed- AppUpdate");
				}
			}
		} catch (IllegalArgumentException exception) {
			mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, AppInfraLogEventID.AI_APPINFRA,
					"AppConfiguration " + exception.toString());
		}
	}

	public static Object getAutoRefreshValue(AppConfigurationInterface appConfigurationInterface , AppInfra ai) {
		try {
			AppConfigurationInterface.AppConfigurationError error = new AppConfigurationInterface.AppConfigurationError();
			return appConfigurationInterface.getPropertyForKey("appUpdate.autoRefresh", "appinfra", error);
		} catch (IllegalArgumentException exception) {
			ai.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO,
					AppInfraLogEventID.AI_APPINFRA,"Error in reading AppUpdate  Config "
							+exception.toString());
		}

		return null;
	}
}