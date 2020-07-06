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
import com.philips.platform.ccb.errors.CCBError
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
        val ccbUser = CCBUser(CCBUrlBuilder.HIDDEN_KNOCK, "", "")
        CCBManager.getCCBSessionHandlerInterface().authenticateUser(ccbUser) { success: Boolean, ccbError: CCBError? ->
            if(success){
                progressBar?.visibility = View.GONE
                textView?.text = "Request Success"
            }else if(ccbError != null){
                progressBar?.visibility = View.GONE
                textView?.text = "Request Failed"
            }
        }
    }
}
