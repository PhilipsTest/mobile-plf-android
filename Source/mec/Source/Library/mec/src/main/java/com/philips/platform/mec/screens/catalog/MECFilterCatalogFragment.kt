package com.philips.platform.mec.screens.catalog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.philips.platform.ecs.microService.model.filter.ECSStockLevel
import com.philips.platform.ecs.microService.model.filter.ProductFilter
import com.philips.platform.mec.databinding.MecFilterFragmentBinding
import com.philips.platform.mec.utils.MECConstant

class MECFilterCatalogFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG: String = "MECFilterCatalogFragment"
    }

    private var mProductFilter: ProductFilter? = null
    private lateinit var binding: MecFilterFragmentBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = MecFilterFragmentBinding.inflate(inflater, container, false)
        mProductFilter = arguments?.getParcelable(MECConstant.SELECTED_FILTERS)
        binding.productFilter = mProductFilter

        binding.mecApplyButton.setOnClickListener {

            if (binding.mecFilterCheckbox1.isChecked) mProductFilter?.stockLevelList?.add(ECSStockLevel.InStock) else mProductFilter?.stockLevelList?.remove(ECSStockLevel.InStock)
            if (binding.mecFilterCheckbox2.isChecked) mProductFilter?.stockLevelList?.add(ECSStockLevel.LowStock) else mProductFilter?.stockLevelList?.remove(ECSStockLevel.LowStock)
            if (binding.mecFilterCheckbox3.isChecked) mProductFilter?.stockLevelList?.add(ECSStockLevel.OutOfStock) else mProductFilter?.stockLevelList?.remove(ECSStockLevel.OutOfStock)

            val bundle = Bundle()
            bundle.putParcelable(MECConstant.SELECTED_FILTERS, mProductFilter)
            val intent = Intent().putExtras(bundle)
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
            this.dismiss()
        }

        binding.mecButtonSecondary.setOnClickListener {
            binding.mecFilterCheckbox1.isChecked = false
            binding.mecFilterCheckbox2.isChecked = false
            binding.mecFilterCheckbox3.isChecked = false
//            mProductFilter?.stockLevelList = mutableListOf()
            //            this.dismiss()
        }
        return binding.root
    }
}



