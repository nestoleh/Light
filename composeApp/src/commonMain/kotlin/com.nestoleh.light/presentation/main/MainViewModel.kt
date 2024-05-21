package com.nestoleh.light.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestoleh.light.data.database.dao.PlaceDao
import com.nestoleh.light.domain.model.Place
import com.nestoleh.light.domain.usecase.GetAllPlacesUseCase
import com.nestoleh.light.domain.usecase.invoke
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    private val getAllPlacesUseCase: GetAllPlacesUseCase
) : ViewModel() {

    val placesState: StateFlow<List<Place>> = getAllPlacesUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

}