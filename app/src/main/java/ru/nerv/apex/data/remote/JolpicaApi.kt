package ru.nerv.apex.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import ru.nerv.apex.data.remote.dto.ScheduleResponse

interface JolpicaApi {

    /** Полный календарь сезона с трассами и сессиями */
    @GET("f1/alpha/schedules/{year}/")
    suspend fun getSchedule(@Path("year") year: Int): ScheduleResponse
}