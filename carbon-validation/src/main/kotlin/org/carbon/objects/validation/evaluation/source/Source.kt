package org.carbon.objects.validation.evaluation.source

import org.carbon.objects.validation.Describe

open class Source(
        open val code: Code,
        open val params: Param<*>,
        open val defaultMessage: String? = ""
) : Describe {
    override fun describe(i: Int): String = """(
        |${i.space()} code=${code.describe(i.indent())}
        |${i.space()} params=${params.describe(i.indent())}
        |${i.space()} )
    """.trimMargin()
}