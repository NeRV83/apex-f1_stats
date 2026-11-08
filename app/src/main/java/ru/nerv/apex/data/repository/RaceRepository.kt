package ru.nerv.apex.data.repository

import kotlinx.coroutines.flow.Flow
import ru.nerv.apex.data.local.entity.RaceEntity
import ru.nerv.apex.data.local.entity.ResultEntity

interface RaceRepository {
    fun observeRaces(year: Int): Flow<List<RaceEntity>>
    suspend fun refreshRaces(year: Int)

    fun observeResults(roundId: String): Flow<List<ResultEntity>>
    suspend fun refreshResults(roundId: String)
}