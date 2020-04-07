/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.integration.serviceDiscovery

import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService
import com.philips.platform.mec.integration.MECHandler.Companion.IAP_FAQ_URL
import com.philips.platform.mec.integration.MECHandler.Companion.IAP_PRIVACY_URL
import com.philips.platform.mec.integration.MECHandler.Companion.IAP_TERMS_URL
import com.philips.platform.mec.utils.MECDataHolder
import java.util.*

class ServiceDiscoveryMapListener : ServiceDiscoveryInterface.OnGetServiceUrlMapListener {


    override fun onError(error: ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES?, message: String?) {
    }


    override fun onSuccess(urlMap: MutableMap<String, ServiceDiscoveryService>?) {
        val collection = urlMap?.values


        val list = ArrayList<ServiceDiscoveryService>()
        if (collection != null) {
            list.addAll(collection)
        }

        val discoveryServicePrivacyUrl = urlMap?.get(IAP_PRIVACY_URL)
        val discoveryServiceFaqUrl = urlMap?.get(IAP_FAQ_URL)
        val discoveryServiceTermsUrl = urlMap?.get(IAP_TERMS_URL)

        val privacyUrl = discoveryServicePrivacyUrl?.configUrls
        val faqUrl = discoveryServiceFaqUrl?.configUrls
        val termsUrl = discoveryServiceTermsUrl?.configUrls
        MECDataHolder.INSTANCE.setPrivacyPolicyUrls(privacyUrl, faqUrl, termsUrl)
    }
}