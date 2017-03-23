/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.servicediscovery;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.appinfra.appidentity.AppIdentityInterface;
import com.philips.platform.appinfra.appidentity.AppIdentityManager;
import com.philips.platform.appinfra.internationalization.InternationalizationInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.securestorage.SecureStorage;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.appinfra.servicediscovery.model.AISDResponse;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscovery;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class downloads list of URLs from service discovery server,
 * providing the locale and the identity of the application.
 */
public class ServiceDiscoveryManager implements ServiceDiscoveryInterface {


	private abstract class DownloadItemListener {
		/**
		 * Force refresh boolean.
		 *
		 * @return the boolean
		 */
		public boolean forceRefresh() {
			return false;
		}

		/**
		 * On download done.
		 *
		 * @param result the result
		 */
		public abstract void onDownloadDone(AISDResponse result);
	}

	/**
	 * The interface Aisd listener.
	 */
	interface AISDListener {
		/**
		 * Ondata received.
		 *
		 * @param response the response
		 */
		void ondataReceived(AISDResponse response);
	}

	/**
	 * The enum Aisdurl type.
	 */
	enum AISDURLType {
		/**
		 * Aisdurl type proposition aisdurl type.
		 */
		AISDURLTypeProposition, /**
		 * Aisdurl type platform aisdurl type.
		 */
		AISDURLTypePlatform
	}

	private static final String COUNTRY = "country";
	private static final String COUNTRY_SOURCE = "country_source";

	private static final String URLTagTest = "apps%2b%2benv%2btest";
	private static final String URLTagDevelopment = "apps%2b%2benv%2bdev";
	private static final String URLTagStaging = "apps%2b%2benv%2bstage";
	private static final String URLTagAcceptance = "apps%2b%2benv%2bacc";
	private static final String URLTagProduction = "apps%2b%2benv%2bprod";

	private static final String baseURLProduction = "www.philips.com";
	private static final String baseURLTesting = "tst.philips.com";
	private static final String baseURLStaging = "dev.philips.com";
	private static final String baseURLAcceptance = "acc.philips.com";

	private static final String stateTesting = "TEST";
	private static final String stateDevelopment = "DEVELOPMENT";
	private static final String stateStaging = "STAGING";
	private static final String stateAccepteance = "ACCEPTANCE";
	private static final String stateProduction = "PRODUCTION";


	private OnGetHomeCountryListener.SOURCE countryCodeSource;
	private final AppInfra mAppInfra;
	private final Context context;
	private AISDResponse serviceDiscovery = null;

	private String countryCode;
	private long holdbackTime = 0l;

	private final RequestManager mRequestItemManager;

	private ServiceDiscoveryInterface.OnGetHomeCountryListener.ERRORVALUES errorvalues;
	//
	private boolean downloadInProgress;
	private ArrayDeque<DownloadItemListener> downloadAwaiters;
	private ReentrantLock downloadLock;
	private String mCountry;
	private String mCountrySourceType;

	/**
	 * Instantiates a new Service discovery manager.
	 *
	 * @param aAppInfra the a app infra
	 */
	public ServiceDiscoveryManager(AppInfra aAppInfra) {
		mAppInfra = aAppInfra;
		context = mAppInfra.getAppInfraContext();
		mRequestItemManager = new RequestManager(context, mAppInfra);
		downloadInProgress = false;
		downloadAwaiters = new ArrayDeque<>();
		downloadLock = new ReentrantLock();
	}

	private void queueResultListener(boolean forcerefresh, DownloadItemListener listener) {
		downloadLock.lock();

		if (forcerefresh)
			serviceDiscovery = null;
		downloadAwaiters.add(listener);

		if (!downloadInProgress)
			if (new Date().getTime() > holdbackTime) {// if current time is greater then holdback time
				downloadInProgress = true;
				new Thread(new Runnable() {
					@Override
					public void run() {
						boolean forceRefresh = false;
						AISDResponse service;
						ArrayList<DownloadItemListener> stalledAwaiters = new ArrayList<DownloadItemListener>();
						do {
							if (forceRefresh)
								downloadLock.unlock();
							forceRefresh = false;
							service = downloadServices();
							downloadLock.lock();
							DownloadItemListener d;
							while ((d = downloadAwaiters.poll()) != null) {
								if (d.forceRefresh())
									forceRefresh = true;
								stalledAwaiters.add(d);
							}
						}
						while (forceRefresh);
						downloadInProgress = false;
						serviceDiscovery = service;
						final AISDResponse result = service;
						downloadLock.unlock();
						for (final DownloadItemListener d : stalledAwaiters) {
							Thread t = new Thread(new Runnable() {
								@Override
								public void run() {
									d.onDownloadDone(result);
								}
							});
							new Handler(Looper.getMainLooper()).post(t);
						}
					}
				}).start();
			} else {
				AISDResponse serviceDiscoveryError = new AISDResponse(mAppInfra);
				if (serviceDiscoveryError.getPlatformURLs() != null) {
					ServiceDiscovery platformError = serviceDiscoveryError.getPlatformURLs();
					platformError.setError(new ServiceDiscovery.Error
							(OnErrorListener.ERRORVALUES.SERVER_ERROR, "Server is not reachable at the moment,Please try after some time"));
				}
				if (serviceDiscoveryError.getPropositionURLs() != null) {
					serviceDiscoveryError.getPropositionURLs().setError(new ServiceDiscovery.Error
							(OnErrorListener.ERRORVALUES.SERVER_ERROR, "Server is not reachable at the moment,Please try after some time"));
					mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SD call", "Server is not reachable at the moment,Please try after some time");

				}

				if (listener != null)
					listener.onDownloadDone(serviceDiscoveryError);
			}
		else {
			mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SD call", "Download already in progress, please wait for response");
		}
		downloadLock.unlock();
	}


	/**
	 * Precondition: download lock is acquired
	 */
	private AISDResponse downloadServices() {
		AISDResponse response = new AISDResponse(mAppInfra);
		ServiceDiscovery platformService = null, propositionService = null;

		propositionService = downloadPropositionService();
		if (propositionService != null && propositionService.isSuccess()) {
			String country = fetchFromSecureStorage(COUNTRY);
			String countrySource = fetchFromSecureStorage(COUNTRY_SOURCE);
			if (country == null) {
				if (countrySource == null)
					countryCodeSource = OnGetHomeCountryListener.SOURCE.GEOIP;
				saveToSecureStore(propositionService.getCountry(), COUNTRY);
				saveToSecureStore(countryCodeSource.toString(), COUNTRY_SOURCE);
			}
			platformService = downloadPlatformService();
		}
		if (platformService != null && propositionService != null) {
			if (propositionService.isSuccess() && platformService.isSuccess()) {
				response.setPlatformURLs(platformService);
				response.setPropositionURLs(propositionService);
			} else {
				ServiceDiscovery error = new ServiceDiscovery();
				error.setError(new ServiceDiscovery.Error(OnErrorListener.ERRORVALUES.INVALID_RESPONSE, "DOWNLOAD FAILED"));
				error.setError(new ServiceDiscovery.Error(OnErrorListener.ERRORVALUES.SERVER_ERROR, "DOWNLOAD FAILED"));
				mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SD call", "DOWNLOAD FAILED");
			}
		} else {
			mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SD call",
					"Download failed");
		}
		return response;
	}


	private ServiceDiscovery downloadPlatformService() {
		String platformURL = getSDURLForType(AISDURLType.AISDURLTypePlatform);
		ServiceDiscovery platformService = new ServiceDiscovery();
		if (platformURL != null) {
			platformService = processRequest(platformURL, platformService, AISDURLType.AISDURLTypePlatform);
		}
		return platformService;
	}


	private ServiceDiscovery downloadPropositionService() {
		String propositionURL = getSDURLForType(AISDURLType.AISDURLTypeProposition);
		ServiceDiscovery propositionService = new ServiceDiscovery();
		if (propositionURL != null) {
			propositionService = processRequest(propositionURL, propositionService, AISDURLType.AISDURLTypeProposition);
		}
		return propositionService;
	}

	private ServiceDiscovery processRequest(String urlBuild, ServiceDiscovery service,
	                                        AISDURLType aisdurlType) {
		if (null != mAppInfra.getRestClient() && !mAppInfra.getRestClient().isInternetReachable()) {
			mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SD call", "NO_NETWORK");
			service.setError(new ServiceDiscovery.Error(OnErrorListener.ERRORVALUES.NO_NETWORK, "NO_NETWORK"));
			errorvalues = OnErrorListener.ERRORVALUES.NO_NETWORK;
		} else {
			if (urlBuild != null) {
				service = mRequestItemManager.execute(urlBuild, aisdurlType);
				if (service.isSuccess()) {
					holdbackTime = 0;   //remove hold back time
					mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SD call", "SD Fetched from server");
				} else {
					holdbackTime = new Date().getTime() + 10000; // curent time + 10 Seconds
					mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "SD call", service.getError().toString());
				}
			} else {
				service.setError(new ServiceDiscovery.Error(OnErrorListener.ERRORVALUES.UNKNOWN_ERROR, "URL is null"));
			}
		}
		return service;
	}

	private String getSDURLForType(AISDURLType aisdurlType) {

		String sector = null, micrositeid = null, environment = null;
		String url = null;
		AppConfigurationInterface.AppConfigurationError error = new AppConfigurationInterface
				.AppConfigurationError();
		final AppIdentityInterface identityManager = mAppInfra.getAppIdentity();
		final InternationalizationInterface localManager = mAppInfra.getInternationalization();
		final String locale = localManager.getUILocaleString();
		final AppIdentityInterface.AppState state = identityManager.getAppState();
		final String service_environment = identityManager.getServiceDiscoveryEnvironment();

		final String appState = getAppStateStringFromState(state);

		switch (aisdurlType) {
			case AISDURLTypePlatform:
				sector = "B2C";
				AppIdentityManager appIdentityManager = new AppIdentityManager(mAppInfra);
				micrositeid = (String) mAppInfra.getConfigInterface().getDefaultPropertyForKey
						("servicediscovery.platformMicrositeId", "appinfra", error);
				appIdentityManager.validateMicrositeId(micrositeid);

				String defSevicediscoveryEnv = (String) mAppInfra.getConfigInterface().getDefaultPropertyForKey
						("servicediscovery.platformEnvironment", "appinfra", error);

				Object dynServiceDiscoveryEnvironment = mAppInfra.getConfigInterface()
						.getPropertyForKey("servicediscovery.platformEnvironment", "appinfra",
								error);

				if (defSevicediscoveryEnv != null) {
					if (defSevicediscoveryEnv.equalsIgnoreCase("production")) // allow manual override only if static appstate != production
						environment = defSevicediscoveryEnv;
					else {
						if (dynServiceDiscoveryEnvironment != null)
							environment = dynServiceDiscoveryEnvironment.toString();
						else
							environment = defSevicediscoveryEnv;
					}
				}
				appIdentityManager.validateServiceDiscoveryEnv(environment);
				environment = getSDBaseURLForEnvironment(environment);
				if (micrositeid == null || micrositeid.isEmpty() || environment == null || environment.isEmpty()) {
					throw new IllegalArgumentException("Platform MicrositeId or Platform Service Environment is Missing");
				}

				break;

			case AISDURLTypeProposition:
				sector = identityManager.getSector();
				micrositeid = identityManager.getMicrositeId();
				environment = getSDBaseURLForEnvironment(service_environment);
				break;
		}
		if (sector != null && micrositeid != null &&
				locale != null && appState != null) {

			url = "https://" + environment + "/api/v1/discovery/" + sector
					+ "/" + micrositeid + "?locale=" +
					locale + "&tags=" + appState;


			String country = fetchFromSecureStorage(COUNTRY);
			if (country == null) {
				country = getCountryCodeFromSim();
				if (country != null) {
					countryCodeSource = OnGetHomeCountryListener.SOURCE.SIMCARD;
					saveToSecureStore(country, COUNTRY);
					saveToSecureStore(countryCodeSource.toString(), COUNTRY_SOURCE);
				}
			}
			if (country != null) {
				url += "&country=" + country;
			}
			mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "URL", "" + url);
		} else {
			mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "Build URL in SD", ""
					+ "Appidentity values are null");
		}
		return url;
	}

	private String getSDBaseURLForEnvironment(String serviceEnv) {
		String baseUrl;
		if (serviceEnv.equalsIgnoreCase(stateProduction)) {
			baseUrl = baseURLProduction;
		} else if (serviceEnv.equalsIgnoreCase(stateStaging)) {
			baseUrl = baseURLStaging;
		} else {
			baseUrl = baseURLProduction;
		}
		return baseUrl;
	}


	private String getAppStateStringFromState(AppIdentityInterface.AppState appState) {
		String appstate = null;
		switch (appState) {
			case TEST:
				appstate = URLTagTest;
				break;
			case DEVELOPMENT:
				appstate = URLTagDevelopment;
				break;
			case STAGING:
				appstate = URLTagStaging;
				break;
			case PRODUCTION:
				appstate = URLTagProduction;
				break;
			case ACCEPTANCE:
				appstate = URLTagAcceptance;
				break;
		}
		return appstate;
	}


	/**
	 * Gets the country from app according to settings/SIM/GEOIP
	 *
	 * @param listener callback.
	 */
	@Override
	public void getHomeCountry(final OnGetHomeCountryListener listener) {
		if (listener == null) {
			mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "Service Discovery",
					"OnGetServiceUrlListener is null initialized");
		} else {
			homeCountryCode(listener);
		}
	}


	@Override
	public void setHomeCountry(String countryCode) {
		if (countryCode != null && countryCode.length() == 2) {
			String country = fetchFromSecureStorage(COUNTRY);
			if (!countryCode.equals(country)) { // entered country is different then existing
				this.countryCode = countryCode;
				countryCodeSource = OnGetHomeCountryListener.SOURCE.STOREDPREFERENCE;
				saveToSecureStore(countryCode, COUNTRY);
				saveToSecureStore(countryCodeSource.toString(), COUNTRY_SOURCE);
				serviceDiscovery = null;  // if there is no internet then also old SD value must be cleared.
				mRequestItemManager.clearCacheServiceDiscovery(); // clear SD cache
				queueResultListener(true, new DownloadItemListener() {
					@Override
					public void onDownloadDone(AISDResponse result) {
						mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "Force Refresh is done", "Force Refresh is done");
					}
				});
			} else {
				mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "SAME COUNTRY", "Entered Country code is same as old one");
			}
		} else {
			mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.INFO, "Invalid COUNTRY", "Country code is INVALID");
		}
	}


	@Override
	public void getServiceUrlWithLanguagePreference(final String serviceId, final OnGetServiceUrlListener listener) {
		getURlwithLanguageOrCountry(serviceId, listener, null, AISDResponse.AISDPreference.AISDLanguagePreference);
	}

	@Override
	public void getServiceUrlWithLanguagePreference(final String serviceId, final OnGetServiceUrlListener listener,
	                                                final Map<String, String> replacement) {
		getURlwithLanguageOrCountry(serviceId, listener, replacement, AISDResponse.AISDPreference.AISDLanguagePreference);
	}

	@Override
	public void getServiceUrlWithCountryPreference(final String serviceId, final OnGetServiceUrlListener listener) {
		getURlwithLanguageOrCountry(serviceId, listener, null, AISDResponse.AISDPreference.AISDCountryPreference);
	}

	@Override
	public void getServiceUrlWithCountryPreference(final String serviceId, final OnGetServiceUrlListener listener,
	                                               final Map<String, String> replacement) {
		getURlwithLanguageOrCountry(serviceId, listener, replacement, AISDResponse.AISDPreference.AISDCountryPreference);
	}


	private void getURlwithLanguageOrCountry(final String serviceId, final OnGetServiceUrlListener urlListener,
	                                         final Map<String, String> replacement, final AISDResponse.AISDPreference preference) {
		if (urlListener == null) {
			mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "Service Discovery",
					"OnGetServiceUrlListener is null initialized");
		} else {
			if (serviceId == null || serviceId.isEmpty()) {
				urlListener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE, "INVALID_INPUT");
			} else {
				getServiceDiscoveryData(new AISDListener() {
					@Override
					public void ondataReceived(AISDResponse response) {
						if (response != null) {
							if (response.isSuccess()) {
								URL url = response.getServiceURL(serviceId, preference,
										replacement);
								if (url != null) {
									urlListener.onSuccess(url);
								} else {
									urlListener.onError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
											"ServiceDiscovery cannot find the locale");
								}
							} else if (response.getError() != null) {
								urlListener.onError(response.getError().getErrorvalue(),
										response.getError().getMessage());
							}
						} else {
							if (errorvalues != null) {
								urlListener.onError(OnErrorListener.ERRORVALUES.NO_NETWORK, "NO NETWORK");
							} else {
								urlListener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE,
										"INVALID RESPONSE OR DOWNLOAD FAILED");
							}
						}
					}
				});
			}
		}
	}


	@Override
	public void getServicesWithLanguagePreference(final ArrayList<String> serviceId, final OnGetServiceUrlMapListener listener) {
		getURlMAPwithLanguageOrCountry(serviceId, listener, null, AISDResponse.AISDPreference.AISDLanguagePreference);
	}

	@Override
	public void getServicesWithLanguagePreference(final ArrayList<String> serviceId, final OnGetServiceUrlMapListener listener,
	                                              final Map<String, String> replacement) {
		getURlMAPwithLanguageOrCountry(serviceId, listener, replacement, AISDResponse.AISDPreference.AISDLanguagePreference);
	}

	@Override
	public void getServicesWithCountryPreference(final ArrayList<String> serviceId, final OnGetServiceUrlMapListener listener) {
		getURlMAPwithLanguageOrCountry(serviceId, listener, null, AISDResponse.AISDPreference.AISDCountryPreference);
	}

	@Override
	public void getServicesWithCountryPreference(final ArrayList<String> serviceId, final OnGetServiceUrlMapListener listener,
	                                             final Map<String, String> replacement) {
		getURlMAPwithLanguageOrCountry(serviceId, listener, replacement, AISDResponse.AISDPreference.AISDCountryPreference);
	}


	private void getURlMAPwithLanguageOrCountry(final ArrayList<String> serviceIds, final OnGetServiceUrlMapListener urlMapListener,
	                                            final Map<String, String> replacement, final AISDResponse.AISDPreference preference) {
		if (urlMapListener == null) {
			mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "Service Discovery",
					"OnGetServiceUrlMapListener is null initialized");
		} else {
			if (serviceIds == null || serviceIds.isEmpty()) {
				urlMapListener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE, "INVALID_INPUT");
			} else {
				getServiceDiscoveryData(new AISDListener() {
					@Override
					public void ondataReceived(AISDResponse response) {
						if (response != null) {
							if (response.isSuccess()) {
								HashMap<String, ServiceDiscoveryService> responseMap = response.getServicesUrl(serviceIds,
										preference, replacement);
								if (responseMap != null && responseMap.size() > 0) {
									urlMapListener.onSuccess(responseMap);
								} else {
									urlMapListener.onError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
											"ServiceDiscovery cannot find the locale");
								}
							} else if (response.getError() != null) {
								urlMapListener.onError(response.getError().getErrorvalue(), response.getError().getMessage());
							}
						} else {
							if (errorvalues != null) {
								urlMapListener.onError(OnErrorListener.ERRORVALUES.NO_NETWORK, "NO NETWORK");
							} else {
								urlMapListener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE,
										"INVALID RESPONSE OR DOWNLOAD FAILED");
							}
						}
					}
				});
			}
		}
	}

	@Override
	public void getServiceLocaleWithLanguagePreference(final String serviceId, final OnGetServiceLocaleListener listener) {
		getServiceLocale(serviceId, listener, AISDResponse.AISDPreference.AISDLanguagePreference);
	}

	@Override
	public void getServiceLocaleWithCountryPreference(final String serviceId, final OnGetServiceLocaleListener listener) {
		getServiceLocale(serviceId, listener, AISDResponse.AISDPreference.AISDCountryPreference);
	}

	private void getServiceLocale(final String serviceId, final OnGetServiceLocaleListener localeListener,
	                              final AISDResponse.AISDPreference preference) {
		if (localeListener == null) {
			mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "Service Discovery",
					"OnGetServiceUrlMapListener is null initialized");
		} else {
			if (serviceId == null) {
				localeListener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE, "INVALID_INPUT");
			} else {
				getServiceDiscoveryData(new AISDListener() {
					@Override
					public void ondataReceived(AISDResponse response) {

						if (response != null) {
							if (response.isSuccess()) {
								String locale = response.getLocaleWithPreference(preference);
								if (locale != null) {
									localeListener.onSuccess(locale);
								} else {
									localeListener.onError(ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES.NO_SERVICE_LOCALE_ERROR,
											"ServiceDiscovery cannot find the locale");
								}
							} else if (response.getError() != null) {
								localeListener.onError(response.getError().getErrorvalue(), response.getError().getMessage());
							}
						} else {
							if (errorvalues != null) {
								localeListener.onError(OnErrorListener.ERRORVALUES.NO_NETWORK, "NO NETWORK");
							} else {
								localeListener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE,
										"INVALID RESPONSE OR DOWNLOAD FAILED");
							}
						}
					}
				});
			}
		}
	}


	@Override
	public URL applyURLParameters(URL inputURL, Map<String, String> parameters) {
		String url = inputURL.toString();
		URL output;
		if (parameters != null && parameters.size() > 0) {
			for (Map.Entry<String, String> param : parameters.entrySet()) {
				String key = param.getKey();
				String value = param.getValue();
				if (key != null && value != null)
					url = url.replace('%' + key + '%', value);
			}
		}
		try {
			output = new URL(url);
			return output;
		} catch (MalformedURLException ex) {
			mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "ServiceDiscovery URL error",
					"Malformed URL");
			return null;
		}
	}

	private void getServiceDiscoveryData(final AISDListener listener) {
		AISDResponse cachedData = mRequestItemManager.getCachedData();
		if (cachedData != null && !cachedURLsExpired()) {
			listener.ondataReceived(cachedData);
		} else {
			mRequestItemManager.clearCacheServiceDiscovery();
			queueResultListener(false, new DownloadItemListener() {
				@Override
				public void onDownloadDone(AISDResponse SDResponse) {
					if (SDResponse != null && SDResponse.isSuccess()) {
						listener.ondataReceived(SDResponse);
					} else {
						if (SDResponse != null && SDResponse.getError() != null) {
							ServiceDiscovery.Error err = SDResponse.getError();
							err.setErrorvalue(err.getErrorvalue());
							//   listener..onError(err.getErrorvalue(), err.getMessage());
						} else {
							listener.ondataReceived(null);
						}
					}
				}
			});
			//}
		}
	}


	@Override
	public void refresh(final OnRefreshListener listener) {
		refresh(listener, false);
	}

	@Override
	public void refresh(final OnRefreshListener listener, final boolean forcerefresh) {
		queueResultListener(forcerefresh, new DownloadItemListener() {
			@Override
			public void onDownloadDone(AISDResponse result) {
				if (result != null && result.isSuccess()) {
					listener.onSuccess();
				} else {
					if (result != null && result.getError() != null) {
						ServiceDiscovery.Error err = result.getError();
						listener.onError(err.getErrorvalue(), err.getMessage());
					} else {
						if (errorvalues != null) {
							listener.onError(OnErrorListener.ERRORVALUES.NO_NETWORK, "NO NETWORK");
						} else {
							listener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE,
									"INVALID RESPONSE OR DOWNLOAD FAILED");
						}
					}
				}
			}

			@Override
			public boolean forceRefresh() {
				return forcerefresh;
			}
		});
	}


	private void homeCountryCode(final OnGetHomeCountryListener listener) {

		String homeCountry = fetchFromSecureStorage(COUNTRY);
		String countrySource = fetchFromSecureStorage(COUNTRY_SOURCE);
		if (homeCountry == null && countrySource == null) {
			String countryCode = getCountryCodeFromSim();
			if (countryCode != null) {
				saveToSecureStore(countryCode, COUNTRY);
				countryCodeSource = OnGetHomeCountryListener.SOURCE.SIMCARD;
				saveToSecureStore(countryCodeSource.name(), COUNTRY_SOURCE);
				listener.onSuccess(countryCode, countryCodeSource);
			} else {
				queueResultListener(false, new DownloadItemListener() {
					@Override
					public void onDownloadDone(AISDResponse result) {
						if (result != null) {
							String country = result.getCountryCode();
							if (country != null) {
								if (countryCodeSource == null)
									countryCodeSource = OnGetHomeCountryListener.SOURCE.GEOIP;
								saveToSecureStore(country, COUNTRY);
								saveToSecureStore(countryCodeSource.name(), COUNTRY_SOURCE);
								listener.onSuccess(country, countryCodeSource);
							} else {
								if (result.getError() != null) {
									ServiceDiscovery.Error err = result.getError();
									listener.onError(err.getErrorvalue(), err.getMessage());
								}
							}
						} else {
							if (errorvalues != null) {
								listener.onError(OnErrorListener.ERRORVALUES.NO_NETWORK, "NO NETWORK");
							} else {
								listener.onError(OnErrorListener.ERRORVALUES.INVALID_RESPONSE,
										"INVALID RESPONSE OR DOWNLOAD FAILED");
							}
						}
					}
				});
			}
		} else {
			listener.onSuccess(homeCountry, OnGetHomeCountryListener.SOURCE.valueOf(countrySource.trim()));
		}
	}

	private String getCountryCodeFromSim() {
		try {
			final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			final String simCountry = tm.getSimCountryIso();
			if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
				countryCode = simCountry.toUpperCase(Locale.US);
			} else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { //
				final String networkCountry = tm.getNetworkCountryIso();
				if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
					countryCode = networkCountry.toUpperCase(Locale.US);
				}
			}
			return countryCode;
		} catch (Exception e) {
			mAppInfra.getAppInfraLogInstance().log(LoggingInterface.LogLevel.ERROR, "ServiceDiscovery URL error",
					e.toString());
		}
		return countryCode;
	}

	private void saveToSecureStore(String country, String countryCode) {
		SecureStorageInterface ssi = mAppInfra.getSecureStorage();
		SecureStorage.SecureStorageError mSecureStorage = new SecureStorage.SecureStorageError();
		if (countryCode.equals(COUNTRY)) {
			this.mCountry = country;
			ssi.storeValueForKey(COUNTRY, country, mSecureStorage);
		} else if (countryCode.equals(COUNTRY_SOURCE)) {
			this.mCountrySourceType = country;
			ssi.storeValueForKey(COUNTRY_SOURCE, country, mSecureStorage);
		}
	}

	private String fetchFromSecureStorage(String countrySource) {
		SecureStorageInterface ssi = mAppInfra.getSecureStorage();
		SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
		String value = null;
		if (countrySource.equals(COUNTRY)) {
			if (mCountry != null) {
				value = mCountry;
			} else {
				value = ssi.fetchValueForKey(COUNTRY, sse);
			}
		} else if (countrySource.equals(COUNTRY_SOURCE)) {
			if (mCountrySourceType != null) {
				value = mCountrySourceType;
			} else {
				value = ssi.fetchValueForKey(COUNTRY_SOURCE, sse);
			}
		}
		return value;
	}

	private boolean cachedURLsExpired() {
		String URLStringProposition = getSDURLForType(AISDURLType.AISDURLTypeProposition);
		String savedURLProposition = mRequestItemManager.getUrlProposition();

		String URLStringPlatform = getSDURLForType(AISDURLType.AISDURLTypePlatform);
		String savedURLPlatform = mRequestItemManager.getUrlPlatform();

		if (savedURLProposition != null && URLStringProposition != null) {
			//if previously saved URL differs from current proposition URL, URLs are expired
			if (!savedURLProposition.equals(URLStringProposition)) {
				return true;
			}
		} else {
			return true;
		}

		if (savedURLPlatform != null && URLStringPlatform != null) {
			//if previously saved URL differs from current platform URL, URLs are expired
			if (!savedURLPlatform.equals(URLStringPlatform)) {
				return true;
			}
		} else {
			return true;
		}

		if (mRequestItemManager.isServiceDiscoveryDataExpired()) {
			return true;
		}
		return false;
	}
}
