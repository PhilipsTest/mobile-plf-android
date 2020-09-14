package com.philips.platform.ccb.analytics

import com.philips.platform.appinfra.tagging.AppTaggingInterface
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito

class CCBAnalyticsTest {

    @Mock
    var mAppTaggingInterfaceMock: AppTaggingInterface = Mockito.mock(AppTaggingInterface::class.java);


    val ccbAnalytics get() = CCBAnalytics.Companion


    @Before
    fun setUp() {
        ccbAnalytics.mAppTaggingInterface = mAppTaggingInterfaceMock
    }

    @Test
    fun initCCBAnalytics() {
    }

    @Test
    fun trackPage() {
        ccbAnalytics.trackPage("some page")
        Mockito.verify(mAppTaggingInterfaceMock, Mockito.atLeastOnce()).trackPageWithInfo(ArgumentMatchers.any(String::class.java), eq(null))
        Mockito.verify(mAppTaggingInterfaceMock).trackPageWithInfo(ArgumentMatchers.any(String::class.java), eq(null))
    }
}