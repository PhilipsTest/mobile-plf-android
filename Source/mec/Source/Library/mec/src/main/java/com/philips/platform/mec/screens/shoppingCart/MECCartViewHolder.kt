/* Copyright (c) Koninklijke Philips N.V., 2020
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.mec.screens.shoppingCart


import android.view.View
import android.view.animation.TranslateAnimation
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.ImageLoader
import com.philips.platform.mec.databinding.MecShoppingCartItemsBinding
import com.philips.platform.mec.networkEssentials.NetworkImageLoader
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.uid.view.widget.UIPicker


class MECCartViewHolder(val binding: MecShoppingCartItemsBinding, var mecShoppingCartFragment: MECShoppingCartFragment) : RecyclerView.ViewHolder(binding.root) {


    private var mPopupWindow: UIPicker? = null
    private var animation: Boolean? = true
    fun bind(cartSummary: MECCartProductReview) {
        binding.cart = cartSummary
        val mImageLoader: ImageLoader = NetworkImageLoader.getInstance(mecShoppingCartFragment.context)
                .imageLoader
        binding.image.setImageUrl(cartSummary.ecsItem.image,mImageLoader)
        bindCountView(binding.mecQuantityVal, cartSummary)
        if(animation == true) {
            rightAnimation()
            animation = false
        }
    }

    private fun rightAnimation(){
        if(adapterPosition == 0) {
            val animation = TranslateAnimation((binding.parentLayout.width - 300).toFloat(), 0f, 0f, 0f) // new TranslateAnimation(xFrom,xTo, yFrom,yTo)
            animation.duration = 800
            animation.repeatCount = 0
            animation.repeatMode = 2
            animation.fillAfter = false
            binding.parentLayout.startAnimation(animation)
        }
        Thread {
            Thread.sleep(800)
            leftAnimation()
        }.start()
    }

    private fun leftAnimation(){
        if(adapterPosition == 0) {
            val animation = TranslateAnimation(0.0f, 300.0f, 0.0f, 0.0f)
            animation.duration = 800
            animation.repeatCount = 0
            animation.repeatMode = 2
            animation.fillAfter = false
//            binding.parentLayout.startAnimation(animation)
        }
    }

    private fun bindCountView(view: View, cartSummary: MECCartProductReview) {
        if (cartSummary.ecsItem.availability?.quantity ?:0 > 1) {
            view.setOnClickListener { v ->
                val stockLevel = cartSummary.ecsItem.availability?.quantity ?:0
                /*if (stockLevel > 50) {
                stockLevel = 50
            }*/

                val countPopUp = MecCountDropDown(v, v.context, stockLevel, cartSummary.ecsItem.quantity ?:0
                        , object : MecCountDropDown.CountUpdateListener {
                    override fun countUpdate(oldCount: Int, newCount: Int) {
                        if (newCount != oldCount) {
                            mecShoppingCartFragment.updateCartRequest(cartSummary.ecsItem, newCount)
                        }

                    }
                })
                countPopUp.createPopUp(v, stockLevel)
                mPopupWindow = countPopUp.popUpWindow
                countPopUp.show()
            }

            if(cartSummary.ecsItem.quantity ?:0 > cartSummary.ecsItem.availability?.quantity ?:0) {
                mecShoppingCartFragment.disableCheckOutButton()
            } else{
                mecShoppingCartFragment.enableCheckoutButton()
            }

        }else if (!MECutility.isStockAvailable(cartSummary.ecsItem.availability?.status, cartSummary.ecsItem.availability?.quantity ?:0)){
            mecShoppingCartFragment.disableCheckOutButton()
        } else{
            mecShoppingCartFragment.enableCheckoutButton()
        }
    }
}
