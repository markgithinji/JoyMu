package com.example.joymu.data.supabase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>
    data class Error(val message: String, val cause: Throwable? = null) : NetworkResult<Nothing>
    data object Loading : NetworkResult<Nothing>
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> = withContext(Dispatchers.IO) {
    try {
        NetworkResult.Success(apiCall())
    } catch (e: IOException) {
        NetworkResult.Error(
            message = "Network connection failed. Please check your internet connection.",
            cause = e
        )
    } catch (e: Exception) {
        NetworkResult.Error(
            message = e.localizedMessage ?: "An unexpected error occurred. Please try again.",
            cause = e
        )
    }
}
