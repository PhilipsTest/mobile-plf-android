package com.philips.platform.mec.screens.address

import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.platform.mec.R
import com.philips.platform.mec.utils.MECDataHolder

class AddressService{


    fun setEnglishSalutation(ecsAddress: ECSAddress){

        val localizedSalutation = ecsAddress.titleCode
        val context = MECDataHolder.INSTANCE.appinfra.appInfraContext

        if (localizedSalutation.equals(context.getString(R.string.mec_mr), ignoreCase = true)) {
            ecsAddress.titleCode = SalutationEnum.MR.englishSalutation
        }else if (localizedSalutation.equals(context.getString(R.string.mec_mrs), ignoreCase = true)){
            ecsAddress.titleCode = SalutationEnum.MS.englishSalutation
        }
    }
}