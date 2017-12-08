
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.handlers;

public interface AddConsumerInterestHandler {

	/**
	 * {@codeonAddConsumerInterestSuccess } method to validate on add consumer interest success
	 */
	public void onAddConsumerInterestSuccess();

	/**
	 * {@code onAddConsumerInterestFailedWithError} mehtod to validate on add consumer interest failed with error
	 * @param error
     */
	public void onAddConsumerInterestFailedWithError(int error);

}
