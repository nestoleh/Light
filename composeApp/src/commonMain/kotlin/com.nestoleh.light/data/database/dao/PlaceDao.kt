package com.nestoleh.light.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nestoleh.light.data.database.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Query("SELECT * FROM Place")
    fun getAllPlacesFlow(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM Place WHERE id = :placeId LIMIT 1")
    fun getPlaceFlow(placeId: Int): Flow<PlaceEntity?>

    @Query("SELECT * FROM Place WHERE id = :placeId LIMIT 1")
    suspend fun getPlace(placeId: Int): PlaceEntity

    @Query("SELECT * FROM Place WHERE name = :name")
    suspend fun getAllPlacesWithName(name: String): List<PlaceEntity>

    suspend fun insert(place: PlaceEntity): PlaceEntity {
        val id = insertPlace(place)
        return place.copy(id = id.toInt())
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity): Long

    @Query("DELETE FROM Place WHERE id = :placeId")
    suspend fun delete(placeId: Int)
}