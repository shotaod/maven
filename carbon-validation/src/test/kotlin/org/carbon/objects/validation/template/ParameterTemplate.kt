package org.carbon.objects.validation.template

import org.carbon.objects.validation.describe
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.input.Input

typealias Case = (describe: String) -> (input: Input, expected: Expected<*>) -> Array<Any>
typealias Assertion = (Evaluation) -> Unit

abstract class ParameterTemplate {
    abstract fun data(): List<Array<Any>>

    protected val case: Case = { describe -> { input, expected -> arrayOf(describe, input, expected) } }
}

abstract class Expected<E : Expected<E>> {
    private var _assertions: List<Assertion> = emptyList()

    open fun assert(describe: String, evalExp: () -> Evaluation) {
        val evaluation = evalExp()
        println("""
                --------------------------------------------------
                $describe
                --------------------------------------------------

                """.trimIndent() + evaluation.describe())
        _assertions.forEach { it(evaluation) }
    }

    protected fun withAssertion(assertion: Assertion): E {
        _assertions += assertion
        return self
    }

    protected abstract val self: E
}