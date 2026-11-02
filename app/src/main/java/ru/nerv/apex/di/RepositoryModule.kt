package ru.nerv.apex.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.nerv.apex.data.repository.RaceRepository
import ru.nerv.apex.data.repository.RaceRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRaceRepository(impl: RaceRepositoryImpl): RaceRepository
}