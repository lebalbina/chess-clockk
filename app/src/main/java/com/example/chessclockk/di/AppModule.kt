package com.example.chessclockk.di

import android.content.Context
import android.content.SharedPreferences
import com.example.chessclockk.clock.SoundManager
import com.example.chessclockk.usecase.TempoUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
    fun provideTempoUseCase(sharedPreferences: SharedPreferences) : TempoUseCase {
        return TempoUseCase(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideSoundManager(@ApplicationContext context: Context): SoundManager {
        return SoundManager(context)
    }
}
