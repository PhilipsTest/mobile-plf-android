/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.integration

import android.content.Context

import com.philips.platform.uappframework.uappinput.UappSettings

/**
 * MECSettings class is used to initialize basic settings for MEC. Right now MEC doesn’t have any settings to be initialized. So only default initialization of MECSettings is required to be passed while creating MECInterface object.
 * @since 1.0.0
 */
class MECSettings
/**
 * @param applicationContext
 */
(applicationContext: Context) : UappSettings(applicationContext)
