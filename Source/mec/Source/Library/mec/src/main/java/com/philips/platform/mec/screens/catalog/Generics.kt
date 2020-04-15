/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.catalog

import androidx.lifecycle.MutableLiveData

fun mutableList(mutableLiveData: MutableLiveData<MutableList<Any>>): MutableList<Any> {
    if (mutableLiveData.value.isNullOrEmpty()) mutableLiveData.value = mutableListOf()
    return mutableLiveData.value!!
}