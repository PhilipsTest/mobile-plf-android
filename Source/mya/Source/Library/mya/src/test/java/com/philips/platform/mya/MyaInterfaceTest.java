/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.platform.mya;

import android.content.Context;
import android.util.AndroidRuntimeException;

import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.mya.launcher.MyaDependencies;
import com.philips.platform.mya.launcher.MyaInterface;
import com.philips.platform.mya.launcher.MyaLaunchInput;
import com.philips.platform.mya.launcher.MyaSettings;
import com.philips.platform.mya.mock.ActionBarListenerMock;
import com.philips.platform.mya.mock.ActivityLauncherMock;
import com.philips.platform.mya.mock.AppInfraInterfaceMock;
import com.philips.platform.mya.mock.FragmentActivityMock;
import com.philips.platform.mya.mock.FragmentLauncherMock;
import com.philips.platform.mya.mock.FragmentManagerMock;
import com.philips.platform.mya.mock.FragmentTransactionMock;
import com.philips.platform.mya.tabs.MyaTabFragment;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.philips.platform.mya.base.MyaBaseFragment.MY_ACCOUNTS_INVOKE_TAG;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class MyaInterfaceTest {
    private MyaInterface myaInterface;

    private UiLauncher givenUiLauncher;

    private ActionBarListener actionBarListener;
    private FragmentActivityMock fragmentActivity;
    private FragmentTransactionMock fragmentTransaction;
    private AppInfraInterfaceMock appInfra;
    private Context context;

    private final int A_SPECIFIC_CONTAINER_ID = 12345678;
    private static final String MYAFRAGMENT = MY_ACCOUNTS_INVOKE_TAG;

    private MyaLaunchInput launchInput;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        context = getInstrumentation().getContext();
        launchInput = new MyaLaunchInput(context);
        final UserDataInterface userDataInterface = mock(UserDataInterface.class);
        when(userDataInterface.getUserLoggedInState()).thenReturn(UserLoggedInState.USER_LOGGED_IN);
        MyaHelper.getInstance().setUserDataInterface(userDataInterface);
        final AppTaggingInterface appTaggingInterface = mock(AppTaggingInterface.class);
        myaInterface = new MyaInterface() {
            @Override
            public UserDataInterface getUserDataInterface() {
                return userDataInterface;
            }

            @Override
            protected AppTaggingInterface getTaggingInterface(MyaDependencies myaDependencies) {

                return appTaggingInterface;
            }
        };

        fragmentTransaction = new FragmentTransactionMock();
        FragmentManagerMock fragmentManager = new FragmentManagerMock(fragmentTransaction);
        fragmentActivity = new FragmentActivityMock(fragmentManager);
        appInfra = new AppInfraInterfaceMock();
        actionBarListener = new ActionBarListenerMock();
    }

    @Test
    public void launchWithFragmentLauncher_correctFragmentIsReplacedInContainer() {
        givenFragmentLauncher(fragmentActivity, A_SPECIFIC_CONTAINER_ID, actionBarListener);
        whenCallingLaunchWithAddToBackstack();
        thenReplaceWasCalledWith(A_SPECIFIC_CONTAINER_ID, MyaTabFragment.class, MY_ACCOUNTS_INVOKE_TAG);
        thenAddToBackStackWasCalled(MY_ACCOUNTS_INVOKE_TAG);
        thenCommitAllowingStateLossWasCalled();
    }

    @Test
    public void launchWithFragmentLauncher_dontCallAddToBackStackWhenNotDemanded() {
        givenFragmentLauncher(fragmentActivity, A_SPECIFIC_CONTAINER_ID, actionBarListener);
        whenCallingLaunchWithoutAddToBackstack();
        thenReplaceWasCalledWith(A_SPECIFIC_CONTAINER_ID, MyaTabFragment.class, MYAFRAGMENT);
        thenCommitAllowingStateLossWasCalled();
    }

    @Test(expected = AndroidRuntimeException.class)
    public void launchWithActivityLauncher_correctFragmentIsReplacedInContainer() {
        givenActivityLauncher();
        whenCallingLaunchWithoutAddToBackstack();
    }

    private void givenFragmentLauncher(FragmentActivityMock fragmentActivity, int containerId, ActionBarListener actionBarListener) {
        givenUiLauncher = new FragmentLauncherMock(fragmentActivity, containerId, actionBarListener);
    }

    private void givenActivityLauncher() {
        givenUiLauncher = new ActivityLauncherMock(context, null, null, 0, null);
    }

    private void whenCallingLaunchWithAddToBackstack() {
        myaInterface.init(new MyaDependencies(appInfra), new MyaSettings(context));
        myaInterface.launch(givenUiLauncher, launchInput);
    }

    private void whenCallingLaunchWithoutAddToBackstack() {
        myaInterface.init(new MyaDependencies(appInfra), new MyaSettings(context));
        myaInterface.launch(givenUiLauncher, launchInput);
    }

    private void thenReplaceWasCalledWith(int expectedParentContainerId, Class<?> expectedFragmentClass, String expectedTag) {
        assertEquals(expectedParentContainerId, fragmentTransaction.replace_containerId);
        assertTrue(fragmentTransaction.replace_fragment.getClass().isAssignableFrom(expectedFragmentClass));
        // assertEquals(expectedTag, fragmentTransaction.replace_tag);
    }

    private void thenAddToBackStackWasCalled(String expectedBackStackId) {
        assertEquals(expectedBackStackId, fragmentTransaction.addToBackStack_backStackId);
    }

    private void thenCommitAllowingStateLossWasCalled() {
        assertTrue(fragmentTransaction.commitAllowingStateLossWasCalled);
    }

}