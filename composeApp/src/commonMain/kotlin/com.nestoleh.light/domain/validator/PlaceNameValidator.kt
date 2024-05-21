package com.nestoleh.light.domain.validator

import com.nestoleh.light.domain.usecase.IsPlaceWithNameExistUseCase
import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.validator_place_name_empty_error
import light.composeapp.generated.resources.validator_place_name_exist_error
import org.jetbrains.compose.resources.getString

class PlaceNameValidator(
    private val isPlaceWithNameExistUseCase: IsPlaceWithNameExistUseCase
) : Validator<String?>() {
    override suspend fun validate(value: String?): ValidationResult {
        return if (value.isNullOrEmpty()) {
            ValidationResult.Error(getString(Res.string.validator_place_name_empty_error))
        } else if (isPlaceWithNameExistUseCase.executeSync(IsPlaceWithNameExistUseCase.Parameters(value))) {
            ValidationResult.Error(getString(Res.string.validator_place_name_exist_error))
        } else {
            ValidationResult.Valid
        }
    }
}