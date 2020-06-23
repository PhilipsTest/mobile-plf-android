
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.handlers;

/**
 * It is a callback class to proposition for updating  consumer interest
 * @since 1.0.0
 */
// Deprecated  since 1903
public interface UpdateConsumerInterestHandler {

	/**
	 * {@code onUpdateConsumerInterestSuccess} method is invoked on update consumer interest success
	 * @since 1.0.0
	 */
	public void onUpdateConsumerInterestSuccess();

	/**
	 * {@code onUpdateConsumerInterestFailedWithError}method is invoked on update consumer interest failed with error
	 * @param error  gives the janrain error
	 * @since 1.0.0
     */
	public void onUpdateConsumerInterestFailedWithError( com.janrain.android.capture.CaptureApiError error);

}
