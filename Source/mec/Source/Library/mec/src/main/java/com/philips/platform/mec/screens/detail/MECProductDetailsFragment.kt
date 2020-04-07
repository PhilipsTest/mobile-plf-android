/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bazaarvoice.bvandroidsdk.BulkRatingsResponse
import com.bazaarvoice.bvandroidsdk.Statistics
import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.cdp.di.ecs.model.retailers.ECSRetailer
import com.philips.cdp.di.ecs.model.retailers.ECSRetailerList
import com.philips.platform.mec.R
import com.philips.platform.mec.analytics.MECAnalyticPageNames.productDetails
import com.philips.platform.mec.analytics.MECAnalyticsConstant.mecProducts
import com.philips.platform.mec.analytics.MECAnalyticsConstant.outOfStock
import com.philips.platform.mec.analytics.MECAnalyticsConstant.prodView
import com.philips.platform.mec.analytics.MECAnalyticsConstant.retailerName
import com.philips.platform.mec.analytics.MECAnalyticsConstant.sendData
import com.philips.platform.mec.analytics.MECAnalyticsConstant.specialEvents
import com.philips.platform.mec.analytics.MECAnalyticsConstant.stockStatus
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.databinding.MecProductDetailsBinding
import com.philips.platform.mec.integration.serviceDiscovery.MECManager
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.retailers.ECSRetailerViewModel
import com.philips.platform.mec.screens.retailers.MECRetailersFragment
import com.philips.platform.mec.screens.retailers.WebBuyFromRetailersFragment
import com.philips.platform.mec.screens.shoppingCart.MECShoppingCartFragment
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECConstant.MEC_PRODUCT
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECutility
import kotlinx.android.synthetic.main.mec_product_details.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DecimalFormat

/**
 * A simple [Fragment] subclass.
 */
open class MECProductDetailsFragment : MecBaseFragment() {

    override fun getFragmentTag(): String {
        return "MECProductDetailsFragment"
    }


    var mRootView :View? =null

    lateinit var param: String

    lateinit var binding: MecProductDetailsBinding
    lateinit var product: ECSProduct
    private lateinit var retailersList: ECSRetailerList
    private lateinit var ecsRetailerViewModel: ECSRetailerViewModel

    lateinit var ecsProductDetailViewModel: EcsProductDetailViewModel

    private val eCSRetailerListObserver: Observer<ECSRetailerList> = object : Observer<ECSRetailerList> {
        override fun onChanged(retailers: ECSRetailerList?) {
            retailersList = retailers!!
            ecsProductDetailViewModel.removeBlacklistedRetailers(retailersList)
            if (retailers.wrbresults.onlineStoresForProduct != null) {
                if (retailersList.wrbresults.onlineStoresForProduct.stores.retailerList.size > 0) {
                    if (binding.mecAddToCartButton.visibility == View.GONE) {
                        binding.mecFindRetailerButtonPrimary.visibility = View.VISIBLE
                        binding.mecFindRetailerButtonSecondary.visibility = View.GONE
                        binding.mecFindRetailerButtonPrimary.isEnabled = true
                    } else if (binding.mecAddToCartButton.visibility == View.VISIBLE) {
                        binding.mecFindRetailerButtonSecondary.visibility = View.VISIBLE
                        binding.mecFindRetailerButtonPrimary.visibility = View.GONE
                        binding.mecFindRetailerButtonSecondary.isEnabled = true
                    }
                }
            } else {
                binding.mecFindRetailerButtonPrimary.isEnabled = false
                binding.mecFindRetailerButtonSecondary.isEnabled = false
            }
            ecsProductDetailViewModel.setStockInfoWithRetailer(binding.mecProductDetailStockStatus, product, retailersList)
        }

    }

    fun getStock(stock: String) {
        if (stock.equals(R.string.mec_out_of_stock)) {

        }
    }

    private val ratingObserver: Observer<BulkRatingsResponse> = Observer<BulkRatingsResponse> { response -> updateData(response?.results) }

    private val productObserver: Observer<ECSProduct> = Observer<ECSProduct> { ecsProduct ->

        //TO show No Image for no asset found for a product
        if(ecsProduct.assets== null || ecsProduct.assets.validPRXImageAssets == null || ecsProduct.assets.validPRXImageAssets.isEmpty()){
            ecsProductDetailViewModel.addNoAsset(product)
        }

        binding.product = ecsProduct
        setUpTab(ecsProduct)
        showPriceDetail()
        addToCartVisibility(ecsProduct!!)
        if (!MECDataHolder.INSTANCE.hybrisEnabled && !MECDataHolder.INSTANCE.retailerEnabled) {
            binding.mecProductDetailStockStatus.text = binding.mecProductDetailStockStatus.context.getString(R.string.mec_out_of_stock)
            binding.mecProductDetailStockStatus.setTextColor(binding.mecProductDetailStockStatus.context.getColor(R.color.uid_signal_red_level_30))
        }
        if (MECDataHolder.INSTANCE.retailerEnabled) {
            getRetailerDetails()
        } else {
            binding.mecFindRetailerButtonPrimary.visibility = View.GONE
            binding.mecFindRetailerButtonSecondary.visibility = View.GONE
            if (MECDataHolder.INSTANCE.hybrisEnabled) {
                if (null != product && null != product!!.stock) {
                    if (MECutility.isStockAvailable(product!!.stock!!.stockLevelStatus, product!!.stock!!.stockLevel)) {
                        binding.mecProductDetailStockStatus.text = binding.mecProductDetailStockStatus.context.getString(R.string.mec_in_stock)
                        binding.mecProductDetailStockStatus.setTextColor(binding.mecProductDetailStockStatus.context.getColor(R.color.uid_signal_green_level_30))
                    } else {
                        binding.mecProductDetailStockStatus.text = binding.mecProductDetailStockStatus.context.getString(R.string.mec_out_of_stock)
                        binding.mecProductDetailStockStatus.setTextColor(binding.mecProductDetailStockStatus.context.getColor(R.color.uid_signal_red_level_30))
                        tagOutOfStockActions(product!!)
                    }
                }
            }
        }
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)

    }





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if(null==mRootView) {
            binding = MecProductDetailsBinding.inflate(inflater, container, false)

            binding.fragment = this
            binding.mecDataHolder = MECDataHolder.INSTANCE

            ecsProductDetailViewModel =  ViewModelProviders.of(this).get(EcsProductDetailViewModel::class.java)

            ecsRetailerViewModel =  ViewModelProviders.of(this).get(ECSRetailerViewModel::class.java)

            ecsRetailerViewModel.ecsRetailerList.observe(this, eCSRetailerListObserver)


            ecsProductDetailViewModel.ecsProduct.observe(this, productObserver)


            ecsProductDetailViewModel.bulkRatingResponse.observe(this, ratingObserver)
            ecsProductDetailViewModel.mecError.observe(this, this)



            binding.indicator.viewPager = binding.pager
            val bundle = arguments
            product = bundle?.getSerializable(MECConstant.MEC_KEY_PRODUCT) as ECSProduct
            

            //if assets are not available , we should show one Default image
           // ecsProductDetailViewModel.addNoAsset(product)

           // ecsProductDetailViewModel.ecsProduct.value = product


            com.philips.platform.mec.analytics.MECAnalytics.trackPage(productDetails)
            tagActions(product)
            mRootView=binding.root
            showData()
            ////////////// start of update cart and login if required
            if(isUserLoggedIn()) {
                GlobalScope.launch {
                    var mecManager: MECManager = MECManager()
                    MECDataHolder.INSTANCE.mecCartUpdateListener?.let { mecManager.getShoppingCartData(it) }
                }
            }
            ////////////// end of update cart and login if required

        }
        return mRootView
    }

    private fun setUpTab(product: ECSProduct) {
        val fragmentAdapter = TabPagerAdapter(this.childFragmentManager, product, binding.mecReviewLebel.context)
        binding.viewpagerMain.offscreenPageLimit = 4
        binding.viewpagerMain.adapter = fragmentAdapter
        binding.tabsMain.setupWithViewPager(binding.viewpagerMain)
    }


    private fun showData() {
        if (MECDataHolder.INSTANCE.hybrisEnabled) {
            binding.mecFindRetailerButtonPrimary.visibility = View.GONE
            binding.mecFindRetailerButtonSecondary.visibility = View.VISIBLE
        } else if (!MECDataHolder.INSTANCE.hybrisEnabled) {
            binding.mecFindRetailerButtonPrimary.visibility = View.VISIBLE
            binding.mecFindRetailerButtonSecondary.visibility = View.GONE
        }
        executeRequest()
        getRatings()
    }


    override fun onResume() {
        super.onResume()
        setTitleAndBackButtonVisibility(R.string.mec_product_detail_title, true)
        setCartIconVisibility(true)
    }


    fun addToCartVisibility(product: ECSProduct) {
        if (MECDataHolder.INSTANCE.hybrisEnabled.equals(false)) {
            binding.mecAddToCartButton.visibility = View.GONE
        } else if ((MECDataHolder.INSTANCE.hybrisEnabled.equals(true)) && product!!.stock!=null && !(MECutility.isStockAvailable(product!!.stock?.stockLevelStatus!!, product!!.stock?.stockLevel!!))) {
            binding.mecAddToCartButton.visibility = View.VISIBLE
            binding.mecAddToCartButton.isEnabled = false
        } else {
            binding.mecAddToCartButton.visibility = View.VISIBLE
            binding.mecAddToCartButton.isEnabled = true
        }
    }

    open fun executeRequest() {
        showProgressBar(binding.mecProgress.mecProgressBarContainer)
        ecsProductDetailViewModel.getProductDetail(product)
    }



    private fun getRetailerDetails() {
        if(null!=product) {
            ecsRetailerViewModel.getRetailers(product.code)
        }
    }

    private fun getRatings() {
        ecsProductDetailViewModel.getRatings(product.codeForBazaarVoice)
    }


    //TODO bind it
    fun updateData(results: List<Statistics>?) {
        if (results != null && results.isNotEmpty()) {
            binding.mecDetailRating.setRating((results.get(0).productStatistics.reviewStatistics.averageOverallRating).toFloat())
            binding.mecRatingLebel.text = DecimalFormat("0.0").format(results.get(0).productStatistics.reviewStatistics.averageOverallRating)
            binding.mecReviewLebel.text = " (" + results.get(0).productStatistics.reviewStatistics.totalReviewCount.toString() + " " + getString(R.string.mec_reviews)+ ")"
        }

    }


    fun showPriceDetail() {

        val textSize16 = getResources().getDimensionPixelSize(com.philips.platform.mec.R.dimen.mec_product_detail_discount_price_label_size);
        val textSize12 = getResources().getDimensionPixelSize(com.philips.platform.mec.R.dimen.mec_product_detail_price_label_size);
        if(null!=product) {

            if (product!!.discountPrice != null && product!!.discountPrice.formattedValue != null && product!!.discountPrice.formattedValue.length > 0 && (product!!.price.value - product!!.discountPrice.value) > 0) {
                mecPriceDetailId.visibility = View.VISIBLE
                mec_priceDetailIcon.visibility = View.VISIBLE
                mec_priceDiscount.visibility = View.VISIBLE
                mec_priceDiscountIcon.visibility = View.VISIBLE
                val price = SpannableString(product!!.price.formattedValue);
                price.setSpan(AbsoluteSizeSpan(textSize12), 0, product!!.price.formattedValue.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                price.setSpan(StrikethroughSpan(), 0, product!!.price.formattedValue.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                price.setSpan(ForegroundColorSpan(R.attr.uidContentItemTertiaryNormalTextColor), 0, product!!.price.formattedValue.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                val discountPrice = SpannableString(product!!.discountPrice.formattedValue);
                discountPrice.setSpan(AbsoluteSizeSpan(textSize16), 0, product!!.discountPrice.formattedValue.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                val CharSequence = TextUtils.concat(price, "  ", discountPrice);
                mecPriceDetailId.text = CharSequence;
                val discount = (product!!.price.value - product!!.discountPrice.value) / product!!.price.value * 100

                val discountRounded: String = String.format("%.2f", discount).toString()
                mec_priceDiscount.text = "-" + discountRounded + "%"
            } else if (product!!.price != null && product!!.price.formattedValue != null && product!!.price.formattedValue.length > 0) {
                mecPriceDetailId.visibility = View.VISIBLE
                mec_priceDetailIcon.visibility = View.VISIBLE

                mec_priceDiscount.visibility = View.GONE
                mec_priceDiscountIcon.visibility = View.GONE
                mecPriceDetailId.text = product!!.price.formattedValue;

            } else {
                mecPriceDetailId.visibility = View.GONE
                mec_priceDetailIcon.visibility = View.GONE
                mec_priceDiscount.visibility = View.GONE
                mec_priceDiscountIcon.visibility = View.GONE
            }
        }

    }


    fun onBuyFromRetailerClick() {
        buyFromRetailers()
    }

    fun addToCartClick(){

        //TODO - do this using observer

        if(null!=binding.product ) {
            if (isUserLoggedIn()) {
                val addToProductCallback =  object: ECSCallback<ECSShoppingCart, Exception> {

                    override fun onResponse(eCSShoppingCart: ECSShoppingCart?) {
                        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
                        val bundle = Bundle()
                        bundle.putSerializable(MECConstant.MEC_SHOPPING_CART, eCSShoppingCart)
                        val fragment = MECShoppingCartFragment()
                        fragment.arguments = bundle
                        replaceFragment(fragment,MECShoppingCartFragment.TAG,true)
                    }

                    override fun onFailure(error: Exception?, ecsError: ECSError?) {
                        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
                        val mecError = MecError(error, ecsError,null)
                        fragmentManager?.let { context?.let { it1 -> MECutility.showErrorDialog(it1, it,getString(R.string.mec_ok), getString(R.string.mec_product_detail_title), mecError!!.exception!!.message.toString()) } }
                    }

                }
                product.let {
                    showProgressBar(binding.mecProgress.mecProgressBarContainer)
                    ecsProductDetailViewModel.addProductToShoppingcart(it,addToProductCallback)
                }
            }else{
                fragmentManager?.let { context?.let { it1 -> MECutility.showErrorDialog(it1, it,getString(R.string.mec_ok), getString(R.string.mec_product_detail_title), getString(R.string.mec_cart_login_error_message)) } }
            }
        }
    }

    private fun buyFromRetailers() {
        val bundle = Bundle()
        var bottomSheetFragment = MECRetailersFragment()
        bundle.putSerializable(MECConstant.MEC_KEY_PRODUCT, retailersList)
        bundle.putSerializable(MEC_PRODUCT,binding.product)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.setTargetFragment(this, MECConstant.RETAILER_REQUEST_CODE)
        fragmentManager?.let { bottomSheetFragment.show(it, bottomSheetFragment.tag) }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MECConstant.RETAILER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (data?.extras?.containsKey(MECConstant.SELECTED_RETAILER)!!) {
                val ecsRetailer: ECSRetailer = data.getSerializableExtra(MECConstant.SELECTED_RETAILER) as ECSRetailer
                param = ecsRetailer.xactparam
                val bundle = Bundle()
                bundle.putString(MECConstant.MEC_BUY_URL, ecsProductDetailViewModel.uuidWithSupplierLink(ecsRetailer.buyURL, param))
                bundle.putString(MECConstant.MEC_STORE_NAME, ecsRetailer.name)
                bundle.putBoolean(MECConstant.MEC_IS_PHILIPS_SHOP, ecsProductDetailViewModel.isPhilipsShop(ecsRetailer))

                tagActionsforRetailer(ecsRetailer.name, MECutility.stockStatus(ecsRetailer.availability))
                val fragment = WebBuyFromRetailersFragment()
                fragment.arguments = bundle
                replaceFragment(fragment, WebBuyFromRetailersFragment.TAG, true)
            }
        }
    }

    override fun processError(mecError: MecError?, bool: Boolean) {

        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        if(mecError?.mECRequestType == MECRequestType.MEC_ADD_PRODUCT_TO_SHOPPING_CART){
            super.processError(mecError,true)
        }else{
            super.processError(mecError,false)
            binding.detailsParentLayout.visibility = View.GONE
            binding.mecProductDetailsEmptyTextLabel.visibility = View.VISIBLE
        }


    }


    private fun tagActions(product: ECSProduct) {
        var map = HashMap<String, String>()
        map.put(specialEvents, prodView)
        map.put(mecProducts, com.philips.platform.mec.analytics.MECAnalytics.getProductInfo(product))
        com.philips.platform.mec.analytics.MECAnalytics.trackMultipleActions(sendData, map)
    }


    private fun tagActionsforRetailer(name: String, status: String) {
        var map = HashMap<String, String>()
        map.put(retailerName, name)
        map.put(stockStatus, status)
        map.put(mecProducts, com.philips.platform.mec.analytics.MECAnalytics.getProductInfo(product!!))
        com.philips.platform.mec.analytics.MECAnalytics.trackMultipleActions(sendData, map)
    }

    companion object {

        val TAG = "MECProductDetailsFragment"

        @JvmStatic
        fun tagOutOfStockActions(product: ECSProduct) {
            var map = HashMap<String, String>()
            map.put(specialEvents, outOfStock)
            map.put(mecProducts, com.philips.platform.mec.analytics.MECAnalytics.getProductInfo(product))
            com.philips.platform.mec.analytics.MECAnalytics.trackMultipleActions(sendData, map)
        }
    }

    override fun onStop() {
        super.onStop()
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
    }



}
