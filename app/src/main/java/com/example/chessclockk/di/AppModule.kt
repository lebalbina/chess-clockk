package com.example.chessclockk.di

import android.content.Context
import android.content.SharedPreferences
import com.example.chessclockk.TempoUseCase
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
}
