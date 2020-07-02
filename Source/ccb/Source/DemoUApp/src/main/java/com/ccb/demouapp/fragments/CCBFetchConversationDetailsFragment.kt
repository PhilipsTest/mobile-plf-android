package com.ccb.demouapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.google.gson.Gson
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccbdemouapp.R


class CCBFetchConversationDetailsFragment : CCBBaseFragment() {

    var textView: TextView? = null
    var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fetch_conversation_details_fragment, container, false)
        textView = rootView.findViewById(R.id.textView)
        progressBar = rootView.findViewById(R.id.progressBar)

        executeRequest()
        return rootView
    }

    private fun executeRequest() {
        progressBar?.visibility = View.VISIBLE
        CCBManager.getCCBSessionHandlerInterface().startConversation { ccbConversation, ccbError ->
            if (ccbConversation != null) {
                progressBar?.visibility = View.GONE
                textView?.text = Gson().toJson(ccbConversation)
            } else if (ccbError != null) {
                progressBar?.visibility = View.GONE
                textView?.text = "Request Failed"
            }
        }

    }
}
