package org.carbon.objects.validation.matcher

import org.carbon.objects.validation.BeCounterExpression
import org.carbon.objects.validation.Logical
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.source.BasicCode
import org.carbon.objects.validation.evaluation.source.LengthCode
import org.carbon.objects.validation.evaluation.source.NumberCode
import org.carbon.objects.validation.evaluation.source.Param

infix fun Int.eq(other: Int): Evaluation =
        if (this == other) Evaluation.Acceptance
        else this.reject(
                BasicCode.Equal,
                Param(listOf(this, other)),
                "values \"$this\" and \"$other\" are not match"
        )

infix fun Int.min(min: Int): Evaluation {
    return if (this >= min) Evaluation.Acceptance
    else this.reject(
            LengthCode.Min,
            Param(listOf(min)),
            "number must be greater than $min"
    )
}

infix fun Int.max(max: Int): Evaluation {
    return if (this <= max) Evaluation.Acceptance
    else this.reject(
            LengthCode.Max,
            Param(listOf(max)),
            "number must be less than $max"
    )
}

infix fun Int.withIn(range: IntRange): Evaluation {
    val min = range.first
    val max = range.last
    if (min > max)
        throw IllegalArgumentException("min and max should be max > min")

    val evaluations = listOf(this.min(min), this.max(max))
    @Suppress("UNCHECKED_CAST")
    val rejections = evaluations.filter { it !is Evaluation.Acceptance } as List<Evaluation.Rejection<Int>>
    return if (rejections.isEmpty()) Evaluation.Acceptance
    else this.reject(*rejections.toTypedArray(), logical = Logical.AND)
}

val WithIn: (range: IntRange) -> BeCounterExpression<Int> = { { this.withIn(it) } }

fun Int.isNatural(): Evaluation =
        if (this > 0) Evaluation.Acceptance
        else this.reject(
                NumberCode.Natural,
                Param(emptyList<Any>()),
                "number must be natural"
        )

val Natural: BeCounterExpression<Int> = { this.isNatural() }