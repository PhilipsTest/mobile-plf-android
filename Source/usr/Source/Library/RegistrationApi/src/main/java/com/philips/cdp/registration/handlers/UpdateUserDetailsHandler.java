
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.handlers;

/**
 * It is a call back class to proposition for updating user details
 * @since 1.0.0
 */
public interface UpdateUserDetailsHandler {

	/**
	 * {@code onUpdateSuccess}method is invoked on update receive marketing email success
	 * @since 1.0.0
	 */
	public void onUpdateSuccess();

	/**
	 * {@code onUpdateFailedWithError}method is invoked on  update receive when marketing email fails with error
	 * @param error  gives error code in integer when updating User fails
	 * @since 1.0.0
     */
	public void onUpdateFailedWithError(int error);

}
