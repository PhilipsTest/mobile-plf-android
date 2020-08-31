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
import androidx.lifecycle.ViewModelProvider
import com.bazaarvoice.bvandroidsdk.BulkRatingsResponse
import com.bazaarvoice.bvandroidsdk.Statistics
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.ecs.microService.model.retailer.ECSRetailer
import com.philips.platform.ecs.microService.model.retailer.ECSRetailerList
import com.philips.platform.mec.R
import com.philips.platform.mec.analytics.MECAnalyticPageNames.productDetailsPage
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.analytics.MECAnalyticsConstant.mecProducts
import com.philips.platform.mec.analytics.MECAnalyticsConstant.outOfStock
import com.philips.platform.mec.analytics.MECAnalyticsConstant.prodView
import com.philips.platform.mec.analytics.MECAnalyticsConstant.retailerName
import com.philips.platform.mec.analytics.MECAnalyticsConstant.scAdd
import com.philips.platform.mec.analytics.MECAnalyticsConstant.sendData
import com.philips.platform.mec.analytics.MECAnalyticsConstant.specialEvents
import com.philips.platform.mec.analytics.MECAnalyticsConstant.stockStatus
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.databinding.MecProductDetailsBinding
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
import java.text.DecimalFormat

/**
 * A simple [Fragment] subclass.
 */
open class MECProductDetailsFragment : MecBaseFragment() {

    override fun getFragmentTag(): String {
        return "MECProductDetailsFragment"
    }

    var mRootView: View? = null
    lateinit var param: String

    lateinit var binding: MecProductDetailsBinding
    internal var product: ECSProduct? = null
    private lateinit var retailersList: ECSRetailerList
    private lateinit var ecsRetailerViewModel: ECSRetailerViewModel

    lateinit var ecsProductDetailViewModel: EcsProductDetailViewModel

    private val eCSRetailerListObserver: Observer<ECSRetailerList> = object : Observer<ECSRetailerList> {
        override fun onChanged(retailers: ECSRetailerList) {
            retailersList = retailers
            ecsProductDetailViewModel.removeBlacklistedRetailers(retailersList)
            if (retailers.wrbresults?.OnlineStoresForProduct != null) {
                if (retailersList.wrbresults?.OnlineStoresForProduct?.Stores?.Store?.size ?:0 > 0) {
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

    private val ratingObserver: Observer<BulkRatingsResponse> = Observer<BulkRatingsResponse> { response -> updateData(response?.results) }

    private val productObserver: Observer<ECSProduct> = Observer<ECSProduct> { ecsProduct ->

        //TO show No Image for no asset found for a product
        if (ecsProduct.assets?.getValidPRXImageAssets()?.isEmpty() == true) {
            ecsProductDetailViewModel.addNoAsset(ecsProduct)
        }
        binding.product = ecsProduct

        setUpTab(ecsProduct)
        showPriceDetail()
        addToCartVisibility(ecsProduct)
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

                val availability = product?.attributes?.availability
                availability?.let {

                    if(MECutility.isStockAvailable(it.status,it.quantity ?:0)){
                        binding.mecProductDetailStockStatus.text = binding.mecProductDetailStockStatus.context.getString(R.string.mec_in_stock)
                        binding.mecProductDetailStockStatus.setTextColor(binding.mecProductDetailStockStatus.context.getColor(R.color.uid_signal_green_level_30))
                    }else{
                        binding.mecProductDetailStockStatus.text = binding.mecProductDetailStockStatus.context.getString(R.string.mec_out_of_stock)
                        binding.mecProductDetailStockStatus.setTextColor(binding.mecProductDetailStockStatus.context.getColor(R.color.uid_signal_red_level_30))
                        product?.let { tagOutOfStockActions(it) }
                    }

                }
            }
        }
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        binding.rlParentContent.visibility = View.VISIBLE

    }

    private val cartObserver: Observer<ECSShoppingCart> = Observer { ecsShoppingCart ->
        gotoShoppingCartScreen(ecsShoppingCart)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if (null == mRootView) {
            binding = MecProductDetailsBinding.inflate(inflater, container, false)

            binding.fragment = this
            binding.mecDataHolder = MECDataHolder.INSTANCE
            binding.isHybris = MECDataHolder.INSTANCE.hybrisEnabled

            ecsProductDetailViewModel = ViewModelProvider(this).get(EcsProductDetailViewModel::class.java)

            ecsRetailerViewModel = ViewModelProvider(this).get(ECSRetailerViewModel::class.java)
            ecsRetailerViewModel.ecsRetailerList.observe(this, eCSRetailerListObserver)
            ecsProductDetailViewModel.ecsProduct.observe(this, productObserver)
            ecsProductDetailViewModel.ecsShoppingCart.observe(this, cartObserver)
            ecsProductDetailViewModel.bulkRatingResponse.observe(this, ratingObserver)
            ecsProductDetailViewModel.mecError.observe(this, this)

            binding.indicator.viewPager = binding.pager
            product = arguments?.getParcelable(MECConstant.MEC_KEY_PRODUCT)

            executeRequest()
            getRatings()
            fetchCartQuantity()

            mRootView=binding.root

        }
        return mRootView
    }

    private fun setUpTab(product: ECSProduct) {
        val fragmentAdapter = TabPagerAdapter(this.childFragmentManager, product, binding.mecReviewLebel.context)
        binding.viewpagerMain.offscreenPageLimit = 4
        binding.viewpagerMain.adapter = fragmentAdapter
        binding.tabsMain.setupWithViewPager(binding.viewpagerMain)
    }

    override fun onResume() {
        super.onResume()
        setTitleAndBackButtonVisibility(R.string.mec_product_detail_title, true)
        setCartIconVisibility(true)
    }

    override fun onStart() {
        super.onStart()
        MECAnalytics.trackPage(productDetailsPage)
        product?.let { tagActions(it) }
    }


    private fun addToCartVisibility(product: ECSProduct) {
        if (!MECDataHolder.INSTANCE.hybrisEnabled) {
            binding.mecAddToCartButton.visibility = View.GONE
        } else if (MECDataHolder.INSTANCE.hybrisEnabled &&  !(MECutility.isStockAvailable(product.attributes?.availability?.status, product.attributes?.availability?.quantity ?:0))) {
            binding.mecAddToCartButton.visibility = View.VISIBLE
            binding.mecAddToCartButton.isEnabled = false
        } else {
            binding.mecAddToCartButton.visibility = View.VISIBLE
            binding.mecAddToCartButton.isEnabled = true
        }
    }

    open fun executeRequest() {
        binding.rlParentContent.visibility = View.INVISIBLE
        showProgressBar(binding.mecProgress.mecProgressBarContainer)
        product?.let { ecsProductDetailViewModel.getProductDetail(it) }
    }


    private fun getRetailerDetails() {
        product?.ctn?.let { ecsRetailerViewModel.getRetailers(it) }
    }

    private fun getRatings() {
        product?.ctn?.let {  ecsProductDetailViewModel.getRatings(it.replace("/","_")) }
    }


    //TODO bind it
    fun updateData(results: List<Statistics>?) {
        if (results?.isNotEmpty() == true) {
            binding.mecDetailRating.setRating((results.get(0).productStatistics.reviewStatistics.averageOverallRating).toFloat())
            binding.mecRatingLebel.text = DecimalFormat("0.0").format(results.get(0).productStatistics.reviewStatistics.averageOverallRating)
            binding.mecReviewLebel.text = " (" + results.get(0).productStatistics.reviewStatistics.totalReviewCount.toString() + " " + getString(R.string.mec_reviews) + ")"
        }

    }


    fun showPriceDetail() {

        val textSize16 = resources.getDimensionPixelSize(R.dimen.mec_product_detail_discount_price_label_size);
        val textSize12 = resources.getDimensionPixelSize(R.dimen.mec_product_detail_price_label_size);
        if (product?.attributes?.discountPrice?.formattedValue?.length ?:0> 0 && (product?.attributes?.price?.value ?:0.0 - (product?.attributes?.discountPrice?.value ?: 0.0)) > 0) {
            mecPriceDetailId.visibility = View.VISIBLE
            mec_priceDetailIcon.visibility = View.VISIBLE
            mec_priceDiscount.visibility = View.VISIBLE
            mec_priceDiscountIcon.visibility = View.VISIBLE
            val price = SpannableString(product?.attributes?.price?.formattedValue)
            price.setSpan(AbsoluteSizeSpan(textSize12), 0, product?.attributes?.price?.formattedValue?.length ?:0, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            price.setSpan(StrikethroughSpan(), 0, product?.attributes?.price?.formattedValue?.length ?:0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            price.setSpan(ForegroundColorSpan(R.attr.uidContentItemTertiaryNormalTextColor), 0, product?.attributes?.price?.formattedValue?.length ?:0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            val discountPrice = SpannableString(product?.attributes?.discountPrice?.formattedValue ?:"")
            discountPrice.setSpan(AbsoluteSizeSpan(textSize16), 0, product?.attributes?.discountPrice?.formattedValue?.length ?:0, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            val CharSequence = TextUtils.concat(price, "  ", discountPrice);
            mecPriceDetailId.text = CharSequence;
            val discount = (product?.attributes?.price?.value ?:0.0 - (product?.attributes?.discountPrice?.value ?:0.0) )/ (product?.attributes?.price?.value ?:0.0) * 100

            val discountRounded: String = String.format("%.2f", discount)
            mec_priceDiscount.text = "-" + discountRounded + "%"
        } else if (product?.attributes?.price?.formattedValue?.length ?:0 > 0) {
            mecPriceDetailId.visibility = View.VISIBLE
            mec_priceDetailIcon.visibility = View.VISIBLE

            mec_priceDiscount.visibility = View.GONE
            mec_priceDiscountIcon.visibility = View.GONE
            mecPriceDetailId.text = product?.attributes?.price?.formattedValue ?:""

        } else {
            mecPriceDetailId.visibility = View.GONE
            mec_priceDetailIcon.visibility = View.GONE
            mec_priceDiscount.visibility = View.GONE
            mec_priceDiscountIcon.visibility = View.GONE
        }

    }

    fun onBuyFromRetailerClick() {
        buyFromRetailers()
    }

    fun addToCartClick() {
        val product = binding.product
        product?.let {
            if (isUserLoggedIn()){
                showProgressBar(binding.mecProgress.mecProgressBarContainer)
                ecsProductDetailViewModel.addProductToShoppingcart(it.ctn)
            }else{
                activity?.supportFragmentManager?.let { context?.let { it1 -> MECutility.showErrorDialog(it1, it,getString(R.string.mec_ok), getString(R.string.mec_product_detail_title), R.string.mec_cart_login_error_message) } }
            }
        }
    }

    private fun gotoShoppingCartScreen(eCSShoppingCart: ECSShoppingCart) {
        product?.let { tagAddToCart(it) }
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        val bundle = Bundle()
        bundle.putParcelable(MECConstant.MEC_SHOPPING_CART, eCSShoppingCart)
        val fragment = MECShoppingCartFragment()
        fragment.arguments = bundle
        replaceFragment(fragment, fragment.getFragmentTag(), true)
    }

    private fun buyFromRetailers() {
        val bundle = Bundle()
        val bottomSheetFragment = MECRetailersFragment()
        bundle.putParcelable(MECConstant.MEC_KEY_PRODUCT, retailersList)
        bundle.putParcelable(MEC_PRODUCT, binding.product)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.setTargetFragment(this, MECConstant.RETAILER_REQUEST_CODE)
        activity?.supportFragmentManager?.let { bottomSheetFragment.show(it, bottomSheetFragment.tag) }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MECConstant.RETAILER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (data?.extras?.containsKey(MECConstant.SELECTED_RETAILER) == true) {
                val ecsRetailer: ECSRetailer = data.getParcelableExtra(MECConstant.SELECTED_RETAILER) as ECSRetailer
                param = ecsRetailer.xactparam ?:""
                val bundle = Bundle()
                bundle.putString(MECConstant.MEC_BUY_URL, ecsProductDetailViewModel.uuidWithSupplierLink(ecsRetailer.buyURL ?:"", param))
                bundle.putString(MECConstant.MEC_STORE_NAME, ecsRetailer.name)
                bundle.putBoolean(MECConstant.MEC_IS_PHILIPS_SHOP, ecsProductDetailViewModel.isPhilipsShop(ecsRetailer))

                tagActionsforRetailer(ecsRetailer.name ?:"", MECutility.stockStatus(ecsRetailer.availability ?:"NO"))
                val fragment = WebBuyFromRetailersFragment()
                fragment.arguments = bundle
                replaceFragment(fragment, fragment.getFragmentTag(), true)
            }
        }
    }

    override fun processError(mecError: MecError?, showDialog: Boolean) {

        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        if (mecError?.mECRequestType == MECRequestType.MEC_ADD_PRODUCT_TO_SHOPPING_CART) {
            super.processError(mecError, true)
        } else {
            super.processError(mecError, false)
            binding.detailsParentLayout.visibility = View.GONE
            binding.mecProductDetailsEmptyTextLabel.visibility = View.VISIBLE
        }


    }


    private fun tagActions(product: ECSProduct) {
        val map = HashMap<String, String>()
        map.put(specialEvents, prodView)
        map.put(mecProducts, MECAnalytics.getProductInfo(product))
        MECAnalytics.trackMultipleActions(sendData, map)
    }


    private fun tagActionsforRetailer(name: String, status: String) {
        val map = HashMap<String, String>()
        map.put(retailerName, name)
        map.put(stockStatus, status)
        product?.let { MECAnalytics.getProductInfo(it) }?.let { map.put(mecProducts, it) }
        MECAnalytics.trackMultipleActions(sendData, map)
    }

    private fun tagAddToCart(product: ECSProduct){
        val map = HashMap<String, String>()
        map.put(specialEvents, scAdd)
        map.put(mecProducts, MECAnalytics.getProductInfoWithChangedQuantity(product,1)) // 1 product added to cart
        MECAnalytics.trackMultipleActions(sendData, map)
    }

    companion object {
        val TAG = "MECProductDetailsFragment"

        @JvmStatic
        fun tagOutOfStockActions(product: ECSProduct) {
            val map = HashMap<String, String>()
            map.put(specialEvents, outOfStock)
            map.put(mecProducts, MECAnalytics.getProductInfo(product))
            MECAnalytics.trackMultipleActions(sendData, map)
        }
    }

    override fun onStop() {
        super.onStop()
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
    }
}
