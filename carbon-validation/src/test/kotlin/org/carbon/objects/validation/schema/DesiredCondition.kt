package org.carbon.objects.validation.schema

import org.carbon.objects.validation.Definition
import org.carbon.objects.validation.Validated
import org.carbon.objects.validation.invalidate
import org.carbon.objects.validation.matcher.min

data class DesiredCondition(
        val monthlyIncome: Int
) : Validated<DesiredCondition> by DesiredConditionSchema

object DesiredConditionSchema : Validated<DesiredCondition> {
    override val def: Definition<DesiredCondition> = { desired ->
        desired.monthlyIncome should { it min 3000 } otherwise "monthlyIncome".invalidate()
    }
}
