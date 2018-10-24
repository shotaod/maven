package org.carbon.objects.validation.schema

import org.carbon.objects.validation.Definition
import org.carbon.objects.validation.Validated
import org.carbon.objects.validation.invalidate
import org.carbon.objects.validation.matcher.max
import org.carbon.objects.validation.matcher.min

data class IllegalMin(
        val text: String
) : Validated<IllegalMin> by IllegalMinSchema

object IllegalMinSchema : Validated<IllegalMin> {
    override val def: Definition<IllegalMin> = { illegalData ->
        illegalData.text should { it min -10 } otherwise "text".invalidate()

    }
}

data class IllegalMax(
        val text: String
) : Validated<IllegalMax> by IllegalMaxSchema

object IllegalMaxSchema : Validated<IllegalMax> {
    override val def: Definition<IllegalMax> = { illegalData ->
        illegalData.text should { it max -10 } otherwise "text".invalidate()
    }
}
