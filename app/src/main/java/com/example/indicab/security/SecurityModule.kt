package com.example.indicab.security

import android.content.Context
import com.example.indicab.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            .add("api.indicab.com", 
                "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=") // Replace with actual certificate hash
            .add("*.indicab.com", 
                "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=") // Replace with actual certificate hash
            .build()
    }

    @Provides
    @Singleton
    fun provideSecureOkHttpClient(
        @ApplicationContext context: Context,
        certificatePinner: CertificatePinner
    ): OkHttpClient {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers = trustManagerFactory.trustManagers
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagers, null)

        return OkHttpClient.Builder()
            .certificatePinner(certificatePinner)
            .sslSocketFactory(sslContext.socketFactory, trustManagers[0] as X509TrustManager)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("X-Api-Key", BuildConfig.API_KEY)
                    .header("X-Device-Id", DeviceUtils.getDeviceId(context))
                    .header("X-Request-Timestamp", System.currentTimeMillis().toString())
                
                if (!original.url.toString().contains("/auth")) {
                    requestBuilder.header("Authorization", "Bearer ${TokenManager.getAccessToken()}")
                }
                
                chain.proceed(requestBuilder.build())
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}
