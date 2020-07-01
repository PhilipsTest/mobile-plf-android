package com.ccb.demouapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.philips.platform.ccb.integration.ccbCallback
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.model.CCBConversation
import com.philips.platform.ccbdemouapp.R

class CCBFetchConversationDetailsFragment : CCBBaseFragment() {

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
        CCBManager.INSTANCE.getccbSessionHandlerInterface().startConversation(object : ccbCallback<CCBConversation,Exception> {
            override fun onResponse(response: CCBConversation) {
                progressBar?.visibility = View.GONE
                textView?.text = response.token
            }

            override fun onFailure(error: Exception) {
                textView?.text = "fail"
            }
        })


        }
}
