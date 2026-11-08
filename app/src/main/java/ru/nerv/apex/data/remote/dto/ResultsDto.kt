package ru.nerv.apex.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ResultsResponse(
    @SerializedName("metadata") val metadata: DetailMetadata,
    @SerializedName("data") val data: Results
)

data class Results(
    @SerializedName("code") val code: String,
    @SerializedName("title") val title: String,
    @SerializedName("season") val season: Season,
    @SerializedName("round") val round: Round,
    @SerializedName("circuit") val circuit: Circuit,
    @SerializedName("results") val results: List<ResultItem>
)

data class Season(
    @SerializedName("id") val id: String,
    @SerializedName("year") val year: Int
)

data class ResultItem(
    @SerializedName("driver") val driver: Driver,
    @SerializedName("team") val team: Team,
    @SerializedName("position") val position: Int?,
    @SerializedName("position_text") val positionText: String?,
    @SerializedName("time") val time: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("points") val points: Double?,
    @SerializedName("laps") val laps: Int?
)

data class Driver(
    @SerializedName("given_name") val givenName: String,
    @SerializedName("family_name") val familyName: String,
    @SerializedName("abbreviation") val abbreviation: String?
)

data class Team(
    @SerializedName("name") val name: String,
    @SerializedName("primary_color") val primaryColor: String?
)