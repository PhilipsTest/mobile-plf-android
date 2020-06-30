package com.philips.platform.ccb.integration

interface ccbCallback<E,R> {

    fun onResponse(response:E)

    fun onFailure(error: R)

}