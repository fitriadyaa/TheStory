package com.fitriadyaa.storyapp.data.remote.retrofit

import android.content.Context
import com.fitriadyaa.storyapp.data.remote.response.AuthInterceptor
import com.fitriadyaa.storyapp.utils.Preference
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    fun getApiService(context: Context): ApiServices {
        val token = Preference.initPref(context, "onSignIn").getString("token", null)

        val okHttpClient = OkHttpClient.Builder().apply {
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            addInterceptor(loggingInterceptor)
            token?.let { addInterceptor(AuthInterceptor(it)) }
        }.build()

        return Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ApiServices::class.java)
    }
}
