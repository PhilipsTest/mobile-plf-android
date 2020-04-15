/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.retailers

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.cdp.di.ecs.model.retailers.ECSRetailer
import com.philips.cdp.di.ecs.model.retailers.ECSRetailerList
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.analytics.MECAnalyticsConstant
import com.philips.platform.mec.analytics.MECAnalyticsConstant.sendData
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.databinding.MecRetailersFragmentBinding
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder
import java.util.*


class MECRetailersFragment : BottomSheetDialogFragment(), ItemClickListener{

    companion object {
        val TAG:String="MECRetailersFragment"
    }

    override fun onItemClick(item: Any) {

        val ecsRetailer = item as ECSRetailer

        val bundle = Bundle()
        bundle.putSerializable(MECConstant.SELECTED_RETAILER, ecsRetailer)

        val intent = Intent().putExtras(bundle)

        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)

        this.dismiss()
    }

    private lateinit var binding: MecRetailersFragmentBinding
    private lateinit var retailers: ECSRetailerList
    private lateinit var product: ECSProduct
    lateinit var appInfra: AppInfra


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = MecRetailersFragmentBinding.inflate(inflater, container, false)


        val bottomSheetBehavior = BottomSheetBehavior.from(binding.designBottomSheet)
        val metrics = resources.displayMetrics
        bottomSheetBehavior.peekHeight = metrics.heightPixels / 2

        val bundle = arguments
        retailers = bundle?.getSerializable(MECConstant.MEC_KEY_PRODUCT) as ECSRetailerList
        product = bundle?.getSerializable(MECConstant.MEC_PRODUCT) as ECSProduct

        binding.retailerList = retailers
        binding.itemClickListener = this
        tagRetailerAndBlackListedRetailerList(retailers,product)
        return binding.root
    }

    private fun tagRetailerAndBlackListedRetailerList(retailers: ECSRetailerList, product :ECSProduct  ){
        // add retailer list if present
        val map = HashMap<String, String>()
        if(retailers!=null && retailers.retailers!=null && retailers.retailers.size>0) {
            val mutableRetailersIterator = retailers.retailers.iterator()
            var retailerListString: String = ""
            for (ecsRetailer in mutableRetailersIterator) {
                retailerListString += "|" + ecsRetailer.name
            }
            retailerListString = retailerListString.substring(1, retailerListString.length )

            map.put(MECAnalyticsConstant.retailerList, retailerListString)
            val productInfo: String = MECAnalytics.getProductInfo(product)
            map.put(MECAnalyticsConstant.mecProducts, productInfo)

        }
        // add Blacklisted retailer list if present otherwise send empty string
        var blackListedRetailerListString: String? = null
        if(MECDataHolder.INSTANCE.blackListedRetailers!=null && MECDataHolder.INSTANCE.blackListedRetailers!!.size>0){
            for(blackListedRetailer: String in MECDataHolder.INSTANCE.blackListedRetailers!!){
                blackListedRetailerListString+= "|" +blackListedRetailer
            }
            if (blackListedRetailerListString != null) {
                blackListedRetailerListString = blackListedRetailerListString.substring(1, blackListedRetailerListString.length )
            }
            blackListedRetailerListString?.let { map.put(MECAnalyticsConstant.blackListedRetailerList, it) }
        }


        MECAnalytics.trackMultipleActions(sendData, map)

    }

}
