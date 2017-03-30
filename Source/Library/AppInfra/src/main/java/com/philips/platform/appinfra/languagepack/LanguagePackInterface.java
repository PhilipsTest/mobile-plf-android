/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.appinfra.languagepack;


/**
 * Created by philips on 3/13/17.
 */

public interface LanguagePackInterface {

	/**
	 * download language pack overview file.
	 * If should be called everytime when app is launched
	 *
	 * @param refreshListener asynchronous callback reporting result of refresh eg {LoadedFromLocalCache, RefreshedFromServer, NoRefreshRequired, RefreshFailed}
	 */
	void refresh(OnRefreshListener refreshListener);

	/**
	 *  It activates device matching locale from downloaded overview file
	 *  Calling activate will return path of Language pack through call back listener
	 * @param onActivateListener asynchronous callback reporting result of activate
	 */
	void activate(OnActivateListener onActivateListener);

	interface OnRefreshListener {
		void onError(AILPRefreshResult error, String message);

		void onSuccess(AILPRefreshResult result);

		enum AILPRefreshResult {LoadedFromLocalCache, RefreshedFromServer, NoRefreshRequired, RefreshFailed}
	}

	interface OnActivateListener {
		void onSuccess(String path);

		void onError(AILPActivateResult ailpActivateResult);

		enum AILPActivateResult {REFRESH_NOT_CALLED, SOMETHING_WENT_WRONG}
	}
}

