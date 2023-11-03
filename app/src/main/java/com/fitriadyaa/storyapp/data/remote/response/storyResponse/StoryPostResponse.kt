package com.fitriadyaa.storyapp.data.remote.response.storyResponse

import com.google.gson.annotations.SerializedName

data class StoryPostResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)