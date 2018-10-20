package org.carbon.objects.validation.matcher

import org.carbon.objects.validation.ShapeExpression
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.source.BasicCode
import org.carbon.objects.validation.evaluation.source.LengthCode
import org.carbon.objects.validation.evaluation.source.NumberCode
import org.carbon.objects.validation.evaluation.source.Param

infix fun Number.eq(other: Number): Evaluation =
        if (this == other) Evaluation.Acceptance
        else this.reject(
                BasicCode.Equal,
                Param(listOf(this, other)),
                "values \"$this\" and \"$other\" are not match"
        )

infix fun Int.withIn(range: IntRange): Evaluation {
    val min = range.first
    val max = range.last
    if (min > max)
        throw IllegalArgumentException("min and max should be max > min")

    return if (this in min..max) Evaluation.Acceptance
    else this.reject(
            LengthCode.Range,
            Param(listOf(min, max)),
            "number must be between $min and $max"
    )
}

infix fun Int.min(min: Int): Evaluation {
    if (min < 0) throw IllegalArgumentException("min should be greater than 0")
    return if (this >= min) Evaluation.Acceptance
    else this.reject(
            LengthCode.Min,
            Param(listOf(min)),
            "number must be less than $min"
    )
}

infix fun Int.max(max: Int): Evaluation {
    if (max < 0) throw IllegalArgumentException("max should be greater than 0")
    return if (this <= max) Evaluation.Acceptance
    else this.reject(
            LengthCode.Max,
            Param(listOf(max)),
            "number must be greater than $max"
    )
}

fun Int.isNatural(): Evaluation =
        if (this > 0) Evaluation.Acceptance
        else this.reject(
                NumberCode.Natural,
                Param(emptyList<Any>()),
                "number must be natural"
        )

val Natural: ShapeExpression<Int> = { this.isNatural() }