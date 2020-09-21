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
import com.philips.cdp.prxclient.datamodels.cdls.ContactPhone
import com.philips.platform.ecs.model.orders.ECSOrderDetail
import com.philips.platform.mec.R
import com.philips.platform.mec.analytics.MECAnalyticPageNames.cancelOrder
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.analytics.MECAnalyticsConstant
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

        var orderDetail: ECSOrderDetail? = arguments?.getParcelable(MECConstant.MEC_ORDER_DETAIL)
        binding.orderNumber = orderDetail?.code

        var contactPhone: ContactPhone? = null
        if (arguments != null && arguments.containsKey(MECConstant.MEC_ORDER_CUSTOMER_CARE_PHONE)) {
            contactPhone = arguments.getSerializable(MECConstant.MEC_ORDER_CUSTOMER_CARE_PHONE) as ContactPhone
        }

        binding.contactPhone = contactPhone
        binding.mecCancelOrderCallBtn.setOnClickListener {
            val actionMap = HashMap<String, String>()
            actionMap.put(MECAnalyticsConstant.specialEvents, MECAnalyticsConstant.callCustomerCare)
            orderDetail?.code?.let { it -> actionMap.put(MECAnalyticsConstant.transationID, it) }
            orderDetail?.entries?.let { it1 -> MECAnalytics.tagActionsWithOrderProductsInfoForECSEntries(actionMap, it1) }

            callPhone(binding.contactPhone!!.phoneNumber)
        }

        val yourRefText: String = String.format(getString(R.string.mec_cancel_order_dls_for_your_ref_sg), "")
        val boldSpanned: Spanned
        boldSpanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml("$yourRefText  <b>$binding.orderNumber</b>", Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml("$yourRefText  <b>$binding.orderNumber</b>")
        }
        binding.mecCancelOrderRef.text = boldSpanned

        return binding.root
    }


    override fun onStart() {
        super.onStart()
        setTitleAndBackButtonVisibility(R.string.mec_cancel_your_order, true)
        setCartIconVisibility(false)
        MECAnalytics.trackPage(cancelOrder)
    }

    fun callPhone(phone: String) {
        try {
            val myintent = Intent(Intent.ACTION_DIAL)
            myintent.data = Uri.parse("tel:" + phone!!)
            //myintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(myintent)
        } catch (e: NullPointerException) {

        }
    }
}