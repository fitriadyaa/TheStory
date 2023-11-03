package com.fitriadyaa.storyapp.ui.auth.register

import androidx.lifecycle.ViewModel
import com.fitriadyaa.storyapp.data.RepositoryStory

class RegisterViewModel(private val storyRepository: RepositoryStory) : ViewModel() {
    fun register(name: String, email: String, password: String) = storyRepository.postSignUp(name, email, password)
}