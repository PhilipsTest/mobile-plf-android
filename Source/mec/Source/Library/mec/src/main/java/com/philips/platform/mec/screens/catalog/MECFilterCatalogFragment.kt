package com.philips.platform.mec.screens.catalog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
        binding.viewModel = ViewModelProvider(this).get(MECStockLevelStateViewModel::class.java)
        binding.viewModel?.validated?.observe(this, Observer {
            Toast.makeText(context, "Validated!$it", Toast.LENGTH_SHORT).show()
            productFilters = ProductFilter(null, it)
            val bundle = Bundle()
            bundle.putParcelable(MECConstant.SELECTED_FILTERS, productFilters)
            val intent = Intent().putExtras(bundle)
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)

            this.dismiss()
        })
        return binding.root
    }

    fun onClickOfClear() {
        binding.mecFilterCheckbox1.isChecked = false
        binding.mecFilterCheckbox2.isChecked = false
        binding.mecFilterCheckbox3.isChecked = false
        this.dismiss()
    }
}



