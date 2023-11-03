package com.fitriadyaa.storyapp.ui.auth.login

import androidx.lifecycle.ViewModel
import com.fitriadyaa.storyapp.data.RepositoryStory

class LoginViewModel(private val repositoryStory: RepositoryStory) : ViewModel() {
    fun login(email: String, password: String) = repositoryStory.postLogin(email, password)
}