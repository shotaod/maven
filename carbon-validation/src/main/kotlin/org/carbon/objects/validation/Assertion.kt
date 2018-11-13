package org.carbon.objects.validation

import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.IndexModifier
import org.carbon.objects.validation.evaluation.KeyAppender
import org.carbon.objects.validation.evaluation.NameAppender
import org.carbon.objects.validation.evaluation.rejection.MutableReferenceRejection

typealias Expression = () -> Evaluation
typealias TypedExpression<T> = (T) -> Evaluation
typealias BeCounterExpression<T> = T.() -> Evaluation
typealias Definition<T> = Assertion.(T) -> Unit

interface Validated<T : Validated<T>> {
    val def: Definition<T>
}

class Assertion {
    private val rejectionRef: MutableReferenceRejection = MutableReferenceRejection()

    val evaluation
        get() = when {
            rejectionRef.isEmpty() -> Evaluation.Accepted
            else -> Evaluation.Rejected(rejectionRef)
        }

    // -----------------------------------------------------
    //                                               DSL
    //                                               -------
    infix fun <T> T.should(expression: TypedExpression<T>) = OtherwiseClause.byUnit(rejectionRef) { expression(this) }

    fun <T : Validated<T>> T.shouldValidated() = OtherwiseClause.byUnit(rejectionRef, this::validate)

    infix fun <T> Iterable<T>.shouldEach(expression: TypedExpression<T>) = OtherwiseClause.byIndexed(
            rejectionRef,
            this.map { { expression(it) } })

    fun <T : Validated<T>> Iterable<T>.shouldEachValidated() = OtherwiseClause.byIndexed(
            rejectionRef,
            this.map { it::validate })
}

// ===================================================================================
//                                                                     OtherwiseClause
//                                                                          ==========
sealed class OtherwiseClause {
    protected abstract val rejection: MutableReferenceRejection
    infix fun otherwise(keyAppender: KeyAppender) = evaluate(keyAppender)

    abstract fun evaluate(keyAppender: KeyAppender)

    companion object {
        fun byUnit(rejection: MutableReferenceRejection, expression: Expression): OtherwiseClause =
                UnitOtherwiseClause(rejection, expression)

        fun byIndexed(rejection: MutableReferenceRejection, expressions: List<Expression>): OtherwiseClause =
                IndexedOtherwiseClause(rejection, expressions)
    }

    class UnitOtherwiseClause(
            override val rejection: MutableReferenceRejection,
            private val expression: Expression
    ) : OtherwiseClause() {
        override fun evaluate(keyAppender: KeyAppender) {
            val evaluation = expression()
            if (evaluation is Evaluation.Rejected)
                rejection.add(evaluation positionedBy keyAppender)
        }
    }

    class IndexedOtherwiseClause(
            override val rejection: MutableReferenceRejection,
            private val expressions: List<Expression>
    ) : OtherwiseClause() {
        override fun evaluate(keyAppender: KeyAppender) {
            expressions
                    .asSequence()
                    .mapIndexed { i, expr ->
                        (expr() as? Evaluation.Rejected)
                                ?.let {
                                    it positionedBy keyAppender positionedBy IndexModifier(i)
                                }
                    }
                    .filterNotNull()
                    .forEach { rejection.add(it) }
        }
    }
}

// ===================================================================================
//                                                                        Modifier API
//                                                                          ==========
fun String.invalidate() = NameAppender(this)