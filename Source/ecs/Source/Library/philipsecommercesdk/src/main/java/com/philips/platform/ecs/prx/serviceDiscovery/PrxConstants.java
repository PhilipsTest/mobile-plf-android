/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.prx.serviceDiscovery;

public class PrxConstants {
	
	public static final String PRX_NETWORK_WRAPPER = "PRXNetworkWrapper";
	public static final String PRX_REQUEST_MANAGER = "PRXRequestManager";

	public enum Catalog {

		DEFAULT,

		CONSUMER,

		NONCONSUMER,

		CARE,

		PROFESSIONAL,

		LP_OEM_ATG,

		LP_PROF_ATG,

		HC,

		HHSSHOP,

		MOBILE,

		EXTENDEDCONSENT

	}

	public enum Sector {

		DEFAULT,

		B2C,

		B2B_LI,

		B2B_HC

	}

}
