package com.philips.platform.ccb.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.philips.platform.ccb.R
import com.philips.platform.ccb.constant.CCBUrlBuilder
import com.philips.platform.ccb.directline.CCBAuthHandler
import com.philips.platform.ccb.directline.CCBAzureSessionHandler
import com.philips.platform.ccb.integration.ccbCallback
import com.philips.platform.ccb.listeners.CCBAuthListener
import com.philips.platform.ccb.manager.CCBManager
import com.philips.platform.ccb.model.CCBUser
import com.philips.platform.pif.DataInterface.USR.enums.Error
import com.philips.platform.pif.DataInterface.USR.listeners.UpdateUserDetailsHandler
import kotlinx.android.synthetic.main.fragment_ccb.*

/**
 * A simple [Fragment] subclass.
 */
class CCBFragment : Fragment(){
    var textView: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_ccb, container, false)
        textView = rootView.findViewById(R.id.tvResponse)

        executeRequest()
        return rootView
    }

    private fun executeRequest() {
        val ccbUser = CCBUser(CCBUrlBuilder.SECRET_KEY,"","")
       CCBAzureSessionHandler().authenticateUser(ccbUser,object: ccbCallback<Boolean,Exception>{
           override fun onResponse(response: Boolean) {
               textView?.text = response.toString()
           }

           override fun onFailure(error: Exception) {
               TODO("Not yet implemented")
           }

       })


    }


}
