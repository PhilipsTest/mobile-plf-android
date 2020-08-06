package com.philips.cdp.prxclient

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