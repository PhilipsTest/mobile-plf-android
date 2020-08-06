package com.philips.platform.mec.screens.history.orderDetail

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
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.utils.MECConstant
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog
import java.net.MalformedURLException
import java.net.URL

class MECOrderDetailTrackUrlFragment : MecBaseFragment() {
    override fun getFragmentTag(): String {
        return "MECOrderDetailTrackUrlFragment"
    }

    private var mWebView: WebView? = null
    private var mUrl: String? = null
    private var mProgressBar: FrameLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val group = inflater.inflate(R.layout.mec_web_fragment, container, false) as ViewGroup
        mProgressBar = group.findViewById(R.id.mec_progress_bar_container) as FrameLayout
        showProgressBar(mProgressBar)
        mUrl = arguments?.getString(MECConstant.MEC_TRACK_ORDER_URL)
        initializeWebView(group)
        return group
    }

    override fun onResume() {
        super.onResume()
        setTitleAndBackButtonVisibility(getString(R.string.mec_track_order), true)
        mWebView!!.onResume()
    }

    override fun onStop() {
        super.onStop()
        dismissProgressBar(mProgressBar)
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

        mWebView!!.loadUrl(mUrl!!)
    }

    private fun shouldHandleError(errorCode: Int): Boolean {
        return (errorCode == WebViewClient.ERROR_CONNECT
                || errorCode == WebViewClient.ERROR_BAD_URL
                || errorCode == WebViewClient.ERROR_TIMEOUT
                || errorCode == WebViewClient.ERROR_HOST_LOOKUP)
    }


    private fun isParameterizedURL(url: String): Boolean {

        try {
            val urlString = URL(url)
            return urlString.query != null
        } catch (e: MalformedURLException) {
            MECLog.e("TRACK_URL", "Exception Occurs : " + e.message)
        } catch (e: Exception) {
            MECLog.e("TRACK_URL", "Exception Occurs : " + e.message)
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