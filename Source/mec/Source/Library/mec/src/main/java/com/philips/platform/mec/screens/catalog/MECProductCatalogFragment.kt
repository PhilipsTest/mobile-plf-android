/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog


import android.content.Context
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
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.philips.cdp.di.ecs.model.products.ECSProduct
import com.philips.cdp.di.ecs.model.products.ECSProducts
import com.philips.platform.mec.R
import com.philips.platform.mec.analytics.MECAnalyticPageNames.productCatalogue
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
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.uid.view.widget.Label
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 */
open class MECProductCatalogFragment : MecBaseFragment(), Pagination, ItemClickListener {
    override fun getFragmentTag(): String {
        return "MECProductCatalogFragment"
    }


    companion object {
        val TAG: String = "MECProductCatalogFragment"
    }

    override fun isCategorizedHybrisPagination(): Boolean {
        return false
    }

    var mRootView: View? = null


    private val productReviewObserver: Observer<MutableList<MECProductReview>> = Observer<MutableList<MECProductReview>> { mecProductReviews ->
        productReviewList.clear()
        mecProductReviews?.let { productReviewList.addAll(it) }
        adapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.GONE
        binding.mecCatalogProgress.mecProgressBar.visibility = View.GONE
        isCallOnProgress = false
        hideProgressBar()

        //For categorized
        if (isCategorizedHybrisPagination()) {
            doProgressbarOperation()
        }
    }

    open fun doProgressbarOperation() {
        //do nothing
    }


    override fun onItemClick(item: Any) {

        val ecsProduct = item as MECProductReview
        val bundle = Bundle()
        bundle.putSerializable(MECConstant.MEC_KEY_PRODUCT, ecsProduct.ecsProduct)

        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
        val fragment = MECProductDetailsFragment()
        fragment.arguments = bundle
        replaceFragment(fragment, MECProductDetailsFragment.TAG, true)
    }

    override fun isPaginationSupported(): Boolean {
        return shouldSupportPagination
    }

    private lateinit var mecCatalogUIModel: MECCatalogUIModel
    private var highLightedBackgroundColor: Int = 0

    var totalPages: Int = 0
    var currentPage: Int = 0
    var pageSize: Int = 20
    var shouldSupportPagination = true
    var isCallOnProgress: Boolean = true

    //Categorized

    lateinit var categorizedCtns: ArrayList<String>
    var totalProductsTobeSearched: Int = 0


    private val productObserver: Observer<MutableList<ECSProducts>> = Observer<MutableList<ECSProducts>>(fun(ecsProductsList: MutableList<ECSProducts>?) {
        if (!ecsProductsList.isNullOrEmpty()) {

            totalPages = ecsProductsList.get(ecsProductsList.size - 1).pagination?.totalPages ?: 0
            currentPage = ecsProductsList.get(ecsProductsList.size - 1).pagination?.currentPage ?: 0

            productList.clear()

            for (ecsProducts in ecsProductsList) {

                for (ecsProduct in ecsProducts.products) {
                    productList.add(ecsProduct)
                }
            }

            if (productList.size != 0) {
                com.philips.platform.mec.analytics.MECAnalytics.tagProductList(productList)
                ecsProductViewModel.fetchProductReview(productList)

                binding.mecCatalogParentLayout.visibility = View.VISIBLE

                if (productList.size != 0 && MECDataHolder.INSTANCE.getPrivacyUrl() != null) {
                    binding.mecPrivacyLayout.visibility = View.VISIBLE
                    binding.mecSeparator.visibility = View.VISIBLE
                    binding.mecLlLayout.visibility = View.VISIBLE
                }

            } else {

                hideProgressBar()
                showNoProduct()
            }

        } else {
            hideProgressBar()
            showNoProduct()
        }
    })

    open fun showNoProduct() {
        isCallOnProgress = false
        binding.mecCatalogParentLayout.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.mecCatalogProgress.mecProgressBar.visibility = View.GONE
        binding.mecProductCatalogEmptyTextLabel.visibility = View.VISIBLE
    }

    private lateinit var adapter: MECProductCatalogBaseAbstractAdapter


    lateinit var ecsProductViewModel: EcsProductViewModel


    private lateinit var productReviewList: MutableList<MECProductReview>
    lateinit var productList: MutableList<ECSProduct>


    lateinit var binding: MecCatalogFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if (null == mRootView) {
            binding = MecCatalogFragmentBinding.inflate(inflater, container, false)

            mecCatalogUIModel = MECCatalogUIModel()


            ecsProductViewModel = ViewModelProviders.of(this).get(EcsProductViewModel::class.java)

            ecsProductViewModel.ecsProductsList.observe(this, productObserver)
            ecsProductViewModel.ecsProductsReviewList.observe(this, productReviewObserver)
            ecsProductViewModel.mecError.observe(this, this)

            val bundle = arguments

            highLightedBackgroundColor = MECutility.getAttributeColor(binding.root.context, R.attr.uidToggleButtonInputQuietNormalOnBackgroundColor)


            binding.mecGrid.setOnClickListener {
                if (null == binding.mecGrid.background || getBackgroundColorOfFontIcon(binding.mecGrid) == 0) {//if Grid is currently not selected
                    binding.mecGrid.setBackgroundColor(highLightedBackgroundColor)
                    binding.mecList.setBackgroundColor(ContextCompat.getColor(binding.mecList.context, R.color.uidTransparent))
                    binding.productCatalogRecyclerView.layoutManager = GridLayoutManager(activity, 2)
                    binding.productCatalogRecyclerView.setItemAnimator(DefaultItemAnimator())
//                val Hdivider = DividerItemDecoration(binding.productCatalogRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL)
//                val Vdivider = DividerItemDecoration(binding.productCatalogRecyclerView.getContext(), DividerItemDecoration.VERTICAL)
//                binding.productCatalogRecyclerView.addItemDecoration(Hdivider)
//                binding.productCatalogRecyclerView.addItemDecoration(Vdivider)
                    adapter.catalogView = MECProductCatalogBaseAbstractAdapter.CatalogView.GRID
                    binding.productCatalogRecyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                    com.philips.platform.mec.analytics.MECAnalytics.tagProductList(productList, gridView)
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
                    com.philips.platform.mec.analytics.MECAnalytics.tagProductList(productList, listView)
                }
            }

            productList = mutableListOf()
            productReviewList = mutableListOf()

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


                    val text = String.format(context?.getResources()?.getText(R.string.mec_no_result).toString(), s)
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
                        binding.progressBar.visibility = View.VISIBLE
                        executeRequest()
                    }
                }
            })


            privacyTextView(binding.mecPrivacy)

            adapter = MECProductCatalogAdapter(productReviewList, this)

            binding.productCatalogRecyclerView.adapter = adapter

            binding.productCatalogRecyclerView.apply {
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }

            adapter.emptyView = binding.mecEmptyResult

            com.philips.platform.mec.analytics.MECAnalytics.trackPage(productCatalogue)
            com.philips.platform.mec.analytics.MECAnalytics.tagProductList(productList, listView)
            mRootView = binding.root


            if (arguments != null) {
                categorizedCtns = arguments?.getStringArrayList(MECConstant.CATEGORISED_PRODUCT_CTNS) as ArrayList<String>
                totalProductsTobeSearched = categorizedCtns.size
            }
            binding.mecCatalogProgress.mecProgressBar.visibility = View.VISIBLE
            executeRequest()
            ////////////// start of update cart and login if required
            if (isUserLoggedIn()) {
                GlobalScope.launch {
                    var mecManager: MECManager = MECManager()
                    MECDataHolder.INSTANCE.mecCartUpdateListener?.let { mecManager.getShoppingCartData(it) }
                }
            }
            ////////////// end of update cart and login if required
        }
        return binding.root
    }

    private fun getBackgroundColorOfFontIcon(label: Label): Int {
        val cd: ColorDrawable = label.background as ColorDrawable;
        val colorCode: Int = cd.color;
        return colorCode

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        setTitleAndBackButtonVisibility(R.string.mec_product_title, true)
        setCartIconVisibility(true)
    }

    private fun privacyTextView(view: TextView) {
        val spanTxt = SpannableStringBuilder(
                getString(R.string.mec_read))
        spanTxt.append(" ")
        spanTxt.append(getString(R.string.mec_privacy))
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                showPrivacyFragment()
                binding.progressBar.visibility = View.GONE
                // hideProgressBar()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
                ds.color = R.attr.uidHyperlinkDefaultPressedTextColor
            }
        }, spanTxt.length - getString(R.string.mec_privacy).length, spanTxt.length, 0)
        spanTxt.append(" ")
        spanTxt.append(getString(R.string.mec_more_info))
        binding.mecPrivacy.setHighlightColor(Color.TRANSPARENT)
        view.movementMethod = LinkMovementMethod.getInstance()
        view.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }

    private fun showPrivacyFragment() {
        val bundle = Bundle()
        bundle.putString(MECConstant.MEC_PRIVACY_URL, MECDataHolder.INSTANCE.getPrivacyUrl())
        val mecPrivacyFragment = MecPrivacyFragment()
        mecPrivacyFragment.arguments = bundle
        replaceFragment(mecPrivacyFragment, MecPrivacyFragment.TAG, true)
    }

    private fun isScrollDown(lay: LinearLayoutManager): Boolean {
        val visibleItemCount = lay.childCount
        val firstVisibleItemPosition = lay.findFirstVisibleItemPosition()
        return visibleItemCount + firstVisibleItemPosition >= lay.itemCount && firstVisibleItemPosition >= 0
    }

    open fun executeRequest() {
        if (MECDataHolder.INSTANCE.hybrisEnabled) {
            isCallOnProgress = true
            ecsProductViewModel.init(currentPage, pageSize)
        } else {
            hideProgressBar()
            binding.mecProductCatalogEmptyTextLabel.visibility = View.VISIBLE

        }
    }

    fun shouldFetchNextPage(): Boolean {

        if (!isPaginationSupported()) {
            return false
        }
        val lay = binding.productCatalogRecyclerView
                .layoutManager as LinearLayoutManager

        if (isScrollDown(lay)) {
            if (currentPage != totalPages - 1) {
                ++currentPage
                return true
            }
        }
        return false
    }

    override fun onStop() {
        super.onStop()
        binding.progressBar.visibility = View.GONE
    }

    override fun processError(mecError: MecError?, showDialog: Boolean) {
        super.processError(mecError, showDialog)
        binding.progressBar.visibility = View.GONE
    }

}