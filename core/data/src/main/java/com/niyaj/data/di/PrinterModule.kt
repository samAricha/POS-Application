package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.PrinterRepositoryImpl
import com.niyaj.data.data.repository.PrinterValidationRepositoryImpl
import com.niyaj.data.repository.PrinterRepository
import com.niyaj.data.repository.validation.PrinterValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object PrinterModule {

    @Provides
    fun providePrinterRepository(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): PrinterRepository {
        return PrinterRepositoryImpl(config, ioDispatcher)
    }


    @Provides
    fun providePrinterValidationRepository(): PrinterValidationRepository {
        return PrinterValidationRepositoryImpl()
    }
}