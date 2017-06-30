package com.philips.cdp.di.iapdemo;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.philips.cdp.di.iapdemo.idlingResources.BtnSignIdlingResource;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LunchUserRegisterationToReachCarrierApp {

    @Rule
    public ActivityTestRule<LuncherActivity> mActivityTestRule = new ActivityTestRule<>(LuncherActivity.class);

    @Test
    public void lunchUserRegisterationToReachCarrierApp() {
        ViewInteraction button = onView(
                allOf(withId(R.id.btn_launch), withText("Launch Activity"), isDisplayed()));
        button.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_register), withText("Register/Login"), isDisplayed()));
        appCompatButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction xProviderButton = onView(
                allOf(withId(R.id.btn_reg_my_philips),
                        withParent(allOf(withId(R.id.rl_reg_singin_options),
                                withParent(withId(R.id.ll_reg_root_container))))));
        xProviderButton.perform(scrollTo(), click());

        ViewInteraction xEditText = onView(
                allOf(withId(R.id.et_reg_email),
                        withParent(withId(R.id.rl_reg_parent_verified_field)),
                        isDisplayed()));
        xEditText.perform(click());

        ViewInteraction xEditText2 = onView(
                allOf(withId(R.id.et_reg_email),
                        withParent(withId(R.id.rl_reg_parent_verified_field)),
                        isDisplayed()));
        xEditText2.perform(replaceText("pabitra@grr.la"), closeSoftKeyboard());

        ViewInteraction xEditText3 = onView(
                allOf(withId(R.id.et_reg_password),
                        withParent(withId(R.id.rl_reg_parent_verified_field)),
                        isDisplayed()));
        xEditText3.perform(replaceText("pabitra@grr.la"), closeSoftKeyboard());

        IdlingPolicies.setMasterPolicyTimeout(
                1000 * 30, TimeUnit.MILLISECONDS);
        IdlingPolicies.setIdlingResourceTimeout(
                1000 * 30, TimeUnit.MILLISECONDS);

        BtnSignIdlingResource btnSignIdlingResource = new BtnSignIdlingResource();
        Espresso.registerIdlingResources(btnSignIdlingResource);

        ViewInteraction xButton = onView(
                allOf(withId(R.id.btn_reg_sign_in), withText("Log In"),
                        withParent(allOf(withId(R.id.fl_reg_sign_in),
                                withParent(withId(R.id.rl_reg_welcome_container))))));
        xButton.perform(scrollTo(), click());


        //Check Terms & Cindition and Press Continue
        onView(withId(R.id.cb_reg_accept_terms)).perform(click());


        Espresso.unregisterIdlingResources(btnSignIdlingResource);

        ViewInteraction xButton2 = onView(
                allOf(withId(R.id.reg_btn_continue), withText("Continue"),
                        withParent(allOf(withId(R.id.rl_reg_btn_continue_container),
                                withParent(withId(R.id.ll_reg_root_container))))));
        xButton2.perform(scrollTo(), click());

        ViewInteraction xButton3 = onView(
                allOf(withId(R.id.btn_reg_continue), withText("Continue"),
                        withParent(allOf(withId(R.id.fl_reg_sign_in),
                                withParent(withId(R.id.rl_reg_continue_id))))));
        xButton3.perform(scrollTo(), click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withId(R.id.iap_header_title), withText("Carrier App"), isDisplayed()));
        textView.check(matches(withText("Carrier App")));

//        ViewInteraction textView2 = onView(
//                allOf(withId(R.id.iap_header_title), withText("Carrier App"),
//                        childAtPosition(
//                                allOf(withId(R.id.ratingthememain),
//                                        childAtPosition(
//                                                withId(R.id.action_bar),
//                                                0)),
//                                0),
//                        isDisplayed()));
//        textView2.check(matches(withText("Carrier App")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
