package ru.nerv.apex.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "results",
    foreignKeys = [
        ForeignKey(
            entity = RaceEntity::class,
            parentColumns = ["roundId"],
            childColumns = ["roundId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("roundId")]
)
data class ResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roundId: String,
    val position: Int?,
    val driverName: String,
    val teamName: String,
    val time: String?,
    val status: String?,
    val points: Double?
)