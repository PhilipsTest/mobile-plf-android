/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.platform.appinfra.tagging;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraLogEventID;
import com.philips.platform.appinfra.logging.LoggingInterface;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApplicationLifeCycleHandlerTest {

    private LoggingInterface loggingInterfaceMock;
    private Activity activity;
    private Bundle bundle;
    private ApplicationLifeCycleHandler applicationLifeCycleHandler;
    private AppInfra appInfraMock;

    @Before
    public void setUp() throws Exception {
        activity = new Activity();
        bundle = new Bundle();
        appInfraMock = mock(AppInfra.class);
        ApplicationLifeCycleHandler.isInBackground = true;
        applicationLifeCycleHandler = new ApplicationLifeCycleHandler(appInfraMock);
        loggingInterfaceMock = mock(LoggingInterface.class);
    }

    @Test
    public void testApplicationLifeCycleOnActivityCreated() {
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        applicationLifeCycleHandler.onActivityCreated(activity, bundle);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler Created");
    }

    @Test
    public void testApplicationLifeCycleOnActivityCreatedNullCheck() {
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        applicationLifeCycleHandler.onActivityCreated(null, null);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler Created");
    }

    @Test
    public void testApplicationLifeCycleOnActivityResumed() {
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        AppTaggingInterface appTaggingInterfaceMock = mock(AppTaggingInterface.class);
        when(appInfraMock.getTagging()).thenReturn(appTaggingInterfaceMock);
        applicationLifeCycleHandler = new ApplicationLifeCycleHandler(appInfraMock);
        applicationLifeCycleHandler.onActivityResumed(activity);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler Resumed");
        verify(appTaggingInterfaceMock).trackActionWithInfo("sendData", "appStatus", "ForeGround");
    }

    @Test
    public void testApplicationLifeCycleOnActivityResumedNullCheck() {
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        AppTaggingInterface appTaggingInterfaceMock = mock(AppTaggingInterface.class);
        when(appInfraMock.getTagging()).thenReturn(appTaggingInterfaceMock);
        applicationLifeCycleHandler = new ApplicationLifeCycleHandler(appInfraMock);
        applicationLifeCycleHandler.onActivityResumed(null);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler Resumed");
    }

    @Test
    public void testApplicationLifeCycleOnActivityPaused() {
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        applicationLifeCycleHandler.onActivityPaused(activity);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler Paused");
    }

    @Test
    public void testApplicationLifeCycleOnActivityPausedNullCheck() {
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        applicationLifeCycleHandler.onActivityPaused(null);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler Paused");
    }

    @Test
    public void testApplicationLifeCycleOnActivitySaveInstanceState() {
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        applicationLifeCycleHandler.onActivitySaveInstanceState(activity, bundle);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler SaveInstanceState");
    }

    @Test
    public void testApplicationLifeCycleOnActivitySaveInstanceStateNullCheck() {
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        applicationLifeCycleHandler.onActivitySaveInstanceState(null, null);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler SaveInstanceState");
    }

    @Test
    public void testApplicationLifeCycleOnActivityStarted() {
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        applicationLifeCycleHandler.onActivityStarted(activity);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler Started");
    }

    @Test
    public void testApplicationLifeCycleOnActivityStartedeNullCheck() {
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        applicationLifeCycleHandler.onActivityStarted(null);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler Started");
    }

    @Test
    public void testApplicationLifeCycleOnActivityStopped() {
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        applicationLifeCycleHandler.onActivityStopped(activity);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler Stopped");
    }

    @Test
    public void testApplicationLifeCycleOnActivityStoppedNullCheck() {
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        applicationLifeCycleHandler.onActivityStopped(null);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler Stopped");
    }

    @Test
    public void testApplicationLifeCycleOnConfigurationChanged() {
        Configuration mConfiguration = new Configuration();
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        applicationLifeCycleHandler.onConfigurationChanged(mConfiguration);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler ConfigurationChanged");
    }

    @Test
    public void testApplicationLifeCycleOnConfigurationChangedNullCheck() {
        Configuration mConfiguration = new Configuration();
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        applicationLifeCycleHandler.onConfigurationChanged(mConfiguration);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler ConfigurationChanged");
    }

    @Test
    public void testApplicationLifeCycleOnTrimMemory() {
        int i = ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN;
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        AppTaggingInterface appTaggingInterfaceMock = mock(AppTaggingInterface.class);
        when(appInfraMock.getTagging()).thenReturn(appTaggingInterfaceMock);
        applicationLifeCycleHandler = new ApplicationLifeCycleHandler(appInfraMock);
        applicationLifeCycleHandler.onTrimMemory(i);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler Background");
    }

    @Test
    public void testApplicationLifeCycleOnLowMemory() {
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        applicationLifeCycleHandler.onLowMemory();
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler onLowMemory");
    }

    @Test
    public void testApplicationLifeCycleOnLowMemoryNullCheck() {
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        applicationLifeCycleHandler.onLowMemory();
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler onLowMemory");
    }

    @Test
    public void testApplicationLifeCycleOnActivityDestroyed() {
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        applicationLifeCycleHandler.onActivityDestroyed(activity);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler Destroyed");
    }

    @Test
    public void testApplicationLifeCycleOnActivityDestroyedNullCheck() {
        when(appInfraMock.getAppInfraLogInstance()).thenReturn(loggingInterfaceMock);
        applicationLifeCycleHandler.onActivityDestroyed(null);
        verify(loggingInterfaceMock).log(LoggingInterface.LogLevel.DEBUG,
                AppInfraLogEventID.AI_TAGGING, "ApplicationLifeCycleHandler Destroyed");
    }

}