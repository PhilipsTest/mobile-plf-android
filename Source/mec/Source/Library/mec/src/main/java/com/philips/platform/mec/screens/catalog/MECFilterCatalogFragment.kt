package com.philips.platform.mec.screens.catalog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.philips.platform.ecs.microService.model.filter.ECSSortType
import com.philips.platform.ecs.microService.model.filter.ECSStockLevel
import com.philips.platform.ecs.microService.model.filter.ProductFilter
import com.philips.platform.mec.databinding.MecFilterFragmentBinding
import com.philips.platform.mec.utils.MECConstant


class MECFilterCatalogFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG: String = "MECFilterCatalogFragment"
    }

    private lateinit var binding: MecFilterFragmentBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = MecFilterFragmentBinding.inflate(inflater, container, false)
        val productFilter : ProductFilter? = arguments?.getParcelable(MECConstant.SELECTED_FILTERS)
        binding.productFilter = productFilter

        binding.mecApplyButton.setOnClickListener {

            val stockLevelSet: MutableSet<ECSStockLevel> = mutableSetOf()
            val sortType: ECSSortType? = null
            val mProductFilter = ProductFilter(sortType, stockLevelSet as HashSet<ECSStockLevel>)

            if (binding.mecFilterCheckbox1.isChecked) mProductFilter.stockLevelSet?.add(ECSStockLevel.InStock) else mProductFilter.stockLevelSet?.remove(ECSStockLevel.InStock)
            if (binding.mecFilterCheckbox2.isChecked) mProductFilter.stockLevelSet?.add(ECSStockLevel.LowStock) else mProductFilter.stockLevelSet?.remove(ECSStockLevel.LowStock)
            if (binding.mecFilterCheckbox3.isChecked) mProductFilter.stockLevelSet?.add(ECSStockLevel.OutOfStock) else mProductFilter.stockLevelSet?.remove(ECSStockLevel.OutOfStock)

            if (binding.mecFilterRadio1.isChecked) mProductFilter.sortType = ECSSortType.topRated
            if (binding.mecFilterRadio2.isChecked) mProductFilter.sortType = ECSSortType.priceAscending
            if (binding.mecFilterRadio3.isChecked) mProductFilter.sortType = ECSSortType.priceDescending
            if (binding.mecFilterRadio4.isChecked) mProductFilter.sortType = ECSSortType.discountPercentageAscending
            if (binding.mecFilterRadio5.isChecked) mProductFilter.sortType = ECSSortType.discountPercentageDescending

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
            binding.mecFilterRadio1.isChecked = false
            binding.mecFilterRadio2.isChecked = false
            binding.mecFilterRadio3.isChecked = false
            binding.mecFilterRadio4.isChecked = false
            binding.mecFilterRadio5.isChecked = false
        }
        return binding.root
    }
}



