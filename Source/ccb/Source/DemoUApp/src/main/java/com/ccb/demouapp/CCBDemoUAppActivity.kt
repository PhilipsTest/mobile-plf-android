/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.ccb.demouapp

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.ccb.demouapp.fragments.CCBDemoUAppHomeFragment
import com.ccb.demouapp.integration.ThemeHelper
import com.philips.platform.ccbdemouapp.R
import com.philips.platform.uappframework.listener.ActionBarListener
import com.philips.platform.uid.thememanager.*
import io.github.inflationx.viewpump.ViewPumpContextWrapper

class CCBDemoUAppActivity : AppCompatActivity(), ActionBarListener {
    private val DEFAULT_THEME = R.style.Theme_DLS_Blue_UltraLight
    private lateinit var mBackImage: ImageView
    private lateinit var mTitleTextView: TextView
    private var fragment: CCBDemoUAppHomeFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ccbdemo_uapp)

        fragment = CCBDemoUAppHomeFragment()
        actionBar()

        setFragment(fragment)

    }

    protected fun setFragment(fragment: Fragment?) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container, fragment!!)
        fragmentTransaction.commit()
    }


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }

    private fun actionBar() {
        val frameLayout = findViewById<FrameLayout>(R.id.ccb_demo_app_header_back_button_framelayout)
        frameLayout.setOnClickListener { onBackPressed() }
        mBackImage = findViewById(R.id.ccb_demo_app_iv_header_back_button)
        val mBackDrawable: Drawable? = VectorDrawableCompat.create(resources, R.drawable.ccb_back_arrow, theme)
        mBackImage.setBackground(mBackDrawable)
        mTitleTextView = findViewById(R.id.ccb_demo_app_header_title)
        fragment!!.setTextView(mTitleTextView)
        setTitle("Messages")
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        mTitleTextView.text = title
    }

    private fun initTheme() {
        UIDHelper.injectCalligraphyFonts()
        val themeResourceID: Int = ThemeHelper(this).themeResourceId
        var themeIndex = themeResourceID
        if (themeIndex <= 0) {
            themeIndex = DEFAULT_THEME
        }
        theme.applyStyle(themeIndex, true)
        UIDHelper.init(ThemeConfiguration(this, ContentColor.ULTRA_LIGHT, NavigationColor.BRIGHT, AccentRange.ORANGE))
    }

    override fun updateActionBar(resId: Int, enableBackKey: Boolean) {
    }

    override fun updateActionBar(resString: String?, enableBackKey: Boolean) {
    }
}
