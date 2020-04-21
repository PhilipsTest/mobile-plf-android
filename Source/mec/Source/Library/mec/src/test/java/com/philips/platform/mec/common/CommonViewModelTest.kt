package com.philips.platform.mec.common

import androidx.lifecycle.MutableLiveData
import com.philips.platform.ecs.error.ECSError
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(MecError::class)
@RunWith(PowerMockRunner::class)
class CommonViewModelTest {
    @Mock
    lateinit var exception: Exception

    @Mock
    lateinit var error: ECSError

    @Mock
    lateinit var mecError: MutableLiveData<MecError>

    lateinit var commonViewModel: CommonViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        commonViewModel = CommonViewModel()
    }

    @Test(expected = NullPointerException::class)
    fun authFailureCallback() {
        Mockito.`when`(mecError.value).thenReturn(null)
        commonViewModel.authFailureCallback(exception, error)
    }
}