package com.nestoleh.light.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.nestoleh.light.data.database.entity.PlaceEntity
import com.nestoleh.light.data.database.entity.PlaceWithScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity): Long

    @Update
    suspend fun updatePlace(place: PlaceEntity)

    @Transaction
    @Query("SELECT * FROM Place")
    fun getAllPlacesAsFlow(): Flow<List<PlaceWithScheduleEntity>>

    @Transaction
    @Query("SELECT * FROM Place")
    suspend fun getAllPlaces(): List<PlaceWithScheduleEntity>

    @Transaction
    @Query("SELECT * FROM Place WHERE name = :name")
    suspend fun getAllPlacesWithName(name: String): List<PlaceWithScheduleEntity>

    @Transaction
    @Query("SELECT * FROM Place WHERE id = :placeId LIMIT 1")
    fun getPlaceAsFlow(placeId: Int): Flow<PlaceWithScheduleEntity?>

    @Transaction
    @Query("SELECT * FROM Place WHERE id = :placeId LIMIT 1")
    suspend fun getPlace(placeId: Int): PlaceWithScheduleEntity?

    @Transaction
    @Query("DELETE FROM Place WHERE id = :placeId")
    suspend fun delete(placeId: Int)
}