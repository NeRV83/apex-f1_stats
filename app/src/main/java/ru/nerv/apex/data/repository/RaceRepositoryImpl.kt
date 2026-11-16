package ru.nerv.apex.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.nerv.apex.data.local.dao.RaceDao
import ru.nerv.apex.data.local.entity.DriverEntity
import ru.nerv.apex.data.local.entity.RaceEntity
import ru.nerv.apex.data.local.entity.ResultEntity
import ru.nerv.apex.data.local.entity.TeamEntity
import ru.nerv.apex.data.remote.JolpicaApi
import ru.nerv.apex.data.remote.dto.DriverStandingData
import ru.nerv.apex.data.remote.dto.ResultItem
import ru.nerv.apex.data.remote.dto.ScheduleEvent
import ru.nerv.apex.data.remote.dto.TeamData
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

    override fun observeResults(roundId: String): Flow<List<ResultEntity>> =
        dao.observeResults(roundId)

    override suspend fun refreshResults(roundId: String) {
        val response = api.getResults(roundId, "R")
        val entities = response.data.results.map { it.toResultEntity(roundId) }
        dao.insertResults(entities)
    }

    override suspend fun refreshRaceLaps(roundId: String, sessionId: String) {
        val response = api.getSession(sessionId)
        val laps = response.data.scheduledLaps
        if (laps != null) {
            dao.updateScheduledLaps(roundId, laps)
        }
    }

    override fun observeTeams(): Flow<List<TeamEntity>> = dao.observeTeams()

    override suspend fun refreshTeams() {
        val response = api.getTeams()
        val entities = response.data.map { it.toTeamEntity() }
        dao.insertTeams(entities)
    }

    override fun observeDrivers(): Flow<List<DriverEntity>> = dao.observeDrivers()

    override suspend fun refreshDrivers(year: Int) {
        val teamsFromDb = dao.observeTeams().first().associateBy { it.name }

        if (teamsFromDb.isEmpty()) {
            runCatching { refreshTeams() }
            val updatedTeams = dao.observeTeams().first().associateBy { it.name }
            mapDrivers(year, updatedTeams)
        } else {
            mapDrivers(year, teamsFromDb)
        }
    }

    private suspend fun mapDrivers(year: Int, teamsMap: Map<String, TeamEntity>) {
        val response = api.getDriverStandings(year)
        val standingsList = response.mrData.standingsTable.standingsLists.firstOrNull() ?: return

        val drivers = standingsList.driverStandings.map { standing ->
            val teamName = standing.constructors.firstOrNull()?.name
            val teamColor = teamsMap[teamName]?.primaryColor

            DriverEntity(
                id = standing.driver.driverId,
                name = "${standing.driver.givenName} ${standing.driver.familyName}",
                abbreviation = standing.driver.code,
                carNumber = standing.driver.permanentNumber?.toIntOrNull(),
                countryFlag = null,
                nationality = standing.driver.nationality,
                teamName = teamName,
                teamColor = teamColor,
                position = standing.position?.toIntOrNull(),
                points = standing.points?.toDoubleOrNull()
            )
        }
        dao.insertDrivers(drivers)
    }

}

private fun ScheduleEvent.toRaceEntity(year: Int): RaceEntity {
    val raceSession = schedule.firstOrNull { it.code == "R" }


    val sessionId = raceSession?.sessions?.firstOrNull { it.type == "R" }?.id

    return RaceEntity(
        roundId = round.id,
        seasonYear = year,
        roundNumber = round.number,
        raceName = round.name,
        circuitName = circuit.name,
        country = circuit.country,
        countryCode = circuit.countryCode,
        countryFlag = circuit.countryFlag,
        raceTimestamp = raceSession?.timestamp,
        raceLocalTimestamp = raceSession?.localTimestamp,
        sessionId = sessionId
    )
}

private fun ResultItem.toResultEntity(roundId: String): ResultEntity =
    ResultEntity(
        roundId = roundId,
        position = position,
        driverName = "${driver.givenName} ${driver.familyName}",
        teamName = team.name,
        teamColor = team.primaryColor,
        time = time,
        status = status,
        points = points
    )

private fun TeamData.toTeamEntity() = TeamEntity(
    id = id,
    name = name,
    primaryColor = primaryColor,
    nationality = nationality,
    countryFlag = countryFlag
)

private fun DriverStandingData.toDriverEntity() = DriverEntity(
    id = driver.id,
    name = "${driver.givenName} ${driver.familyName}",
    abbreviation = driver.abbreviation,
    carNumber = driver.carNumber,
    countryFlag = driver.countryFlag,
    nationality = driver.nationality,
    teamName = team.name,
    teamColor = team.primaryColor,
    position = position,
    points = points
)
