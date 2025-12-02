package com.project.service;

import com.project.data.DiaryUploadModel;
import com.project.respone.ArticleDetailResponse;
import com.project.respone.ArticlePopulerResponse;
import com.project.respone.DiaryDetailResponse;
import com.project.respone.DiaryRespone;
import com.project.response.DiaryPostResponse;


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

    @GET("api/articles/{id}")
    Call<ArticleDetailResponse> getArticleDetail(@Path("id") int articleId);

    @GET("api/Diary/{id}")
    Call<DiaryDetailResponse> getDiaryDetail(@Path("id") int diaryId);

    @POST("api/Diary")
    Call<DiaryPostResponse> uploadDiary(@Body DiaryUploadModel diary);

    @PATCH("api/articles/view/{id}")
    Call<Void> incrementArticleView(@Path("id") int articleId);
}

