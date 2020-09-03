package com.philips.platform.mec.screens.orderSummary

import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner
//TODO write fresh test cases from json
@RunWith(PowerMockRunner::class)
class MECOrderSummaryServicesTest {
    private lateinit var mecOrderSummaryServices: MECOrderSummaryServices

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mecOrderSummaryServices = MECOrderSummaryServices()
    }
}