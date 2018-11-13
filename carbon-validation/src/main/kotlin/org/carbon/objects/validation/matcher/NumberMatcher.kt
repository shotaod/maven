package org.carbon.objects.validation.matcher

import org.carbon.objects.validation.BeCounterExpression
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.source.BasicCode
import org.carbon.objects.validation.evaluation.source.LengthCode
import org.carbon.objects.validation.evaluation.source.NumberCode
import org.carbon.objects.validation.evaluation.source.ParamList

infix fun Int.eq(other: Int): Evaluation =
        if (this == other) Evaluation.Accepted
        else reject(
                BasicCode.Equal,
                ParamList(listOf(this, other)),
                "values \"$this\" and \"$other\" are not match"
        )

infix fun Int.min(min: Int): Evaluation {
    return if (this >= min) Evaluation.Accepted
    else this.reject(
            LengthCode.Min,
            ParamList(listOf(min)),
            "number must be greater than $min"
    )
}

infix fun Int.max(max: Int): Evaluation {
    return if (this <= max) Evaluation.Accepted
    else this.reject(
            LengthCode.Max,
            ParamList(listOf(max)),
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
    val rejections = evaluations.filter { it !is Evaluation.Accepted } as List<Evaluation.Rejected>
    return if (rejections.isEmpty()) Evaluation.Accepted
    else this.reject(
            LengthCode.Range,
            ParamList(listOf(min, max)),
            "number mut be between $min and $max")
}

val WithIn: (range: IntRange) -> BeCounterExpression<Int> = { { this.withIn(it) } }

fun Int.isNatural(): Evaluation =
        if (this > 0) Evaluation.Accepted
        else reject(
                NumberCode.Natural,
                ParamList(emptyList<Any>()),
                "number must be natural"
        )

val Natural: BeCounterExpression<Int> = { this.isNatural() }