/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.cdp.di.ecs.model.address.ECSDeliveryMode
import com.philips.cdp.di.ecs.model.address.ECSUserProfile
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart
import com.philips.cdp.di.ecs.model.payment.CardType
import com.philips.cdp.di.ecs.model.payment.ECSPayment
import com.philips.platform.mec.databinding.MecDeliveryBinding
import com.philips.platform.mec.R
import com.philips.platform.mec.common.ItemClickListener
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.screens.payment.MECPayment
import com.philips.platform.mec.screens.payment.MECPayments
import com.philips.platform.mec.screens.payment.PaymentRecyclerAdapter
import com.philips.platform.mec.screens.payment.PaymentViewModel
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.orderSummary.MECOrderSummaryFragment
import com.philips.platform.mec.screens.profile.ProfileViewModel
import com.philips.platform.mec.screens.shoppingCart.EcsShoppingCartViewModel
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECutility
import java.io.Serializable

class MECDeliveryFragment : MecBaseFragment(), ItemClickListener {


    private lateinit var paymentViewModel: PaymentViewModel
    var mRootView: View? = null
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: MecDeliveryBinding

    private var mECDeliveryModesAdapter: MECDeliveryModesAdapter? = null
    private var mecPaymentAdapter: PaymentRecyclerAdapter? = null
    private lateinit var ecsPayment: ECSPayment
    private lateinit var mECSDeliveryModeList: MutableList<ECSDeliveryMode>
    lateinit var ecsShoppingCartViewModel: EcsShoppingCartViewModel

    var ecsAddresses: List<ECSAddress>? = null
    lateinit var mECSShoppingCart: ECSShoppingCart

    private lateinit var addressViewModel: AddressViewModel

    var ecsBillingAddress: ECSAddress? = null


    override fun getFragmentTag(): String {
        return "MECDeliveryFragment"
    }

    private var bottomSheetFragment: ManageAddressFragment? = null


    private val ecsDeliveryModesObserver: Observer<List<ECSDeliveryMode>> = Observer(fun(eCSDeliveryMode: List<ECSDeliveryMode>?) {
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        mECSDeliveryModeList.clear()
        if (eCSDeliveryMode != null && eCSDeliveryMode.isNotEmpty()) {
            eCSDeliveryMode.let { mECSDeliveryModeList.addAll(it) }
        }
        if (null != mECSShoppingCart.deliveryMode) {
            mECDeliveryModesAdapter?.setSelectedDeliveryModeAsCart(mECSShoppingCart.deliveryMode)
        }
        mECDeliveryModesAdapter?.notifyDataSetChanged()
        binding.mecOrderSummaryBtn.visibility = View.VISIBLE
    })

    private val ecsSetDeliveryModeObserver: Observer<Boolean> = Observer { boolean ->

        ecsShoppingCartViewModel.getShoppingCart()

    }

    private val fetchProfileObserver: Observer<ECSUserProfile> = Observer { userProfile ->
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        var profileECSAddress: ECSAddress? = null

        if (userProfile.defaultAddress != null) {
            profileECSAddress = ecsAddresses?.let { MECutility.findGivenAddressInAddressList(userProfile.defaultAddress.id, it) }
        }

        if (profileECSAddress != null) {
            //if userProfile  delivery address and its ID is matching with one of the fetched address list
            setAndFetchDeliveryAddress(profileECSAddress)
            binding.ecsAddressShipping = profileECSAddress
        } else {
            //if delivery address  not present neither in Cart nor in user profile then set first address of fetched list
            ecsAddresses?.get(0)?.let { setAndFetchDeliveryAddress(it) }
        }

    }

    private val addressObserver: Observer<List<ECSAddress>> = Observer(fun(addressList: List<ECSAddress>?) {

        Log.d("Pabitra", "In addressObserver")

        ecsAddresses = addressList!!
        ecsShoppingCartViewModel.getShoppingCart()

    })

    private val paymentObserver: Observer<MECPayments> = Observer(fun(mecPayments: MECPayments) {

        MECDataHolder.INSTANCE.PAYMENT_HOLDER.payments.addAll(mecPayments.payments)
        MECDataHolder.INSTANCE.PAYMENT_HOLDER.isPaymentDownloaded = true
        binding.mecPaymentProgressBar.visibility = View.GONE
        showPaymentCardList()

    })

    private val cartObserver: Observer<ECSShoppingCart> = Observer<ECSShoppingCart> { ecsShoppingCart ->
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
        mECSShoppingCart = ecsShoppingCart

        onRefreshCart()
    }

    private fun onRefreshCart() {
        if (mECSShoppingCart.deliveryAddress != null) {

            val findGivenAddressInAddressList = ecsAddresses?.let { MECutility.findGivenAddressInAddressList(mECSShoppingCart.deliveryAddress.id, it) }

            if (findGivenAddressInAddressList != null) binding.ecsAddressShipping = findGivenAddressInAddressList

            if (!mECSDeliveryModeList.isNullOrEmpty()) {
                // if delivery modes are already fetched
                mECDeliveryModesAdapter?.setSelectedDeliveryModeAsCart(mECSShoppingCart.deliveryMode)
                mECDeliveryModesAdapter?.notifyDataSetChanged()
            } else {
                fetchDeliveryModes()
            }
        } else {
            checkDeliveryAddressSet()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if (null == mRootView) {

            binding = MecDeliveryBinding.inflate(inflater, container, false)

            addressViewModel = ViewModelProviders.of(this).get(AddressViewModel::class.java)
            profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
            ecsShoppingCartViewModel = ViewModelProviders.of(this).get(EcsShoppingCartViewModel::class.java)
            paymentViewModel =  ViewModelProviders.of(this).get(PaymentViewModel::class.java)


            //observe addressViewModel
            addressViewModel.mecError.observe(this, this)
            activity?.let { addressViewModel.ecsAddresses.observe(it, addressObserver) }
            addressViewModel.ecsDeliveryModes.observe(this, ecsDeliveryModesObserver)
            addressViewModel.ecsDeliveryModeSet.observe(this, ecsSetDeliveryModeObserver)


            //observe ProfileViewModel
            profileViewModel.userProfile.observe(this, fetchProfileObserver)
            profileViewModel.mecError.observe(this, this)


            //observe ecsShoppingCartViewModel
            ecsShoppingCartViewModel.ecsShoppingCart.observe(this, cartObserver)
            ecsShoppingCartViewModel.mecError.observe(this, this)
            paymentViewModel.mecPayments.observe(this, paymentObserver)
            paymentViewModel.mecError.observe(this, this)

            //observe paymentViewmodel
            // activity?.let { paymentViewModel.mecPayments.observe(it,paymentObserver) }
//            activity?.let { paymentViewModel.mecError.observe(it, this) }


            ecsAddresses = arguments?.getSerializable(MECConstant.KEY_ECS_ADDRESSES) as List<ECSAddress>
            mECSDeliveryModeList = mutableListOf()

            mECDeliveryModesAdapter = MECDeliveryModesAdapter(mECSDeliveryModeList, this)
            binding.mecDeliveryModeRecyclerView.adapter = mECDeliveryModesAdapter

            ecsAddresses = arguments?.getSerializable(MECConstant.KEY_ECS_ADDRESSES) as List<ECSAddress>
            mECSShoppingCart = arguments?.getSerializable(MECConstant.KEY_ECS_SHOPPING_CART) as ECSShoppingCart



            binding.ecsAddressShipping = ecsAddresses!![0]

            binding.mecAddressEditIcon.setOnClickListener { onEditClick() }

            binding.tvManageAddress.setOnClickListener { onManageAddressClick() }

            binding.mecDeliveryFragment = this

            mRootView = binding.root
            checkDeliveryAddressSet()

            getPaymentInfo()

        }
        return binding.root
    }

    private fun getPaymentInfo() {
        //Create a empty payment list
        if (arguments?.getSerializable(MECConstant.KEY_ECS_BILLING_ADDRESS) != null) {
            ecsBillingAddress = arguments?.getSerializable(MECConstant.KEY_ECS_BILLING_ADDRESS) as ECSAddress

            if (ecsBillingAddress != null) { // New user if user has created new billing address

                ecsPayment = ECSPayment()
                ecsPayment.id = MECConstant.NEW_CARD_PAYMENT
                ecsPayment.billingAddress = ecsBillingAddress
                val mecPayment = MECPayment(ecsPayment)
                MECDataHolder.INSTANCE.PAYMENT_HOLDER.payments.add(mecPayment)
            }
        }


        if (!MECDataHolder.INSTANCE.PAYMENT_HOLDER.isPaymentDownloaded) { // fetch data
            paymentViewModel.fetchPaymentDetails()
            //show progress bar here
        } else {
            binding.mecPaymentProgressBar.visibility = View.GONE
            showPaymentCardList() // taking from cache
        }
        // Payment logic ends
    }

    fun  showPaymentCardList() {
        mecPaymentAdapter = PaymentRecyclerAdapter(MECDataHolder.INSTANCE.PAYMENT_HOLDER, this)
        binding.mecPaymentRecyclerView.adapter = mecPaymentAdapter
    }

    override fun onResume() {
        super.onResume()
        setTitleAndBackButtonVisibility(R.string.mec_delivery, true)
        setCartIconVisibility(false)
    }

    override fun onStop() {
        super.onStop()
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)
    }


    override fun onItemClick(item: Any) {


        if (item is ECSDeliveryMode) {
            showProgressBar(binding.mecProgress.mecProgressBarContainer)
            addressViewModel.setDeliveryMode(item as ECSDeliveryMode)

        }

        // When Create Address from BottomSheet is clicked
        if (item is String && item.equals(MECConstant.CREATE_ADDRESS, true)) {

            //dismiss the bottom sheet fragment

            if (bottomSheetFragment != null) {
                if (bottomSheetFragment?.isVisible!!) {
                    bottomSheetFragment?.dismiss()
                }
            }
            Log.d("ADDRESS", "CREATE_ADDRESS")
            // create Address ======= starts
            val ecsAddress = createNewAddress()
            gotoCreateOrEditAddress(ecsAddress)
        }

        // When Create Billing Address  is clicked
        if (item is String && item.equals(MECConstant.CREATE_BILLING_ADDRESS, true)) {
            Log.d("ADDRESS", "CREATE_BILLING_ADDRESS")
            // create Address ======= starts
            val ecsAddress = createNewAddress()
            gotoCreateOrEditBillingAddress(ecsAddress)
        }

        // when Edit Billing Address is clicked
        if (item is MECPayment && item.ecsPayment.id.equals(MECConstant.NEW_CARD_PAYMENT, true)) {
            Log.d("ADDRESS", "CREATE_BILLING_ADDRESS")
            gotoCreateOrEditBillingAddress(item.ecsPayment.billingAddress)
        }


    }

    private fun gotoCreateOrEditBillingAddress(ecsAddress: ECSAddress) {
        val editAddressFragment = AddOrEditBillingAddressFragment()
        editAddressFragment.setTargetFragment(this, MECConstant.REQUEST_CODE_BILLING_ADDRESS)
        val bundle = Bundle()
        bundle.putSerializable(MECConstant.KEY_ECS_ADDRESS, ecsAddress)
        editAddressFragment.arguments = bundle
        replaceFragment(editAddressFragment, editAddressFragment.getFragmentTag(), true)
    }


    private fun createNewAddress(): ECSAddress {
        val ecsAddress = ECSAddress()

        //Set Country before binding
        ecsAddress.country = addressViewModel.getCountry()

        //set First Name
        val firstName = MECDataHolder.INSTANCE.getUserInfo().firstName
        if (!firstName.isNullOrEmpty() && !firstName.equals("null", true)) {
            ecsAddress.firstName = firstName
        }

        //set Last Name
        val lastName = MECDataHolder.INSTANCE.getUserInfo().lastName
        if (!lastName.isNullOrEmpty() && !lastName.equals("null", true)) {
            ecsAddress.lastName = lastName
        }
        return ecsAddress
    }


    fun onEditClick() {
        gotoCreateOrEditAddress(binding.ecsAddressShipping!!)
    }

    private fun gotoCreateOrEditAddress(ecsAddress: ECSAddress) {
        val editAddressFragment = CreateOrEditAddressFragment()
        editAddressFragment.setTargetFragment(this, MECConstant.REQUEST_CODE_ADDRESSES)
        val bundle = Bundle()
        bundle.putSerializable(MECConstant.KEY_ECS_ADDRESS, ecsAddress)
        editAddressFragment.arguments = bundle
        replaceFragment(editAddressFragment, editAddressFragment.getFragmentTag(), true)
    }

    private fun onManageAddressClick() {
        val bundle = Bundle()

        bottomSheetFragment = ManageAddressFragment()
        bottomSheetFragment?.setTargetFragment(this, MECConstant.REQUEST_CODE_ADDRESSES)
        bundle.putSerializable(MECConstant.KEY_ECS_ADDRESSES, ecsAddresses as Serializable)
        bundle.putSerializable(MECConstant.KEY_MEC_DEFAULT_ADDRESSES_ID, binding.ecsAddressShipping?.id)
        bundle.putSerializable(MECConstant.KEY_ITEM_CLICK_LISTENER, this)
        bottomSheetFragment?.arguments = bundle
        fragmentManager?.let { bottomSheetFragment?.show(it, bottomSheetFragment?.tag) }
    }

    private fun fetchDeliveryModes() {
        showProgressBar(binding.mecProgress.mecProgressBarContainer)
        addressViewModel.fetchDeliveryModes()
    }

    override fun processError(mecError: MecError?, showDialog: Boolean) {
        dismissProgressBar(binding.mecProgress.mecProgressBarContainer)

        if(mecError?.mECRequestType == MECRequestType.MEC_FETCH_PAYMENT_DETAILS) {
            binding.mecPaymentProgressBar.visibility = View.GONE
            showPaymentCardList() // even for error inflate the Add Payment view
        }
        super.processError(mecError, showDialog)
    }

    private fun checkDeliveryAddressSet() {
        var findGivenAddressInAddressList: ECSAddress? = null
        if (null != mECSShoppingCart.deliveryAddress) {
            findGivenAddressInAddressList = ecsAddresses?.let { MECutility.findGivenAddressInAddressList(mECSShoppingCart.deliveryAddress.id, it) }
        }
        if (null != findGivenAddressInAddressList) {
            // if shopping cart has delivery address and its ID is matching with one of the fetched address list
            binding.ecsAddressShipping = findGivenAddressInAddressList
            fetchDeliveryModes()
        } else {

            profileViewModel.fetchUserProfile()

        }

    }

    private fun setAndFetchDeliveryAddress(ecsAddress: ECSAddress) {
        showProgressBar(binding.mecProgress.mecProgressBarContainer)
        addressViewModel.setAndFetchDeliveryAddress(ecsAddress)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == MECConstant.REQUEST_CODE_ADDRESSES) {
            val bundleExtra = data?.getBundleExtra(MECConstant.BUNDLE_ADDRESSES)
            ecsAddresses = bundleExtra?.getSerializable(MECConstant.KEY_ECS_ADDRESSES) as List<ECSAddress>

            if(bundleExtra.getSerializable(MECConstant.KEY_ECS_SHOPPING_CART)!=null) {
                mECSShoppingCart = bundleExtra.getSerializable(MECConstant.KEY_ECS_SHOPPING_CART) as ECSShoppingCart
            }
            onRefreshCart()
        }

        if (requestCode == MECConstant.REQUEST_CODE_BILLING_ADDRESS) {

            //TODO duplicate issue

            val bundleExtra = data?.getBundleExtra(MECConstant.BUNDLE_BILLING_ADDRESS)
            val ecsBillingAddress = bundleExtra?.getSerializable(MECConstant.KEY_ECS_BILLING_ADDRESS) as ECSAddress


            //Check if "New Card" is already present , then only add or else update the existing object of the list
            val newCardPresent = MECDataHolder.INSTANCE.PAYMENT_HOLDER.isNewCardPresent()

            if (newCardPresent) { // This happens when we edit the existing billing address
                MECDataHolder.INSTANCE.PAYMENT_HOLDER.getNewCard()?.ecsPayment?.billingAddress = ecsBillingAddress

            } else {

                var ecsPayment = ECSPayment()
                ecsPayment.id = MECConstant.NEW_CARD_PAYMENT
                val newCardType = CardType()
                newCardType.name = getString(R.string.mec_new_card_text)
                ecsPayment.cardType = newCardType
                ecsPayment.billingAddress = ecsBillingAddress
                val mecPaymentNew = MECPayment(ecsPayment)

                MECDataHolder.INSTANCE.PAYMENT_HOLDER.payments.add(mecPaymentNew)
            }

            showPaymentCardList()
        }
    }

    fun onOrderSummaryClick() {
        val mecOrderSummaryFragment = MECOrderSummaryFragment()
        val bundle = Bundle()
        if (MECDataHolder.INSTANCE.PAYMENT_HOLDER.getSelectedPayment() != null) {
            bundle.putSerializable(MECConstant.MEC_PAYMENT_METHOD, MECDataHolder.INSTANCE.PAYMENT_HOLDER.getSelectedPayment())
        } else {
            MECutility.showDLSDialog(binding.mecPaymentRecyclerView.context, getString(R.string.mec_ok), getString(R.string.mec_address), getString(R.string.mec_no_payment_error_message), fragmentManager!!)
            return
        }
        if (mECSShoppingCart.deliveryMode != null) {
            bundle.putSerializable(MECConstant.KEY_ECS_SHOPPING_CART, mECSShoppingCart)
        } else {
            MECutility.showDLSDialog(binding.mecPaymentRecyclerView.context, getString(R.string.mec_ok), getString(R.string.mec_delivery_method), getString(R.string.mec_no_delivery_mode_error_message), fragmentManager!!)
            return
        }
        if (ecsAddresses!!.isNotEmpty()) {
            bundle.putSerializable(MECConstant.KEY_ECS_ADDRESS, ecsAddresses?.get(0))
        } else {
            MECutility.showDLSDialog(binding.mecPaymentRecyclerView.context, getString(R.string.mec_ok), getString(R.string.mec_shipping_address), getString(R.string.mec_no_address_select_message), fragmentManager!!)
            return
        }
        mecOrderSummaryFragment.arguments = bundle
        replaceFragment(mecOrderSummaryFragment, MECOrderSummaryFragment.TAG, true)
    }
}