package com.philips.platform.ccb.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.philips.platform.ccb.R
import com.philips.platform.ccb.constant.CCBUrlBuilder
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.model.CCBUser
import kotlinx.android.synthetic.main.fragment_ccb.view.*

/**
 * A simple [Fragment] subclass.
 */
class CCBFragment : Fragment(){
    private val TAG: String? = CCBFragment::class.java.simpleName
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_ccb, container, false)

        progressBar = rootView.progressBar
        progressBar.visibility = View.VISIBLE

        connectChatBot()

        return rootView
    }

    private fun connectChatBot(){
        val ccbUser = CCBUser(CCBUrlBuilder.SECRET_KEY, "EMS", "")
        CCBManager.getCCBSessionHandlerInterface().authenticateUser(ccbUser){ success, ccbError ->
            if(success){
                Log.i(TAG,"Authenticated success!!")
                startConverssation()
            }

            if(ccbError != null){
                progressBar.visibility = View.GONE
                Log.i(TAG,"Authentication failed!!")
            }
        }
    }

    private fun startConverssation(){
        CCBManager.getCCBSessionHandlerInterface().startConversation{
            ccbConversation, ccbError ->
            if(ccbConversation != null){
                Log.i(TAG,"Conversation started!!")
                progressBar.visibility = View.GONE
                showConverstationalFragment()
            }

            if(ccbError!= null){
                Log.i(TAG,"Conversation Failed!!")
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showConverstationalFragment() {
        if (null!=activity && !activity!!.isFinishing) {

            val transaction = activity!!.supportFragmentManager.beginTransaction()

            transaction.replace(id, CCBConversationalFragment(), TAG)
            transaction.addToBackStack(null)
            transaction.commitAllowingStateLoss()
        }
    }
}
