
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.handlers;

/**
 * It is a callback class to proposition for handling refresh User
 * @since 1.0.0
 * @deprecated since 1903
 */
//@Deprecated
public interface RefreshUserHandler {

	/**
	 *{@code onRefreshUserSuccess} method is invoked on refresh User success
	 *
	 * @since 1.0.0
	 */
	public void onRefreshUserSuccess();

	/**
	 * {@code onRefreshUserFailed} method is invoked on refresh user fails
	 * @param error  error code when refresh user fails
	 *
	 * @since 1.0.0
     */
	public void onRefreshUserFailed(int error);

}
