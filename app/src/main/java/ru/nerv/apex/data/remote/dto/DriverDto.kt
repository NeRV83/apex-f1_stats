package ru.nerv.apex.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DriversResponse(
    @SerializedName("metadata") val metadata: DetailMetadata,
    @SerializedName("data") val data: List<DriverData>
)

data class DriverData(
    @SerializedName("id") val id: String,
    @SerializedName("abbreviation") val abbreviation: String?,
    @SerializedName("given_name") val givenName: String,
    @SerializedName("family_name") val familyName: String,
    @SerializedName("nationality") val nationality: String?,
    @SerializedName("country_code") val countryCode: String?,
    @SerializedName("country_flag") val countryFlag: String?,
    @SerializedName("permanent_car_number") val carNumber: Int?,
    @SerializedName("date_of_birth") val dateOfBirth: String?,
    @SerializedName("wikipedia") val wikipedia: String?
)