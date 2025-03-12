# Retrofit Setup Guide for IndiCab

This guide explains how to set up and use Retrofit for network requests in the IndiCab Android application.

## Dependencies

Add these dependencies to your `app/build.gradle.kts`:

```kotlin
dependencies {
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // OkHttp for logging
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    
    // Coroutines support
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")
}
```

## Basic Setup

1. Create API Interface:

```kotlin
interface ApiService {
    @GET("rides")
    suspend fun getRides(): Response<List<Ride>>

    @POST("bookings")
    suspend fun createBooking(@Body booking: BookingRequest): Response<Booking>
}
```

2. Configure Retrofit Instance:

```kotlin
object RetrofitClient {
    private const val BASE_URL = "https://api.indicab.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
```

## Usage Examples

### In Repository

```kotlin
class RideRepository(private val apiService: ApiService) {
    suspend fun getRides(): Result<List<Ride>> {
        return try {
            val response = apiService.getRides()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### In ViewModel

```kotlin
class RideViewModel(private val repository: RideRepository) : ViewModel() {
    private val _rides = MutableStateFlow<List<Ride>>(emptyList())
    val rides: StateFlow<List<Ride>> = _rides

    fun fetchRides() {
        viewModelScope.launch {
            repository.getRides()
                .onSuccess { rides -> _rides.value = rides }
                .onFailure { error -> /* Handle error */ }
        }
    }
}
```

## Error Handling

1. Create a wrapper class for API responses:

```kotlin
sealed class ApiResponse<T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error<T>(val message: String) : ApiResponse<T>()
}
```

2. Create an extension function for Response:

```kotlin
fun <T> Response<T>.toApiResponse(): ApiResponse<T> {
    return if (isSuccessful) {
        val body = body()
        if (body != null) {
            ApiResponse.Success(body)
        } else {
            ApiResponse.Error("Response body is null")
        }
    } else {
        ApiResponse.Error(errorBody()?.string() ?: "Unknown error")
    }
}
```

## Authentication

Add an interceptor for authentication:

```kotlin
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${getToken()}")
            .build()
        return chain.proceed(request)
    }
}
```

## Best Practices

1. Use suspend functions for network calls
2. Handle errors appropriately
3. Implement proper caching strategies
4. Use dependency injection for Retrofit instances
5. Keep API interfaces clean and focused
6. Use proper response models
7. Implement retry mechanisms for failed requests

## Testing

1. Create mock responses:

```kotlin
@Test
fun `test successful API call`() = runTest {
    val mockResponse = MockResponse()
        .setResponseCode(200)
        .setBody(/* your JSON response */)
    mockWebServer.enqueue(mockResponse)

    val result = repository.getRides()
    assert(result.isSuccess)
}
```

2. Test error scenarios:

```kotlin
@Test
fun `test API error handling`() = runTest {
    val mockResponse = MockResponse()
        .setResponseCode(404)
    mockWebServer.enqueue(mockResponse)

    val result = repository.getRides()
    assert(result.isFailure)
}
