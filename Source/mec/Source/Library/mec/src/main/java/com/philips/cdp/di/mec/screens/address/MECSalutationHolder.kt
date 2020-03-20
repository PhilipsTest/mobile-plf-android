package com.philips.cdp.di.mec.screens.address

import android.content.Context
import com.philips.cdp.di.ecs.util.ECSConfiguration
import com.philips.cdp.di.mec.R

open class MECSalutationHolder(context: Context?) {

    val DROP_DOWN_DATA = arrayOf(context?.getString(R.string.mec_mr), context?.getString(R.string.mec_mrs))

    val locale = ECSConfiguration.INSTANCE.country
}