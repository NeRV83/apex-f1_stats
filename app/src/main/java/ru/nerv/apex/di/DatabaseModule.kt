package ru.nerv.apex.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.nerv.apex.data.local.ApexDatabase
import ru.nerv.apex.data.local.dao.RaceDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ApexDatabase =
        Room.databaseBuilder(context, ApexDatabase::class.java, "apex.db").build()

    @Provides
    fun provideRaceDao(db: ApexDatabase): RaceDao = db.raceDao()
}