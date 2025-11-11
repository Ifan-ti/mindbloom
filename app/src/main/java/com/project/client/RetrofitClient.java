// RetrofitClient.java
package com.project.client;

import android.content.Context;
import android.util.Log;

import com.project.service.ApiService;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://brenna-nonimbricate-randall.ngrok-free.dev/";
    private static Retrofit retrofit;

    public static ApiService getApiService(Context context) {
        if (retrofit == null) {
            Context appCtx = context.getApplicationContext();

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();

                            SessionManager sm = new SessionManager(appCtx);
                            String token = sm.getAuthToken();

                            Request.Builder builder = original.newBuilder()
                                    .header("Content-Type", "application/json");

                            if (token != null && !token.isEmpty()) {
                                builder.header("Authorization", "Bearer " + token);
                                Log.d("Retrofit", "Token dikirim: " + token);
                            }

                            return chain.proceed(builder.build());
                        }
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
