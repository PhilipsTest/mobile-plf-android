/**
 * (C) Konin klijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.mec.integration

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.config.ECSConfig
import com.philips.cdp.di.mec.analytics.MECAnalytics
import com.philips.cdp.di.mec.common.MECLauncherActivity
import com.philips.cdp.di.mec.integration.serviceDiscovery.ServiceDiscoveryMapListener
import com.philips.cdp.di.mec.screens.reviews.BazaarVoiceHelper
import com.philips.cdp.di.mec.utils.MECConstant
import com.philips.cdp.di.mec.utils.MECDataHolder
import com.philips.platform.appinfra.AppInfra
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface.OnGetServiceUrlMapListener
import com.philips.platform.uappframework.launcher.ActivityLauncher
import com.philips.platform.uappframework.launcher.FragmentLauncher
import com.philips.platform.uappframework.launcher.UiLauncher
import java.util.*


internal open class MECHandler(private val mMECDependencies: MECDependencies, private val mMECSetting: MECSettings, private val mUiLauncher: UiLauncher, private val mLaunchInput: MECLaunchInput) {
    private var appInfra: AppInfra? = null
    private var listOfServiceId: ArrayList<String>? = null
    lateinit var serviceUrlMapListener: OnGetServiceUrlMapListener


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
        /*  appInfra = mMECDependencies.appInfra as AppInfra
          val configInterface = appInfra!!.configInterface
          val configError = AppConfigurationInterface.AppConfigurationError()
          val propositionID = configInterface.getPropertyForKey("propositionid", "MEC", configError)
          var propertyForKey = ""
          if(propositionID!=null) {
              propertyForKey = propositionID as String
          }
          val ecsServices = ECSServices(propertyForKey, appInfra!!)
          MECDataHolder.INSTANCE.eCSServices = ecsServices // singleton
          MECDataHolder.INSTANCE.appinfra = appInfra as AppInfra
          MECDataHolder.INSTANCE.propositionId = propertyForKey*/
        MECDataHolder.INSTANCE.mecBannerEnabler = mLaunchInput.mecBannerConfigurator
        MECDataHolder.INSTANCE.hybrisEnabled = mLaunchInput.supportsHybris
        MECDataHolder.INSTANCE.retailerEnabled = mLaunchInput.supportsRetailer
        MECDataHolder.INSTANCE.mecBazaarVoiceInput = mLaunchInput.mecBazaarVoiceInput!!
        MECDataHolder.INSTANCE.voucherCode = mLaunchInput.voucherCode
        MECDataHolder.INSTANCE.maxCartCount = mLaunchInput.maxCartCount
        MECDataHolder.INSTANCE.mecBazaarVoiceInput = mLaunchInput.mecBazaarVoiceInput

        if (MECDataHolder.INSTANCE.bvClient == null) {
            val bazarvoiceSDK = BazaarVoiceHelper().getBazarvoiceClient(mMECSetting.context.applicationContext as Application)
            MECDataHolder.INSTANCE.bvClient = bazarvoiceSDK
        }

        MECDataHolder.INSTANCE.blackListedRetailers = mLaunchInput.blackListedRetailerNames

        MECAnalytics.initMECAnalytics(mMECDependencies)

        getUrl()

        // get config

        MECDataHolder.INSTANCE.eCSServices.configureECSToGetConfiguration(object : ECSCallback<ECSConfig, Exception> {

            override fun onResponse(config: ECSConfig?) {


                //set config data to singleton
                MECDataHolder.INSTANCE.config = config

                if (MECDataHolder.INSTANCE.hybrisEnabled) {
                    MECDataHolder.INSTANCE.hybrisEnabled = config?.isHybris ?: return
                }

                MECDataHolder.INSTANCE.locale = config!!.locale
                MECAnalytics.setCurrencyString(MECDataHolder.INSTANCE.locale)
                if (null != config!!.rootCategory) {
                    MECDataHolder.INSTANCE.rootCategory = config!!.rootCategory
                }

                // Launch fragment or activity
                if (mUiLauncher is ActivityLauncher) {
                    launchMECasActivity(MECDataHolder.INSTANCE.hybrisEnabled)
                } else {
                    launchMECasFragment(MECDataHolder.INSTANCE.hybrisEnabled)
                }
            }

            override fun onFailure(error: Exception?, ecsError: ECSError?) {
                Log.e("MEC", "Config failed : Can't Launch MEC")
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


        MECDataHolder.INSTANCE.setActionBarListener(fragmentLauncher.actionbarListener, mLaunchInput.mecListener!!)
        val transaction = fragmentLauncher.fragmentActivity.supportFragmentManager.beginTransaction()
        transaction.replace(fragmentLauncher.parentContainerResourceID, mecLandingFragment!!, mecLandingFragment.getFragmentTag())
        transaction.addToBackStack(mecLandingFragment.getFragmentTag())
        transaction.commitAllowingStateLoss()
    }

}
