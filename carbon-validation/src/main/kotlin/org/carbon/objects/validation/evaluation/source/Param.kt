package org.carbon.objects.validation.evaluation.source

import org.carbon.objects.validation.Describe

open class Param<T>(private val list: List<T>) : Describe {
    operator fun get(i: Int): T = list[i]
    fun all() = list
    override fun describe(i: Int): String = list.joinToString(
            separator = "\n${i.space()}",
            prefix = "[\n${i.space()}",
            postfix = "\n${i.space()}]") { p -> "- ${(p as? Describe)?.describe(i.indent()) ?: p}" }

    fun isEmpty() = list.isEmpty()
}

class DelegateParam<T>(list: MutableList<T>) : Param<T>(list)
