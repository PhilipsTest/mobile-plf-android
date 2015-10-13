package com.philips.cdp.ui.catalog.Matchers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class IsBackgroundColorAsExpectedMatcher extends BoundedMatcher<View, View> {

    public static final String TAG = "IsBackgroundColorAsExpectedMatcher";
    private String expectedColor;

    public IsBackgroundColorAsExpectedMatcher(final Class<? extends View> expectedType, String expectedColor) {
        super(expectedType);
        this.expectedColor = expectedColor;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Actual  color  differs  when compared with expected color");
    }

    @Override
    public boolean matchesSafely(View view) {

        Bitmap actualBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas actualCanvas = new Canvas(actualBitmap);
        view.draw(actualCanvas);
        int actualRGB = actualBitmap.getPixel(30,30);

        String actualcolor = "#"  +
                Integer.toString(Color.red(actualRGB), 16) +
                Integer.toString(Color.green(actualRGB), 16) +
                Integer.toString(Color.blue(actualRGB), 16);

        if (actualcolor.equalsIgnoreCase(expectedColor)) {
            return true;
        } else {
            return false;
        }
    }

    public static Matcher<View> isBackgroundColorSimilar(final String expectedColor){
        return new IsBackgroundColorAsExpectedMatcher(View.class, expectedColor);
    }
}








