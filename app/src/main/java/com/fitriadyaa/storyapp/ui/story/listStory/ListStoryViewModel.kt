package com.fitriadyaa.storyapp.ui.story.listStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fitriadyaa.storyapp.data.RepositoryStory
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.Story

class ListStoryViewModel(repositoryStory: RepositoryStory): ViewModel() {
    val stories: LiveData<PagingData<Story>> = repositoryStory.getStories().cachedIn(viewModelScope)
}