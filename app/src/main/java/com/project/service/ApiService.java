package com.project.service;

import com.project.request.RegisterRequest;
import com.project.request.LoginRequest;
import com.project.response.ArticleDetailResponse;
import com.project.response.ArticlePopulerResponse;
import com.project.response.DiaryDetailResponse;
import com.project.response.DiaryRespone;
import com.project.response.LoginResponse;
import com.project.response.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/articles_populer")
    Call<ArticlePopulerResponse> getArticles();

    @GET("api/Diary")
    Call<DiaryRespone> getDiary();

    // 🔥 TAMBAHAN: Endpoint baru yang efisien
    @GET("api/articles/{id}")
    Call<ArticleDetailResponse> getArticleDetail(@Path("id") int articleId);

    @GET("api/Diary/{id}") // Endpoint untuk detail diary (dari server)
    Call<DiaryDetailResponse> getDiaryDetail(@Path("id") int diaryId);

    @PATCH("api/articles/view/{id}")
    Call<Void> incrementArticleView(@Path("id") int articleId);

    @GET("api/users/{id}")
    Call<UserResponse> getUserById(@Path("id") int userId);

    @POST("api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/register")
    Call<LoginResponse> register(@Body RegisterRequest registerRequest);

    @GET("api/Diary/me")
    Call<DiaryRespone> getMyDiary();

}
