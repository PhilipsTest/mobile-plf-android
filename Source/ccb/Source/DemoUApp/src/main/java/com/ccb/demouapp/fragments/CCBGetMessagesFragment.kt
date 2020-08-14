/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.ccb.demouapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.philips.platform.ccb.errors.CCBError
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.model.CCBMessage
import com.philips.platform.ccbdemouapp.R

class CCBGetMessagesFragment : CCBBaseFragment() {

    var textView: TextView? = null
    var progressBar : ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.get_messages_fragment, container, false)
        textView = rootView.findViewById(R.id.textView)
        progressBar = rootView.findViewById(R.id.progressBar)

        executeRequest()
        return rootView
    }

    private fun executeRequest() {
        progressBar?.visibility = View.VISIBLE
        CCBManager.getCCBConversationHandlerInterface().getAllMessages { ccbMessage: CCBMessage?, ccbError: CCBError? ->
            if(ccbMessage !=null){
                progressBar?.visibility = View.GONE
                textView?.text = getJsonString(ccbMessage)
            }else if(ccbError != null){
                progressBar?.visibility = View.GONE
                textView?.text = "Request Failed"
            }
        }
    }

    fun getJsonString(ccbMessage: CCBMessage): String {
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonTutPretty: String = gsonPretty.toJson(ccbMessage)
        return jsonTutPretty
    }
}
