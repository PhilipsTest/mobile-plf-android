package com.philips.platform.mec.screens.history.orderDetail

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
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

        var orderNumber: String? = arguments?.getString(MECConstant.MEC_ORDER_NUMBER)
        binding.orderNumber =  orderNumber

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
        
        val yourRefText : String= String.format(getString(R.string.mec_cancel_order_dls_for_your_ref_sg),"")
        val boldSpanned: Spanned
        boldSpanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml("$yourRefText  <b>$orderNumber</b>", Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml("$yourRefText  <b>$orderNumber</b>")
        }
        binding.mecCancelOrderRef.text=boldSpanned

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
        } catch (e: NullPointerException) {

        }
    }
}