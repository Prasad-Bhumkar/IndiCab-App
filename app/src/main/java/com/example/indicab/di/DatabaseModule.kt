package com.example.indicab.di

import android.content.Context
import com.example.indicab.data.AppDatabase
import com.example.indicab.data.dao.*
import com.example.indicab.data.repositories.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideWalletDao(database: AppDatabase): WalletDao {
        return database.walletDao()
    }

    @Provides
    @Singleton
    fun providePaymentMethodDao(database: AppDatabase): PaymentMethodDao {
        return database.paymentMethodDao()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteLocationDao(database: AppDatabase): FavoriteLocationDao {
        return database.favoriteLocationDao()
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao)
    }

    @Provides
    @Singleton
    fun providePaymentRepository(
        walletDao: WalletDao,
        paymentMethodDao: PaymentMethodDao,
        transactionDao: TransactionDao
    ): PaymentRepository {
        return PaymentRepository(walletDao, paymentMethodDao, transactionDao)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(favoriteLocationDao: FavoriteLocationDao): LocationRepository {
        return LocationRepository(favoriteLocationDao)
    }

    @Provides
    @Singleton
    fun provideBookingRepository(bookingService: com.example.indicab.api.BookingService): BookingRepository {
        return BookingRepository(bookingService)
    }

    @Provides
    @Singleton
    fun provideDriverRepository(driverService: com.example.indicab.services.DriverService): DriverRepository {
        return DriverRepository(driverService)
    }
}
