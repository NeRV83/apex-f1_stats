package ru.nerv.apex.data.repository

import kotlinx.coroutines.flow.Flow
import ru.nerv.apex.data.local.entity.RaceEntity

interface RaceRepository {

    fun observeRaces(year: Int): Flow<List<RaceEntity>>

    suspend fun refreshRaces(year: Int)
}