package com.philips.platform.mec.utils

import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.error.ECSErrorEnum
import com.philips.platform.ecs.model.address.Country
import com.philips.platform.ecs.model.address.ECSAddress
import com.philips.platform.ecs.model.address.Region
import com.philips.platform.ecs.model.orders.PaymentInfo
import com.philips.platform.ecs.model.payment.CardType
import com.philips.platform.ecs.model.payment.ECSPayment
import com.philips.platform.mec.screens.payment.MECPayment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(MECutility::class)
@RunWith(PowerMockRunner::class)
class MECutilityTest {


    private var mECutility = MECutility();
    val mECutilityCompanion get() = MECutility.Companion
 
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `should construct address string and it should be as expected for empty address object`() {
        val ecsAddress = ECSAddress()
        val constructShippingAddressDisplayField = mECutility.constructShippingAddressDisplayField(ecsAddress)
        assertEquals("", constructShippingAddressDisplayField)
    }

    @Test
    fun `should construct address as expected when region name is not there`() {
        val ecsAddress = ECSAddress()
        val region = Region()
        region.isocodeShort = "US"

        val country = Country()
        country.isocode = "USA"

        ecsAddress.region = region
        ecsAddress.country = country

        ecsAddress.firstName = "pabitra"
        ecsAddress.lastName = "sahoo"

        ecsAddress.line1 = "White Field"
        ecsAddress.line2 = "Patel layout"

        val expectedString = "White Field,\n" +
                "Patel layout,\n" +
                "US,\n" +
                "USA"
        val constructShippingAddressDisplayField = mECutility.constructShippingAddressDisplayField(ecsAddress)

        assertEquals(expectedString, constructShippingAddressDisplayField)
    }

    @Test
    fun `construct card detail for invalid mecPayment`() {


        val ecsPayment = ECSPayment()
        val mecPayment = MECPayment(ecsPayment)
        val constructCardDetails = mECutility.constructCardDetails(mecPayment)
        assertNull(constructCardDetails)

    }

    @Test
    fun `construct card detail for a valid mecPayment`() {
        val ecsPayment = ECSPayment()
        ecsPayment.cardNumber = "3124 5674 8934"

        val cardType = CardType()
        cardType.name = "VISA"
        ecsPayment.cardType = cardType
        val mecPayment = MECPayment(ecsPayment)

        val constructCardDetails = mECutility.constructCardDetails(mecPayment)
        assertEquals("VISA 674 8934", constructCardDetails)
    }

    @Test
    fun `construct card detail for invalid paymentInfo`() {
        val constructCardDetails = mECutility.constructCardDetails(PaymentInfo())
        assertNull(constructCardDetails)
    }

    @Test
    fun `construct card detail for valid payment info`() {
        val paymentInfo = PaymentInfo()

        paymentInfo.cardNumber = "3124 5674 8934"
        val cardType = CardType()
        cardType.name = "VISA"
        paymentInfo.cardType = cardType

        val constructCardDetails = mECutility.constructCardDetails(paymentInfo)
        assertEquals("VISA 674 8934", constructCardDetails)
    }

    private fun getListOfInvalidMECPayments(): MutableList<MECPayment> {

        val ecsPayment = ECSPayment()
        val mecPayment = MECPayment(ecsPayment)


        val ecsPayment1 = ECSPayment()
        ecsPayment1.expiryMonth = "10"
        val mecPayment1 = MECPayment(ecsPayment1)

        val ecsPayment2 = ECSPayment()
        ecsPayment2.expiryYear = "20"
        val mecPayment2 = MECPayment(ecsPayment1)

        val mutableListOf = mutableListOf<MECPayment>()
        mutableListOf.add(mecPayment)
        mutableListOf.add(mecPayment1)
        mutableListOf.add(mecPayment2)
        return mutableListOf
    }

    private fun getListOfInvalidPaymentInfo(): MutableList<PaymentInfo> {

        val paymentInfo = PaymentInfo()
        val paymentInfo1 = PaymentInfo()
        paymentInfo1.expiryMonth = "10"
        val paymentInfo2 = PaymentInfo()
        paymentInfo2.expiryYear = "20"


        val mutableListOf = mutableListOf<PaymentInfo>()
        mutableListOf.add(paymentInfo)
        mutableListOf.add(paymentInfo1)
        mutableListOf.add(paymentInfo2)
        return mutableListOf
    }

    @Test
    fun `construct card validity for invalid MEC payment`() {

        val listOfInvalidMECPayments = getListOfInvalidMECPayments()

        for (mecPayment in listOfInvalidMECPayments){
            assertNull(mECutility.constructCardValidityDetails(mecPayment))
        }
    }

    @Test
    fun `construct card validity for valid mec payment`() {

        val ecsPayment = ECSPayment()
        ecsPayment.expiryMonth = "10"
        ecsPayment.expiryYear = "20"
        val mecPayment = MECPayment(ecsPayment)
        val constructCardValidityDetails = mECutility.constructCardValidityDetails(mecPayment)
        assertEquals("10/20",constructCardValidityDetails)
    }

    @Test
    fun `construct card validity for invalid  paymentInfo`() {

        val listOfInvalidPaymentInfo = getListOfInvalidPaymentInfo()

        for (paymentInfo in listOfInvalidPaymentInfo){
            assertNull(mECutility.constructCardValidityDetails(paymentInfo))
        }
    }

    @Test
    fun `construct card validity for valid  paymentInfo`() {

        val paymentInfo = PaymentInfo()
        paymentInfo.expiryMonth = "10"
        paymentInfo.expiryYear="20"

        val constructCardValidityDetails = mECutility.constructCardValidityDetails(paymentInfo)
        assertEquals("10/20",constructCardValidityDetails)
    }

    @Test
    fun `indexOfSubString  method test for same case string`(){


        assertEquals(-1, mECutilityCompanion.indexOfSubString(true,null,null))
        assertEquals(0,  mECutilityCompanion.indexOfSubString(true,"blackListed","blackListed"))


         assertEquals(-1,  mECutilityCompanion.indexOfSubString(true,"blackListed","blackListed long"))



        assertEquals(0,  mECutilityCompanion.indexOfSubString(true,"blackListed long","blackListed"))


        assertEquals(-1,  mECutilityCompanion.indexOfSubString(true,"Some retailer","xyz"))
    }

    @Test
    fun `Test IsStockAvailable()`(){

        assertEquals(true ,mECutilityCompanion.isStockAvailable("inStock",5))
        assertEquals(true ,mECutilityCompanion.isStockAvailable("lowStock",5))
        assertEquals(false ,mECutilityCompanion.isStockAvailable("inStock",0))
        assertEquals(false ,mECutilityCompanion.isStockAvailable("lowStock",0))
        assertEquals(false ,mECutilityCompanion.isStockAvailable(null,5))
    }

    @Test
    fun `Test stockStatus`(){
        assertEquals("available" ,mECutilityCompanion.stockStatus("YES"))
        assertEquals("out of stock" ,mECutilityCompanion.stockStatus("NO"))
        assertEquals("" ,mECutilityCompanion.stockStatus("random"))
    }

    @Test
    fun `test isAuthError()`(){
        var error = ECSError(ECSErrorEnum.ECSinvalid_grant.errorCode,ECSErrorEnum.ECSinvalid_grant.localizedErrorString)
        assertEquals(true ,mECutilityCompanion.isAuthError(error))

    }

}