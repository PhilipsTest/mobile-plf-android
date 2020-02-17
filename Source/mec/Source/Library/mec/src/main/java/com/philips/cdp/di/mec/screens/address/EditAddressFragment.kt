package com.philips.cdp.di.mec.screens.address


import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.philips.cdp.di.ecs.model.region.ECSRegion
import com.philips.cdp.di.mec.screens.MecBaseFragment
import com.philips.platform.uid.view.widget.InputValidationLayout
import com.philips.platform.uid.view.widget.ValidationEditText
import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.cdp.di.ecs.util.ECSConfiguration
import com.philips.cdp.di.mec.R
import com.philips.cdp.di.mec.databinding.MecAddressEditBinding
import com.philips.cdp.di.mec.utils.MECConstant
import kotlinx.android.synthetic.main.mec_main_activity.*


class EditAddressFragment : MecBaseFragment() {
    override fun getFragmentTag(): String {
        return "EditAddressFragment"
    }


    private lateinit var ecsAddress: ECSAddress
    private var addressFieldEnabler: MECAddressFieldEnabler? = null

    lateinit var binding: MecAddressEditBinding

    lateinit var addressViewModel: AddressViewModel

    var isError = false
    var validationEditText : ValidationEditText ? =null

    private val regionListObserver: Observer<List<ECSRegion>> = object : Observer<List<ECSRegion>> {

        override fun onChanged(regionList: List<ECSRegion>?) {

            val mecRegions = MECRegions(regionList!!)
            binding.mecRegions = mecRegions
            hideProgressBar()
        }

    }


    private val updateAndFetchAddressObserver: Observer<List<ECSAddress>> = Observer(fun(addressList: List<ECSAddress>?) {
        Log.d("Pabitra","Address updated")
        hideProgressBar()

    })

    override fun onResume() {
        super.onResume()
        setCartIconVisibility(false)
        setTitleAndBackButtonVisibility(R.string.mec_address, true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = MecAddressEditBinding.inflate(inflater, container, false)
        binding.pattern = MECRegexPattern()


        addressViewModel = ViewModelProviders.of(this).get(AddressViewModel::class.java)

        ecsAddress = arguments?.getSerializable(MECConstant.KEY_ECS_ADDRESS) as ECSAddress
        binding.ecsAddress = ecsAddress

        addressViewModel.regionsList.observe(this, regionListObserver)
        addressViewModel.mecError.observe(this, this)
        addressViewModel.ecsAddresses.observe(this,updateAndFetchAddressObserver)

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
                    // update region properly ..as two way data binding for region is not possible
                    val ecsAddress = binding.ecsAddress
                    addressViewModel.setRegion(binding.llShipping,binding.mecRegions,ecsAddress!!)

                    addressViewModel.updateAndFetchAddress(ecsAddress!!)
                    createCustomProgressBar(container,MEDIUM)
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
                    validator = PhoneNumberInputValidator(MECRegexPattern().PHONE_NUMBER_PATTERN)
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

        if(addressFieldEnabler?.isStateEnabled!!) {
            addressViewModel.fetchRegions()
            createCustomProgressBar(container,MEDIUM)
        }
    }


}



