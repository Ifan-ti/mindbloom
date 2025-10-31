package com.project.service;

import com.project.respone.ArticlePopulerResponse;
import com.project.respone.DiaryRespone;

import retrofit2.Call;
import retrofit2.http.GET;
public interface ApiService {
    @GET("api/articles_populer")
    Call<ArticlePopulerResponse> getArticles();

    @GET("api/Diary")
    Call<DiaryRespone> getDiary();
}
