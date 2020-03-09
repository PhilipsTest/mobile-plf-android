package com.philips.cdp.di.mec.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.fragment.app.FragmentManager
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.error.ECSErrorEnum
import com.philips.cdp.di.ecs.model.address.ECSAddress
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart
import com.philips.cdp.di.mec.R
import com.philips.cdp.di.mec.analytics.MECAnalyticServer
import com.philips.cdp.di.mec.analytics.MECAnalytics
import com.philips.cdp.di.mec.analytics.MECAnalyticsConstant
import com.philips.cdp.di.mec.common.MecError
import com.philips.cdp.di.mec.utils.MECConstant.IN_STOCK
import com.philips.cdp.di.mec.utils.MECConstant.LOW_STOCK
import com.philips.platform.uid.thememanager.UIDHelper
import com.philips.platform.uid.utils.DialogConstants
import com.philips.platform.uid.view.widget.AlertDialogFragment

class MECutility {

    companion object {

        private var alertDialogFragment: AlertDialogFragment? = null
        val ALERT_DIALOG_TAG = "ALERT_DIALOG_TAG"


        @JvmStatic
        fun showErrorDialog(context: Context, pFragmentManager: FragmentManager,
                            pButtonText: String, pErrorString: String, pErrorDescription: String) {

            if (!(context as Activity).isFinishing) {
                showDLSDialog(UIDHelper.getPopupThemedContext(context), pButtonText, pErrorString, pErrorDescription, pFragmentManager)
            }
        }


        internal fun showDLSDialog(context: Context, pButtonText: String, pErrorString: String, pErrorDescription: String, pFragmentManager: FragmentManager) {
            val builder = AlertDialogFragment.Builder(context)
                    .setMessage(pErrorDescription).setPositiveButton(pButtonText) { dismissAlertFragmentDialog(alertDialogFragment, pFragmentManager) }

            builder.setTitle(pErrorString)
            if (alertDialogFragment != null) {
                dismissAlertFragmentDialog(alertDialogFragment, pFragmentManager)
            }
            alertDialogFragment = builder.create()
            if (alertDialogFragment == null) {
                alertDialogFragment = builder.setCancelable(false).create()
            }

            if (!alertDialogFragment!!.isVisible && isCallingFragmentVisible(pFragmentManager)) {
                alertDialogFragment!!.show(pFragmentManager, ALERT_DIALOG_TAG)
            }

        }

        internal fun dismissAlertFragmentDialog(alertDialogFragment: AlertDialogFragment?, fragmentManager: FragmentManager) {
            var alertDialogFragment = alertDialogFragment

            if (alertDialogFragment == null) {
                alertDialogFragment = fragmentManager.findFragmentByTag(ALERT_DIALOG_TAG) as AlertDialogFragment?
            }
            if (alertDialogFragment != null  && isCallingFragmentVisible(fragmentManager))
                alertDialogFragment.dismiss()
        }

        fun showActionDialog(context: Context, positiveBtnText: String, negativeBtnText: String,
                             pErrorString: String, descriptionText: String, pFragmentManager: FragmentManager, alertListener: AlertListener) {
            val builder = AlertDialogFragment.Builder(context)
            builder.setDialogType(DialogConstants.TYPE_ALERT)

            if (!TextUtils.isEmpty(descriptionText)) {
                builder.setMessage(descriptionText)
            }

            if (!TextUtils.isEmpty(pErrorString)) {
                builder.setTitle(pErrorString)
            }
            builder.setPositiveButton(positiveBtnText
            ) {
                alertListener.onPositiveBtnClick()
                dismissAlertFragmentDialog(alertDialogFragment, pFragmentManager)
            }
            builder.setNegativeButton(negativeBtnText) {
                alertListener.onNegativeBtnClick()
                dismissAlertFragmentDialog(alertDialogFragment, pFragmentManager)
            }
            alertDialogFragment = builder.setCancelable(false).create()
            if (!alertDialogFragment!!.isVisible()) {
                alertDialogFragment!!.show(pFragmentManager, ALERT_DIALOG_TAG)
            }
        }


        fun isCallingFragmentVisible(fragmentManager: FragmentManager?): Boolean {

            if (fragmentManager != null) {
                val fragments = fragmentManager.fragments
                if (fragments != null && fragments.size > 0) {
                    val fragment = fragments[fragments.size - 1]
                    return fragment.activity != null && fragment.isAdded //&& fragment.isVisible && fragment.isResumed
                }
            }
            return false
        }

        fun indexOfSubString(ignoreCase: Boolean, str: CharSequence?, subString: CharSequence?): Int {
            if (str == null || subString == null) {
                return -1
            }
            val subStringLen = subString.length
            val max = str.length - subStringLen
            for (i in 0..max) {
                if (regionMatches(ignoreCase, str, i, subString, 0, subStringLen)) {
                    return i
                }
            }
            return -1
        }

        fun regionMatches(ignoreCase: Boolean, str: CharSequence?, strOffset: Int,
                          subStr: CharSequence?, subStrOffset: Int, length: Int): Boolean {
            if (str == null || subStr == null) {
                return false
            }

            if (str is String && subStr is String) {
                return str.regionMatches(strOffset, (subStr as String?)!!, subStrOffset, length, ignoreCase = ignoreCase)
            }

            //SubString length is more than string
            if (subStr.length > str.length) {
                return false
            }

            //Invalid start point
            if (strOffset < 0 || subStrOffset < 0 || length < 0) {
                return false
            }

            //Length can't be greater than diff of string length and offset
            if (str.length - strOffset < length || subStr.length - subStrOffset < length) {
                return false
            }

            //Start comparing
            var strIndex = strOffset
            var subStrIndex = subStrOffset
            var tmpLenth = length

            while (tmpLenth-- > 0) {
                val c1 = str[strIndex++]
                val c2 = subStr[subStrIndex++]

                if (c1 == c2) {
                    continue
                }

                //Same comparison as java framework
                if (ignoreCase && (Character.toUpperCase(c1) == Character.toUpperCase(c2) || Character.toLowerCase(c1) == Character.toLowerCase(c2))) {
                    continue
                }
                return false
            }
            return true
        }

        internal fun isStockAvailable(stockLevelStatus: String?, stockLevel: Int): Boolean {

            /* if (if hybris available ) { // todo
                 return true
             }*/

            if (stockLevelStatus == null) {
                return false
            }

            return if (stockLevelStatus.equals(IN_STOCK, ignoreCase = true) ||
                    stockLevelStatus.equals(LOW_STOCK, ignoreCase = true) || stockLevel > 0) {
                true
            } else false

        }

        fun stockStatus(availability : String): String {
            if(availability.equals("YES")){
                return "available"
            } else if(availability.equals("NO")){
                return "out of stock"
            }
            return ""
        }

        fun getQuantity(carts: ECSShoppingCart): Int {
            val totalItems = carts.totalItems
            var quantity = 0
            if (carts.entries != null) {
                val entries = carts.entries
                if (totalItems != 0 && null != entries) {
                    for (i in entries.indices) {
                        quantity = quantity + entries[i].quantity
                    }
                }
            }
            return quantity
        }

        fun isAuthError(ecsError: ECSError?):Boolean{
            var authError :Boolean = false
            if (ecsError!!.errorcode == ECSErrorEnum.ECSInvalidTokenError.errorCode
                    || ecsError!!.errorcode == ECSErrorEnum.ECSinvalid_grant.errorCode
                    || ecsError!!.errorcode == ECSErrorEnum.ECSinvalid_client.errorCode
                    || ecsError!!.errorcode == ECSErrorEnum.ECSOAuthDetailError.errorCode
                    || ecsError!!.errorcode == ECSErrorEnum.ECSOAuthNotCalled.errorCode){
                authError=true
            }
            return authError
        }

        @JvmStatic
        fun findGivenAddressInAddressList(ecsAddressID: String, ecsAddressList:List<ECSAddress>) :ECSAddress?{

            for (ecsAddress in  ecsAddressList){
                if(ecsAddressID.equals(ecsAddress.id,true)){
                    return ecsAddress
                }
            }
            return null
        }

        @JvmStatic
        fun tagAndShowError(mecError: MecError?, showDialog: Boolean, aFragmentManager: FragmentManager?, Acontext: Context?){
            if (!mecError!!.ecsError!!.errorType.equals("No internet connection")) {
                try {
                    //tag all techinical defect except "No internet connection"
                    var errorString: String = MECAnalyticsConstant.COMPONENT_NAME + ":"
                    if (mecError!!.ecsError!!.errorcode == 1000) {
                        errorString += MECAnalyticServer.bazaarVoice + ":"
                    } else if (mecError!!.ecsError!!.errorcode >= 5000 && mecError!!.ecsError!!.errorcode < 6000) {
                        errorString += MECAnalyticServer.hybris + ":"
                    } else {
                        //
                        errorString += MECAnalyticServer.prx + ":"
                    }
                    errorString = errorString + mecError!!.exception!!.message.toString()
                    errorString = errorString + mecError!!.ecsError!!.errorcode + ":"

                    MECAnalytics.trackAction(MECAnalyticsConstant.sendData, MECAnalyticsConstant.technicalError, errorString)
                } catch (e: Exception) {

                }
            }
            if (showDialog.equals(true)) {
                aFragmentManager?.let { Acontext?.let { it1 -> MECutility.showErrorDialog(it1, it, "OK", "Error", mecError!!.exception!!.message.toString()) } }
            }

        }

        fun getImageArrow(mContext: Context): Drawable {
            val width = mContext.resources.getDimension(R.dimen.mec_drop_down_icon_width_size).toInt()
            val height = mContext.resources.getDimension(R.dimen.mec_drop_down_icon_height_size).toInt()
            val imageArrow = VectorDrawableCompat.create(mContext.resources, R.drawable.mec_product_count_drop_down, mContext.theme)
            imageArrow!!.setBounds(0, 0, width, height)
            return imageArrow
        }


    }

    fun  constructShippingAddressDisplayField(ecsAddress: ECSAddress) : String{

        var formattedAddress = ""
        var regionDisplayName =  if (ecsAddress.region?.name !=null)  ecsAddress.region?.name else  ecsAddress.region?.isocodeShort
        var countryDisplayName = if (ecsAddress.country?.name !=null)  ecsAddress.country?.name else  ecsAddress.country?.isocode
        var houseNumber = ecsAddress.houseNumber
        var line1 = ecsAddress.line1
        var line2 = ecsAddress.line2
        var town = ecsAddress.town
        var postalCode = ecsAddress.postalCode
        var phoneNumber = ecsAddress.phone1

        formattedAddress = if (!houseNumber.isNullOrEmpty()) formattedAddress+ houseNumber +"," else formattedAddress
        formattedAddress = if (!line1.isNullOrEmpty()) formattedAddress+ line1 + ","+"\n" else formattedAddress
        formattedAddress = if (!line2.isNullOrEmpty()) formattedAddress+ line2 + ","+"\n" else formattedAddress
        formattedAddress = if (!town.isNullOrEmpty()) formattedAddress+ town + ","+"\n" else formattedAddress
        formattedAddress = if (!regionDisplayName.isNullOrEmpty()) formattedAddress+ regionDisplayName + ","+"\n" else formattedAddress
        formattedAddress = if (!postalCode.isNullOrEmpty()) formattedAddress+ postalCode + ","+"\n" else formattedAddress
        formattedAddress = if (!countryDisplayName.isNullOrEmpty()) formattedAddress+ countryDisplayName  else formattedAddress

        return formattedAddress
    }



}