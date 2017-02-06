package com.philips.platform.uid.components.alert;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.philips.platform.uid.DialogTestFragment;
import com.philips.platform.uid.activity.BaseTestActivity;
import com.philips.platform.uid.matcher.ViewPropertiesMatchers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class AlertNoTitleTest extends BastTest {
    @Rule
    public ActivityTestRule<BaseTestActivity> mActivityTestRule = new ActivityTestRule<>(BaseTestActivity.class, false, false);
    BaseTestActivity activity;

    @Before
    public void setUp() {
        activity = mActivityTestRule.launchActivity(getLaunchIntent(0));
        activity.switchTo(com.philips.platform.uid.test.R.layout.main_layout);
        activity.switchFragment(DialogTestFragment.create());
        testResources = getInstrumentation().getContext().getResources();
    }

    @Test
    public void verifyAlertIsDisplayedWithNoTitle() {
        getAlertTitle().check(matches(ViewPropertiesMatchers.isVisible(View.GONE)));
    }

    @Test
    public void verifyContentTopMarginWithNoTitle() {
        getAlertTitle().check(matches(ViewPropertiesMatchers.isVisible(View.GONE)));

        int expectedTopMargin = testResources.getDimensionPixelSize(com.philips.platform.uid.test.R.dimen.alert_content_top_margin_when_no_title);
        getAlertContainer().check(matches(ViewPropertiesMatchers.isSameTopMargin(expectedTopMargin)));
    }

    private ViewInteraction getAlertTitle() {
        return onView(withId(com.philips.platform.uid.test.R.id.uid_alert_title));
    }

    private ViewInteraction getAlertContainer() {
        return onView(withId(com.philips.platform.uid.test.R.id.uid_alert_message_scroll_container));
    }
}
