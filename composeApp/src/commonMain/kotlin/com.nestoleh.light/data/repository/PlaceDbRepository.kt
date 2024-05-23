package com.nestoleh.light.data.repository

import com.nestoleh.light.data.converters.toPlace
import com.nestoleh.light.data.converters.toPlaceEntity
import com.nestoleh.light.data.converters.toPlaces
import com.nestoleh.light.data.database.dao.PlaceDao
import com.nestoleh.light.domain.model.Place
import com.nestoleh.light.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaceDbRepository(
    private val dao: PlaceDao
) : PlaceRepository {
    override suspend fun addPlace(place: Place): Place {
        val id = dao.insertPlace(place.toPlaceEntity())
        return place.copy(id = id.toInt())
    }

    override suspend fun updatePlace(place: Place): Place {
        dao.updatePlace(place.toPlaceEntity())
        return place
    }

    override fun getAllPlacesAsFlow(): Flow<List<Place>> {
        return dao.getAllPlacesAsFlow()
            .map { it.toPlaces() }
    }

    override suspend fun getAllPlaces(): List<Place> {
        return dao.getAllPlaces().toPlaces()
    }

    override suspend fun getAllPlacesWithName(name: String): List<Place> {
        return dao.getAllPlacesWithName(name).toPlaces()
    }

    override fun getPlaceAsFlow(placeId: Int): Flow<Place?> {
        return dao.getPlaceAsFlow(placeId).map { it?.toPlace() }
    }

    override suspend fun getPlace(placeId: Int): Place? {
        return dao.getPlace(placeId)?.toPlace()
    }

    override suspend fun deletePlace(placeId: Int) {
        return dao.delete(placeId)
    }
}