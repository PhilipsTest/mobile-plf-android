/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address


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
import com.philips.platform.mec.databinding.MecAddressCreateBinding
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.address.region.RegionViewModel
import com.philips.platform.mec.screens.shoppingCart.EcsShoppingCartViewModel
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.uid.view.widget.InputValidationLayout
import com.philips.platform.uid.view.widget.ValidationEditText
import java.io.Serializable


class AddAddressFragment : MecBaseFragment() {
    override fun getFragmentTag(): String {
        return "AddAddressFragment"
    }


    private lateinit var mECSShoppingCart: ECSShoppingCart
    private lateinit var ecsShoppingCartViewModel: EcsShoppingCartViewModel
    private var addressFieldEnabler: MECAddressFieldEnabler? = null

    lateinit var binding: MecAddressCreateBinding

    lateinit var addressViewModel: AddressViewModel

    lateinit var regionViewModel: RegionViewModel


    var isError = false
    var validationEditText : ValidationEditText ? =null

    var eCSAddressShipping : ECSAddress = ECSAddress()

    private var eCSAddressBilling : ECSAddress = ECSAddress()

    private var mAddressList: List<ECSAddress>? = null

    var mecRegions : MECRegions? = null


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

    private val createAddressObserver: Observer<ECSAddress> = Observer { ecsAddress ->
        Log.d(this@AddAddressFragment.javaClass.name, ecsAddress?.id)
        addressViewModel.setDeliveryAddress(ecsAddress!!)
    }

    private val fetchAddressObserver: Observer<List<ECSAddress>> = Observer(fun(addressList: List<ECSAddress>?) {
        mAddressList = addressList
        gotoDeliveryAddress(mAddressList)
    })

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

        binding = MecAddressCreateBinding.inflate(inflater, container, false)
        binding.pattern = MECSalutationHolder(context)

        addressViewModel = ViewModelProviders.of(this).get(AddressViewModel::class.java)
        regionViewModel = activity?.let { ViewModelProviders.of(it).get(RegionViewModel::class.java) }!!

        mECSShoppingCart = arguments?.getSerializable(MECConstant.KEY_ECS_SHOPPING_CART)!! as ECSShoppingCart


        //Set Country before binding
        eCSAddressShipping.country =addressViewModel.getCountry()
        eCSAddressBilling.country = addressViewModel.getCountry()

        //set First Name
        val firstName = MECDataHolder.INSTANCE.getUserInfo().firstName
        if(!firstName.isNullOrEmpty() && !firstName.equals("null",true)){
            eCSAddressShipping.firstName = firstName
            eCSAddressBilling.firstName = firstName
        }

        //set Last Name
        val lastName = MECDataHolder.INSTANCE.getUserInfo().lastName
        if(!lastName.isNullOrEmpty() && !lastName.equals("null",true)){
            eCSAddressShipping.lastName = lastName
            eCSAddressBilling.lastName = lastName
        }


        binding.ecsAddressShipping = eCSAddressShipping
        binding.ecsAddressBilling = eCSAddressBilling




        regionViewModel.regionsList.observe(activity!!, regionListObserver)
        regionViewModel.mecError.observe(this,this)

        addressViewModel.mecError.observe(this, this)
        addressViewModel.eCSAddress.observe(this,createAddressObserver)
        addressViewModel.ecsAddresses.observe(this,fetchAddressObserver)
        addressViewModel.isDeliveryAddressSet.observe(this,setDeliveryAddressObserver)

        ecsShoppingCartViewModel = ViewModelProviders.of(this).get(EcsShoppingCartViewModel::class.java)
        ecsShoppingCartViewModel.ecsShoppingCart.observe(this, cartObserver)
        ecsShoppingCartViewModel.mecError.observe(this,this)


        addressFieldEnabler = context?.let { addressViewModel.getAddressFieldEnabler(ECSConfiguration.INSTANCE.country, it) }

        binding.addressFieldEnabler = addressFieldEnabler


        binding.btnContinue.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                //Re assign
                isError = false
                validationEditText = null

                validateEditTexts(binding.addressContainer)

                if(isError){
                    validationEditText?.requestFocus()

                }else{

                    // update region properly ..as two way data binding for region is not possible : Shipping
                    val ecsAddressShipping = binding.ecsAddressShipping
                    ecsAddressShipping?.phone2 = ecsAddressShipping?.phone1
                    addressViewModel.setRegion(binding.llShipping,binding.mecRegions,ecsAddressShipping!!)


                    if(binding.billingCheckBox.isChecked){ // assign shipping address to billing address if checkbox is checked
                        eCSAddressBilling = ecsAddressShipping
                    }else{
                        eCSAddressBilling = binding.ecsAddressBilling!!
                        eCSAddressBilling.phone2 = eCSAddressBilling.phone1
                        // update region properly ..as two way data binding for region is not possible : Billing
                        addressViewModel.setRegion(binding.llBilling,binding.mecRegions,eCSAddressBilling)
                    }

                    addressViewModel.createAddress(ecsAddressShipping)
                    showProgressBar(binding.mecProgress.mecProgressBarContainer)
                }
            }
        })

        return binding.root
    }

    private fun validateEditTexts(v: ViewGroup){

        for (i in 0 until v.childCount) {
            val child = v.getChildAt(i)

            if (v is InputValidationLayout && child is ValidationEditText && child.visibility == View.VISIBLE) {

                Log.d("MEC",child.hint.toString())

                var validator:InputValidationLayout.Validator

                if(child.inputType == InputType.TYPE_CLASS_PHONE){
                    validator = PhoneNumberInputValidator(child, PhoneNumberUtil.getInstance())
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

    private fun gotoDeliveryAddress(addressList: List<ECSAddress>?) {
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        val deliveryFragment = MECDeliveryFragment()
        val bundle = Bundle()
        bundle.putSerializable(MECConstant.KEY_ECS_ADDRESSES, addressList as Serializable)
        bundle.putSerializable(MECConstant.KEY_ECS_BILLING_ADDRESS,eCSAddressBilling)
        bundle.putSerializable(MECConstant.KEY_ECS_SHOPPING_CART , mECSShoppingCart)
        deliveryFragment.arguments = bundle
        replaceFragment(deliveryFragment, MECDeliveryFragment().getFragmentTag(), true)
    }

    override fun processError(mecError: MecError?, showDialog: Boolean) {


        if(mecError?.mECRequestType == MECRequestType.MEC_CREATE_ADDRESS){
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



