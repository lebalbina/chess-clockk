package com.example.chessclockk.di

import android.content.Context
import android.content.SharedPreferences
import com.example.chessclockk.ITimeProvider
import com.example.chessclockk.TimeProvider
import com.example.chessclockk.clock.SoundManager
import com.example.chessclockk.usecase.TempoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("clokk_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideTempoUseCase(sharedPreferences: SharedPreferences): TempoRepository {
        return TempoRepository(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideSoundManager(@ApplicationContext context: Context): SoundManager {
        return SoundManager(context)
    }

    @Provides
    @Singleton
    fun provideTimeProvider(): ITimeProvider {
        return TimeProvider()
    }

    @Provides
    @Singleton
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
