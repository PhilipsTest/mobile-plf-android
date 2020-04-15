/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.philips.cdp.di.ecs.model.address.ECSDeliveryMode
import com.philips.cdp.di.ecs.model.cart.DeliveryModeEntity
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.databinding.MecDeliveryModeItemBinding
import kotlinx.android.synthetic.main.mec_delivery_mode_item.view.*

class MECDeliveryModesAdapter(private val deliveryModes : MutableList<ECSDeliveryMode>,private val itemClickListener : ItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mSelectedItem = -1 // default value no selection..

    private lateinit var deliveryMode: ECSDeliveryMode
    private  var mECSShoppingCartDeliveryModeEntity: DeliveryModeEntity? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MECDeliveryModeHolder(MecDeliveryModeItemBinding.inflate(LayoutInflater.from(parent.context)))
    }


    override fun getItemCount(): Int {
        return deliveryModes.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        deliveryMode=deliveryModes.get(position)
        if(null!=mECSShoppingCartDeliveryModeEntity && deliveryMode.code.equals(mECSShoppingCartDeliveryModeEntity?.code,true)){
            // if this fetched delivery mode is same as cart delivery Mode then select radio button
            mSelectedItem=position
        }
       val mECDeliveryModeHolder = holder as MECDeliveryModeHolder
        mECDeliveryModeHolder.bind(deliveryMode,itemClickListener)
        mECDeliveryModeHolder.itemView.mec_delivery_mode_radio_button.setChecked(position == mSelectedItem);
    }

    fun getSelectedDeliveryMode() : ECSDeliveryMode{
        return deliveryMode
    }

    fun setSelectedDeliveryModeAsCart(aDeliveryModeEntity: DeliveryModeEntity?){
        mECSShoppingCartDeliveryModeEntity=aDeliveryModeEntity
    }


    inner class MECDeliveryModeHolder(val binding: MecDeliveryModeItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(deliveryMode :ECSDeliveryMode ,  itemClickListener: ItemClickListener){
            binding.deliveryMode=deliveryMode
            binding.mecDeliveryModeItemRow.setOnClickListener{
                setDeliveryMode()
            }
            binding.mecDeliveryModeRadioButton.setOnClickListener{
                setDeliveryMode()
            }

        }

        private fun setDeliveryMode(){
            if(mSelectedItem!=getAdapterPosition()) {
                mECSShoppingCartDeliveryModeEntity=null// reset previous set delivery mode
                mSelectedItem = getAdapterPosition()
                itemClickListener.onItemClick(deliveryModes.get(getAdapterPosition()) as Object)
                notifyDataSetChanged()

            }
        }
    }
}