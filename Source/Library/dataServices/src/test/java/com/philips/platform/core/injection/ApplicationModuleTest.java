package com.philips.platform.core.injection;

import android.content.Context;
import android.os.Handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by indrajitkumar on 07/12/16.
 */
public class ApplicationModuleTest {

    @InjectMocks
    private ApplicationModule module;

    @Mock
    Context mockContext;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        module = new ApplicationModule(mockContext);
    }

    @Test
    public void ShouldReturnContextWhenAsked() throws Exception {
        //module = new ApplicationModule(RuntimeEnvironment.application);
        assertThat(module.providesContext()).isNotNull();
        assertThat(module.providesContext()).isInstanceOf(Context.class);
    }

    @Test
    public void ShouldReturnTimer_WhenProvidesBackgroundExecutorTimerIsCalled() throws Exception {
        final ExecutorService executorService = module.provideBackgroundExecutor();
        assertThat(executorService).isNotNull();
        assertThat(executorService).isInstanceOf(ExecutorService.class);
    }

    @Test
    public void ShouldReturnHandler_WhenProvideHandlerIsCalled() throws Exception {
        final Handler handler = module.providesHandler();
        assertThat(handler).isNotNull();
        assertThat(handler).isInstanceOf(Handler.class);
    }

    @Test
    public void ShouldReturnExecutor_WhenProvideExecutorIsCalled() throws Exception {
        final Executor executor = module.providesExecutor(Mockito.mock(ExecutorService.class));
        assertThat(executor).isNotNull();
        assertThat(executor).isInstanceOf(ExecutorService.class);
    }

    @Test
    public void ShouldReturnSharedPreferences_WhenProvidesSharedPreferencesIsCalled() throws Exception {
        module.provideSharedPreferences();
    }
}