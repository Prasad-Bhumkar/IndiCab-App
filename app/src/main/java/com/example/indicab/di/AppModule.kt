 package com.example.indicab.di
 
 import com.example.indicab.api.BookingService
 import com.example.indicab.data.AppDatabase
 import com.example.indicab.data.dao.FavoriteLocationDao
 import dagger.Module
 import dagger.Provides
 import dagger.hilt.InstallIn
 import dagger.hilt.components.SingletonComponent
 import retrofit2.Retrofit
 import retrofit2.converter.gson.GsonConverterFactory
 import javax.inject.Singleton
 
 @Module
 @InstallIn(SingletonComponent::class)
 object AppModule {
 
     @Provides
     @Singleton
     fun provideOkHttpClient(): OkHttpClient {
         val certificatePinner = CertificatePinner.Builder()
             .add("api.indicab.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=") // Replace with actual certificate pin
             .add("api.indicab.com", "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=") // Replace with backup certificate pin
             .build()
 
         return OkHttpClient.Builder()
             .connectTimeout(30, TimeUnit.SECONDS)
             .readTimeout(30, TimeUnit.SECONDS)
             .writeTimeout(30, TimeUnit.SECONDS)
             .certificatePinner(certificatePinner)
             .addInterceptor(HttpLoggingInterceptor().apply {
                 level = HttpLoggingInterceptor.Level.BODY
             })
             .addInterceptor { chain ->
                 val request = chain.request().newBuilder()
                     .addHeader("Authorization", "Bearer ${getAuthToken()}")
                     .build()
                 chain.proceed(request)
             }
             .addInterceptor { chain ->
                 val request = chain.request()
                 var response = chain.proceed(request)
                 var tryCount = 0
                 
                 while (!response.isSuccessful && tryCount < 3) {
                     tryCount++
                     response = chain.proceed(request)
                 }
                 response
             }
             .build()
     }
 
     @Provides
     @Singleton
     fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
         return Retrofit.Builder()
             .baseUrl("https://api.indicab.com/")
             .client(okHttpClient)
             .addConverterFactory(GsonConverterFactory.create())
             .build()
     }
 
     private fun getAuthToken(): String {
         val secureStorage: SecureStorage // Assuming you can inject this via Dagger
         return secureStorage.retrieveData("auth_token_key") ?: ""
     }
 
     @Provides
     @Singleton
     fun provideBookingService(retrofit: Retrofit): BookingService {
         return retrofit.create(BookingService::class.java)
     }
 
     @Provides
     @Singleton
     fun provideDatabase(application: Application): AppDatabase {
         return AppDatabase.getInstance(application)
     }
 
     @Provides
     @Singleton
     fun provideFavoriteLocationDao(database: AppDatabase): FavoriteLocationDao {
         return database.favoriteLocationDao()
     }
 
     @Provides
     @Singleton
     fun provideBookingHistoryDao(database: AppDatabase): BookingHistoryDao {
         return database.bookingHistoryDao()
     }
 
     @Provides
     @Singleton
     fun provideBookingHistoryRepository(
         bookingHistoryDao: BookingHistoryDao,
         cache: Cache<Long, BookingHistory>
     ): BookingHistoryRepository {
         return BookingHistoryRepository(bookingHistoryDao, cache)
     }
 
     @Provides
     @Singleton
     fun provideBookingHistoryCache(): Cache<Long, BookingHistory> {
         return MemoryCache()
     }
 
     @Provides
     @Singleton
     fun provideNetworkMonitor(context: Context): NetworkMonitor {
         return NetworkMonitor(context)
     }
 
     @Provides
     @Singleton
     fun provideOperationQueue(): OperationQueue {
         return OperationQueue()
     }
 
     @Provides
     @Singleton
     fun provideMonitoringService(context: Context): MonitoringService {
         return MonitoringService(context)
     }
 
     @Provides
     @Singleton
     fun provideSecureStorage(context: Context): SecureStorage {
         return SecureStorage(context)
     }
 
     @Provides
     @Singleton
     fun provideRideHistoryRepository(rideHistoryDao: RideHistoryDao): RideHistoryRepository {
         return RideHistoryRepository(rideHistoryDao)
     }
 
     @Provides
     @Singleton
     fun providePaymentService(): PaymentService {
         return MockPaymentService()
     }
 }
