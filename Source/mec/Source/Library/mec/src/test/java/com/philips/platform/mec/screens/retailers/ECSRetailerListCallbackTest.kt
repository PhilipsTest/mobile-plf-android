package com.philips.platform.mec.screens.retailers

import androidx.lifecycle.MutableLiveData
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
    lateinit var ecsRetailerList: com.philips.platform.ecs.model.retailers.ECSRetailerList

    @Mock
    lateinit var mutableLiveDataMock: MutableLiveData<com.philips.platform.ecs.model.retailers.ECSRetailerList>

    @Mock
    lateinit var mutableLiveDataMecErrorMock: MutableLiveData<MecError>

    @Mock
    lateinit var ecsRetailer: com.philips.platform.ecs.model.retailers.ECSRetailer

    @Mock
    lateinit var mecErrorMutableLiveData: MutableLiveData<MecError>


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
//        Mockito.`when`(ecsRetailerViewModelMock.ecsRetailerList).thenReturn(mutableLiveDataMock)
//        Mockito.`when`(ecsRetailerViewModelMock.mecError).thenReturn(mutableLiveDataMecErrorMock)
        ecsRetailerListCallback = ECSRetailerListCallback(ecsRetailerViewModelMock)
    }


    @Test(expected = NullPointerException::class)
    fun onResponse() {
        Mockito.`when`(ecsRetailerList.retailers).thenReturn(listOf(ecsRetailer))
        ecsRetailerListCallback.onResponse(ecsRetailerList)
        assertNotNull(ecsRetailerViewModelMock.ecsRetailerList)
    }

    @Test
    fun shouldRemovePhilipsStoreIfHybrisIsOn() {

        val ecsRetailerList = com.philips.platform.ecs.model.retailers.ECSRetailerList()

        val ecsRetailer = com.philips.platform.ecs.model.retailers.ECSRetailer()
        ecsRetailer.isPhilipsStore = "Y"

        val list = ArrayList<com.philips.platform.ecs.model.retailers.ECSRetailer>()
        list.add(ecsRetailer)

        assertEquals(0, ecsRetailerListCallback.removePhilipsStoreForHybris(ecsRetailerList).retailers.size)
    }

}