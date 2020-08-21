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

    private var productFilters: ProductFilter? = null
    private lateinit var binding: MecFilterFragmentBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = MecFilterFragmentBinding.inflate(inflater, container, false)
        binding.fragment = this

        return binding.root
    }

    private fun updateSelectedFilters() {
        val stockList: ArrayList<ECSStockLevel> = ArrayList()
        when {
            binding.mecFilterCheckbox1.isChecked -> stockList.add(ECSStockLevel.InStock)
            binding.mecFilterCheckbox2.isChecked -> stockList.add(ECSStockLevel.LowStock)
            binding.mecFilterCheckbox3.isChecked -> stockList.add(ECSStockLevel.OutOfStock)
            binding.mecFilterCheckbox1.isChecked && binding.mecFilterCheckbox2.isChecked -> {
                stockList.add(ECSStockLevel.InStock)
                stockList.add(ECSStockLevel.LowStock)
            }
            binding.mecFilterCheckbox1.isChecked && binding.mecFilterCheckbox3.isChecked -> {
                stockList.add(ECSStockLevel.InStock)
                stockList.add(ECSStockLevel.OutOfStock)
            }
            binding.mecFilterCheckbox2.isChecked && binding.mecFilterCheckbox3.isChecked -> {
                stockList.add(ECSStockLevel.LowStock)
                stockList.add(ECSStockLevel.OutOfStock)
            }
            binding.mecFilterCheckbox1.isChecked && binding.mecFilterCheckbox2.isChecked && binding.mecFilterCheckbox3.isChecked -> {
                stockList.add(ECSStockLevel.InStock)
                stockList.add(ECSStockLevel.LowStock)
                stockList.add(ECSStockLevel.OutOfStock)
            }
        }

        productFilters = ProductFilter(null,stockList)
    }

    fun onClickOfClear() {
        binding.mecFilterCheckbox1.isChecked = false
        binding.mecFilterCheckbox2.isChecked = false
        binding.mecFilterCheckbox3.isChecked = false
    }

    fun onClickOfApply() {
        updateSelectedFilters()

        val bundle = Bundle()
        bundle.putSerializable(MECConstant.SELECTED_FILTERS, productFilters)
        val intent = Intent().putExtras(bundle)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)

        this.dismiss()
    }
}


