package com.fitriadyaa.storyapp.data

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: String) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}
