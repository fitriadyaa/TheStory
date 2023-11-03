package com.fitriadyaa.storyapp.di

import android.content.Context
import com.fitriadyaa.storyapp.data.RepositoryStory
import com.fitriadyaa.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): RepositoryStory {
        val apiService = ApiConfig.getApiService(context)
        return runBlocking {
            RepositoryStory(apiService)
        }
    }
}