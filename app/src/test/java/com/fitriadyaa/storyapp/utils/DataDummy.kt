package com.fitriadyaa.storyapp.utils

import com.fitriadyaa.storyapp.data.remote.response.authResponse.LoginResponse
import com.fitriadyaa.storyapp.data.remote.response.authResponse.LoginResult
import com.fitriadyaa.storyapp.data.remote.response.authResponse.RegisterResponse
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.Story
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.StoryPostResponse
import com.fitriadyaa.storyapp.data.remote.response.storyResponse.StoryResponse

object DataDummy {
    fun generateDummyStory(count: Int): StoryResponse {
        val listStory = (1..count).map { i ->
            Story(
                createdAt = "2022-02-22T22:22:22Z",
                description = "Description $i",
                id = "id_$i",
                lat = i.toDouble() * 10,
                lon = i.toDouble() * 10,
                name = "Name $i",
                photoUrl = "https://images.tokopedia.net/img/cache/500-square/VqbcmM/2022/10/4/5bb38329-dcf8-4c23-b707-8da8f2f12bd8.jpg"
            )
        }

        return StoryResponse(
            error = false,
            message = "Stories fetched successfully",
            listStory = listStory
        )
    }

    fun generateDummyCreateStory(): StoryPostResponse {
        return StoryPostResponse(
            error = false,
            message = "success"
        )
    }

    fun generateDummyRegister(): RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "User Created"
        )
    }

    fun generateDummyLogin(): LoginResponse {
        return LoginResponse(
            error = false,
            message = "success",
            loginResult = LoginResult(
                userId = "user-hfy8hws_LffAgK61",
                name = "Fitria Widyani",
                token = "eyfdjfhru7HDEHUiks.eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
            )
        )
    }
}
