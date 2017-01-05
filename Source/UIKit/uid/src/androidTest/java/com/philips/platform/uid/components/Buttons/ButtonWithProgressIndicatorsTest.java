package com.philips.platform.uid.components.buttons;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.philips.platform.uid.R;
import com.philips.platform.uid.activity.BaseTestActivity;
import com.philips.platform.uid.matcher.FunctionDrawableMatchers;
import com.philips.platform.uid.matcher.TextViewPropertiesMatchers;
import com.philips.platform.uid.matcher.ViewPropertiesMatchers;
import com.philips.platform.uid.utils.TestConstants;
import com.philips.platform.uid.utils.UIDTestUtils;
import com.philips.platform.uid.view.widget.ProgressIndicatorButton;

import org.hamcrest.Matcher;
import org.hamcrest.core.IsNot;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static com.philips.platform.uid.activity.BaseTestActivity.CONTENT_COLOR_KEY;
import static org.hamcrest.CoreMatchers.allOf;

public class ButtonWithProgressIndicatorsTest {
    private Resources testResources;
    private Context context;

    @Rule
    public ActivityTestRule<BaseTestActivity> mActivityTestRule = new ActivityTestRule<>(BaseTestActivity.class, false, false);
    private BaseTestActivity activity;

    public void setUpDefaultTheme() {
        final Intent intent = getIntent(0);
        activity = mActivityTestRule.launchActivity(intent);
        activity.switchTo(com.philips.platform.uid.test.R.layout.main_layout);

        activity.switchFragment(new ButtonsTestFragment());
        testResources = getInstrumentation().getContext().getResources();
        context = getInstrumentation().getContext();
    }

    @Test
    public void verifyProgressIndicatorProgressBarHeight() {
        setUpDefaultTheme();
        int expectedHeight = testResources.getDimensionPixelSize(com.philips.platform.uid.test.R.dimen.circularprogressbar_small_heightwidth);

        getProgressBar().check(matches(FunctionDrawableMatchers.isSameHeight(TestConstants.FUNCTION_GET_PROGRESS_DRAWABLE, expectedHeight)));
    }

    @Test
    public void verifyProgressIndicatorProgressBarWidth() {
        setUpDefaultTheme();
        int expectedWidth = testResources.getDimensionPixelSize(com.philips.platform.uid.test.R.dimen.circularprogressbar_small_heightwidth);

        getProgressBar().check(matches(FunctionDrawableMatchers.isSameWidth(TestConstants.FUNCTION_GET_PROGRESS_DRAWABLE, expectedWidth)));
    }

    @Test
    public void verifyProgressIndicatorMinHeight() {
        setUpDefaultTheme();
        int expectedHeight = testResources.getDimensionPixelSize(com.philips.platform.uid.test.R.dimen.uid_button_min_height);

        getDeterminateProgressIndicatorButton().check(matches(ViewPropertiesMatchers.isSameViewHeight(expectedHeight)));
    }

    @Test
    public void verifyProgressIndicatorMinWidth() {
        setUpDefaultTheme();
        int expectedWidth = testResources.getDimensionPixelSize(com.philips.platform.uid.test.R.dimen.uid_button_min_width);

        getDeterminateProgressIndicatorButton().check(matches(ViewPropertiesMatchers.isSameViewMinWidth(expectedWidth)));
    }

    @Test
    public void verifyMarginsBetweenProgressbarAndProgressTextWhenThereIsProgessText() {
        setUpDefaultTheme();

        final int progressIndicatorTextPadding = testResources.getDimensionPixelOffset(R.dimen.uid_progress_indicator_button_progress_text_padding);
        final int progressIndicatorLeftRightMargin = testResources.getDimensionPixelOffset(R.dimen.uid_button_padding_left_right);
        simulateSetProgressText("Hello ");

        //To check progress text is displayed
        getProgressText().check(matches(ViewPropertiesMatchers.isSameRightMargin(progressIndicatorLeftRightMargin)))
                .check(matches(ViewPropertiesMatchers.isSameLeftMargin(progressIndicatorTextPadding)))
                .check(matches(ViewMatchers.isDisplayed()));
        getProgressBar().check(matches(ViewPropertiesMatchers.isSameRightMargin(0)))
                .check(matches(ViewPropertiesMatchers.isSameLeftMargin(progressIndicatorLeftRightMargin)))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void verifyMarginsBetweenProgressbarAndProgressTextWhenProgressTextIsEmpty() {
        setUpDefaultTheme();

        final int progressIndicatorLeftRightMargin = testResources.getDimensionPixelOffset(R.dimen.uid_button_padding_left_right);
        simulateSetProgressText("");

        //To check progress text is not displayed
        getProgressText().check(matches(new IsNot(isDisplayed())));
        getProgressBar().check(matches(ViewPropertiesMatchers.isSameRightMargin(progressIndicatorLeftRightMargin)))
                .check(matches(ViewPropertiesMatchers.isSameLeftMargin(progressIndicatorLeftRightMargin)))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void verifyProgessTextColor() {
        setUpDefaultTheme();

        simulateSetProgressText("Hello ");
        final int color = UIDTestUtils.getAttributeColor(activity, R.attr.uidProgressIndicatorButtonProgressColor);
        final float alpha = UIDTestUtils.getFloatValueFromDimen(activity, R.dimen.uid_progress_indicator_button_label_alpha);
        final int colorAlpha = UIDTestUtils.modulateColorAlpha(color, alpha);
        //To check progress text is displayed
        getProgressText().check(matches(TextViewPropertiesMatchers.isSameTextColor(colorAlpha)));
    }

    @Test
    public void verifyProgressTextColor() {
        setUpDefaultTheme();

        simulateSetProgressText("Hello ");
        final int color = UIDTestUtils.getAttributeColor(activity, R.attr.uidControlPrimaryTextColor);

        getButton().check(matches(TextViewPropertiesMatchers.isSameTextColor(color)));
    }

    @Test
    public void verifyProgressTextSize() {
        setUpDefaultTheme();

        simulateSetProgressText("Hello ");
        final float textSize = testResources.getDimension(R.dimen.uid_label_text_size);

        getProgressText().check(matches(TextViewPropertiesMatchers.isSameFontSize(textSize)));
    }

    @Test
    public void verifyProgressTextFillColor() {
        setUpDefaultTheme();

        simulateSetProgressText("Hello ");
        final int color = UIDTestUtils.getAttributeColor(activity, R.attr.uidColorLevel45);

        getProgressText().check(matches(TextViewPropertiesMatchers.isSameTextColor(color)));
    }

    @Test
    public void verifyButtonFillColor() {
        setUpDefaultTheme();

        final int color = UIDTestUtils.getAttributeColor(activity, R.attr.uidControlPrimaryEnabledColor);

        getButton().check(matches(FunctionDrawableMatchers
                .isSameColorFromColorList(TestConstants.FUNCTION_GET_SUPPORT_BACKROUND_TINT_LIST, android.R.attr.state_enabled, color)));
    }

    @Test
    public void verifyButtonTextSize() {
        setUpDefaultTheme();

        final float textSize = testResources.getDimension(R.dimen.uid_label_text_size);
        getButton().check(matches(TextViewPropertiesMatchers.isSameFontSize(textSize)));
    }

    @Test
    public void verifyProgressButtonFillColor() {
        setUpDefaultTheme();

        final int colorWithAlpha = UIDTestUtils.getColorWithAlphaFromAttrs(activity, R.attr.uidProgressIndicatorButtonBackgroundColor, R.attr.uidProgressIndicatorButtonBackgroundAlpha);

        getDeterminateProgressIndicatorButton().check(matches(FunctionDrawableMatchers.isSameColor(TestConstants.FUNCTION_GET_BACKGROUND, 0, colorWithAlpha)));
    }

    @Test
    public void verifyProgressBarProgressColor() {
        setUpDefaultTheme();

        final int expectedProgressBarProgressColor = UIDTestUtils.getAttributeColor(activity, R.attr.uidColorLevel45);

        getProgressBar().check(matches(FunctionDrawableMatchers.isSameColor(TestConstants.FUNCTION_GET_PROGRESS_DRAWABLE, android.R.attr.enabled, expectedProgressBarProgressColor, android.R.id.progress, true)));
    }

    @Test
    public void verifyProgressTextFillColorInBright() {
        setupBrightTheme();

        simulateSetProgressText("Hello ");
        final int color = ContextCompat.getColor(activity, R.color.uidColorWhite);

        getProgressText().check(matches(TextViewPropertiesMatchers.isSameTextColor(color)));
    }

    @Test
    public void verifyIndeterminateSmallCircularPBStartColor() {
        setUpDefaultTheme();

        int expectedStartColor = Color.TRANSPARENT;
        getIndeterminateProgressBar()
                .check(matches(FunctionDrawableMatchers.isSameColors(TestConstants.FUNCTION_GET_INDETERMINATE_DRAWABALE, android.R.id.progress, expectedStartColor, 0)));
    }

    @Test
    public void verifyIndeterminateSmallCircularPBEndColor() {
        setUpDefaultTheme();
        final int expectedEndColor = UIDTestUtils.getAttributeColor(activity, R.attr.uidColorLevel45);

        getIndeterminateProgressBar()
                .check(matches(FunctionDrawableMatchers.isSameColors(TestConstants.FUNCTION_GET_INDETERMINATE_DRAWABALE, android.R.id.progress, expectedEndColor, 1)));
    }

    @Test
    public void verifyProgressTextFillColorForIndeterminateInBrightColorRange() {
        setupBrightTheme();

        simulateSetProgressText("Hello ");
        final int color = ContextCompat.getColor(activity, R.color.uidColorWhite);

        getIndeterminateProgressText().check(matches(TextViewPropertiesMatchers.isSameTextColor(color)));
    }

    private void setupBrightTheme() {
        final Intent intent = getIntent(2);

        activity = mActivityTestRule.launchActivity(intent);
        activity.switchTo(com.philips.platform.uid.test.R.layout.main_layout);
        activity.switchFragment(new ButtonsTestFragment());
    }

    @NonNull
    private Intent getIntent(final int contentColorIndex) {
        final Bundle bundleExtra = new Bundle();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        bundleExtra.putInt(CONTENT_COLOR_KEY, contentColorIndex);
        intent.putExtras(bundleExtra);
        return intent;
    }

    private void simulateSetProgressText(final String progressText) {
        getDeterminateProgressIndicatorButton().perform(new CustomViewAction(progressText));
    }

    private ViewInteraction getProgressText() {
        return onView(allOf(withId(com.philips.platform.uid.test.R.id.uid_progress_indicator_button_text),
                withParent(allOf(withId(com.philips.platform.uid.test.R.id.uid_progress_indicator_button_layout),
                        withParent(withId(com.philips.platform.uid.test.R.id.progressButtonsNormalDeterminate))))));
    }

    private ViewInteraction getButton() {
        return onView(allOf(withId(com.philips.platform.uid.test.R.id.uid_progress_indicator_button_button),
                withParent(allOf(withId(com.philips.platform.uid.test.R.id.uid_progress_indicator_button_layout),
                        withParent(withId(com.philips.platform.uid.test.R.id.progressButtonsNormalDeterminate))))));
    }

    private ViewInteraction getProgressBar() {
        return onView(allOf(withId(com.philips.platform.uid.test.R.id.uid_progress_indicator_button_progress_bar),
                withParent(allOf(withId(com.philips.platform.uid.test.R.id.uid_progress_indicator_button_layout),
                        withParent(withId(com.philips.platform.uid.test.R.id.progressButtonsNormalDeterminate))))));
    }

    private ViewInteraction getDeterminateProgressIndicatorButton() {
        return onView(withId(com.philips.platform.uid.test.R.id.progressButtonsNormalDeterminate));
    }

    private ViewInteraction getIndeterminateProgressBar() {
        return onView(allOf(withId(com.philips.platform.uid.test.R.id.uid_progress_indicator_button_progress_bar),
                withParent(allOf(withId(com.philips.platform.uid.test.R.id.uid_progress_indicator_button_layout),
                        withParent(withId(com.philips.platform.uid.test.R.id.progressButtonsNormalIndeterminate))))));
    }

    private ViewInteraction getIndeterminateProgressText() {
        return onView(allOf(withId(com.philips.platform.uid.test.R.id.uid_progress_indicator_button_text),
                withParent(allOf(withId(com.philips.platform.uid.test.R.id.uid_progress_indicator_button_layout),
                        withParent(withId(com.philips.platform.uid.test.R.id.progressButtonsNormalIndeterminate))))));
    }

    private ViewInteraction getIndeterminateProgressIndicatorButton() {
        return onView(withId(com.philips.platform.uid.test.R.id.progressButtonsNormalIndeterminate));
    }

    private static class CustomViewAction implements ViewAction {
        private final String progressText;

        public CustomViewAction(final String progressText) {
            this.progressText = progressText;
        }

        @Override
        public Matcher<View> getConstraints() {
            return allOf();
        }

        @Override
        public String getDescription() {
            return "replace text";
        }

        @Override
        public void perform(final UiController uiController, final View view) {
            ((ProgressIndicatorButton) view).setProgressText(progressText);
        }
    }
}
