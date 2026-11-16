package ru.nerv.apex.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ErgastStandingsResponse(
    @SerializedName("MRData") val mrData: MRData
)

data class MRData(
    @SerializedName("StandingsTable") val standingsTable: StandingsTable
)

data class StandingsTable(
    @SerializedName("StandingsLists") val standingsLists: List<StandingsList>
)

data class StandingsList(
    @SerializedName("DriverStandings") val driverStandings: List<DriverStanding>
)

data class DriverStanding(
    @SerializedName("position") val position: String?,
    @SerializedName("points") val points: String?,
    @SerializedName("wins") val wins: String?,
    @SerializedName("Driver") val driver: ErgastDriver,
    @SerializedName("Constructors") val constructors: List<ErgastConstructor>
)

data class ErgastDriver(
    @SerializedName("driverId") val driverId: String,
    @SerializedName("givenName") val givenName: String,
    @SerializedName("familyName") val familyName: String,
    @SerializedName("nationality") val nationality: String?,
    @SerializedName("permanentNumber") val permanentNumber: String?,
    @SerializedName("code") val code: String?
)

data class ErgastConstructor(
    @SerializedName("constructorId") val constructorId: String,
    @SerializedName("name") val name: String,
    @SerializedName("nationality") val nationality: String?
)