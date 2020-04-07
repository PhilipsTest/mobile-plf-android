/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address

import com.philips.cdp.di.ecs.ECSServices
import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.cdp.di.ecs.model.address.ECSDeliveryMode

class AddressRepository(val ecsServices: ECSServices) {


    private var addressService = AddressService()

    fun fetchSavedAddresses( eCSFetchAddressesCallback: ECSFetchAddressesCallback) {
        ecsServices.fetchSavedAddresses(eCSFetchAddressesCallback)
    }

    fun createAddress(ecsAddress: ECSAddress , ecsCreateAddressCallBack :ECSCreateAddressCallBack) {
        addressService.setEnglishSalutation(ecsAddress) //set salutation to english ,while doing service call
        ecsServices.createAddress(ecsAddress,ecsCreateAddressCallBack)
    }

    fun updateAndFetchAddress(ecsAddress: ECSAddress,ecsFetchAddressesCallback: ECSFetchAddressesCallback){
        addressService.setEnglishSalutation(ecsAddress) //set salutation to english ,while doing service call
        ecsServices.updateAndFetchAddress(ecsAddress,ecsFetchAddressesCallback)
    }

    fun updateAddress(ecsAddress: ECSAddress,updateAddressCallBack: UpdateAddressCallBack){
        addressService.setEnglishSalutation(ecsAddress) //set salutation to english ,while doing service call
        ecsServices.updateAddress(ecsAddress,updateAddressCallBack)
    }

    fun setAndFetchDeliveryAddress(ecsAddress: ECSAddress, ecsFetchAddressesCallback: ECSFetchAddressesCallback) {
        ecsServices.setAndFetchDeliveryAddress(true,ecsAddress,ecsFetchAddressesCallback)
    }

    fun setDeliveryAddress(ecsAddress: ECSAddress, setDeliveryAddressCallBack: SetDeliveryAddressCallBack ) {
        ecsServices.setDeliveryAddress(true,ecsAddress,setDeliveryAddressCallBack)
    }

    fun fetchDeliveryModes(eCSFetchDeliveryModesCallback :ECSFetchDeliveryModesCallback ){
        ecsServices.fetchDeliveryModes(eCSFetchDeliveryModesCallback)
    }

    fun setDeliveryMode(ecsDeliveryMode: ECSDeliveryMode, ecsSetDeliveryModesCallback: ECSSetDeliveryModesCallback){
        ecsServices.setDeliveryMode(ecsDeliveryMode,ecsSetDeliveryModesCallback)
    }

    fun createAndFetchAddress(ecsAddress: ECSAddress, ecsFetchAddressesCallback: ECSFetchAddressesCallback) {
        addressService.setEnglishSalutation(ecsAddress) //set salutation to english ,while doing service call
        ecsServices.createAndFetchAddress(ecsAddress,ecsFetchAddressesCallback)
    }

    fun deleteAndFetchAddress(ecsAddress: ECSAddress, ecsFetchAddressesCallback: ECSFetchAddressesCallback){
        ecsServices.deleteAndFetchAddress(ecsAddress,ecsFetchAddressesCallback)
    }

    fun deleteAddress(ecsAddress: ECSAddress, deleteAddressCallBack: DeleteAddressCallBack) {
        ecsServices.deleteAddress(ecsAddress,deleteAddressCallBack)
    }


}