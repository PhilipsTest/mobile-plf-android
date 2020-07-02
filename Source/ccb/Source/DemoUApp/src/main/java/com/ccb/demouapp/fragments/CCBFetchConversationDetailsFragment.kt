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

    var conversationId: TextView? = null
    var token: TextView? = null
    var streamUrl: TextView? = null
    var expiresIn: TextView? = null
    var referenceId: TextView? = null
    var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fetch_conversation_details_fragment, container, false)
        conversationId = rootView.findViewById(R.id.tvConversationId)
        token = rootView.findViewById(R.id.tvToken)
        streamUrl = rootView.findViewById(R.id.tvStreamUrl)
        expiresIn = rootView.findViewById(R.id.tvExpiresIn)
        referenceId = rootView.findViewById(R.id.tvReferenceId)
        progressBar = rootView.findViewById(R.id.progressBar)

        executeRequest()
        return rootView
    }

    private fun executeRequest() {
        progressBar?.visibility = View.VISIBLE
        CCBManager.getCCBSessionHandlerInterface().startConversation { ccbConversation, ccbError ->
            if (ccbConversation != null) {
                progressBar?.visibility = View.GONE
                conversationId?.text = "conversationId: " + ccbConversation.conversationId
                token?.text = "token: " + ccbConversation.token
                expiresIn?.text = "expires_in: " + ccbConversation.expires_in
                streamUrl?.text = "streamUrl: " + ccbConversation.streamUrl
                referenceId?.text = "referenceGrammarId: " + ccbConversation.referenceGrammarId
            } else if (ccbError != null) {
                progressBar?.visibility = View.GONE
                conversationId?.text = "Request Failed"
            }
        }

    }
}
