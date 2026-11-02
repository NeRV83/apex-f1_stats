package ru.nerv.apex.data.repository

import kotlinx.coroutines.flow.Flow
import ru.nerv.apex.data.local.dao.RaceDao
import ru.nerv.apex.data.local.entity.RaceEntity
import ru.nerv.apex.data.remote.JolpicaApi
import ru.nerv.apex.data.remote.dto.ScheduleEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RaceRepositoryImpl @Inject constructor(
    private val api: JolpicaApi,
    private val dao: RaceDao
) : RaceRepository {

    override fun observeRaces(year: Int): Flow<List<RaceEntity>> =
        dao.observeRaces(year)

    override suspend fun refreshRaces(year: Int) {
        val response = api.getSchedule(year)
        val entities = response.data.events.map { it.toRaceEntity(year) }
        dao.insertRaces(entities)
    }
}

private fun ScheduleEvent.toRaceEntity(year: Int): RaceEntity {
    val raceSession = schedule.firstOrNull { it.code == "R" }
    return RaceEntity(
        roundId = round.id,
        seasonYear = year,
        roundNumber = round.number,
        raceName = round.name,
        circuitName = circuit.name,
        country = circuit.country,
        countryCode = circuit.countryCode,
        raceTimestamp = raceSession?.timestamp,
        raceLocalTimestamp = raceSession?.localTimestamp
    )
}