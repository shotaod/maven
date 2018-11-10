package org.carbon.objects.validation.evaluation.source

import org.carbon.objects.validation.Describe

open class ParamList<T>(private val list: List<T>) : Describe, List<T> by list {
    fun all() = list
    override fun describe(i: Int): String = list.joinToString(
            separator = "\n${i.render()}",
            prefix = "[\n${i.render()}",
            postfix = "\n${i.render()}]") { p -> "- ${(p as? Describe)?.describe(i.indent()) ?: p}" }
}
