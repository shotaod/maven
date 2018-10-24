package org.carbon.objects.validation.input

import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.schema.IllegalMax
import org.carbon.objects.validation.schema.IllegalMin
import org.carbon.objects.validation.validate

class IllegalMinInput : Input {
    override fun value(): IllegalMin = IllegalMin("text value")

    override fun tryValidate(): Evaluation = value().validate()
}

class IllegalMaxInput : Input {
    override fun value(): IllegalMax = IllegalMax("text value")

    override fun tryValidate(): Evaluation = value().validate()
}
