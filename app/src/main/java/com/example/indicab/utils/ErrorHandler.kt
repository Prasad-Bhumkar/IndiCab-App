package com.example.indicab.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: AppError) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

sealed class AppError {
    data class NetworkError(val message: String) : AppError()
    data class ServerError(val code: Int, val message: String) : AppError()
    data class ValidationError(val message: String) : AppError()
    data class DatabaseError(val message: String) : AppError()
    data class UnknownError(val message: String) : AppError()
    object NoInternetError : AppError()
    object TimeoutError : AppError()
}

class ErrorHandler @Inject constructor(
    private val context: Context
) {
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun <T> handleApiCall(apiCall: suspend () -> T): Flow<Result<T>> {
        return kotlinx.coroutines.flow.flow {
            if (!isNetworkAvailable()) {
                emit(Result.Error(AppError.NoInternetError))
                return@flow
            }

            try {
                val response = apiCall()
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(mapError(e)))
            }
        }.onStart { emit(Result.Loading) }
    }

    fun <T> handleDatabaseOperation(operation: suspend () -> T): Flow<Result<T>> {
        return kotlinx.coroutines.flow.flow {
            try {
                val result = operation()
                emit(Result.Success(result))
            } catch (e: Exception) {
                emit(Result.Error(AppError.DatabaseError(e.message ?: "Database operation failed")))
            }
        }.onStart { emit(Result.Loading) }
    }

    private fun mapError(throwable: Throwable): AppError {
        return when (throwable) {
            is IOException -> AppError.NetworkError(throwable.message ?: "Network error occurred")
            is SocketTimeoutException -> AppError.TimeoutError
            is HttpException -> {
                when (throwable.code()) {
                    in 400..499 -> AppError.ValidationError(throwable.message())
                    in 500..599 -> AppError.ServerError(
                        throwable.code(),
                        throwable.message()
                    )
                    else -> AppError.UnknownError(throwable.message())
                }
            }
            else -> AppError.UnknownError(throwable.message ?: "Unknown error occurred")
        }
    }

    fun <T> Flow<T>.asResult(): Flow<Result<T>> {
        return this
            .map<T, Result<T>> { Result.Success(it) }
            .onStart { emit(Result.Loading) }
            .catch { emit(Result.Error(mapError(it))) }
    }

    companion object {
        private const val TAG = "ErrorHandler"
    }
}
