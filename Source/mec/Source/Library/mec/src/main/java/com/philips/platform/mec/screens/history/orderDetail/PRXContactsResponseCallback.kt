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

import com.philips.cdp.prxclient.datamodels.contacts.ContactsModel
import com.philips.cdp.prxclient.error.PrxError
import com.philips.cdp.prxclient.response.ResponseData
import com.philips.cdp.prxclient.response.ResponseListener

class PRXContactsResponseCallback(private val mecOrderDetailViewModel: MECOrderDetailViewModel)  : ResponseListener {

    override fun onResponseError(prxError: PrxError?) {
        // Assign PRX error to MECError TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResponseSuccess(responseData: ResponseData?) {
        mecOrderDetailViewModel.contacts.value = responseData as ContactsModel
    }
}