package org.carbon.objects.validation

import org.carbon.objects.validation.evaluation.Evaluation

/**
 * @author Soda 2018/10/13.
 */
object Validator {
    fun <T : Validated<T>> validate(test: T): Evaluation {
        val assert = Assertion()
        test.def(assert, test)
        return when {
            assert.isValid() -> Evaluation.Acceptance
            else -> assert.rejection
        }
    }
}

fun <T : Validated<T>> T.validate() = Validator.validate(this)