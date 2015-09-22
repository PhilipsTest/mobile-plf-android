package com.philips.cdp.ui.catalog;

import android.app.Instrumentation;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;

import com.philips.cdp.ui.catalog.activity.ActionButtonsActivity;
import com.philips.cdp.ui.catalog.activity.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.philips.cdp.ui.catalog.IsBackgroundColorAsExpectedMatcher.isBackgroundColorSimilar;
import static com.philips.cdp.ui.catalog.IsPixelAsExpectedMatcher.isImageSimilar;
import static com.philips.cdp.ui.catalog.IsBackgroundBitmapColorAsExpectedMatcher.isBackgroundBitmapColorSimilar;
import static com.philips.cdp.ui.catalog.IsTextColorAsExpectedMatcher.isTextColorSimilar;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class ActionButtonTest extends ActivityInstrumentationTestCase2<MainActivity> {

/*

    int actual = 0;
    int expected = 1;

    int [][] MiscButton_ids = {
            {R.id.miscBtnSquareArrow,com.philips.cdp.ui.catalog.test.R.drawable.square_right},
            {R.id.miscBtnSquarePlus,com.philips.cdp.ui.catalog.test.R.drawable.square_plus},
            {R.id.miscBtnSquareMinus,com.philips.cdp.ui.catalog.test.R.drawable.square_minus},
            {R.id.miscBtnSquareCrossMark,com.philips.cdp.ui.catalog.test.R.drawable.square_cross},
            {R.id.miscBtnCircleArrow,com.philips.cdp.ui.catalog.test.R.drawable.circle_right},
            {R.id.miscBtnCirclePlus,com.philips.cdp.ui.catalog.test.R.drawable.circle_plus},
            {R.id.miscBtnCircleMinus,com.philips.cdp.ui.catalog.test.R.drawable.circle_minus},
            {R.id.miscBtnCircleQuestionMark,com.philips.cdp.ui.catalog.test.R.drawable.circle_question},
           };
*/

    private Resources testResources;

    public ActionButtonTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
        testResources = getInstrumentation().getContext().getResources();
    }

/*    public void testAllButtons() {
            for(int i = 0; i < MiscButton_ids.length; i++) {
            Bitmap expectedBitmap = BitmapFactory.decodeResource(testResources, MiscButton_ids[i][expected]);
            onView(withId(MiscButton_ids[i][actual]))
                    .check(matches(isImageSimilar(expectedBitmap)));
            expectedBitmap.recycle();

        }*/

    public void testAButtonSquareCrossIsPixelPerfect() {
        onView(withText("Miscellaneous Buttons")).perform(click());

        Bitmap expectedBitmap = BitmapFactory.decodeResource(testResources, com.philips.cdp.ui.catalog.test.R.drawable.sqaure_cross_mdpi) ;
        onView(withId(R.id.miscBtnSquareCrossMark))
                .check(matches(isImageSimilar(expectedBitmap)));
    }

    public void testAButtonSquareMinusIsPixelPerfect() {
        onView(withText("Miscellaneous Buttons")).perform(click());

        Bitmap expectedBitmap = BitmapFactory.decodeResource(testResources, com.philips.cdp.ui.catalog.test.R.drawable.sqaure_minus_mdpi);
        onView(withId(R.id.miscBtnSquareMinus))
                .check(matches(IsDimensionAsExpectedMatcher.isDimensionSimilar(expectedBitmap)));
    }

    public void testAButtonSquarePlusIsPixelPerfect() {

        onView(withText("Miscellaneous Buttons")).perform(click());

        Bitmap expectedBitmap = BitmapFactory.decodeResource(testResources, com.philips.cdp.ui.catalog.test.R.drawable.sqaure_plus_mdpi);
        onView(withId(R.id.miscBtnSquarePlus))
                .check(matches(isImageSimilar(expectedBitmap)));
    }

    public void testAButtonSquareRightIsPixelPerfect() {
        onView(withText("Miscellaneous Buttons")).perform(click());

        Bitmap expectedBitmap = BitmapFactory.decodeResource(testResources, com.philips.cdp.ui.catalog.test.R.drawable.sqaure_right_mdpi);
        onView(withId(R.id.miscBtnSquareArrow))
                .check(matches(isImageSimilar(expectedBitmap)));
    }

    public void testAButtonCirclePlusIsPixelPerfect() {
        onView(withText("Miscellaneous Buttons")).perform(click());

        Bitmap expectedBitmap = BitmapFactory.decodeResource(testResources, com.philips.cdp.ui.catalog.test.R.drawable.circle_plus_mdpi);
        onView(withId(R.id.miscBtnCirclePlus))
                .check(matches(IsDimensionAsExpectedMatcher.isDimensionSimilar(expectedBitmap)));
        onView(withId(R.id.miscBtnCirclePlus))
                .check(matches(IsPixelAsExpectedMatcher.isImageSimilar(expectedBitmap)));
    }

    public void testAButtonCircleMinusIsPixelPerfect() {
        onView(withText("Miscellaneous Buttons")).perform(click());

        Bitmap expectedBitmap = BitmapFactory.decodeResource(testResources, com.philips.cdp.ui.catalog.test.R.drawable.circle_minus_mdpi);
        onView(withId(R.id.miscBtnCircleMinus))
                .check(matches(isImageSimilar(expectedBitmap)));
    }

    public void testAButtonCircleRightIsPixelPerfect() {
        onView(withText("Miscellaneous Buttons")).perform(click());

        Bitmap expectedBitmap = BitmapFactory.decodeResource(testResources, com.philips.cdp.ui.catalog.test.R.drawable.circle_right_mdpi);
        onView(withId(R.id.miscBtnCircleArrow))
                .check(matches(isImageSimilar(expectedBitmap)));
    }

    public void testDBThemeActionButtonColourAsExpected() {
        onView(withText("Change Theme")).perform(click());
        onView(withText("Blue Theme")).perform(click());
        pressBack();
        onView(withText("Miscellaneous Buttons")).perform(click());
        onView(withId(R.id.miscBtnCircleArrow))
                .check(matches(isBackgroundColorSimilar("#03478")));
    }

    public void testBOThemeActionButtonColourAsExpected() {
        onView(withText("Change Theme")).perform(click());
        onView(withText("Orange Theme")).perform(click());
        pressBack();
        onView(withText("Miscellaneous Buttons")).perform(click());
        onView(withId(R.id.miscBtnCircleArrow))
                .check(matches(isBackgroundColorSimilar("#e98300")));
    }

    // Not executing testActionButtonCircleQuestionAsExpected as the design for question mark is a text instead of image
/*    public void testActionButtonCircleQuestionAsExpected() {
        onView(withText("Miscellaneous Buttons")).perform(click());

        Bitmap expectedBitmap = BitmapFactory.decodeResource(testResources, com.philips.cdp.ui.catalog.test.R.drawable.circle_question);
        onView(withId(R.id.miscBtnCircleQuestionMark))
                .check(matches(isImageSimilar(expectedBitmap)));
    }*/

/*    public void testActionButtonBGBitmapColorAsExpected() {
        onView(withText("Miscellaneous Buttons")).perform(click());

        Bitmap expectedBitmap = BitmapFactory.decodeResource(testResources, com.philips.cdp.ui.catalog.test.R.drawable.circle_question);
        onView(withId(R.id.miscBtnCircleQuestionMark))
                .check(matches(isBackgroundBitmapColorSimilar(expectedBitmap)));
    }*/
/*
    public void testActionButtonBGColorAsExpected() {
        onView(withText("Miscellaneous Buttons")).perform(click());
        onView(withId(R.id.miscBtnCircleQuestionMark))
                .check(matches(isBackgroundColorSimilar("#cae3e9")));
    }*/

    public void testActionSampleButtonTextColor() {
        onView(withText("Buttons")).perform(click());
        onView(withId(R.id.theme_button))
                .check(matches(isTextColorSimilar("#ffffff")));
    }
}
