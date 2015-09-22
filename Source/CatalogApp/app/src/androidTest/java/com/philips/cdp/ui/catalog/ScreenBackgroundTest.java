package com.philips.cdp.ui.catalog;

import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;

import com.philips.cdp.ui.catalog.activity.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.philips.cdp.ui.catalog.IsBackgroundColorAsExpectedMatcher.isBackgroundColorSimilar;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class ScreenBackgroundTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private Resources testResources;

    public ScreenBackgroundTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
        testResources = getInstrumentation().getContext().getResources();
    }

    public void testDBThemeScreenBackground() {
        onView(withText("Change Theme")).perform(click());
        onView(withText("Blue Theme")).perform(click());
        onView(withId(R.id.colorSwitch)).perform(click());
        onView(withText("solid")).perform(click());
        onView(withId(android.R.id.content))
                .check(matches(isBackgroundColorSimilar("#b3c8e6")));

    }
}
