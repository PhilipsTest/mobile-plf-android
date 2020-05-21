package com.philips.platform.pim;

import android.content.Context;

import com.philips.platform.pif.DataInterface.USR.UserDataInterfaceException;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pif.DataInterface.USR.listeners.LogoutSessionListener;
import com.philips.platform.pif.DataInterface.USR.listeners.RefetchUserDetailsListener;
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener;
import com.philips.platform.pif.DataInterface.USR.listeners.UpdateUserDetailsHandler;
import com.philips.platform.pif.DataInterface.USR.listeners.UserDataListener;
import com.philips.platform.pim.manager.PIMUserManager;
import com.philips.platform.pim.models.PIMOIDCUserProfile;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Ordering;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class PIMDataImplementationTest extends TestCase {

    @Mock
    private Context mockContext;
    @Mock
    private PIMUserManager mockUserManager;

    PIMDataImplementation pimDataImplementation;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        pimDataImplementation = new PIMDataImplementation(mockContext, mockUserManager);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLogoutSession() {
        LogoutSessionListener mockLogoutSessionListener = mock(LogoutSessionListener.class);

        when(mockUserManager.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_LOGGED_IN);
        pimDataImplementation.logoutSession(mockLogoutSessionListener);
        verify(mockUserManager).logoutSession(any(LogoutSessionListener.class));

        when(mockUserManager.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_NOT_LOGGED_IN);
        pimDataImplementation.logoutSession(mockLogoutSessionListener);
        verify(mockLogoutSessionListener).logoutSessionFailed(any(Error.class));

    }

    @Test
    public void testRefreshSession() {
        RefreshSessionListener mockRefreshSessionListener = mock(RefreshSessionListener.class);

        when(mockUserManager.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_LOGGED_IN);
        pimDataImplementation.refreshSession(mockRefreshSessionListener);
        verify(mockUserManager).refreshSession(any(RefreshSessionListener.class));

        when(mockUserManager.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_NOT_LOGGED_IN);
        pimDataImplementation.refreshSession(mockRefreshSessionListener);
        verify(mockRefreshSessionListener).refreshSessionFailed(any(Error.class));
    }

    @Test
    public void testRefreshHSDPSession() {
        RefreshSessionListener mockRefreshSessionListener = mock(RefreshSessionListener.class);
        pimDataImplementation.refreshHSDPSession(mockRefreshSessionListener);
    }

    @Test
    public void testIsOIDCToken() {
        final boolean isOIDCToken = pimDataImplementation.isOIDCToken();
        assertEquals(isOIDCToken,false);
        when(mockUserManager.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_LOGGED_IN);
        final boolean oidcToken = pimDataImplementation.isOIDCToken();
        assertEquals(oidcToken,true);
    }

    @Test
    public void testRefetchUserDetails() {
        RefetchUserDetailsListener mockRefetchUserDetailsListener = mock(RefetchUserDetailsListener.class);

        when(mockUserManager.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_LOGGED_IN);
        pimDataImplementation.refetchUserDetails(mockRefetchUserDetailsListener);
        verify(mockUserManager).refetchUserProfile(mockRefetchUserDetailsListener);

        when(mockUserManager.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_NOT_LOGGED_IN);
        pimDataImplementation.refetchUserDetails(mockRefetchUserDetailsListener);
        verify(mockRefetchUserDetailsListener).onRefetchFailure(any(Error.class));
    }

    @Test
    public void testUpdateReceiveMarketingEmail() {
        UpdateUserDetailsHandler mockUpdateUserDetailsListener = mock(UpdateUserDetailsHandler.class);

        when(mockUserManager.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_LOGGED_IN);
        pimDataImplementation.updateReceiveMarketingEmail(mockUpdateUserDetailsListener,true);
        verify(mockUserManager).updateMarketingOptIn(mockUpdateUserDetailsListener,true);

        when(mockUserManager.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_NOT_LOGGED_IN);
        pimDataImplementation.updateReceiveMarketingEmail(mockUpdateUserDetailsListener,false);
        verify(mockUpdateUserDetailsListener).onUpdateFailedWithError(any(Error.class));
    }

    @Test
    public void testGetUserLoggedInState() {
        when(mockUserManager.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_LOGGED_IN);
        UserLoggedInState userLoggedInState = pimDataImplementation.getUserLoggedInState();
        assertEquals(userLoggedInState,UserLoggedInState.USER_LOGGED_IN);
    }

    @Test
    public void testGetLogoutSessionListener() throws Exception {
        LogoutSessionListener mockLogoutSessionListener = mock(LogoutSessionListener.class);
        PIMDataImplementation spyDataImpl = spy(pimDataImplementation);
        LogoutSessionListener logoutSessionListener = Whitebox.invokeMethod(spyDataImpl, "getLogoutSessionListener", mockLogoutSessionListener);
        logoutSessionListener.logoutSessionSuccess();
        verify(mockLogoutSessionListener).logoutSessionSuccess();
        Error error = mock(Error.class);
        logoutSessionListener.logoutSessionFailed(error);
        verify(mockLogoutSessionListener).logoutSessionFailed(error);
    }

    @Test
    public void testGetRefreshSessionListener() throws Exception {
        RefreshSessionListener mockRefreshSessionListener = mock(RefreshSessionListener.class);
        PIMDataImplementation spyDataImpl = spy(pimDataImplementation);
        RefreshSessionListener refreshSessionListener = Whitebox.invokeMethod(spyDataImpl, "getRefreshSessionListener", mockRefreshSessionListener);
        refreshSessionListener.refreshSessionSuccess();
        verify(mockRefreshSessionListener).refreshSessionSuccess();
        Error error = mock(Error.class);
        refreshSessionListener.refreshSessionFailed(error);
        verify(mockRefreshSessionListener).refreshSessionFailed(error);
        refreshSessionListener.forcedLogout();
        verify(mockRefreshSessionListener).forcedLogout();
    }

    @Test
    public void testGetUserDetails() throws Exception {
        final PIMOIDCUserProfile mockOIDCUserProfile = mock(PIMOIDCUserProfile.class);
        when(mockUserManager.getUserProfile()).thenReturn(mockOIDCUserProfile);
        when(mockUserManager.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_LOGGED_IN);
        pimDataImplementation.getUserDetails(null);

        ArrayList<String> arrayList = new ArrayList<>();
        pimDataImplementation.getUserDetails(arrayList);

        arrayList.add(UserDetailConstants.GIVEN_NAME);
        arrayList.add(UserDetailConstants.ACCESS_TOKEN);
        arrayList.add(UserDetailConstants.RECEIVE_MARKETING_EMAIL);
        pimDataImplementation.getUserDetails(arrayList);
        verify(mockOIDCUserProfile).fetchUserDetails(arrayList);
    }

    @Test
    public void testFillRequestedKeyToList() throws Exception {
        PIMDataImplementation spyDataImpl = spy(pimDataImplementation);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(UserDetailConstants.GIVEN_NAME);
        arrayList.add(UserDetailConstants.ACCESS_TOKEN);
        arrayList.add(UserDetailConstants.RECEIVE_MARKETING_EMAIL);
        ArrayList<String> validKeyList = Whitebox.invokeMethod(spyDataImpl, "fillOnlyRequestedValidKeyToKeyList", arrayList);
        assertEquals(validKeyList.size(),arrayList.size());
    }

    @Test
    public void testGetAllValidKeys() throws Exception {
        PIMDataImplementation spyDataImpl = spy(pimDataImplementation);
        ArrayList<String> allValidKeyList = Whitebox.invokeMethod(spyDataImpl, "getAllValidUserDetailsKeys");
        assertEquals(allValidKeyList.size(),12);
    }

    @Test
    public void getHSDPAccessToken(){
        String hsdpAccessToken = pimDataImplementation.getHSDPAccessToken();
        assertEquals(hsdpAccessToken,null);
    }

    @Test
    public void getHSDPUUID(){
        String hsdpUUID = pimDataImplementation.getHSDPUUID();
        assertEquals(hsdpUUID,null);
    }

    @Test
    public void addUserDataInterfaceListener() {
        UserDataListener mockUserDataListener = mock(UserDataListener.class);
        pimDataImplementation.addUserDataInterfaceListener(mockUserDataListener);
    }

    @Test
    public void removeUserDataInterfaceListener() {
        UserDataListener mockUserDataListener = mock(UserDataListener.class);
        pimDataImplementation.removeUserDataInterfaceListener(mockUserDataListener);
    }
}