package ru.nerv.apex.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DriverStandingsResponse(
    @SerializedName("metadata") val metadata: DetailMetadata,
    @SerializedName("data") val data: List<DriverStandingData>
)

data class DriverStandingData(
    @SerializedName("position") val position: Int?,
    @SerializedName("points") val points: Double?,
    @SerializedName("wins") val wins: Int?,
    @SerializedName("driver") val driver: DriverData,
    @SerializedName("team") val team: TeamData
)