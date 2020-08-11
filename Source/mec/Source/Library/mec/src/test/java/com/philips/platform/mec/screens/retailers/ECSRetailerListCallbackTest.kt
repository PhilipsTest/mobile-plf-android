package com.philips.platform.mec.screens.retailers

import androidx.lifecycle.MutableLiveData
import com.philips.platform.ecs.microService.model.retailer.ECSRetailer
import com.philips.platform.ecs.microService.model.retailer.ECSRetailerList
import com.philips.platform.mec.common.MecError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner


@PrepareForTest(ECSRetailerViewModel::class)
@RunWith(PowerMockRunner::class)
class ECSRetailerListCallbackTest {


    lateinit var ecsRetailerListCallback: ECSRetailerListCallback

    @Mock
    lateinit var ecsRetailerViewModelMock: ECSRetailerViewModel

    @Mock
    lateinit var ecsRetailerList: ECSRetailerList

    @Mock
    lateinit var mutableLiveDataMock: MutableLiveData<ECSRetailerList>

    @Mock
    lateinit var mutableLiveDataMecErrorMock: MutableLiveData<MecError>

    @Mock
    lateinit var ecsRetailer: ECSRetailer

    @Mock
    lateinit var mecErrorMutableLiveData: MutableLiveData<MecError>


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ecsRetailerListCallback = ECSRetailerListCallback(ecsRetailerViewModelMock)
    }


    @Test(expected = NullPointerException::class)
    fun onResponse() {
        Mockito.`when`(ecsRetailerList.getRetailers()).thenReturn(listOf(ecsRetailer))
        ecsRetailerListCallback.onResponse(ecsRetailerList)
        assertNotNull(ecsRetailerViewModelMock.ecsRetailerList)
    }

    @Test
    fun shouldRemovePhilipsStoreIfHybrisIsOn() {

        val ecsRetailerList = ECSRetailerList()

        val ecsRetailer = ECSRetailer(null,null,"Y",null,null,null,null,null,null,null)


        val list = ArrayList<ECSRetailer>()
        list.add(ecsRetailer)

        assertEquals(0, ecsRetailerListCallback.removePhilipsStoreForHybris(ecsRetailerList))
    }

}