/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.integration

import java.io.Serializable
import java.util.*

/**
 * MECFlowConfigurato initializes the required ctn’s for MEC to set the flow of micro app.
 * @since 2001.0
 */
class MECFlowConfigurator : Serializable {


    var productCTNs: ArrayList<String>? =null

    var landingView: MECLandingView? =null


    /**
     * ctns: set array of CTN number for categorised flow
     * @since 2001.0
     */
    fun setCTNs(ctns:ArrayList<String>){
        productCTNs =ctns
    }

    enum class MECLandingView{
        MEC_PRODUCT_LIST_VIEW,                //landing view is used to launch the Product Catalogue Screen.
        MEC_PRODUCT_DETAILS_VIEW,             //landing view is used to launch the Product Details view for the Product CTN passed
        MEC_CATEGORIZED_PRODUCT_LIST_VIEW,    //landing view is used to launch the Product Catalogue Screen with a list of Product CTNs passed.
        MEC_SHOPPING_CART_VIEW,               //landing view is used to launch the shopping cart view
        MEC_ORDER_HISTORY                     //landing view is used to launch the order history view of an user
    }

}
