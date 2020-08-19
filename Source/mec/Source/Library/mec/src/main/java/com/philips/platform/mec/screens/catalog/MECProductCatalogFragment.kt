/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.philips.platform.ecs.microService.model.product.ECSProduct
import com.philips.platform.mec.R
import com.philips.platform.mec.analytics.MECAnalyticPageNames.productCataloguePage
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.analytics.MECAnalyticsConstant.gridView
import com.philips.platform.mec.analytics.MECAnalyticsConstant.listView
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.databinding.MecCatalogFragmentBinding
import com.philips.platform.mec.integration.serviceDiscovery.MECManager
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.detail.MECProductDetailsFragment
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.uid.view.widget.Label
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 */
open class MECProductCatalogFragment : MecBaseFragment(), Pagination, ItemClickListener {
    override fun getFragmentTag(): String {
        return TAG
    }


    companion object {
        val TAG: String = "MECProductCatalogFragment"
    }

    override fun isCategorizedHybrisPagination(): Boolean {
        return false
    }

    var mRootView: View? = null

    private val mProductReviewObserver: Observer<MutableList<MECProductReview>> = Observer<MutableList<MECProductReview>> { mecProductReviews ->

        mecProductReviews?.let { mProductsWithReview.addAll(it) }
        adapter.notifyDataSetChanged()

        binding.mecCatalogParentLayout.visibility = View.VISIBLE
        showPrivacyURL()
        dismissPaginationProgressBar()
        dismissProgressBar(binding.mecCatalogProgress.mecProgressBarContainer)
        isCallOnProgress = false

        //For categorized
        if (isCategorizedHybrisPagination()) {
            handleHybrisCategorized()
        }
    }

    open fun handleHybrisCategorized() {
        //do nothing
    }


    override fun onItemClick(item: Any) {

        val ecsProduct = item as MECProductReview
        val bundle = Bundle()
        bundle.putParcelable(MECConstant.MEC_KEY_PRODUCT, ecsProduct.ecsProduct)

        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
        val fragment = MECProductDetailsFragment()
        fragment.arguments = bundle
        replaceFragment(fragment, fragment.getFragmentTag(), true)
    }

    override fun isPaginationSupported(): Boolean {
        return shouldSupportPagination
    }

    private var highLightedBackgroundColor: Int = 0

    var shouldSupportPagination = true
    var isCallOnProgress: Boolean = true

    var offSet: Int = 0
    val limit: Int = 50
    var isAllProductDownloaded = false

    //Categorized

    var categorizedCtns: ArrayList<String>? = null
    var totalProductsTobeSearched: Int = 0
    lateinit var mECProductCatalogService: MECProductCatalogService


    private val mProductObserver = Observer<com.philips.platform.ecs.microService.model.product.ECSProducts> {
        offSet += limit
        var commerceProducts = it.commerceProducts
        if (commerceProducts.size < limit) isAllProductDownloaded = true

        if (commerceProducts.isNotEmpty()) {

            //Process commerce product for categorization
            if (isCategorizedHybrisPagination()) {
                commerceProducts = mECProductCatalogService.getCategorizedProducts(categorizedCtns, commerceProducts)
                if (commerceProducts.isEmpty()) {
                    isCallOnProgress = false
                    handleHybrisCategorized()
                }
            }

            if (commerceProducts.isNotEmpty()) {
                ecsProductViewModel.fetchProductReview(commerceProducts)
            }

        } else {
            dismissPaginationProgressBar()
            dismissProgressBar(binding.mecCatalogProgress.mecProgressBarContainer)
            decideToShowNoProduct()
            isCallOnProgress = false
        }

    }

    private fun decideToShowNoProduct() {
        if (isPaginationSupported()) {
            if (isNoProductFound()) {
                showNoProduct() // if pagination is supported , check all pages are downloaded and still no products shown on recyclerview
            }
        } else {
            showNoProduct()
        }
    }


    private fun showPrivacyURL() {

        if (MECDataHolder.INSTANCE.getPrivacyUrl() != null) {
            binding.mecPrivacyLayout.visibility = View.VISIBLE
            binding.mecSeparator.visibility = View.VISIBLE
            binding.mecLlLayout.visibility = View.VISIBLE
        }
    }

    open fun showNoProduct() {
        isCallOnProgress = false
        binding.mecCatalogParentLayout.visibility = View.GONE
        dismissPaginationProgressBar()
        dismissProgressBar(binding.mecCatalogProgress.mecProgressBarContainer)
        binding.mecProductCatalogEmptyTextLabel.visibility = View.VISIBLE
    }


    private lateinit var adapter: MECProductCatalogBaseAbstractAdapter


    lateinit var ecsProductViewModel: EcsProductViewModel


    internal val mProductsWithReview: MutableList<MECProductReview> = mutableListOf()


    lateinit var binding: MecCatalogFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if (null == mRootView) {
            binding = MecCatalogFragmentBinding.inflate(inflater, container, false)

            mECProductCatalogService = MECProductCatalogService()
            ecsProductViewModel = ViewModelProvider(this).get(EcsProductViewModel::class.java)

            ecsProductViewModel.ecsPILProducts.observe(this, mProductObserver)
            ecsProductViewModel.ecsPILProductsReviewList.observe(this, mProductReviewObserver)
            ecsProductViewModel.mecError.observe(this, this)

            val bundle = arguments

            highLightedBackgroundColor = MECutility.getAttributeColor(binding.root.context, R.attr.uidToggleButtonInputQuietNormalOnBackgroundColor)


            binding.mecGrid.setOnClickListener {
                if (null == binding.mecGrid.background || getBackgroundColorOfFontIcon(binding.mecGrid) == 0) {//if Grid is currently not selected
                    binding.mecGrid.setBackgroundColor(highLightedBackgroundColor)
                    binding.mecList.setBackgroundColor(ContextCompat.getColor(binding.mecList.context, R.color.uidTransparent))
                    binding.productCatalogRecyclerView.layoutManager = GridLayoutManager(activity, 2)
                    binding.productCatalogRecyclerView.setItemAnimator(DefaultItemAnimator())
                    adapter.catalogView = MECProductCatalogBaseAbstractAdapter.CatalogView.GRID
                    binding.productCatalogRecyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                    MECAnalytics.tagProductList(getProductsFromProductsWithReview(), gridView)
                }
            }

            binding.mecList.setOnClickListener {
                if (null == binding.mecList.background || getBackgroundColorOfFontIcon(binding.mecList) == 0) { //if List is currently not selected
                    binding.mecList.setBackgroundColor(highLightedBackgroundColor)
                    binding.mecGrid.setBackgroundColor(ContextCompat.getColor(binding.mecGrid.context, R.color.uidTransparent))
                    binding.productCatalogRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                    adapter.catalogView = MECProductCatalogBaseAbstractAdapter.CatalogView.LIST
                    binding.productCatalogRecyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                    MECAnalytics.tagProductList(getProductsFromProductsWithReview(), listView)
                }
            }

            binding.mecFilter.setOnClickListener {
                if (null == binding.mecFilter.background || getBackgroundColorOfFontIcon(binding.mecFilter) == 0) {//if Filter is currently not selected
                    binding.mecFilter.setBackgroundColor(highLightedBackgroundColor)
                    binding.mecList.setBackgroundColor(ContextCompat.getColor(binding.mecList.context, R.color.uidTransparent))
                    filterCatalog()
                }
            }


            val mClearIconView = binding.mecSearchBox.getClearIconView()
            val searchText = binding.mecSearchBox.searchTextView
            binding.mecSearchBox.searchTextView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 0) {
                        shouldSupportPagination = true
                        binding.llBannerPlaceHolder.visibility = View.VISIBLE
                    } else {
                        shouldSupportPagination = false
                        binding.llBannerPlaceHolder.visibility = View.GONE
                    }


                    adapter.filter.filter(s)


                    val text = context?.resources?.getText(R.string.mec_no_result).toString() + " " + s
                    binding.tvEmptyListFound.text = text

                    if (MECDataHolder.INSTANCE.getPrivacyUrl() != null) {
                        if (adapter.itemCount != 0) {
                            binding.mecPrivacyLayout.visibility = View.VISIBLE
                        } else {
                            binding.mecPrivacyLayout.visibility = View.GONE
                        }
                    }

                    if (s?.length == 0) {
                        binding.mecEmptyResult.visibility = View.GONE
                    }


                    mClearIconView.setOnClickListener {
                        searchText.text.clear()
                        binding.mecEmptyResult.visibility = View.GONE
                    }
                }

            })


            binding.mecNestedScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {


                    if (shouldFetchNextPage() && !isCallOnProgress) {
                        showPaginationProgressBar()
                        executeRequest()
                    }
                }
            })


            privacyTextView(binding.mecPrivacy)

            adapter = MECProductCatalogAdapter(mProductsWithReview, this)
            binding.productCatalogRecyclerView.adapter = adapter


            binding.productCatalogRecyclerView.apply {
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }

            adapter.emptyView = binding.mecEmptyResult


            mRootView = binding.root

            categorizedCtns = arguments?.getStringArrayList(MECConstant.CATEGORISED_PRODUCT_CTNS) as ArrayList<String>
            totalProductsTobeSearched = categorizedCtns?.size ?: 0

            executeRequest()
            fetchShoppingCartData()
        }
        return binding.root
    }

    private fun getProductsFromProductsWithReview(): MutableList<ECSProduct> {
        val ecsProductList = mutableListOf<ECSProduct>()

        for (productWithReview in mProductsWithReview) {
            ecsProductList.add(productWithReview.ecsProduct)
        }
        return ecsProductList
    }

    private fun fetchShoppingCartData() {
        if (isUserLoggedIn() && MECDataHolder.INSTANCE.hybrisEnabled) {
            GlobalScope.launch {
                val mecManager: MECManager = MECManager()
                MECDataHolder.INSTANCE.mecCartUpdateListener?.let { mecManager.getShoppingCartData(it) }
            }
        }
    }

    private fun showPaginationProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    internal fun dismissPaginationProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun getBackgroundColorOfFontIcon(label: Label): Int {
        val cd: ColorDrawable = label.background as ColorDrawable;
        val colorCode: Int = cd.color;
        return colorCode
    }

    override fun onResume() {
        super.onResume()
        setTitleAndBackButtonVisibility(R.string.mec_product_title, true)
        setCartIconVisibility(true)
    }

    override fun onStart() {
        super.onStart()
        MECAnalytics.trackPage(productCataloguePage)

        val productList = mutableListOf<ECSProduct>()
        for (productWithReview in mProductsWithReview) {
            productList.add(productWithReview.ecsProduct)
        }
        MECAnalytics.tagProductList(productList, listView)
    }

    private fun privacyTextView(view: TextView) {
        val spanTxt = SpannableStringBuilder(
                getString(R.string.mec_read))
        spanTxt.append(" ")
        spanTxt.append(getString(R.string.mec_privacy))
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                showPrivacyFragment()
                dismissPaginationProgressBar()
                // hideProgressBar()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
                ds.color = R.attr.uidHyperlinkDefaultPressedTextColor
            }
        }, spanTxt.length - getString(R.string.mec_privacy).length, spanTxt.length, 0)
        spanTxt.append(" ")
        spanTxt.append(getString(R.string.mec_more_info))
        view.setHighlightColor(Color.TRANSPARENT)
        view.movementMethod = LinkMovementMethod.getInstance()
        view.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }

    private fun showPrivacyFragment() {
        val bundle = Bundle()
        bundle.putString(MECConstant.MEC_PRIVACY_URL, MECDataHolder.INSTANCE.getPrivacyUrl())
        val mecPrivacyFragment = MecPrivacyFragment()
        mecPrivacyFragment.arguments = bundle
        replaceFragment(mecPrivacyFragment, mecPrivacyFragment.getFragmentTag(), true)
    }

    private fun isScrollDown(lay: LinearLayoutManager): Boolean {
        val visibleItemCount = lay.childCount
        val firstVisibleItemPosition = lay.findFirstVisibleItemPosition()
        return visibleItemCount + firstVisibleItemPosition >= lay.itemCount && firstVisibleItemPosition >= 0
    }

    open fun executeRequest() {
        if (MECDataHolder.INSTANCE.hybrisEnabled) {
            isCallOnProgress = true
            ecsProductViewModel.fetchProducts(offSet, limit)
        } else {
            binding.mecProductCatalogEmptyTextLabel.visibility = View.VISIBLE
        }
    }

    private fun shouldFetchNextPage(): Boolean {

        if (!isPaginationSupported()) {
            return false
        }
        val lay = binding.productCatalogRecyclerView
                .layoutManager as LinearLayoutManager

        if (isScrollDown(lay) && !isAllProductDownloaded) {
            return true
        }
        return false
    }

    override fun onStop() {
        super.onStop()
        dismissPaginationProgressBar()
    }

    override fun processError(mecError: MecError?, showDialog: Boolean) {
        super.processError(mecError, showDialog)

        dismissPaginationProgressBar()
        dismissProgressBar(binding.mecCatalogProgress.mecProgressBarContainer)
        if (offSet == 0) showNoProduct()
    }

    internal fun isNoProductFound(): Boolean {
        return isAllProductDownloaded && mProductsWithReview.size == 0
    }

    private fun filterCatalog() {
        val bottomSheetFragment = MECFilterCatalogFragment()
        bottomSheetFragment.setTargetFragment(this, MECConstant.FILTER_REQUEST_CODE)
        fragmentManager?.let { bottomSheetFragment.show(it, bottomSheetFragment.tag) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MECConstant.FILTER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            MECLog.d(TAG, "OnActivityResult called from MECProductCatalgFragment")
        }
    }
}