package com.philips.cdp.di.mec.screens.shoppingCart


import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.cdp.di.ecs.model.cart.AppliedVoucherEntity
import com.philips.cdp.di.ecs.model.cart.ECSEntries
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart
import com.philips.cdp.di.mec.R
import com.philips.cdp.di.mec.common.ItemClickListener
import com.philips.cdp.di.mec.common.MECRequestType
import com.philips.cdp.di.mec.common.MecError
import com.philips.cdp.di.mec.databinding.MecShoppingCartFragmentBinding
import com.philips.cdp.di.mec.screens.MecBaseFragment
import com.philips.cdp.di.mec.screens.address.AddAddressFragment
import com.philips.cdp.di.mec.screens.address.AddressViewModel
import com.philips.cdp.di.mec.screens.address.MECDeliveryFragment
import com.philips.cdp.di.mec.utils.AlertListener
import com.philips.cdp.di.mec.utils.MECConstant
import com.philips.cdp.di.mec.utils.MECDataHolder
import com.philips.cdp.di.mec.utils.MECutility
import com.philips.platform.uid.view.widget.UIPicker
import com.philips.platform.uid.view.widget.ValidationEditText
import java.io.Serializable


/**
 * A simple [Fragment] subclass.
 */
class MECShoppingCartFragment : MecBaseFragment(), AlertListener, ItemClickListener {
    override fun getFragmentTag(): String {
        return "MECShoppingCartFragment"
    }

    private lateinit var addressViewModel: AddressViewModel


    private var mAddressList: List<ECSAddress>? = null

    var mRootView: View? = null

    companion object {
        val TAG = "MECShoppingCartFragment"
    }

    internal var swipeController: MECSwipeController? = null

    private lateinit var binding: MecShoppingCartFragmentBinding
    private var itemPosition: Int = 0
    private var mPopupWindow: UIPicker? = null
    private lateinit var shoppingCart: ECSShoppingCart
    lateinit var ecsShoppingCartViewModel: EcsShoppingCartViewModel
    private var productsAdapter: MECProductsAdapter? = null
    private var cartSummaryAdapter: MECCartSummaryAdapter? = null
    private var vouchersAdapter: MECVouchersAdapter? = null
    private lateinit var productReviewList: MutableList<MECCartProductReview>
    private lateinit var cartSummaryList: MutableList<MECCartSummary>
    private lateinit var voucherList: MutableList<AppliedVoucherEntity>
    private var voucherCode: String = ""
    private var removeVoucher: Boolean = true
    private var name: String = ""
    private var price: String = ""
    var validationEditText: ValidationEditText? = null
    val list: ArrayList<String>? = ArrayList()

    private val cartObserver: Observer<ECSShoppingCart> = Observer<ECSShoppingCart> { ecsShoppingCart ->
        binding.shoppingCart = ecsShoppingCart
        shoppingCart = ecsShoppingCart!!

        if (ecsShoppingCart!!.entries.size != 0) {
            binding.mecEmptyResult.visibility = View.GONE
            binding.mecParentLayout.visibility = View.VISIBLE
            ecsShoppingCartViewModel.fetchProductReview(ecsShoppingCart.entries)
        } else if (ecsShoppingCart.entries.size == 0) {
            binding.mecEmptyResult.visibility = View.VISIBLE
            dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
            binding.mecParentLayout.visibility = View.GONE
        }

        voucherList.clear()
        if (ecsShoppingCart.appliedVouchers.size > 0) {
            ecsShoppingCart.appliedVouchers?.let { voucherList.addAll(it) }
        }
        vouchersAdapter?.notifyDataSetChanged()

        if (MECDataHolder.INSTANCE.voucherEnabled && !(MECDataHolder.INSTANCE.voucherCode.isEmpty()) && !(MECDataHolder.INSTANCE.voucherCode.equals("invalid_code"))) {
            for (i in 0..ecsShoppingCart.appliedVouchers.size - 1) {
                list?.add(ecsShoppingCart.appliedVouchers.get(i).voucherCode!!)
                break
            }
            if (!list!!.contains(MECDataHolder.INSTANCE.voucherCode)) {
                ecsShoppingCartViewModel.addVoucher(MECDataHolder.INSTANCE.voucherCode, MECRequestType.MEC_APPLY_VOUCHER_SILENT)
                MECDataHolder.INSTANCE.voucherCode = ""
            }
        }

        if(ecsShoppingCart!!.appliedVouchers.size > 0){
            binding.mecAcceptedCode.visibility = View.VISIBLE
            binding.mecAcceptedCodeRecyclerView.visibility = View.VISIBLE
        } else {
            binding.mecAcceptedCode.visibility = View.GONE
            binding.mecAcceptedCodeRecyclerView.visibility = View.GONE
        }

        vouchersAdapter?.notifyDataSetChanged()
        cartSummaryAdapter?.notifyDataSetChanged()
        productsAdapter?.notifyDataSetChanged()


        if (ecsShoppingCart != null) {
            val quantity = MECutility.getQuantity(ecsShoppingCart)
            updateCount(quantity)
        }
        if(productsAdapter!!.itemCount>0 ) {
            dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        }
    }


    private val productReviewObserver: Observer<MutableList<MECCartProductReview>> = Observer { mecProductReviews ->
        productReviewList.clear()
        cartSummaryList.clear()
        mecProductReviews?.let { productReviewList.addAll(it) }

        for (i in 0..shoppingCart.entries.size - 1) {
            name = shoppingCart.entries.get(i).quantity.toString() + "x " + shoppingCart.entries.get(i).product.summary.productTitle
            price = shoppingCart.entries.get(i).totalPrice.formattedValue
            cartSummaryList.add(MECCartSummary(name, price))
        }

        if(shoppingCart.deliveryCost!=null){
            name = getString(R.string.mec_delivery_cost)
            price = shoppingCart.deliveryCost.formattedValue
            cartSummaryList.add(MECCartSummary(name, price))
        }

        for (i in 0..shoppingCart.appliedVouchers.size - 1) {
            if(shoppingCart.appliedVouchers.get(i).name==null) {
                name = " "
            } else {
                name = shoppingCart.appliedVouchers.get(i).name
            }
            price = "-" + shoppingCart.appliedVouchers?.get(i)?.appliedValue?.formattedValue
            cartSummaryList.add(MECCartSummary(name, price))
        }

        if(shoppingCart.appliedOrderPromotions.size>0) {
            for (i in 0..shoppingCart.appliedOrderPromotions.size - 1) {
                if(shoppingCart.appliedOrderPromotions.get(i).promotion.description == null) {
                    name = " "
                } else {
                    name = shoppingCart.appliedOrderPromotions.get(i).promotion.description
                }
                price = "-" + shoppingCart.appliedOrderPromotions.get(i).promotion.promotionDiscount.formattedValue
                cartSummaryList.add(MECCartSummary(name, price))
            }
        }


        /*for (i in 0..shoppingCart.appliedProductPromotions.size - 1) {
            name = shoppingCart.appliedProductPromotions.get(i).promotion.description
            price = "-" + shoppingCart.appliedProductPromotions.get(i).promotion.promotionDiscount.formattedValue
            cartSummaryList.add(MECCartSummary(name, price))
        }*/

        productsAdapter?.notifyDataSetChanged()
        cartSummaryAdapter?.notifyDataSetChanged()
        if (productsAdapter != null) {
            //MECProductsAdapter.CloseWindow(this.mPopupWindow).onStop()
        }
        if(productsAdapter!!.itemCount>0 ) {
            dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        }
    }




    private val addressObserver: Observer<List<ECSAddress>> = Observer(fun(addressList: List<ECSAddress>?) {

        mAddressList = addressList
        if(productsAdapter!!.itemCount>0 ) {
            dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        }
        if (mAddressList.isNullOrEmpty()) {
            replaceFragment(AddAddressFragment(), AddAddressFragment().getFragmentTag(), true)

        } else {
            gotoDeliveryAddress(mAddressList)
        }

    })

    private fun moveDefaultAddressToTopOfTheList(addressList: List<ECSAddress>, defaultAddressID: String) {
        val mutableAddressList = addressList.toMutableList()

        val iterator = mutableAddressList.iterator()

        while (iterator.hasNext()) {

            val ecsAddress = iterator.next()

            if (ecsAddress.id.equals(defaultAddressID, true)) {
                mutableAddressList.remove(ecsAddress)
                mutableAddressList.add(0, ecsAddress)
            }
        }
    }

    private fun gotoDeliveryAddress(addressList: List<ECSAddress>?) {
        var deliveryFragment = MECDeliveryFragment()
        var bundle = Bundle()
        bundle.putSerializable(MECConstant.KEY_ECS_ADDRESSES, addressList as Serializable)
        bundle.putSerializable(MECConstant.KEY_ECS_SHOPPING_CART, shoppingCart)
        deliveryFragment.arguments = bundle
        replaceFragment(deliveryFragment, deliveryFragment.getFragmentTag(), true)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setCartIconVisibility(false)
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

            swipeController = MECSwipeController(binding.mecCartSummaryRecyclerView.context, object : SwipeControllerActions() {
                override fun onRightClicked(position: Int) {
                    itemPosition = position
                    removeVoucher= false
                    showDialog()
                }

                override fun onLeftClicked(position: Int) {
                    itemPosition = position
                    removeVoucher= false
                    showDialog()
                }
            })

            val itemTouchHelper = ItemTouchHelper(swipeController!!)
            itemTouchHelper.attachToRecyclerView(binding.mecCartSummaryRecyclerView)

            binding.mecCartSummaryRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                    if(productsAdapter!!.itemCount>0 ) {
                            swipeController!!.drawButtons(c, parent.findViewHolderForAdapterPosition(0)!!)
                    }
                    swipeController!!.onDraw(c)
                }
            })
            mRootView = binding.root

        }
        return binding.root
    }

    fun showDialog() {
        if (removeVoucher) {
            MECutility.showActionDialog(binding.mecVoucherEditText.context, getString(R.string.mec_remove_voucher), getString(R.string.mec_cancel), getString(R.string.mec_delete_voucher), getString(R.string.mec_delete_voucher_description), fragmentManager!!, this)
        } else {
            MECutility.showActionDialog(binding.mecVoucherEditText.context, getString(R.string.mec_remove_product), getString(R.string.mec_cancel), getString(R.string.mec_delete_item), getString(R.string.mec_delete_item_description), fragmentManager!!, this)
        }
    }

    override fun onPositiveBtnClick() {
        if (removeVoucher) {
            showProgressBar(binding.mecProgress.mecProgressBarContainer)
            removeVoucher = false
            ecsShoppingCartViewModel.removeVoucher(vouchersAdapter?.getVoucher()?.voucherCode.toString())
        } else {
            updateCartRequest(shoppingCart.entries.get(itemPosition), 0)
        }
    }

    override fun onNegativeBtnClick() {

    }

    override fun onResume() {
        super.onResume()
        setTitleAndBackButtonVisibility(R.string.mec_shopping_cart, true)
        setCartIconVisibility(false)
    }

    override fun onStart() {
        super.onStart()
        executeRequest()
    }

    fun executeRequest() {
        showProgressBar(binding.mecProgress.mecProgressBarContainer)

        //createCustomProgressBar(container, MEDIUM)
        ecsShoppingCartViewModel.getShoppingCart()
    }

    fun updateCartRequest(entries: ECSEntries, int: Int) {
        showProgressBar(binding.mecProgress.mecProgressBarContainer)
        ecsShoppingCartViewModel.updateQuantity(entries, int)
    }

    fun afterUserNameChange(s: CharSequence) {
        binding.mecVoucherBtn.isEnabled = true
        voucherCode = s.toString()
    }

    fun onClickAddVoucher() {
        showProgressBar(binding.mecProgress.mecProgressBarContainer)
        ecsShoppingCartViewModel.addVoucher(voucherCode, MECRequestType.MEC_APPLY_VOUCHER)
        binding.mecVoucherEditText.text?.clear()
    }

    fun onCheckOutClick() {
        if (MECDataHolder.INSTANCE.maxCartCount != 0 && shoppingCart.deliveryItemsQuantity > MECDataHolder.INSTANCE.maxCartCount) {
            fragmentManager?.let { context?.let { it1 -> MECutility.showErrorDialog(it1, it, getString(R.string.mec_ok), getString(R.string.mec_exceed_cart_limit), getString(R.string.mec_cannot_add) + MECDataHolder.INSTANCE.maxCartCount + getString(R.string.mec_product_in_cart)) } }
        } else {
            showProgressBar(binding.mecProgress.mecProgressBarContainer)
            addressViewModel.fetchAddresses()
        }
    }

    fun gotoProductCatalog() {
        showProductCatalogFragment(TAG)
    }

    override fun onItemClick(item: Any) {
        removeVoucher = true
        showDialog()
    }

    fun disableButton() {
        binding.mecContinueCheckoutBtn.isEnabled = false
    }

    fun enableButton() {
        binding.mecContinueCheckoutBtn.isEnabled = true
    }

    override fun onStop() {
        super.onStop()
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
    }


    override fun processError(mecError: MecError?, bool: Boolean) {
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        MECDataHolder.INSTANCE.voucherCode = "invalid_code"
        if (mecError!!.mECRequestType == MECRequestType.MEC_APPLY_VOUCHER) {
            super.processError(mecError, false)
            if (mecError!!.exception!!.message.toString().contentEquals(getString(R.string.mec_invalid_voucher_error))) {
                validationEditText = null
                binding.mecVoucherEditText.startAnimation(addressViewModel.shakeError())
                binding.llAddVoucher.showError()
                validationEditText?.requestFocus()
            }
        }else if(mecError?.mECRequestType == MECRequestType.MEC_FETCH_USER_PROFILE){
            gotoDeliveryAddress(mAddressList)
        }else{
            super.processError(mecError, true)
        }
    }
}
