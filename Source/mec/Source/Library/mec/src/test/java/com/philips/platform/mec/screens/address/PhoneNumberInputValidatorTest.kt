package com.philips.platform.mec.screens.address

import android.content.Context
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock

class PhoneNumberInputValidatorTest {






    @Mock
    lateinit var validator :PhoneNumberInputValidator

    var context = mock(Context::class.java)
//    var validationEditText :ValidationEditText= ValidationEditText(context)
    @Before
    fun setUp() {


       /*  validationEditText=ValidationEditText(context)
         validator = PhoneNumberInputValidator(validationEditText, PhoneNumberUtil.getInstance())*/
    }

    @Test
    fun validate() {
      /*  assertEquals(true,validator.validate("123456"))
        assertEquals(false,validator.validate(""))*/
    }

    @Test
    fun getFormattedPhoneNumber() {
    }

    @Test
    fun getPhoneNumberUtil() {
    }
}