package ru.nerv.apex.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TeamsResponse(
    @SerializedName("metadata") val metadata: DetailMetadata,
    @SerializedName("data") val data: List<TeamData>
)

data class TeamData(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("primary_color") val primaryColor: String?,
    @SerializedName("nationality") val nationality: String?,
    @SerializedName("country_code") val countryCode: String?,
    @SerializedName("country_flag") val countryFlag: String?,
    @SerializedName("wikipedia") val wikipedia: String?
)