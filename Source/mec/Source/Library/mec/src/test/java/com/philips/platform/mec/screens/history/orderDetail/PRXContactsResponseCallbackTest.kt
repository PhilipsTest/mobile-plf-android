package com.philips.platform.mec.screens.history.orderDetail

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.philips.cdp.prxclient.datamodels.cdls.ContactPhone
import com.philips.cdp.prxclient.datamodels.cdls.CDLSDataModel
import com.philips.cdp.prxclient.error.PrxError
import com.philips.cdp.prxclient.response.ResponseData
import com.philips.platform.mec.common.MecError
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

    lateinit var pRXContactsResponseCallback: PRXContactsResponseCallback


    lateinit var mecOrderDetailViewModelMock: MECOrderDetailViewModel


    @Mock
    lateinit var mResponseData: ResponseData


    var errorLiveDataMock = MutableLiveData<MecError>()

    @Mock
    var mContactsModel = CDLSDataModel()


    @Mock
    lateinit var mContext: Context

    @Mock
    lateinit var productCategory: String

    @Mock
    lateinit var mPrxError: PrxError


    @Before
    fun setUp() {
        // MockitoAnnotations.initMocks(this)
        var contactPhone = MutableLiveData<ContactPhone>()

        var phoneList: MutableList<MutableLiveData<ContactPhone>> = mutableListOf()
        phoneList.add(contactPhone)

        mecOrderDetailViewModelMock = MECOrderDetailViewModel()

        mecOrderDetailViewModelMock.contactPhone = contactPhone
        mecOrderDetailViewModelMock.mecError = errorLiveDataMock




        mResponseData = mContactsModel as CDLSDataModel
        // mecOrderDetailViewModel.contactPhone  = mContactsModel.value.data.phone.get(0)
        pRXContactsResponseCallback = PRXContactsResponseCallback(mecOrderDetailViewModelMock)
    }

    @Test
    fun onResponseError() {
        pRXContactsResponseCallback.onResponseError(mPrxError)
        assertNotNull(mecOrderDetailViewModelMock.mecError)
    }

    @Test
    fun onResponseSuccess() {
        //  pRXContactsResponseCallback.onResponseSuccess(mResponseData)
        assertNotNull(mecOrderDetailViewModelMock.contactPhone)

    }

    @Test
    fun fetchContactsTest() {
        assertNotNull(mecOrderDetailViewModelMock.fetchContacts(mContext, productCategory))
    }
}