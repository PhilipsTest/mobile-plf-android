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
import com.philips.cdp.di.ecs.model.region.ECSRegion
import com.philips.cdp.di.ecs.util.ECSConfiguration
import com.philips.platform.mec.R
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.databinding.MecAddressEditBinding
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.address.region.RegionViewModel
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.uid.view.widget.InputValidationLayout
import com.philips.platform.uid.view.widget.ValidationEditText


class AddOrEditBillingAddressFragment : MecBaseFragment() {
    override fun getFragmentTag(): String {
        return "EditAddressFragment"
    }


    private lateinit var regionViewModel: RegionViewModel
    private lateinit var ecsAddress: ECSAddress
    private var addressFieldEnabler: MECAddressFieldEnabler? = null

    lateinit var binding: MecAddressEditBinding

    lateinit var addressViewModel: AddressViewModel

    var isError = false
    var validationEditText: ValidationEditText? = null

    var mecRegions: MECRegions? = null

    private val regionListObserver: Observer<List<ECSRegion>> = object : Observer<List<ECSRegion>> {

        override fun onChanged(regionList: List<ECSRegion>?) {

            mecRegions = MECRegions(regionList!!)
            binding.mecRegions = mecRegions
            dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        }

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


        regionViewModel = activity?.let { ViewModelProviders.of(it).get(RegionViewModel::class.java) }!!
        regionViewModel.regionsList.observe(activity!!, regionListObserver)
        regionViewModel.mecError.observe(this, this)





        ecsAddress = arguments?.getSerializable(MECConstant.KEY_ECS_ADDRESS) as ECSAddress
        binding.ecsAddress = ecsAddress


        if (!ecsAddress.isShippingAddress) {
            binding.tvShippingAddress.setText(R.string.mec_billing_address)
            binding.dlsIapAddressShipping.labelFirstName.setText(R.string.mec_card_holder_first_name)
            binding.dlsIapAddressShipping.lableLastName.setText(R.string.mec_card_holder_last_name)
        }



        addressFieldEnabler = context?.let { addressViewModel.getAddressFieldEnabler(ECSConfiguration.INSTANCE.country, it) }

        binding.addressFieldEnabler = addressFieldEnabler

        //On click of continue button
        binding.btnContinue.setOnClickListener {
            //Re assign
            isError = false
            validationEditText = null

            validateEditTexts(binding.addressContainer)

            if (isError) {
                validationEditText?.requestFocus()

            } else {
                // update region properly ..as two way data binding for region is not possible
                val ecsAddress = binding.ecsAddress
                addressViewModel.setRegion(binding.llShipping, binding.mecRegions, ecsAddress!!)
                ecsAddress.phone2 = ecsAddress.phone1

                // billing Address  is created
                sendBillingAddressToDeliveryFragment(ecsAddress)

            }
        }

        return binding.root
    }

    private fun sendBillingAddressToDeliveryFragment(ecsAddress: ECSAddress?) {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putSerializable(MECConstant.KEY_ECS_BILLING_ADDRESS, ecsAddress)
        intent.putExtra(MECConstant.BUNDLE_BILLING_ADDRESS, bundle)
        targetFragment?.onActivityResult(MECConstant.REQUEST_CODE_BILLING_ADDRESS, Activity.RESULT_OK, intent)
        activity?.supportFragmentManager?.popBackStack()
    }

    private fun validateEditTexts(v: ViewGroup) {

        for (i in 0 until v.childCount) {
            val child = v.getChildAt(i)

            if (v is InputValidationLayout && child is ValidationEditText && child.visibility == View.VISIBLE) {

                Log.d("MEC", child.hint.toString())

                var validator: InputValidationLayout.Validator

                if (child.inputType == InputType.TYPE_CLASS_PHONE) {
                    validator = PhoneNumberInputValidator(child, PhoneNumberUtil.getInstance())
                } else {
                    validator = EmptyInputValidator()
                }

                if (!validator.validate(child.text.toString())) {
                    child.startAnimation(addressViewModel.shakeError())
                    if (validationEditText == null) {
                        validationEditText = child
                    }
                    v.showError()
                    isError = true
                }

            } else if (child is ViewGroup) {

                if (child.visibility == View.VISIBLE) {
                    validateEditTexts(child)  // Recursive call.
                }

            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (addressFieldEnabler?.isStateEnabled!! && mecRegions == null) {

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



