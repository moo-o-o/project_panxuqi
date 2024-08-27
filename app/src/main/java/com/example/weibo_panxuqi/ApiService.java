package com.example.weibo_panxuqi;

import com.example.weibo_panxuqi.request.LikeRequest;
import com.example.weibo_panxuqi.request.LoginRequest;
import com.example.weibo_panxuqi.request.PhoneRequest;
import com.example.weibo_panxuqi.request.UnlikeRequest;
import com.example.weibo_panxuqi.request.UserInfoRequest;
import com.example.weibo_panxuqi.response.ApiResponse;
import com.example.weibo_panxuqi.response.LikeResponse;
import com.example.weibo_panxuqi.response.LoginResponse;
import com.example.weibo_panxuqi.response.UnlikeResponse;
import com.example.weibo_panxuqi.response.UserInfoResponse;
import com.example.weibo_panxuqi.response.WeiboResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/weibo/api/auth/sendCode")
    Call<ApiResponse> sendCode(@Body PhoneRequest phoneRequest);

    @Headers("Content-Type: application/json")
    @POST("/weibo/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @Headers("Content-Type: application/json")
    @GET("/weibo/homePage")
    Call<WeiboResponse> getHomePage(@Query("Authorization") String token,
                                    @Query("current") int current,
                                    @Query("size") int size);

    @Headers("Content-Type: application/json")
    @POST("/weibo/like/up")
    Call<LikeResponse> like(@Query("Authorization") String token, @Body LikeRequest likeRequest);

    @Headers("Content-Type: application/json")
    @POST("/weibo/like/down")
    Call<UnlikeResponse> unlike(@Query("Authorization") String token, @Body UnlikeRequest unlikeRequest);

    @Headers("Content-Type: application/json")
    @GET("/weibo/api/user/info")
    Call<UserInfoResponse> getUserInfo(@Query("Authorization") String token);
}
