/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.platform.uid.matcher;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import org.hamcrest.Matcher;

public class ViewPropertiesMatchers {
    public static Matcher<View> isSameLeftPadding(final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                setValues(view.getPaddingLeft(), expectedValue);
                return areEqual();
            }
        };
    }

    public static Matcher<View> isSameStartPadding(final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                setValues(view.getPaddingStart(), expectedValue);
                return areEqual();
            }
        };
    }

    public static Matcher<View> isSameEndPadding(final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                setValues(view.getPaddingEnd(), expectedValue);
                return areEqual();
            }
        };
    }

    public static Matcher<View> isSameRightPadding(final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                setValues(view.getPaddingRight(), expectedValue);
                return areEqual();
            }
        };
    }

    public static Matcher<View> isSameTopPadding(final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                setValues(view.getPaddingTop(), expectedValue);
                return areEqual();
            }
        };
    }

    public static Matcher<View> isSameBottomPadding(final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                setValues(view.getPaddingBottom(), expectedValue);
                return areEqual();
            }
        };
    }

    public static Matcher<View> isSameViewWidth(final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                setValues(view.getWidth(), expectedValue);
                return areEqual();
            }
        };
    }

    public static Matcher<View> isSameViewHeight(final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                setValues(view.getHeight(), expectedValue);
                return areEqual();
            }
        };
    }

    public static Matcher<View> isSameLeftMargin(final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                setValues(lp.leftMargin, expectedValue);
                return areEqual();
            }
        };
    }

    public static Matcher<View> isSameRightMargin(final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                setValues(lp.getMarginEnd(), expectedValue);
                return areEqual();
            }
        };
    }

    public static Matcher<View> isSameBottomMargin(final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                setValues(lp.bottomMargin, expectedValue);
                return areEqual();
            }
        };
    }

    public static Matcher<View> isSameTopMargin(final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                setValues(lp.topMargin, expectedValue);
                return areEqual();
            }
        };
    }

    public static Matcher<? super View> isSameViewMinHeight(final int expectedIconHeight) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(final View view) {
                setValues(view.getMinimumHeight(), expectedIconHeight);
                return areEqual();
            }
        };
    }

    public static Matcher<? super View> isSameViewMinWidth(final int expectedIconWidth) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(final View view) {
                setValues(view.getMinimumWidth(), expectedIconWidth);
                return areEqual();
            }
        };
    }

    public static Matcher<? super View> isVisible(final int gone) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(final View view) {
                setValues(view.getVisibility(), gone);
                return areEqual();
            }
        };
    }

    public static Matcher<? super View> isSameTitleRightMargin(final int rightMargin) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(final View view) {
                if (view instanceof Toolbar) {
                    Toolbar toolbar = (Toolbar) view;
                    setValues(toolbar.getTitleMarginEnd(), rightMargin);
                    return areEqual();
                }
                return false;
            }
        };
    }

    public static Matcher<? super View> isSameTittleLeftMargin(final int leftMargin) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(final View view) {
                if (view instanceof Toolbar) {
                    Toolbar toolbar = (Toolbar) view;
                    setValues(toolbar.getTitleMarginStart(), leftMargin);
                    return areEqual();
                }
                return false;
            }
        };
    }
}