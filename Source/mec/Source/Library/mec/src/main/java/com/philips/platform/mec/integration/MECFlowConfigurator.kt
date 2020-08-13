/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.integration

import java.io.Serializable
import java.util.*


/**
 * MECFlowConfigurator is the public class for setting landing view of MEC library
 * @since 2001.0
 */
class MECFlowConfigurator : Serializable {


    var productCTNs: ArrayList<String>? =null

    /*
    *  This variable is used to pass a Product Category (eg: PRX Product Category, Hybris Product Category, etc)
    *  which will be used to fetch the relevant Products for the Category and display in the Product List screen.
    *
    *  You can pass this value while launching MEC with all the landing views, except mecOrderHistoryView(evn if you pass it will be for no use)
    *  This value will only be used when Hybris is available.
    *  @since 2004.0
    * */
    var productCategory : String?= null

     /**
      * landingView: enum to set Landing View
      * @since 2001.0
      */
    var landingView: MECLandingView? =null



    /**
     * ctns: set array of CTN number for categorised flow
     * @since 2001.0
     */
    fun setCTNs(ctns:ArrayList<String>){
        productCTNs =ctns
    }

    enum class MECLandingView{
            /**
             * @since 2001.0
             * MEC_PRODUCT_LIST_VIEW is used to launch the Product Catalogue Screen.
             */
             MEC_PRODUCT_LIST_VIEW,
            /**
             * @since 2001.0
             * MEC_PRODUCT_DETAILS_VIEW used to launch the Product Details view for the Product CTN passed.
             * One Product CTN has to be passed when launching with this Landing View.
             */
             MEC_PRODUCT_DETAILS_VIEW,
            /**
             * @since 2001.0
             * MEC_CATEGORIZED_PRODUCT_LIST_VIEW is used to launch the Product Catalogue Screen with a list of Product CTNs passed.
             * A list of one or more Product CTNs has to be passed when launching with this Landing View.
             */
             MEC_CATEGORIZED_PRODUCT_LIST_VIEW,
            /**
             * @since 2002.0
             * MEC_SHOPPING_CART_VIEW view is used to launch the shopping cart view.
             */
             MEC_SHOPPING_CART_VIEW,
            /**
             * @since 2003.0
             * MEC_ORDER_HISTORY is used to launch the order history view of an user.
             */
             MEC_ORDER_HISTORY
    }

}
