package com.training.radioapptrial.channelsGetViewPlay.di

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExoPlayerModule  {

    @Singleton
    @Provides
    fun provideExoPlayer(@ApplicationContext appContext: Context): ExoPlayer {
        return ExoPlayer.Builder(appContext).build().apply {
            playWhenReady = true
        }
    }
}