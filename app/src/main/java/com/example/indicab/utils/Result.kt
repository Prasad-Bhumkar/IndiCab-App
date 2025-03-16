package com.example.indicab.utils

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun error(exception: Exception): Result<Nothing> = Error(exception)
        fun loading(): Result<Nothing> = Loading

        fun <T> fromThrowable(throwable: Throwable): Result<T> =
            Error(when (throwable) {
                is Exception -> throwable
                else -> Exception(throwable)
            })
    }

    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (Exception) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }

    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }

    fun <R> map(transform: (T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(exception)
            is Loading -> Loading
        }
    }

    suspend fun <R> suspendMap(transform: suspend (T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(exception)
            is Loading -> Loading
        }
    }

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Result is Loading")
    }

    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading
}

// Extension function to handle API responses
suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): Result<T> = try {
    Result.success(apiCall())
} catch (throwable: Throwable) {
    Result.fromThrowable(throwable)
}

// Extension function to handle database operations
suspend fun <T> safeDbOperation(
    dbOperation: suspend () -> T
): Result<T> = try {
    Result.success(dbOperation())
} catch (throwable: Throwable) {
    Result.fromThrowable(throwable)
}

// Extension function to handle location operations
suspend fun <T> safeLocationOperation(
    locationOperation: suspend () -> T
): Result<T> = try {
    Result.success(locationOperation())
} catch (throwable: Throwable) {
    Result.fromThrowable(throwable)
}

// Extension function to handle payment operations
suspend fun <T> safePaymentOperation(
    paymentOperation: suspend () -> T
): Result<T> = try {
    Result.success(paymentOperation())
} catch (throwable: Throwable) {
    Result.fromThrowable(throwable)
}
