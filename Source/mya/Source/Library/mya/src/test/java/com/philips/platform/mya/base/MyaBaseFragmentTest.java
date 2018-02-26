/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.mya.base;


import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.mya.BuildConfig;
import com.philips.platform.mya.MyaHelper;
import com.philips.platform.mya.activity.MyaActivity;
import com.philips.platform.mya.runner.CustomRobolectricRunner;
import com.philips.platform.mya.settings.MyaSettingsFragment;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static com.philips.platform.mya.base.MyaBaseFragment.MY_ACCOUNTS_INVOKE_TAG;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class MyaBaseFragmentTest {

    private MyaBaseFragment myaBaseFragment;
    private Context mContext;
    private ActionBarListener actionBarListener = new ActionBarListener() {
        @Override
        public void updateActionBar(int i, boolean b) {
            assertEquals(RuntimeEnvironment.application.getString(i), "My account");
        }

        @Override
        public void updateActionBar(String s, boolean b) {
            assertEquals(s, "My account");
        }
    };

    @Before
    public void setup() {
        initMocks(this);
        mContext = RuntimeEnvironment.application;
        myaBaseFragment = new MyaSettingsFragment(){
            @Override
            public int getActionbarTitleResId() {
                return 1;
            }

            @Override
            public String getActionbarTitle(Context context) {
                return "My Account";
            }

            @Override
            public boolean getBackButtonState() {
                return false;
            }
        };
        AppInfra appInfra = new AppInfra.Builder().build(mContext);
        MyaHelper.getInstance().setAppInfra(appInfra);
        SupportFragmentTestUtil.startFragment(myaBaseFragment);
        assertNotNull(myaBaseFragment.getContext());
    }

    @Test
    public void testInvocations() {
        myaBaseFragment.setActionbarUpdateListener(actionBarListener);
        assertEquals(myaBaseFragment.getActionbarUpdateListener(),actionBarListener);
        assertTrue(myaBaseFragment.getBackButtonState());
    }

    @Test
    public void shouldExitMyAccounts() {
        FragmentLauncher fragmentLauncherMock = mock(FragmentLauncher.class);
        FragmentActivity fragmentActivity = mock(FragmentActivity.class);
        FragmentManager fragmentManagerMock = mock(FragmentManager.class);
        when(fragmentActivity.getSupportFragmentManager()).thenReturn(fragmentManagerMock);
        when(fragmentLauncherMock.getFragmentActivity()).thenReturn(fragmentActivity);
        myaBaseFragment.setFragmentLauncher(fragmentLauncherMock);
        myaBaseFragment.exitMyAccounts();
        verify(fragmentManagerMock).popBackStackImmediate(MY_ACCOUNTS_INVOKE_TAG,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        assertEquals(myaBaseFragment.getFragmentLauncher(),fragmentLauncherMock);
        MyaActivity myaActivityMock = mock(MyaActivity.class);
        when(fragmentLauncherMock.getFragmentActivity()).thenReturn(myaActivityMock);
        myaBaseFragment.setFragmentLauncher(fragmentLauncherMock);
        myaBaseFragment.exitMyAccounts();
        verify(myaActivityMock).finish();
    }

    @Test
    public void shouldAddFragmentWhenRequired() {

        int mEnterAnimation = 500;
        int mExitAnimation = 700;
        final String startAnim = "start_anim";
        final String endAnim= "end_anim";
        FragmentLauncher fragmentLauncherMock = mock(FragmentLauncher.class);
        FragmentActivity fragmentActivity = mock(FragmentActivity.class);
        when(fragmentActivity.getPackageName()).thenReturn("some_package");
        FragmentManager fragmentManagerMock = mock(FragmentManager.class);
        MyaBaseFragment myaBaseFragmentMock = mock(MyaSettingsFragment.class);
        int value = 123456;
        when(myaBaseFragmentMock.getId()).thenReturn(value);
        when(fragmentActivity.getSupportFragmentManager()).thenReturn(fragmentManagerMock);
        when(fragmentLauncherMock.getFragmentActivity()).thenReturn(fragmentActivity);
        when(fragmentLauncherMock.getParentContainerResourceID()).thenReturn(value);
        when(fragmentLauncherMock.getEnterAnimation()).thenReturn(mEnterAnimation);
        when(fragmentLauncherMock.getExitAnimation()).thenReturn(mExitAnimation);
        Resources resourcesMock = mock(Resources.class);
        when(resourcesMock.getResourceName(mEnterAnimation)).thenReturn(startAnim);
        when(resourcesMock.getResourceName(mExitAnimation)).thenReturn(endAnim);
        when(resourcesMock.getIdentifier(startAnim,"anim","some_package")).thenReturn(mEnterAnimation);
        when(resourcesMock.getIdentifier(endAnim,"anim","some_package")).thenReturn(mExitAnimation);
        when(fragmentActivity.getResources()).thenReturn(resourcesMock);
        FragmentTransaction fragmentTransactionMock = mock(FragmentTransaction.class);
        when(fragmentManagerMock.beginTransaction()).thenReturn(fragmentTransactionMock);
        when(fragmentManagerMock.findFragmentById(myaBaseFragmentMock.getId())).thenReturn(myaBaseFragmentMock);
        myaBaseFragment.setFragmentLauncher(fragmentLauncherMock);
        myaBaseFragment.showFragment(myaBaseFragmentMock);

        verify(fragmentTransactionMock).setCustomAnimations(mEnterAnimation,
                mExitAnimation, mEnterAnimation, mExitAnimation);
        verify(fragmentTransactionMock).replace(myaBaseFragmentMock.getId(), myaBaseFragmentMock, myaBaseFragmentMock.getClass().getSimpleName());
        verify(fragmentTransactionMock).commitAllowingStateLoss();
        Fragment fragmentMock = mock(Fragment.class);
        when(fragmentMock.getId()).thenReturn(value);
        when(fragmentManagerMock.findFragmentById(value)).thenReturn(fragmentMock);
        myaBaseFragment.showFragment(myaBaseFragmentMock);
        when(fragmentManagerMock.findFragmentById(fragmentMock.getId())).thenReturn(myaBaseFragmentMock);
        verify(fragmentTransactionMock,atLeastOnce()).addToBackStack(MY_ACCOUNTS_INVOKE_TAG);
    }

    @Test
    public void shouldSetTitleWhenInvoked() {
        ActionBarListener actionBarListener = mock(ActionBarListener.class);
        myaBaseFragment = new MyaSettingsFragment();
        myaBaseFragment.setActionbarUpdateListener(actionBarListener);
        startFragment(myaBaseFragment);
        verify(actionBarListener).updateActionBar(myaBaseFragment.getActionbarTitleResId(),myaBaseFragment.getBackButtonState());
        verify(actionBarListener).updateActionBar(myaBaseFragment.getActionbarTitle(RuntimeEnvironment.application),myaBaseFragment.getBackButtonState());
    }
}