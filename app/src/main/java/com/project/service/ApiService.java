package com.project.service;

import com.project.respone.ArticleDetailResponse;
import com.project.respone.ArticlePopulerResponse;
import com.project.respone.DiaryDetailResponse;
import com.project.respone.DiaryRespone;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
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
}
