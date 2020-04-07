/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.detail


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.platform.mec.R
import com.philips.platform.mec.screens.features.MECProductFeaturesFragment
import com.philips.platform.mec.screens.specification.SpecificationFragment
import com.philips.platform.mec.utils.MECConstant


class TabPagerAdapter(var fm: FragmentManager, var product: ECSProduct, var context: Context) : FragmentPagerAdapter(fm) {



    override fun getItem(position: Int): Fragment {

        val bundle = Bundle()
        bundle.putString(MECConstant.MEC_PRODUCT_CTN,product.code)



        return when (position) {
            0 -> {
                MECProductInfoFragment(product)
            }
            1 -> {
                val productFeaturesFragment = MECProductFeaturesFragment()
                productFeaturesFragment.arguments = bundle
                return productFeaturesFragment
            }
            2 -> {


                val specificationFragment = SpecificationFragment()
                specificationFragment.arguments = bundle
                return specificationFragment
            }

            3 -> {

                val fragment = MECProductReviewsFragment()
                fragment.arguments = bundle
                return fragment
            }
            else ->{
                MECProductInfoFragment(product)
            }
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> context.getString(R.string.mec_info)
            1 -> context.getString(R.string.mec_features)
            2 -> context.getString(R.string.mec_specs)
            3 -> context.getString(R.string.mec_reviews)
            else -> {
                context.getString(R.string.mec_info)
            }
        }
    }
}
