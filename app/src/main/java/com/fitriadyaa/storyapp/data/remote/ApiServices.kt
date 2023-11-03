package com.fitriadyaa.storyapp.data.remote

import com.fitriadyaa.storyapp.data.remote.response.authResponse.LoginResponse
import com.fitriadyaa.storyapp.data.remote.response.authResponse.RegisterResponse
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.StoryPostResponse
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiServices {
    @FormUrlEncoded
    @POST("register")
    suspend fun postSignUp(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location: Int
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): StoryPostResponse
}
