package com.fitriadyaa.storyapp.ui.auth.register

import RepositoryStory
import androidx.lifecycle.ViewModel

class RegisterViewModel(private val storyRepository: RepositoryStory) : ViewModel() {
    fun register(name: String, email: String, password: String) = storyRepository.postSignUp(name, email, password)


}