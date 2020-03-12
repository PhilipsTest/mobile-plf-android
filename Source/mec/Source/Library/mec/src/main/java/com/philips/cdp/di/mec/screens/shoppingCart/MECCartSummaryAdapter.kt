package com.philips.cdp.di.mec.screens.shoppingCart


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.cdp.di.mec.databinding.MecCartSummaryLayoutBinding


class MECCartSummaryAdapter(private val mecCart: MutableList<MECCartSummary>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MECCartSummaryViewHolder(MecCartSummaryLayoutBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return mecCart.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cartSummary = mecCart.get(position)
        val viewHolder = holder as MECCartSummaryViewHolder
        viewHolder.bind(cartSummary)
    }

}

