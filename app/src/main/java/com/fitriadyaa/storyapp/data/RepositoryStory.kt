package com.fitriadyaa.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.fitriadyaa.storyapp.data.remote.retrofit.ApiServices
import com.fitriadyaa.storyapp.data.remote.response.authResponse.LoginResponse
import com.fitriadyaa.storyapp.data.remote.response.authResponse.RegisterResponse
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.StoryPostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.StoryResponse

class RepositoryStory(private val apiService: ApiServices) {

    fun getStories(page: Int, size: Int): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStories(page, size)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("StoryRepository", "getStories: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun postStory(file: MultipartBody.Part, description: RequestBody): LiveData<Result<StoryPostResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.postStory(file, description)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("StoryRepository", "postStory: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun postSignUp(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.postSignUp(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("StoryRepository", "postSignUp: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun postLogin(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.postLogin(email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("StoryRepository", "postLogin: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }
}
