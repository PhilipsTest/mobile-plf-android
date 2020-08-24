/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.philips.platform.ccb.errors

class CCBError (var errorCode: Int,var errorDesc: String?){
    val errCode get() = errorCode
    val errDesc get() = errorDesc
}