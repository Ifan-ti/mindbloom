package com.project.service;

import com.project.model.DiaryUploadModel;
import com.project.request.ForgotPasswordRequest;
import com.project.request.ResetPasswordRequest;
import com.project.request.VerifyOTPRequest;
import com.project.response.ApiResponse;
import com.project.response.ArticleDetailResponse;
import com.project.response.DiaryDetailResponse;
import com.project.response.DiaryRespone;
import com.project.response.DiaryPostResponse;

import com.project.request.AftercareRequest;
import com.project.request.ChatSaveRequest;
import com.project.request.DeepSeekRequest;
import com.project.request.DiaryRequest;
import com.project.request.RegisterRequest;
import com.project.request.LoginRequest;
import com.project.request.UpdateProfileRequest;

import com.project.response.AfterCareResponse;
import com.project.response.ArticleDetailResponse;
import com.project.response.ArticlesResponse;
import com.project.response.ChatHistoryChatBot;
import com.project.response.ChatHistoryResponse;
import com.project.response.DeepSeekResponse;
import com.project.response.DefaultResponse;
import com.project.response.DiaryDetailResponse;
import com.project.response.DiaryRespone;
import com.project.response.DiaryPostResponse;
import com.project.response.ExpertsDetailResponse;
import com.project.response.ExpertsRensponse;
import com.project.response.LoginResponse;
import com.project.response.MoodResponse;
import com.project.response.NotificationResponse;
import com.project.response.OTPResponse;
import com.project.response.PatientDetailResponse;
import com.project.response.PostResponse;
import com.project.response.ProfileResponse;
import com.project.response.StatusResponse;
import com.project.response.UserResponse;
import com.project.response.VerifyOTPResponse;
import com.project.response.ViewCountResponse;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @POST("api/register")
    Call<LoginResponse> registerUser(@Body RegisterRequest request);

    @POST("api/users/update")
    Call<ResponseBody> updateProfile(@Body UpdateProfileRequest request);

    @GET("api/users/experts")
    Call<ExpertsRensponse> getExpert();
    @GET("api/users/expertsDetail/{id}")
    Call<ExpertsDetailResponse> getExpertsDetail(@Path("id") int diaryId);

    //===================
    //     ARTICLES-API
    //===================
    @GET("api/articles/populer")
    Call<ArticlesResponse> getArticlesPopuler();

    @GET("api/articles/new")
    Call<ArticlesResponse> getArticlesNew();

    @GET("api/articles/Detail/{id}")
    Call<ArticleDetailResponse> getArticleDetail(@Path("id") int articleId);
    // Increment view count artikel
    @PATCH("api/articles/view/{id}")
    Call<ViewCountResponse> incrementArticleView(@Path("id") int articleId, @Header("Authorization") String authToken);

    //===================
    //     DIARY-API
    //===================
    @GET("api/Diary/all")
    Call<DiaryRespone> getDiary();

    @GET("api/Diary/Detail/{id}")
    Call<DiaryDetailResponse> getDiaryDetail(@Path("id") int diaryId);

    @GET("api/Diary/me")
    Call<DiaryRespone> getMyDiary(@Header("Authorization") String authToken);

    @GET("api/Diary/Mood")
    Call<MoodResponse> getDiaryMood(@Header("Authorization") String authToken);

    @POST("api/Diary/upload")
    Call<DiaryPostResponse> uploadDiary(
            @Header("Authorization") String authToken,
            @Body DiaryUploadModel diary
    );


    @POST("api/Diary/create")
    Call<DiaryPostResponse> createDiary(@Body DiaryRequest diaryRequest);

    @PUT("api/Diary/update/{id}")
    Call<DiaryPostResponse> updateDiary(
            @Header("Authorization") String token,
            @Path("id") int diaryId,
            @Body DiaryUploadModel diary
    );


    //===================
    //     POSTING-API
    //===================
    @GET("api/posts")
    Call<PostResponse> getPosts();

    @GET("api/notifications")
    Call<NotificationResponse> getNotifications(
            @Header("Authorization") String authToken
    );

    //===================
    //     CHATBOT-API
    //===================
    @POST("api/chatbot/chat")
    Call<DeepSeekResponse> sendChatToProxy(@Body DeepSeekRequest request);

    @GET("api/chatbot/history/{userId}")
    Call<List<ChatHistoryChatBot>> getChatHistory(@Path("userId") int userId);

    @POST("api/chatbot/save")
    Call<DefaultResponse> saveChat(@Body ChatSaveRequest request);

    //===================
    //     PROFILE-API
    //===================
    @GET("api/users/profile")
    Call<ProfileResponse> getProfile(
            @Header("Authorization") String authToken
    );

    //===================
    //     CONSULTATION-API
    //===================
    @POST("api/consultation/send")
    Call<DefaultResponse> sendMessage(@Body Map<String, Object> body);

    @POST("api/consultation/request")
    Call<DefaultResponse> sendConsultationRequest(@Body Map<String, Object> body);

    @GET("api/consultation/status")
    Call<StatusResponse> checkRequestStatus(
            @Query("uid") int userId,
            @Query("eid") int expertId
    );

    @GET("api/consultation/history")
    Call<ChatHistoryResponse> getChatHistory(@Query("room_id") String roomId);

    @POST("api/consultation/approve-by-user")
    Call<DefaultResponse> approveRequestByUser(@Body Map<String, Object> body);

    //===================
    //     AFTERCARE-API  ⭐ BARU
    //===================
    //===================
//     AFTERCARE-API
//===================
    @POST("api/aftercare")
    Call<AfterCareResponse> generateAftercare(
            @Header("Authorization") String authToken,
            @Body AftercareRequest request
    );

    // ===================
    // OTP - API
    // ===================

    @POST("api/forgot-password/request")
    Call<OTPResponse> requestOTP(@Body ForgotPasswordRequest request);

    @POST("api/forgot-password/verify-otp")
    Call<VerifyOTPResponse> verifyOTP(@Body VerifyOTPRequest request);

    @POST("api/forgot-password/reset")
    Call<ApiResponse> resetPassword(@Body ResetPasswordRequest request);


    //===================
    //     PATIENT-API
    //===================
    @GET("api/patient/detail")
    Call<PatientDetailResponse> getPatientDetail(@Header("Authorization") String token);

    @GET("api/expert-chat/status")
    Call<StatusResponse> checkRequestStatusExpert(
            @Query("uid") int patientId,
            @Query("eid") int expertId
    );
}
