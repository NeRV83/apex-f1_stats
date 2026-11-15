package ru.nerv.apex.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import ru.nerv.apex.data.remote.dto.ResultsResponse
import ru.nerv.apex.data.remote.dto.ScheduleResponse
import ru.nerv.apex.data.remote.dto.SessionResponse
import ru.nerv.apex.data.remote.dto.TeamsResponse

interface JolpicaApi {

    /** Полный календарь сезона с трассами и сессиями */
    @GET("f1/alpha/schedules/{year}/")
    suspend fun getSchedule(@Path("year") year: Int): ScheduleResponse

    /** Результаты заездов */
    @GET("f1/alpha/results/{round_id}/{session_filter}/")
    suspend fun getResults(
        @Path("round_id") roundId: String,
        @Path("session_filter") sessionFilter: String = "R"
    ): ResultsResponse

    /** Получение ИД гоночной сессии */
    @GET("f1/alpha/core/sessions/{session_id}/")
    suspend fun getSession(@Path("session_id") sessionId: String): SessionResponse

    /** Команды */
    @GET("f1/alpha/core/teams/")
    suspend fun getTeams(): TeamsResponse

}