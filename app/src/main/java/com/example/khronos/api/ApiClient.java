package com.example.khronos.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "https://khronos.si/api/";
    private static Retrofit retrofit = null;
    public static String token = "";

    static OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request newRequest  = chain.request().newBuilder()
                    .addHeader("auth-token", token)
                    .build();
            return chain.proceed(newRequest);
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
}
