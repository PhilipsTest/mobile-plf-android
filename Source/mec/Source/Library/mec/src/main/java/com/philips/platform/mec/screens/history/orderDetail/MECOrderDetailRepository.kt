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

package com.philips.platform.mec.screens.history.orderDetail

import android.content.Context
import com.philips.cdp.prxclient.PRXDependencies
import com.philips.cdp.prxclient.RequestManager
import com.philips.cdp.prxclient.request.CustomerCareContactsRequest
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder

class MECOrderDetailRepository{

    fun fetchContacts(context: Context ,productCategory: String, prxContactsResponseCallback: PRXContactsResponseCallback) {

        var customerCareContactsRequest  = CustomerCareContactsRequest(productCategory)
        val mRequestManager = RequestManager()
        val prxDependencies = PRXDependencies(context, MECDataHolder.INSTANCE.appinfra, MECConstant.COMPONENT_NAME)
        mRequestManager.init(prxDependencies)
        mRequestManager.executeRequest(customerCareContactsRequest, prxContactsResponseCallback)
    }
}