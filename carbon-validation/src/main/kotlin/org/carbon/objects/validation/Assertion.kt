package org.carbon.objects.validation

import org.carbon.objects.validation.OtherwiseClause.Companion.delegateOtherwise
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.IndexModifier
import org.carbon.objects.validation.evaluation.KeyModifier
import org.carbon.objects.validation.evaluation.NameModifier
import org.carbon.objects.validation.evaluation.rejection.RootRejection

typealias Expression = () -> Evaluation
typealias TypedExpression<T> = (T) -> Evaluation
typealias BeCounterExpression<T> = T.() -> Evaluation
typealias Definition<T> = Assertion.(T) -> Unit

interface Validated<T : Validated<T>> {
    val def: Definition<T>
}

class Assertion {
    val rejection: RootRejection = RootRejection()

    fun isValid(): Boolean = rejection.isValid()

    infix fun String.should(expression: TypedExpression<String>) = delegateOtherwise(rejection) { expression(this) }

    infix fun Int.should(expression: TypedExpression<Int>) = delegateOtherwise(rejection) { expression(this) }

    infix fun <T> T.should(expression: TypedExpression<T>) = delegateOtherwise(rejection) { expression(this) }

    fun <T : Validated<T>> T.shouldValidated() = delegateOtherwise(rejection) { this.validate() }

    infix fun <T> Iterable<T>.shouldEach(expression: TypedExpression<T>) = delegateOtherwise(rejection, this.map { { expression(it) } })

    fun <T : Validated<T>> Iterable<T>.shouldEachValidated() = delegateOtherwise(rejection, this.map { { it.validate() } })
}

// ===================================================================================
//                                                                     OtherwiseClause
//                                                                          ==========
sealed class OtherwiseClause(
        open val rejection: RootRejection
) {
    infix fun otherwise(keyModifier: KeyModifier) = evaluate(keyModifier)

    abstract fun evaluate(keyModifier: KeyModifier)

    companion object {
        fun delegateOtherwise(rejection: RootRejection, expression: Expression): OtherwiseClause =
                UnitOtherwiseClause(rejection, expression)

        fun delegateOtherwise(rejection: RootRejection, expressions: List<Expression>): OtherwiseClause =
                IndexedOtherwiseClause(rejection, expressions)
    }

    open class UnitOtherwiseClause(
            override val rejection: RootRejection,
            private val expression: Expression
    ) : OtherwiseClause(rejection) {
        override fun evaluate(keyModifier: KeyModifier) {
            val evaluation = expression()
            if (evaluation is Evaluation.Rejection<*>)
                rejection.addRejection(evaluation modify keyModifier)
        }
    }

    class IndexedOtherwiseClause(
            override val rejection: RootRejection,
            private val expressions: List<Expression>
    ) : OtherwiseClause(rejection) {
        override fun evaluate(keyModifier: KeyModifier) {
            expressions
                    .asSequence()
                    .mapIndexed { i, expr ->
                        (expr() as? Evaluation.Rejection<*>)
                                ?.let {
                                    it modify keyModifier modify IndexModifier(i)
                                }
                    }
                    .filterNotNull()
                    .forEach { rejection.addRejection(it) }
        }
    }
}

// ===================================================================================
//                                                                        Modifier API
//                                                                          ==========
fun String.invalidate() = NameModifier(this)