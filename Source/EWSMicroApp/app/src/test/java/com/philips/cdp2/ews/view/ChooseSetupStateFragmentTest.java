/*
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.cdp2.ews.view;

import com.philips.cdp2.ews.injections.EWSComponent;
import com.philips.cdp2.ews.tagging.Pages;
import com.philips.cdp2.ews.viewmodel.ChooseSetupStateViewModel;
import com.philips.cdp2.powersleep.R;
import com.philips.cdp2.powersleep.databinding.FragmentChooseSetupStateBinding;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChooseSetupStateFragmentTest {

    private ChooseSetupStateFragment fragment;

    @Mock
    private FragmentChooseSetupStateBinding viewModelBinderMock;
    @Mock
    private EWSComponent ewsComponentMock;
    @Mock
    private ChooseSetupStateViewModel viewModelMock;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        fragment = new ChooseSetupStateFragment();
        injectMembers();
    }

    private void injectMembers() {
        doAnswer(new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                fragment.viewModel = viewModelMock;
                return null;
            }
        }).when(ewsComponentMock).inject(fragment);
        fragment.inject(ewsComponentMock);
    }

    @Test
    public void shouldBindDataWhenAsked() throws Exception {
        fragment.bindViewModel(viewModelBinderMock);
        verify(viewModelBinderMock).setViewModel(viewModelMock);
    }

    @Test
    public void shouldReturnCorrectHierarchyLevelWhenAsked() throws Exception {
        assertEquals(ChooseSetupStateFragment.FRAGMENT_HIERARCHY_LEVEL, fragment.getHierarchyLevel());
    }

    @Test
    public void shouldReturnCorrectHLayoutIdWhenAsked() throws Exception {
        assertEquals(R.layout.fragment_choose_setup_state, fragment.getLayoutId());
    }

    @Test
    public void shouldReturnCorrectPageNameForTagging() throws Exception {
        assertEquals(Pages.CHOOSE_SETUP_STATE, fragment.getPageName());
    }
}