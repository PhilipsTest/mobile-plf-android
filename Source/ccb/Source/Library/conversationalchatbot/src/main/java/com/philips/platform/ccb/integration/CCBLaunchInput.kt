/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccb.integration

import com.philips.platform.uappframework.uappinput.UappLaunchInput

class CCBLaunchInput : UappLaunchInput() {
    var ccbDeviceCapabilityInterface: CCBDeviceCapabilityInterface? = null
}