/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.mec.R
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.databinding.MecAddressCardBinding
import com.philips.platform.mec.databinding.MecAddressCreateCardBinding
import com.philips.platform.mec.utils.MECutility
import kotlinx.android.synthetic.main.mec_address_card.view.*


class AddressBottomSheetRecyclerAdapter(private val mecAddresses: MECAddresses, val defaultAddressId: String, val itemClickListener: ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val totalItem = mecAddresses.ecsAddresses.size + 1

    private val CREATE_ADDRESS = "CREATE_ADDRESS"
    private var mSelectedItem = 0
    var mSelectedAddress = mecAddresses.ecsAddresses[0]


    fun setDefaultSelectedAddressAndPosition() {

        for (x in 0 until totalItem - 1) {

            if (mecAddresses.ecsAddresses[x].id.equals(defaultAddressId, true)) {
                mSelectedItem = x
                mSelectedAddress = mecAddresses.ecsAddresses[x]
            }
        }
    }


    private val VIEW_TYPE_FOOTER = 1
    lateinit var binding: ViewDataBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        if (viewType == VIEW_TYPE_FOOTER) {
            binding = MecAddressCreateCardBinding.inflate(inflater)

            binding.root.setOnClickListener {
                itemClickListener.onItemClick(CREATE_ADDRESS)
            }

            return AddressBottomSheetFooterViewHolder(binding)
        } else {
            binding = MecAddressCardBinding.inflate(inflater)
            return AddressBottomSheetViewHolder(binding)
        }

    }

    override fun getItemCount(): Int {
        return totalItem
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        if (viewHolder is AddressBottomSheetViewHolder) {
            viewHolder.bind(mecAddresses.ecsAddresses[position])

            if (position == mSelectedItem) {
                viewHolder.binding.root.tv_name.setTextColor(MECutility.getAttributeColor(binding.root.context, R.attr.uidTextBoxDefaultValidatedTextColor))
                viewHolder.binding.root.tv_address_text.setTextColor(MECutility.getAttributeColor(binding.root.context, R.attr.uidTextBoxDefaultValidatedTextColor))
                viewHolder.binding.root.ll_rl_address.setBackgroundResource(R.drawable.address_selector)
            } else {
                viewHolder.binding.root.tv_name.setTextColor(MECutility.getAttributeColor(binding.root.context, R.attr.uidContentItemPrimaryNormalTextColor))
                viewHolder.binding.root.tv_address_text.setTextColor(MECutility.getAttributeColor(binding.root.context, R.attr.uidContentItemPrimaryNormalTextColor))
                viewHolder.binding.root.ll_rl_address.setBackgroundResource(R.drawable.address_deselector)
            }

            viewHolder.binding.root.setOnClickListener {
                mSelectedAddress = mecAddresses.ecsAddresses[position]
                mSelectedItem = position
                notifyDataSetChanged()
            }

        }
    }

    override fun getItemViewType(position: Int): Int {

        if (position == totalItem - 1) return VIEW_TYPE_FOOTER
        return super.getItemViewType(position)

    }

}