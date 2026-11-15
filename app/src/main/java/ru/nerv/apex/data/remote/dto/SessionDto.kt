package ru.nerv.apex.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SessionResponse(
    @SerializedName("metadata") val metadata: DetailMetadata,
    @SerializedName("data") val data: SessionData
)

data class SessionData(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("scheduled_laps") val scheduledLaps: Int?
)