package com.mandiri.movieapp.core.network

import com.mandiri.movieapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TmdbAuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val isBearerTokenValid = BuildConfig.TMDB_ACCESS_TOKEN.isNotBlank() &&
                BuildConfig.TMDB_ACCESS_TOKEN != "dummy_tmdb_access_token"

        val requestBuilder = originalRequest.newBuilder()

        if (isBearerTokenValid) {
            requestBuilder.header("Authorization", "Bearer ${BuildConfig.TMDB_ACCESS_TOKEN}")
            requestBuilder.header("accept", "application/json")
        } else {
            // Fallback to TMDB v3 API Key query parameter
            val newUrl = originalUrl.newBuilder()
                .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build()
            requestBuilder.url(newUrl)
        }

        return chain.proceed(requestBuilder.build())
    }
}
