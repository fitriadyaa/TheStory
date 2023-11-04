package com.fitriadyaa.storyapp.ui.maps

import androidx.lifecycle.ViewModel
import com.fitriadyaa.storyapp.data.RepositoryStory

class MapsViewModel(private val repositoryStory: RepositoryStory): ViewModel() {
    fun getStoriesWithLocation() = repositoryStory.getStoriesWithLocation()
}