package ru.nerv.apex.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "races")
data class RaceEntity(
    @PrimaryKey val roundId: String,
    val seasonYear: Int,
    val roundNumber: Int?,
    val raceName: String?,
    val circuitName: String,
    val country: String?,
    val countryCode: String?,
    val raceTimestamp: String?,
    val raceLocalTimestamp: String?
)