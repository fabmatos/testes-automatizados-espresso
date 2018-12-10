package com.example.heitorcolangelo.espressotests;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.heitorcolangelo.espressotests.network.Api;
import com.example.heitorcolangelo.espressotests.network.UsersApi;
import com.example.heitorcolangelo.espressotests.ui.activity.UserDetailsActivity;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.test.InstrumentationRegistry.getContext;

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

    private String readJSONFromAsset() {

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
