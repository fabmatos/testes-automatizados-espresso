package com.example.heitorcolangelo.espressotests;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;

import com.example.heitorcolangelo.espressotests.entities.User;
import com.example.heitorcolangelo.espressotests.entities.UserResults;
import com.example.heitorcolangelo.espressotests.mocks.Mocks;
import com.example.heitorcolangelo.espressotests.network.Api;
import com.example.heitorcolangelo.espressotests.network.UsersApi;
import com.example.heitorcolangelo.espressotests.network.model.UserVO;
import com.example.heitorcolangelo.espressotests.ui.activity.UserDetailsActivity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.vidageek.mirror.dsl.Mirror;

import org.hamcrest.Matcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static matcher.TextColorMatcher.withTextColor;
import static org.hamcrest.Matchers.allOf;

import static android.support.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
public class UserDetailsActivityTest {

    private MockWebServer server;

    @Rule
    public ActivityTestRule<UserDetailsActivity> mActivutyRule = new ActivityTestRule<>(UserDetailsActivity.class,false, false);

    @Before
    public void setUp() throws Exception{
        server = new MockWebServer();
        server.start();
        setupServerUrl();
    }

    @Test
    public void displayUserDetailsActivity(){

        String users=readJsonToStringFromAsset("userFull.json");

        server.enqueue(new MockResponse().setResponseCode(200).setBody(users));
        mActivutyRule.launchActivity(createIntent(users));
        onView(withId(R.id.user_details_image)).check(matches(isDisplayed()));
        onView(withId(R.id.user_details_name)).check(matches(isDisplayed()));
        onView(withId(R.id.user_details_address)).check(matches(isDisplayed()));
        onView(withId(R.id.user_details_email)).check(matches(isDisplayed()));
        onView(withId(R.id.user_details_phone)).check(matches(isDisplayed()));
//        onView(allOf(
//                withId(R.id.image_and_text_image),
//                hasSibling(withText("6200 lucasbolwerk"))))
//                .check(matches(isDisplayed()));
//        onView(allOf(
//                withId(R.id.image_and_text_image),
//                hasSibling(withText("woohooo@gmail.com"))))
//                .check(matches(isDisplayed()));
//        onView(allOf(
//                withId(R.id.image_and_text_image),
//                hasSibling(withText("(602)-140-1188"))))
//                .check(matches(isDisplayed()));
    }

    @Test
    public void whenEmailIsMissing_shouldDisplay_NoInfoMessage(){
        String users=readJsonToStringFromAsset("userWithoutEmail.json");

        server.enqueue(new MockResponse().setResponseCode(200).setBody(users));
        mActivutyRule.launchActivity(createIntent(users));
        onView(withId(R.id.user_details_image)).check(matches(isDisplayed()));
        onView(withId(R.id.user_details_name)).check(matches(isDisplayed()));
        onView(allOf(
                withId(R.id.image_and_text_image),
                hasSibling(withText("No info available"))))
                .check(matches(isDisplayed()));
        onView(allOf(
                withText("No info available"),
                withTextColor(ContextCompat.getColor(mActivutyRule.getActivity(), R.color.red)))
        ).check(matches(isDisplayed()));
    }

    private Intent createIntent(String users) {
        return new Intent().putExtra(UserDetailsActivity.CLICKED_USER, getMockedUser(users));
    }

    private UserVO getMockedUser(String users) {
        final String mock = users;
        return UsersApi.GSON.fromJson(mock, UserVO.class);

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

        final Api api = new Retrofit.Builder()
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

    private String readJsonToStringFromAsset(String fileName) {

        String json = null;
        try {
            InputStream is = getContext().getAssets().open(fileName);
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

    private JSONObject readJsonFromAsset() {

        String jsonString = readJsonToStringFromAsset("users.json");
        JSONObject json = null;
        try {
            json = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    private JSONArray readJsonArrayFromAsset() {

        JSONObject jsonObject = readJsonFromAsset();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);

        return jsonArray;
    }

    private List<UserVO> getUsersFromGson(){
        Gson gson = new Gson();
        return gson.fromJson(readJsonToStringFromAsset("users.json"),UserResults.class).results;
    }


}
