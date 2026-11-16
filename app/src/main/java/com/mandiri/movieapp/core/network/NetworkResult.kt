package com.mandiri.movieapp.core.network

import java.io.IOException
import retrofit2.HttpException

sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>
    data class Error(val message: String, val code: Int? = null, val throwable: Throwable? = null) : NetworkResult<Nothing>
    data object Loading : NetworkResult<Nothing>
}

inline fun <T, R> NetworkResult<T>.map(transform: (T) -> R): NetworkResult<R> {
    return when (this) {
        is NetworkResult.Success -> NetworkResult.Success(transform(data))
        is NetworkResult.Error -> NetworkResult.Error(message, code, throwable)
        is NetworkResult.Loading -> NetworkResult.Loading
    }
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(apiCall())
    } catch (e: HttpException) {
        val errorMessage = when (e.code()) {
            401 -> "Invalid or missing TMDB API credentials."
            404 -> "Requested movie resource was not found."
            429 -> "Rate limit reached. Please wait a moment."
            in 500..599 -> "TMDB server error (${e.code()}). Please try again later."
            else -> e.message() ?: "HTTP error ${e.code()}"
        }
        NetworkResult.Error(message = errorMessage, code = e.code(), throwable = e)
    } catch (e: IOException) {
        NetworkResult.Error(
            message = "Network connection failed. Please check your internet connection.",
            throwable = e
        )
    } catch (e: Exception) {
        NetworkResult.Error(
            message = e.localizedMessage ?: "An unexpected error occurred.",
            throwable = e
        )
    }
}
