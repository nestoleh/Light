package com.nestoleh.light.di

import com.nestoleh.light.data.repository.ParametersDbRepository
import com.nestoleh.light.data.repository.PlaceDbRepository
import com.nestoleh.light.domain.repository.ParametersRepository
import com.nestoleh.light.domain.repository.PlaceRepository
import com.nestoleh.light.domain.usecase.CalculateNearestElectricityPeriodsUseCase
import com.nestoleh.light.domain.usecase.CalculateScheduleAsBlocksUseCase
import com.nestoleh.light.domain.usecase.CreatePlaceUseCase
import com.nestoleh.light.domain.usecase.DeletePlaceUseCase
import com.nestoleh.light.domain.usecase.GetAllPlacesUseCase
import com.nestoleh.light.domain.usecase.GetPlaceUseCase
import com.nestoleh.light.domain.usecase.GetSelectedPlaceUseCase
import com.nestoleh.light.domain.usecase.IsPlaceWithNameExistUseCase
import com.nestoleh.light.domain.usecase.SelectPlaceUseCase
import com.nestoleh.light.domain.usecase.UpdatePlaceUseCase
import com.nestoleh.light.domain.validator.PlaceNameValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    single<ParametersRepository> {
        ParametersDbRepository(
            dao = get()
        )
    }
    single<PlaceRepository> {
        PlaceDbRepository(
            placeDao = get(),
        )
    }
    single {
        CreatePlaceUseCase(
            placeRepository = get(),
            dispatcher = get(DispatcherQualifier.IO.qualifier)
        )
    }
    singleOf(::GetAllPlacesUseCase)
    single {
        IsPlaceWithNameExistUseCase(
            placeRepository = get(),
            dispatcher = get(DispatcherQualifier.IO.qualifier)
        )
    }
    singleOf(::PlaceNameValidator)
    single {
        GetSelectedPlaceUseCase(
            placeRepository = get(),
            parametersRepository = get(),
            dispatcher = get(DispatcherQualifier.IO.qualifier)
        )
    }
    single {
        SelectPlaceUseCase(
            placeRepository = get(),
            parametersRepository = get(),
            dispatcher = get(DispatcherQualifier.IO.qualifier)
        )
    }
    singleOf(::GetPlaceUseCase)
    single {
        DeletePlaceUseCase(
            placeRepository = get(),
            parametersRepository = get(),
            dispatcher = get(DispatcherQualifier.IO.qualifier)
        )
    }
    single {
        UpdatePlaceUseCase(
            placeRepository = get(),
            dispatcher = get(DispatcherQualifier.IO.qualifier)
        )
    }
    single {
        CalculateScheduleAsBlocksUseCase(
            dispatcher = get(DispatcherQualifier.Default.qualifier)
        )
    }
    single {
        CalculateNearestElectricityPeriodsUseCase(
            dispatcher = get(DispatcherQualifier.Default.qualifier)
        )
    }
}