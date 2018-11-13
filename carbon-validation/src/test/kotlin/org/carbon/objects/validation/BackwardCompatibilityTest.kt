package org.carbon.objects.validation

import io.kotlintest.be
import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.should
import io.kotlintest.shouldNotBe
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.input.PersonInput
import org.junit.jupiter.api.Test

class BackwardCompatibilityTest {
    @Test
    fun can_touch_with_map_api() {
        val validation = PersonInput().name("s").password("illegal").tryValidate()
        validation.shouldBeTypeOf<Evaluation.Rejected>()
        val rejected = validation as Evaluation.Rejected
        rejected should {
            it.keys.size should be(2)
            it.entries.size should be(2)
            it["name"] shouldNotBe null
            it["password"] shouldNotBe null
        }
    }
}