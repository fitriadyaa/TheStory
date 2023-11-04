package com.fitriadyaa.storyapp.ui.story.createStory

import androidx.lifecycle.ViewModel
import com.fitriadyaa.storyapp.data.RepositoryStory
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateStoryViewModel(private val repositoryStory: RepositoryStory) : ViewModel() {

    fun postStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lon: Double,
        lat: Double
    ) = repositoryStory.postStory(file, description, lon, lat)
}