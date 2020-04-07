/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.payment

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout

import com.philips.platform.mec.R
import com.philips.platform.mec.screens.MecBaseFragment

abstract class MECWebFragment : MecBaseFragment() {

    override fun getFragmentTag(): String {
        return "MECWebFragment"
    }

    protected lateinit var mWebView: WebView
    private var mUrl: String? = null
    private var mProgressBar: FrameLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewGroup = inflater.inflate(R.layout.mec_web_fragment, container, false) as ViewGroup
        mProgressBar = viewGroup.findViewById(R.id.mec_progress_bar_container) as FrameLayout
        showProgressBar(mProgressBar)

        mWebView = viewGroup.findViewById<View>(R.id.mec_webView) as WebView
        mWebView.webViewClient = IAPWebViewClient()
        mWebView.settings.javaScriptEnabled = true
        mWebView.settings.saveFormData = false
        mWebView.settings.javaScriptCanOpenWindowsAutomatically = true
        mWebView.settings.setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 9_3 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13E233 Safari/601.1")
        mWebView.settings.domStorageEnabled = true
        mWebView.settings.setAppCacheEnabled(true)
        mWebView.settings.loadsImagesAutomatically = true
        mWebView.settings.useWideViewPort = true

        mUrl = getWebUrl()
        return viewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mWebView.loadUrl(mUrl)
    }

    override fun onResume() {
        super.onResume()
        setCartIconVisibility(false)
        mWebView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mWebView.onPause()
    }


    override fun onDestroy() {
        super.onDestroy()
        dismissProgressBar(mProgressBar)
    }

    abstract fun getWebUrl(): String

    open fun shouldOverrideUrlLoading(url: String): Boolean {
        return false
    }

    private fun shouldHandleError(errorCode: Int): Boolean {
        return (errorCode == WebViewClient.ERROR_CONNECT
                || errorCode == WebViewClient.ERROR_BAD_URL
                || errorCode == WebViewClient.ERROR_TIMEOUT
                || errorCode == WebViewClient.ERROR_HOST_LOOKUP)
    }

    private inner class IAPWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return this@MECWebFragment.shouldOverrideUrlLoading(url)
        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return this@MECWebFragment.shouldOverrideUrlLoading(request.url.toString())
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            super.onReceivedSslError(view, handler, error)
            handler.proceed() // Ignore SSL certificate errors
        }

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            dismissProgressBar(mProgressBar)
        }

        @TargetApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView, req: WebResourceRequest, rerr: WebResourceError?) {
            // Redirect to deprecated method, so you can use it in all SDK versions
            if (rerr != null && shouldHandleError(rerr.errorCode)) {
                if (isVisible) {
                    onReceivedError(view, rerr.errorCode, rerr.description.toString(), req.url.toString())
                }
            }
        }

        override fun onPageFinished(view: WebView, url: String) {
            dismissProgressBar(mProgressBar)
            super.onPageFinished(view, url)
        }
    }


}