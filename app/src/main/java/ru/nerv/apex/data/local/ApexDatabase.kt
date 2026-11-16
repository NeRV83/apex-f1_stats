package ru.nerv.apex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.nerv.apex.data.local.dao.RaceDao
import ru.nerv.apex.data.local.entity.DriverEntity
import ru.nerv.apex.data.local.entity.RaceEntity
import ru.nerv.apex.data.local.entity.ResultEntity
import ru.nerv.apex.data.local.entity.TeamEntity

@Database(
    entities = [RaceEntity::class, ResultEntity::class, TeamEntity::class, DriverEntity::class],
    version = 4,
    exportSchema = false
)
abstract class ApexDatabase : RoomDatabase() {
    abstract fun raceDao(): RaceDao
}