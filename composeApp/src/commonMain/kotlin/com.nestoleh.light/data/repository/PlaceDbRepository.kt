package com.nestoleh.light.data.repository

import com.nestoleh.light.data.converters.toPlace
import com.nestoleh.light.data.converters.toPlaceEntity
import com.nestoleh.light.data.converters.toPlaces
import com.nestoleh.light.data.converters.toScheduleSlotEntities
import com.nestoleh.light.data.database.dao.PlaceDao
import com.nestoleh.light.data.database.entity.PlaceWithScheduleEntity
import com.nestoleh.light.domain.model.Place
import com.nestoleh.light.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaceDbRepository(
    private val placeDao: PlaceDao,
) : PlaceRepository {

    override suspend fun addPlace(place: Place): Place {
        val id = placeDao.insertPlace(place.toPlaceEntity()).toInt()
        placeDao.upsertScheduleList(place.schedule.toScheduleSlotEntities(id))
        return place.copy(id = id)
    }

    override suspend fun updatePlace(place: Place): Place {
        placeDao.upsertPlaceWithSchedule(
            PlaceWithScheduleEntity(
                place = place.toPlaceEntity(),
                schedule = place.schedule.toScheduleSlotEntities(place.id)
            )
        )
        return placeDao.getPlace(place.id)?.toPlace() ?: place
    }

    override fun getAllPlacesAsFlow(): Flow<List<Place>> {
        return placeDao.getAllPlacesAsFlow()
            .map { it.toPlaces() }
    }

    override suspend fun getAllPlaces(): List<Place> {
        return placeDao.getAllPlaces().toPlaces()
    }

    override suspend fun getAllPlacesWithName(name: String): List<Place> {
        return placeDao.getAllPlacesWithName(name).toPlaces()
    }

    override fun getPlaceAsFlow(placeId: Int): Flow<Place?> {
        return placeDao.getPlaceAsFlow(placeId).map { it?.toPlace() }
    }

    override suspend fun getPlace(placeId: Int): Place? {
        return placeDao.getPlace(placeId)?.toPlace()
    }

    override suspend fun deletePlace(placeId: Int) {
        return placeDao.delete(placeId)
    }
}