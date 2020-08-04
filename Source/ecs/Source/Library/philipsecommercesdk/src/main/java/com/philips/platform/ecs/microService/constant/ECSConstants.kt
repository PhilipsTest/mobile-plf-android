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

package com.philips.platform.ecs.microService.constant

class ECSConstants {

    companion object {

        const val CONFIG = "config"

        //Service IDS
        const val SERVICEID_IAP_BASEURL = "iap.baseurl"
        const val SERVICEID_PRX_ASSETS = "prxclient.assets"
        const val SERVICEID_PRX_DISCLAIMERS= "prxclient.disclaimers"
        const val SERVICEID_PRX_SUMMARY_LIST = "prxclient.summarylist"
        const val SERVICEID_ECS_PRODUCT_DETAILS = "ecs.productDetails"
        const val SERVICEID_ECS_PRODUCTS = "ecs.productSearch"
        const val SERVICEID_ECS_RETAILERS = "ecs.wtbURL"
        const val SERVICEID_ECS_CREATE_CART = "ecs.createCart"
        const val SERVICEID_ECS_GET_CART    = "ecs.getCart"
        const val SERVICEID_ECS_ADD_TO_CART = "ecs.addToCart"
        const val SERVICEID_ECS_UPDATE_CART = "ecs.updateCart"
        const val SERVICEID_ECS_NOTIFY_ME = "ecs.notifyMe"
    }


}