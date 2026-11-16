package ru.nerv.apex.data.repository

import kotlinx.coroutines.flow.Flow
import ru.nerv.apex.data.local.entity.DriverEntity
import ru.nerv.apex.data.local.entity.RaceEntity
import ru.nerv.apex.data.local.entity.ResultEntity
import ru.nerv.apex.data.local.entity.TeamEntity

interface RaceRepository {
    fun observeRaces(year: Int): Flow<List<RaceEntity>>
    suspend fun refreshRaces(year: Int)

    fun observeResults(roundId: String): Flow<List<ResultEntity>>
    suspend fun refreshResults(roundId: String)

    suspend fun refreshRaceLaps(roundId: String, sessionId: String)

    fun observeTeams(): Flow<List<TeamEntity>>

    suspend fun refreshTeams()

    fun observeDrivers(): Flow<List<DriverEntity>>

    suspend fun refreshDrivers(year: Int)
}