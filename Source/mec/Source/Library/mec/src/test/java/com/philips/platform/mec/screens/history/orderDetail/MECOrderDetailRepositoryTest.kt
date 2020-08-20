package com.philips.platform.mec.screens.history.orderDetail

import android.content.Context
import com.philips.cdp.prxclient.PRXDependencies
import com.philips.cdp.prxclient.RequestManager
import com.philips.cdp.prxclient.request.CDLSRequest
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(CDLSRequest::class, RequestManager::class, PRXDependencies::class,PRXContactsResponseCallback::class)
@RunWith(PowerMockRunner::class)
class MECOrderDetailRepositoryTest {

    lateinit var mECOrderDetailRepository: MECOrderDetailRepository

    @Mock
    lateinit var  mPRXContactsResponseCallback: PRXContactsResponseCallback


    @Mock
    lateinit var mContextMock : Context



    @Mock
    lateinit var mRequestManagerMock  : RequestManager



    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mECOrderDetailRepository=MECOrderDetailRepository()
        mECOrderDetailRepository.mRequestManager = mRequestManagerMock
        MECDataHolder.INSTANCE.appinfra = appinfraMock
    }


    @Mock
    lateinit var appinfraMock: AppInfraInterface

    @Mock
    lateinit var CDLSRequestMock: CDLSRequest

    @Test
    fun fetchContacts() {
  //     mECOrderDetailRepository.fetchContacts(mContextMock,CDLSRequestMock,mPRXContactsResponseCallback)
   //    Mockito.verify( mRequestManagerMock).executeRequest(CDLSRequestMock,mPRXContactsResponseCallback)
    }
}