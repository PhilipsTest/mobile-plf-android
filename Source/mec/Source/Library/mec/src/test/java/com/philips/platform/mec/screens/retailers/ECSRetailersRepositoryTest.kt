package com.philips.platform.mec.screens.retailers

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(ECSRetailerListCallback::class, ECSRetailerViewModel::class)
@RunWith(PowerMockRunner::class)
class ECSRetailersRepositoryTest {


    lateinit var  ecsRetailersRepository: ECSRetailersRepository

    @Mock
    lateinit var microServiceMock : com.philips.platform.ecs.microService.ECSServices


    @Mock
    lateinit var  ecsRetailerViewModelMock: ECSRetailerViewModel

    @Mock
    lateinit var  ecsServicesMock: com.philips.platform.ecs.ECSServices

    @Mock
    lateinit var eCSRetailerListCallback : ECSRetailerListCallback

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        Mockito.`when`(ecsServicesMock.microService).thenReturn(microServiceMock)
        ecsRetailersRepository = ECSRetailersRepository(ecsServicesMock,ecsRetailerViewModelMock)
        ecsRetailersRepository.eCSRetailerListCallback = eCSRetailerListCallback
    }


    @Test(expected = NullPointerException::class)
    fun getRetailersShouldCallFetchRetailers() {

        ecsRetailersRepository.getRetailers("CTN")
        Mockito.verify(ecsServicesMock.microService).fetchRetailers("CTN",eCSRetailerListCallback)

    }
}