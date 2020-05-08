package com.philips.platform.mec.screens.history.orderDetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.philips.cdp.prxclient.datamodels.contacts.ContactPhone
import com.philips.platform.mec.R
import com.philips.platform.mec.databinding.MecCancelOrderFragmentBinding
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.utils.MECConstant

class MECCancelOrderFragment : MecBaseFragment() {
    override fun getFragmentTag(): String {
        return "MECCancelOrderFragment"
    }


    private lateinit var binding: MecCancelOrderFragmentBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setCartIconVisibility(false)
        binding = MecCancelOrderFragmentBinding.inflate(inflater, container, false)


        val arguments = arguments
        binding.orderNumber= arguments?.getString(MECConstant.MEC_ORDER_NUMBER)

        var contactPhone: ContactPhone? = null
        if (arguments != null && arguments.containsKey(MECConstant.MEC_ORDER_CUSTOMER_CARE_PHONE)) {
            contactPhone = ContactPhone()
            contactPhone.phoneNumber = arguments?.getString(MECConstant.MEC_ORDER_CUSTOMER_CARE_PHONE)
        }
        if (arguments != null && arguments.containsKey(MECConstant.MEC_ORDER_CUSTOMER_CARE_WEEK_WORKING_HOUR)) {
            contactPhone!!.openingHoursWeekdays = arguments?.getString(MECConstant.MEC_ORDER_CUSTOMER_CARE_WEEK_WORKING_HOUR)
        }
        if (arguments != null && arguments.containsKey(MECConstant.MEC_ORDER_CUSTOMER_CARE_HOLIDAY_WORKING_HOUR)) {
            contactPhone!!.openingHoursSaturday = arguments?.getString(MECConstant.MEC_ORDER_CUSTOMER_CARE_HOLIDAY_WORKING_HOUR)
        }
        binding.contactPhone = contactPhone
        binding.mecCancelOrderCallBtn.setOnClickListener { callPhone(binding.contactPhone!!.phoneNumber) }


        return binding.root
    }


    override fun onStart() {
        super.onStart()
        setTitleAndBackButtonVisibility(R.string.mec_cancel_your_order, true)
        setCartIconVisibility(false)

    }

    fun callPhone(phone: String) {
        try {
            val myintent = Intent(Intent.ACTION_DIAL)
            myintent.data = Uri.parse("tel:" + phone!!)
            myintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(myintent)
            /* val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone))
             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
             context?.let { startActivity(it, intent, null) }*/
        } catch (e: NullPointerException) {

        }
    }
}