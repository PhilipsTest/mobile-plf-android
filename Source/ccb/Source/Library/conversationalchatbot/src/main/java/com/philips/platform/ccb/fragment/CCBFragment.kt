package com.philips.platform.ccb.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.philips.platform.ccb.R
import com.philips.platform.ccb.constant.CCBUrlBuilder
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.model.CCBUser
import com.philips.platform.ccb.util.CCBLog
import kotlinx.android.synthetic.main.ccb_fragment.view.*


/**
 * A simple [Fragment] subclass.
 */
class CCBFragment : Fragment(){
    private val TAG: String? = CCBFragment::class.java.simpleName
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.ccb_fragment, container, false)

        progressBar = rootView.ccb_progressBar
        progressBar.visibility = View.VISIBLE

        connectChatBot()

        return rootView
    }

    private fun connectChatBot(){
        val ccbUser = CCBUser(CCBUrlBuilder.HIDDEN_KNOCK, "", "")
        CCBManager.getCCBSessionHandlerInterface().authenticateUser(ccbUser){ success, ccbError ->
            if(success){
                CCBLog.d(TAG, "Authentication success")
                startConverssation()
            }

            if(ccbError != null){
                progressBar.visibility = View.GONE
                CCBLog.d(TAG, "Authentication failed")
            }
        }
    }

    private fun startConverssation(){
        val ccbUser = CCBUser(CCBUrlBuilder.HIDDEN_KNOCK, "", "")
        CCBManager.getCCBSessionHandlerInterface().startConversation(ccbUser){
            ccbConversation, ccbError ->
            if(ccbConversation != null){
                CCBLog.d(TAG, "Conversation started")
                progressBar.visibility = View.GONE
                showConverstationalFragment()
            }

            if(ccbError!= null){
                CCBLog.d(TAG, "Conversation Failed")
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showConverstationalFragment() {
        if (null!=activity && !activity!!.isFinishing) {

            val transaction = activity!!.supportFragmentManager.beginTransaction()

            transaction.replace(id, CCBConversationalFragment(), TAG)
            fragmentManager?.popBackStack()
            transaction.addToBackStack(null)
            transaction.commitAllowingStateLoss()
        }
    }
}
