package org.carbon.objects.validation.matcher

import org.carbon.objects.validation.ShapeExpression
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.source.BasicCode
import org.carbon.objects.validation.evaluation.source.IncludeCode
import org.carbon.objects.validation.evaluation.source.LengthCode
import org.carbon.objects.validation.evaluation.source.Param
import org.carbon.objects.validation.evaluation.source.StringCode

infix fun String.eq(other: String): Evaluation =
        if (this == other) Evaluation.Acceptance
        else this.reject(
                BasicCode.Equal,
                Param(listOf(this, other)),
                "two values are not match"
        )

infix fun String.min(min: Int): Evaluation =
        if (this.length > min) Evaluation.Acceptance
        else this.reject(
                LengthCode.Min,
                Param(listOf(min))
        )

infix fun String.minEq(min: Int): Evaluation =
        if (this.length >= min) Evaluation.Acceptance
        else this.reject(
                LengthCode.MinEq,
                Param(listOf(min))
        )

infix fun String.max(max: Int): Evaluation =
        if (this.length < max) Evaluation.Acceptance
        else this.reject(
                LengthCode.Max,
                Param(listOf(max))
        )

infix fun String.maxEq(max: Int): Evaluation =
        if (this.length <= max) Evaluation.Acceptance
        else this.reject(
                LengthCode.MaxEq,
                Param(listOf(max))
        )

infix fun String.has(string: String): Evaluation =
        if (this.contains(string)) Evaluation.Acceptance
        else this.reject(
                StringCode.Contain,
                Param(listOf(string)),
                "Should contain $string"
        )

sealed class IncludeShape {
    class AnyOf(vararg val text: String) : IncludeShape()
    class AllOf(vararg val text: String) : IncludeShape()

    companion object {
        @Suppress("FunctionName")
        fun OneOfChar(chars: String): AnyOf = AnyOf(*chars.toCharArray().map(Char::toString).distinct().toTypedArray())
    }
}

infix fun String.include(shape: IncludeShape): Evaluation = when (shape) {
    is IncludeShape.AnyOf -> {
        if (shape.text.any { it in this }) Evaluation.Acceptance
        else this.reject(
                IncludeCode.Any,
                Param(shape.text.toList()),
                "Should include one of [${shape.text.joinToString(", ")}]"
        )
    }
    is IncludeShape.AllOf -> {
        if (shape.text.all { it in this }) Evaluation.Acceptance
        else this.reject(
                IncludeCode.All,
                Param(shape.text.toList()),
                "Should include all of [${shape.text.joinToString(", ")}]"
        )
    }
}

// misc
private val urlRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
private val emailRegex = "^[a-zA-Z0-9!#$%&'_`/=~*+\\-?^{|}]+(\\.[a-zA-Z0-9!#$%&'_`/=~*+\\-?^{|}]+)*+(.*)@[a-zA-Z0-9][a-zA-Z0-9\\-]*(\\.[a-zA-Z0-9\\-]+)+$".toRegex()
fun String.isEmail(): Evaluation =
        if (emailRegex.matches(this)) Evaluation.Acceptance
        else this.reject(
                StringCode.Email,
                Param(emptyList<String>()),
                "illegal email format"
        )

fun String.isURL(): Evaluation =
        if (urlRegex.matches(this)) Evaluation.Acceptance
        else this.reject(
                StringCode.URL,
                Param(emptyList<String>()),
                "illegal URL format"
        )

// -----------------------------------------------------
//                                               ShapeExpression
//                                               -------
val Email: ShapeExpression<String> = { this.isEmail() }

val URL: ShapeExpression<String> = { this.isURL() }
