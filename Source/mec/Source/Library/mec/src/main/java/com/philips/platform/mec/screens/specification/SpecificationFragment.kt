/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.specification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.philips.cdp.prxclient.datamodels.specification.SpecificationModel
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.databinding.MecProductSpecsFragmentBinding
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.utils.MECConstant


class SpecificationFragment : MecBaseFragment() {
    var mSpecification: SpecificationModel? = null
    var mRecyclerView : RecyclerView? =null

    override fun getFragmentTag(): String {
        return "SpecificationFragment"
    }

    private lateinit var binding: MecProductSpecsFragmentBinding
    private lateinit var prxSpecificationViewModel: SpecificationViewModel

    private val specificationObserver: Observer<SpecificationModel> = object : Observer<SpecificationModel> {

        override fun onChanged(specification: SpecificationModel?) {
            binding.specificationModel = specification
            mSpecification = specification
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        /*
        * When comes back to this screen upon back press of WebRetailers and Shopping cart
        * Here existing recyclerView(if already created) needs to be removed from its parent(View pager)
        * */
        if (mRecyclerView != null) {
            val parent = mRecyclerView!!.getParent() as ViewGroup
            parent?.removeView(mRecyclerView)
        }

        binding = MecProductSpecsFragmentBinding.inflate(inflater, container, false)

        prxSpecificationViewModel = ViewModelProviders.of(this).get(SpecificationViewModel::class.java)

        prxSpecificationViewModel.mecError.observe(this, this)
        prxSpecificationViewModel.specification.observe(this, specificationObserver)

        val bundle = arguments
        val productCtn = bundle!!.getString(MECConstant.MEC_PRODUCT_CTN, "INVALID")
        mRecyclerView = binding.root as RecyclerView
        if (mSpecification == null) {
            context?.let { prxSpecificationViewModel.fetchSpecification(it, productCtn) }
        }else{
          binding.specificationModel = mSpecification
        }


        return binding.root

    }

    override fun processError(mecError: MecError?, showDialog: Boolean) {
        super.processError(mecError, false)
    }


}