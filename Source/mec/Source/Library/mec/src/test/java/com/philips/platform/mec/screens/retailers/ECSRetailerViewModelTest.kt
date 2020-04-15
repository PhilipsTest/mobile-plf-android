package com.philips.platform.mec.screens.retailers

import com.philips.cdp.di.ecs.ECSServices
import com.philips.platform.mec.screens.detail.EcsProductDetailViewModel
import com.philips.platform.mec.utils.MECDataHolder
import org.junit.Test

import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(ECSRetailersRepository::class)
@RunWith(PowerMockRunner::class)
class ECSRetailerViewModelTest {


    lateinit var ecsRetailerViewModel: ECSRetailerViewModel

    @Mock
    lateinit var ecsServices: ECSServices

    @Mock
    lateinit var ecsRetailersRepository: ECSRetailersRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        MECDataHolder.INSTANCE.eCSServices = ecsServices
        ecsRetailerViewModel = ECSRetailerViewModel()

        ecsRetailerViewModel.ecsServices = ecsServices
        ecsRetailerViewModel.ecsRetailersRepository = ecsRetailersRepository
    }

    @Test(expected = NullPointerException::class)
    fun getRetailersShouldCallRetailersForCTN() {

        ecsRetailerViewModel.getRetailers("CTN")
        Mockito.verify(ecsRetailersRepository).getRetailers("CTN")
    }

}