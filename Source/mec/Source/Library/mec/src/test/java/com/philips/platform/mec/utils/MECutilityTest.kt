package com.philips.platform.mec.utils

import com.philips.platform.ecs.model.address.ECSAddress
import com.philips.platform.ecs.model.payment.ECSPayment
import com.philips.platform.mec.integration.MECFlowConfigurator
import com.philips.platform.mec.screens.payment.MECPayment
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(MECutility::class)
@RunWith(PowerMockRunner::class)
class MECutilityTest {


    var mECutility = MECutility();
    val ecsAddress = ECSAddress()
    val ecPayment= MECPayment(ECSPayment())

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }


    @Test
    fun `sample test`(){
        ecsAddress.line1 = "stree 1 "
        ecsAddress.line2 = "street 2"
        var str =  mECutility.constructShippingAddressDisplayField(ecsAddress)
        print(str);
    }

    @Test
    fun `sample test 2`()
    {

        var str =  mECutility.constructCardDetails(ecPayment)
        print(str);
    }


}