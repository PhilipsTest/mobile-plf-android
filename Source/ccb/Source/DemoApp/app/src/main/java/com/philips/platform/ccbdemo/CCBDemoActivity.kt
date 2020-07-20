package com.philips.platform.ccbdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import com.ccb.demouapp.integration.CCBDemoUAppDependencies
import com.ccb.demouapp.integration.CCBDemoUAppInterface
import com.ccb.demouapp.integration.CCBDemoUAppSettings
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.uappframework.launcher.ActivityLauncher
import com.philips.platform.uappframework.uappinput.UappLaunchInput
import com.philips.platform.uid.utils.UIDActivity
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_ccbdemo.*


class CCBDemoActivity : UIDActivity() {

    private lateinit var appInfraInterface: AppInfraInterface
    private lateinit var demoAppInterface: CCBDemoUAppInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ccbdemo)

        val ccbDemoApplication = applicationContext as CCBDemoApplication;
        appInfraInterface = ccbDemoApplication.getAppInfra();

        launchButton.setOnClickListener { launchDemoUApp() }

        initDemoUApp()

       // startActivity(Intent(applicationContext,MainActivity::class.java))
    }

    private fun initDemoUApp() {
        val demoUAppDependencies = CCBDemoUAppDependencies(appInfraInterface)
        val demoUAppSettings = CCBDemoUAppSettings(applicationContext)
        demoAppInterface = CCBDemoUAppInterface()
        demoAppInterface.init(demoUAppDependencies, demoUAppSettings)
    }

    private fun launchDemoUApp() {
        demoAppInterface.launch(ActivityLauncher(this, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED, null, 0, null), UappLaunchInput())
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }
}
