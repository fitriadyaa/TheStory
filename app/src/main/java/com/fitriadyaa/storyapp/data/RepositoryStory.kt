package com.fitriadyaa.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.fitriadyaa.storyapp.data.remote.retrofit.ApiServices
import com.fitriadyaa.storyapp.data.remote.response.authResponse.LoginResponse
import com.fitriadyaa.storyapp.data.remote.response.authResponse.RegisterResponse
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.Story
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.StoryPostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.StoryResponse
import kotlinx.coroutines.Dispatchers

class RepositoryStory(private val apiService: ApiServices) {

    fun getStoriesWithLocation(): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading) // Ensure Result.Loading is properly referenced
        try {
            val response = apiService.getStoriesWithLocation(1)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.d("ListStoryViewModel", "getStoriesWithLocation: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStories(): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPaging(apiService)
            }
        ).liveData
    }

    fun postStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lon: Double?,
        lat: Double?
    ): LiveData<Result<StoryPostResponse>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.postStory(file, description, lon, lat)
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
