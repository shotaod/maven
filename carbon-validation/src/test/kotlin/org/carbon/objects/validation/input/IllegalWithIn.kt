package org.carbon.objects.validation.input

import org.carbon.objects.validation.Definition
import org.carbon.objects.validation.Validated
import org.carbon.objects.validation.invalidate
import org.carbon.objects.validation.matcher.withIn

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
