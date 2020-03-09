package com.philips.cdp.di.mec.screens.address


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
import com.philips.cdp.di.ecs.model.region.ECSRegion
import com.philips.cdp.di.mec.screens.MecBaseFragment
import com.philips.platform.uid.view.widget.InputValidationLayout
import com.philips.platform.uid.view.widget.ValidationEditText
import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.cdp.di.ecs.util.ECSConfiguration
import com.philips.cdp.di.mec.R
import com.philips.cdp.di.mec.common.MecError
import com.philips.cdp.di.mec.databinding.MecAddressEditBinding
import com.philips.cdp.di.mec.screens.address.region.RegionViewModel
import com.philips.cdp.di.mec.utils.MECConstant
import kotlinx.android.synthetic.main.mec_main_activity.*
import java.io.Serializable


class CreateOrEditAddressFragment : MecBaseFragment() {
    override fun getFragmentTag(): String {
        return "EditAddressFragment"
    }


    private lateinit var regionViewModel: RegionViewModel

    private lateinit var ecsAddress: ECSAddress
    private var addressFieldEnabler: MECAddressFieldEnabler? = null

    lateinit var binding: MecAddressEditBinding

    lateinit var addressViewModel: AddressViewModel

    var isError = false
    var validationEditText : ValidationEditText ? =null

    var mecRegions :MECRegions ? = null

    private val regionListObserver: Observer<List<ECSRegion>> = object : Observer<List<ECSRegion>> {

        override fun onChanged(regionList: List<ECSRegion>?) {

            mecRegions = MECRegions(regionList!!)
            binding.mecRegions = mecRegions
            dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        }

    }


    private val updateAndFetchAddressObserver: Observer<List<ECSAddress>> = Observer(fun(addressList: List<ECSAddress>?) {

        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)

        val intent = Intent()
        val bundle = Bundle()
        bundle.putSerializable(MECConstant.KEY_ECS_ADDRESSES,addressList as Serializable)
        intent.putExtra(MECConstant.BUNDLE_ADDRESSES,bundle)
        targetFragment?.onActivityResult(MECConstant.REQUEST_CODE_ADDRESSES, Activity.RESULT_OK,intent)
        activity?.supportFragmentManager?.popBackStack()

    })

    override fun onResume() {
        super.onResume()
        setCartIconVisibility(false)
        setTitleAndBackButtonVisibility(R.string.mec_address, true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = MecAddressEditBinding.inflate(inflater, container, false)
        binding.pattern = MECSalutationHolder()


        addressViewModel = ViewModelProviders.of(this).get(AddressViewModel::class.java)

        ecsAddress = arguments?.getSerializable(MECConstant.KEY_ECS_ADDRESS) as ECSAddress
        binding.ecsAddress = ecsAddress


        addressViewModel.ecsAddresses.observe(this,updateAndFetchAddressObserver)
        addressViewModel.mecError.observe(this, this)


        regionViewModel = activity?.let { ViewModelProviders.of(it).get(RegionViewModel::class.java) }!!
        regionViewModel.regionsList.observe(activity!!, regionListObserver)
        regionViewModel.mecError.observe(this,this)

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
                    ecsAddress.phone2 = ecsAddress.phone1

                    if(ecsAddress.id !=null) { // This means address already existed , so need to create it again
                        addressViewModel.updateAndFetchAddress(ecsAddress!!)
                    }else{
                        addressViewModel.createAndFetchAddress(ecsAddress)
                    }

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
        super.processError(mecError, showDialog)
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
    }

    override fun onStop() {
        super.onStop()
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
    }


}



