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

import androidx.lifecycle.MutableLiveData
import com.philips.cdp.prxclient.datamodels.cdls.CDLSDataModel
import com.philips.cdp.prxclient.error.PrxError
import com.philips.cdp.prxclient.response.ResponseData
import com.philips.cdp.prxclient.response.ResponseListener
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.error.ECSErrorEnum
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECLog

class PRXContactsResponseCallback(private val mecOrderDetailViewModel: MECOrderDetailViewModel)  : ResponseListener {

    override fun onResponseError(prxError: PrxError?) {
        MECLog.d("pabitra",prxError?.description)
        val exception = Exception(prxError?.description)
        var ecsError= ECSError(5999, ECSErrorEnum.ECSsomethingWentWrong.localizedErrorString)
        val mecError = MecError(exception, ecsError, MECRequestType.MEC_FETCH_ORDER_HISTORY)
        var mMecErrorLiveData = MutableLiveData(mecError)
        mecOrderDetailViewModel.mecError=mMecErrorLiveData
    }

    override fun onResponseSuccess(responseData: ResponseData?) {
        var CDLSDataModel: CDLSDataModel =  responseData as CDLSDataModel
        mecOrderDetailViewModel.contactPhone.value = CDLSDataModel.data.phone.get(0)
    }
}