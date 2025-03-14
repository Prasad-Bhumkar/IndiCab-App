package com.example.indicab.di

import android.content.Context
import com.example.indicab.analytics.AnalyticsManager
import com.example.indicab.data.sync.OfflineCache
import com.example.indicab.data.sync.SyncManager
import com.example.indicab.monitoring.AppMonitor
import com.example.indicab.utils.ErrorHandler
import com.example.indicab.utils.PerformanceTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MonitoringModule {

    @Provides
    @Singleton
    fun provideErrorHandler(
        @ApplicationContext context: Context
    ): ErrorHandler {
        return ErrorHandler(context)
    }

    @Provides
    @Singleton
    fun provideAnalyticsManager(
        @ApplicationContext context: Context
    ): AnalyticsManager {
        return AnalyticsManager(context)
    }

    @Provides
    @Singleton
    fun provideSyncManager(
        @ApplicationContext context: Context,
        offlineCache: OfflineCache,
        errorHandler: ErrorHandler
    ): SyncManager {
        return SyncManager(context, offlineCache, errorHandler)
    }

    @Provides
    @Singleton
    fun providePerformanceTracker(): PerformanceTracker {
        return PerformanceTracker
    }

    @Provides
    @Singleton
    fun provideAppMonitor(
        analyticsManager: AnalyticsManager,
        errorHandler: ErrorHandler,
        performanceTracker: PerformanceTracker,
        syncManager: SyncManager
    ): AppMonitor {
        return AppMonitor(
            analyticsManager,
            errorHandler,
            performanceTracker,
            syncManager
        )
    }
}
