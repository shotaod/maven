package org.carbon.objects.validation.input

import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.schema.Apple
import org.carbon.objects.validation.validate

class FruitInput : Input {
    private var _name: String = ""

    fun apple(): FruitInput {
        _name = "apple"
        return this
    }

    fun fakeBanana(): FruitInput {
        _name = "banana"
        return this
    }

    override fun value(): Apple = Apple(_name)

    override fun tryValidate(): Evaluation = value().validate()
}
