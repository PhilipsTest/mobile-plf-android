package com.philips.platform.mec.integration

import com.philips.platform.mec.screens.reviews.MECBazaarVoiceEnvironment
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.test.assertEquals

@RunWith(PowerMockRunner::class)
class MECBazaarVoiceInputTest {

    lateinit var mecBazaarVoiceInput: MECBazaarVoiceInput

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mecBazaarVoiceInput = MECBazaarVoiceInput()
    }

    @Test
    fun `getBazaarVoiceClientID not null`() {
        val bazaarVoiceClientID = mecBazaarVoiceInput.getBazaarVoiceClientID()
        assertNotNull(bazaarVoiceClientID)
    }

    @Test
    fun `getBazaarVoiceClientID should have  philipsglobal`() {
        val bazaarVoiceClientID = mecBazaarVoiceInput.getBazaarVoiceClientID()
        assertEquals(bazaarVoiceClientID, "philipsglobal")
    }

    @Test
    fun `getBazaarVoiceConversationAPIKey should have ca23LB5V0eOKLe0cX6kPTz6LpAEJ7SGnZHe21XiWJcshc`() {
        val bazaarVoiceConversationAPIKey = mecBazaarVoiceInput.getBazaarVoiceConversationAPIKey()
        assertEquals(bazaarVoiceConversationAPIKey, "ca23LB5V0eOKLe0cX6kPTz6LpAEJ7SGnZHe21XiWJcshc")
    }

    @Test
    fun `getBazaarVoiceConversationAPIKey not null`() {
        val bazaarVoiceConversationAPIKey = mecBazaarVoiceInput.getBazaarVoiceConversationAPIKey()
        assertNotNull(bazaarVoiceConversationAPIKey)
    }

    @Test
    fun `getBazaarVoiceEnvironment not null`() {
        val bazaarVoiceEnvironment = mecBazaarVoiceInput.getBazaarVoiceEnvironment()
        assertNotNull(bazaarVoiceEnvironment)
    }

    @Test
    fun `getBazaarVoiceEnvironment should STAGING`() {
        val bazaarVoiceEnvironment = mecBazaarVoiceInput.getBazaarVoiceEnvironment()
        assertEquals(bazaarVoiceEnvironment, MECBazaarVoiceEnvironment.STAGING)
    }
}