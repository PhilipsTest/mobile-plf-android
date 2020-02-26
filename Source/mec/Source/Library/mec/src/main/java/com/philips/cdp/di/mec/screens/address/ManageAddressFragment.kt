package com.philips.cdp.di.mec.screens.address

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.cdp.di.mec.common.ItemClickListener
import com.philips.cdp.di.mec.common.MecError
import com.philips.cdp.di.mec.databinding.MecAddressManageBinding
import com.philips.cdp.di.mec.utils.MECConstant
import kotlinx.android.synthetic.main.mec_address_manage.view.*


class ManageAddressFragment : BottomSheetDialogFragment(){


    private lateinit var addressViewModel: AddressViewModel
    private lateinit var binding: MecAddressManageBinding

    private lateinit var mecAddresses: MECAddresses

    private lateinit var addressBottomSheetRecyclerAdapter : AddressBottomSheetRecyclerAdapter

    companion object {
        val TAG:String="ManageAddressFragment"
    }

    private val fetchAddressObserver: Observer<List<ECSAddress>> = Observer(fun(addressList: List<ECSAddress>?) {


        dismiss()

    })

    private val errorObserver : Observer<MecError>  = Observer(fun( mecError: MecError?) {


        dismiss()
        Log.d(TAG ,"Error on deleting or setting address" )

    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = MecAddressManageBinding.inflate(inflater, container, false)

        addressViewModel = ViewModelProviders.of(this).get(AddressViewModel::class.java)
        addressViewModel.ecsAddresses.observe(this,fetchAddressObserver)
        addressViewModel.mecError.observe(this,errorObserver)

        var ecsAddresses = arguments?.getSerializable(MECConstant.KEY_ECS_ADDRESSES) as List<ECSAddress>
        var itemClickListener = arguments?.getSerializable(MECConstant.KEY_ITEM_CLICK_LISTENER) as ItemClickListener

        mecAddresses = MECAddresses(ecsAddresses)

        //if only one Address is there , make the delete button disable
        if(ecsAddresses.size == 1){
            binding.root.mec_btn_delete_address.isEnabled = false
        }

        addressBottomSheetRecyclerAdapter = AddressBottomSheetRecyclerAdapter(mecAddresses, itemClickListener)

        binding.recyclerView.adapter = addressBottomSheetRecyclerAdapter



        binding.mecBtnDeleteAddress.setOnClickListener {

            val mSelectedAddress = addressBottomSheetRecyclerAdapter.mSelectedAddress
            addressViewModel.deleteAndFetchAddress(mSelectedAddress)
            dismiss()
        }

        binding.mecBtnSetAddress.setOnClickListener {

            val mSelectedAddress = addressBottomSheetRecyclerAdapter.mSelectedAddress

            addressViewModel.setAndFetchDeliveryAddress(mSelectedAddress)
            dismiss()
        }

        return binding.root
    }


}
