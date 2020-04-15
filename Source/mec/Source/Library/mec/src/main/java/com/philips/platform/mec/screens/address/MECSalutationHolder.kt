/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.address

import android.content.Context
import com.philips.cdp.di.ecs.util.ECSConfiguration
import com.philips.platform.mec.R

open class MECSalutationHolder(context: Context?) {

    val DROP_DOWN_DATA = arrayOf(context?.getString(R.string.mec_mr), context?.getString(R.string.mec_mrs))

    val locale = ECSConfiguration.INSTANCE.country
}