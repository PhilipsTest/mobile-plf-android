package com.philips.platform.mec.screens.history.orderDetail

import android.content.Context
import com.philips.cdp.prxclient.PRXDependencies
import com.philips.cdp.prxclient.RequestManager
import com.philips.cdp.prxclient.request.CustomerCareContactsRequest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(CustomerCareContactsRequest::class, RequestManager::class, PRXDependencies::class)
@RunWith(PowerMockRunner::class)
class MECOrderDetailRepositoryTest {

    lateinit var mECOrderDetailRepository: MECOrderDetailRepository

    @Mock
    lateinit var  mPRXContactsResponseCallback: PRXContactsResponseCallback


    @Mock
    lateinit var mContext : Context

    @Mock
    lateinit var mCustomerCareContactsRequest :CustomerCareContactsRequest

    @Mock
     var mRequestManager =RequestManager()



    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mECOrderDetailRepository=MECOrderDetailRepository()
    }

    @Test
    fun fetchContacts() {
       mECOrderDetailRepository.fetchContacts(mContext,"",mPRXContactsResponseCallback)
       Mockito.verify( mRequestManager).executeRequest(mCustomerCareContactsRequest,mPRXContactsResponseCallback)
    }
}