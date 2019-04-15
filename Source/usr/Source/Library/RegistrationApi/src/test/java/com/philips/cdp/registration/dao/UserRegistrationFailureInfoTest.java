/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp.registration.dao;

import android.content.Context;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class UserRegistrationFailureInfoTest extends TestCase {

    private UserRegistrationFailureInfo userRegistrationFailureInfo;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        userRegistrationFailureInfo = new UserRegistrationFailureInfo(mock(Context.class));
    }

    @Test
    public void testErrorCode() {
        userRegistrationFailureInfo.setErrorCode(1);
        assertEquals(1, userRegistrationFailureInfo.getErrorCode());

    }

}
