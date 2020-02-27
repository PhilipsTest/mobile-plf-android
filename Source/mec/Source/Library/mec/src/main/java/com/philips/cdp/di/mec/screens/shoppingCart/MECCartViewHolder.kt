package com.philips.cdp.di.mec.screens.shoppingCart


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.ImageLoader
import com.philips.cdp.di.mec.databinding.MecShoppingCartItemsBinding
import com.philips.cdp.di.mec.networkEssentials.NetworkImageLoader
import com.philips.cdp.di.mec.utils.MECutility
import com.philips.platform.uid.view.widget.UIPicker
import android.R
import android.view.animation.*


class MECCartViewHolder(val binding: MecShoppingCartItemsBinding, var mecShoppingCartFragment: MECShoppingCartFragment) : RecyclerView.ViewHolder(binding.root) {

    private var mPopupWindow: UIPicker? = null
    fun bind(cartSummary: MECCartProductReview) {
        binding.cart = cartSummary
        val mImageLoader: ImageLoader
        mImageLoader = NetworkImageLoader.getInstance(mecShoppingCartFragment.context)
                .getImageLoader()
        binding.image.setImageUrl(cartSummary.entries.product.summary.imageURL,mImageLoader)
        bindCountView(binding.mecQuantityVal, cartSummary)
        if(adapterPosition.equals(0)) {
            //binding.parentLayout.startAnimation(shakeError())
           // binding.parentLayout.startAnimation(AnimationUtils.loadAnimation(binding.image.context,R.anim.slide_in_left))
        }

    }

    fun shakeError(): TranslateAnimation {
        /*val inFromRight = TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f)
        inFromRight.duration = 500
        inFromRight.interpolator = AccelerateInterpolator()
        return inFromRight*/
        val shake = TranslateAnimation(200f, 0f, 0f, 200f)
        shake.duration = 500
        shake.interpolator = AccelerateInterpolator(4f)
        return shake
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
            }

        }else if (!MECutility.isStockAvailable(cartSummary.entries.product.stock!!.stockLevelStatus, cartSummary.entries.product.stock!!.stockLevel)){
            mecShoppingCartFragment.disableButton()
        }
    }
}
