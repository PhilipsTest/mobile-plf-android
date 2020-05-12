package com.philips.platform.mec.screens.history.orderDetail

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.philips.cdp.prxclient.datamodels.contacts.ContactPhone
import com.philips.cdp.prxclient.response.ResponseData
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.test.assertNotNull

@PrepareForTest(MECOrderDetailViewModel::class)
@RunWith(PowerMockRunner::class)
class PRXContactsResponseCallbackTest {

    lateinit var pRXContactsResponseCallback :PRXContactsResponseCallback

    @Mock
    lateinit var mecOrderDetailViewModel: MECOrderDetailViewModel

    @Mock
    lateinit var mContactPhone: MutableLiveData<ContactPhone>

    @Mock
    lateinit var mResponseData : ResponseData

    @Mock
    lateinit var mPRXContactsResponseCallback :PRXContactsResponseCallback

    @Mock
    lateinit var mContext: Context

    @Mock
    lateinit var productCategory : String


    @Before
    fun setUp() {
        mecOrderDetailViewModel.contactPhone = mContactPhone
        pRXContactsResponseCallback= PRXContactsResponseCallback(mecOrderDetailViewModel)
    }

    @Test
    fun onResponseError() {
    }

    @Test
    fun onResponseSuccess() {
        pRXContactsResponseCallback.onResponseSuccess(mResponseData)
        assertNotNull(mecOrderDetailViewModel.contactPhone)

    }

    @Test
    fun fetchContactsTest() {
        assertNotNull(mecOrderDetailViewModel.fetchContacts(mContext,productCategory ))
    }
}