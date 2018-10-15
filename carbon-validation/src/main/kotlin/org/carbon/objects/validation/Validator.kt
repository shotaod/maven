package org.carbon.objects.validation

/**
 * @author Soda 2018/10/13.
 */
object Validator {
    fun <T : Validated<T>> validate(target: T): ValidationResult {
        val assert = Assertion()
        target.def(assert, target)
        return when {
            assert.isValid() -> ObservanceResult(target)
            else -> ViolationResult(assert.violations)
        }
    }
}

fun <T : Validated<T>> T.validate() = Validator.validate(this)