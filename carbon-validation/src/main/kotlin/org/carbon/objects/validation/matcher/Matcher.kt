package org.carbon.objects.validation.matcher

import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.Logical
import org.carbon.objects.validation.evaluation.source.CompositeViolationSource

/**
 * @author Soda 2018/10/07.
 */
typealias ShapeExpression<T> = T.() -> Evaluation

interface Matcher<T> {

    infix fun T.be(shape: ShapeExpression<T>): Evaluation = shape(this)
    infix fun T.eq(other: T): Evaluation

    // -----------------------------------------------------
    //                                               Logical
    //                                               -------
    fun or(vararg matches: Evaluation): Evaluation =
            if (Evaluation.Accept in matches) Evaluation.Accept
            else mergeRejects(matches, Logical.OR)

    fun and(vararg matches: Evaluation): Evaluation =
            if (matches.all { it === Evaluation.Accept }) Evaluation.Accept
            else mergeRejects(matches, Logical.AND)

    private fun mergeRejects(matches: Array<out Evaluation>, logical: Logical) =
            matches.filter { it !== Evaluation.Accept }
                    .map {
                        @Suppress("UNCHECKED_CAST")
                        it as Evaluation.Reject<Any>
                    }
                    .let { rejects ->
                        val first = rejects.first()
                        val groups = rejects.map { it.source }
                        val compositeViolationGroup = CompositeViolationSource(first.type, groups, logical)
                        Evaluation.Reject(first.type, compositeViolationGroup, "composite violation", first.original)
                    }
}
