/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.features

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.philips.cdp.prxclient.datamodels.features.FeaturesModel
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.databinding.MecProductFeaturesFragmentBinding
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.utils.MECConstant

class MECProductFeaturesFragment : MecBaseFragment() {
    override fun getFragmentTag(): String {
        return "MECProductFeaturesFragment"
    }

    var mRecyclerView : RecyclerView? =null
    var mFeaturesModel: FeaturesModel?=null
    private lateinit var binding: MecProductFeaturesFragmentBinding
    private lateinit var productFeaturesViewModel: ProductFeaturesViewModel

    private val featuresObserver : Observer<FeaturesModel> = object : Observer<FeaturesModel> {

        override fun onChanged(featuresModel: FeaturesModel?) {
            mFeaturesModel=featuresModel
            setImageForFeatureItem(featuresModel)
            binding.featureModel = featuresModel
        }

    }

    private fun setImageForFeatureItem(featuresModel: FeaturesModel?) {
        for (keyBenefitAreaItem in featuresModel!!.data.keyBenefitArea) {

            for (featureItem in keyBenefitAreaItem.feature) {

                var singleAssetImageFromFeatureCode = featuresModel.data.getSingleAssetImageFromFeatureCode(featureItem.featureCode)

                if(singleAssetImageFromFeatureCode!=null){
                    singleAssetImageFromFeatureCode = singleAssetImageFromFeatureCode + "?wid=" + 220 +
                              "&hei=" + 220 + "&\$pnglarge$" + "&fit=fit,1"
                }

                featureItem.setSingleFeatureImage(singleAssetImageFromFeatureCode)
            }
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
        binding = MecProductFeaturesFragmentBinding.inflate(inflater, container, false)

        productFeaturesViewModel = ViewModelProviders.of(this).get(ProductFeaturesViewModel::class.java)

        productFeaturesViewModel.mecError.observe(this,this)
        productFeaturesViewModel.features.observe(this,featuresObserver)

        val bundle = arguments
        val productCtn = bundle!!.getString(MECConstant.MEC_PRODUCT_CTN,"INVALID")

        if(null==mFeaturesModel) {
            context?.let { productFeaturesViewModel.fetchProductFeatures(it, productCtn) }
        }else{
            setImageForFeatureItem(mFeaturesModel)
            binding.featureModel = mFeaturesModel
        }
        mRecyclerView = binding.root as RecyclerView
        return binding.root
    }

    override fun processError(mecError: MecError?, showDialog: Boolean) {
        super.processError(mecError, false)
    }

}