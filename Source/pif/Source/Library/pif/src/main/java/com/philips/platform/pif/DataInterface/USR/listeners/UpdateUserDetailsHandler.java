
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.platform.pif.DataInterface.USR.listeners;

import com.philips.platform.pif.DataInterface.USR.enums.Error;

/**
 * It is a callback class to proposition for updating user details
 * @since 1903
 *
 */
public interface UpdateUserDetailsHandler {

	/**
	 * {@code onUpdateSuccess}method is invoked on update receive marketing email, gender and up[date date of birth success
	 * @since 1903
	 */
	public void onUpdateSuccess();

	/**
	 * {@code onUpdateFailedWithError}method is invoked on  update receive marketing email, gender and up[date date of birth fails with error
	 * @param error  gives error code in integer when updating User fails
	 * @since 1903
     */
	public void onUpdateFailedWithError(Error error);

}
