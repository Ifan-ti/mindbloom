package com.project.client;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull; // Import untuk anotasi

import com.project.service.ApiService;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Pastikan URL NGROK ini masih aktif dan benar.
    private static final String BASE_URL = "https://brenna-nonimbricate-randall.ngrok-free.dev/";

    // Kita buat 'retrofit' sebagai singleton
    private static Retrofit retrofit;

    public static ApiService getApiService(Context context) {
        // Gunakan pola singleton check
        if (retrofit == null) {
            // Gunakan ApplicationContext untuk menghindari memory leak
            Context appCtx = context.getApplicationContext();

            // Interceptor untuk logging (sangat berguna untuk debugging)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Buat OkHttpClient kustom
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging) // Tambahkan logging interceptor
                    .addInterceptor(new Interceptor() {
                        @NonNull // Tambahkan anotasi
                        @Override
                        public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                            Request original = chain.request();

                            // Dapatkan token dari SessionManager
                            SessionManager sm = new SessionManager(appCtx);
                            String token = sm.getAuthToken();

                            Request.Builder builder = original.newBuilder()
                                    // Header Content-Type biasanya ditangani oleh GSON,
                                    // tapi tidak masalah jika ada di sini.
                                    .header("Content-Type", "application/json");

                            // Tambahkan header Authorization jika token ada
                            if (token != null && !token.isEmpty()) {
                                builder.header("Authorization", "Bearer " + token);
                                Log.d("Retrofit", "Token dikirim: " + token);
                            } else {
                                Log.w("Retrofit", "Token tidak ditemukan, request tanpa token.");
                            }

                            return chain.proceed(builder.build());
                        }
                    })
                    .build();

            // Buat instance Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        // Kembalikan service API
        return retrofit.create(ApiService.class);
    }
}