/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.retailers


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
import android.widget.FrameLayout
import com.philips.platform.mec.R
import com.philips.platform.mec.analytics.MECAnalyticPageNames.retailerListPage
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.analytics.MECAnalyticsConstant
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder
import java.net.MalformedURLException
import java.net.URL

class WebBuyFromRetailersFragment : MecBaseFragment() {
    override fun getFragmentTag(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    companion object {
        val TAG:String="WebBuyFromRetailersFragment"

         fun getFragmentTag(): String {
            return "WebBuyFromRetailersFragment"
        }

    }

    private var mWebView: WebView? = null
    private var mUrl: String? = null
    private var isPhilipsShop = false
    private var mProgressBar : FrameLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val group = inflater.inflate(R.layout.mec_web_fragment, container, false) as ViewGroup
        mProgressBar = group.findViewById(R.id.mec_progress_bar_container) as FrameLayout
        showProgressBar(mProgressBar)
        mUrl = getArguments()!!.getString(MECConstant.MEC_BUY_URL)
        isPhilipsShop = arguments!!.getBoolean(MECConstant.MEC_IS_PHILIPS_SHOP)
        initializeWebView(group)
        com.philips.platform.mec.analytics.MECAnalytics.trackPage(retailerListPage)
        return group
    }

    override fun onResume() {
        super.onResume()
        setCartIconVisibility(false)
        val title = getArguments()!!.getString(MECConstant.MEC_STORE_NAME)
        if (title != null) {
            setTitleAndBackButtonVisibility(title, true)
        }
        mWebView!!.onResume()
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
                dismissProgressBar(mProgressBar)
            }

            override fun onPageCommitVisible(view: WebView?, url: String) {
                var tagUrl = url
                if (isPhilipsShop) {
                    tagUrl = getPhilipsFormattedUrl(url)
                }
                com.philips.platform.mec.analytics.MECAnalytics.trackAction(com.philips.platform.mec.analytics.MECAnalyticsConstant.sendData, com.philips.platform.mec.analytics.MECAnalyticsConstant.exitLinkNameKey, tagUrl)
                super.onPageCommitVisible(view, url)
            }

//            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
//                var tagUrl = url
//                if (isPhilipsShop) {
//                    tagUrl = getPhilipsFormattedUrl(url)
//                }
//                MECAnalytics.trackAction(MECAnalyticsConstant.sendData, MECAnalyticsConstant.exitLinkNameKey, tagUrl)
//                super.onPageStarted(view, url, favicon)
//            }

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
                dismissProgressBar(mProgressBar)
                //hideProgressBar()
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

    fun getPhilipsFormattedUrl(url: String): String {

        val appName = MECDataHolder.INSTANCE.appinfra.appIdentity.appName
        val localeTag = MECDataHolder.INSTANCE.appinfra.internationalization.uiLocaleString
        val builder = Uri.Builder().appendQueryParameter("origin", String.format(com.philips.platform.mec.analytics.MECAnalyticsConstant.exitLinkParameter, localeTag, appName, appName))

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

    private fun shouldHandleError(errorCode: Int): Boolean {
        return (errorCode == WebViewClient.ERROR_CONNECT
                || errorCode == WebViewClient.ERROR_BAD_URL
                || errorCode == WebViewClient.ERROR_TIMEOUT
                || errorCode == WebViewClient.ERROR_HOST_LOOKUP)
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
