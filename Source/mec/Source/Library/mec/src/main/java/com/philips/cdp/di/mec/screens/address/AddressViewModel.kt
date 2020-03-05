package com.philips.cdp.di.mec.screens.address

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.philips.cdp.di.ecs.model.address.Country
import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.cdp.di.ecs.model.address.ECSDeliveryMode
import com.philips.cdp.di.ecs.model.region.ECSRegion
import com.philips.cdp.di.ecs.util.ECSConfiguration
import com.philips.cdp.di.mec.R
import com.philips.cdp.di.mec.common.CommonViewModel
import com.philips.cdp.di.mec.common.MECRequestType
import com.philips.cdp.di.mec.utils.MECDataHolder
import com.philips.cdp.di.mec.utils.MECutility
import com.philips.cdp.di.mec.view.MECDropDown
import com.philips.platform.uid.view.widget.CheckBox
import com.philips.platform.uid.view.widget.InputValidationLayout
import com.philips.platform.uid.view.widget.Label
import com.philips.platform.uid.view.widget.ValidationEditText
import kotlinx.android.synthetic.main.mec_enter_address.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class AddressViewModel : CommonViewModel() {


    private var ecsRegionListCallback = ECSRegionListCallback(this)

    private var ecsCreateAddressCallBack = ECSCreateAddressCallBack(this)

    private var ecsFetchAddressesCallback = ECSFetchAddressesCallback(this)

    private var ecsFetchDeliveryModesCallback = ECSFetchDeliveryModesCallback(this)

    private var ecsSetDeliveryModesCallback = ECSSetDeliveryModesCallback(this)

    var ecsServices = MECDataHolder.INSTANCE.eCSServices

    var addressRepository = AddressRepository(ecsServices)

    var regionsList = MutableLiveData<List<ECSRegion>>()

    var eCSAddress = MutableLiveData<ECSAddress>()

    val ecsAddresses = MutableLiveData<List<ECSAddress>>()

    val ecsDeliveryModes = MutableLiveData<List<ECSDeliveryMode>>()

    val ecsDeliveryModeSet = MutableLiveData<Boolean>()

    lateinit var paramEcsAddress: ECSAddress

    lateinit var paramEcsDeliveryMode: ECSDeliveryMode


    fun fetchRegions() {
        ecsRegionListCallback.mECRequestType=MECRequestType.MEC_FETCH_REGIONS
        addressRepository.getRegions(ecsRegionListCallback)
    }

    fun fetchAddresses(){
        ecsFetchAddressesCallback.mECRequestType=MECRequestType.MEC_FETCH_SAVED_ADDRESSES
        addressRepository.fetchSavedAddresses(ecsFetchAddressesCallback)
    }

    fun createAddress(ecsAddress: ECSAddress){
        paramEcsAddress=ecsAddress
        ecsCreateAddressCallBack.mECRequestType=MECRequestType.MEC_CREATE_ADDRESS
        addressRepository.createAddress(ecsAddress,ecsCreateAddressCallBack)
    }

    fun createAndFetchAddress(ecsAddress: ECSAddress){
        paramEcsAddress=ecsAddress
        ecsCreateAddressCallBack.mECRequestType=MECRequestType.MEC_CREATE_AND_FETCH_ADDRESS
        addressRepository.createAndFetchAddress(ecsAddress,ecsFetchAddressesCallback)
    }

    fun deleteAndFetchAddress(ecsAddress: ECSAddress){
        paramEcsAddress=ecsAddress
        ecsCreateAddressCallBack.mECRequestType=MECRequestType.MEC_DELETE_AND_FETCH_ADDRESS
        addressRepository.deleteAddress(ecsAddress,ecsFetchAddressesCallback)
    }

    fun setAndFetchDeliveryAddress(ecsAddress: ECSAddress){
        paramEcsAddress=ecsAddress
        ecsFetchAddressesCallback.mECRequestType=MECRequestType.MEC_SET_AND_FETCH_DELIVERY_ADDRESS
        addressRepository.setAndFetchDeliveryAddress(ecsAddress,ecsFetchAddressesCallback)
    }

    fun updateAndFetchAddress(ecsAddress: ECSAddress){
        paramEcsAddress=ecsAddress
        ecsFetchAddressesCallback.mECRequestType=MECRequestType.MEC_UPDATE_AND_FETCH_ADDRESS
        addressRepository.updateAndFetchAddress(ecsAddress,ecsFetchAddressesCallback)
    }

    fun fetchDeliveryModes(){
        ecsFetchDeliveryModesCallback.mECRequestType = MECRequestType.MEC_FETCH_DELIVERY_MODES
        addressRepository.fetchDeliveryModes(ecsFetchDeliveryModesCallback)
    }

    fun setDeliveryMode(ecsDeliveryMode: ECSDeliveryMode ){
        paramEcsDeliveryMode=ecsDeliveryMode
        ecsSetDeliveryModesCallback.mECRequestType=MECRequestType.MEC_SET_DELIVERY_MODE
        addressRepository.setDeliveryMode(ecsDeliveryMode,ecsSetDeliveryModesCallback)

    }

    fun retryAPI(mecRequestType: MECRequestType) {
        var retryAPI = selectAPIcall(mecRequestType)
        authAndCallAPIagain(retryAPI,authFailCallback)
    }

    fun selectAPIcall(mecRequestType: MECRequestType):() -> Unit{

        lateinit  var APIcall: () -> Unit
        when(mecRequestType) {
            MECRequestType.MEC_FETCH_REGIONS                     -> APIcall = { fetchRegions() }
            MECRequestType.MEC_FETCH_SAVED_ADDRESSES             -> APIcall = { fetchAddresses()}
            MECRequestType.MEC_CREATE_ADDRESS                    -> APIcall = { createAddress(paramEcsAddress) }
            MECRequestType.MEC_CREATE_AND_FETCH_ADDRESS          -> APIcall = { createAndFetchAddress(paramEcsAddress) }
            MECRequestType.MEC_DELETE_AND_FETCH_ADDRESS          -> APIcall = { deleteAndFetchAddress(paramEcsAddress) }
            MECRequestType.MEC_SET_AND_FETCH_DELIVERY_ADDRESS    -> APIcall = { setAndFetchDeliveryAddress(paramEcsAddress) }
            MECRequestType.MEC_UPDATE_AND_FETCH_ADDRESS          -> APIcall = { updateAndFetchAddress(paramEcsAddress) }
            MECRequestType.MEC_FETCH_DELIVERY_MODES              -> APIcall = { fetchDeliveryModes() }
            MECRequestType.MEC_SET_DELIVERY_MODE                 -> APIcall = { setDeliveryMode(paramEcsDeliveryMode) }

        }
        return APIcall
    }


    private fun getAddressFieldEnablerJson(context: Context): String {

        val manager = context.assets
        val file = manager.open("mec_address_config.json")
        val formArray = ByteArray(file.available())
        file.read(formArray)
        file.close()
        return String(formArray)
    }

    fun getAddressFieldEnabler(country: String , context: Context): MECAddressFieldEnabler? {

        var addressFieldEnablerJson: String? = null
        var addressFieldEnabler: MECAddressFieldEnabler? = null
        try {
            addressFieldEnablerJson = getAddressFieldEnablerJson(context)

            var jsonObject: JSONObject? = null

            jsonObject = JSONObject(addressFieldEnablerJson)

            val addressEnablerJsonObject = jsonObject.getJSONObject("excludedFields")
            addressFieldEnabler = MECAddressFieldEnabler()

            val jsonArray = addressEnablerJsonObject.getJSONArray(country)


            for (i in 0 until jsonArray.length()) {
                val excludedField = jsonArray.getString(i)
                val addressFieldJsonEnum = AddressFieldJsonEnum.getAddressFieldJsonEnumFromField(excludedField)
                setAddressFieldEnabler(addressFieldEnabler, addressFieldJsonEnum!!)
            }

        } catch (e: JSONException) {

        } catch (e: IOException) {

        }

        return addressFieldEnabler
    }

    private fun setAddressFieldEnabler(addressFieldEnabler: MECAddressFieldEnabler, addressFieldJsonEnum: AddressFieldJsonEnum) {

        when (addressFieldJsonEnum) {

            AddressFieldJsonEnum.ADDRESS_ONE -> addressFieldEnabler.isAddress1Enabled = false

            AddressFieldJsonEnum.ADDRESS_TWO -> addressFieldEnabler.isAddress2Enabled = false
            AddressFieldJsonEnum.PHONE -> addressFieldEnabler.isPhoneEnabled = false
            AddressFieldJsonEnum.FIRST_NAME -> addressFieldEnabler.isFirstNameEnabled = false
            AddressFieldJsonEnum.LAST_NAME -> addressFieldEnabler.isLastNmeEnabled = false
            AddressFieldJsonEnum.STATE -> addressFieldEnabler.isStateEnabled= false
            AddressFieldJsonEnum.SALUTATION -> addressFieldEnabler.isSalutationEnabled = false
            AddressFieldJsonEnum.COUNTRY -> addressFieldEnabler.isCountryEnabled = false
            AddressFieldJsonEnum.POSTAL_CODE -> addressFieldEnabler.isPostalCodeEnabled = false
            AddressFieldJsonEnum.HOUSE_NUMBER -> addressFieldEnabler.isHouseNumberEnabled =false
            AddressFieldJsonEnum.TOWN -> addressFieldEnabler.isTownEnabled = false
        }

    }



    companion object DataBindingAdapter {

        //for Address

        @JvmStatic
        @BindingAdapter("billingView","scrollView")
        fun enableBillingForm(checkBox: CheckBox, view: LinearLayout , scrollView:ScrollView){

            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                val animate = view.animate()
                animate.duration = 500

                if(isChecked){
                    animate.alpha(0.0f)
                    animate.setListener(object: AnimatorListenerAdapter() {

                        override fun onAnimationEnd(animation: Animator) {

                            view.visibility = View.GONE
                        }
                    })

                } else {
                    animate.alpha(1.0f)
                    animate.setListener(object: AnimatorListenerAdapter() {

                        override fun onAnimationEnd(animation: Animator) {

                            view.visibility = View.VISIBLE
                            view.parent.requestChildFocus(view,view)

                        }
                    })
                }
            }
        }


        @JvmStatic
        @BindingAdapter("phoneNumberEditText")
        fun setPhoneNumberValidator(inputValidationLayout: InputValidationLayout,phoneNumberValidationEditText: ValidationEditText) {
            inputValidationLayout.setValidator(PhoneNumberInputValidator(phoneNumberValidationEditText, PhoneNumberUtil.getInstance()))
        }

        @JvmStatic
        @BindingAdapter("emptyValidator")
        fun setEmptyValidator(inputValidationLayout: InputValidationLayout, obj:Any?) { // As binding Adapter without parameter not possible

            inputValidationLayout.setValidator(EmptyInputValidator())
        }

        @JvmStatic
        @BindingAdapter("dropDownData")
        fun setDropDown(validationEditText: ValidationEditText, dropDownData:Array<String> ?){

            if (dropDownData.isNullOrEmpty()) return

            validationEditText.setCompoundDrawables(null, null, MECutility.getImageArrow(validationEditText.context), null)
            val salutationDropDown = MECDropDown(validationEditText,dropDownData)

            salutationDropDown.createPopUp()
            validationEditText.setOnTouchListener(fun(v: View, event: MotionEvent): Boolean {
                salutationDropDown.show()
                return false
            })
        }

        @JvmStatic
        @BindingAdapter("firstName")
        fun setFirstName(validationEditText: ValidationEditText, obj:Any?){

            val firstName = MECDataHolder.INSTANCE.getUserInfo().firstName
            if(!firstName.isNullOrEmpty() && !firstName.equals("null",true)){
                validationEditText.setText(firstName)
            }

        }

        @JvmStatic
        @BindingAdapter("lastName")
        fun setLastName(validationEditText: ValidationEditText, obj:Any?){

            val lastName = MECDataHolder.INSTANCE.getUserInfo().lastName
            if(!lastName.isNullOrEmpty() && !lastName.equals("null",true)){
                validationEditText.setText(lastName)
            }

        }

        //ECSAddress

        @JvmStatic
        @BindingAdapter("shippingAddress")
        fun setShippingAddress(lebel: Label, ecsAddress: ECSAddress){
            lebel.setText(MECutility().constructShippingAddressDisplayField(ecsAddress))
        }

    }

    enum class AddressFieldJsonEnum (val addressField: String) {

        SALUTATION("salutation"),
        FIRST_NAME("firstName"),
        LAST_NAME("lastName"),
        EMAIL("email"),
        PHONE("phone"),
        HOUSE_NUMBER("houseNumber"),
        ADDRESS_ONE("address1"),
        ADDRESS_TWO("address2"),
        TOWN("town"),
        POSTAL_CODE("postalCode"),
        STATE("state"),
        COUNTRY("country");


        companion object {

            fun getAddressFieldJsonEnumFromField(field: String): AddressFieldJsonEnum? {
                val values = AddressFieldJsonEnum.values()

                for (addressFieldJsonEnum in values) {

                    if (addressFieldJsonEnum.addressField.equals(field.trim { it <= ' ' }, ignoreCase = true)) {
                        return addressFieldJsonEnum
                    }
                }
                return null
            }
        }
    }

    fun getECSAddress(linearLayout: LinearLayout, mecRegions: MECRegions?): ECSAddress {

        val firstName = linearLayout.et_first_name.text.toString()
        val lastName = linearLayout.et_last_name.text.toString()
        // val countryCode = linearLayout.et_country.text.toString()
        val addressLineOne = linearLayout.et_address_line_one.text.toString()
        val addressLineTwo = linearLayout.et_address_line_two.text.toString()
        val postalCode = linearLayout.et_postal_code.text.toString()
        val phoneOne = linearLayout.et_phone1.text.toString()
        val town = linearLayout.et_town.text.toString()
        val houseNumber = linearLayout.et_house_no.text.toString()
        val title = linearLayout.et_salutation.text.toString()
        val state =linearLayout.et_state.text.toString()


        val ecsAddress = ECSAddress()

        ecsAddress.firstName = firstName
        ecsAddress.lastName = lastName
        ecsAddress.titleCode = title.toLowerCase(Locale.getDefault()) // Todo , pass the locale to lower case

        val country = Country()
        country.isocode = ECSConfiguration.INSTANCE.country
        ecsAddress.country = country

        ecsAddress.line1 = addressLineOne
        ecsAddress.line2 = addressLineTwo
        ecsAddress.postalCode = postalCode
        ecsAddress.phone1 = phoneOne
        ecsAddress.phone2=phoneOne
        ecsAddress.town = town
        ecsAddress.houseNumber = houseNumber
        ecsAddress.region = mecRegions?.getRegion(state)

        return ecsAddress
    }

    fun getCountry() : Country{
        val country = Country()
        country.isocode = ECSConfiguration.INSTANCE.country
        return country
    }

    fun setRegion(linearLayout: LinearLayout , mecRegions: MECRegions? ,ecsAddress: ECSAddress){
        var state= linearLayout.et_state.text.toString()
        ecsAddress.region = mecRegions?.getRegion(state)
    }

    public fun shakeError(): TranslateAnimation {
        val shake = TranslateAnimation(0f, 10f, 0f, 0f)
        shake.duration = 500
        shake.interpolator = CycleInterpolator(7f)
        return shake
    }

}