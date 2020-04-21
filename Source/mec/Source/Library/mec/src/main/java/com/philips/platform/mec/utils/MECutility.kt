/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.utils

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import androidx.fragment.app.FragmentManager
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.gson.Gson
import com.philips.platform.appinfra.securestorage.SecureStorageInterface
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.error.ECSErrorEnum
import com.philips.platform.ecs.model.address.ECSAddress
import com.philips.platform.ecs.model.cart.ECSShoppingCart
import com.philips.platform.mec.R
import com.philips.platform.mec.analytics.MECAnalyticServer.bazaarVoice
import com.philips.platform.mec.analytics.MECAnalyticServer.hybris
import com.philips.platform.mec.analytics.MECAnalyticServer.other
import com.philips.platform.mec.analytics.MECAnalyticServer.prx
import com.philips.platform.mec.analytics.MECAnalyticServer.wtb
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.analytics.MECAnalyticsConstant
import com.philips.platform.mec.analytics.MECAnalyticsConstant.COMPONENT_NAME
import com.philips.platform.mec.analytics.MECAnalyticsConstant.appError
import com.philips.platform.mec.analytics.MECAnalyticsConstant.inappnotification
import com.philips.platform.mec.analytics.MECAnalyticsConstant.inappnotificationresponse
import com.philips.platform.mec.analytics.MECAnalyticsConstant.sendData
import com.philips.platform.mec.auth.HybrisAuth
import com.philips.platform.mec.common.MECRequestType
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.integration.MECDataProvider
import com.philips.platform.mec.screens.payment.MECPayment
import com.philips.platform.mec.utils.MECConstant.IN_STOCK
import com.philips.platform.mec.utils.MECConstant.LOW_STOCK
import com.philips.platform.uid.thememanager.UIDHelper
import com.philips.platform.uid.utils.DialogConstants
import com.philips.platform.uid.view.widget.AlertDialogFragment


class MECutility {

    companion object {

        private var alertDialogFragment: AlertDialogFragment? = null
        val ALERT_DIALOG_TAG = "ALERT_DIALOG_TAG"


        /*
        * This method shows error dialog and takes error description as String parameter (pErrorDescriptionString: String)
        * */
        @JvmStatic
        fun showErrorDialog(context: Context, pFragmentManager: FragmentManager, pButtonText: String, pErrorString: String, pErrorDescriptionString: String) {
            if (!(context as Activity).isFinishing) {
                showErrorDialogWithResourceID(UIDHelper.getPopupThemedContext(context), pFragmentManager, pButtonText, pErrorString, null, pErrorDescriptionString)
            }
        }


        /*
        * This method shows error dialog and takes error description as Int(resource id) parameter (pErrorDescriptionResourceId: Int)
        * */
        @JvmStatic
        fun showErrorDialog(context: Context, pFragmentManager: FragmentManager, pButtonText: String, pErrorString: String, pErrorDescriptionResourceId: Int) {
            if (!(context as Activity).isFinishing) {
                showErrorDialogWithResourceID(UIDHelper.getPopupThemedContext(context), pFragmentManager, pButtonText, pErrorString, pErrorDescriptionResourceId, context.getString(pErrorDescriptionResourceId))
            }
        }


        private fun showErrorDialogWithResourceID(context: Context, pFragmentManager: FragmentManager, pButtonText: String, pErrorString: String, pErrorDescriptionResourceId: Int?, pErrorDescriptionString: String) {
            val builder = AlertDialogFragment.Builder(context).setMessage(pErrorDescriptionString.toString())
                    .setPositiveButton(pButtonText) {
                        val defaultEnglishErrorDescription: String = if (pErrorDescriptionResourceId != null) MECAnalytics.getDefaultString(context, pErrorDescriptionResourceId!!) else pErrorDescriptionString
                        MECAnalytics.trackInAppNotofication(defaultEnglishErrorDescription, "OK") // default english string
                        dismissAlertFragmentDialog(alertDialogFragment, pFragmentManager)
                    }

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
            if (alertDialogFragment != null && isCallingFragmentVisible(fragmentManager))
                alertDialogFragment.dismiss()
        }

        fun showActionDialog(context: Context, positiveBtnTextResourceID: Int, negativeBtnTextResourceID: Int?,
                             pErrorTitleTextResourceId: Int, descriptionTextResourceId: Int, pFragmentManager: FragmentManager, alertListener: AlertListener) {
            val builder = AlertDialogFragment.Builder(context)
            var actionMap = HashMap<String, String>()

            builder.setDialogType(DialogConstants.TYPE_ALERT)

            if (!TextUtils.isEmpty(context.getString(descriptionTextResourceId))) {
                builder.setMessage(descriptionTextResourceId)
                actionMap.put(inappnotification, MECAnalytics.getDefaultString(context, descriptionTextResourceId))
            }

            if (!TextUtils.isEmpty(context.getString(pErrorTitleTextResourceId))) {
                builder.setTitle(pErrorTitleTextResourceId)
            }
            builder.setPositiveButton(positiveBtnTextResourceID
            ) {
                actionMap.put(inappnotificationresponse, MECAnalytics.getDefaultString(context, positiveBtnTextResourceID))
                MECAnalytics.trackMultipleActions(sendData, actionMap)
                alertListener.onPositiveBtnClick()
                dismissAlertFragmentDialog(alertDialogFragment, pFragmentManager)
            }

            if (negativeBtnTextResourceID != null) {
                builder.setNegativeButton(negativeBtnTextResourceID) {
                    actionMap.put(inappnotificationresponse, MECAnalytics.getDefaultString(context, negativeBtnTextResourceID))
                    MECAnalytics.trackMultipleActions(sendData, actionMap)
                    alertListener.onNegativeBtnClick()
                    dismissAlertFragmentDialog(alertDialogFragment, pFragmentManager)
                }
            }
            alertDialogFragment = builder.setCancelable(false).create()
            if (!alertDialogFragment!!.isVisible) {
                alertDialogFragment!!.show(pFragmentManager, ALERT_DIALOG_TAG)
            }
        }

        fun showPositiveActionDialog(context: Context,btnText:String,errorTitle:String,errorDescription:String,fragmentManager: FragmentManager,alertListener: AlertListener){

            val builder = AlertDialogFragment.Builder(context)
            builder.setDialogType(DialogConstants.TYPE_ALERT)
            builder.setTitle(errorTitle)
            builder.setMessage(errorDescription)
            builder.setPositiveButton(btnText) {
                alertListener.onPositiveBtnClick()
                dismissAlertFragmentDialog(alertDialogFragment, fragmentManager)
            }

            alertDialogFragment = builder.setCancelable(false).create()
            if (!alertDialogFragment!!.isVisible) {
                alertDialogFragment!!.show(fragmentManager, ALERT_DIALOG_TAG)
            }
        }




        private fun isCallingFragmentVisible(fragmentManager: FragmentManager?): Boolean {

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

            return ((stockLevelStatus.equals(IN_STOCK, ignoreCase = true) || stockLevelStatus.equals(LOW_STOCK, ignoreCase = true)) && stockLevel > 0)
        }

        fun stockStatus(availability: String): String {
            return when (availability) {
                "YES" -> "available"
                "NO" -> "out of stock"
                else -> ""
            }
        }

        fun getQuantity(carts: ECSShoppingCart): Int {
            val totalItems = carts.totalItems
            var quantity = 0
            if (carts.entries != null) {
                val entries = carts.entries
                if (totalItems != 0 && null != entries) {
                    for (i in entries.indices) {
                        quantity += entries[i].quantity
                    }
                }
            }
            return quantity
        }

        fun isAuthError(ecsError: ECSError?): Boolean {
            var authError: Boolean = false
            with(ecsError!!.errorcode) {
                if (this == ECSErrorEnum.ECSInvalidTokenError.errorCode
                        || this == ECSErrorEnum.ECSinvalid_grant.errorCode
                        || this == ECSErrorEnum.ECSinvalid_client.errorCode
                        || this == ECSErrorEnum.ECSOAuthDetailError.errorCode
                        || this == ECSErrorEnum.ECSOAuthNotCalled.errorCode) {
                    authError = true
                }
            }

            return authError
        }

        @JvmStatic
        fun findGivenAddressInAddressList(ecsAddressID: String, ecsAddressList: List<ECSAddress>): ECSAddress? {

            for (ecsAddress in ecsAddressList) {
                if (ecsAddressID.equals(ecsAddress.id, true)) {
                    return ecsAddress
                }
            }
            return null
        }

        @JvmStatic
        fun tagAndShowError(mecError: MecError?, showDialog: Boolean, aFragmentManager: FragmentManager?, Acontext: Context?) {
            var errorMessage: String = ""
            if (mecError!!.ecsError!!.errorType.equals("No internet connection")) {
                MECAnalytics.trackInformationError(MECAnalytics.getDefaultString(MECDataProvider.context!!, R.string.mec_no_internet))
            } else {
                try {
                    //tag all techinical defect except "No internet connection"
                    var errorString: String = COMPONENT_NAME + ":"
                    if (mecError!!.ecsError!!.errorcode == 1000) {
                        errorString += bazaarVoice + ":"
                    } else if (mecError!!.ecsError!!.errorcode in 5000..5999) {
                        errorString += hybris + ":"
                    } else if (mecError.mECRequestType!!.category.equals(MECRequestType.MEC_FETCH_RETAILER_FOR_CTN)) {
                        errorString += wtb + ":"
                    } else {
                        //
                        errorString += prx + ":"
                    }
                    errorString += mecError.mECRequestType!!.category + ":"// Error_Category

                    if (null == mecError!!.exception!!.message && mecError.ecsError?.errorType.equals("ECS_volley_error", true)) {
                        errorMessage = Acontext!!.getString(R.string.mec_time_out_error)
                    } else if (null != mecError!!.exception!!.message && mecError.ecsError?.errorType.equals("ECS_volley_error", true) && ((mecError!!.exception!!.message!!.contains("java.net.UnknownHostException")) || (mecError!!.exception!!.message!!.contains("I/O error during system call, Software caused connection abort")))) {
                        // No Internet: Information Error
                        //java.net.UnknownHostException: Unable to resolve host "acc.us.pil.shop.philips.com": No address associated with hostname
                        //javax.net.ssl.SSLException: Read error: ssl=0x7d59fa3b48: I/O error during system call, Software caused connection abort
                        MECAnalytics.trackInformationError(MECAnalytics.getDefaultString(MECDataProvider.context!!,R.string.mec_no_internet ))
                        errorMessage = Acontext!!.getString(R.string.mec_no_internet)
                    } else if (mecError!!.ecsError!!.errorcode == ECSErrorEnum.ECSUnsupportedVoucherError.errorCode) {
                        //voucher apply fail:  User error
                        val errorMsg = mecError!!.exception!!.message.toString()
                        errorString +=errorMsg
                        MECAnalytics.trackUserError(errorString)
                        errorMessage=mecError!!.exception!!.message.toString()
                    }else{
                        // Remaining all errors: Technical errors
                        errorMessage = mecError!!.exception!!.message.toString()
                        errorString += errorMessage
                        errorString = errorString + mecError!!.ecsError!!.errorcode + ":"
                        MECAnalytics.trackTechnicalError(errorString)
                    }

                } catch (e: Exception) {
                    MECAnalytics.trackTechnicalError(COMPONENT_NAME + ":" + appError+ ":" + other + e.toString() + ":" + MECAnalyticsConstant.exceptionErrorCode)
                }
            }
            if (showDialog.equals(true)) {
                aFragmentManager?.let { Acontext?.let { it1 -> MECutility.showErrorDialog(it1, it, Acontext!!.getString(R.string.mec_ok), "Error", errorMessage) } }
            }

        }

        fun getImageArrow(mContext: Context): Drawable {
            val width = mContext.resources.getDimension(R.dimen.mec_drop_down_icon_width_size).toInt()
            val height = mContext.resources.getDimension(R.dimen.mec_drop_down_icon_height_size).toInt()
            val imageArrow = VectorDrawableCompat.create(mContext.resources, R.drawable.mec_product_count_drop_down, mContext.theme)
            imageArrow!!.setBounds(0, 0, width, height)
            return imageArrow
        }

        fun getShakeAnimation(): TranslateAnimation {
            val shake = TranslateAnimation(0f, 10f, 0f, 0f)
            shake.duration = 500
            shake.interpolator = CycleInterpolator(7f)
            return shake
        }


        fun getAttributeColor(context: Context, id: Int): Int {
            val numbers: IntArray = intArrayOf(id)
            var typedArray: TypedArray = context!!.obtainStyledAttributes(numbers)
            val colorCodeHighlighted: Int = typedArray.getColor(0, 0)
            typedArray.recycle();
            return colorCodeHighlighted
        }

        fun isExistingUser(): Boolean {

            var storedEmail = "NONE"

            val isEmailKEYExist = MECDataHolder.INSTANCE.appinfra.secureStorage.doesStorageKeyExist(HybrisAuth.KEY_MEC_AUTH_DATA)
            if (isEmailKEYExist) {

                val sse = SecureStorageInterface.SecureStorageError()

                val storedAuthJsonString = MECDataHolder.INSTANCE.appinfra.secureStorage.fetchValueForKey(HybrisAuth.KEY_MEC_AUTH_DATA, sse)
                if(sse!=null && sse.errorMessage!=null && sse.errorCode!=null) {
                    MECAnalytics.trackTechnicalError(COMPONENT_NAME + ":" + appError+ ":" + other + sse.errorMessage + ":" + sse.errorCode)
                }
                //TODO to have a defined type map instead generic
                val map: Map<*, *> = Gson().fromJson(storedAuthJsonString, MutableMap::class.java)
                storedEmail = map[HybrisAuth.KEY_MEC_EMAIL] as String
            }

            return storedEmail == MECDataHolder.INSTANCE.getUserInfo().email
        }



    }

    fun constructShippingAddressDisplayField(ecsAddress: ECSAddress): String {

        var formattedAddress = ""
        val regionDisplayName = if (ecsAddress.region?.name != null) ecsAddress.region?.name else ecsAddress.region?.isocodeShort
        val countryDisplayName = if (ecsAddress.country?.name != null) ecsAddress.country?.name else ecsAddress.country?.isocode
        val houseNumber = ecsAddress.houseNumber
        val line1 = ecsAddress.line1
        val line2 = ecsAddress.line2
        val town = ecsAddress.town
        val postalCode = ecsAddress.postalCode

        formattedAddress = if (!houseNumber.isNullOrEmpty()) "$formattedAddress$houseNumber," else formattedAddress
        formattedAddress = if (!line1.isNullOrEmpty()) "$formattedAddress$line1,\n" else formattedAddress
        formattedAddress = if (!line2.isNullOrEmpty()) "$formattedAddress$line2,\n" else formattedAddress
        formattedAddress = if (!town.isNullOrEmpty()) "$formattedAddress$town,\n" else formattedAddress
        formattedAddress = if (!regionDisplayName.isNullOrEmpty()) "$formattedAddress$regionDisplayName, " else formattedAddress
        formattedAddress = if (!postalCode.isNullOrEmpty()) "$formattedAddress$postalCode, " else formattedAddress
        formattedAddress = if (!countryDisplayName.isNullOrEmpty()) formattedAddress + countryDisplayName else formattedAddress

        return formattedAddress
    }


    fun constructCardDetails(mecPayment: MECPayment): CharSequence? {
        var formattedCardDetail = ""
        val cardType = if (mecPayment.ecsPayment.cardType != null) mecPayment.ecsPayment.cardType.name else ""
        val cardNumber = if (mecPayment.ecsPayment.cardNumber != null) mecPayment.ecsPayment.cardNumber else ""
        formattedCardDetail = "$formattedCardDetail$cardType ${cardNumber.takeLast(8)}"
        return formattedCardDetail
    }

    fun constructCardValidityDetails(mecPayment: MECPayment): CharSequence? {
        var formattedCardValidityDetail = ""
        val cardExpMon = if (mecPayment.ecsPayment.expiryMonth != null) mecPayment.ecsPayment.expiryMonth else ""
        val cardExpYear = if (mecPayment.ecsPayment.expiryYear != null) mecPayment.ecsPayment.expiryYear else ""
        if (cardExpMon == "" || cardExpYear == "") return null
        formattedCardValidityDetail = "$cardExpMon/$cardExpYear"
        return formattedCardValidityDetail
    }


}