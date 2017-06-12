package com.philips.cdp.registration.coppa.ui.fragment;

import android.support.multidex.MultiDex;
import android.test.InstrumentationTestCase;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class ParentalAccessConfirmFragmentTest extends InstrumentationTestCase {

    ParentalAccessConfirmFragment parentalAccessConfirmFragment;

    @Override
    protected void setUp() throws Exception {
        MultiDex.install(getInstrumentation().getTargetContext());
        super.setUp();

        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
        parentalAccessConfirmFragment = new ParentalAccessConfirmFragment();
    }
    @Test
    public void testAssert(){
        assertNotNull(parentalAccessConfirmFragment);
    }
    @Test
    public void testUpdateUI(){
        Method method = null;
        try {
            method = ParentalAccessConfirmFragment.class.getDeclaredMethod("updateUI");
            method.setAccessible(true);
            method.invoke(parentalAccessConfirmFragment);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testShowParentalAccessDailog(){
        Method method = null;
        int minAge=19;
        try {
            String minAgeLimitTest = "text";
            minAgeLimitTest = String.format(minAgeLimitTest, minAge);

            method = ParentalAccessConfirmFragment.class.getDeclaredMethod("showParentalAccessDailog",int.class);
           method.setAccessible(true);
            method.invoke(parentalAccessConfirmFragment,minAge);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testValidateInputs(){
        Method method = null;
        try {
            method = ParentalAccessConfirmFragment.class.getDeclaredMethod("validateInputs");
            method.setAccessible(true);
            method.invoke(parentalAccessConfirmFragment);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}