package org.carbon.objects.validation.matcher

import org.carbon.objects.validation.Logical
import org.carbon.objects.validation.ShapeExpression
import org.carbon.objects.validation.evaluation.CompositeRejection
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.Key

/**
 * @author Soda 2018/10/07.
 */
infix fun <T> T.be(expr: ShapeExpression<T>): Evaluation = expr(this)

// -----------------------------------------------------
//                                               Logical
//                                               -------
fun or(vararg matches: Evaluation): Evaluation =
        if (Evaluation.Acceptance in matches) Evaluation.Acceptance
        else merge(matches, Logical.OR)

fun and(vararg matches: Evaluation): Evaluation =
        if (matches.all { it === Evaluation.Acceptance }) Evaluation.Acceptance
        else merge(matches, Logical.AND)

private fun merge(matches: Array<out Evaluation>, logical: Logical): Evaluation.Rejection<Any> {
    @Suppress("UNCHECKED_CAST")
    val rejects = matches
            .filter { it !== Evaluation.Acceptance }
            as List<Evaluation.Rejection<Any>>
    return CompositeRejection(
            Key.Unresolved,
            rejects.first().original,
            logical,
            rejects
    )
}
