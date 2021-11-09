package com.training.radioapptrial.channelsGetViewPlay.di

import com.training.radioapptrial.channelsGetViewPlay.channelsapi.ApiService
import com.training.radioapptrial.channelsGetViewPlay.channelsapi.RadioStationsClient
import com.training.radioapptrial.channelsGetViewPlay.util.NetworkConstants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideRetrofit(): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofitClient(service: ApiService): RadioStationsClient {
        return RadioStationsClient((service))
    }


}