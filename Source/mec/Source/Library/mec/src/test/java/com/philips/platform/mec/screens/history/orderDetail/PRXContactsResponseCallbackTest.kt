package com.philips.platform.mec.screens.history.orderDetail

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.philips.cdp.prxclient.datamodels.cdls.CDLSDataModel
import com.philips.cdp.prxclient.datamodels.cdls.ContactPhone
import com.philips.cdp.prxclient.datamodels.cdls.Data
import com.philips.cdp.prxclient.error.PrxError
import com.philips.cdp.prxclient.response.ResponseData
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.error.ECSErrorEnum
import com.philips.platform.mec.common.MECRequestType
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


    lateinit var mecOrderDetailViewModel: MECOrderDetailViewModel

    @Mock
    lateinit var mecOrderDetailViewModelMOCK: MECOrderDetailViewModel


    @Mock
    lateinit var mResponseData:  ResponseData


    lateinit var mMecError : MutableLiveData<MecError>


    @Mock
    lateinit var mContext: Context

    @Mock
    lateinit var productCategory: String


    lateinit var mPrxError: PrxError



    @Before
    fun setUp() {
//        MockitoAnnotations.initMocks(this)
        var contactPhoneLiveData = MutableLiveData<ContactPhone>()
        var contactPhone = ContactPhone()
        contactPhone.openingHoursSaturday="open hours"
        contactPhone.openingHoursWeekdays="week day"
        contactPhone.phoneNumber="12334"

        contactPhoneLiveData = MutableLiveData(contactPhone)
        var phoneList: MutableList<ContactPhone> = mutableListOf()
        phoneList.add(contactPhone)
        var mData = Data()
        mData.phone=phoneList
        var mCDLSDataModel = CDLSDataModel()
        mCDLSDataModel.data=mData

        mecOrderDetailViewModel = MECOrderDetailViewModel()
        mecOrderDetailViewModel.contactPhone = contactPhoneLiveData


        val exception = Exception("exception")
        var ecsError= ECSError(5999, ECSErrorEnum.ECSsomethingWentWrong.toString())
        val mecError = MecError(exception, ecsError, MECRequestType.MEC_FETCH_ORDER_HISTORY)
        mMecError = MutableLiveData(mecError)
        mecOrderDetailViewModel.mecError=mMecError


        pRXContactsResponseCallback=PRXContactsResponseCallback(mecOrderDetailViewModel)
        mPrxError= PrxError("ERROR",1)

    }

    @Test
    fun onResponseError() {
        pRXContactsResponseCallback.onResponseError( mPrxError)
        assertNotNull(mecOrderDetailViewModel.mecError)
    }

    @Test
    fun onResponseSuccess() {
      /* pRXContactsResponseCallback=PRXContactsResponseCallback(mecOrderDetailViewModelMOCK)
        mecOrderDetailViewModelMOCK.contactPhone=contactPhoneMock
        Mockito.verify(pRXContactsResponseCallback).onResponseSuccess(mResponseData)*/
        //todo
    }

    @Test
    fun fetchContactsTest() {
        assertNotNull(mecOrderDetailViewModelMOCK.fetchContacts(mContext, productCategory))
    }

    @Test
    fun MecOrderDeatilViewModel(){
        assertNotNull(mecOrderDetailViewModel.mecError)
        assertNotNull(mecOrderDetailViewModel.contactPhone)
        assertNotNull(mecOrderDetailViewModel.mecOrderDetailRepository)
        assertNotNull(mecOrderDetailViewModel.prxContactsResponseCallback)
    }
}