package ru.nerv.apex.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey val id: String,
    val name: String,
    val primaryColor: String?,
    val nationality: String?,
    val countryFlag: String?
)