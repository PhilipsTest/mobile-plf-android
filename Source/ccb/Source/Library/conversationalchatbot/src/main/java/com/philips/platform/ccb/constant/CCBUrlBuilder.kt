/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccb.constant

class CCBUrlBuilder {

    companion object {
        const val BASE_URL = "https://directline.botframework.com/v3/directline"
        const val AUTHENTICATION = "/tokens/generate"
        const val REFRESH_TOKEN = "/tokens/refresh"
        const val START_CONVERSATION = "/conversations"
        const val SUFIX_CONVERSATION = "/conversations/"
        const val SUFIX_ACTIVITIES = "/activities"
        const val HIDDEN_KNOCK = "g8Ye_oNrqs4.n37wCTf_kd2In2X6kXNP1apzryHDZ_1OGR5olkQpRM4"
    }

}