package com.fitriadyaa.storyapp.data.remote.response

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private var token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return if (request.header("No-Authentication") == null && token.isNotEmpty()) {
            val finalToken = "Bearer $token"
            val authenticatedRequest = request.newBuilder()
                .addHeader("Authorization", finalToken)
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            chain.proceed(request)
        }
    }
}
