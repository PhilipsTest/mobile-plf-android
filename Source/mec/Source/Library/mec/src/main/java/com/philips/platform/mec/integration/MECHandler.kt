/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.integration

import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface.OnGetServiceUrlMapListener
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


internal open class MECHandler(private val mMECDependencies: MECDependencies, private val mMECSetting: MECSettings, private val mUiLauncher: UiLauncher, private val mLaunchInput: MECLaunchInput) {
    private var appInfra: AppInfra? = null
    private var listOfServiceId: ArrayList<String>? = null
    lateinit var serviceUrlMapListener: OnGetServiceUrlMapListener
    private val TAG: String = MECHandler::class.java.simpleName


    companion object {
        val IAP_PRIVACY_URL = "iap.privacyPolicy"
        val IAP_FAQ_URL = "iap.faq"
        val IAP_TERMS_URL = "iap.termOfUse"
    }

    // mBundle.putSerializable(MECConstant.FLOW_INPUT,mLaunchInput.getFlowConfigurator());
    fun getBundle(): Bundle {
        val mBundle = Bundle()
        if (mLaunchInput.flowConfigurator != null) {

            mBundle.putSerializable(MECConstant.FLOW_INPUT, mLaunchInput.flowConfigurator)

            if (mLaunchInput.flowConfigurator!!.productCTNs != null) {
                mBundle.putStringArrayList(MECConstant.CATEGORISED_PRODUCT_CTNS,
                        mLaunchInput.flowConfigurator!!.productCTNs)
            }
        }
        return mBundle
    }


    fun launchMEC() {
        MECDataHolder.INSTANCE.mecBannerEnabler = mLaunchInput.mecBannerConfigurator
        MECDataHolder.INSTANCE.hybrisEnabled = mLaunchInput.supportsHybris
        MECDataHolder.INSTANCE.retailerEnabled = mLaunchInput.supportsRetailer
        MECDataHolder.INSTANCE.mecBazaarVoiceInput = mLaunchInput.mecBazaarVoiceInput
        MECDataHolder.INSTANCE.voucherCode = mLaunchInput.voucherCode
        MECDataHolder.INSTANCE.maxCartCount = mLaunchInput.maxCartCount
        MECDataHolder.INSTANCE.mecOrderFlowCompletion = mLaunchInput.mecOrderFlowCompletion

        if (MECDataHolder.INSTANCE.bvClient == null) {
            val bazarvoiceSDK = BazaarVoiceHelper().getBazaarVoiceClient(mMECSetting.context.applicationContext as Application)
            MECDataHolder.INSTANCE.bvClient = bazarvoiceSDK
        }

        MECDataHolder.INSTANCE.blackListedRetailers = mLaunchInput.blackListedRetailerNames



        getUrl()

        // get config

        MECDataHolder.INSTANCE.eCSServices.configureECSToGetConfiguration(object : com.philips.platform.ecs.integration.ECSCallback<com.philips.platform.ecs.model.config.ECSConfig, Exception> {

            override fun onResponse(config: com.philips.platform.ecs.model.config.ECSConfig) {


                //set config data to singleton
                MECDataHolder.INSTANCE.config = config

                if (MECDataHolder.INSTANCE.hybrisEnabled) {
                    MECDataHolder.INSTANCE.hybrisEnabled = config.isHybris
                }

                MECDataHolder.INSTANCE.locale = config.locale
                MECAnalytics.setCurrencyString(MECDataHolder.INSTANCE.locale)
                MECDataHolder.INSTANCE.rootCategory = config.rootCategory


                // Launch fragment or activity
                if (mUiLauncher is ActivityLauncher) {
                    launchMECasActivity(MECDataHolder.INSTANCE.hybrisEnabled)
                } else {
                    launchMECasFragment(MECDataHolder.INSTANCE.hybrisEnabled)
                }
            }

            override fun onFailure(error: Exception?, ecsError: com.philips.platform.ecs.error.ECSError?) {
                MECLog.d(HybrisAuth.TAG, "hybrisRefreshAuthentication : onFailure : " + error!!.message + " ECS Error code " + ecsError!!.errorcode + "ECS Error type " + ecsError!!.errorType)
            }
        })


    }

    //TODO
    private fun getUrl() {

        listOfServiceId = ArrayList()
        listOfServiceId!!.add(IAP_PRIVACY_URL)
        listOfServiceId!!.add(IAP_FAQ_URL)
        listOfServiceId!!.add(IAP_TERMS_URL)
        serviceUrlMapListener = ServiceDiscoveryMapListener()
        MECDataHolder.INSTANCE.appinfra.serviceDiscovery.getServicesWithCountryPreference(listOfServiceId, serviceUrlMapListener, null)
    }


    protected fun launchMECasActivity(isHybris: Boolean) {
        val intent = Intent(mMECSetting.context, MECLauncherActivity::class.java)
        val activityLauncher = mUiLauncher as ActivityLauncher
        val bundle = getBundle()
        bundle.putSerializable(MECConstant.KEY_FLOW_CONFIGURATION, mLaunchInput.flowConfigurator)
        bundle.putBoolean(MECConstant.KEY_IS_HYBRIS, isHybris)
        bundle.putInt(MECConstant.MEC_KEY_ACTIVITY_THEME, activityLauncher.uiKitTheme)
        intent.putExtras(bundle)
        mMECSetting.context.startActivity(intent)

    }

    private fun launchMECasFragment(hybris: Boolean) {

        val bundle = getBundle()

        val mecLandingFragment = FragmentSelector().getLandingFragment(hybris, mLaunchInput.flowConfigurator!!, bundle)
        val fragmentLauncher = mUiLauncher as FragmentLauncher
        bundle.putInt("fragment_container", fragmentLauncher.parentContainerResourceID) // frame_layout for fragment
        mecLandingFragment?.arguments = bundle


        MECDataHolder.INSTANCE.setUpdateCartListener(fragmentLauncher.actionbarListener, mLaunchInput.mecCartUpdateListener)
        val transaction = fragmentLauncher.fragmentActivity.supportFragmentManager.beginTransaction()
        transaction.replace(fragmentLauncher.parentContainerResourceID, mecLandingFragment!!, mecLandingFragment.getFragmentTag())
        transaction.addToBackStack(mecLandingFragment.getFragmentTag())
        transaction.commitAllowingStateLoss()
    }

}
