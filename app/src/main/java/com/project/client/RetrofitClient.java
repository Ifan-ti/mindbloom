package com.project.client;

import android.content.Context;
import android.content.SharedPreferences;

import com.project.service.ApiService;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://unlacquered-unbankable-alia.ngrok-free.dev/mindbloom-api/api/";
    private static Retrofit retrofit = null;

    // ===============================
    //  Retrofit tanpa token (default)
    // ===============================
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // ===============================
    //  ApiService default
    // ===============================
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }

    // ===============================
    //  Retrofit dengan Token (opsional)
    // ===============================
    public static ApiService getApiService(Context context) {

        SharedPreferences prefs = context.getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {

                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json");

                    // tambahkan header token hanya jika ada
                    if (token != null) {
                        requestBuilder.header("Authorization", "Bearer " + token);
                    }

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                })
                .build();

        Retrofit retrofitAuth = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofitAuth.create(ApiService.class);
    }
}
