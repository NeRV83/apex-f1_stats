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
import ru.nerv.apex.data.remote.JolpicaApi
import ru.nerv.apex.data.remote.dto.Circuit
import ru.nerv.apex.data.remote.dto.DetailMetadata
import ru.nerv.apex.data.remote.dto.Round
import ru.nerv.apex.data.remote.dto.Schedule
import ru.nerv.apex.data.remote.dto.ScheduleEvent
import ru.nerv.apex.data.remote.dto.ScheduleResponse

class RaceRepositoryImplTest {

    private class TestApi(
        private val response: ScheduleResponse? = null,
        private val throwError: Boolean = false
    ) : JolpicaApi {
        override suspend fun getSchedule(year: Int): ScheduleResponse {
            if (throwError) throw RuntimeException("Network error")
            return response!!
        }
    }

    private class TestDao : RaceDao {
        val insertedRaces = mutableListOf<RaceEntity>()

        override fun observeRaces(year: Int): Flow<List<RaceEntity>> {
            return flowOf(insertedRaces.filter { it.seasonYear == year })
        }

        override suspend fun insertRaces(races: List<RaceEntity>) {
            insertedRaces.addAll(races)
        }
    }

    @Test
    fun `refreshRaces writes mapped entities to dao`() = runTest {
        val dao = TestDao()
        val api = TestApi(response = sampleResponse())
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
            roundId = "cached_round",
            seasonYear = 2026,
            roundNumber = 1,
            raceName = "Cached GP",
            circuitName = "Cached Circuit",
            country = "Cached Country",
            countryCode = "CCC",
            raceTimestamp = null,
            raceLocalTimestamp = null
        )
        dao.insertedRaces.add(cachedEntity)

        val api = TestApi(throwError = true)
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
        assertEquals("cached_round", cachedRaces.first().roundId)
        assertEquals("Cached GP", cachedRaces.first().raceName)
    }

    private fun sampleResponse(): ScheduleResponse {
        return ScheduleResponse(
            metadata = DetailMetadata(timestamp = "2026-01-01T00:00:00Z"),
            data = Schedule(
                id = "season_2026",
                url = "",
                year = 2026,
                wikipedia = null,
                roundsInfo = null,
                events = listOf(
                    ScheduleEvent(
                        round = Round(
                            id = "round_1",
                            url = "",
                            number = 1,
                            name = "Australian Grand Prix",
                            isCancelled = false,
                            raceNumber = 1,
                            wikipedia = null
                        ),
                        circuit = Circuit(
                            id = "circuit_1",
                            url = "",
                            name = "Albert Park",
                            locality = "Melbourne",
                            countryCode = "AUS",
                            country = "Australia",
                            countryFlag = null,
                            latitude = null,
                            longitude = null,
                            altitude = null,
                            wikipedia = null
                        ),
                        schedule = emptyList()
                    )
                )
            )
        )
    }
}