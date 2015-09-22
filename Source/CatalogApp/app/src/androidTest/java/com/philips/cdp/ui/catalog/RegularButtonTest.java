package com.philips.cdp.ui.catalog;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;

import com.philips.cdp.ui.catalog.activity.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.philips.cdp.ui.catalog.IsBackgroundColorAsExpectedMatcher.isBackgroundColorSimilar;
import static com.philips.cdp.ui.catalog.IsPixelAsExpectedMatcher.isImageSimilar;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class RegularButtonTest extends ActivityInstrumentationTestCase2<MainActivity>{

    private Resources testResources;

    public RegularButtonTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
        testResources = getInstrumentation().getContext().getResources();
    }

    public void testRButtonIsPixelPerfect() {
        onView(withText("Buttons")).perform(click());
        Bitmap expectedBitmap = BitmapFactory.decodeResource(testResources, com.philips.cdp.ui.catalog.test.R.drawable.regularbtn_darkblue_mdpi);
        onView(withId(R.id.theme_button))
                .check(matches(isImageSimilar(expectedBitmap)));
    }

    public void testDBThemeActionButtonColourAsExpected() {
        onView(withText("Change Theme")).perform(click());
        onView(withText("Blue Theme")).perform(click());
        pressBack();
        onView(withText("Buttons")).perform(click());
        onView(withId(R.id.theme_button))
                .check(matches(isBackgroundColorSimilar("#03478")));
    }

    public void testBOThemeActionButtonColourAsExpected() {
        onView(withText("Change Theme")).perform(click());
        onView(withText("Orange Theme")).perform(click());
        pressBack();
        onView(withText("Buttons")).perform(click());
        onView(withId(R.id.theme_button))
                .check(matches(isBackgroundColorSimilar("#e98300")));
    }


}
