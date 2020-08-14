/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.ccb.demouapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import com.philips.platform.ccb.errors.CCBError
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccbdemouapp.R
import kotlinx.android.synthetic.main.post_message_fragment.view.*


class CCBPostMessageFragment : CCBBaseFragment() {

    var textView: TextView? = null
    var progressBar : ProgressBar? = null
    var layout : LinearLayout? = null
    var btn : Button? = null
    var text : EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.post_message_fragment, container, false)
        textView = rootView.findViewById(R.id.textView)
        progressBar = rootView.findViewById(R.id.progressBar)
        layout = rootView.findViewById(R.id.layout_button)
        btn = rootView.findViewById(R.id.btnSend)
        text = rootView.findViewById(R.id.text)
        rootView.btnSend.setOnClickListener{executeRequest()}
        return rootView
    }

    private fun executeRequest() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
        progressBar?.visibility = View.VISIBLE
        CCBManager.getCCBConversationHandlerInterface().postMessage(text?.text.toString()) { success: Boolean, ccbError: CCBError? ->
            if(success){
                progressBar?.visibility = View.GONE
                layout?.visibility = View.GONE
                textView?.visibility = View.VISIBLE
                textView?.text = "You said: " + text?.text.toString()
            }else if(ccbError != null){
                progressBar?.visibility = View.GONE
                textView?.text = "Request Failed"
            }
        }
    }
}
