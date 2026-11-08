package ru.nerv.apex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.nerv.apex.data.local.dao.RaceDao
import ru.nerv.apex.data.local.entity.RaceEntity
import ru.nerv.apex.data.local.entity.ResultEntity

@Database(
    entities = [RaceEntity::class, ResultEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ApexDatabase : RoomDatabase() {
    abstract fun raceDao(): RaceDao
}