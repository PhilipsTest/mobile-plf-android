/**
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.cdp2.ews.view;

import com.philips.cdp2.ews.setupsteps.FirstSetupStepsFragment;
import com.philips.cdp2.ews.tagging.EWSTagger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EWSTagger.class)
public class FirstSetupStepsFragmentTest {

    private FirstSetupStepsFragment subject;

    @Before
    public void setUp() throws Exception {
        mockStatic(EWSTagger.class);
        subject = new FirstSetupStepsFragment();

    }

    @Test
    public void itShouldReturnCorrectPageNameForTagging() throws Exception {
        assertEquals("setupStep1", subject.getPageName());
    }

    @Test
    public void itShouldCalltrackPageOnResume() throws Exception {
        subject.onResume();
        verifyStatic();
        EWSTagger.trackPage("setupStep1");
    }
}