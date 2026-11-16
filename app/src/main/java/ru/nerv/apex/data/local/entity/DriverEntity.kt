package ru.nerv.apex.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey val id: String,
    val name: String,
    val abbreviation: String?,
    val carNumber: Int?,
    val countryFlag: String?,
    val nationality: String?,
    val teamName: String?,
    val teamColor: String?,
    val position: Int?,
    val points: Double?
)