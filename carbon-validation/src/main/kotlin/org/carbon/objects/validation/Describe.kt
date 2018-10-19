package org.carbon.objects.validation

interface Describe {
    fun Int.space() = " ".repeat(this)
    fun Int.indent(inc: Int = 4) = this + inc
    fun describe(i: Int): String
}

fun Describe.describe() = this.describe(4)