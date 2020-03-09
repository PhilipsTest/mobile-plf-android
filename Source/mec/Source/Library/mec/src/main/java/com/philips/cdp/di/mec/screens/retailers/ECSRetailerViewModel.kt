package com.philips.cdp.di.mec.screens.retailers

import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.philips.cdp.di.ecs.model.retailers.ECSRetailerList
import com.philips.cdp.di.mec.R
import com.philips.cdp.di.mec.common.CommonViewModel
import com.philips.cdp.di.mec.common.ItemClickListener
import com.philips.cdp.di.mec.utils.MECDataHolder
import com.philips.platform.uid.view.widget.Label
import java.util.*

class ECSRetailerViewModel : CommonViewModel() {

    val ecsRetailerList = MutableLiveData<ECSRetailerList>()

    var ecsServices = MECDataHolder.INSTANCE.eCSServices

    var ecsRetailersRepository =  ECSRetailersRepository(ecsServices,this)

    fun getRetailers (ctn:String){
        ecsRetailersRepository.getRetailers(ctn)
    }

    companion object DataBindingAdapter {
        @JvmStatic
        @BindingAdapter("retailers","listener")
        fun setAdapter(recyclerView: RecyclerView, ecsRetailerList: ECSRetailerList, itemClickListener: ItemClickListener) {
            recyclerView.adapter = MECRetailersAdapter(ecsRetailerList.retailers,itemClickListener)
        }

        @JvmStatic
        @BindingAdapter("setStock")
        fun setStock(stockLabel: Label, stock: String) {
            if (stock.equals("YES")) {
                stockLabel.setText(R.string.mec_in_stock)
                stockLabel.setTextColor(ContextCompat.getColor(stockLabel.context, R.color.uid_signal_green_level_60))
            } else {
                stockLabel.setText(R.string.mec_out_of_stock)
                stockLabel.setTextColor(ContextCompat.getColor(stockLabel.context, R.color.uid_signal_red_level_60))
            }
        }

        @JvmStatic
        @BindingAdapter("rotation")
        fun setRotation(stockLabel: Label, rotation: String?) { // roation text is not of anny use , can be removed

            if (Locale.getDefault().language.contentEquals("ar") || Locale.getDefault().language.contentEquals("fa") || Locale.getDefault().language.contentEquals("he")) {
                stockLabel.rotation = 180f
            }
        }
    }

}