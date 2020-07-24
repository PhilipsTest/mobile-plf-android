package com.ccb.demouapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ccb.demouapp.fragments.CCBDemoUAppHomeFragment
import com.philips.platform.ccbdemouapp.R

class CCBDemoUAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ccbdemo_uapp)

        supportFragmentManager.beginTransaction().add(R.id.fragment_container,CCBDemoUAppHomeFragment()).commit()

    }
}
