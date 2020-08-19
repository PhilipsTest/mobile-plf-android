package com.philips.platform.mec.screens.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.databinding.MecFilterFragmentBinding

class MECFilterCatalogFragment : BottomSheetDialogFragment(), ItemClickListener {
    companion object {
        val TAG: String = "MECFilterCatalogFragment"
    }

    override fun onItemClick(item: Any) {
        TODO("Not yet implemented")
    }

    private lateinit var binding: MecFilterFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = MecFilterFragmentBinding.inflate(inflater, container, false)


        return binding.root
    }
}
