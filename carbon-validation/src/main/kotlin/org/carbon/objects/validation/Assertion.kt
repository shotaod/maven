package org.carbon.objects.validation

import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.matcher.IntMatcher
import org.carbon.objects.validation.matcher.StringMatcher


class Assertion {
    val violations = ViolationList()

    fun isValid(): Boolean = violations.isEmpty()

    infix fun String.should(block: StringMatcher.(String) -> Evaluation) =
            call { block(StringMatcher, this) }

    infix fun Int.should(block: IntMatcher.(Int) -> Evaluation) =
            call { block(IntMatcher, this) }

    private fun call(assertion: () -> Evaluation): ViolationClause {
        val result = assertion()
        return when (result) {
            is Evaluation.Accept -> NoopViolationClause()
            is Evaluation.Reject<*> -> SpecifyViolationClause(result, violations)
        }
    }
}

// ===================================================================================
//                                                                     ViolationClause
//                                                                          ==========
sealed class ViolationClause {
    abstract infix fun otherwise(specify: Specify)
}

class NoopViolationClause : ViolationClause() {
    override fun otherwise(specify: Specify) = Unit
}

class SpecifyViolationClause<T : Any>(
        private val rejects: Evaluation.Reject<T>,
        private val list: ViolationList) : ViolationClause() {
    override fun otherwise(specify: Specify) {
        specify.toViolation(rejects).also { list.add(it) }
    }
}

// ===================================================================================
//                                                                          Specify
//                                                                          ==========
class Specify(
        private vararg val key: String,
        private val at: Int? = null,
        private val message: String? = null
) {
    fun <T : Any> toViolation(reject: Evaluation.Reject<T>): Violation<T> = Violation(
            if (message !== null) message else reject.message,
            ViolationKey(key.toList(), at),
            reject.type,
            reject.source
    )
}
