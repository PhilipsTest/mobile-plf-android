package com.philips.platform.mec.integration

import android.os.Bundle
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.detail.MECLandingProductDetailsFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.test.assertNotNull
import kotlin.test.assertSame

@PrepareForTest(MECFlowConfigurator::class)
@RunWith(PowerMockRunner::class)
class FragmentSelectorTest {


    lateinit var fragmentSelector: FragmentSelector

    lateinit var mecFlowConfigurator: MECFlowConfigurator

    @Mock
    lateinit var fragment: MecBaseFragment

    @Mock
    lateinit var bundle: Bundle

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        fragmentSelector = FragmentSelector()
        mecFlowConfigurator = MECFlowConfigurator()
    }

    @Test
    fun `getLandingFragment of MEC_PRODUCT_LIST_VIEW`() {
        mecFlowConfigurator.landingView = MECFlowConfigurator.MECLandingView.MEC_PRODUCT_LIST_VIEW
        fragmentSelector.getLandingFragment(true, mecFlowConfigurator, bundle)
        assertNotNull(fragment)
    }

    @Test
    fun `getLandingFragment of MEC_CATEGORIZED_PRODUCT_LIST_VIEW`() {
        mecFlowConfigurator.landingView = MECFlowConfigurator.MECLandingView.MEC_CATEGORIZED_PRODUCT_LIST_VIEW
        fragmentSelector.getLandingFragment(true, mecFlowConfigurator, bundle)
        assertNotNull(fragment)
    }

    @Test
    fun `getLandingFragment of MEC_PRODUCT_DETAILS_VIEW`() {
        mecFlowConfigurator.landingView = MECFlowConfigurator.MECLandingView.MEC_PRODUCT_DETAILS_VIEW
        fragmentSelector.getLandingFragment(true, mecFlowConfigurator, bundle)
        assertNotNull(fragment)
    }

    @Test
    fun `getLandingFragment of MEC_SHOPPING_CART_VIEW`() {
        mecFlowConfigurator.landingView = MECFlowConfigurator.MECLandingView.MEC_SHOPPING_CART_VIEW
        fragmentSelector.getLandingFragment(true, mecFlowConfigurator, bundle)
        assertNotNull(fragment)
    }
}