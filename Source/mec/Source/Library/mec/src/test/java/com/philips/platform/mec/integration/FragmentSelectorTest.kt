package com.philips.platform.mec.integration

import android.os.Bundle
import com.philips.platform.mec.screens.MecBaseFragment
import com.philips.platform.mec.screens.catalog.MECCategorizedRetailerFragment
import com.philips.platform.mec.screens.catalog.MECProductCatalogCategorizedFragment
import com.philips.platform.mec.screens.catalog.MECProductCatalogFragment
import com.philips.platform.mec.screens.detail.MECLandingProductDetailsFragment
import com.philips.platform.mec.screens.history.MECOrderHistoryFragment
import com.philips.platform.mec.screens.shoppingCart.MECShoppingCartFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.ArrayList
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@PrepareForTest(MECFlowConfigurator::class)
@RunWith(PowerMockRunner::class)
class FragmentSelectorTest {


    lateinit var fragmentSelector: FragmentSelector

    lateinit var mecFlowConfigurator: MECFlowConfigurator



    @Mock
    lateinit var bundle: Bundle

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        fragmentSelector = FragmentSelector()
        mecFlowConfigurator = MECFlowConfigurator()
    }

    @Test
    fun `getLandingFragment of MEC_PRODUCT_LIST_VIEW should be Catalog Fragment`() {
        mecFlowConfigurator.landingView = MECFlowConfigurator.MECLandingView.MEC_PRODUCT_LIST_VIEW
        val landingFragment = fragmentSelector.getLandingFragment(true, mecFlowConfigurator, bundle)
        assertTrue(landingFragment is MECProductCatalogFragment)
    }

    @Test
    fun `getLandingFragment of MEC_CATEGORIZED_PRODUCT_LIST_VIEW should be Categorized fragment when hybris is enabled`() {

        val ctnList: ArrayList<String> = ArrayList()
        ctnList.add("HXCTQA/03")
        ctnList.add("HXCTQA/02")
        mecFlowConfigurator.productCTNs = ctnList

        mecFlowConfigurator.landingView = MECFlowConfigurator.MECLandingView.MEC_CATEGORIZED_PRODUCT_LIST_VIEW
        val landingFragment = fragmentSelector.getLandingFragment(true, mecFlowConfigurator, bundle)
        assertTrue(landingFragment is MECProductCatalogCategorizedFragment)
    }

    @Test
    fun `getLandingFragment of MEC_CATEGORIZED_PRODUCT_LIST_VIEW should be Categorized retailer fragment when hybris is disabled`() {

        val ctnList: ArrayList<String> = ArrayList()
        ctnList.add("HXCTQA/03")
        ctnList.add("HXCTQA/02")
        mecFlowConfigurator.productCTNs = ctnList

        mecFlowConfigurator.landingView = MECFlowConfigurator.MECLandingView.MEC_CATEGORIZED_PRODUCT_LIST_VIEW
        val landingFragment = fragmentSelector.getLandingFragment(false, mecFlowConfigurator, bundle)
        assertTrue(landingFragment is MECCategorizedRetailerFragment)
    }

    @Test
    fun `getLandingFragment of MEC_PRODUCT_DETAILS_VIEW should be MECLandingProductDetailFragment`() {

        val ctnList: ArrayList<String> = ArrayList()
        ctnList.add("HXCTQA/02")
        mecFlowConfigurator.productCTNs = ctnList

        mecFlowConfigurator.landingView = MECFlowConfigurator.MECLandingView.MEC_PRODUCT_DETAILS_VIEW
        val landingFragment = fragmentSelector.getLandingFragment(true, mecFlowConfigurator, bundle)
        assertTrue(landingFragment is MECLandingProductDetailsFragment)
    }

    @Test
    fun `getLandingFragment of MEC_SHOPPING_CART_VIEW should be MECShoppingCartFragment`() {
        mecFlowConfigurator.landingView = MECFlowConfigurator.MECLandingView.MEC_SHOPPING_CART_VIEW
        val landingFragment = fragmentSelector.getLandingFragment(true, mecFlowConfigurator, bundle)
        assertTrue(landingFragment is MECShoppingCartFragment)
    }

    @Test
    fun `getLandingFragment of MEC_SHOPPING_CART_VIEW should be MECOrderHistoryFragment`() {
        mecFlowConfigurator.landingView = MECFlowConfigurator.MECLandingView.MEC_ORDER_HISTORY
        val landingFragment = fragmentSelector.getLandingFragment(true, mecFlowConfigurator, bundle)
        assertTrue(landingFragment is MECOrderHistoryFragment)
    }
}