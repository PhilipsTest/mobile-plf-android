package com.philips.cdp.di.mec.screens.shoppingCart


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Canvas
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.philips.cdp.di.ecs.model.cart.ECSEntries
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart

import com.philips.cdp.di.mec.R
import com.philips.cdp.di.mec.databinding.MecShoppingCartFragmentBinding
import com.philips.cdp.di.mec.screens.MecBaseFragment
import com.philips.cdp.di.mec.utils.AlertListener
import com.philips.cdp.di.mec.utils.MECConstant
import com.philips.cdp.di.mec.utils.MECutility
import com.philips.platform.uid.view.widget.UIPicker
import kotlinx.android.synthetic.main.mec_main_activity.*


/**
 * A simple [Fragment] subclass.
 */
class MECShoppingCartFragment : MecBaseFragment(),AlertListener {

    internal var swipeController: SwipeController? = null

    private lateinit var binding: MecShoppingCartFragmentBinding
    private var itemPosition: Int = 0
    private var mPopupWindow: UIPicker? = null
    private lateinit var ecsShoppingCart: ECSShoppingCart
    lateinit var ecsShoppingCartViewModel: EcsShoppingCartViewModel
    private var productsAdapter: MECProductsAdapter? = null
    private lateinit var productReviewList: MutableList<MECCartProductReview>

    private val cartObserver: Observer<ECSShoppingCart> = object : Observer<ECSShoppingCart> {
        override fun onChanged(ecsShoppingCart: ECSShoppingCart?) {
            binding.shoppingCart = ecsShoppingCart

            if (ecsShoppingCart!!.entries.size != 0) {
                ecsShoppingCartViewModel.fetchProductReview(ecsShoppingCart!!.entries)
            }
            hideProgressBar()
        }
    }

    private val productReviewObserver: Observer<MutableList<MECCartProductReview>> = Observer { mecProductReviews ->
        productReviewList.clear()
        mecProductReviews?.let { productReviewList.addAll(it) }
        productsAdapter?.notifyDataSetChanged()
        if(productsAdapter!=null){
            MECProductsAdapter.CloseWindow(this.mPopupWindow).onStop()
        }
        hideProgressBar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = MecShoppingCartFragmentBinding.inflate(inflater, container, false)
        binding.fragment = this
        ecsShoppingCartViewModel = activity!!?.let { ViewModelProviders.of(it).get(EcsShoppingCartViewModel::class.java) }!!

        ecsShoppingCartViewModel.ecsShoppingCart.observe(this, cartObserver)
        ecsShoppingCartViewModel.ecsProductsReviewList.observe(this, productReviewObserver)

        val bundle = arguments
        ecsShoppingCart = bundle?.getSerializable(MECConstant.MEC_SHOPPING_CART) as ECSShoppingCart

        productReviewList = mutableListOf()

        productsAdapter = MECProductsAdapter(productReviewList,ecsShoppingCartViewModel,this)

        binding.mecCartSummaryRecyclerView.adapter = productsAdapter

        binding.mecCartSummaryRecyclerView.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        swipeController = SwipeController(binding.mecCartSummaryRecyclerView.context,object : SwipeControllerActions(){
            override fun onRightClicked(position: Int) {
                itemPosition = position
                showDialog()
            }
        })

        val itemTouchHelper = ItemTouchHelper(swipeController!!)
        itemTouchHelper.attachToRecyclerView(binding.mecCartSummaryRecyclerView)

        binding.mecCartSummaryRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                swipeController!!.onDraw(c)
            }
        })

        return binding.root
    }

    fun showDialog(){
        MECutility.showActionDialog(binding.enterVoucherLabel.context,getString(R.string.mec_remove_product),getString(R.string.mec_cancel),getString(R.string.mec_delete_item), getString(R.string.mec_delete_item_description),fragmentManager!!,this )
    }

    override fun onPositiveBtnClick() {
        updateCartRequest(ecsShoppingCart.entries.get(itemPosition),0)
    }

    override fun onNegativeBtnClick() {

    }

    override fun onResume() {
        super.onResume()
        setTitleAndBackButtonVisibility(R.string.mec_shopping_cart, true)
    }

    override fun onStart() {
        super.onStart()
        if (ecsShoppingCart!!.entries.size != 0) {
            createCustomProgressBar(container, MEDIUM)
            ecsShoppingCartViewModel.fetchProductReview(ecsShoppingCart!!.entries)
        }
        //executeRequest()
    }

     fun executeRequest() {
        //createCustomProgressBar(container, MEDIUM)
        ecsShoppingCartViewModel.getShoppingCart()
    }

    fun updateCartRequest(entries: ECSEntries, int: Int){
        createCustomProgressBar(container,MEDIUM)
        ecsShoppingCartViewModel.updateQuantity(entries,int)
    }
}
