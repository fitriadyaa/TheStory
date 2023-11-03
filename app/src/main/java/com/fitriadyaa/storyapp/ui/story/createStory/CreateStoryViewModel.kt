package com.fitriadyaa.storyapp.ui.story.createStory

import RepositoryStory
import androidx.lifecycle.ViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateStoryViewModel(private val repositoryStory: RepositoryStory) : ViewModel() {

    fun postStory(file: MultipartBody.Part, description: RequestBody) = repositoryStory.postStory(file, description)
}