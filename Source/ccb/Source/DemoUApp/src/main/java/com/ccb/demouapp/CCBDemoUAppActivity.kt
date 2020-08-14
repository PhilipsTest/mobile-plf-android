/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

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
