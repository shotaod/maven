package org.carbon.objects.validation.evaluation.source

import org.carbon.objects.validation.Describe

open class Source(
        open val code: Code,
        open val params: ParamList<*>,
        open val defaultMessage: String? = ""
) : Describe {
    override fun describe(i: Int): String = """(
        |${i.render()} code=${code.describe(i.indent())}
        |${i.render()} params=${params.describe(i.indent())}
        |${i.render()} message="$defaultMessage"
        |${i.render()} )
    """.trimMargin()
}
