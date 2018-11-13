package org.carbon.objects.validation.matcher

import org.carbon.objects.validation.BeCounterExpression
import org.carbon.objects.validation.Logical
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.rejection.CompositeRejection

/**
 * @author Soda 2018/10/07.
 */
infix fun <T> T.be(expr: BeCounterExpression<T>): Evaluation = expr(this)

infix fun <T> T?.mayBe(expr: BeCounterExpression<T>): Evaluation = this?.let { expr(it) } ?: Evaluation.Accepted

// -----------------------------------------------------
//                                               Logical
//                                               -------
fun or(vararg matches: Evaluation): Evaluation =
        if (Evaluation.Accepted in matches) Evaluation.Accepted
        else merge(matches, Logical.OR)

fun and(vararg matches: Evaluation): Evaluation =
        if (matches.all { it === Evaluation.Accepted }) Evaluation.Accepted
        else merge(matches, Logical.AND)

private fun merge(matches: Array<out Evaluation>, logical: Logical): Evaluation.Rejected {
    @Suppress("UNCHECKED_CAST")
    val rejects = matches
            .filter { it !== Evaluation.Accepted }
            as List<Evaluation.Rejected>
    return Evaluation.Rejected.from(
            CompositeRejection(
                    rejects.first().original,
                    logical,
                    rejects
            ))
}
