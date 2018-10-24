package org.carbon.objects.validation.schema

import org.carbon.objects.validation.Definition
import org.carbon.objects.validation.Validated
import org.carbon.objects.validation.invalidate
import org.carbon.objects.validation.matcher.max
import org.carbon.objects.validation.matcher.min
import org.carbon.objects.validation.matcher.withIn

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

data class IllegalWithIn(
        val number: Int
) : Validated<IllegalWithIn> by IllegalWithInSchema

object IllegalWithInSchema : Validated<IllegalWithIn> {
    override val def: Definition<IllegalWithIn> = { illegal ->
        illegal.number should {
            @Suppress("EmptyRange")
            it withIn 90..0
        } otherwise "number".invalidate()
    }
}
