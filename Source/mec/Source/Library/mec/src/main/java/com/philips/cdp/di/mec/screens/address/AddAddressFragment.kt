package com.philips.cdp.di.mec.screens.address


import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.philips.cdp.di.ecs.model.region.ECSRegion
import com.philips.cdp.di.mec.screens.MecBaseFragment
import com.philips.platform.uid.view.widget.InputValidationLayout
import com.philips.platform.uid.view.widget.ValidationEditText
import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart
import com.philips.cdp.di.ecs.util.ECSConfiguration
import com.philips.cdp.di.mec.R
import com.philips.cdp.di.mec.common.MecError
import com.philips.cdp.di.mec.databinding.MecAddressCreateBinding
import com.philips.cdp.di.mec.screens.address.region.RegionViewModel
import com.philips.cdp.di.mec.utils.MECConstant
import com.philips.cdp.di.mec.utils.MECDataHolder
import kotlinx.android.synthetic.main.mec_main_activity.*
import java.io.Serializable


class AddAddressFragment : MecBaseFragment() {
    override fun getFragmentTag(): String {
        return "AddAddressFragment"
    }


    private var addressFieldEnabler: MECAddressFieldEnabler? = null

    lateinit var binding: MecAddressCreateBinding

    lateinit var addressViewModel: AddressViewModel

    lateinit var regionViewModel: RegionViewModel


    var isError = false
    var validationEditText : ValidationEditText ? =null

    var eCSAddressShipping : ECSAddress = ECSAddress()

    var eCSAddressBilling : ECSAddress = ECSAddress()

    var mAddressList: List<ECSAddress>? = null

    var mecRegions : MECRegions? = null


    private val regionListObserver: Observer<List<ECSRegion>> = object : Observer<List<ECSRegion>> {

        override fun onChanged(regionList: List<ECSRegion>?) {

            mecRegions = MECRegions(regionList!!)
            binding.mecRegions = mecRegions
            dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        }

    }

    private val createAddressObserver: Observer<ECSAddress> = object : Observer<ECSAddress> {

        override fun onChanged(ecsAddress: ECSAddress?) {

            Log.d(this@AddAddressFragment.javaClass.name, ecsAddress?.id)
            addressViewModel.setAndFetchDeliveryAddress(ecsAddress!!)
        }

    }

    private val addressObserver: Observer<List<ECSAddress>> = Observer(fun(addressList: List<ECSAddress>?) {
        mAddressList = addressList

    })


    override fun onResume() {
        super.onResume()
        setCartIconVisibility(false)
        setTitleAndBackButtonVisibility(R.string.mec_address, true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = MecAddressCreateBinding.inflate(inflater, container, false)
        binding.pattern = MECSalutationHolder()

        addressViewModel = ViewModelProviders.of(this).get(AddressViewModel::class.java)
        regionViewModel = activity?.let { ViewModelProviders.of(it).get(RegionViewModel::class.java) }!!


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
        addressViewModel.ecsAddresses.observe(this,addressObserver)


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

                    //  // update region properly ..as two way data binding for region is not possible : Billing

                    val ecsAddressBilling = binding.ecsAddressBilling
                    ecsAddressBilling?.phone2 = ecsAddressBilling?.phone1
                    addressViewModel.setRegion(binding.llBilling,binding.mecRegions,ecsAddressBilling!!)


                    addressViewModel.createAddress(ecsAddressShipping!!)
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

    private fun gotoDeliveryAddress(addressList: List<ECSAddress>? , shoppingCart : ECSShoppingCart) {
        var deliveryFragment = MECDeliveryFragment()
        var bundle = Bundle()
        bundle.putSerializable(MECConstant.KEY_ECS_ADDRESSES, addressList as Serializable)
        bundle.putSerializable(MECConstant.KEY_ECS_SHOPPING_CART,shoppingCart)
        deliveryFragment.arguments = bundle
        replaceFragment(deliveryFragment, MECDeliveryFragment().getFragmentTag(), true)
    }

    override fun processError(mecError: MecError?, showDialog: Boolean) {
        super.processError(mecError, showDialog)
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
    }

    override fun onStop() {
        super.onStop()
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
    }

}



