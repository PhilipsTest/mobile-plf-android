package com.philips.cdp.registration.ads.listener

import android.view.View

interface NativeAdListener {

    fun onAdLoaded()
    fun onAdLoadFailed(exception: Exception)

    interface CallToActionListener {
        fun onCallToActionClicked(view: View)
    }
}