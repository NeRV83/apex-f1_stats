package ru.nerv.apex.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.nerv.apex.data.local.entity.RaceEntity

@Dao
interface RaceDao {

    @Query("SELECT * FROM races WHERE seasonYear = :year ORDER BY roundNumber ASC")
    fun observeRaces(year: Int): Flow<List<RaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRaces(races: List<RaceEntity>)
}