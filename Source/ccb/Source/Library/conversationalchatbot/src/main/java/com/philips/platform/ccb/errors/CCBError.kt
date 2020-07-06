package com.philips.platform.ccb.errors

class CCBError (var errorCode: Int,var errorDesc: String?){
    val errCode get() = errorCode
    val errDesc get() = errorDesc
}