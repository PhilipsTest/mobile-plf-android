package com.ccb.demouapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.philips.platform.ccb.constant.CCBUrlBuilder
import com.philips.platform.ccb.integration.ccbCallback
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.model.CCBUser
import com.philips.platform.ccbdemouapp.R
import kotlinx.android.synthetic.main.fetch_auth_details_fragment.*

class CCBFetchAuthDetailsFragment : CCBBaseFragment() {

    var textView: TextView? = null
    var progressBar : ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fetch_auth_details_fragment, container, false)
        textView = rootView.findViewById(R.id.textView)
        progressBar = rootView.findViewById(R.id.progressBar)

        executeRequest()
        return rootView
    }

    private fun executeRequest() {
        progressBar?.visibility = View.VISIBLE
        val ccbUser = CCBUser(CCBUrlBuilder.SECRET_KEY, "", "")
        CCBManager.INSTANCE.getccbSessionHandlerInterface().authenticateUser(ccbUser, object : ccbCallback<Boolean, Exception> {
            override fun onResponse(response: Boolean) {
                progressBar?.visibility = View.GONE
                textView?.text = response.toString()
            }

            override fun onFailure(error: Exception) {
                TODO("Not yet implemented")
            }

        })

    }
}
