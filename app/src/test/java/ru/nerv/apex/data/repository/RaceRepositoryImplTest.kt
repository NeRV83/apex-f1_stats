package ru.nerv.apex.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.nerv.apex.data.local.dao.RaceDao
import ru.nerv.apex.data.local.entity.RaceEntity
import ru.nerv.apex.data.local.entity.ResultEntity
import ru.nerv.apex.data.remote.JolpicaApi
import ru.nerv.apex.data.remote.dto.Circuit
import ru.nerv.apex.data.remote.dto.DetailMetadata
import ru.nerv.apex.data.remote.dto.Driver
import ru.nerv.apex.data.remote.dto.ResultItem
import ru.nerv.apex.data.remote.dto.Results
import ru.nerv.apex.data.remote.dto.ResultsResponse
import ru.nerv.apex.data.remote.dto.Round
import ru.nerv.apex.data.remote.dto.Schedule
import ru.nerv.apex.data.remote.dto.ScheduleEvent
import ru.nerv.apex.data.remote.dto.ScheduleResponse
import ru.nerv.apex.data.remote.dto.Season
import ru.nerv.apex.data.remote.dto.Team

class RaceRepositoryImplTest {

    private class TestApi(
        private val scheduleResponse: ScheduleResponse? = null,
        private val resultsResponse: ResultsResponse? = null,
        private val throwScheduleError: Boolean = false,
        private val throwResultsError: Boolean = false
    ) : JolpicaApi {
        override suspend fun getSchedule(year: Int): ScheduleResponse {
            if (throwScheduleError) throw RuntimeException("Network error")
            return scheduleResponse!!
        }

        override suspend fun getResults(roundId: String, sessionFilter: String): ResultsResponse {
            if (throwResultsError) throw RuntimeException("Network error")
            return resultsResponse!!
        }
    }

    private class TestDao : RaceDao {
        val insertedRaces = mutableListOf<RaceEntity>()
        val insertedResults = mutableListOf<ResultEntity>()

        override fun observeRaces(year: Int): Flow<List<RaceEntity>> {
            return flowOf(insertedRaces.filter { it.seasonYear == year })
        }

        override suspend fun insertRaces(races: List<RaceEntity>) {
            insertedRaces.addAll(races)
        }

        override fun observeResults(roundId: String): Flow<List<ResultEntity>> {
            return flowOf(insertedResults.filter { it.roundId == roundId })
        }

        override suspend fun insertResults(results: List<ResultEntity>) {
            insertedResults.addAll(results)
        }
    }

    @Test
    fun `refreshRaces writes mapped entities to dao`() = runTest {
        val dao = TestDao()
        val api = TestApi(scheduleResponse = sampleScheduleResponse())
        val repository = RaceRepositoryImpl(api = api, dao = dao)

        repository.refreshRaces(2026)

        assertEquals(1, dao.insertedRaces.size)
        val entity = dao.insertedRaces.first()
        assertEquals("round_1", entity.roundId)
        assertEquals(2026, entity.seasonYear)
        assertEquals("Australian Grand Prix", entity.raceName)
        assertEquals("Albert Park", entity.circuitName)
    }

    @Test
    fun `refreshRaces fails but dao retains cached data`() = runTest {
        val dao = TestDao()
        val cachedEntity = RaceEntity(
            roundId = "cached_round", seasonYear = 2026, roundNumber = 1,
            raceName = "Cached GP", circuitName = "Cached Circuit",
            country = "Cached Country", countryCode = "CCC",
            raceTimestamp = null, raceLocalTimestamp = null
        )
        dao.insertedRaces.add(cachedEntity)

        val api = TestApi(throwScheduleError = true)
        val repository = RaceRepositoryImpl(api = api, dao = dao)

        var errorCaught = false
        try {
            repository.refreshRaces(2026)
        } catch (e: RuntimeException) {
            errorCaught = true
        }

        assertTrue("Ожидалось исключение при ошибке сети", errorCaught)
        val cachedRaces = repository.observeRaces(2026).first()
        assertEquals(1, cachedRaces.size)
        assertEquals("Cached GP", cachedRaces.first().raceName)
    }

    @Test
    fun `refreshResults writes mapped results to dao`() = runTest {
        val dao = TestDao()
        val api = TestApi(resultsResponse = sampleResultsResponse())
        val repository = RaceRepositoryImpl(api = api, dao = dao)

        repository.refreshResults("round_1")

        assertEquals(1, dao.insertedResults.size)
        val result = dao.insertedResults.first()
        assertEquals("round_1", result.roundId)
        assertEquals(1, result.position)
        assertEquals("Max Verstappen", result.driverName)
        assertEquals("Red Bull Racing", result.teamName)
        assertEquals(25.0, result.points)
    }

    @Test
    fun `refreshResults fails but dao retains cached results`() = runTest {
        val dao = TestDao()
        val cachedResult = ResultEntity(
            roundId = "round_1", position = 1, driverName = "Cached Driver",
            teamName = "Cached Team", time = "1:30:00", status = "Finished", points = 25.0
        )
        dao.insertedResults.add(cachedResult)

        val api = TestApi(throwResultsError = true)
        val repository = RaceRepositoryImpl(api = api, dao = dao)

        var errorCaught = false
        try {
            repository.refreshResults("round_1")
        } catch (e: RuntimeException) {
            errorCaught = true
        }

        assertTrue("Ожидалось исключение при ошибке сети", errorCaught)
        val cachedResults = repository.observeResults("round_1").first()
        assertEquals(1, cachedResults.size)
        assertEquals("Cached Driver", cachedResults.first().driverName)
    }

    private fun sampleScheduleResponse(): ScheduleResponse {
        return ScheduleResponse(
            metadata = DetailMetadata(timestamp = "2026-01-01T00:00:00Z"),
            data = Schedule(
                id = "season_2026", url = "", year = 2026, wikipedia = null, roundsInfo = null,
                events = listOf(
                    ScheduleEvent(
                        round = Round(id = "round_1", url = "", number = 1, name = "Australian Grand Prix", isCancelled = false, raceNumber = 1, wikipedia = null),
                        circuit = Circuit(id = "circuit_1", url = "", name = "Albert Park", locality = "Melbourne", countryCode = "AUS", country = "Australia", countryFlag = null, latitude = null, longitude = null, altitude = null, wikipedia = null),
                        schedule = emptyList()
                    )
                )
            )
        )
    }

    private fun sampleResultsResponse(): ResultsResponse {
        return ResultsResponse(
            metadata = DetailMetadata(timestamp = "2026-01-01T00:00:00Z"),
            data = Results(
                code = "R", title = "Race",
                season = Season(id = "s1", year = 2026),
                round = Round(id = "round_1", url = "", number = 1, name = "Australian Grand Prix", isCancelled = false, raceNumber = 1, wikipedia = null),
                circuit = Circuit(id = "c1", url = "", name = "Albert Park", locality = "Melbourne", countryCode = "AUS", country = "Australia", countryFlag = null, latitude = null, longitude = null, altitude = null, wikipedia = null),
                results = listOf(
                    ResultItem(
                        driver = Driver(givenName = "Max", familyName = "Verstappen", abbreviation = "VER"),
                        team = Team(name = "Red Bull Racing", primaryColor = "#0000FF"),
                        position = 1, positionText = "1", time = "1:30:00", status = "Finished", points = 25.0, laps = 58
                    )
                )
            )
        )
    }
}