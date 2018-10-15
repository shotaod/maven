package org.carbon.objects.validation.evaluation

enum class Logical(private val operator: String) {
    AND("&&"),
    OR("||"),
    ;

    override fun toString(): String = "Logical(operator='$operator')"
}
