package com.nestoleh.light.domain.validator

abstract class Validator<in T> {

    abstract suspend fun validate(value: T): ValidationResult

    suspend fun isValid(value: T): Boolean = validate(value).isValid
}

sealed class ValidationResult(
    val isValid: Boolean
) {

    data object Valid : ValidationResult(true)

    data class Error(
        val message: String
    ) : ValidationResult(false)
}

open class ValidatorsComposer<T>(validators: List<Validator<T>>? = null) : Validator<T>() {
    private val allValidators: MutableList<Validator<T>> = validators?.toMutableList() ?: ArrayList()

    fun addValidator(validator: Validator<T>) {
        allValidators.add(validator)
    }

    override suspend fun validate(value: T): ValidationResult {
        if (allValidators.isNotEmpty()) {
            for (validator in allValidators) {
                val validationResult = validator.validate(value)
                if (validationResult is ValidationResult.Error) {
                    return validationResult
                }
            }
        }
        return ValidationResult.Valid
    }
}

fun <T> validatorOf(vararg validators: Validator<T>): Validator<T> {
    return ValidatorsComposer(validators.toList())
}