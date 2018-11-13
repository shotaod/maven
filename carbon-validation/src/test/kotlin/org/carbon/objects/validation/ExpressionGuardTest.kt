package org.carbon.objects.validation

import io.kotlintest.shouldThrow
import org.carbon.objects.validation.evaluation.Evaluation
import org.carbon.objects.validation.input.IllegalMaxInput
import org.carbon.objects.validation.input.IllegalMinInput
import org.carbon.objects.validation.input.IllegalWithInInput
import org.carbon.objects.validation.input.Input
import org.carbon.objects.validation.template.Expected
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.reflect.KClass

/**
 * @author Soda 2018/10/08.
 */
class ExpressionGuardTest {
    class ThrowExpected : Expected<ThrowExpected>() {
        override val self: ThrowExpected
            get() = this

        override fun assert(describe: String, evalExp: () -> Evaluation) {
            println("""
                --------------------------------------------------
                $describe
                --------------------------------------------------
                expect throws ●～* ${type.simpleName}
            """.trimIndent())
            assertThrow(evalExp)
        }

        var assertThrow: (eval: () -> Evaluation) -> Unit = {}
        var type: KClass<*> = Exception::class

        inline fun <reified T : Throwable> throws(): ThrowExpected {
            type = T::class
            assertThrow = { it -> shouldThrow<T>(it) }
            return this
        }
    }

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun data() = listOf(
                case("[illegal] schema definition use illegal min")(
                        IllegalMinInput(),
                        ThrowExpected().throws<IllegalArgumentException>()),
                case("[illegal] schema definition use illegal max")(
                        IllegalMaxInput(),
                        ThrowExpected().throws<IllegalArgumentException>()),
                case("[illegal] schema definition use illegal within")(
                        IllegalWithInInput(),
                        ThrowExpected().throws<IllegalArgumentException>())
        )

        private val case: (describe: String) -> (input: Input, expected: Expected<*>) -> Array<Any> = { describe ->
            { input, expected ->
                arrayOf(describe, input, expected)
            }
        }
    }

    @ParameterizedTest(name = "Expression Guard Test[{index}] {0}")
    @MethodSource("data")
    fun validate(describe: String, input: Input, expected: Expected<*>) {
        expected.assert(describe, input::tryValidate)
    }
}
