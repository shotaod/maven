package org.carbon.objects.validation.schema

import org.carbon.objects.validation.Definition
import org.carbon.objects.validation.Validated
import org.carbon.objects.validation.invalidate
import org.carbon.objects.validation.matcher.Length
import org.carbon.objects.validation.matcher.be
import org.carbon.objects.validation.matcher.eq

sealed class Fruit {
    abstract val name: String
}

data class Apple(
        override val name: String
) : Fruit(), Validated<Apple> by VerboseSchema

object VerboseSchema : Validated<Apple> {
    override val def: Definition<Apple> = { fruit ->
        //        fruit.name should {
        //            and(
        //                    it has "apple",
        //                    it be Length(5)
        //            )
        //        }
        fruit.name should { it eq "apple" } otherwise "name".invalidate()
        fruit.name should { it be Length(5) } otherwise "name".invalidate()
    }
}
