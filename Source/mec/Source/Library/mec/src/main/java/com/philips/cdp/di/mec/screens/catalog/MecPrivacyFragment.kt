package com.philips.cdp.di.mec.screens.catalog


import android.annotation.TargetApi
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.philips.cdp.di.ecs.constants.NetworkConstants
import com.philips.cdp.di.mec.R
import com.philips.cdp.di.mec.analytics.MECAnalytics
import com.philips.cdp.di.mec.analytics.MECAnalyticsConstant

import com.philips.cdp.di.mec.screens.MecBaseFragment
import com.philips.cdp.di.mec.utils.MECConstant
import com.philips.cdp.di.mec.utils.MECDataHolder
import java.net.MalformedURLException
import java.net.URL

class MecPrivacyFragment : MecBaseFragment() {

    override fun getFragmentTag(): String {
        return "MecPrivacyFragment"
    }

    companion object {
        val TAG:String="MecPrivacyFragment"
    }

    private var mWebView: WebView? = null
    private var mUrl: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val group = inflater.inflate(R.layout.mec_web_fragment, container, false) as ViewGroup
        createCustomProgressBar(group, MEDIUM)
        mUrl = arguments!!.getString(MECConstant.MEC_PRIVACY_URL)
        initializeWebView(group)
        return group
    }

    override fun onResume() {
        super.onResume()
        setTitleAndBackButtonVisibility(getString(R.string.mec_privacy), true)
        mWebView!!.onResume()
    }

    override fun onStop() {
        super.onStop()
        hideProgressBar()
    }

    //TODO take this code to a separate class

    internal fun initializeWebView(group: View) {
        mWebView = group.findViewById<View>(R.id.mec_webView) as WebView
        mWebView!!.settings.javaScriptEnabled = true
        mWebView!!.settings.domStorageEnabled = true
        mWebView!!.settings.setAppCacheEnabled(true)
        mWebView!!.settings.loadsImagesAutomatically = true
        mWebView!!.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        mWebView!!.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                hideProgressBar()

            }

            override fun onPageCommitVisible(view: WebView?, url: String) {
                var tagUrl = url
                    tagUrl = getPhilipsFormattedUrl(url)
                MECAnalytics.trackAction(MECAnalyticsConstant.sendData, MECAnalyticsConstant.exitLinkNameKey, tagUrl)
                super.onPageCommitVisible(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                if (url == null) return false

                try {
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        view.loadUrl(url)
                        return true
                    } else {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                        return true
                    }
                } catch (e: Exception) {
                    // Avoid crash due to not installed app which can handle the specific url scheme
                    return false
                }

            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                super.onReceivedSslError(view, handler, error)
                handler.proceed() // Ignore SSL certificate errors
            }

            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                hideProgressBar()
            }

            @TargetApi(Build.VERSION_CODES.M)
            override fun onReceivedError(view: WebView, req: WebResourceRequest, rerr: WebResourceError?) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                if (rerr != null && shouldHandleError(rerr.errorCode)) {
                    if (isVisible()) {
                        onReceivedError(view, rerr.errorCode, rerr.description.toString(), req.url.toString())
                    }
                }
            }
        }

        mWebView!!.loadUrl(mUrl)
    }

    private fun shouldHandleError(errorCode: Int): Boolean {
        return (errorCode == WebViewClient.ERROR_CONNECT
                || errorCode == WebViewClient.ERROR_BAD_URL
                || errorCode == WebViewClient.ERROR_TIMEOUT
                || errorCode == WebViewClient.ERROR_HOST_LOOKUP)
    }

    fun getPhilipsFormattedUrl(url: String): String {

        val appName = MECDataHolder.INSTANCE.appinfra.appIdentity.appName
        val localeTag = MECDataHolder.INSTANCE.appinfra.internationalization.uiLocaleString
        val builder = Uri.Builder().appendQueryParameter("origin", String.format(MECAnalyticsConstant.exitLinkParameter, localeTag, appName, appName))

        return if (isParameterizedURL(url)) {
            url + "&" + builder.toString().replace("?", "")
        } else {
            url + builder.toString()
        }
    }

    private fun isParameterizedURL(url: String): Boolean {

        try {
            val urlString = URL(url)
            return urlString.query != null
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }


    override fun handleBackEvent(): Boolean {
        if (mWebView?.canGoBack()!!) {
            mWebView?.goBack()
            return true
        }
        super.handleBackEvent()
        return false
    }


}

