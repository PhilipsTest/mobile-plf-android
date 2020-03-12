package com.philips.cdp.di.mec.screens.shoppingCart


import android.view.View
import android.view.animation.TranslateAnimation
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.ImageLoader
import com.philips.cdp.di.mec.databinding.MecShoppingCartItemsBinding
import com.philips.cdp.di.mec.networkEssentials.NetworkImageLoader
import com.philips.cdp.di.mec.utils.MECutility
import com.philips.platform.uid.view.widget.UIPicker


class MECCartViewHolder(val binding: MecShoppingCartItemsBinding, var mecShoppingCartFragment: MECShoppingCartFragment) : RecyclerView.ViewHolder(binding.root) {


    private var mPopupWindow: UIPicker? = null
    private var animation: Boolean? = true
    fun bind(cartSummary: MECCartProductReview) {
        binding.cart = cartSummary
        val mImageLoader: ImageLoader
        mImageLoader = NetworkImageLoader.getInstance(mecShoppingCartFragment.context)
                .getImageLoader()
        binding.image.setImageUrl(cartSummary.entries.product.summary.imageURL,mImageLoader)
        bindCountView(binding.mecQuantityVal, cartSummary)
        if(animation == true) {
            rightAnimation()
            animation = false
        }
    }

    fun rightAnimation(){
        if(adapterPosition.equals(0)) {
            val animation = TranslateAnimation((binding.parentLayout.width - 300).toFloat(), 0f, 0f, 0f) // new TranslateAnimation(xFrom,xTo, yFrom,yTo)
            animation.duration = 800
            animation.repeatCount = 0
            animation.repeatMode = 2
            animation.setFillAfter(false)
            binding.parentLayout.startAnimation(animation)
        }
        Thread {
            Thread.sleep(800)
            leftAnimation()
        }.start()
    }

    fun leftAnimation(){
        if(adapterPosition.equals(0)) {
            val animation = TranslateAnimation(0.0f, 300.0f, 0.0f, 0.0f)
            animation.duration = 800
            animation.repeatCount = 0
            animation.repeatMode = 2
            animation.setFillAfter(false)
            binding.parentLayout.startAnimation(animation)
        }
    }

    private fun bindCountView(view: View, cartSummary: MECCartProductReview) {
        if (cartSummary.entries.product.stock.stockLevel > 1) {
            view.setOnClickListener { v ->
                val data = cartSummary
                val stockLevel = cartSummary.entries.product.stock.stockLevel
                /*if (stockLevel > 50) {
                stockLevel = 50
            }*/

                val countPopUp = MecCountDropDown(v, v.context, stockLevel, data.entries.quantity
                        , object : MecCountDropDown.CountUpdateListener {
                    override fun countUpdate(oldCount: Int, newCount: Int) {
                        if (newCount != oldCount) {
                            mecShoppingCartFragment.updateCartRequest(cartSummary.entries, newCount)
                        }

                    }
                })
                countPopUp.createPopUp(v, stockLevel)
                mPopupWindow = countPopUp.popUpWindow
                countPopUp.show()
            }

            if(cartSummary.entries.quantity > cartSummary.entries.product.stock.stockLevel) {
                mecShoppingCartFragment.disableButton()
            } else{
                mecShoppingCartFragment.enableButton()
            }

        }else if (!MECutility.isStockAvailable(cartSummary.entries.product.stock!!.stockLevelStatus, cartSummary.entries.product.stock!!.stockLevel)){
            mecShoppingCartFragment.disableButton()
        } else{
            mecShoppingCartFragment.enableButton()
        }
    }
}
