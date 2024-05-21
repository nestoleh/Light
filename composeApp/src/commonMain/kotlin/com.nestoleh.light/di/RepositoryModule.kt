package com.nestoleh.light.di

import com.nestoleh.light.domain.usecase.CreatePlaceUseCase
import com.nestoleh.light.domain.usecase.GetAllPlacesUseCase
import com.nestoleh.light.domain.usecase.IsPlaceWithNameExistUseCase
import com.nestoleh.light.domain.validator.PlaceNameValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    single {
        CreatePlaceUseCase(
            placeDao = get(),
            dispatcher = get(DispatcherQualifier.IO.qualifier)
        )
    }
    singleOf(::GetAllPlacesUseCase)
    single {
        IsPlaceWithNameExistUseCase(
            placeDao = get(),
            dispatcher = get(DispatcherQualifier.IO.qualifier)
        )
    }
    singleOf(::PlaceNameValidator)
}