package com.example.heitorcolangelo.espressotests;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.JsonReader;

import com.example.heitorcolangelo.espressotests.mocks.Mocks;
import com.example.heitorcolangelo.espressotests.network.Api;
import com.example.heitorcolangelo.espressotests.network.UsersApi;
import com.example.heitorcolangelo.espressotests.ui.activity.LoginActivity;
import com.example.heitorcolangelo.espressotests.ui.activity.MainActivity;
import com.example.heitorcolangelo.espressotests.ui.activity.UserDetailsActivity;

import net.vidageek.mirror.dsl.Mirror;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private MockWebServer server;

    @Rule
    public ActivityTestRule<MainActivity> mActivutyRule = new ActivityTestRule<>(MainActivity.class,false, false);

    @Before
    public void setUp() throws Exception{
        server = new MockWebServer();
        server.start();
        setupServerUrl();
    }

    @Test
    public void whenResultIsOK_shouldDisplayListWithUsers() {

        String users=readJSONFromAsset();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(users));
        mActivutyRule.launchActivity(new Intent());
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void whenResultIsNotOK_shouldDisplayError() {

        server.enqueue(new MockResponse().setResponseCode(500).setBody(Mocks.ERROR));
        mActivutyRule.launchActivity(new Intent());
        onView(withId(R.id.error_view)).check(matches(isDisplayed()));
    }

    @Test
    public void checkUserItemView_isDisplayed(){

        String users=readJSONFromAsset();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(users));
        mActivutyRule.launchActivity(new Intent());
        onView(allOf(withId(R.id.user_view_image),hasSibling(withText("Rasha Bosse")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.user_view_name), withText("Rasha Bosse"))).check(matches(isDisplayed()));
    }

    @Test
    public void whenClickOnItemList_shouldStartUserDetailsActivity_withExtra(){

        String users=readJSONFromAsset();
        server.enqueue(new MockResponse().setResponseCode(200).setBody(users));
        mActivutyRule.launchActivity(new Intent());
        Intents.init();
        Matcher<Intent> matcher = allOf(
                hasComponent(UserDetailsActivity.class.getName()),
                hasExtraWithKey(UserDetailsActivity.CLICKED_USER)
        );

        Instrumentation.ActivityResult
                result = new Instrumentation.ActivityResult(Activity.RESULT_OK, null);

        intending(matcher).respondWith(result);

        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(0, click()));

        intended(matcher);
        Intents.release();
    }

    @After
    public void tearDown() throws IOException{
        server.shutdown();
    }

    private void setupServerUrl() {
        String url = server.url("/").toString();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        final UsersApi usersApi = UsersApi.getInstance();

        final  Api api = new Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(UsersApi.GSON))
                .client(client)
                .build()
                .create(Api.class);
        setField(usersApi, "api", api);
    }

    private void setField(Object target, String fieldName, Object value) {
        new Mirror()
                .on(target)
                .set()
                .field(fieldName)
                .withValue(value);
    }

    public String readJSONFromAsset() {

        String json = null;
        try {
            InputStream is = getContext().getAssets().open("users.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
