package com.project.service;

import com.project.request.ChatSaveRequest;
import com.project.request.DeepSeekRequest;
import com.project.request.RegisterRequest;
import com.project.request.LoginRequest;
import com.project.request.UpdateProfileRequest;


import com.project.response.ArticleDetailResponse;
import com.project.response.ArticlePopulerResponse;
import com.project.response.ChatHistory;
import com.project.response.DeepSeekResponse;
import com.project.response.DefaultResponse;
import com.project.response.DiaryDetailResponse;
import com.project.response.DiaryRespone;
import com.project.response.ExpertsDetailResponse;
import com.project.response.ExpertsRensponse;
import com.project.response.LoginResponse;
import com.project.response.MoodResponse;
import com.project.response.NotificationResponse;
import com.project.response.PostResponse;
import com.project.response.ProfileResponse;
import com.project.response.UserResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    //===================
    //     USER-API
    //===================
    @GET("api/users/username/{id}")
    Call<UserResponse> getUserById(@Path("id") int userId);

    @POST("api/users/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/users/register")
    Call<LoginResponse> register(@Body RegisterRequest registerRequest);
    @POST("api/users/update") // Ganti dengan path file PHP Anda
    Call<ResponseBody> updateProfile(@Body UpdateProfileRequest request);
    @GET("api/users/experts")
    Call<ExpertsRensponse> getExpert();
    @GET("api/users/expertsDetail/{id}") // Endpoint untuk detail diary (dari server)
    Call<ExpertsDetailResponse> getExpertsDetail(@Path("id") int diaryId);

    //===================
    //     ARTICLES-API
    //===================
    @GET("api/articles/populer")
    Call<ArticlePopulerResponse> getArticles();
    @GET("api/articles/Detail/{id}")
    Call<ArticleDetailResponse> getArticleDetail(@Path("id") int articleId);
    @PATCH("api/articles/view/{id}")
    Call<Void> incrementArticleView(@Path("id") int articleId);

    //===================
    //     DIARY-API
    //===================
    @GET("api/Diary/all")
    Call<DiaryRespone> getDiary();
    @GET("api/Diary/Detail/{id}") // Endpoint untuk detail diary (dari server)
    Call<DiaryDetailResponse> getDiaryDetail(@Path("id") int diaryId);
    @GET("api/Diary/me")
    Call<DiaryRespone> getMyDiary(
            @Header("Authorization") String authToken);
    @GET("api/Diary/Mood")
    Call<MoodResponse> getDiaryMood(
            @Header("Authorization") String authToken);

    //===================
    //     POSTING-API
    //===================
    @GET("api/posts")
    Call<PostResponse> getPosts();

    @GET("api/notifications")
    Call<NotificationResponse> getNotifications(
            @Header("Authorization") String authToken // Memerlukan "Bearer <token>"
    );
    //===================
    //     CHATBOT-API
    //===================
    @POST("api/chatbot/chat") // 👈 GANTI ke endpoint proxy Anda
    Call<DeepSeekResponse> sendChatToProxy(@Body DeepSeekRequest request);

    /**
     * 2. Mengambil history chat dari database Anda
     */
    @GET("api/chatbot/history/{userId}") // 👈 Endpoint untuk history
    Call<List<ChatHistory>> getChatHistory(@Path("userId") int userId);

    /**
     * 3. Menyimpan chat baru ke database Anda
     */
    @POST("api/chatbot/save") // 👈 Endpoint untuk menyimpan chat
    Call<DefaultResponse> saveChat(@Body ChatSaveRequest request);

    //===================
    //     CHATBOT-API
    //===================
    @GET("api/users/profile")
    Call<ProfileResponse> getProfile(
        @Header("Authorization") String authToken);

}
