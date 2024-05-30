package com.nestoleh.light.domain.repository

import com.nestoleh.light.domain.model.Place
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    suspend fun addPlace(place: Place): Place
    suspend fun updatePlace(place: Place): Place

    fun getAllPlacesAsFlow(): Flow<List<Place>>
    suspend fun getAllPlaces(): List<Place>
    suspend fun getAllPlacesWithName(name: String): List<Place>

    fun getPlaceAsFlow(placeId: String): Flow<Place?>
    suspend fun getPlace(placeId: String): Place?

    suspend fun deletePlace(placeId: String)
}