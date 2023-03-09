package com.bignerdranch.android.weather.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineDispatcherModule {

    @Provides
    @Singleton
    fun provideDispatcherForSettingsLoader(): CoroutineDispatcher = Dispatchers.Default
}