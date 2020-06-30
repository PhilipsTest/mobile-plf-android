/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.integration

import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.config.ECSConfig
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.auth.HybrisAuth
import com.philips.platform.mec.common.MECLauncherActivity
import com.philips.platform.mec.integration.serviceDiscovery.ServiceDiscoveryMapListener
import com.philips.platform.mec.screens.reviews.BazaarVoiceHelper
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.uappframework.launcher.ActivityLauncher
import com.philips.platform.uappframework.launcher.FragmentLauncher
import com.philips.platform.uappframework.launcher.UiLauncher
import java.util.*


class MECHandler{

    internal var serviceUrlMapListener = ServiceDiscoveryMapListener()
    private val TAG: String = MECHandler::class.java.simpleName


    companion object {
        val IAP_PRIVACY_URL = "iap.privacyPolicy"
        val IAP_FAQ_URL = "iap.faq"
        val IAP_TERMS_URL = "iap.termOfUse"
    }

    // mBundle.putSerializable(MECConstant.FLOW_INPUT,mLaunchInput.getFlowConfigurator());
    fun getBundle(mLaunchInput: MECLaunchInput): Bundle {
        val mBundle = Bundle()
        mBundle.putSerializable(MECConstant.FLOW_INPUT, mLaunchInput.flowConfigurator)
        mBundle.putStringArrayList(MECConstant.CATEGORISED_PRODUCT_CTNS, mLaunchInput.flowConfigurator?.productCTNs)
        return mBundle
    }


    fun launchMEC(mMECSetting: MECSettings,mUiLauncher: UiLauncher,mLaunchInput: MECLaunchInput) {
        MECDataHolder.INSTANCE.mecBannerEnabler = mLaunchInput.mecBannerConfigurator
        MECDataHolder.INSTANCE.hybrisEnabled = mLaunchInput.supportsHybris
        MECDataHolder.INSTANCE.retailerEnabled = mLaunchInput.supportsRetailer
        MECDataHolder.INSTANCE.mecBazaarVoiceInput = mLaunchInput.mecBazaarVoiceInput
        MECDataHolder.INSTANCE.voucherCode = mLaunchInput.voucherCode
        MECDataHolder.INSTANCE.maxCartCount = mLaunchInput.maxCartCount
        MECDataHolder.INSTANCE.mecOrderFlowCompletion = mLaunchInput.mecOrderFlowCompletion

        if (MECDataHolder.INSTANCE.bvClient == null && mMECSetting.context!=null) {
            val bazarvoiceSDK = BazaarVoiceHelper().getBazaarVoiceClient(mMECSetting.context.applicationContext as Application)
            MECDataHolder.INSTANCE.bvClient = bazarvoiceSDK
        }

        MECDataHolder.INSTANCE.blackListedRetailers = mLaunchInput.blackListedRetailerNames
        getUrl()
        MECDataHolder.INSTANCE.eCSServices.configureECSToGetConfiguration(getConfigCallback(mUiLauncher, mMECSetting, mLaunchInput))
    }

    internal fun getConfigCallback(mUiLauncher: UiLauncher, mMECSetting: MECSettings, mLaunchInput: MECLaunchInput): ECSCallback<ECSConfig, Exception> {
        return object : ECSCallback<ECSConfig, Exception> {

            override fun onResponse(config: ECSConfig) {

                MECDataHolder.INSTANCE.config = config
                if (MECDataHolder.INSTANCE.hybrisEnabled) {
                    MECDataHolder.INSTANCE.hybrisEnabled = config.isHybris
                }
                MECDataHolder.INSTANCE.locale = config.locale
                MECAnalytics.setCurrencyString(config.locale)
                MECDataHolder.INSTANCE.rootCategory = config.rootCategory
                if (mUiLauncher is ActivityLauncher) {
                    launchMECasActivity(MECDataHolder.INSTANCE.hybrisEnabled, mMECSetting, mUiLauncher, mLaunchInput)
                } else {
                    launchMECasFragment(MECDataHolder.INSTANCE.hybrisEnabled, mUiLauncher, mLaunchInput)
                }
            }

            override fun onFailure(error: Exception?, ecsError: ECSError?) {
                MECLog.d(HybrisAuth.TAG, "hybrisRefreshAuthentication : onFailure : " + error!!.message + " ECS Error code " + ecsError!!.errorcode + "ECS Error type " + ecsError!!.errorType)
            }
        }
    }

    internal fun getUrl() {
        val listOfServiceId = mutableListOf<String>()
        listOfServiceId.add(IAP_PRIVACY_URL)
        listOfServiceId.add(IAP_FAQ_URL)
        listOfServiceId.add(IAP_TERMS_URL)
        MECDataHolder.INSTANCE.appinfra.serviceDiscovery?.getServicesWithCountryPreference(listOfServiceId as ArrayList<String>, serviceUrlMapListener, null)
    }


    private fun launchMECasActivity(isHybris: Boolean , mecSettings: MECSettings,mUiLauncher: UiLauncher,mLaunchInput: MECLaunchInput) {
        val intent = Intent(mecSettings.context, MECLauncherActivity::class.java)
        val activityLauncher = mUiLauncher as ActivityLauncher
        val bundle = getBundle(mLaunchInput)
        bundle.putSerializable(MECConstant.KEY_FLOW_CONFIGURATION, mLaunchInput.flowConfigurator)
        bundle.putBoolean(MECConstant.KEY_IS_HYBRIS, isHybris)
        bundle.putInt(MECConstant.MEC_KEY_ACTIVITY_THEME, activityLauncher.uiKitTheme)
        intent.putExtras(bundle)
        mecSettings.context.startActivity(intent)
    }

    private fun launchMECasFragment(hybris: Boolean,mUiLauncher: UiLauncher,mLaunchInput: MECLaunchInput) {

        val bundle = getBundle(mLaunchInput)

        val mecLandingFragment = FragmentSelector().getLandingFragment(hybris, mLaunchInput.flowConfigurator, bundle)
        val fragmentLauncher = mUiLauncher as FragmentLauncher
        bundle.putInt("fragment_container", fragmentLauncher.parentContainerResourceID) // frame_layout for fragment
        mecLandingFragment.arguments = bundle
        MECDataHolder.INSTANCE.mecLaunchingFragmentName=mecLandingFragment.getFragmentTag()

        MECDataHolder.INSTANCE.setUpdateCartListener(fragmentLauncher.actionbarListener, mLaunchInput.mecCartUpdateListener)
        val transaction = fragmentLauncher.fragmentActivity.supportFragmentManager.beginTransaction()
        transaction.replace(fragmentLauncher.parentContainerResourceID, mecLandingFragment, mecLandingFragment.getFragmentTag())
        transaction.addToBackStack(mecLandingFragment.getFragmentTag())
        transaction.commitAllowingStateLoss()
    }

}
