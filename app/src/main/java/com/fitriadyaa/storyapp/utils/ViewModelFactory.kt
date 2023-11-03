package com.fitriadyaa.storyapp.utils

import ListStoryViewModel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fitriadyaa.storyapp.di.Injection
import com.fitriadyaa.storyapp.ui.auth.login.LoginViewModel
import com.fitriadyaa.storyapp.ui.auth.register.RegisterViewModel
import com.fitriadyaa.storyapp.ui.story.createStory.CreateStoryViewModel


class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ListStoryViewModel::class.java) -> {
                ListStoryViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(CreateStoryViewModel::class.java) -> {
                CreateStoryViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(Injection.provideRepository(context)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}