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
                Param(listOf(min)),
                "length should be greater or equal $min"
        )

infix fun String.max(max: Int): Evaluation =
        if (this.length < max) Evaluation.Acceptance
        else this.reject(
                LengthCode.Max,
                Param(listOf(max)),
                "length should be less or equal $max"
        )

infix fun String.has(string: String): Evaluation =
        if (string in this) Evaluation.Acceptance
        else this.reject(
                StringCode.Contain,
                Param(listOf(string)),
                "Should contain $string"
        )

sealed class IncludeShape {
    open class AnyOf(vararg val text: String) : IncludeShape()
    open class AllOf(vararg val text: String) : IncludeShape()
    class AnyOfChar(chars: String) : AnyOf(*chars.toCharArray().map(Char::toString).distinct().toTypedArray())
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

private val regCache: MutableMap<String, Regex> = mutableMapOf()
infix fun String.matchReg(regStr: String): Evaluation {
    val regex = regCache.computeIfAbsent(regStr) { regStr.toRegex() }
    return if (regex.matches(this)) Evaluation.Acceptance
    else this.reject(
            StringCode.Regex,
            Param(listOf(regex)),
            "Should match $regStr"
    )
}

val Reg: (reg: String) -> ShapeExpression<String> = { { this.matchReg(it) } }

// misc
private val urlRegex = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
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
