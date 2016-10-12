/**
 * (C) Koninklijke Philips N.V., 2016.
 * All rights reserved.
 */
package com.philips.platform.uit.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.content.ContextCompat;


import com.philips.platform.uit.R;
import com.philips.platform.uit.matcher.FunctionDrawableMatchers;
import com.philips.platform.uit.matcher.TextViewPropertiesMatchers;
import com.philips.platform.uit.matcher.ViewPropertiesMatchers;
import com.philips.platform.uit.utils.TestConstants;
import com.philips.platform.uit.utils.UITTestUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.philips.platform.uit.test.R.color.GroupBlue35;
import static com.philips.platform.uit.test.R.color.GroupBlue75;
import static com.philips.platform.uit.utils.UITTestUtils.modulateColorAlpha;

public class AlternativePrimaryButtonWithTextOnlyTest {

    private Resources testResources;
    private Context instrumentationContext;

    @Rule
    public ActivityTestRule<BaseTestActivity> mActivityTestRule = new ActivityTestRule<>(BaseTestActivity.class);

    @Before
    public void setUp() {
        final Activity activity = mActivityTestRule.getActivity();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setContentView(com.philips.platform.uit.test.R.layout.layout_buttons);
            }
        });
        testResources = getInstrumentation().getContext().getResources();
        instrumentationContext = getInstrumentation().getContext();
    }

    /*****************************************
     * Layout Scenarios
     *********************************************/

    @Test
    public void verifyAltButtonHeight() {
        UITTestUtils.waitFor(testResources, 750);
        int expectedHeight = (int) testResources.getDimension(com.philips.platform.uit.test.R.dimen.button_height);
        getPrimaryButton()
                .check(matches(FunctionDrawableMatchers.isSameHeight(TestConstants.FUNCTION_GET_BACKGROUND, expectedHeight)));
    }

    @Test
    public void verifyAltButtonLeftPadding() {
        int expectedLeftPadding = testResources.getDimensionPixelSize(com.philips.platform.uit.test.R.dimen.button_left_padding);
        getPrimaryButton().check(matches(ViewPropertiesMatchers.isSameLeftPadding(expectedLeftPadding)));
    }

    @Test
    public void verifyAltButtonRightPadding() {
        int expectedRightPadding = testResources.getDimensionPixelSize(com.philips.platform.uit.test.R.dimen.button_right_padding);
        getPrimaryButton().check(matches(ViewPropertiesMatchers.isSameRightPadding(expectedRightPadding)));
    }

    @Test
    public void verifyAltButtonCornerRadius() {
        float radius = testResources.getDimensionPixelSize(com.philips.platform.uit.test.R.dimen.button_cornerradius);
        getPrimaryButton().check(matches(FunctionDrawableMatchers.isSameRadius(TestConstants.FUNCTION_GET_BACKGROUND, 0, radius)));
    }



    // TODO: 9/14/2016
    @Ignore
    public void verifyAltButtonFontType() {

    }

    @Test
    public void verifyAltButtonFontSize() {
        float expectedFontSize = testResources.getDimensionPixelSize(com.philips.platform.uit.test.R.dimen.button_font_size);
        getPrimaryButton().check(matches(TextViewPropertiesMatchers.isSameFontSize(expectedFontSize)));
    }



    /*******************************************************
     * Theming
     ******************************************************/

    @Test
    public void verifyAltPrimaryTextOnlyButtonControlColorULTone() {
        TypedArray typedArray = mActivityTestRule.getActivity().getTheme().obtainStyledAttributes(new int[]{R.attr.uitColorLevel75});
        final int expectedColor = typedArray.getColor(0,Color.MAGENTA);
        getPrimaryButton().check(matches(FunctionDrawableMatchers
                .isSameColorFromColorList(TestConstants.FUNCTION_GET_SUPPORT_BACKROUND_TINT_LIST, android.R.attr.state_enabled, expectedColor)));
    }

    // TODO: 9/20/2016 Fix this failing test case.
    @Ignore
    @Test
    public void verifyPrimaryTextOnlyPressedButtonControlColorULTone() {
        final int expectedColor = ContextCompat.getColor(instrumentationContext, GroupBlue35);
        getPrimaryButton().check(matches(FunctionDrawableMatchers
                .isSameColorFromColorList(TestConstants.FUNCTION_GET_SUPPORT_BACKROUND_TINT_LIST, android.R.attr.state_pressed, expectedColor)));
    }

    @Test
    public void verifyPrimaryTextOnlyDisabledButtonControlColorULTone() {
        final int disabledColor = modulateColorAlpha(Color.parseColor("#1C2D66"), 0.25f);
        disableAllViews();
        getPrimaryButton().check(matches(FunctionDrawableMatchers
                .isSameColorFromColorList(TestConstants.FUNCTION_GET_SUPPORT_BACKROUND_TINT_LIST, -android.R.attr.enabled, disabledColor)));
    }

    @Test
    public void verifyPrimaryTextOnlyButtonFontColor() {
        getPrimaryButton().check(matches(TextViewPropertiesMatchers.isSameTextColor(android.R.attr.state_enabled, Color.WHITE)));
    }

    @Ignore
    @Test
    public void verifyPrimaryTextOnlyPressedButtonFontColor() {
        getPrimaryButton().check(matches(TextViewPropertiesMatchers.isSameTextColor(android.R.attr.state_pressed, Color.WHITE)));
    }

    @Test
    public void verifyPrimaryTextOnlyDisabledButtonFontColor() {
        disableAllViews();
        final int disabledTextColor = UITTestUtils.modulateColorAlpha(Color.WHITE, 0.25f);
        getPrimaryButton().check(matches(TextViewPropertiesMatchers.isSameTextColor(-android.R.attr.enabled, disabledTextColor)));
    }

    private ViewInteraction getPrimaryButton() {
        return onView(withId(com.philips.platform.uit.test.R.id.alt_primary_button));
    }

    private void disableAllViews() {
        onView(withId(com.philips.platform.uit.test.R.id.disable_switch)).perform(ViewActions.click());
    }
}