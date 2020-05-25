package com.philips.platform.pim.models;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.philips.platform.pim.utilities.PIMInitState;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
public class PIMInitViewModelTest extends TestCase {

    PIMInitViewModel pimInitViewModel;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        Application context = Mockito.mock(Application.class);
        pimInitViewModel = new PIMInitViewModel((Application) context);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetMuatbleInitLiveData() {
        MutableLiveData<PIMInitState> muatbleInitLiveData = pimInitViewModel.getMuatbleInitLiveData();
        assertNotNull(muatbleInitLiveData);
    }
}