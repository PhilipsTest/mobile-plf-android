/*
 * (C) Koninklijke Philips N.V., 2016.
 * All rights reserved.
 *
 */
package com.philips.platform.uid.components.toggleswitch;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.content.ContextCompat;

import com.philips.platform.uid.R;
import com.philips.platform.uid.activity.BaseTestActivity;
import com.philips.platform.uid.matcher.FunctionDrawableMatchers;
import com.philips.platform.uid.matcher.ViewPropertiesMatchers;
import com.philips.platform.uid.utils.TestConstants;
import com.philips.platform.uid.utils.UIDTestUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.philips.platform.uid.test.R.color.GroupBlue10;
import static com.philips.platform.uid.test.R.color.GroupBlue45;
import static com.philips.platform.uid.utils.UIDTestUtils.modulateColorAlpha;

public class ToggleSwitchTest {

    private Context activityContext;
    private Context instrumentationContext;

    @Rule
    public ActivityTestRule<BaseTestActivity> mActivityTestRule = new ActivityTestRule<>(BaseTestActivity.class);
    private Resources testResources;

    @Before
    public void setUp() {
        final BaseTestActivity activity = mActivityTestRule.getActivity();
        activity.switchTo(com.philips.platform.uid.test.R.layout.layout_toggle_switch);
        testResources = getInstrumentation().getContext().getResources();
        instrumentationContext = getInstrumentation().getContext();
        activityContext = activity;
    }

    //*********************************Toggle Switch Layout TestScenarios**************************//
    @Test
    public void verifyToggleSwitchWidth() {
        UIDTestUtils.waitFor(testResources, 750);
        int expectedWidth = testResources.getDimensionPixelSize(com.philips.platform.uid.test.R.dimen.toggleswitch_width);
        getToggleSwitch().check(matches(ViewPropertiesMatchers.isSameViewWidth(expectedWidth)));
    }

    @Test
    public void verifyToggleSwitchHeight() {
        UIDTestUtils.waitFor(testResources, 750);
        int expectedWidth = testResources.getDimensionPixelSize(com.philips.platform.uid.test.R.dimen.toggleswitch_thumb_height);
        getToggleSwitch().check(matches(ViewPropertiesMatchers.isSameViewHeight(expectedWidth)));
    }

    @Test
    public void verifyToggleSwitchTrackWidth() {
        UIDTestUtils.waitFor(testResources, 750);
        int expectedWidth = testResources.getDimensionPixelSize(com.philips.platform.uid.test.R.dimen.toggleswitch_track_width);
        getToggleSwitch().check(matches(FunctionDrawableMatchers.isSameWidth(trackFunction(), expectedWidth, trackID())));
    }

    @Test
    public void verifyToggleSwitchTrackHeight() {
        UIDTestUtils.waitFor(testResources, 750);
        int expectedHeight = testResources.getDimensionPixelSize(com.philips.platform.uid.test.R.dimen.toggleswitch_track_height);
        getToggleSwitch().check(matches(FunctionDrawableMatchers.isSameHeight(trackFunction(), expectedHeight, trackID())));
    }

    @Test
    public void verifyToggleSwitchThumbHeight() {
        UIDTestUtils.waitFor(testResources, 750);
        int expectedHeight = testResources.getDimensionPixelSize(com.philips.platform.uid.test.R.dimen.toggleswitch_thumb_height);
        getToggleSwitch()
                .check(matches(FunctionDrawableMatchers.isSameHeight(TestConstants.FUNCTION_GET_THUMB_DRAWABLE, expectedHeight)));
    }

    @Test
    public void verifyToggleSwitchThumbWidth() {
        UIDTestUtils.waitFor(testResources, 750);
        int expectedWidth = testResources.getDimensionPixelSize(com.philips.platform.uid.test.R.dimen.toggleswitch_thumb_height);
        getToggleSwitch()
                .check(matches(FunctionDrawableMatchers.isSameWidth(TestConstants.FUNCTION_GET_THUMB_DRAWABLE, expectedWidth)));
    }

    @Test
    public void verifyToggleSwitchCornerRadius() {
        float radius = (float) Math.floor(testResources.getDimensionPixelSize(com.philips.platform.uid.test.R.dimen.toggleswitch_corner_radius));
        getToggleSwitch().check(matches(FunctionDrawableMatchers.isSameRadius(trackFunction(), 0, radius)));
    }

    @Test
    public void verifyToggleSwitchNotCheckedOnEnabledTap() {
        getToggleSwitch().perform(ViewActions.click());
        getToggleSwitch().check(matches(isNotChecked()));
    }

    @Test
    public void verifyToggleSwitchCheckedOnDoubleEnabledTap() {
        getToggleSwitch().perform(ViewActions.click());
        getToggleSwitch().perform(ViewActions.click());
        getToggleSwitch().check(matches(isChecked()));
    }

    @Test
    public void verifyThumbHighlightRadius() {
        int radius = testResources.getDimensionPixelSize(com.philips.platform.uid.test.R.dimen.toggleswitch_ripple_radius);
        getToggleSwitch().check(matches(FunctionDrawableMatchers.isSameRippleRadius(TestConstants.FUNCTION_GET_BACKGROUND, radius)));
    }

    @Test
    public void verifyThumbHighlightColor() {
        int color = modulateColorAlpha(Color.parseColor("#1474A4"), 0.20f);
        getToggleSwitch().
                check(matches(FunctionDrawableMatchers.
                        isSameRippleColor(TestConstants.FUNCTION_GET_BACKGROUND, android.R.attr.state_enabled, color)));
    }

    @Test
    public void verifySwitchOffIfSlideRightOnSwitchOn() {
        getToggleSwitch().perform(ViewActions.swipeRight());
        getToggleSwitch().check(matches(isNotChecked()));
    }

    @Test
    public void verifySwitchRemainsOnIfSlideRightTwiceOnSwitchOn() {
        getToggleSwitch().perform(ViewActions.swipeRight());
        getToggleSwitch().perform(ViewActions.swipeLeft());
        getToggleSwitch().check(matches(isChecked()));
    }
    //*********************************Toggle Switch Theming TestScenarios**************************//

    @Test
    public void verifySwitchThumbFillColor() {
        getToggleSwitch().check(matches(FunctionDrawableMatchers
                .isSameColorFromColorList(TestConstants.FUNCTION_GET_SWITCH_THUMB_TINT_LIST, android.R.attr.state_enabled, Color.WHITE)));
    }

    // TODO: 11/2/2016  How to fix this. Always returns disabled color combination ???
    @Ignore
    @Test
    public void verifyToggleSwitchTrackOnColorTest() {
        final int expectedTrackEnabledColor = ContextCompat.getColor(instrumentationContext, GroupBlue45);
        getToggleSwitch().check(matches(isChecked()));
        getToggleSwitch().check(matches(FunctionDrawableMatchers
                .isSameColor(trackFunction(), android.R.attr.state_checked, expectedTrackEnabledColor, trackID())));
    }

    @Test
    public void verifyToggleSwitchTrackOffColorTest() {
        final int expectedTrackOffEnabledColor = modulateColorAlpha(Color.parseColor("#1474A4"), 0.30f);
        getToggleSwitch().check(matches(FunctionDrawableMatchers
                .isSameColor(trackFunction(), android.R.attr.state_enabled, expectedTrackOffEnabledColor, trackID())));
    }

    @Test
    public void verifyDisabledToggleSwitchTrackFillColorTest() {
        final int expectedTrackOffEnabledColor = modulateColorAlpha(ContextCompat.getColor(instrumentationContext, GroupBlue45), 0.35f);
        getToggleSwitch().check(matches(FunctionDrawableMatchers.isSameColor(trackFunction(), -android.R.attr.enabled, expectedTrackOffEnabledColor, trackID())));
    }

    @Test
    public void verifyDisabledToggleSwitchThumbFillColorTest() {
        final int disableThumbColor = ContextCompat.getColor(instrumentationContext, GroupBlue10);
        getToggleSwitch().check(matches(FunctionDrawableMatchers.isSameColor(TestConstants.FUNCTION_GET_THUMB_DRAWABLE, -android.R.attr.enabled, disableThumbColor)));
    }

    private ViewInteraction getToggleSwitch() {
        return onView(withId(com.philips.platform.uid.test.R.id.toggle_switch));
    }

    private String trackFunction() {
        return TestConstants.FUNCTION_GET_UID_TRACK_DRAWABLE;
    }

    private int trackID() {
        return R.id.uid_id_switch_track;
    }
}