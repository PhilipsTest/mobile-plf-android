/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart
import com.philips.cdp.di.ecs.model.region.ECSRegion
import com.philips.cdp.di.ecs.util.ECSConfiguration
import com.philips.platform.mec.R
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.databinding.MecAddressEditBinding
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.address.region.RegionViewModel
import com.philips.platform.mec.screens.shoppingCart.EcsShoppingCartViewModel
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.uid.view.widget.InputValidationLayout
import com.philips.platform.uid.view.widget.ValidationEditText
import java.io.Serializable


class CreateOrEditAddressFragment : MecBaseFragment() {
    override fun getFragmentTag(): String {
        return "EditAddressFragment"
    }


    private lateinit var ecsShoppingCartViewModel: EcsShoppingCartViewModel
    private var mAddressList: List<ECSAddress>? = null
    private var mECSShoppingCart: ECSShoppingCart? = null
    private lateinit var regionViewModel: RegionViewModel

    private lateinit var ecsAddress: ECSAddress
    private var addressFieldEnabler: MECAddressFieldEnabler? = null

    lateinit var binding: MecAddressEditBinding

    lateinit var addressViewModel: AddressViewModel

    var isError = false
    var validationEditText : ValidationEditText ? =null

    var mecRegions :MECRegions ? = null

    private val regionListObserver: Observer<List<ECSRegion>> = Observer { regionList ->
        mecRegions = MECRegions(regionList!!)
        binding.mecRegions = mecRegions
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
    }


    private val setDeliveryAddressObserver: Observer<Boolean> = Observer {isAddressSet->

        if(isAddressSet){
            ecsShoppingCartViewModel.getShoppingCart()
        }else{
            addressViewModel.fetchAddresses()
        }

    }

    //TODO call List of Address API on update Address
    private val updateAddressObserver: Observer<Boolean> = Observer {isAddressUpdated->
            if(isAddressUpdated)ecsShoppingCartViewModel.getShoppingCart()
    }


    private val createAddressObserver: Observer<ECSAddress> = Observer { ecsAddress ->
        Log.d(this@CreateOrEditAddressFragment.javaClass.name, ecsAddress?.id)
        addressViewModel.setDeliveryAddress(ecsAddress!!)
    }

    private val fetchAddressObserver: Observer<List<ECSAddress>> = Observer(fun(addressList: List<ECSAddress>?) {
        mAddressList = addressList
        gotoDeliveryAddress(addressList)
    })

    private fun gotoDeliveryAddress(mAddressList: List<ECSAddress> ?) {

        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        val intent = Intent()
        val bundle = Bundle()
        bundle.putSerializable(MECConstant.KEY_ECS_ADDRESSES,mAddressList as Serializable)

        if(mECSShoppingCart!=null) {
            bundle.putSerializable(MECConstant.KEY_ECS_SHOPPING_CART, mECSShoppingCart)
        }

        intent.putExtra(MECConstant.BUNDLE_ADDRESSES,bundle)
        targetFragment?.onActivityResult(MECConstant.REQUEST_CODE_ADDRESSES, Activity.RESULT_OK,intent)
        activity?.supportFragmentManager?.popBackStack()

    }

    private val cartObserver: Observer<ECSShoppingCart> = Observer { ecsShoppingCart ->
        mECSShoppingCart = ecsShoppingCart
        addressViewModel.fetchAddresses()
    }

    override fun onResume() {
        super.onResume()
        setCartIconVisibility(false)
        setTitleAndBackButtonVisibility(R.string.mec_address, true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = MecAddressEditBinding.inflate(inflater, container, false)
        binding.pattern = MECSalutationHolder(context)


        addressViewModel = ViewModelProviders.of(this).get(AddressViewModel::class.java)

        ecsAddress = arguments?.getSerializable(MECConstant.KEY_ECS_ADDRESS) as ECSAddress
        binding.ecsAddress = ecsAddress


        addressViewModel.ecsAddresses.observe(this,fetchAddressObserver)
        addressViewModel.eCSAddress.observe(this,createAddressObserver)
        addressViewModel.isDeliveryAddressSet.observe(this,setDeliveryAddressObserver)
        addressViewModel.mecError.observe(this, this)
        addressViewModel.isAddressUpdate.observe(this,updateAddressObserver)


        regionViewModel = activity?.let { ViewModelProviders.of(it).get(RegionViewModel::class.java) }!!
        regionViewModel.regionsList.observe(activity!!, regionListObserver)
        regionViewModel.mecError.observe(this,this)

        ecsShoppingCartViewModel = ViewModelProviders.of(this).get(EcsShoppingCartViewModel::class.java)
        ecsShoppingCartViewModel.ecsShoppingCart.observe(this, cartObserver)
        ecsShoppingCartViewModel.mecError.observe(this,this)

        addressFieldEnabler = context?.let { addressViewModel.getAddressFieldEnabler(ECSConfiguration.INSTANCE.country, it) }

        binding.addressFieldEnabler = addressFieldEnabler


        binding.btnContinue.setOnClickListener {

            //Re assign
            isError = false
            validationEditText = null

            validateEditTexts(binding.addressContainer)

            if(isError){
                validationEditText?.requestFocus()

            }else{
                // update region properly ..as two way data binding for region is not possible
                val ecsAddress = binding.ecsAddress
                addressViewModel.setRegion(binding.llShipping,binding.mecRegions,ecsAddress!!)
                ecsAddress.phone2 = ecsAddress.phone1

                if(ecsAddress.id !=null) { // This means address already existed , so need to create it again
                    addressViewModel.updateAddress(ecsAddress)
                }else{
                    addressViewModel.createAddress(ecsAddress)
                }

                showProgressBar(binding.mecProgress.mecProgressBarContainer)
            }
        }

        return binding.root
    }

    private fun validateEditTexts(v: ViewGroup){

        for (i in 0 until v.childCount) {
            val child = v.getChildAt(i)

            if (v is InputValidationLayout && child is ValidationEditText && child.visibility == View.VISIBLE) {

                Log.d("MEC",child.hint.toString())

                var validator:InputValidationLayout.Validator

                if(child.inputType == InputType.TYPE_CLASS_PHONE){
                    validator = PhoneNumberInputValidator(child , PhoneNumberUtil.getInstance())
                }else{
                    validator = EmptyInputValidator()
                }

                if(!validator.validate(child.text.toString())){
                    child.startAnimation(addressViewModel.shakeError())
                    if(validationEditText==null) {
                        validationEditText = child
                    }
                    v.showError()
                    isError = true
                }

            } else if (child is ViewGroup) {

                if(child.visibility == View.VISIBLE) {
                    validateEditTexts(child)  // Recursive call.
                }

            }
        }
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(addressFieldEnabler?.isStateEnabled!! && mecRegions==null) {
            regionViewModel.fetchRegions()
           showProgressBar(binding.mecProgress.mecProgressBarContainer)
        }
    }

    override fun processError(mecError: MecError?, showDialog: Boolean) {

        if(mecError?.mECRequestType == MECRequestType.MEC_CREATE_ADDRESS || mecError?.mECRequestType == MECRequestType.MEC_UPDATE_ADDRESS){
            super.processError(mecError, showDialog)
        }else{
            if(mecError?.mECRequestType == MECRequestType.MEC_FETCH_SHOPPING_CART){
                addressViewModel.fetchAddresses()
                showProgressBar(binding.mecProgress.mecProgressBarContainer)
            }else {
                var errorMessage = mecError!!.exception!!.message
                MECLog.e(javaClass.simpleName, errorMessage)
                MECutility.tagAndShowError(mecError, false, fragmentManager, context)
            }
        }

        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
    }

    override fun onStop() {
        super.onStop()
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
    }

}



