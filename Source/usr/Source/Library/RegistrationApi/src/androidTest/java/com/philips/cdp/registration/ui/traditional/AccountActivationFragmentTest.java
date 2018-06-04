package com.philips.cdp.registration.ui.traditional;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.philips.cdp.registration.RegistrationApiInstrumentationBase;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class AccountActivationFragmentTest extends RegistrationApiInstrumentationBase {

    AccountActivationFragment accountActivationFragment;
    ProgressBar mPbActivateSpinner;

    @Before
    public void setUp() throws Exception {
       super.setUp();
        accountActivationFragment = new AccountActivationFragment();
        mPbActivateSpinner= new ProgressBar(getInstrumentation().getContext());
    }
    @Test
    public void testAccess()
    {
        assertNotNull(accountActivationFragment);
    }


    @Test
    public void testinitUI(){
        Method method = null;
        View view = new View(getInstrumentation().getContext());
        try {
            method = AccountActivationFragment.class.getDeclaredMethod("initUI", View.class);
            method.setAccessible(true);
            method.invoke(accountActivationFragment,view);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHandleUiState(){
        Method method = null;
        try {
            method =AccountActivationFragment.class.getDeclaredMethod("handleUiState");;
            method.setAccessible(true);
            method.invoke(accountActivationFragment);
            assertNotNull(UserRegistrationInitializer.getInstance().isJanrainIntialized());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void testShowActivateSpinner(){
        Method method = null;
        try {
            method =AccountActivationFragment.class.getDeclaredMethod("showActivateSpinner");;
            method.setAccessible(true);
            method.invoke(accountActivationFragment);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testShowResendSpinner(){
        Method method = null;
        try {
            method =AccountActivationFragment.class.getDeclaredMethod("showResendSpinner");;
            method.setAccessible(true);
            method.invoke(accountActivationFragment);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testHideResendSpinner(){
        Method method = null;
        try {
            method =AccountActivationFragment.class.getDeclaredMethod("hideResendSpinner");;
            method.setAccessible(true);
            method.invoke(accountActivationFragment);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testUpdateActivationUIState(){
        Method method = null;
        try {
            method =AccountActivationFragment.class.getDeclaredMethod("updateActivationUIState");;
            method.setAccessible(true);
            method.invoke(accountActivationFragment);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void testLaunchWelcomeFragment(){
        Method method = null;
        try {
            method =AccountActivationFragment.class.getDeclaredMethod("completeRegistration");;
            method.setAccessible(true);
            method.invoke(accountActivationFragment);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void testLaunchAlmostFragment(){
        Method method = null;
        try {
            method =AccountActivationFragment.class.getDeclaredMethod("launchAlmostFragment");;
            method.setAccessible(true);
            method.invoke(accountActivationFragment);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testHandleRefreshUserFailed(){
        Method method = null;
        int error=10000;
        try {
            synchronized(this){//synchronized block

                try{
                    RLog.init();
                }catch(Exception ignored ){}
            }
            method =AccountActivationFragment.class.getDeclaredMethod("handleRefreshUserFailed",int.class);;
            method.setAccessible(true);
            method.invoke(accountActivationFragment,error);
            assertEquals(error, RegConstants.HSDP_ACTIVATE_ACCOUNT_FAILED);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void testHandleResendVerificationEmailSuccess(){
        Method method = null;
        try {
            synchronized(this){//synchronized block

                try{
                    RLog.init();
                }catch(Exception ignored){}
            }
            method =AccountActivationFragment.class.getDeclaredMethod("handleResendVerificationEmailSuccess");;
            method.setAccessible(true);
            method.invoke(accountActivationFragment);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void testUpdateResendUIState(){
        Method method = null;
        try {
            method =AccountActivationFragment.class.getDeclaredMethod("updateResendUIState");;
            method.setAccessible(true);
            method.invoke(accountActivationFragment);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void testHandleResendVerificationEmailFailedWithError(){
        Method method = null;
        UserRegistrationFailureInfo userRegistrationFailureInfo= new UserRegistrationFailureInfo(mock(Context.class));
        try {
            synchronized(this){//synchronized block

                try{
                    RLog.init();
                }catch(Exception ignored){}
            }
            method =AccountActivationFragment.class.getDeclaredMethod("handleResendVerificationEmailFailedWithError",UserRegistrationFailureInfo.class);;
            method.setAccessible(true);
            method.invoke(accountActivationFragment,userRegistrationFailureInfo);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}