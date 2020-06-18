/* Copyright (c) Koninklijke Philips N.V., 2020
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.mec.screens.shoppingCart

import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.error.ECSErrorEnum
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.voucher.ECSVoucher
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECutility

class ECSVoucherCallback(private var ecsShoppingCartViewModel: EcsShoppingCartViewModel) : ECSCallback<List<ECSVoucher>, Exception> {
    lateinit var mECRequestType :MECRequestType
    override fun onResponse(ecsVoucher: List<ECSVoucher>?) {
        ecsShoppingCartViewModel.tagApplyOrDeleteVoucher(mECRequestType)
        ecsShoppingCartViewModel.getShoppingCart()
    }

    override fun onFailure(error: Exception?, ecsError: ECSError?) {

        if( MECutility.isAuthError(ecsError)){
            ecsShoppingCartViewModel.retryAPI(mECRequestType)
        }else{
            val mecError = MecError(error, ecsError,mECRequestType )
            ecsShoppingCartViewModel.mecError.value = mecError
        }
    }
}