package com.nestoleh.light.domain.validator

import light.composeapp.generated.resources.Res
import light.composeapp.generated.resources.validator_home_name_empty_error
import org.jetbrains.compose.resources.getString

class HomeNameValidator : Validator<String?>() {
    override suspend fun validate(value: String?): ValidationResult {
        return if (value.isNullOrEmpty()) {
            ValidationResult.Error(getString(Res.string.validator_home_name_empty_error))
        } else {
            ValidationResult.Valid
        }
    }
}