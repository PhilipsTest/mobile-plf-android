/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.platform.uid.matcher;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.internal.util.Checks;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.philips.platform.uid.utils.UIDTestUtils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class TextViewPropertiesMatchers {

    public static Matcher<View> isSameTextColor(final int stateAttr, final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (view instanceof TextView) {
                    int actual = ((TextView) view).getTextColors().getColorForState(new int[]{stateAttr}, Color.MAGENTA);
                    setValues(actual, expectedValue);
                    return areEqual();
                }
                throw new RuntimeException("expected TextView got " + view.getClass().getName());
            }
        };
    }

    public static Matcher<View> isSameTextColor(final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (view instanceof TextView) {
                    int actual = ((TextView) view).getCurrentTextColor();
                    setValues(actual, expectedValue);
                    return areEqual();
                }
                throw new RuntimeException("expected TextView got " + view.getClass().getName());
            }
        };
    }

    public static Matcher<View> isSameHintTextColor(final int stateAttr, final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (view instanceof TextView) {
                    int actual = ((TextView) view).getHintTextColors().getColorForState(new int[]{stateAttr}, Color.MAGENTA);
                    setValues(actual, expectedValue);
                    return areEqual();
                }
                throw new RuntimeException("expected TextView got " + view.getClass().getName());
            }
        };
    }

    public static Matcher<View> isSameFontSize(final float expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (view instanceof TextView) {
                    setValues(((TextView) view).getTextSize(), expectedValue);
                    return areEqual();
                }
                throw new RuntimeException("expected TextView got " + view.getClass().getName());
            }
        };
    }

    public static Matcher<View> isSameLineSpacing(final float expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (view instanceof TextView) {
                    setValues(((TextView) view).getLineSpacingExtra(), expectedValue);
                    return areEqual();
                }
                throw new RuntimeException("expected TextView got " + view.getClass().getName());
            }
        };
    }

    public static Matcher<View> isSameLineSpacingMultiplier(final float expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (view instanceof TextView) {
                    setValues(((TextView) view).getLineSpacingMultiplier(), expectedValue);
                    return areEqual();
                }
                throw new RuntimeException("expected TextView got " + view.getClass().getName());
            }
        };
    }

    public static Matcher<View> isSameCompoundDrawablePadding(final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (view instanceof TextView) {
                    setValues(((TextView) view).getCompoundDrawablePadding(), expectedValue);
                    return areEqual();
                }
                throw new RuntimeException("expected TextView got " + view.getClass().getName());
            }
        };
    }

    public static Matcher<View> isSameCompoundDrawableHeight(final int index, final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {

                if (view instanceof TextView) {
                    Drawable[] drawables = ((TextView) view).getCompoundDrawables();
                    if (drawables != null && drawables[index] != null) {
                        final BaseTypeSafteyMatcher<Drawable> heightMatcher = (BaseTypeSafteyMatcher<Drawable>) DrawableMatcher.isSameHeight(expectedValue);
                        boolean result = heightMatcher.matches(drawables[index]);
                        setValues(heightMatcher.actual, heightMatcher.expected);
                        return result;
                    }
                    return false;
                }
                throw new RuntimeException("expected TextView got " + view.getClass().getName());
            }
        };
    }

    public static Matcher<View> isSameCompoundDrawableWidth(final int index, final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {

                if (view instanceof TextView) {
                    Drawable[] drawables = ((TextView) view).getCompoundDrawables();
                    if (drawables != null && drawables[index] != null) {
                        final BaseTypeSafteyMatcher<Drawable> widthMatcher = (BaseTypeSafteyMatcher<Drawable>) DrawableMatcher.isSameWidth(expectedValue);
                        boolean result = widthMatcher.matches(drawables[index]);
                        setValues(widthMatcher.actual, widthMatcher.expected);
                        return result;
                    }
                    return false;
                }
                throw new RuntimeException("expected TextView got " + view.getClass().getName());
            }
        };
    }

    public static Matcher<View> isSameCompoundDrawableColor(final int index, final int state, final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (view instanceof TextView) {
                    Drawable[] drawables = ((TextView) view).getCompoundDrawables();
                    if (drawables != null && drawables[index] != null) {
                        final BaseTypeSafteyMatcher<Drawable> colorMatcher = (BaseTypeSafteyMatcher<Drawable>) DrawableMatcher.isSameColor(new int[]{state}, expectedValue, false);
                        boolean result = colorMatcher.matches(drawables[index]);

                        setValues(colorMatcher.actual, colorMatcher.expected);
                        return result;
                    }
                    return false;
                }
                throw new RuntimeException("expected TextView got " + view.getClass().getName());
            }
        };
    }

    public static Matcher<? super View> isSameLineHeight(final float expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(final View view) {
                if (view instanceof TextView) {
                    setValues(((TextView) view).getLineHeight(), (int) expectedValue);
                    return areEqual();
                }
                throw new RuntimeException("expected TextView got " + view.getClass().getName());
            }
        };
    }

    public static Matcher<? super View> hasMasking() {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(final View view) {
                if (view instanceof TextView) {
                    setValues(((TextView) view).getTransformationMethod(), null);
                    return actual != null;
                }
                throw new RuntimeException("expected TextView got " + view.getClass().getName());
            }
        };
    }

    public static Matcher<? super View> hasNoMasking() {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(final View view) {
                if (view instanceof TextView) {
                    setValues(((TextView) view).getTransformationMethod(), null);
                    return actual == null;
                }
                throw new RuntimeException("expected TextView got " + view.getClass().getName());
            }
        };
    }

    public static Matcher<? super View> hasCompoundDrawable(final int index) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(final View view) {
                if (view instanceof TextView) {
                    setValues(((TextView) view).getCompoundDrawables()[index] != null, true);
                    return areEqual();
                }
                return false;
            }
        };
    }

    public static Matcher<? super View> hasNoText() {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(final View view) {
                if (view instanceof TextView) {
                    setValues(TextUtils.isEmpty(((TextView) view).getText()), true);
                    return areEqual();
                }
                return false;
            }
        };
    }

    public static Matcher<? super View> noCompoundDrawable(final int index) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(final View view) {
                if (view instanceof TextView) {
                    setValues(((TextView) view).getCompoundDrawables()[index] == null, true);

                    return areEqual();
                }
                return false;
            }
        };
    }

    public static Matcher<? super View> sameBackgroundColorTintList(final int attributeColor) {
        return new BaseTypeSafteyMatcher<View>() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            protected boolean matchesSafely(final View view) {
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    setValues(textView.getBackgroundTintList(), attributeColor);
                    return areEqual();
                }
                return false;
            }
        };
    }

    public static Matcher<View> sameBackgroundColor(final int color) {
        Checks.checkNotNull(color);
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public boolean matchesSafely(TextView view) {
                return color == ((ColorDrawable) view.getBackground()).getColor();
            }
            @Override
            public void describeTo(Description description) {
            }
        };
    }

    public static Matcher<View> isSameTextColorWithReflection(final int index, final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (view instanceof TextView) {
                    final ColorStateList textColors = ((TextView) view).getTextColors();
                    final int[] colors = UIDTestUtils.getColorsWithReflection(textColors);

                    setValues(colors[index], expectedValue);
                    return areEqual();
                }
                throw new RuntimeException("expected TextView got " + view.getClass().getName());
            }
        };
    }
}