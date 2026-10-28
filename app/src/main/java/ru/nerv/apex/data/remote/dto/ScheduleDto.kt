package ru.nerv.apex.data.remote.dto

import com.google.gson.annotations.SerializedName

// metadata + data
data class DetailResponse<T>(
    @SerializedName("metadata") val metadata: DetailMetadata,
    @SerializedName("data") val data: T
)

data class DetailMetadata(
    @SerializedName("timestamp") val timestamp: String
)

// Schedule (календарь сезона)

data class ScheduleResponse(
    @SerializedName("metadata") val metadata: DetailMetadata,
    @SerializedName("data") val data: Schedule
)

data class Schedule(
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String,
    @SerializedName("year") val year: Int,
    @SerializedName("wikipedia") val wikipedia: String?,
    @SerializedName("rounds_info") val roundsInfo: RoundsInfo?,
    @SerializedName("events") val events: List<ScheduleEvent>
)

data class RoundsInfo(
    @SerializedName("next") val next: RoundInfoDetail?,
    @SerializedName("previous") val previous: RoundInfoDetail?
)

data class RoundInfoDetail(
    @SerializedName("number") val number: Int,
    @SerializedName("index") val index: Int
)

data class ScheduleEvent(
    @SerializedName("round") val round: Round,
    @SerializedName("circuit") val circuit: Circuit,
    @SerializedName("schedule") val schedule: List<ScheduleSession>
)

data class Round(
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String,
    @SerializedName("number") val number: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("is_cancelled") val isCancelled: Boolean,
    @SerializedName("race_number") val raceNumber: Int?,
    @SerializedName("wikipedia") val wikipedia: String?
)

data class Circuit(
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String,
    @SerializedName("name") val name: String,
    @SerializedName("locality") val locality: String?,
    @SerializedName("country_code") val countryCode: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("country_flag") val countryFlag: String?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("altitude") val altitude: Double?,
    @SerializedName("wikipedia") val wikipedia: String?
)

data class ScheduleSession(
    @SerializedName("code") val code: String,
    @SerializedName("title") val title: String,
    @SerializedName("sessions") val sessions: List<BasicSession>,
    @SerializedName("timestamp") val timestamp: String?,
    @SerializedName("missing_time_data") val missingTimeData: Boolean?,
    @SerializedName("local_timestamp") val localTimestamp: String?,
    @SerializedName("timezone") val timezone: String?,
    @SerializedName("results_url") val resultsUrl: String,
    @SerializedName("laps_url") val lapsUrl: String
)

data class BasicSession(
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String,
    @SerializedName("number") val number: Int?,
    @SerializedName("type") val type: String,
    @SerializedName("type_display") val typeDisplay: String,
    @SerializedName("is_cancelled") val isCancelled: Boolean = false
)