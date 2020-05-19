package com.philips.platform.pim;

import com.philips.platform.pim.listeners.UserLoginListener;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

@RunWith(PowerMockRunner.class)
public class PIMLaunchInputTest extends TestCase {
    private PIMLaunchInput pimLaunchInput;
    @Mock
    private UserLoginListener mockUserLoginListener;
    @Mock
    private HashMap<PIMParameterToLaunchEnum, Object> mockPimParameterToLaunchEnumObjectHashMap;

    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        pimLaunchInput = new PIMLaunchInput();
    }

    @Test
    public void testGetPimUserLoginListener() {
        pimLaunchInput.setUserLoginListener(mockUserLoginListener);
        UserLoginListener pimUserLoginListener = pimLaunchInput.getUserLoginListener();
        assertSame(mockUserLoginListener, pimUserLoginListener);
    }

    @Test
    public void testGetParameterToLaunch() {
        pimLaunchInput.setParameterToLaunch(mockPimParameterToLaunchEnumObjectHashMap);
        HashMap<PIMParameterToLaunchEnum, Object> parameterToLaunch = pimLaunchInput.getParameterToLaunch();
        assertSame(mockPimParameterToLaunchEnumObjectHashMap, parameterToLaunch);
    }
}