package org.carbon.objects.validation

enum class Logical(private val operator: String) {
    AND("&&"),
    OR("||"),
    ;

    override fun toString(): String = "Logical(operator='$operator')"
}
