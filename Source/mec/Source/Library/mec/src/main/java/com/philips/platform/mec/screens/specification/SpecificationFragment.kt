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
import com.philips.platform.ecs.microService.model.product.ECSProduct

import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.analytics.MECAnalyticsConstant.mecProducts
import com.philips.platform.mec.analytics.MECAnalyticsConstant.productTabsClick
import com.philips.platform.mec.analytics.MECAnalyticsConstant.sendData
import com.philips.platform.mec.analytics.MECAnalyticsConstant.specs
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.databinding.MecProductSpecsFragmentBinding
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.utils.MECConstant


class SpecificationFragment : MecBaseFragment() {
    var mSpecification: SpecificationModel? = null
    var mRecyclerView : RecyclerView? =null
    private lateinit var mECSProduct : ECSProduct

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
        mECSProduct =bundle!!.getParcelable<ECSProduct>(MECConstant.MEC_PRODUCT)!!
        mRecyclerView = binding.root as RecyclerView
        if (mSpecification == null) {
            context?.let { prxSpecificationViewModel.fetchSpecification(it, productCtn) }
        }else{
          binding.specificationModel = mSpecification
        }


        return binding.root

    }

    // when user switch to this tab, this method will be called
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            var actionMap = HashMap<String, String>()
            actionMap.put(productTabsClick, specs)
            actionMap.put(mecProducts, MECAnalytics.getProductInfo(mECSProduct))
            MECAnalytics.trackMultipleActions(sendData, actionMap)
        }
    }

    override fun processError(mecError: MecError?, showDialog: Boolean) {
        super.processError(mecError, false)
    }


}