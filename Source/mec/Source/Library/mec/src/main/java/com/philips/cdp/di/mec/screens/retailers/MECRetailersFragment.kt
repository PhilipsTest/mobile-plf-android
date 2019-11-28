package com.philips.cdp.di.mec.screens.retailers

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.philips.cdp.di.ecs.model.retailers.ECSRetailerList
import com.philips.cdp.di.mec.common.ItemClickListener
import com.philips.cdp.di.mec.databinding.MecRetailersFragmentBinding
import com.philips.cdp.di.mec.screens.MecBaseFragment
import com.philips.cdp.di.mec.utils.MECConstant
import com.philips.cdp.di.mec.utils.MECDataHolder
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface
import java.util.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.philips.cdp.di.ecs.model.retailers.ECSRetailer
import kotlin.collections.ArrayList


class MECRetailersFragment : BottomSheetDialogFragment(),ItemClickListener {

    private lateinit var binding: MecRetailersFragmentBinding
    private lateinit var retailers: ECSRetailerList
    private lateinit var list: ArrayList<ECSRetailer>
    lateinit var appInfra: AppInfra


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = MecRetailersFragmentBinding.inflate(inflater, container, false)


        val bottomSheetBehavior = BottomSheetBehavior.from(binding.designBottomSheet)
        val metrics = resources.displayMetrics
        bottomSheetBehavior.peekHeight = metrics.heightPixels / 2
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        val bundle = arguments
        retailers = bundle?.getSerializable(MECConstant.MEC_KEY_PRODUCT) as ECSRetailerList


        val itemClickListener = bundle.getSerializable(MECConstant.MEC_CLICK_LISTENER) as ItemClickListener

        binding.retailerList = retailers
        binding.itemClickListener = itemClickListener

        return binding.root
    }

    override fun onItemClick(item: Any) {

    }


}
