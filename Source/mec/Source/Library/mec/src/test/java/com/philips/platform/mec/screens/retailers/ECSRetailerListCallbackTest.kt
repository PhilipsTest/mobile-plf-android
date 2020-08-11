package com.philips.platform.mec.screens.retailers

import androidx.lifecycle.MutableLiveData
import com.philips.platform.ecs.microService.model.retailer.*
import com.philips.platform.mec.common.MecError
import com.philips.platform.mec.utils.MECDataHolder
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
import kotlin.test.assertFalse


@PrepareForTest(ECSRetailerViewModel::class)
@RunWith(PowerMockRunner::class)
class ECSRetailerListCallbackTest {


    lateinit var ecsRetailerListCallback: ECSRetailerListCallback

    @Mock
    lateinit var ecsRetailerViewModelMock: ECSRetailerViewModel


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        ecsRetailerListCallback = ECSRetailerListCallback(ecsRetailerViewModelMock)
    }


    @Test(expected = NullPointerException::class)
    fun onResponse() {
        ecsRetailerListCallback.onResponse(createECSRetailerList())
        assertNotNull(ecsRetailerViewModelMock.ecsRetailerList)
    }

    @Test
    fun shouldRemovePhilipsStoreIfHybrisIsOn() {

        MECDataHolder.INSTANCE.hybrisEnabled = true
        val ecsRetailer = ECSRetailer(null, null, "Y", null, null, null, null, null, null, null)
        val ecsRetailer1 = ECSRetailer(null, null, "N", null, null, null, null, null, null, null)
        val ecsRetailer2 = ECSRetailer(null, null, "N", null, null, null, null, null, null, null)


        val list = ArrayList<ECSRetailer>()
        list.add(ecsRetailer)
        list.add(ecsRetailer1)
        list.add(ecsRetailer2)


        val ECSRetailers = ECSRetailers(list)
        val OnlineStoresForProduct = OnlineStoresForProduct(ECSRetailers, null, null, null)
        val Wrbresults = Wrbresults(null, null, null, OnlineStoresForProduct, null, null, null, null)

        val ecsRetailerList = ECSRetailerList(Wrbresults)

        val removePhilipsStoreForHybris = ecsRetailerListCallback.removePhilipsStoreForHybris(ecsRetailerList)
        val retailers = removePhilipsStoreForHybris?.getRetailers()
        val contains = retailers?.contains(ecsRetailer) ?:false

        assertFalse(contains)

    }

    private fun createECSRetailerList(): ECSRetailerList {
        val ecsRetailer = ECSRetailer(null, null, "Y", null, null, null, null, null, null, null)
        val ecsRetailer1 = ECSRetailer(null, null, "N", null, null, null, null, null, null, null)
        val ecsRetailer2 = ECSRetailer(null, null, "N", null, null, null, null, null, null, null)


        val list = ArrayList<ECSRetailer>()
        list.add(ecsRetailer)
        list.add(ecsRetailer1)
        list.add(ecsRetailer2)


        val ECSRetailers = ECSRetailers(list)
        val OnlineStoresForProduct = OnlineStoresForProduct(ECSRetailers, null, null, null)
        val Wrbresults = Wrbresults(null, null, null, OnlineStoresForProduct, null, null, null, null)

        val ecsRetailerList = ECSRetailerList(Wrbresults)
        return ecsRetailerList
    }

}