package com.training.radioapptrial.radioforegroundservice.di

import com.training.radioapptrial.channelsGetViewPlay.factory.MediaSourceAbstractFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaSourceAbstractFactoryModule {

    @Singleton
    @Provides
    fun provideMediaSourceAbstractFactory():MediaSourceAbstractFactory{
        return MediaSourceAbstractFactory()
    }
}