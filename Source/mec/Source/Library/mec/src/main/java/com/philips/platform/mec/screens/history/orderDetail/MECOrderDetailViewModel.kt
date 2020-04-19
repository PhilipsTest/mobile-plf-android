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
import androidx.lifecycle.MutableLiveData
import com.philips.cdp.prxclient.datamodels.contacts.ContactsModel
import com.philips.platform.mec.common.CommonViewModel
import com.philips.platform.mec.utils.MECDataHolder

class MECOrderDetailViewModel : CommonViewModel() {

    var contacts = MutableLiveData<ContactsModel>()
    var prxContactsResponseCallback = PRXContactsResponseCallback(this)
    var ecsService = MECDataHolder.INSTANCE.eCSServices
    var mecOrderDetailRepository = MECOrderDetailRepository()

    fun fetchContacts(context: Context,productCategory : String){
       MECOrderDetailRepository().fetchContacts(context,productCategory,prxContactsResponseCallback)
    }
}