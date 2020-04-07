/* Copyright (c) Koninklijke Philips N.V., 2020
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.mec.screens.shoppingCart


import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.mec.databinding.MecCartSummaryLayoutBinding

class MECCartSummaryViewHolder(val binding: MecCartSummaryLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(cartSummary: MECCartSummary) {
        binding.cart = cartSummary
    }
}
