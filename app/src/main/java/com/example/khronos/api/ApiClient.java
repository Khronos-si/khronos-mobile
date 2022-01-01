package com.example.khronos.api;

import static android.content.Context.MODE_PRIVATE;
import static com.example.khronos.MainActivity.apiInterface;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.khronos.MainActivity;
import com.example.khronos.structures.TodoGroup;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String TAG = "API Client";

    private static final String BASE_URL = "https://khronos.si/api/";
    private static Retrofit retrofit = null;
    public static String token = "";

    // editor for token update
    private static SharedPreferences.Editor editor;

    // invalid token
    private static boolean invalidToken = false;

    static OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request  = chain.request().newBuilder()
                    .addHeader("auth-token", token)
                    .build();

            Response response = chain.proceed(request);

            // if token is invalid
            if (response.code() == 400 && !request.url().toString().equals(BASE_URL + "user/logout")) {

                Log.d(TAG, "intercept: FIRST INVALID: CODE: " + response.message());
                Log.d(TAG, "intercept: USL: " + request.url());

                // if token is again invalid (can't renew) -> re-login
                if(invalidToken) {
                    invalidToken = false;
                    // return response
                    return response;
                }

                response.close();
                Log.d(TAG, "intercept: invalid token");

                // request new token and save it
                invalidToken = true;
                Call<Void> call = apiInterface.refreshToken();
                setNewToken(call.execute().headers().get("auth-token"));
                

                // if request for new token was succesfull
                if (response.code() == 200) {
                    response = chain.proceed(request);
                } else {
                    // if request for new token was not succesfull -> re-login
                    Log.d(TAG, "intercept: need to relogin");

                }
            }

            // return response
            return response;
        }
    }).build();

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static void setSharedPreferences(SharedPreferences.Editor mainEditor) {
        editor = mainEditor;
    }

    private static void setNewToken(String token) {
        editor.putString("token", token);
        editor.commit();
    }
}
