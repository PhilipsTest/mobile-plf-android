package com.philips.platform.mec.screens.history.orderDetail

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class MECOrderDetailRepositoryTest {

    lateinit var mECOrderDetailRepository: MECOrderDetailRepository

    @Mock
    lateinit var  mPRXContactsResponseCallback: PRXContactsResponseCallback


    @Before
    fun setUp() {
    }

    @Test
    fun fetchContacts() {
    }
}