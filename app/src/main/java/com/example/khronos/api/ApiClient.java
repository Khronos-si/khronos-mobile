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
            if (response.code() == 400 && response.body() != null && response.body().string().contains("Invalid Token")
                    && !request.url().toString().equals(BASE_URL + "user/refresh-token")
                    && !request.url().toString().equals(BASE_URL + "user/logout")) {

                Log.d(TAG, "intercept: URL: " + request.url().toString());

                response.close();

                // request new token and save it
                Call<Void> call = apiInterface.refreshToken();

                Request newRequest  = call.request().newBuilder()
                        .addHeader("auth-token", token)
                        .build();

                response = chain.proceed(newRequest);

                // if request for new token was succesfull
                if (response.code() == 200) {
                    setNewToken(response.headers().get("auth-token"));
                } else {
                    editor.remove("token");
                    editor.commit();
                    MainActivity.returnToLogin.postValue(true);
                    Log.d(TAG, "intercept: RETURN TO LOGIN!");
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
