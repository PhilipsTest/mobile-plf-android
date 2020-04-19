/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */

package com.philips.platform.mec.screens.history.orderDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.philips.cdp.di.ecs.model.orders.ECSOrderDetail
import com.philips.cdp.prxclient.datamodels.contacts.ContactsModel
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.utils.MECConstant

class MECOrderDetailFragment : MecBaseFragment(){

    private var ecsOrderDetail: ECSOrderDetail? = null
    private lateinit var mecOrderDetailViewModel: MECOrderDetailViewModel

    override fun getFragmentTag(): String {
        return "MECOrderDetailFragment"
    }

    private val contactsObserver: Observer<ContactsModel> = Observer { contactsModel ->


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setCartIconVisibility(false)

        //TODO to add layout

        mecOrderDetailViewModel = ViewModelProvider(this).get(MECOrderDetailViewModel::class.java)

        mecOrderDetailViewModel.contacts.observe(viewLifecycleOwner, contactsObserver)
        mecOrderDetailViewModel.mecError.observe(viewLifecycleOwner, this)
        ecsOrderDetail = arguments?.getParcelable<ECSOrderDetail>(MECConstant.MEC_ORDER_DETAIL)

        context?.let { ecsOrderDetail?.entries?.get(0)?.product?.summary?.subcategory?.let { it1 -> mecOrderDetailViewModel.fetchContacts(it, it1) } }

        return null
    }
}