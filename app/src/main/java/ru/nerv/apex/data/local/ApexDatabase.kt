package ru.nerv.apex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.nerv.apex.data.local.dao.RaceDao
import ru.nerv.apex.data.local.entity.RaceEntity

@Database(
    entities = [RaceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ApexDatabase : RoomDatabase() {
    abstract fun raceDao(): RaceDao
}