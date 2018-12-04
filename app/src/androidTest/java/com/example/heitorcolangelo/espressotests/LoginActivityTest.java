package com.example.heitorcolangelo.espressotests;

import android.provider.ContactsContract;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.heitorcolangelo.espressotests.ui.activity.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.example.heitorcolangelo.espressotests.ui.activity.LoginActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.content.Intent;
import android.support.test.espresso.intent.Intents;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    private final String BLANK = "";

    @Rule
    public ActivityTestRule<LoginActivity>

        mActivutyRule = new ActivityTestRule<>(LoginActivity.class,false, true);

    @Test
    public void whenActivityLoginIsLauched_shouldDisplayInitialState(){

        onView(withId(R.id.login_image)).check(matches(isDisplayed()));
        onView(withId(R.id.login_username)).check(matches(isDisplayed()));
        onView(withId(R.id.login_password)).check(matches(isDisplayed()));
        onView(withId(R.id.login_button)).check(matches(isDisplayed()));

    }

    @Test
    public void whenPasswordIsEmpty_andClickOnLoginButton_shouldDisplayDialog(){

        testEmptyFieldState(R.id.login_username);
    }

    @Test
    public void whenUserNameIsEmpty_andClickOnLoginButton_shouldDisplayDialog(){

        testEmptyFieldState(R.id.login_password);
    }

    @Test
    public void whenAllFieldsAreEmpty_andClickOnLoginButton_shouldDisplayDialog(){
        onView(withId(R.id.login_username)).perform(typeText(BLANK), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText(BLANK), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withText(R.string.validation_message)).check(matches(isDisplayed()));
        onView(withText(R.string.ok)).perform(click());
    }

    @Test
    public void whenAllFieldsAreCorrect__andClickOnLoginButton_shouldDisplayDialog(){

        onView(withId(R.id.login_username)).perform(typeText("login"), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("senha"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.)).check(matches(isDisplayed()));
    }

    private void testEmptyFieldState(int notEmptyField){
        onView(withId(notEmptyField)).perform(typeText("testeTeste"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        onView(withText(R.string.validation_message)).check(matches(isDisplayed()));
        onView(withText(R.string.ok)).perform(click());
    }

}
