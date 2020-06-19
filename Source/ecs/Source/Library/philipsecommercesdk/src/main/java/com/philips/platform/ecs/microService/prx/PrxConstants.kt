/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */
package com.philips.platform.ecs.microService.prx

object PrxConstants {
    const val PRX_NETWORK_WRAPPER = "PRXNetworkWrapper"
    const val PRX_REQUEST_MANAGER = "PRXRequestManager"

    enum class Catalog {
        DEFAULT, CONSUMER, NONCONSUMER, CARE, PROFESSIONAL, LP_OEM_ATG, LP_PROF_ATG, HC, HHSSHOP, MOBILE, EXTENDEDCONSENT
    }

    enum class Sector {
        DEFAULT, B2C, B2B_LI, B2B_HC
    }
}