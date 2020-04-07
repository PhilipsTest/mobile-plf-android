/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.detail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.platform.mec.databinding.MecProductInfoFragmentBinding
import com.philips.platform.mec.screens.MecBaseFragment

/**
 * A simple [Fragment] subclass.
 */
class MECProductInfoFragment(val product: ECSProduct) : MecBaseFragment() {



    override fun getFragmentTag(): String {
       return "MECProductInfoFragment"
    }

    private lateinit var binding:MecProductInfoFragmentBinding
    lateinit var ecsProductDetailViewModel: EcsProductDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = MecProductInfoFragmentBinding.inflate(inflater, container, false)
        binding.product = product
        return binding.root
    }

}
