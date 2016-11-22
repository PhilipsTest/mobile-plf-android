/*
 * (C) Koninklijke Philips N.V., 2016.
 * All rights reserved.
 *
 */
package com.philips.platform.uit.components.ProgressIndicators;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.content.ContextCompat;

import com.philips.platform.uit.activity.BaseTestActivity;
import com.philips.platform.uit.matcher.FunctionDrawableMatchers;
import com.philips.platform.uit.matcher.ProgressBarMatcher;
import com.philips.platform.uit.utils.TestConstants;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.philips.platform.uit.test.R.color.GroupBlue45;
import static com.philips.platform.uit.utils.UITTestUtils.modulateColorAlpha;

public class CircularProgressIndicators {

    private Context activityContext;
    private Context instrumentationContext;

    @Rule
    public ActivityTestRule<BaseTestActivity> mActivityTestRule = new ActivityTestRule<>(BaseTestActivity.class);
    private Resources testResources;

    @Before
    public void setUp() {
        final BaseTestActivity activity = mActivityTestRule.getActivity();
        activity.switchTo(com.philips.platform.uit.test.R.layout.layout_progressbar);
        testResources = getInstrumentation().getContext().getResources();
        instrumentationContext = getInstrumentation().getContext();
        activityContext = activity;
    }

    //*********************************Layout TestScenarios**************************//
    @Test
    public void verifyHeightOfSmallCircularProgressIndicator() {
    int expectedHeight = testResources.getDimensionPixelSize(com.philips.platform.uit.test.R.dimen.circularprogressbar_small_heightwidth);
        getSmallCircularProgressBar().check(matches(FunctionDrawableMatchers.isSameHeight(TestConstants.FUNCTION_GET_PROGRESS_DRAWABLE, expectedHeight)));
    }

    @Test
    public void verifyWidthOfSmallCircularProgressIndicator() {
        int expectedWidth = testResources.getDimensionPixelSize(com.philips.platform.uit.test.R.dimen.circularprogressbar_small_heightwidth);
        getSmallCircularProgressBar().check(matches(FunctionDrawableMatchers.isSameWidth(TestConstants.FUNCTION_GET_PROGRESS_DRAWABLE, expectedWidth)));
    }

    @Test
    public void verifyHeightOfMediumCircularProgressIndicator() {
        int expectedHeight = testResources.getDimensionPixelSize(com.philips.platform.uit.test.R.dimen.circularprogressbar_middle_heightwidth);
        getMiddleCircularProgressBar().check(matches(FunctionDrawableMatchers.isSameHeight(TestConstants.FUNCTION_GET_PROGRESS_DRAWABLE, expectedHeight)));
    }

    @Test
    public void verifyWidthOfMediumCircularProgressIndicator() {
        int expectedWidth = testResources.getDimensionPixelSize(com.philips.platform.uit.test.R.dimen.circularprogressbar_middle_heightwidth);
        getMiddleCircularProgressBar().check(matches(FunctionDrawableMatchers.isSameWidth(TestConstants.FUNCTION_GET_PROGRESS_DRAWABLE, expectedWidth)));
    }

    @Test
    public void verifyHeightOfBigCircularProgressIndicator() {
        int expectedHeight = testResources.getDimensionPixelSize(com.philips.platform.uit.test.R.dimen.circularprogressbar_large_heightwidth);
        getLargeCircularProgressBar().check(matches(FunctionDrawableMatchers.isSameHeight(TestConstants.FUNCTION_GET_PROGRESS_DRAWABLE, expectedHeight)));
    }

    @Test
    public void verifyWidthOfBigCircularProgressIndicator() {
        int expectedWidth = testResources.getDimensionPixelSize(com.philips.platform.uit.test.R.dimen.circularprogressbar_large_heightwidth);
        getLargeCircularProgressBar().check(matches(FunctionDrawableMatchers.isSameWidth(TestConstants.FUNCTION_GET_PROGRESS_DRAWABLE, expectedWidth)));
    }

    @Test
    public void verifyDurationOfBigCircularProgressIndicator() {
//        getLargeCircularProgressBar().check(matches(ProgressBarMatcher.isSameDuration(1250)));
    }

    @Test
    public void verifyIndeterminateSmallCircularPBThickness() {
//        int thickness = 20;
//        getSmallIndeterminateCircularProgressBar()
//                .check(matches(FunctionDrawableMatchers.isSameRingThickness(TestConstants.FUNCTION_GET_INDETERMINATE_DRAWABALE, circularprogressID(), thickness)));
    }

    @Test
    public void verifyIndeterminateSmallCircularPBRadius() {
//        int thickness = 25;
//        getSmallIndeterminateCircularProgressBar()
//                .check(matches(FunctionDrawableMatchers.isSameInnerRadius(TestConstants.FUNCTION_GET_INDETERMINATE_DRAWABALE, circularprogressID(), thickness)));
    }

    @Test
    public void verifyIndeterminateSmallCircularPBStartColor() {
        int startColor = Color.TRANSPARENT;
        getSmallIndeterminateCircularProgressBar()
                .check(matches(FunctionDrawableMatchers.isSameColors(TestConstants.FUNCTION_GET_INDETERMINATE_DRAWABALE, circularprogressID(), startColor, 0)));
    }

    @Test
    public void verifyIndeterminateSmallCircularPBEndColor() {
//        int startColor = Color.TRANSPARENT;
//        getSmallIndeterminateCircularProgressBar()
//                .check(matches(FunctionDrawableMatchers.isSameColors(TestConstants.FUNCTION_GET_INDETERMINATE_DRAWABALE, circularprogressID(), startColor, 1)));
    }
    //*********************************Theming TestScenarios**************************//
//
    @Test
    public void verifyCircularProgressBarProgressColor() {
        final int expectedProgressBarProgressColor = ContextCompat.getColor(instrumentationContext, GroupBlue45);
        getSmallCircularProgressBar().check(matches(FunctionDrawableMatchers.isSameColor(TestConstants.FUNCTION_GET_PROGRESS_DRAWABLE, android.R.attr.enabled, expectedProgressBarProgressColor, circularprogressID(), true)));
    }

    @Test
    public void verifyProgressBarBackgroundColor() {
        final int expectedProgressBarBackgroundColor = modulateColorAlpha(ContextCompat.getColor(instrumentationContext, GroupBlue45), 0.15f);
        getSmallCircularProgressBar().check(matches(FunctionDrawableMatchers.isSameColor(TestConstants.FUNCTION_GET_PROGRESS_DRAWABLE, android.R.attr.enabled, expectedProgressBarBackgroundColor, circularprogressBackgroundID(), true)));
    }


    private ViewInteraction getSmallCircularProgressBar() {
        return onView(withId(com.philips.platform.uit.test.R.id.progress_bar_determinate_circular_small));
    }

    private ViewInteraction getMiddleCircularProgressBar() {
        return onView(withId(com.philips.platform.uit.test.R.id.progress_bar_determinate_circular_middle));
    }

    private ViewInteraction getLargeCircularProgressBar() {
        return onView(withId(com.philips.platform.uit.test.R.id.progress_bar_determinate_circular_big));
    }

    private ViewInteraction getSmallIndeterminateCircularProgressBar() {
        return onView(withId(com.philips.platform.uit.test.R.id.progress_bar_indeterminate_circular_small));
    }

    private int circularprogressID() {
        return android.R.id.progress;
    }

    private int circularprogressBackgroundID() {
        return android.R.id.background;
    }
}