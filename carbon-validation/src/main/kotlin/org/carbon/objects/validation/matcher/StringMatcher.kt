package org.carbon.objects.validation.matcher

import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.evaluation.reject
import org.carbon.objects.validation.evaluation.source.BasicViolationSource
import org.carbon.objects.validation.evaluation.source.StringViolationSource

object StringMatcher : Matcher<String> {
    private val urlRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
    private val emailRegex = "^[a-zA-Z0-9!#$%&'_`/=~*+\\-?^{|}]+(\\.[a-zA-Z0-9!#$%&'_`/=~*+\\-?^{|}]+)*+(.*)@[a-zA-Z0-9][a-zA-Z0-9\\-]*(\\.[a-zA-Z0-9\\-]+)+$".toRegex()

    // -----------------------------------------------------
    //                                               Infix
    //                                               -------
    override fun String.eq(other: String): Evaluation =
            if (this == other) Evaluation.Accept
            else this.reject(BasicViolationSource.Equal(this, other), "not match")

    infix fun String.minEq(min: Int): Evaluation =
            if (this.length >= min) Evaluation.Accept
            else this.reject(BasicViolationSource.Length.MinEq(String::class, min), "character must be less than or equal to $min")

    infix fun String.min(min: Int): Evaluation =
            if (this.length > min) Evaluation.Accept
            else this.reject(StringViolationSource.min(min), "character must be less than $min")

    infix fun String.maxEq(max: Int): Evaluation =
            if (this.length <= max) Evaluation.Accept
            else this.reject(StringViolationSource.maxEq(max), "character must be less than or equal to $max")

    infix fun String.max(max: Int): Evaluation =
            if (this.length < max) Evaluation.Accept
            else this.reject(StringViolationSource.max(max), "character must be less than $max")

    // -----------------------------------------------------
    //                                               Suffix
    //                                               -------
    fun String.isEmail(): Evaluation =
            if (emailRegex.matches(this)) Evaluation.Accept
            else this.reject(StringViolationSource.Email, "reject email format")


    fun String.isURL(): Evaluation =
            if (urlRegex.matches(this)) Evaluation.Accept
            else this.reject(StringViolationSource.URL, "reject URL format")

    // -----------------------------------------------------
    //                                               ShapeExpression
    //                                               -------
    val Email: ShapeExpression<String> = { this.isEmail() }

    val URL: ShapeExpression<String> = { this.isURL() }
}