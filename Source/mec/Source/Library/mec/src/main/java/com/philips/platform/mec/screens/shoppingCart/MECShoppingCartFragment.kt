/* Copyright (c) Koninklijke Philips N.V., 2020
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.mec.screens.shoppingCart


import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.ecs.microService.model.cart.ECSItem
import com.philips.platform.ecs.microService.model.cart.ECSShoppingCart
import com.philips.platform.ecs.microService.model.cart.Voucher
import com.philips.platform.ecs.model.address.ECSAddress
import com.philips.platform.mec.R
import com.philips.platform.mec.analytics.MECAnalyticPageNames.shoppingCartPage
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.analytics.MECAnalyticsConstant.continueShoppingSelected
import com.philips.platform.mec.analytics.MECAnalyticsConstant.scCheckout
import com.philips.platform.mec.analytics.MECAnalyticsConstant.scView
import com.philips.platform.mec.analytics.MECAnalyticsConstant.specialEvents
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.databinding.MecShoppingCartFragmentBinding
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.address.AddAddressFragment
import com.philips.platform.mec.screens.address.AddressViewModel
import com.philips.platform.mec.screens.address.MECDeliveryFragment
import com.philips.platform.mec.utils.AlertListener
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.uid.view.widget.UIPicker
import com.philips.platform.uid.view.widget.ValidationEditText
import kotlinx.android.synthetic.main.mec_product_catalog_item_grid.*
import java.io.Serializable
import java.util.concurrent.atomic.AtomicBoolean


/**
 * A simple [Fragment] subclass.
 */
class MECShoppingCartFragment : MecBaseFragment(), AlertListener, ItemClickListener {
    override fun getFragmentTag(): String {
        return TAG
    }

    private lateinit var addressViewModel: AddressViewModel


    private var mAddressList: List<ECSAddress>? = null

    var mRootView: View? = null

    companion object {
        val TAG = "MECShoppingCartFragment"
    }

    private lateinit var binding: MecShoppingCartFragmentBinding
    private var itemPosition: Int = 0
    private lateinit var shoppingCart: ECSShoppingCart
    lateinit var ecsShoppingCartViewModel: EcsShoppingCartViewModel
    private var productsAdapter: MECProductsAdapter? = null
    private var cartSummaryAdapter: MECCartSummaryAdapter? = null
    private var vouchersAdapter: MECVouchersAdapter? = null
    private lateinit var productReviewList: MutableList<MECCartProductReview>
    private lateinit var cartSummaryList: MutableList<MECCartSummary>
    private lateinit var voucherList: MutableList<Voucher>
    private var voucherCode: String = ""
    private var removeVoucher: Boolean = true
    var validationEditText: ValidationEditText? = null
    val list: ArrayList<String> = ArrayList()
    var mAtomicBoolean: AtomicBoolean = AtomicBoolean(true) // default false

    private val cartObserver: Observer<ECSShoppingCart> = Observer { ecsShoppingCart ->
        binding.shoppingCart = ecsShoppingCart
        shoppingCart = ecsShoppingCart

        if (ecsShoppingCart.data?.attributes?.items?.size != 0) {
            binding.mecEmptyResult.visibility = View.GONE
            binding.mecParentLayout.visibility = View.VISIBLE
            ecsShoppingCartViewModel.fetchProductReview(ecsShoppingCart.data?.attributes?.items as MutableList<ECSItem>)
        } else {
            binding.mecEmptyResult.visibility = View.VISIBLE
            binding.mecParentLayout.visibility = View.GONE
            dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        }

        voucherList.clear()
        if (ecsShoppingCart.data?.attributes?.appliedVouchers?.size ?: 0 > 0) {
            ecsShoppingCart.data?.attributes?.appliedVouchers?.let { voucherList.addAll(it) }
        }
        vouchersAdapter?.notifyDataSetChanged()

        if (MECDataHolder.INSTANCE.voucherEnabled && MECDataHolder.INSTANCE.voucherCode?.isEmpty() == false && !(MECDataHolder.INSTANCE.voucherCode.equals("invalid_code"))) {
            for (i in 0 until (ecsShoppingCart.data?.attributes?.appliedVouchers?.size ?: 0)) {
                ecsShoppingCart.data?.attributes?.appliedVouchers?.get(i)?.id?.let { list.add(it) }
                break
            }

            MECDataHolder.INSTANCE.voucherCode?.let {
                if (!list.contains(it)) {
                    ecsShoppingCartViewModel.addVoucher(it, MECRequestType.MEC_APPLY_VOUCHER_SILENT)
                    MECDataHolder.INSTANCE.voucherCode = ""
                }
            }

        }

        if (ecsShoppingCart.data?.attributes?.appliedVouchers?.size ?: 0 > 0) {
            binding.mecAcceptedCode.visibility = View.VISIBLE
            binding.mecAcceptedCodeRecyclerView.visibility = View.VISIBLE
        } else {
            binding.mecAcceptedCode.visibility = View.GONE
            binding.mecAcceptedCodeRecyclerView.visibility = View.GONE
        }

        vouchersAdapter?.notifyDataSetChanged()
        cartSummaryAdapter?.notifyDataSetChanged()
        productsAdapter?.notifyDataSetChanged()


        val quantity = MECutility.getQuantity(ecsShoppingCart)
        updateCount(quantity)
        if (productsAdapter?.itemCount ?: 0 > 0) {
            dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        }

        if (mAtomicBoolean.compareAndSet(true, false)) { // scView should tag only once upon shopping cart screen visit
            val actionMap = HashMap<String, String>()
            actionMap.put(specialEvents, scView)
            //TODO
            //MECAnalytics.tagActionsWithOrderProductsInfo(actionMap, binding.shoppingCart?.entries)
        }
    }


    private val productReviewObserver = Observer<MutableList<MECCartProductReview>> { mecProductReviews ->
        productReviewList.clear()
        cartSummaryList.clear()
        binding.mecVoucherEditText.clearFocus()
        if (binding.llAddVoucher.isShowingError)
            binding.llAddVoucher.hideError()
        mecProductReviews?.let { productReviewList.addAll(it) }

        val items = shoppingCart.data?.attributes?.items
        items?.forEach { item ->
            val name = (item.quantity ?: 0).toString() + "x " + item.title
            val price = item.totalPrice?.formattedValue ?: ""
            cartSummaryList.add(MECCartSummary(name, price))
        }

        val appliedPromotions = shoppingCart.data?.attributes?.promotions?.appliedPromotions
        appliedPromotions?.forEach { appliedPromotion ->
            val name = appliedPromotion.code ?: ""
            val price = "Demo"
            cartSummaryList.add(MECCartSummary(name, price))
        }

        val appliedVouchers = shoppingCart.data?.attributes?.appliedVouchers
        appliedVouchers?.forEach { voucher ->
            val name = voucher.name ?: ""
            val price = voucher.value?.formattedValue ?: ""
            cartSummaryList.add(MECCartSummary(name, price))
        }

        val deliveryCost = shoppingCart.data?.attributes?.pricing?.delivery?.formattedValue
        deliveryCost?.let {
            val name = getString(R.string.mec_shipping_cost)
            cartSummaryList.add(MECCartSummary(name, it))
        }
        productsAdapter?.notifyDataSetChanged()
        cartSummaryAdapter?.notifyDataSetChanged()

        if (productsAdapter?.itemCount ?: 0 > 0) {
            dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
            binding.mecVat.visibility = View.VISIBLE
            binding.mecTotalPrice.visibility = View.VISIBLE
            binding.mecTotalProducts.visibility = View.VISIBLE
            binding.mecContinueShoppingBtn.visibility = View.VISIBLE
            binding.mecContinueCheckoutBtn.visibility = View.VISIBLE
        }
    }


    private val addressObserver: Observer<List<ECSAddress>> = Observer(fun(addressList: List<ECSAddress>?) {

        mAddressList = addressList
        if (productsAdapter?.itemCount ?: 0 > 0) {
            dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        }
        if (mAddressList.isNullOrEmpty()) {
            gotoAddAddressFragment()

        } else {
            gotoDeliveryAddress(mAddressList)
        }

    })

    private fun gotoAddAddressFragment() {

        val addAddressFragment = AddAddressFragment()
        val bundle = Bundle()
        bundle.putParcelable(MECConstant.KEY_ECS_SHOPPING_CART, shoppingCart)
        addAddressFragment.arguments = bundle
        replaceFragment(addAddressFragment, addAddressFragment.getFragmentTag(), true)
    }

    private fun gotoDeliveryAddress(addressList: List<ECSAddress>?) {
        val deliveryFragment = MECDeliveryFragment()
        val bundle = Bundle()
        bundle.putSerializable(MECConstant.KEY_ECS_ADDRESSES, addressList as Serializable)
        bundle.putParcelable(MECConstant.KEY_ECS_SHOPPING_CART, shoppingCart)
        deliveryFragment.arguments = bundle
        replaceFragment(deliveryFragment, deliveryFragment.getFragmentTag(), true)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mAtomicBoolean.set(true)
        if (null == mRootView) {

            binding = MecShoppingCartFragmentBinding.inflate(inflater, container, false)
            binding.fragment = this
            binding.mecDataHolder = MECDataHolder.INSTANCE
            ecsShoppingCartViewModel = ViewModelProviders.of(this).get(EcsShoppingCartViewModel::class.java)
            addressViewModel = ViewModelProviders.of(this).get(AddressViewModel::class.java)
            ecsShoppingCartViewModel.ecsShoppingCart.observe(this, cartObserver)
            ecsShoppingCartViewModel.ecsProductsReviewList.observe(this, productReviewObserver)
            addressViewModel.ecsAddresses.observe(this, addressObserver)
            ecsShoppingCartViewModel.mecError.observe(this, this)
            addressViewModel.mecError.observe(this, this)

            productReviewList = mutableListOf()
            voucherList = mutableListOf()
            cartSummaryList = mutableListOf()

            productsAdapter = MECProductsAdapter(productReviewList, this)
            cartSummaryAdapter = MECCartSummaryAdapter(cartSummaryList)
            vouchersAdapter = MECVouchersAdapter(voucherList, this)

            binding.mecCartSummaryRecyclerView.adapter = productsAdapter
            binding.mecAcceptedCodeRecyclerView.adapter = vouchersAdapter
            binding.mecPriceSummaryRecyclerView.adapter = cartSummaryAdapter

            val swipeController = MECSwipeController(binding.mecCartSummaryRecyclerView.context, object : SwipeControllerActions() {
                override fun onRightClicked(position: Int) {
                    itemPosition = position
                    removeVoucher = false
                    showDialog()
                }

                override fun onLeftClicked(position: Int) {
                    itemPosition = position
                    removeVoucher = false
                    showDialog()
                }
            })

            val itemTouchHelper = ItemTouchHelper(swipeController)
            itemTouchHelper.attachToRecyclerView(binding.mecCartSummaryRecyclerView)

            binding.mecCartSummaryRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                    if (productsAdapter?.itemCount ?: 0 > 0) {
                        parent.findViewHolderForAdapterPosition(0)?.let { swipeController.drawButtons(c, it) }
                    }
                    swipeController.onDraw(c)
                }
            })
            mRootView = binding.root

        }
        binding.mecVoucherEditText.clearFocus()
        if (binding.llAddVoucher.isShowingError)
            binding.llAddVoucher.hideError()
        return binding.root
    }

    fun showDialog() {
        if (removeVoucher) {
            activity?.supportFragmentManager?.let { MECutility.showActionDialog(binding.mecVoucherEditText.context, R.string.mec_delete, R.string.mec_cancel, R.string.mec_shopping_cart_title, R.string.mec_delete_voucher_confirmation_title, it, this) }
        } else {
            activity?.supportFragmentManager?.let { MECutility.showActionDialog(binding.mecVoucherEditText.context, R.string.mec_delete, R.string.mec_cancel, R.string.mec_shopping_cart_title, R.string.mec_delete_product_confirmation_title, it, this) }
        }
    }

    override fun onPositiveBtnClick() {
        if (removeVoucher) {
            showProgressBar(binding.mecProgress.mecProgressBarContainer)
            removeVoucher = false
            ecsShoppingCartViewModel.removeVoucher(vouchersAdapter?.getVoucher()?.id.toString())
        } else {
            if (shoppingCart.data?.attributes?.items?.size ?: 0 > itemPosition) { // condition to be added to avoid ArrayIndex out of bound in case the
                // delete button is clicked multiple times immediately
                shoppingCart.data?.attributes?.items?.get(itemPosition)?.let { updateCartRequest(it, 0) }
            }
        }
    }

    override fun onNegativeBtnClick() {

    }

    override fun onStart() {
        super.onStart()
        setCartIconVisibility(false)
        setTitleAndBackButtonVisibility(R.string.mec_shopping_cart_title, true)
        MECAnalytics.trackPage(shoppingCartPage)
        executeRequest()
    }

    fun executeRequest() {
        showProgressBar(binding.mecProgress.mecProgressBarContainer)
        ecsShoppingCartViewModel.getShoppingCart()
    }

    fun updateCartRequest(ecsItem: ECSItem, int: Int) {
        showProgressBar(binding.mecProgress.mecProgressBarContainer)
        ecsShoppingCartViewModel.updateQuantity(ecsItem, int)
    }

    fun afterUserNameChange(s: CharSequence) {
        voucherCode = s.toString()
    }

    fun onClickAddVoucher() {
        if (voucherCode.isNotEmpty()) {
            showProgressBar(binding.mecProgress.mecProgressBarContainer)
            ecsShoppingCartViewModel.addVoucher(voucherCode, MECRequestType.MEC_APPLY_VOUCHER)
        }
        binding.mecVoucherEditText.text?.clear()
    }

    fun onCheckOutClick() {
        val actionMap = HashMap<String, String>()
        actionMap.put(specialEvents, scCheckout)
        //TODO tagging
        //MECAnalytics.tagActionsWithOrderProductsInfo(actionMap, binding.shoppingCart?.entries)
        if (MECDataHolder.INSTANCE.maxCartCount != 0 && shoppingCart.data?.attributes?.deliveryUnits ?: 0 > MECDataHolder.INSTANCE.maxCartCount) {
            activity?.supportFragmentManager?.let { context?.let { it1 -> MECutility.showErrorDialog(it1, it, getString(R.string.mec_ok), getString(R.string.mec_shopping_cart_title), String.format(getString(R.string.mec_cart_count_exceed_message), MECDataHolder.INSTANCE.maxCartCount)) } }
        } else {
            showProgressBar(binding.mecProgress.mecProgressBarContainer)
            addressViewModel.fetchAddresses()
        }
    }

    fun gotoProductCatalog() {
        val actionMap = HashMap<String, String>()
        actionMap.put(specialEvents, continueShoppingSelected)
        //TODO tagging
        //MECAnalytics.tagActionsWithOrderProductsInfo(actionMap, binding.shoppingCart?.entries)
        showProductCatalogFragment(TAG)
    }

    override fun onItemClick(item: Any) {
        removeVoucher = true
        showDialog()
    }

    fun disableCheckOutButton() {
        binding.mecContinueCheckoutBtn.isEnabled = false
    }

    fun enableCheckoutButton() {
        binding.mecContinueCheckoutBtn.isEnabled = true
    }

    override fun onStop() {
        super.onStop()
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
    }

    override fun onDestroy() {
        super.onDestroy()
        resetPaymentMethods()
    }

    private fun resetPaymentMethods() {
        MECDataHolder.INSTANCE.PAYMENT_HOLDER.payments.clear() //Reset payment billing address cache
        MECDataHolder.INSTANCE.PAYMENT_HOLDER.isPaymentDownloaded = false
    }


    override fun processError(mecError: MecError?, showDialog: Boolean) {
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        when (mecError?.mECRequestType) {
            MECRequestType.MEC_APPLY_VOUCHER -> {
                super.processError(mecError, false)
                validationEditText = null
                binding.mecVoucherEditText.startAnimation(addressViewModel.shakeError())
                binding.llAddVoucher.setErrorMessage(context?.let { MECutility.getErrorString(mecError, it) })
                binding.llAddVoucher.showError()
                validationEditText?.requestFocus()
            }
            MECRequestType.MEC_FETCH_USER_PROFILE -> {
                gotoDeliveryAddress(mAddressList)
            }
            else -> {
                super.processError(mecError, true)
            }
        }
    }
}
