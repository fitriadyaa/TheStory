package com.fitriadyaa.storyapp.ui.auth.login

import RepositoryStory
import androidx.lifecycle.ViewModel

class LoginViewModel(private val repositoryStory: RepositoryStory) : ViewModel() {
    fun login(email: String, password: String) = repositoryStory.postLogin(email, password)
}