package com.philips.platform.csw;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import com.philips.platform.catk.CatkConstants;
import com.philips.platform.csw.mock.*;
import com.philips.platform.csw.utils.CustomRobolectricRunner;
import com.philips.platform.csw.wrapper.CswFragmentWrapper;
import com.philips.platform.mya.consentwidgets.R;
import com.philips.platform.uappframework.listener.ActionBarListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

@RunWith(CustomRobolectricRunner.class)
@Ignore
@Config(constants = com.philips.platform.mya.consentaccesstoolkit.BuildConfig.class, sdk = 25)
public class CswFragmentTest {

    @Before
    public void setup() {
        fragmentTransactionMock = new FragmentTransactionMock();
        fragmentManagerMock = new FragmentManagerMock(fragmentTransactionMock);
        fragment = new CswFragmentWrapper();
        fragment.fragmentManagerMock = fragmentManagerMock;
        fragment.setChildFragmentManager(fragmentManagerMock);
    }

    @Test
    public void getFragmentCount() throws Exception {
        givenFragmentCountIs(3);
        whenGetFragmetCountIsInvoked();
        thenFragmentCountIs(3);
    }

    //@Test
    public void onCreateView_setsApplicationAndPropositionName() throws Exception {
        givenArgumentsAre(APPLICATION_NAME, PROPOSITION_NAME);
        whenOnCreateViewIsInvoked();
        thenApplicationNameIs(APPLICATION_NAME);
        thenPropositionNameIs(PROPOSITION_NAME);
    }

    @Test
    public void onCreateView_DoesNotSetApplicationAndPropositionName_WhenArgumentsAreNull() throws Exception {
        givenNullArguments();
        whenOnCreateViewIsInvoked();
        thenApplicationNameIs(null);
        thenPropositionNameIs(null);
    }

    //@Test
    public void onCreateView_InvokesInflatorWthRightParams() throws Exception {
        givenArgumentsAre(APPLICATION_NAME, PROPOSITION_NAME);
        whenOnCreateViewIsInvoked();
        thenPermissionViewIsInflatedWith(R.id.csw_frame_layout_view_container, APPLICATION_NAME, PROPOSITION_NAME);
    }

    //@Test
    public void onCreateView_InflatesPermissionView() throws Exception {
        givenArgumentsAre(APPLICATION_NAME, PROPOSITION_NAME);
        whenOnCreateViewIsInvoked();
        thenInflatorInflateIsCalledWith(R.layout.csw_fragment_consent_widget_root, null, false);
    }

    @Test
    public void onViewStateRestored_ReadsApplicationNameAndPropositionNameFromState() {
        givenBundleStateWithValues(APPLICATION_NAME, PROPOSITION_NAME);
        whenOnViewStateRestoredIsInvoked();
        thenApplicationNameIs(APPLICATION_NAME);
        thenPropositionNameIs(PROPOSITION_NAME);
    }

    @Test
    public void onViewStateRestored_DoesNotSetAppNameAndPropName_WhenStateIsNull() {
        whenOnViewStateRestoredIsInvoked();
        thenApplicationNameIs(null);
        thenPropositionNameIs(null);
    }

    @Test
    public void onViewStateSave_savesApplicationNameAndPropositionNameToState() {
        givenEmptyBundleState();
        givenFragmentApplicationAndPropositionNamesAre(APPLICATION_NAME, PROPOSITION_NAME);
        whenOnViewStateSaveIsInvoked();
        thenStateContainsApplicationName(APPLICATION_NAME);
        thenStateContainsPropositionName(PROPOSITION_NAME);
    }

    @Test
    public void onViewStateSave_savesApplicationNameAndPropositionNameToState_WhenStateIsNull() {
        whenOnViewStateSaveIsInvoked();
        Assert.assertNull(state);
    }

    private void givenFragmentApplicationAndPropositionNamesAre(String applicationName, String propositionName) {
        Bundle bundle = new Bundle();
        bundle.putString(PROPOSITION_NAME_KEY, propositionName);
        bundle.putString(APPLICATION_NAME_KEY, applicationName);
        fragment.onViewStateRestored(bundle);
    }

    @Test
    public void onViewStateSave_DoesNotSetAppNameAndPropName_WhenStateIsNull() {
        whenOnViewStateRestoredIsInvoked();
        thenApplicationNameIs(null);
        thenPropositionNameIs(null);
    }

    @Test
    public void onBackPressed_handlesEmptyBackStack() throws Exception {
        givenBackStackDepthIs(0);
        whenOnBackPressedIsCalled();
        thenOnBackPressedReturns(true);
    }

    @Test
    public void onBackPressed_handlesNonEmptyBackStack() throws Exception {
        givenBackStackDepthIs(1);
        whenOnBackPressedIsCalled();
        thenOnBackPressedReturns(false);
        thenPopBackStackWasCalled();
    }

    @Test
    public void onBackPressedFinishesActivity_WhenFragmentManagerNull() throws Exception {
        givenFragmentManagerIsNull();
        whenOnBackPressedIsCalled();
        thenCurrentActivityIsFinished();
    }

    private void thenCurrentActivityIsFinished() {
        assertTrue(fragment.fragmentActivity.finishWasCalled);
    }

    private void givenFragmentManagerIsNull() {
        fragment.setChildFragmentManager(null);
    }

    @Test
    public void handleBackEvent_returnsNotHandledWhenBackStackCountIsZero() throws Exception {
        givenBackStackDepthIs(0);
        whenHandleBackEventIsCalled();
        thenEventIsNotHandled();
    }

    @Test
    public void handleBackEvent_returnsHandledWhenBackStackCountIsNonZero() throws Exception {
        givenBackStackDepthIs(1);
        whenHandleBackEventIsCalled();
        thenEventIsHandled();
    }

    @Test
    public void setsAndGetsResourceId() {
        givenResourceIdIsSetTo(123);
        whenGetResourceIdIsInvoked();
        thenResourceIdReturnedIs(123);
    }

    @Test
    public void setsAndGetsUpdateTitleListener() {
        givenUpdateTitleListenerIsSet(actionBarListenerMock);
        whenGetUpdateTitleListener();
        thenUpdateTitleListenerReturnedIs(actionBarListenerMock);
    }

    private void thenUpdateTitleListenerReturnedIs(ActionBarListener expectedActionBarListener) {
        assertEquals(expectedActionBarListener, actualActionBarListener);
    }

    private void whenGetUpdateTitleListener() {
        actualActionBarListener = fragment.getUpdateTitleListener();
    }

    private void givenUpdateTitleListenerIsSet(ActionBarListener actionBarListener) {
        fragment.setOnUpdateTitleListener(actionBarListener);
    }

    private void givenNullArguments() {
        fragment.setArguments(null);
    }

    private void whenOnViewStateRestoredIsInvoked() {
        fragment.onViewStateRestored(state);
    }

    private void whenOnViewStateSaveIsInvoked() {
        fragment.onSaveInstanceState(state);
    }

    private void givenBundleStateWithValues(String applicationName, String propositionName) {
        state = new Bundle();
        state.putString(PROPOSITION_NAME_KEY, propositionName);
        state.putString(APPLICATION_NAME_KEY, applicationName);
    }

    private void thenResourceIdReturnedIs(int expectedResourceId) {
        assertEquals(expectedResourceId, actualResourceId);
    }

    private void whenGetResourceIdIsInvoked() {
        actualResourceId = fragment.getResourceID();
    }

    private void givenResourceIdIsSetTo(int resourceId) {
        fragment.setResourceID(resourceId);
    }

    private void givenEmptyBundleState() {
        state = new Bundle();
    }

    private void givenOnClickIsMocked() {
        fragment.mockOnBackPressed = true;
    }

    private void thenPermissionViewIsInflatedWith(int csw_frame_layout_view_container, String applicationName, String propositionName) {
        assertEquals(csw_frame_layout_view_container, fragmentTransactionMock.replace_containerId);
        assertEquals(applicationName, fragmentTransactionMock.replace_fragment.getArguments().get("appName"));
        assertEquals(propositionName, fragmentTransactionMock.replace_fragment.getArguments().get("propName"));
    }

    private void thenInflatorInflateIsCalledWith(int csw_fragment_consent_widget_root, Object resource, boolean attachToRoot) {
        assertEquals(csw_fragment_consent_widget_root, mockLayoutInflater.usedResource);
        assertEquals(resource, mockLayoutInflater.usedViewGroup);
        assertEquals(attachToRoot, mockLayoutInflater.usedAttachToRoot);
    }

    private void thenPropositionNameIs(String propositionName) {
        Assert.assertEquals(propositionName, fragment.getPropositionName());
    }

    private void thenApplicationNameIs(String applicationName) {
        Assert.assertEquals(applicationName, fragment.getApplicationName());
    }

    private void thenStateContainsApplicationName(String expected) {
        Assert.assertEquals(expected, state.getString(CatkConstants.BUNDLE_KEY_APPLICATION_NAME));
    }

    private void thenStateContainsPropositionName(String expected) {
        Assert.assertEquals(expected, state.getString(CatkConstants.BUNDLE_KEY_PROPOSITION_NAME));
    }

    private void whenOnCreateViewIsInvoked() {
        fragment.onCreateView(mockLayoutInflater, null, null);
    }

    private void givenArgumentsAre(String applicationName, String propositionName) {
        //fragment.setArguments(applicationName, propositionName);
    }

    private void givenBackStackDepthIs(int depth) {
        fragmentManagerMock.backStackCount = depth;
    }

    private void givenViewWithId(int id) {
        View view = new View(new ContextMock());
        view.setId(id);
    }

    private void whenOnBackPressedIsCalled() {
        onBackPressed_return = fragment.onBackPressed();
    }

    private void whenHandleBackEventIsCalled() {
        handleBackEvent_return = fragment.handleBackEvent();
    }

    private void thenPopBackStackWasCalled() {
        assertTrue(fragmentManagerMock.popBackStack_wasCalled);
    }

    private void thenOnBackPressedReturns(boolean expectedReturn) {
        assertEquals(expectedReturn, onBackPressed_return);
    }

    private void thenFragmentCountIs(int expectedCount) {
        assertEquals(expectedCount, actualFragmentCount);
    }

    private void whenGetFragmetCountIsInvoked() {
        actualFragmentCount = fragment.getFragmentCount();
    }

    private void givenFragmentCountIs(int numberOfFragments) {
        fragmentManagerMock.fragments = new ArrayList<Fragment>();
        for (int i = 0; i < numberOfFragments; i++) {
            fragmentManagerMock.fragments.add(new Fragment());
        }
    }

    private void thenEventIsNotHandled() {
        assertFalse(handleBackEvent_return);
    }

    private void thenEventIsHandled() {
        assertTrue(handleBackEvent_return);
    }

    private void thenOnBackPressedIsCalled() {
        Assert.assertTrue(fragment.onBackPressedInvoked);
    }

    private FragmentManagerMock fragmentManagerMock;
    private boolean onBackPressed_return;
    private boolean handleBackEvent_return;
    private FragmentTransactionMock fragmentTransactionMock;
    private CswFragmentWrapper fragment;
    private Bundle state;
    private View view;
    private int actualFragmentCount;
    private int actualResourceId;
    private ActionBarListener actualActionBarListener;
    private ActionBarListenerMock actionBarListenerMock = new ActionBarListenerMock();
    private static final String APPLICATION_NAME_KEY = "appName";
    private static final String PROPOSITION_NAME_KEY = "propName";
    private static final String PROPOSITION_NAME = "PROPOSITION_NAME";
    private static final String APPLICATION_NAME = "APPLICATION_NAME";

    LayoutInflatorMock mockLayoutInflater = LayoutInflatorMock.createMock();
}